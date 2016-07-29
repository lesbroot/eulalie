(ns eulalie.test.lambda
  (:require [eulalie.lambda :as l]
            [eulalie.test.common :refer [issue-raw! creds]]
            [glossop.core #?(:clj :refer :cljs :refer-macros) [go-catching <?]]
            [eulalie.test.common #?(:clj :refer :cljs :refer-macros) [deftest is]]
            [eulalie.util :refer [env!]]
            [eulalie.core :as e]
            [clojure.string :as str]
            [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as csk-extras]
            [eulalie.platform :as platform]))

(deftest create-function-request-prep
  (let [function-name "this-is-the-function-name"
        req {:creds creds
             :service :lambda
             :target :create-function
             :body {:code {:zip-file "This should be a base64 encoded string"}
                    :function-name function-name
                    :handler "test.handler"
                    :role "this is an amazon arn"
                    :runtime "nodejs4.3"}}
        {:keys [body method endpoint]} (e/prepare-req req)]
    (is (= (->> body
                (platform/decode-json)
                (csk-extras/transform-keys csk/->kebab-case))
           (:body req)))
    (is (str/starts-with? (:host endpoint) "lambda"))
    (is (= (:path endpoint) (str "/" l/service-version "/functions")))
    (is (= :post method))))

(deftest delete-function-request-prep
  (let [function-name "this-is-the-function-name"
        req {:creds creds
             :service :lambda
             :method :delete
             :target :delete-function
             :body {:function-name function-name}}
        {:keys [method endpoint]} (e/prepare-req req)]
    (is (str/starts-with? (:host endpoint) "lambda"))
    (is (= (:path endpoint) (str "/" l/service-version "/functions/" function-name)))
    (is (= :delete method))))
