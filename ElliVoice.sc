

ElliVoice {

	var <>local, <>id, <>name;
	var <>voiceContainer, <>fxContainer, <>rtmView, <>pitchView, <>transposeViewRtm, <>transposeViewPitch;
	var <>seqView, <>mnmSlidePhrase, <>fxView;
	var <>sel_seq, <>sel_rhythm, <>sel_pitch, <>sel_fx, <>rtmTranspose, <>pitchTranspose;
	var <>seqCollection, <>rhythmCollection, <>pitchCollection, <>fxCollection, <>rtmTranspDict , <>pitchTranspDict ;
	var <>sel_defPbind, <>defPbindCol, <>sel_buf, <>bufCollection;

	var <>type, <>midiOut, <>midiChan;
	var <>voiceGroup, <>soundGroup, <>fxGroup;
	var <>muteStatus, <>soloState;
	var <>mainOut, <>fxIn, <>fxOut;
	var <>amp;
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

		this.muteState_(false);
		soloState = false;
		amp = 0.5;
		//midiOut=nil;
		//midiChan = 0;

		// MVC Models
		sel_seq = 0;
		sel_rhythm = 0;
		sel_pitch = 0;
		sel_fx = 0;
		rtmTranspose = 1;
		pitchTranspose = 1;

		sel_defPbind = 0;
		defPbindCol = IdentityDictionary.new;

		sel_buf = 0;
		bufCollection = IdentityDictionary.new;


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
		this.bufferChanged;
		this.pbindefChanged;
		this.muteChanged;



	}

	// small text windows for inserting new Patterns
	textRhythm { | position |
		var win, text;
		{
			win= Window.new("RTM "++ name.asString ++ " ( " ++ type.asString ++ ")", Rect(150,550,470,50)).background_(Color.red).front;
			text = TextField(win, Rect(10, 10, 450, 20));
			text.font_(Font("Andale Mono", 18));

			if( rhythmCollection[position].notNil){ // if something is there, show it
				text.string_(rhythmCollection[position].asCompileString)
			};

			text.action = { arg field;
				field.value.postln;
				rhythmCollection.put(position, field.value.interpret);
				win.close;
			};
		}.defer;
	}

	textPitch { | position |
		var win, text;
		{
			win= Window.new("PITCH " ++ name.asString ++ " ( " ++ type.asString ++ ")",
				Rect(150,550,470,50)).background_(Color.green).front;
			text = TextField(win, Rect(10, 10, 450, 20));
			text.font_(Font("Andale Mono", 18));

			if( pitchCollection[position].notNil){ // if something is there, show it
				text.string_(pitchCollection[position].asCompileString)
			};

			text.action = { arg field;
				field.value.postln;
				pitchCollection.put(position, field.value.interpret);
				pitchCollection.put(position, field.value.interpret);
				win.close
			};
		}.defer;
	}

	textBuffer { | position |
		var win, text;
		{
			if (type == \buf){
				win= Window.new(EE.longBufs.size.asString ++" LONGBufs  - "++ name.asString ++ " ( " ++ type.asString ++ ")",
					Rect(150,550,470,50)).background_(Color.magenta).front;
			}{
				win= Window.new(EE.shortBufs.size.asString ++" SHORTBufs  - "++ name.asString ++ " ( " ++ type.asString ++ ")",
					Rect(150,550,470,50)).background_(Color.yellow).front;

			};

			text = TextField(win, Rect(10, 10, 450, 20));
			text.font_(Font("Andale Mono", 18));

			if( bufCollection[position].notNil){ // if something is there, show it
				text.string_(bufCollection[position].asCompileString)
			};

			text.action = { arg field;
				field.value.postln;
				bufCollection.put(position, field.value.interpret);
				win.close;
			};
		}.defer;
	}

	// new window where you can edit the Pbindef. Pass all keys except Buffer and Group!
	patWindow { |pos|
		var string, win, source;
		var altDict = ();
		var pairs = Pbindef(name).source.source.source.patternpairs.asDict; // access keys+values
		var rout = pairs.keysValuesDo{ |k,v| // put everything in the new Dict except unwanted keys
			if( (k==\sndbuf) || (k=='group') ){ nil }{
				altDict.put( k, v.source.asCompileString ++ "\n");
		}};
		source = altDict.asString.replace(":", ","); // cook some String noodles
		source = source.replaceAt(" ",  0);
		string = "( \n" + "Pbindef( " ++ Pbindef(name).key.asCompileString ++ "," + "\n" ++ source + ") \n ;"; // final String
		win = string.newEditWindow; // edit it. New method in String : .newEditWindow; included in ElliClassExtensions
		win.onClose = { |self|
			defPbindCol.put(pos, self.text); // onClose, store the new Pbindef in a position
		};

	}



	// MVC Responders
	set_seq { | val, who|
		sel_seq = val;
		this.changed(\seq_changed, val, who);
	}

	set_rhythm { |val, who|

		if( (type == \buf) || (type == \sample) ){
			sel_buf = val;
			this.changed(\buffer_changed, val, who);
		}{
			sel_rhythm = val;
			this.changed(\rhythm_changed, val, who);
		}
	}

	set_rtmTranspose { | val, who|
		rtmTranspose = val;
		this.changed(\rtm_transpose, val, who);
	}

	set_pitch { | val, who|
		if( (type == \buf) || (type == \sample) ){
			sel_defPbind = val;
			this.changed(\pbindef_changed, val, who);
		}{
			sel_pitch = val;
			this.changed(\pitch_changed, val, who);

		}
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

	muteState_ { |val|

		muteStatus = val;
		("newState = "++ val).postcs;
		this.changed(\mute_changed, val);
	}

	/*mute { |val|


	}*/



	prepareAudioRouting {
		voiceGroup = Group.new;
		fxGroup = Group.after(voiceGroup)
	}

	getState {
		var params;
		params = [id, name, type, seqCollection, rhythmCollection, pitchCollection, fxCollection, rtmTranspDict , pitchTranspDict ];
		if (type==\midi){
			var adds = [midiOut, midiChan];
			params = params ++ adds;
		};
		if((type == \buf) || (type==\sample)){
			var adds = [bufCollection, defPbindCol];
			params = params ++ adds;
		}
		^params;
	}



	// MVC Controllers

	muteChanged {
		SimpleController(this).put(\mute_changed, { |obj, tag, val, who|

			if (val==true){ // mute

				if (Pbindef(name).isPlaying) {
					Pbindef(name).pause;
					"paused".postln;
				}{
					this.name.asString ++ " is already muted !".postln;
				};


				if( EE.mutesBlinkList[this.id].isPlaying ){ //blink the appropriate Voice button when muted
					nil;
				}{
					EE.mutesBlinkList[this.id].reset;
					EE.mutesBlinkList[this.id].play;
				};

			}{ // unmute
				if (Pbindef(name).isPlaying.not) {
					Pbindef(name).resume;
					"resumed".postln;
				};

				if( EE.mutesBlinkList[this.id].isPlaying){
					EE.mutesBlinkList[this.id].stop;
				}

			}

		})
	}

	sequenceChanged  {
		SimpleController(this).put(\seq_changed, { |obj, tag, val, who|

			var allParams = [ this.rhythms, this.pitches, this.fx];
			var bufParams = [sel_buf, sel_defPbind, this.fx];


			// While SHIFT+SEQ is pressed store the sequences in the Dictionary
			if( EE.shift == true)
			{
				if((type == \buf) || (type==\sample)){
					seqCollection.put(val, bufParams);
				}{
					seqCollection.put(val, allParams)}
			}
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

			if ( EE.shift == true)
			{ this.textRhythm(val)}  // with SHIFT pressed, enter a new pattern in the TextView
			{
				rtmView.setStepValueAction(val, false);

				if( rhythmCollection[val].notNil) {
					Pbindef(name, \dur, rhythmCollection[val]);
				}
			};




		})
	}

	bufferChanged  {
		SimpleController(this).put(\buffer_changed, { |obj, tag, val, who|

			if ( EE.shift == true)
			{ this.textBuffer(val)}  // with SHIFT pressed, enter a new pattern in the TextView
			{
				rtmView.setStepValueAction(val, false);

				if( bufCollection[val].notNil) {
					bufCollection[val].postln;
					if( bufCollection[val].isInteger){ // check whether an integer or a Pattern is stored
						if ( type == \buf){
							Pbindef(name, \sndbuf, EE.longBufs[bufCollection[val]]);
						}{
							Pbindef(name, \sndbuf, EE.shortBufs[bufCollection[val]]);
						}
					}{ // if it is a Pattern, get its List and assign it into Buffer indexes
						var pat = bufCollection[val].list;
						var bufRow, newPat;

						if ( type == \buf){

							bufRow = (pat.collect{|i| EE.longBufs.wrapAt(i).bufnum}).asString;
							newPat = bufCollection[val].class.asString ++ "(" + bufRow + ", inf ); \n";
							newPat.postln;
							Pbindef(name, \sndbuf, newPat.interpret);
						}{

							bufRow = (pat.collect{|i| EE.shortBufs.wrapAt(i).bufnum}).asString;
							newPat = bufCollection[val].class.asString ++ "(" + bufRow + ", inf ); \n";
							newPat.postln;
							Pbindef(name, \sndbuf, newPat.interpret);
						}
					}
				}
			};




		})
	}

	pitchChanged  {
		SimpleController(this).put(\pitch_changed, { |obj, tag, val, who|

			if ( EE.shift == true)
			{ this.textPitch(val) } // with SHIFT pressed, enter a new pattern in the TextView
			{ pitchView.setStepValueAction(val, false);

				if( pitchCollection[val].notNil) {
					Pbindef(name, \degree, pitchCollection[val]);
				}
			};
		})
	}

	pbindefChanged {
		SimpleController(this).put(\pbindef_changed, { |obj, tag, val, who|

			if ( EE.shift == true)
			{ this.patWindow(val) } // with SHIFT pressed, enter a new pattern in the TextView
			{ pitchView.setStepValueAction(val, false);

				if( defPbindCol[val].notNil) {
					defPbindCol[val].interpret;
					defPbindCol[val].postln;
				}
			};
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

		var s = Server.default;

		SimpleController(this).put(\voiceType_changed, { |obj, tag, val, who|

			Pbindef(name).clear; // if it exists, clear it;
			case
			{val == \osc} {"am an osci".postln;
				Pbindef(name,
					\server, Server.default,
					\group, voiceGroup,
					\instrument, \elliRing,
					\amp, amp,
					\degree, 0,
					\octave, 5
				);
			}
			{val == \buf} {

				"buf buf".postln;
				Pbindef(name,
					\server, Server.default,
					\group, voiceGroup,
					\instrument, \elliBuf,
					\amp, amp,
					\start, 0,
					\sndbuf, EE.longBufs[0].bufnum,
					\rate, 1,
					\len,  Pfunc{ |e|
						var tempo, duration, speed, newDur;
						tempo = EE.clock.tempo;
						duration = Buffer.cachedBufferAt(s, e.sndbuf).duration ;
						newDur = duration * tempo ;
						newDur
					},
					\dur,  16,
					\out, 2
				);
			}
			{val == \sample} {

				"am a sam".postln;
				Pbindef(name,
					\server, Server.default,
					\group, voiceGroup,
					\instrument, \elliBuf,
					\amp, amp,
					\start, 0,
					\sndbuf, EE.shortBufs[0].bufnum,
					\rate, 1,
					/*	\len,  Pfunc{ |e|
					var tempo, duration, speed, newDur;
					tempo = EE.clock.tempo;
					duration = e.sndbuf.duration ;
					newDur = duration * tempo ;
					newDur
					},*/
					\dur,  1,
					\out, 4
				);
			}
			{val == \midi} {"am ol midi".postln;

				Pbindef(name,
					\server, Server.default,
					\group, voiceGroup,
					\type, \midi,
					\midicmd, \noteOn,
					\chan, midiChan,
					\midiout, midiOut,
					\dur, 1,
					\degree, 0,
					\amp, amp
				);

				if (name ==\voice1){Pbindef(name, \octave, 3)};
				if (name ==\voice2){Pbindef(name, \octave, 4)};
			}

		})
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

	muteState {
		^muteStatus
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

}