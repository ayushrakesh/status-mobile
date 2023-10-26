(ns status-im2.common.floating-button-page.view
  (:require
    [oops.core :as oops]
    [quo.theme :as quo.theme]
    [react-native.core :as rn]
    [react-native.hooks :as hooks]
    [react-native.platform :as platform]
    [react-native.safe-area :as safe-area]
    [reagent.core :as reagent]
    [status-im2.common.floating-button-page.floating-container.view :as floating-container]
    [status-im2.common.floating-button-page.style :as style]))

(defn- show-background-android
  [{:keys [window-height keyboard-height floating-container-height
           content-scroll-y content-container-height header-height]} keyboard-shown?]
  (let [available-space (- window-height
                           keyboard-height
                           floating-container-height
                           header-height)
        content-height  (+ content-container-height
                           content-scroll-y)]
    (and keyboard-shown? (< available-space content-height))))

(defn- show-background-ios
  [{:keys [window-height keyboard-height floating-container-height
           content-scroll-y content-container-height header-height]} keyboard-shown?]
  (let [available-space (- window-height
                           keyboard-height
                           floating-container-height
                           (safe-area/get-top))
        content-height  (+ (safe-area/get-bottom)
                           header-height
                           content-scroll-y
                           content-container-height)]
    (and keyboard-shown? (< content-height available-space))))

(defn show-background
  [props keyboard-shown?]
  (if platform/android?
    (show-background-android props keyboard-shown?)
    (show-background-ios props keyboard-shown?)))

(defn f-view
  [{:keys [header footer]}
   page-content]
  (reagent/with-let [theme                     (quo.theme/use-theme-value)
                     window-height             (:height (rn/get-window))

                     floating-container-height (reagent/atom 0)
                     header-height             (reagent/atom 0)
                     content-container-height  (reagent/atom 0)
                     show-keyboard?            (reagent/atom false)
                     content-scroll-y          (reagent/atom 0)

                     show-listener             (oops/ocall rn/keyboard
                                                           "addListener"
                                                           (if platform/android?
                                                             "keyboardDidShow"
                                                             "keyboardWillShow")
                                                           #(reset! show-keyboard? true))
                     hide-listener             (oops/ocall rn/keyboard
                                                           "addListener"
                                                           (if platform/android?
                                                             "keyboardDidHide"
                                                             "keyboardWillHide")
                                                           #(reset! show-keyboard? false))]
    (let [{:keys [keyboard-shown
                  keyboard-height]} (hooks/use-keyboard)
          show-background?          (show-background {:window-height window-height
                                                      :floating-container-height
                                                      @floating-container-height
                                                      :keyboard-height keyboard-height
                                                      :content-scroll-y @content-scroll-y
                                                      :content-container-height @content-container-height
                                                      :header-height @header-height}
                                                     keyboard-shown)]

      [rn/view {:style style/page-container}
       [rn/view
        {:on-layout (fn [event]
                      (let [height (oops/oget event "nativeEvent.layout.height")]
                        (reset! header-height height)))}
        header]
       [rn/scroll-view
        {:on-scroll               (fn [event]
                                    (let [y (oops/oget event "nativeEvent.contentOffset.y")]
                                      (reset! content-scroll-y y)))
         :scroll-event-throttle   64
         :content-container-style {:flexGrow 1}}
        [rn/view
         {:on-layout (fn [event]
                       (let [height (oops/oget event "nativeEvent.layout.height")]
                         (reset! content-container-height height)))}
         page-content]]
       [rn/keyboard-avoiding-view
        {:style          style/keyboard-avoiding-view
         :pointer-events :box-none}
        [floating-container/view
         {:theme            theme
          :keyboard-shown?  keyboard-shown
          :show-background? show-background?
          :on-layout        (fn [event]
                              (let [height (oops/oget event "nativeEvent.layout.height")]
                                (reset! floating-container-height height)))}
         footer]]])
    (finally
     (oops/ocall show-listener "remove")
     (oops/ocall hide-listener "remove"))))

(defn view
  [props header page-content button-component]
  [:f> f-view props header page-content button-component])
