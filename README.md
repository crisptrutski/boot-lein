boot-lein
=========

Example preamble for "using boot in a lein project":

```clojure
(merge-env!
  :dependencies
  '[[crisptrutski/boot-lein "0.1.0" :scope "test"]])

(require '[crisptrutski.boot-lein :refer :all])

(from-lein)
```

