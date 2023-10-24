(ns status-im2.contexts.wallet.common.utils
  (:require
    [clojure.string :as string]))

(defn get-first-name
  [full-name]
  (first (string/split full-name #" ")))

(defn get-balance-by-address
  [balances address]
  (.toFixed (->> balances
                 (filter #(= (:address %) address))
                 first
                 :balance)
            2))
