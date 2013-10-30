(defn get-freq [])




(def semitone (math/expt 2 1/12))

(stop)

(definst tone-player [freq 440] (square freq))

;Low piano tone is A0 27.5, high piano tone is C8 4186.01
(defn all-tones []
	(loop [this-tone 8.175798915643707 list-tones []]
		 (if (< this-tone 4187)
		 	(recur (* this-tone semitone) (conj list-tones this-tone))
		 	list-tones)))


; (defn get-octave [midi-number system-length]
;   ;Starting from octave -1 for midi value 0
;   (- (int (/ midi-number system-length)) 1))

; (defn octave-degree->midi [octave degree system-length]
;   (+ (* (+ octave 1) system-length) degree)) ;Degrees from 0-scalelength


;(defn play-tone [tone octave degree] (tone octave degree somehow))

(defn gonow [] 
	(loop [remaining (all-tones) time (now)]
		(if (not (empty? remaining))
			(do 
				(at time (stop))
							;(tone-player (first remaining))))
				(recur (rest remaining) (+ 1000 time))))))



;(group-play [400 500 600 700])
;(saw 440)

(defn play-some-notes []
	(let [time (now)]
		(at time (sin-wave (get-freq (octave-degree->midi 4 0 13) all-bp-tones)))
		(at (+ time 1000) (sin-wave (get-freq (octave-degree->midi 4 3 13) all-bp-tones)))
		(at (+ time 2000) (sin-wave (get-freq (octave-degree->midi 4 4 13) all-bp-tones)))
		(at (+ time 3000) (sin-wave (get-freq (octave-degree->midi 4 9 13) all-bp-tones)))
		(at (+ time 4000) (sin-wave (get-freq (octave-degree->midi 4 7 13) all-bp-tones)))
		))

(defn play-bp-notes [rate & notes]
	(loop [remaining notes time (now) this-note (first notes)]
		(if (not (empty? remaining))
			(do
				(at time (sin-wave (get-freq (octave-degree->midi (first this-note) (second this-note) 13) all-bp-tones)))
				(recur (rest remaining) (+ time rate) (second remaining))))))

(defn some-chord [freq & intervals]
	(loop [remaining intervals final [freq]]
		(if (not (empty? remaining))
			(recur (rest remaining) (conj final (* freq (math/expt semitone (first remaining)))))
			final)))


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

;(defn bp-triad [freq]
;	(some-chord ))

;(defn scale [])

(def major-scale [2 2 1 2 2 2 1])
(def minor-scale [2 1 2 2 1 3 1])
(def whole-scale [2 2 2 2 2])


(def bp-diatonic [(/ 9 7) (/ 7 5) (/ 5 3) (/ 9 5) (/ 15 7) (/ 7 3) (/ 9 3)])

(defn play-bp-scale [freq rate]
	(loop [remaining bp-diatonic next-freq freq time (now) freq freq]
		(at time (sin-wave next-freq))
		(if (not (empty? remaining))
			(recur (rest remaining) (* freq (first remaining)) (+ time rate) freq))))

(defn play-bp [octave degree]
	(sin-wave (get-freq (octave-degree->midi octave degree 13) all-bp-tones)))




;(sin-wave (* 550 (/ 7 5))) 
;(play-bp-scale 100 400)
;(play-bp-scale 300 400)
;(play-bp-scale 900 400)

(defn play-major-scale [freq rate]
	(loop [remaining major-scale freq freq time (now)]
		(at time (sin-wave freq))
		(if (not (empty? remaining))
				(recur (rest remaining) (* freq (math/expt semitone (first remaining))) (+ time rate)))))



(defn arpeggio [type chord]
	(loop []))

;(group-play (diminished-chord 400))
;(group-play (minor-seventh-chord 220))



;(group-play 440 (* 440 (math/expt semitone 4)) (* 440 (math/expt semitone 7)))

; (definst spooky-house [freq 440 width 0.2 
;                          attack 0.3 sustain 4 release 0.3 
;                          vol 0.4] 
;   (* (env-gen (lin-env attack sustain release) 1 1 0 1 FREE)
;      (sin-osc (+ freq (* 20 (lf-pulse:kr 0.5 0 width))))
;      vol))

;(spooky-house)