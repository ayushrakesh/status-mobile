(ns status-im2.contexts.quo-preview.avatars.user-avatar
  (:require
    [quo2.core :as quo]
    [reagent.core :as reagent]
    [status-im2.common.resources :as resources]
    [status-im2.contexts.quo-preview.preview :as preview]))

(def descriptor
  [{:key     :size
    :type    :select
    :options [{:key :size-80}
              {:key :size-64}
              {:key :size-48}
              {:key :size-32}
              {:key :size-28}
              {:key :size-24}
              {:key :size-20}
              {:key :size-16}]}
   (preview/customization-color-option)
   {:key  :online?
    :type :boolean}
   {:key  :status-indicator?
    :type :boolean}
   {:label "Identicon Ring (applies only when there's no profile picture)"
    :key   :ring?
    :type  :boolean}
   {:key  :full-name
    :type :text}
   {:key     :profile-picture
    :type    :select
    :options [{:value "None"
               :key   nil}
              {:value "Alicia Keys"
               :key   (resources/get-mock-image :user-picture-female2)}
              {:value "pedro.eth"
               :key   (resources/get-mock-image :user-picture-male4)}]}])

(defn view
  []
  (let [state (reagent/atom {:full-name           "A Y"
                             :status-indicator?   true
                             :online?             true
                             :size                :size-48
                             :customization-color :blue})]
    (fn []
      [preview/preview-container {:state state :descriptor descriptor}
       [quo/user-avatar @state]])))
