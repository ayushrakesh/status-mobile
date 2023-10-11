(ns status-im2.contexts.wallet.home.view
  (:require
   [clojure.string :as string]
   [quo2.core :as quo]
   [react-native.core :as rn]
   [react-native.safe-area :as safe-area]
   [reagent.core :as reagent]
   [status-im2.common.home.top-nav.view :as common.top-nav]
   [status-im2.contexts.wallet.common.activity-tab.view :as activity]
   [status-im2.contexts.wallet.common.collectibles-tab.view :as collectibles]
   [status-im2.contexts.wallet.common.temp :as temp]
   [status-im2.contexts.wallet.home.style :as style]
   [utils.i18n :as i18n]
   [utils.re-frame :as rf]))

(defn new-account
  []
  [quo/action-drawer
   [[{:icon                :i/add
      :accessibility-label :start-a-new-chat
      :label               (i18n/label :t/add-account)
      :sub-label           (i18n/label :t/add-account-description)
      :on-press            #(rf/dispatch [:navigate-to :wallet-create-account])}
     {:icon                :i/reveal
      :accessibility-label :add-a-contact
      :label               (i18n/label :t/add-address)
      :sub-label           (i18n/label :t/add-address-description)
      :on-press            #(rf/dispatch [:navigate-to :wallet-address-watch])
      :add-divider?        true}]]])

(def add-account-placeholder
  {:customization-color :blue
   :on-press            #(rf/dispatch [:show-bottom-sheet {:content new-account}])
   :type                :add-account})

(def tabs-data
  [{:id :assets :label (i18n/label :t/assets) :accessibility-label :assets-tab}
   {:id :collectibles :label (i18n/label :t/collectibles) :accessibility-label :collectibles-tab}
   {:id :activity :label (i18n/label :t/activity) :accessibility-label :activity-tab}])

(defn calculate-raw-balance
  [raw-balance decimals]
  (if (not (js/isNaN (js/parseInt raw-balance)))
  (/ (js/parseInt raw-balance) (Math/pow 10 (js/parseInt decimals))) 0))

(defn calculate-balance
  [{:keys [address]}]
  (let [tokens       (rf/sub [:wallet-2/tokens])
        token        (get tokens (keyword (string/lower-case address)))
        result       (reduce (fn [acc item]
                               (let [total-value-per-token (reduce (fn [ac balances]
                                                                     (+ (calculate-raw-balance (:rawBalance balances)
                                                                                               (:decimals item))
                                                                        ac))
                                                                   0
                                                                   (vals (:balancesPerChain item)))
                                     total-values (* total-value-per-token
                                                     (get-in item [:marketValuesPerCurrency :USD :price]))]
                                 (+ acc total-values)))
                             0
                             token)] 
    (.toFixed result 2)))

(defn refactor-data
  [accounts loading?]
  (let [refactored-accounts (mapv (fn [account]
                                    (assoc account
                                           :type                :empty
                                           :customization-color :blue
                                           :on-press            #(rf/dispatch [:navigate-to
                                                                               :wallet-accounts])
                                           :loading?            loading?
                                           :balance             (str "$" (calculate-balance account))))
                                  accounts)]
    (conj refactored-accounts add-account-placeholder)))

(defn reagent-render
  [accounts]
  (let [top          (safe-area/get-top)
        selected-tab (reagent/atom (:id (first tabs-data)))
        loading?     (rf/sub [:wallet-2/tokens-loading?])]
    [rn/view
     {:style {:margin-top top
              :flex       1}}
     [common.top-nav/view]
     [rn/view {:style style/overview-container}
      [quo/wallet-overview temp/wallet-overview-state]]
     [rn/pressable
      {:on-long-press #(rf/dispatch [:show-bottom-sheet {:content temp/wallet-temporary-navigation}])}
      [quo/wallet-graph {:time-frame :empty}]]
     [rn/flat-list
      {:style      style/accounts-list
       :data       (refactor-data accounts loading?)
       :horizontal true
       :separator  [rn/view {:style {:width 12}}]
       :render-fn  quo/account-card}]
     [quo/tabs
      {:style          style/tabs
       :size           32
       :default-active @selected-tab
       :data           tabs-data
       :on-change      #(reset! selected-tab %)}]
     (case @selected-tab
       :assets       [rn/flat-list
                      {:render-fn               quo/token-value
                       :data                    temp/tokens
                       :key                     :assets-list
                       :content-container-style {:padding-horizontal 8}}]
       :collectibles [collectibles/view]
       [activity/view])]))

(defn view
  []
  (let [accounts (rf/sub [:profile/wallet-accounts])]
    (rf/dispatch [:wallet-2/get-wallet-token accounts])
    (fn []
      [reagent-render accounts])))
