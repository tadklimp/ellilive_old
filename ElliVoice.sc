

ElliVoice : ElliPiece {

	classvar <allVoices;

	var <>local, <>ancestor;
	var create, <container, <fx;




	*initClass {

		allVoices = []
	}

	*new { |argParent|
		^super.new.initElliVoice(argParent)
	}


	*all {
		^allVoices
	}


	initElliVoice { | myParent |

		var sequenceChanged, rhythmChanged;

		ancestor = myParent;

		container =  GRContainerView( myParent, 0@1, 11, 7);
		fx = GRContainerView.newDisabled( myParent, 0@1, 11, 4, true);

		// Initialize Voice State
		local = (
			// init Monome Views
			voiceContainer: container,
			rtmView: GRStepView(container, 1@0, 4, 4).fill,
			soundView: GRStepView(container, 7@0, 4, 4).fill,
			// FIXME: last toggle key should be RESET => momentary + resets toggle position
			transposeViewRtm: GRVToggle(container, 0@0, 1, 4),
			transposeViewSound: GRVToggle(container, 6@0, 1, 4),
			seqView: GRStepView(container, 1@5, 10, 2).fill,
			// FIXME: toggle behaviour?
			mnmSlidePhrase: GRVToggle(container, 0@5, 1, 2),
			fxContainer: fx,
			fxView: GRStepView(fx, 3@0, 5, 4).fill,

			// MVC Model Dictionaries
			seqs: (),
			rhythm: (),
			sound: (),
			voiceFx:(),
			rtmTranspose:1,
			soundTranspose:1,


			// MVC Responders
			set_seq:{ |env, val, who|
				env[\seq] = val;
				env.changed(\seq_changed, val, who);
			},
			set_rhythm:{ |env, val, who|
				env[\rhythm] = val;
				env.changed(\rhythm_changed, val, who);
			},
			set_rtmTranspose:{ |env, val, who|
				env[\rtmTranspose] = val;
				env.changed(\rtm_transpose, val, who);
			},
			set_sound:{ |env, val, who|
				env[\sound] = val;
				env.changed(\sound_changed, val, who);
			},
			set_soundTranspose:{ |env, val, who|
				env[\soundTranspose] = val;
				env.changed(\sound_transpose, val, who);
			},
			set_voiceFx:{ |env, val, who|
				env[\voiceFx] = val;
				env.changed(\voiceFx_changed, val, who);
			},

		);

		// MVC "Controllers"


		// seq buttons
		local.seqView.stepPressedAction = { |view, value|
			local.set_seq(value, \user);
		};
		// rtm buttons
		local.rtmView.stepPressedAction = { |view, value|
			local.set_rhythm(value, \user);
		};

		// rtmTranspose buttons
		local.transposeViewRtm.action = { |view, value|
			local.set_rtmTranspose(value, \user);
		};
		// sound buttons
		local.soundView.stepPressedAction = { |view, value|
			local.set_sound(value, \user);
		};
		// soundTranspose buttons
		local.transposeViewSound.action = { |view, value|
			local.set_soundTranspose(value, \user);
		};

		// MVC Responders

		// seq changed
		sequenceChanged = SimpleController(local).put(\seq_changed, { |obj, tag, val, who|
			//local.seq.postln;
			var allParams = [local.rhythm, local.sound, local.voiceFx,];
			//if(who == \scene_toggle){{SinOsc.ar(Rand(300,800))*EnvGen.kr(Env.perc,doneAction:2)}.play;}

			// While SHIFT+SEQ is pressed store the sequences in the Dictionary
			if(1==1)
			{ local.seqs.put(val, allParams); local.seqs[val].postln }
			{
				if( local.seqs.at(val) != nil)
				{	var newRhythm = local.seqs[val][0][0];
					var newSound = local.seqs[val][1][0];
					var newFx = local.seqs[val][2][0];

					local.set_rhythm( newRhythm);
					local.set_sound( newSound);
					local.set_voiceFx( newFx);
				}
				{"EMPTY SEQ".warn}
			};
		});



		rhythmChanged = SimpleController(local).put(\rhythm_changed, { |obj, tag, val, who|
			local.rhythm.postln;

		});

		local.seqView.blinkNegative;
		local.soundView.showSelectedNeg;
		local.rtmView.showSelectedNeg;

		allVoices = allVoices.add(this);


		// return the Dictionary
		^local

	}

	contained {
		^container
	}
}