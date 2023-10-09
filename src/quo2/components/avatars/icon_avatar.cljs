(ns quo2.components.avatars.icon-avatar
  (:require
    [quo2.components.icon :as icons]
    [quo2.foundations.colors :as colors]
    [quo2.theme :as quo.theme]
    [react-native.core :as rn]))

(def ^:private sizes
  {:size-48 {:component 48
             :icon      20}
   :size-32 {:component 32
             :icon      20}
   :size-24 {:component 24
             :icon      16}
   :size-20 {:component 20
             :icon      12}})

(defn view-internal
  [{:keys [size icon color opacity border? theme]
    :or   {opacity 20
           size    :size-32}}]
  (let [{component-size :component icon-size :icon} (get sizes size)
        circle-color                                (colors/resolve-color color theme opacity)
        icon-color                                  (colors/resolve-color color theme)]
    [rn/view
     {:style {:width            component-size
              :height           component-size
              :border-radius    component-size
              :border-width     (when border? 1)
              :border-color     (colors/theme-colors colors/neutral-20 colors/neutral-80 theme)
              :background-color circle-color
              :justify-content  :center
              :align-items      :center}}
     [icons/icon icon
      {:size  icon-size
       :color icon-color}]]))

(def view (quo.theme/with-theme view-internal))
