(ns taiga-cli.core
  (:require
    [taiga-cli.client :as client]
    [clojure.string :as string]
    [clojure.set :as clj-set])
  (:gen-class))

(defn quit []
  (println "bye")
  :quit)

(defn cli-loop [line]
  (if line
    (let [arguments (string/split line #"\s")]
      (case (first arguments)
        "login" (if (= (count arguments) 3)
                  (println (client/login (nth arguments 1) (nth arguments 2)))
                  (println "Usage: login <username> <password>"))
        "projects" (if (= (count arguments) 1)
                  (println (string/join "\n" (client/projects)))
                  (println "Usage: projects"))
        "backlog" (if (= (count arguments) 2)
                  (println (string/join "\n" (client/backlog (nth arguments 1))))
                  (println "Usage: backlog <project-slug>"))
        "quit" (quit)
        (println "error")))
    (quit)))

(defn -main [& args]
  (loop []
    (print "> ")
    (flush)
    (if (not= :quit (cli-loop (read-line))) (recur))))
