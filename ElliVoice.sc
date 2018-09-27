

ElliVoice {

	var <>local, <id, <name;
	var <>voiceContainer, <>fxContainer, <>rtmView, <>pitchView, <>transposeViewRtm, <>transposeViewPitch;
	var <>seqView, <>mnmSlidePhrase, <>fxView;
	var <>sel_seq, <>sel_rhythm, <>sel_pitch, <>sel_fx, <>rtmTranspose, <>pitchTranspose;
	var <>seqCollection, <>rhythmCollection, <>pitchCollection, <>fxCollection, <>rtmTranspDict , <>pitchTranspDict ;

	var <>type, <>midiOut, <>midiChan;
	var <>voiceGroup, <>soundGroup, <>fxGroup;
	var <>mainOut, <>fxIn, <>fxOut;

	var <>pbind;


	*new {
		^super.new.initElliVoice
	}


	initElliVoice {


		id = EE.voices.size;
		name = ("voice"++ this.id).asSymbol;
		voiceGroup = Group.new(EE.group);
		soundGroup = Group.new(voiceGroup, \addToHead);
		fxGroup = Group.new(voiceGroup, \addToTail);
		//midiOut=nil;
		//midiChan = 0;

		// MVC Models
		sel_seq = 0;
		sel_rhythm = 0;
		sel_pitch = 0;
		sel_fx = 0;
		rtmTranspose = 1;
		pitchTranspose = 1;

		seqCollection = IdentityDictionary.new;
		rhythmCollection = IdentityDictionary.new;
		rtmTranspDict = IdentityDictionary.new;
		pitchCollection = IdentityDictionary.new;
		pitchTranspDict = IdentityDictionary.new;
		fxCollection = IdentityDictionary.new;

		// Monome Stuff
		voiceContainer =  GRContainerView( EE.monome, 0@1, 11, 7);
		fxContainer = GRContainerView.newDisabled( EE.monome, 0@1, 11, 4, true);

		rtmView = GRStepView(voiceContainer, 1@0, 4, 4).fill;
		pitchView = GRStepView(voiceContainer, 7@0, 4, 4).fill;
		// FIXME: last toggle key should be RESET => momentary + resets toggle position
		transposeViewRtm = GRVToggle(voiceContainer, 0@0, 1, 4);
		transposeViewPitch = GRVToggle(voiceContainer, 6@0, 1, 4);
		seqView = GRStepView(voiceContainer, 1@5, 10, 2).fill;
		// FIXME: toggle behaviour?
		mnmSlidePhrase = GRVToggle(voiceContainer, 0@5, 1, 2);

		fxView = GRStepView(fxContainer, 3@0, 5, 4).fill;


		// Button Actions
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
		// pitch buttons
		pitchView.stepPressedAction = { |view, value|
			this.set_pitch(value, \user);
		};
		// pitchTranspose buttons
		transposeViewPitch.action = { |view, value|
			this.set_pitchTranspose(value, \user);
		};

		fxView.stepPressedAction = { |view, value|
			this.set_voiceFx(value);
		};


		seqView.blinkNegative;
		pitchView.showSelectedNeg;
		rtmView.showSelectedNeg;


		// init the MVC responders
		this.sequenceChanged;
		this.rhythmChanged;
		this.pitchChanged;
		this.typeChanged;
		this.fxChanged;




	}

	// MVC Responders
	set_seq { | val, who|
		sel_seq = val;
		this.changed(\seq_changed, val, who);
	}

	set_rhythm { |val, who|
		sel_rhythm = val;
		this.changed(\rhythm_changed, val, who);
	}

	set_rtmTranspose { | val, who|
		rtmTranspose = val;
		this.changed(\rtm_transpose, val, who);
	}

	set_pitch { | val, who|
		sel_pitch = val;
		this.changed(\pitch_changed, val, who);
	}

	set_pitchTranspose { | val, who|
		pitchTranspose = val;
		this.changed(\pitch_transpose, val, who);
	}

	set_voiceFx { | val, who|
		sel_fx = val;
		this.changed(\voiceFx_changed, val, who);
	}

	setVoiceType { |val, who|
		type = val;
		this.changed(\voiceType_changed, val, who);
	}

	// access Instance variables

	container {
		^voiceContainer
	}

	rtmBox {
		^rtmView
	}

	pitchBox {
		^pitchView
	}

	fxBox {
		^fxContainer
	}

	transposeRtmToggle {
		^transposeViewRtm
	}

	transposePitchToggle {
		^transposeViewPitch
	}

	sequenceView {
		^seqView
	}

	sequencer {
		^sel_seq
	}

	rhythms {
		^sel_rhythm
	}

	pitches {
		^sel_pitch
	}

	fx {
		^sel_fx
	}

	rhythmTransp {
		^rtmTranspose
	}

	pitchTransp {
		^pitchTranspose
	}

	// access/change Dictionaries

	seqDict {
		^seqCollection
	}

	seqDict_ { |val|
		seqCollection = IdentityDictionary.new;
		seqCollection.putAll(val);
	}

	rhythmDict {
		^rhythmCollection
	}

	rhythmDict_ { |val|
		rhythmCollection = IdentityDictionary.new;
		rhythmCollection.putAll(val);
	}

	pitchDict {
		^pitchCollection
	}

	pitchDict_ { |val|
		pitchCollection = IdentityDictionary.new;
		pitchCollection.putAll(val);
	}

	fxDict {
		^fxCollection
	}

	fxDict_ { |val|
		fxCollection = IdentityDictionary.new;
		fxCollection.putAll(val);
	}
	voiceType {
		^type
	}

	prepareAudioRouting {
		voiceGroup = Group.new;
		fxGroup = Group.after(voiceGroup)
	}

	// MVC Responders

	// seq changed
	sequenceChanged  {
		SimpleController(this).put(\seq_changed, { |obj, tag, val, who|

			var allParams = [ this.rhythms, this.pitches, this.fx];
			//if(who == \scene_toggle){{SinOsc.ar(Rand(300,800))*EnvGen.kr(Env.perc,doneAction:2)}.play;}

			// While SHIFT+SEQ is pressed store the sequences in the Dictionary
			if( EE.shift == true)
			{ seqCollection.put(val, allParams); seqCollection[val].postln }
			{// otherwise recall the SEQ at val
				if( seqCollection.at(val).notNil)
				{	var newRhythm = seqCollection[val][0];
					var newPitch = seqCollection[val][1];
					var newFx = seqCollection[val][2];

					// and trigger the appropriate rhythms/pitches/fx
					this.set_rhythm( newRhythm, \user);
					this.set_pitch( newPitch, \user);
					this.set_voiceFx( newFx, \user);
				}
				{"EMPTY SEQ".warn}
			};

			if(who != \user and: who != \scene_toggle){
				seqView.setStepValueAction(val, false)
			}
		})

	}

	rhythmChanged  {
		SimpleController(this).put(\rhythm_changed, { |obj, tag, val, who|

			rtmView.setStepValueAction(val, false);

			if( rhythmCollection[val].notNil) {
				rhythmCollection[val].postln;
			}

		})
	}

	pitchChanged  {
		SimpleController(this).put(\pitch_changed, { |obj, tag, val, who|
			pitchView.setStepValueAction(val, false)

		})
	}

	fxChanged  {
		SimpleController(this).put(\voiceFx_changed, { |obj, tag, val, who|

			if (fxCollection[val].notNil){
				//fxCollection[val].state(\on);
				fxCollection[val].postln;

			}
		})
	}

	typeChanged {
		SimpleController(this).put(\voiceType_changed, { |obj, tag, val, who|

			Pbindef(name).clear; // if it exists, clear it;
			case
			{val == \osc} {"am an osci".postln;  }
			{val == \sample} {"am a sam".postln;  }
			{val == \midi} {"am ol midi".postln;
				Pbindef(name,
					\server, Server.default,
					\group, voiceGroup,
					\type, \midi,
					\midicmd, \noteOn,
					\chan, midiChan,
					\midiout, midiOut
				)
			}

		})
	}
}