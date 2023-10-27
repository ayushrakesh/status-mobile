(ns status-im2.subs.wallet
  (:require
    [re-frame.core :as re-frame]))

(re-frame/reg-sub
 :wallet/collectibles
 :<- [:wallet]
 (fn [wallet]
   (:collectibles wallet)))

(re-frame/reg-sub
 :wallet/all-addresses
 :<- [:profile/wallet-accounts]
 (fn [accounts]
   (map :address accounts)))
