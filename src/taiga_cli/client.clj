(ns taiga-cli.client
  (:require
    [clj-http.client :as http]
    [clojure.data.json :as json]
    [clojure.set :as cljset])
  (:gen-class))

(def default-client-config {
  :as :json
  :accept :json
  :content-type :json
  :throw-exceptions false})

(def status (atom {}))

(defn cli-post [url data params]
  (let [client-config (cljset/union @status default-client-config { :body (json/write-str data) :query-params params })]
    (http/post url client-config)))

(defn cli-get [url params]
  (let [client-config (cljset/union @status default-client-config { :query-params params })]
    (http/get url client-config)))

(defn login [username password]
  (let [response (cli-post
    "http://localhost:8000/api/v1/auth"
    {
       :username username
       :password password
       :type "normal"
    }
    {})]
    (if (= 200 (:status response))
      (swap! status assoc :headers {"Authorization" (str "Bearer " (:auth_token (:body response)))})
      {:error (:_error_message (:body response))}
    )))

(defn resolve-project-slug [project-slug]
  (let [response (cli-get "http://localhost:8000/api/v1/resolver" {:project project-slug})]
    (println response)
    (if (= 200 (:status response))
      (:project (:body response))
      {:error (:_error_message (:body response))}
    )))

(defn projects []
  (let [response (cli-get "http://localhost:8000/api/v1/projects" {})]
    (if (= 200 (:status response))
      (map (fn [proj] {:name (:name proj) :slug (:slug proj)}) (:body response))
      {:error (:_error_message (:body response))}
    )))

(defn backlog [project-slug]
  (let [project-id (resolve-project-slug project-slug)
      response (cli-get "http://localhost:8000/api/v1/userstories" {:project project-id :milestone nil})]
    (if (= 200 (:status response))
      (map (fn [us] {:subject (:subject us) :ref (:ref us)}) (:body response))
      {:error (:_error_message (:body response))}
    )))
