
ElliControls {


	var globalCtr, <selected ;
	var voiceChanged, pageChanged, sceneChanged, playbackChanged, shiftPressed, sequenceChanged;
	var <globalControlsContainer, voiceSelector, sceneView, pageToggle;
	var mixerView;
	var playButton, playBlinkRout, shiftKey;




	*new {
		^super.new.initElliControls;
	}


	initElliControls {

		////////////////////////////////////////////////////////////////
		//////////////// - Global Controls: Monome - ///////////////////
		////////////////////////////////////////////////////////////////


		// Includes all the global controls:
		globalControlsContainer = GRContainerView( EE.monome, 11@0, 5, 8);


		voiceSelector = GRHToggle( EE.monome, 0@0, EE.voices.size, 1); // VOICE Toggle Selector:
		voiceSelector.action = { |view, value|
			// inform the model
			this.set_voice(value, \voiceToggle);
		};
		this.set_voice(0, \init); // initialize Toggle position


		sceneView = GRStepView(globalControlsContainer, 1@0, 3,6).fill; // the SCENE's container + action
		sceneView.stepPressedAction = { |view, value|
			this.set_scene(value, \sceneToggle)
		};
		sceneView.blinkNegative;
		this.set_scene(0, \init); // Initialise Scene position

		// PAGE Toggle selector
		// select between COMPOSE page and FX page
		pageToggle = GRHToggle(globalControlsContainer, 1@7, 3, 1);
		pageToggle.action = { |view, value|
			// inform the model
			this.set_page(value, \pageToggle);
		};
		this.set_page(0, \init); // initialize Toggle position

		// PLAY Button
		playButton = GRButton(globalControlsContainer, 4@0);
		// Blink Playbutton on the TempoClock's tempo.
			playBlinkRout = Routine{ loop{ playButton.flash; (1/EE.clock.tempo).wait }};
		// blink only when pressed
			playButton.action = {|view, value|
				if (playBlinkRout.isPlaying){ playBlinkRout.stop;}
				{ playBlinkRout.reset; playBlinkRout.play};
				this.set_play(value)
			};

		// SHIFT key - press and hold a Scne or Pattern to store - momentary
		shiftKey = GRButton(globalControlsContainer, 4@7, behavior:\momentary);
		shiftKey.buttonPressedAction = { |view, value|
			this.set_shift(true);
		};
		shiftKey.buttonReleasedAction = { |view, value|
			this.set_shift(false);
		};

		// MVC "View" of Toggle Selections
		voiceChanged = SimpleController(EE).put(\voice_changed, { |obj, tag, val, who|

			// access each Voice's Container and FX
			var container = EE.voices[val].container;
			var fxC = EE.voices[val].fxBox;
			var seqV = EE.voices[val].sequenceView;

			// voice asks which page is selected:
			switch( EE.selPage,
				nil, { container.bringToFront; seqV.bringToFront },
				0, { container.bringToFront; seqV.bringToFront; },
				1, { if ( fxC.isDisabled )
					{ fxC.enable; fxC.bringToFront; seqV.bringToFront;}
					{ fxC.bringToFront; seqV.bringToFront; }
				},
				2, { container.bringToFront; seqV.bringToFront; }
			);
			// initialise voice selection
			//	if ( who == \init) {this.set_voice(val)};
			//[obj, tag, key, val, who].postln;
		});

		pageChanged = SimpleController(EE).put(\page_changed, { |obj, tag, val, who|

			var voice = EE.selVoice;
			var container = EE.voices[voice].container;
			var fxC = EE.voices[voice].fxBox;
			var seqV = EE.voices[val].sequenceView;
			// page asks which voice is displayed:
			switch( val,
				nil, { container.bringToFront; seqV.bringToFront },
				0, { container.bringToFront; seqV.bringToFront },
				1, { if (fxC.isDisabled)
					{fxC.enable; fxC.bringToFront; seqV.bringToFront; }
					{fxC.bringToFront; seqV.bringToFront }
				},
				2, { container.bringToFront; seqV.bringToFront; }
			)
			//[obj, tag, key, val, who].postln;
		});

		sceneChanged = SimpleController(EE).put(\scene_changed, { |obj, tag, val, who|

			var voice = EE.selVoice;
			var seq = EE.voices[voice].sequenceView.value.indicesOfEqual(false); // find which sequence is currently selected
			var allSeqs = EE.voices.size.collect{ |i| EE.voices[i].sequenceView.value.indicesOfEqual(false)}; // access the SeqView page of each Voice
			var press = {
				if( EE.scenes.at(val).notNil) // pressed Scene-button logic function -> recall ALL asssigned sequences
				{ EE.voices.size.do{ |i|
					var newValue = EE.scenes[val][i];
					 if (newValue.notNil){
						EE.voices[i].sequenceView.setStepValueAction(newValue[0], false);
						EE.voices[i].set_seq(newValue[0], \scene_toggle); // inform the model that SEQ has changed
					}
					{"some seqs left unchanged".warn;}
					//newValue.postln;
				}}
				{"scene is empty".warn}
			};

			// If SHIFT+Scene is pressed store the sequences in the "scenes" Dictionary
			// else trigger the scene
			if ( EE.shift == true)
			{ EE.scenes.put( val, allSeqs); ( "STORED SCENE " ++ val).postln; EE.scenes.postln}
			{ press.value; ( "SCENE " ++ val).postln;};

			// initialise scene selection
			//if ( who == \init) {this.set_scene = val};
		});


		playbackChanged = SimpleController(EE).put(\playback, { |obj, tag, val, who|

			if(EE.clock.isRunning){
			if(val==true){
				Pbindef.all.keys.do{ |x| Pdef(x).play(EE.clock, quant:1);}
			}{
				Pbindef.all.keys.do{ |x| Pdef(x).stop;}
			}}{
				"CLOCK IS NOT RUNNING!".warn
			};

		});

		shiftPressed = SimpleController(EE).put(\shiftMode, { |obj, tag, val, who|

		});


	}

	set_voice { | val, who|
		EE.selVoice = val;
		EE.changed(\voice_changed, val, who);
	}

	set_page { | val, who|
		EE.selPage = val;
		EE.changed(\page_changed, val, who);
	}

	set_scene { | val, who|
		EE.selScene = val;
		EE.changed(\scene_changed, val, who);
	}

	set_play { | val, who|
		EE.play = val;
		EE.changed(\playback, val, who);
	}

	set_shift { | val, who|
		EE.shift = val;
		EE.changed(\shiftMode, val, who);
	}

	set_bpm { | val, who|
		EE.bpm = val;
		EE.changed(\tempo, val, who);
	}


}