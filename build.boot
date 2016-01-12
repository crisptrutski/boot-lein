(def +version+ "0.1.0-SNAPSHOT")

(task-options!
 pom  {:project     'crisptrutski/boot-lein
       :version     +version+
       :description "Boot task load to use boot with lein/cursive"
       :url         "https://github.com/crisptrutski/boot-lein"
       :scm         {:url "https://github.com/crisptrutski/boot-lein"}
       :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})

(set-env!
  :dependencies '[[org.clojure/clojure       "1.7.0"          :scope "provided"]
                  [boot/core                 "2.5.5"          :scope "provided"]
                  [adzerk/bootlaces          "0.1.13"         :scope "test"]
                  [adzerk/boot-test          "1.1.0"          :scope "test"]])

(require '[adzerk.bootlaces :refer :all]
         '[adzerk.boot-test :refer :all])

(bootlaces! +version+)
