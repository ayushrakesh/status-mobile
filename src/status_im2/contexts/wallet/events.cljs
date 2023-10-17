(ns status-im2.contexts.wallet.events
  (:require [re-frame.core :as re-frame]
            [taoensso.timbre :as log]
            [utils.re-frame :as rf]))

(re-frame/reg-event-fx :wallet-2/get-wallet-token
 (fn [_ [accounts]]
   (let [params (map :address accounts)]
     {:json-rpc/call [{:method     "wallet_getWalletToken"
                       :params     [params]
                       :on-success #(rf/dispatch [:wallet-2/get-wallet-token-success %])
                       :on-error   (fn [error]
                                     (log/info "failed to get wallet token"
                                               {:event  :wallet-2/get-wallet-token
                                                :error  error
                                                :params params}))}]})))

(re-frame/reg-event-fx :wallet-2/get-wallet-token-success
 (fn [{:keys [db]} [data]]
   {:db (assoc db
               :wallet-2/tokens          data
               :wallet-2/tokens-loading? false)}))
