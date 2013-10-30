(ns musitron.bohlen-pierce
    (:use [overtone.core])
    (:require [clojure.math.numeric-tower :as math]))


(def M_PI 3.14159265358979323846264)


;FOR EQUAL TEMPERAMENT
(def bp-semitone (math/expt 3 1/13))


;OSCILLATORS
(definst sin-wave [freq 440 attack 0.1 sustain 0.4 release 5.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (sin-osc freq)
     vol))

(defn square-wave [freq]
  (let [attack 0.1 sustain 0.4 release 2.1 vol 0.4]
  (loop [harmonics 6 k 1]
      (if (> harmonics 0)
          (if-not (= (mod k 2) 0)
              (do
                (sin-wave (* freq k) attack sustain release (* vol (/ 1 k)))
                (recur (dec harmonics) (inc k)))
              (recur (dec harmonics) (inc k)))))))

(defn saw-wave [freq]
  (let [attack 0.1 sustain 0.4 release 2.1 vol 0.4]
  (loop [harmonics 6 k 1]
      (if (> harmonics 0)
          (do 
              (sin-wave (* freq k) attack sustain release (* vol (/ 1 k)))
              (recur (dec harmonics) (inc k)))))))


;;GENERATE ALL BOHLEN-PIERCE TONES
(defn all-bp-tones []
  (lazy-seq
  (loop [this-tone 8.175798915643707 list-tones []]
     (if (< this-tone 4187)
      (recur (* this-tone bp-semitone) (conj list-tones this-tone))
      list-tones))))


;;SCALES
(def bp-diatonic-scale [3 1 2 1 2 1 3])
;Lambda family
(def bp-lambda-scale [2 1 1 2 1 2 1 2 1])
(def bp-harmonic-scale [1 2 1 2 1 2 1 1 2])
(def bp-moll-ii-scale [1 2 1 2 1 2 1 2 1])
(def bp-walker-a-scale [1 1 2 1 2 1 2 1 2])
;Gamma family
(def bp-dur-ii-scale [2 1 1 2 1 2 1 1 2])


(def markov-bp [[]
                []
                []
                []
                []
                []
                []
                []
                []
                []
                []
                []
                []])

(defn bp-midi->hz [note]
  (nth (all-bp-tones) note))




;;Altered Overtone source code to integrate Bohlen-Pierce system 

(def MIDI-NOTE-RE-STR-BP "([a-jA-J][#bB]?)([-0-9]+)" )
(def MIDI-NOTE-RE-BP (re-pattern MIDI-NOTE-RE-STR-BP))
(def ONLY-MIDI-NOTE-RE-BP (re-pattern (str "\\A" MIDI-NOTE-RE-STR-BP "\\Z")))

(def BP-NOTES {:C 0 :c 0 :b# 0 :B# 0
            :C# 1 :c# 1 :Db 1 :db 1 :DB 1 :dB 1
            :D 2 :d 2
            :E 3 :e 3
            :F 4 :f 4
            :F# 5 :f# 5 :Gb 5 :gb 5 :GB 5 :gB 5
            :G 6 :g 6
            :H 7 :h 7
            :H# 8 :h# 8 :Jb 8 :jb 8 :JB 8 :jB 8
            :J 9 :j 9
            :A 10 :a 10
            :A# 11 :a# 11 :Bb 11 :bb 11 :BB 11 :bB 11
            :B 12 :b 12 :Cb 12 :cb 12 :CB 12 :cB 12})

(def REVERSE-BP-NOTES
  {0 :C
   1 :C#
   2 :D
   3 :E
   4 :F
   5 :F#
   6 :G
   7 :H
   8 :H#
   9 :J
   10 :A
   11 :A#
   12 :B})

(defn canonical-bp-pitch-class-name
  "Returns the canonical version of the specified pitch class pc."
  [pc]
  (let [pc (keyword (name pc))]
      (REVERSE-BP-NOTES (BP-NOTES pc))))


(defn- midi-string-matcher-bp
  "Determines whether a midi keyword is valid or not. If valid,
returns a regexp match object"
  [mk]
  (re-find ONLY-MIDI-NOTE-RE-BP (name mk)))

(defn- validate-bp-midi-string!
  "Throws a friendly exception if midi-keyword mk is not
valid. Returns matches if valid."
  [mk]
  (let [matches (midi-string-matcher-bp mk)]
    (when-not matches
      (throw (IllegalArgumentException.
              (str "Invalid midi-string. " mk
                   " does not appear to be in MIDI format i.e. C#4"))))

    (let [[match pictch-class octave] matches]
      (when (< (Integer. octave) -1)
        (throw (IllegalArgumentException.
                (str "Invalid midi-string: " mk
                     ". Octave is out of range. Lowest octave value is -1")))))
    matches))

(defn bp-note-info
  "Takes a string representing a midi note such as C4 and returns a map
of note info"
  [midi-string]
  (let [[match pitch-class octave] (validate-bp-midi-string! midi-string)
        pitch-class (canonical-bp-pitch-class-name pitch-class)
        octave (Integer. octave)
        interval (BP-NOTES (keyword pitch-class))]
    {:match match
     :pitch-class pitch-class
     :octave (Integer. octave)
     :interval interval
     :midi-note (octave-note octave interval)}))

(defn bp-note
  "Resolves note to MIDI number format. Resolves upper and lower-case
keywords and strings in MIDI note format. If given an integer or
nil, returns them unmodified. All other inputs will raise an
exception."
  [n]
  (cond
    (nil? n) nil
    (integer? n) (if (>= n 0)
                   n
                   (throw (IllegalArgumentException.
                           (str "Unable to resolve note: "
                                n
                                ". Value is out of range. Lowest value is 0"))))
    (keyword? n) (bp-note (name n))
    (string? n) (:midi-note (bp-note-info n))
    :else (throw (IllegalArgumentException. (str "Unable to resolve note: " n ". Wasn't a recognised format (either an integer, keyword, string or nil)")))))

