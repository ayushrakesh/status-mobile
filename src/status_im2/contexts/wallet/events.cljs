(ns status-im2.contexts.wallet.events
  (:require
    [native-module.core :as native-module]
    [status-im2.data-store.wallet :as data-store]
    [taoensso.timbre :as log]
    [utils.ethereum.chain :as chain]
    [utils.re-frame :as rf]
    [utils.security.core :as security]))

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

(def collectibles-request-batch-size 1000)

(defn- displayable-collectible?
  [{:keys [image_url animation_url]}]
  (or (not= "" animation_url)
      (not= "" image_url)))

(rf/reg-event-fx :wallet/store-collectibles
 (fn [{:keys [db]} [collectibles]]
   (let [stored-collectibles      (get-in db [:wallet :collectibles])
         displayable-collectibles (filter displayable-collectible? collectibles)]
     {:db (assoc-in db
           [:wallet :collectibles]
           (reduce conj displayable-collectibles stored-collectibles))})))

(rf/reg-event-fx :wallet/clear-stored-collectibles
 (fn [{:keys [db]}]
   {:db (assoc-in db [:wallet :collectibles] [])}))

(rf/reg-event-fx :wallet/save-collectibles-request-details
 (fn [{:keys [db]} [request-details]]
   {:db (assoc-in db [:wallet :ongoing-collectibles-request] request-details)}))

(rf/reg-event-fx :wallet/clear-collectibles-request-details
 (fn [{:keys [db]}]
   {:db (assoc-in db [:wallet :ongoing-collectibles-request] {})}))

(rf/reg-event-fx :wallet/request-collectibles
 (fn [{:keys [db]} [{:keys [addresses offset new-request?]}]]
   (let [request-params [0
                         [(chain/chain-id db)]
                         addresses
                         offset
                         collectibles-request-batch-size]]
     (merge
      {:json-rpc/call [{:method     "wallet_filterOwnedCollectiblesAsync"
                        :params     request-params
                        :on-success #()
                        :on-error   (fn [error]
                                      (log/error "failed to request collectibles"
                                                 {:event  :wallet/request-collectibles
                                                  :error  error
                                                  :params request-params})
                                    )}]}
      (when new-request?
        {:fx
         [[:dispatch [:wallet/clear-stored-collectibles]]
          [:dispatch [:wallet/save-collectibles-request-details {:addresses addresses}]]]})))))

