(ns musitron.core
  (:use [overtone.core])
  (:require [clojure.math.numeric-tower :as math]))

;(boot-external-server)

(def semitone (math/expt 2 1/12))

(stop)

(definst tone-player [freq 440] (square freq))

;Low piano tone is A0 27.5, high piano tone is C8 4186.01
(defn all-tones []
	(loop [this-tone 27.5 list-tones [27.5]]
		 (if (< this-tone 4187)
		 	(recur (* this-tone semitone) (conj list-tones this-tone))
		 	list-tones)))


;(defn play-tone [tone octave degree] (tone octave degree somehow))

(defn gonow [] 
	(loop [remaining (all-tones) time (now)]
		(if (not (empty? remaining))
			(do 
				(at time (stop))
							;(tone-player (first remaining))))
				(recur (rest remaining) (+ 1000 time))))))

(definst sin-wave [freq 440 attack 0.1 sustain 0.4 release 5.1 vol 0.4] 
  (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
     (sin-osc freq)
     vol))

(defn group-play [tones]
	(loop [remaining tones]
		(if (not (empty? remaining))
			(do 
				(sin-wave (first remaining))
				(recur (rest remaining))))))

;(group-play [400 500 600 700])
;(saw 440)

(defn some-chord [freq & intervals]
	(loop [remaining intervals final [freq]]
		(if (not (empty? remaining))
			(recur (rest remaining) (conj final (* freq (math/expt semitone (first remaining)))))
			final)))
	;(list freq (* freq (math/expt semitone interval1)) (* freq (math/expt semitone interval2))))

;(group-play (some-chord 440 4 7))

(defn major-chord [freq] 
	(some-chord freq 4 7))

(defn minor-chord [freq] 
	(some-chord freq 3 7))

(defn diminished-chord [freq]
	(some-chord freq 3 6))

(defn seventh-chord [freq]
	(some-chord freq 4 7 10))

(defn minor-seventh-chord [freq]
	(some-chord freq 3 7 10))

(defn bohlen-triad [freq]
	())

(defn scale [])

(def major-scale [2 2 1 2 2 2 1])
(def minor-scale [2 1 2 2 1 3 1])
(def whole-scale [2 2 2 2 2])

(def bohlen-diatonic [(/ 9 7) (/ 7 5) (/ 5 3) (/ 9 5) (/ 15 7) (/ 7 3) (/ 9 3)])

(defn play-bohlen-scale [freq rate]
	(loop [remaining bohlen-diatonic next-freq freq time (now) freq freq]
		(at time (sin-wave next-freq))
		(if (not (empty? remaining))
			(recur (rest remaining) (* freq (first remaining)) (+ time rate) freq))))

(sin-wave (* 550 (/ 7 5))) 
(play-bohlen-scale 100 400)
(play-bohlen-scale 300 400)
(play-bohlen-scale 900 400)

(defn play-major-scale [freq rate]
	(loop [remaining major-scale freq freq time (now)]
		(at time (sin-wave freq))
		(if (not (empty? remaining))
				(recur (rest remaining) (* freq (math/expt semitone (first remaining))) (+ time rate)))))


(defn play-scale [freq scale rate octaves]
	(loop [remaining scale freq freq time (now)]
		(at time (sin-wave freq))
		(if (not (empty? remaining))
				(recur (rest remaining) (* freq (math/expt semitone (first remaining))) (+ time rate)))))


(play-scale 220 whole-scale 300)

(defn quick-chords []
	(do
		(at (now) (major-chord 440))
		(at (+ (now) 1000) (group-play (minor-chord 550)))
		(at (+ (now) 2000) (group-play (diminished-chord 660)))
		(at (+ (now) 3000) (group-play (minor-seventh-chord 550)))
		(at (+ (now) 4000) (group-play (major-chord 440)))))

(quick-chords)



(defn arpeggio [type chord]
	(loop []))

;(group-play (diminished-chord 400))
(group-play (minor-seventh-chord 220))



;(group-play 440 (* 440 (math/expt semitone 4)) (* 440 (math/expt semitone 7)))

; (definst spooky-house [freq 440 width 0.2 
;                          attack 0.3 sustain 4 release 0.3 
;                          vol 0.4] 
;   (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
;      (sin-osc (+ freq (* 20 (lf-pulse:kr 0.5 0 width))))
;      vol))

;(spooky-house)

