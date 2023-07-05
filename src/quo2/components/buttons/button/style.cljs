(ns quo2.components.buttons.button.style
  (:require [quo2.foundations.colors :as colors]))

(defn themes
  [customization-color]
  {:light
   {:primary         {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  (colors/custom-color customization-color 50)
                                             :pressed  (colors/custom-color customization-color 60)
                                             :disabled (colors/custom-color customization-color 50)}}
    :secondary       {:icon-color       colors/primary-50
                      :label-color      colors/primary-50
                      :background-color {:default  colors/primary-50-opa-20
                                         :pressed  colors/primary-50-opa-40
                                         :disabled colors/primary-50-opa-20}}
    :grey            {:icon-color           colors/neutral-100
                      :icon-secondary-color colors/neutral-50
                      :label-color          colors/neutral-100
                      :background-color     {:default  colors/neutral-10
                                             :pressed  colors/neutral-20
                                             :disabled colors/neutral-10}}
    :dark-grey       {:icon-color           colors/neutral-100
                      :icon-secondary-color colors/neutral-50
                      :label-color          colors/neutral-100
                      :background-color     {:default  colors/neutral-20
                                             :pressed  colors/neutral-30
                                             :disabled colors/neutral-20}}
    :outline         {:icon-color           colors/neutral-50
                      :icon-secondary-color colors/neutral-50
                      :label-color          colors/neutral-100
                      :border-color         {:default  colors/neutral-30
                                             :pressed  colors/neutral-40
                                             :disabled colors/neutral-30}}
    :ghost           {:icon-color           colors/neutral-50
                      :icon-secondary-color colors/neutral-50
                      :label-color          colors/neutral-100
                      :background-color     {:pressed colors/neutral-10}}
    :danger          {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  colors/danger-50
                                             :pressed  colors/danger-60
                                             :disabled colors/danger-50}}
    :positive        {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  colors/success-50
                                             :pressed  colors/success-60
                                             :disabled colors/success-50-opa-30}}
    :photo-bg        {:icon-color           colors/neutral-100
                      :icon-secondary-color colors/neutral-80-opa-40
                      :label-color          colors/neutral-100
                      :background-color     {:default  colors/white-opa-40
                                             :pressed  colors/white-opa-50
                                             :disabled colors/white-opa-40}}
    :blur-bg         {:icon-color           colors/neutral-100
                      :icon-secondary-color colors/neutral-80-opa-40
                      :label-color          colors/neutral-100
                      :background-color     {:default  colors/neutral-80-opa-5
                                             :pressed  colors/neutral-80-opa-10
                                             :disabled colors/neutral-80-opa-5}}
    :blur-bg-outline {:icon-color           colors/neutral-100
                      :icon-secondary-color colors/neutral-80-opa-40
                      :label-color          colors/neutral-100
                      :border-color         {:default  colors/neutral-80-opa-10
                                             :pressed  colors/neutral-80-opa-20
                                             :disabled colors/neutral-80-opa-10}}
    :shell           {:icon-color       colors/white
                      :label-color      colors/white
                      :background-color {:default  colors/neutral-95
                                         :pressed  colors/neutral-95
                                         :disabled colors/neutral-95}}}
   :dark
   {:primary         {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  (colors/custom-color customization-color 60)
                                             :pressed  (colors/custom-color customization-color 50)
                                             :disabled (colors/custom-color customization-color 60)}}
    :secondary       {:icon-color       colors/primary-50
                      :label-color      colors/primary-50
                      :background-color {:default  colors/primary-50-opa-20
                                         :pressed  colors/primary-50-opa-30
                                         :disabled colors/primary-50-opa-20}}
    :grey            {:icon-color           colors/white
                      :icon-secondary-color colors/neutral-40
                      :label-color          colors/white
                      :background-color     {:default  colors/neutral-90
                                             :pressed  colors/neutral-60
                                             :disabled colors/neutral-90}}
    :dark-grey       {:icon-color           colors/white
                      :icon-secondary-color colors/neutral-40
                      :label-color          colors/white
                      :background-color     {:default  colors/neutral-70
                                             :pressed  colors/neutral-60
                                             :disabled colors/neutral-70}}
    :outline         {:icon-color           colors/neutral-40
                      :icon-secondary-color colors/neutral-40
                      :label-color          colors/white
                      :border-color         {:default  colors/neutral-70
                                             :pressed  colors/neutral-60
                                             :disabled colors/neutral-70}}
    :ghost           {:icon-color           colors/neutral-40
                      :icon-secondary-color colors/neutral-40
                      :label-color          colors/white
                      :background-color     {:pressed colors/neutral-80}}
    :danger          {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  colors/danger-60
                                             :pressed  colors/danger-50
                                             :disabled colors/danger-60}}
    :positive        {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  colors/success-60
                                             :pressed  colors/success-50
                                             :disabled colors/success-60-opa-30}}
    :photo-bg        {:icon-color           colors/white
                      :icon-secondary-color colors/neutral-30
                      :label-color          colors/white
                      :background-color     {:default  colors/neutral-80-opa-40
                                             :pressed  colors/neutral-80-opa-50
                                             :disabled colors/neutral-80-opa-40}}
    :blur-bg         {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-70
                      :label-color          colors/white
                      :background-color     {:default  colors/white-opa-5
                                             :pressed  colors/white-opa-10
                                             :disabled colors/white-opa-5}}
    :blur-bg-outline {:icon-color           colors/white
                      :icon-secondary-color colors/white-opa-40
                      :label-color          colors/white
                      :border-color         {:default  colors/white-opa-10
                                             :pressed  colors/white-opa-20
                                             :disabled colors/white-opa-5}}
    :shell           {:icon-color       colors/white
                      :label-color      colors/white
                      :background-color {:default colors/neutral-95}}}})

(defn shape-style-container
  [type icon size]
  {:height        size
   :border-radius (if (and icon (#{:primary :secondary :danger} type))
                    24
                    (case size
                      56 12
                      40 12
                      32 10
                      24 8))})

(defn style-container
  [{:keys [type size disabled background-color border-color icon above width before after]}]
  (merge {:height             size
          :align-items        :center
          :justify-content    :center
          :flex-direction     (if above :column :row)
          :background-color   background-color
          :padding-horizontal (when-not (or icon before after)
                                (case size
                                  56 16
                                  40 16
                                  32 12
                                  24 8))
          :padding-left       (when-not (or icon before)
                                (case size
                                  56 16
                                  40 16
                                  32 12
                                  24 8))
          :padding-right      (when-not (or icon after)
                                (case size
                                  56 16
                                  40 16
                                  32 12
                                  24 8))
          :padding-top        (when-not (or icon before after)
                                (case size
                                  56 0
                                  40 9
                                  32 5
                                  24 3))
          :padding-bottom     (when-not (or icon before after)
                                (case size
                                  56 0
                                  40 9
                                  32 5
                                  24 4))}
         (shape-style-container type icon size)
         (when width
           {:width width})
         (when icon
           {:width size})
         (when border-color
           {:border-color border-color
            :border-width 1})
         (when disabled
           {:opacity 0.3})))
