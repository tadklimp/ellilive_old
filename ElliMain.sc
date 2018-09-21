
ElliMain : ElliPiece{


	var  <scenes, globalCtr, <selected, <>ancestor;
	var voiceChanged, pageChanged, sceneChanged, playbackChanged, shiftPressed, sequenceChanged;
	var globalControlsContainer, voiceSelector, sceneView, pageToggle;
	var mixerView;
	var playButton, playBlinkRout, shiftKey;


	*new { |argParent|
		^super.new.initElliMain(argParent)
	}



	initElliMain { |myParent|
		// MVC model of Global Toggle/Button States (Voice/Page/Scene/Play)
		// selected has to be a GLobal variable

		ancestor = myParent;

		selected = (
			voice: nil,
			page: nil,
			scene: nil,
			play: false,
			shift: false,
			tempo: 100,

			set_voice:{ |env, val, who|
				env[\voice] = val;
				env.changed(\voice_changed, val, who);
			},
			set_page:{ |env, val, who|
				env[\page] = val;
				env.changed(\page_changed, val, who);
			},
			set_scene:{ |env, val, who|
				env[\scene] = val;
				env.changed(\scene_changed, val, who);
			},
			set_play:{ |env, val, who|
				env[\play] = val;
				env.changed(\playback, val, who);
			},
			set_shift:{ |env, val, who|
				env[\shift] = val;
				env.changed(\shiftMode, val, who);
			},
			set_tempo:{ |env, val, who|
				env[\tempo] = val;
				env.changed(\tempo, val, who);
			}
		);


		// MVC "View" of Toggle Selections
		voiceChanged = SimpleController(selected).put(\voice_changed, { |obj, tag, val, who|

			// access each Voice's Container and FX
			var container = voices[val].voiceContainer;
			var fxC = voices[val].fxContainer;

			// voice asks which page is selected:
			switch( selected[\page],
				nil, { container.bringToFront},
				0, { container.bringToFront},
				1, { if (fxC.isDisabled)
					{fxC.enable; fxC.bringToFront}
					{fxC.bringToFront}},
				2, { container.bringToFront}
			);
			// initialise voice selection
			//	if ( who == \init) {selected.set_voice = val};
			//[obj, tag, key, val, who].postln;
		});

		pageChanged = SimpleController(selected).put(\page_changed, { |obj, tag, val, who|

			var voice = selected[\voice];
			var container = voices[voice].voiceContainer;
			var fxC = voices[voice].fxContainer;
			// page asks which voice is displayed:
			switch( val,
				nil, { container.bringToFront},
				0, { container.bringToFront},
				1, { if (fxC.isDisabled)
					{fxC.enable; fxC.bringToFront}
					{fxC.bringToFront}},
				2, { container.bringToFront}
			)
			//[obj, tag, key, val, who].postln;
		});

		sceneChanged = SimpleController(selected).put(\scene_changed, { |obj, tag, val, who|
			var voice = selected[\voice];
			var page = selected[\page];
			var seq = voices[voice].seqView.value.indicesOfEqual(false); // find which sequence is currently selected
			var allSeqs = voices.size.collect{ |i| voices[i].seqView.value.indicesOfEqual(false)}; // access the SeqView page of each Voice
			var press = {if( scenes.at(val) != nil) // pressed Scene-button logic function -> recall ALL asssigned sequences
				{voices.size.do{ |i|
					var newValue = scenes[val][i][0]; // the last [0] is a trick to drop the array and access the integer inside
					voices[i].seqView.setStepValueAction(newValue, false);
					voices[i].set_seq(newValue, \scene_toggle); // inform the model that SEQ has changed
				}}
				{"scene is empty".warn}};

			// If SHIFT+Scene is pressed store the sequences in the "scenes" Dictionary
			// else trigger the scene
			if ( selected[\shift]==true)
			{ scenes.put( val, allSeqs); ( "STORED SCENE " ++ val).postln; scenes.postln}
			{ press.value; ( "SCENE " ++ val).postln};

			// initialise scene selection
			//if ( who == \init) {selected.set_scene = val};
		});


		playbackChanged = SimpleController(selected).put(\playback, { |obj, tag, val, who|

		});

		shiftPressed = SimpleController(selected).put(\shiftMode, { |obj, tag, val, who|
			var voice = selected[\voice];
			var page = selected[\page];
			var scene = selected[\scene];
		});

		////////////////////////////////////////////////////////////////
		//////////////// - Global Controls: Monome - ///////////////////
		////////////////////////////////////////////////////////////////



		// Includes all the global controls:
		globalControlsContainer = GRContainerView(ancestor, 11@0, 5, 8);



		// VOICE Toggle Selector:
		// TODO: ask which scene is selected?
		voiceSelector = GRHToggle(ancestor, 0@0, voices.size, 1);
		voiceSelector.action = { |view, value|
			// inform the model
			selected.set_voice(value, \voiceToggle);
		};
		selected.set_voice(0, \init); // initialize Toggle position

		// the SCENE's container + action
		sceneView = GRStepView(globalControlsContainer, 1@0, 3,6).fill;
		sceneView.stepPressedAction = { |view, value|
			selected.set_scene(value, \sceneToggle)
		};
		sceneView.blinkNegative;
		//selected.set_scene(0, \init); // Initialise Scene position

		// PAGE Toggle selector
		// select between COMPOSE page and FX page
		pageToggle = GRHToggle(globalControlsContainer, 1@7, 3, 1);
		pageToggle.action = { |view, value|
			// inform the model
			selected.set_page(value, \pageToggle);
		};
		selected.set_page(0, \init); // initialize Toggle position

		// PLAY Button
		playButton = GRButton(globalControlsContainer, 4@0);
		// Blink Playbutton on the TempoClock's tempo.
		playBlinkRout = Routine{ loop{ playButton.flash; (1/clock.tempo).wait }};
		// blink only when pressed
		playButton.action = {|view, value|
			if (playBlinkRout.isPlaying){ playBlinkRout.stop;}
			{ playBlinkRout.reset; playBlinkRout.play};
			selected.set_play(value)
		};

		// SHIFT key - press and hold a Scne or Pattern to store - momentary
		shiftKey = GRButton(globalControlsContainer, 4@7, behavior:\momentary);
		shiftKey.buttonPressedAction = { |view, value|
			selected.set_shift(true);
		};
		shiftKey.buttonReleasedAction = { |view, value|
			selected.set_shift(false);
		};

		^selected
	}
}