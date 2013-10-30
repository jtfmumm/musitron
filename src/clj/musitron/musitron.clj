(ns musitron.core
  (:use [overtone.core]
        [musitron.bohlen-pierce])
  (:require [clojure.math.numeric-tower :as math]))

(boot-external-server)


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



