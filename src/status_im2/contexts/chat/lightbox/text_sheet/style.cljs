(ns status-im2.contexts.chat.lightbox.text-sheet.style
  (:require
    [quo.foundations.colors :as colors]
    [react-native.reanimated :as reanimated]
    [status-im2.contexts.chat.lightbox.constants :as constants]))

(defn sheet-container
  [{:keys [height top]}]
  (reanimated/apply-animations-to-style
   {:height height
    :top    top}
   {:position :absolute
    :left     0
    :right    0}))

(defn text-style
  [expanding-message?]
  {:color             colors/white
   :margin-horizontal 20
   :margin-bottom     constants/text-margin
   :align-items       (if expanding-message? nil :center)
   :flex-grow         1})

(def bar-container
  {:height          constants/bar-container-height
   :left            0
   :right           0
   :top             0
   :justify-content :center
   :align-items     :center})

(def bar
  {:width            32
   :height           4
   :border-radius    100
   :background-color colors/white-opa-40
   :border-width     0.5
   :border-color     colors/neutral-100})

(defn top-gradient
  [{:keys [derived-value]} {:keys [top]} insets max-height]
  (let [initial-top (- (+ (:top insets)
                          constants/top-view-height))]
    (reanimated/apply-animations-to-style
     {:opacity (reanimated/interpolate derived-value
                                       [max-height (+ max-height constants/line-height)]
                                       [0 1])
      :top     (reanimated/interpolate top
                                       [0 (- max-height)]
                                       [initial-top (- initial-top max-height)]
                                       {:extrapolateLeft  "clamp"
                                        :extrapolateRight "clamp"})}
     {:position :absolute
      :left     0
      :right    0
      :height   (+ (:top insets)
                   constants/top-view-height
                   (* constants/line-height 2))
      :z-index  1})))

(def bottom-gradient
  {:position :absolute
   :left     0
   :right    0
   :height   (+ 40 constants/small-list-height)
   :bottom   (- constants/small-list-height)
   :z-index  1})
