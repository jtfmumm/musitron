(ns musitron.core
  (:use [overtone.core]
        ) ;[musitron.bohlen-pierce])
  (:require [clojure.math.numeric-tower :as math]))

(boot-external-server)


(def M_PI 3.14159265358979323846264)


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


;;TESTS FOR PLAYING TONES, SEQUENCES, AND CHORDS
(defn group-play [tones]
	(loop [remaining tones]
		(if (not (empty? remaining))
			(do 
				(square-wave (first remaining))
				(recur (rest remaining))))))

(defn bp-cluster [& notes]
	(let [notes notes list-tones (all-bp-tones)]
		(group-play (map bp-midi->hz (map bp-note notes)))))

(defn test-chord [offset]
	(group-play (map bp-midi->hz (vector 36 (+ 36 offset)))))

(defn test-notes [rate & offsets]
	(loop [remaining offsets time (now) next-note 36]
		(at time (square-wave (bp-midi->hz next-note)))
		(if (not (empty? remaining))
			(recur (rest remaining) (+ time rate) (+ 36 (first remaining))))))

(defn bp-chord [root & intervals]
	(let [freq (bp-midi->hz (bp-note root))]
	(loop [remaining intervals freq freq final []]
		(if (not (empty? remaining))
			(recur (rest remaining) freq (conj final (* freq (math/expt bp-semitone (first remaining)))))
			final))))

(defn bp-lambda-chord [freq]
	(bp-chord freq 6 10))

(defn bp-lambda-2-chord [freq]
	(bp-chord freq 4 9))

(defn play-chord [chord root]
	(group-play (chord root)))

(defn quick-chords []
	(do
		(at (now) (bp-lambda-chord "C2"))
		(at (+ (now) 1000) (group-play (bp-lambda-2-chord "F2")))
		(at (+ (now) 2000) (group-play (bp-lambda-chord "G2")))
		(at (+ (now) 3000) (group-play (bp-lambda-2-chord "A2")))
		(at (+ (now) 4000) (group-play (bp-lambda-chord "F2")))))


;;Song Data Structure
(defn note-data  
    "Tick in ms from start of song.
     Note is a string like C#3 or J4.
     Velocity value ranges from 0-127.
     Duration in ms."
     [tick note velocity duration] 
    {:tick tick :note note :velocity velocity :duration duration})

(def song-data [])


;;Play song
(defn play-note [freq]
    (square-wave freq))

(defn play-song [song]
    (let [start (now)]
    (loop [remaining song this-note (first song)]
        (if-not (empty? remaining)
           (do
              (at (+ (this-note :tick) start) (play-note (bp-midi->hz (bp-note (this-note :note)))))
              (recur (rest remaining) (second remaining))
            )))))

(def simple-sequence ["H2" "J2" "A2" "B2" "C3" "D3" "E3" "F3" "G3" "H3" "J3" "A3" "B3"])

(defn simple-generate []
    (loop [counter 50 song [] tick 0 last-note 0]
        (if (> counter 0)
            (recur (dec counter)
                   (conj song (note-data
                              tick
                              (simple-sequence last-note)
                              50 200))
                   (+ tick (rand-int 700))
                   (markov-roll last-note markov-bp))
            song)))