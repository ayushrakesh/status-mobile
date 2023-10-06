(ns status-im2.contexts.wallet.events
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [react-native.background-timer :as background-timer]
            [status-im2.common.resources :as resources]
            [status-im2.contexts.wallet.item-types :as types]
            [utils.re-frame :as rf]))

(def ens-local-suggestion-saved-address-mock
  {:type     types/saved-address
   :name     "Pedro"
   :ens      "pedro.eth"
   :address  "0x4732894732894738294783294723894723984"
   :networks [:ethereum :optimism]})

(def ens-local-suggestion-mock
  {:type     types/address
   :ens      "pedro.eth"
   :address  "0x4732894732894738294783294723894723984"
   :networks [:ethereum :optimism]})

(def address-local-suggestion-saved-contact-address-mock
  {:type                types/saved-contact-address
   :customization-color :blue
   :accounts            [{:name                "New House"
                          :address             "0x62cf6E0Ba4C4530735616e1Ee7ff5FbCB726fBd2"
                          :emoji               "🍔"
                          :customization-color :blue}]
   :contact-props       {:full-name           "Mark Libot"
                         :profile-picture     (resources/get-mock-image :user-picture-male4)
                         :customization-color :purple}})

(def address-local-suggestion-saved-address-mock
  {:type                types/saved-address
   :name                "Peter Lamborginski"
   :address             "0x12FaBc34De56Ef78A9B0Cd12Ef3456AbC7D8E9F0"
   :customization-color :magenta
   :networks            [:ethereum :optimism]})

(def address-local-suggestion-mock
  {:type     types/address
   :address  "0x1233cD34De56Ef78A9B0Cd12Ef3456AbC7123dee"
   :networks [:ethereum :optimism]})

(defn find-matching-addresses
  [substring]
  (let [all-addresses [address-local-suggestion-saved-address-mock address-local-suggestion-mock]]
    (vec (filter #(string/starts-with? (:address %) substring) all-addresses))))

(rf/defn scan-address-success
  {:events [:wallet/scan-address-success]}
  [{:keys [db]} address]
  {:db (assoc db :wallet/scanned-address address)})

(rf/defn clean-scanned-address
  {:events [:wallet/clean-scanned-address]}
  [{:keys [db]}]
  {:db (dissoc db :wallet/scanned-address)})

(defn fetch-address-suggestions
  [{:keys [db]} [address]]
  {:db (assoc db
              :wallet/local-suggestions
              (cond
                (= address
                   (get-in
                    address-local-suggestion-saved-contact-address-mock
                    [:accounts 0 :address]))
                [address-local-suggestion-saved-contact-address-mock]
                (= address
                   (get address-local-suggestion-saved-address-mock
                        :address))
                [address-local-suggestion-saved-address-mock]
                :else (find-matching-addresses address))
              :wallet/valid-ens-or-address?
              false)})

(re-frame/reg-event-fx :wallet/fetch-address-suggestions fetch-address-suggestions)

(defn fetch-ens-suggestions
  [{:keys [db]} [ens]]
  {:db (assoc db
              :wallet/local-suggestions     (if (= ens
                                                   (:ens ens-local-suggestion-saved-address-mock))
                                              [ens-local-suggestion-saved-address-mock]
                                              [ens-local-suggestion-mock])
              :wallet/valid-ens-or-address? true)})

(re-frame/reg-event-fx :wallet/ens-validation-success fetch-ens-suggestions)

(defn address-validation-success
  [{:keys [db]} [_]]
  {:db (assoc db :wallet/valid-ens-or-address? true)})

(re-frame/reg-event-fx :wallet/address-validation-success address-validation-success)

(defn validate-address
  [{:keys [db]} [address]]
  (let [current-timeout (get db :wallet/search-timeout)
        timeout         (background-timer/set-timeout
                         #(rf/dispatch [:wallet/address-validation-success address])
                         2000)]
    (background-timer/clear-timeout current-timeout)
    {:db (assoc db
                :wallet/valid-ens-or-address? false
                :wallet/search-timeout        timeout)}))

(re-frame/reg-event-fx :wallet/validate-address validate-address)

(defn validate-ens
  [{:keys [db]} [ens]]
  (let [current-timeout (get db :wallet/search-timeout)
        timeout         (background-timer/set-timeout
                         #(rf/dispatch [:wallet/ens-validation-success ens])
                         2000)]
    (background-timer/clear-timeout current-timeout)
    {:db (assoc db
                :wallet/valid-ens-or-address? false
                :wallet/search-timeout        timeout)}))

(re-frame/reg-event-fx :wallet/validate-ens validate-ens)

(defn clean-local-suggestions
  [{:keys [db]}]
  (let [current-timeout (get db :wallet/search-timeout)]
    (background-timer/clear-timeout current-timeout)
    {:db (assoc db :wallet/local-suggestions [] :wallet/valid-ens-or-address? false)}))

(re-frame/reg-event-fx :wallet/clean-local-suggestions clean-local-suggestions)
