(ns status-im2.common.standard-authentication.standard-auth.view
  (:require
    [quo.core :as quo]
    [quo.theme :as quo.theme]
    [react-native.core :as rn]
    [react-native.touch-id :as biometric]
    [reagent.core :as reagent]
    [status-im2.common.standard-authentication.enter-password.view :as enter-password]
    [taoensso.timbre :as log]
    [utils.i18n :as i18n]
    [utils.re-frame :as rf]))

(defn reset-password
  []
  (rf/dispatch [:set-in [:profile/login :password] nil])
  (rf/dispatch [:set-in [:profile/login :error] ""]))

(defn authorize
  [{:keys [on-enter-password biometric-auth? on-auth-success on-auth-fail on-close
           auth-button-label theme blur? customization-color auth-button-icon-left]}]
  (biometric/get-supported-type
   (fn [biometric-type]
     (if (and biometric-auth? biometric-type)
       (biometric/authenticate
        {:reason     (i18n/label :t/biometric-auth-confirm-message)
         :on-success (fn [response]
                       (when on-auth-success (on-auth-success response))
                       (log/info "response" response))
         :on-fail    (fn [error]
                       (log/error "Authentication Failed. Error:" error)
                       (when on-auth-fail (on-auth-fail error))
                       (rf/dispatch [:show-bottom-sheet
                                     {:theme   theme
                                      :shell?  blur?
                                      :content (fn []
                                                 [enter-password/view
                                                  {:on-enter-password on-enter-password}])}]))})
       (do
         (reset-password)
         (rf/dispatch [:show-bottom-sheet
                       {:on-close on-close
                        :theme    theme
                        :shell?   blur?
                        :content  (fn []
                                    [enter-password/view
                                     {:customization-color customization-color
                                      :on-enter-password   on-enter-password
                                      :button-icon-left    auth-button-icon-left
                                      :button-label        auth-button-label}])}]))))))

(defn- view-internal
  [_]
  (let [reset-slider? (reagent/atom false)
        on-close      #(reset! reset-slider? true)]
    (fn [{:keys [biometric-auth?
                 track-text
                 customization-color
                 auth-button-label
                 on-enter-password
                 on-auth-success
                 on-auth-fail
                 auth-button-icon-left
                 size
                 theme
                 blur?
                 container-style]}]
      [rn/view {:style {:flex 1}}
       [quo/slide-button
        {:size                size
         :container-style     container-style
         :customization-color customization-color
         :on-reset            (when @reset-slider? #(reset! reset-slider? false))
         :on-complete         #(authorize {:on-close              on-close
                                           :auth-button-icon-left auth-button-icon-left
                                           :theme                 theme
                                           :blur?                 blur?
                                           :customization-color   customization-color
                                           :on-enter-password     on-enter-password
                                           :biometric-auth?       biometric-auth?
                                           :on-auth-success       on-auth-success
                                           :on-auth-fail          on-auth-fail
                                           :auth-button-label     auth-button-label})
         :track-icon          (if biometric-auth? :i/face-id :password)
         :track-text          track-text}]])))

(def view (quo.theme/with-theme view-internal))
