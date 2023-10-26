(ns status-im2.common.floating-button-page.floating-container.view
  (:require [quo.foundations.colors :as colors]
            [quo.theme :as quo.theme]
            [react-native.blur :as blur]
            [react-native.core :as rn]
            [react-native.platform :as platform]
            [react-native.reanimated :as reanimated]
            [react-native.safe-area :as safe-area]
            [status-im2.common.floating-button-page.floating-container.style :as style]))

(def duration 110)

(defn blur-container-props
  [theme]
  {:blur-amount      12
   :blur-radius      25
   :blur-type        (quo.theme/theme-value :light :dark theme)
   :style            (when platform/ios?
                       {:width              "100%"
                        :padding-horizontal 20
                        :padding-vertical   12})
   :background-color (colors/theme-colors colors/white-70-blur colors/neutral-80-opa-1-blur theme)})

(defn- blur-container
  [props & children]
  [rn/view
   (merge props
          {:width    "100%"
           :overflow :hidden}
          (when platform/android?
            {:padding-horizontal 20
             :padding-vertical   12}))
   (into [blur/view (blur-container-props (:theme props))] children)])

"
-        on-layout : will trigger to dynamically get the height of the container to screen using it.
- show-background? : blurred container that is activated when this component is on top of the page content.
-  keyboard-shown? : keyboard  is visible on the current page.
"
(defn get-margin-bottom
  [show-background? keyboard-shown?]
  (if platform/android?
    0
    (cond keyboard-shown?                        (safe-area/get-top)
          (and show-background? keyboard-shown?) (safe-area/get-bottom)
          :else                                  (safe-area/get-bottom))))

(defn f-view
  [{:keys [on-layout theme show-background? keyboard-shown?]}
   children]
  (let [blur-active?           show-background?
        container-view         (if blur-active? blur-container rn/view)
        inline-container-style (if blur-active? style/blur-container style/view-container)
        margin-bottom          (reanimated/use-shared-value (get-margin-bottom show-background?
                                                                               keyboard-shown?))]
    (rn/use-effect #(reanimated/animate-shared-value-with-timing
                     margin-bottom
                     (get-margin-bottom show-background? keyboard-shown?)
                     duration
                     :easing4)
                   [show-background? keyboard-shown?])
    [reanimated/view
     {:style (reanimated/apply-animations-to-style
              {:margin-bottom margin-bottom}
              {:margin-top :auto})}
     [container-view
      {:on-layout on-layout
       :theme theme
       :style
       (merge inline-container-style)}
      children]]))

(defn view
  [props children]
  [:f> f-view props children])
