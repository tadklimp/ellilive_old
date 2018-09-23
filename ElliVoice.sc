

ElliVoice {

	var <>local;
	var <>voiceContainer, <>fxContainer, <>rtmView, <>soundView, <>transposeViewRtm, <>transposeViewSound;
	var <>seqView, <>mnmSlidePhrase,  <>fxView;
	var <>selected_seq, <>seqs, <>rhythm, <>sound, voiceFx, <>rtmTranspose, <>soundTranspose;


	*new {
		^super.new.initElliVoice
	}


	initElliVoice {

		var sequenceChanged, rhythmChanged;

		//	ancestor = myParent;

		voiceContainer =  GRContainerView( EE.monome, 0@1, 11, 7);
		fxContainer = GRContainerView.newDisabled( EE.monome, 0@1, 11, 4, true);

		// Initialize Voice State

		// init Monome Views

		rtmView = GRStepView(voiceContainer, 1@0, 4, 4).fill;
		soundView = GRStepView(voiceContainer, 7@0, 4, 4).fill;
		// FIXME: last toggle key should be RESET => momentary + resets toggle position
		transposeViewRtm = GRVToggle(voiceContainer, 0@0, 1, 4);
		transposeViewSound = GRVToggle(voiceContainer, 6@0, 1, 4);
		seqView = GRStepView(voiceContainer, 1@5, 10, 2).fill;
		// FIXME: toggle behaviour?
		mnmSlidePhrase = GRVToggle(voiceContainer, 0@5, 1, 2);

		fxView = GRStepView(fxContainer, 3@0, 5, 4).fill;

		// MVC Model Dictionaries
		selected_seq = 0;
		seqs = IdentityDictionary.new;
		rhythm = IdentityDictionary.new;
		sound = IdentityDictionary.new;
		voiceFx = IdentityDictionary.new;
		rtmTranspose = 1;
		soundTranspose = 1;




		// seq buttons
		seqView.stepPressedAction = { |view, value|
			this.set_seq(value, \user);
		};
		// rtm buttons
		rtmView.stepPressedAction = { |view, value|
			this.set_rhythm(value, \user);
		};

		// rtmTranspose buttons
		transposeViewRtm.action = { |view, value|
			this.set_rtmTranspose(value, \user);
		};
		// sound buttons
		soundView.stepPressedAction = { |view, value|
			this.set_sound(value, \user);
		};
		// soundTranspose buttons
		transposeViewSound.action = { |view, value|
			this.set_soundTranspose(value, \user);
		};

		this.sequenceChanged;

		seqView.blinkNegative;
		soundView.showSelectedNeg;
		rtmView.showSelectedNeg;

	}

	// MVC Responders
	set_seq { | val, who|
		selected_seq = val;
		this.changed(\seq_changed, val, who);
	}

	set_rhythm { |val, who|
		rhythm = val;
		this.changed(\rhythm_changed, val, who);
	}

	set_rtmTranspose { | val, who|
		rtmTranspose = val;
		this.changed(\rtm_transpose, val, who);
	}

	set_sound { | val, who|
		sound = val;
		this.changed(\sound_changed, val, who);
	}

	set_soundTranspose { | val, who|
		soundTranspose = val;
		this.changed(\sound_transpose, val, who);
	}

	set_voiceFx { | val, who|
		voiceFx = val;
		this.changed(\voiceFx_changed, val, who);
	}

	container {
		^voiceContainer
	}

	rtmBox {
		^rtmView
	}

	soundBox {
		^soundView
	}

	fxBox {
		^fxContainer
	}

	transposeRtmToggle {
		^transposeViewRtm
	}

	transposeSoundToggle {
		^transposeViewRtm
	}

	sequenceView {
		^seqView
	}

	sequencer {
		^seqs
	}

	rhythms {
		^rhythm
	}

	sounds {
		^sound
	}

	fx {
		^voiceFx
	}

	rhythmTransp {
		^rtmTranspose
	}

	soundTransp {
		^soundTranspose
	}
	// MVC Responders

	// seq changed
	sequenceChanged  {
		SimpleController(this).put(\seq_changed, { |obj, tag, val, who|
			//local.seq.postln;
			/*
			var allParams = [ this.rhythms, this.sounds, this.fx];
			//if(who == \scene_toggle){{SinOsc.ar(Rand(300,800))*EnvGen.kr(Env.perc,doneAction:2)}.play;}

			// While SHIFT+SEQ is pressed store the sequences in the Dictionary
			if( EE.shift == true)
			{ seqs.put(val, allParams); seqs[val].postln }
			{
				if( seqs.at(val) != nil)
				{	var newRhythm = seqs[val][0];
					var newSound = seqs[val][1];
					var newFx = seqs[val][2];

					this.set_rhythm( newRhythm);
					this.set_sound( newSound);
					this.set_voiceFx( newFx);
				}
				{"EMPTY SEQ".warn}
			};
			*/
			"checking if Controller works".postln;
			[obj,tag,val,who].postln;
		})

	}

	rhythmChanged  {
		SimpleController(this).put(\rhythm_changed, { |obj, tag, val, who|
			rhythm.postln;

		})
	}
}