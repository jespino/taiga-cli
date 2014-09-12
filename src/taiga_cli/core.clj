(ns taiga-cli.core
  (:require
    [clojure.string :as string]
    [clj-http.client :as client]
    [clojure.data.json :as json])
  (:gen-class))

(defn login [username password]
  (get (:body (client/post
    "http://localhost:8000/api/v1/auth"
    {
      :body (json/write-str {
         :username username
         :password password
         :type "normal"
      })
      :accept :json
      :content-type :json
      :throw-exceptions false
    })) "auth_token"))

(defn -main [& args]
  (loop []
    (print "> ")
    (flush)
    (let [arguments (string/split (read-line) #"\s")]
      (case (first arguments)
        "login" (do (println (login "admin" "123123")) (recur))
        "stop" (do (println "stop") (recur))
        "status" (do (println "status") (recur))
        "quit" (println "bye")
        (do (println "error") (recur))))))
