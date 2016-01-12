(ns boot-lein.core
  (:require
    [boot.core :refer :all]
    [boot.util :refer :all]
    [boot.task.built-in :refer :all]))

(defn- generate-lein-project-file! [& {:keys [keep-project] :or {:keep-project true}}]
  (require 'clojure.java.io)
  (let [pfile ((resolve 'clojure.java.io/file) "project.clj")
        ; Only works when pom options are set using task-options!
        {:keys [project version]} (:task-options (meta #'boot.task.built-in/pom))
        prop #(when-let [x (get-env %2)] [%1 x])
        head (list* 'defproject (or project 'boot-project) (or version "0.0.0-SNAPSHOT")
               (concat
                 (prop :url :url)
                 (prop :license :license)
                 (prop :description :description)
                 [:dependencies (get-env :dependencies)
                  :source-paths (vec (concat (get-env :source-paths)
                                             (get-env :resource-paths)))]))
        proj (pp-str head)]
      (if-not keep-project (.deleteOnExit pfile))
      (spit pfile proj)))

(deftask lein-generate
  "Generate a leiningen `project.clj` file.
   This task generates a leiningen `project.clj` file based on the boot
   environment configuration, including project name and version (generated
   if not present), dependencies, and source paths. Additional keys may be added
   to the generated `project.clj` file by specifying a `:lein` key in the boot
   environment whose value is a map of keys-value pairs to add to `project.clj`."
 []
 (generate-lein-project-file! :keep-project true))

(def boot-version
  (get (boot.App/config) "BOOT_VERSION" "2.5.5"))

(deftask from-lein
  "Use project.clj as source of truth as far as possible"
  []
  (let [lein-proj (let [l (-> "project.clj" slurp read-string)]
                    (merge (->> l (drop 3) (partition 2) (map vec) (into {}))
                           {:project (second l) :version (nth l 2)}))]
    (merge-env! :repositories (:repositories lein-proj))
    (set-env!
      :certificates   (:certificates lein-proj)
      :source-paths   (set (or (:source-paths lein-proj) #{"src"}))
      :resource-paths (set (or (:resource-paths lein-proj) #{"resources"}))
      :dependencies   (into (:dependencies lein-proj)
                            `[[boot/core ~boot-version   :scope "provided"]
                              [adzerk/bootlaces "0.1.13" :scope "test"]]))

    (require '[adzerk.bootlaces :refer :all])
    ((resolve 'bootlaces!) (:version lein-proj))
    (task-options!
      repl (:repl-options lein-proj {})
      aot  (let [aot (:aot lein-proj)
                 all? (or (nil? aot) (= :all aot))
                 ns (when-not all? (set aot))]
             {:namespace ns :all all?})
      jar  {:main (:main lein-proj)}
      pom  {:project     (symbol (:project lein-proj))
            :version     (:version lein-proj)
            :description (:description lein-proj)
            :url         (:url lein-proj)
            :scm         (:scm lein-proj)
            :license     (get lein-proj :license {"EPL" "http://www.eclipse.org/legal/epl-v10.html"})}))
  identity)

(deftask deps []
  identity)
