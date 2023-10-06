(ns status-im2.contexts.wallet.events
  (:require
    [native-module.core :as native-module]
    [status-im2.data-store.wallet :as data-store]
    [taoensso.timbre :as log]
    [utils.re-frame :as rf]
    [utils.security.core :as security]
    [react-native.background-timer :as background-timer]
    [utils.re-frame :as re-frame]))

(rf/defn scan-address-success
  {:events [:wallet/scan-address-success]}
  [{:keys [db]} address]
  {:db (assoc db :wallet/scanned-address address)})

(rf/defn clean-scanned-address
  {:events [:wallet/clean-scanned-address]}
  [{:keys [db]}]
  {:db (dissoc db :wallet/scanned-address)})

(rf/reg-event-fx :wallet/create-derived-addresses
 (fn [{:keys [db]} [password {:keys [path]} on-success]]
   (let [{:keys [wallet-root-address]} (:profile/profile db)
         sha3-pwd                      (native-module/sha3 (str (security/safe-unmask-data password)))]
     {:fx [[:json-rpc/call
            [{:method     "wallet_getDerivedAddresses"
              :params     [sha3-pwd wallet-root-address [path]]
              :on-success on-success
              :on-error   #(log/info "failed to derive address " %)}]]]})))

(rf/reg-event-fx :wallet/add-account
 (fn [{:keys [db]} [password {:keys [emoji account-name color]} {:keys [public-key address path]}]]
   (let [key-uid        (get-in db [:profile/profile :key-uid])
         sha3-pwd       (native-module/sha3 (security/safe-unmask-data password))
         account-config {:key-uid    key-uid
                         :wallet     false
                         :chat       false
                         :type       :generated
                         :name       account-name
                         :emoji      emoji
                         :path       path
                         :address    address
                         :public-key public-key
                         :colorID    color}]
     {:fx [[:json-rpc/call
            [{:method     "accounts_addAccount"
              :params     [sha3-pwd account-config]
              :on-success #(rf/dispatch [:navigate-to :wallet-accounts])
              :on-error   #(log/info "failed to create account " %)}]]]})))

(rf/reg-event-fx :wallet/derive-address-and-add-account
 (fn [_ [password account-details]]
   (let [on-success (fn [derived-adress-details]
                      (rf/dispatch [:wallet/add-account password account-details
                                    (first derived-adress-details)]))]
     {:fx [[:dispatch [:wallet/create-derived-addresses password account-details on-success]]]})))

(rf/defn get-ethereum-chains
  {:events [:wallet/get-ethereum-chains]}
  [{:keys [db]}]
  {:fx [[:json-rpc/call
         [{:method     "wallet_getEthereumChains"
           :params     []
           :on-success [:wallet/get-ethereum-chains-success]
           :on-error   #(log/info "failed to get networks " %)}]]]})

(rf/reg-event-fx
 :wallet/get-ethereum-chains-success
 (fn [{:keys [db]} [data]]
   (let [network-data
         {:test (map #(->> %
                           :Test
                           data-store/<-rpc)
                     data)
          :prod (map #(->> %
                           :Prod
                           data-store/<-rpc)
                     data)}]
     {:db (assoc db :wallet/networks network-data)})))

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
