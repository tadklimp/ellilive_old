

ElliPresets {

	var <presetDict;


	*new {
		^super.new.initElliPresets
	}

	initElliPresets {
		var file = "/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/presets.elli";

		if( Object.readArchive(file).isNil, {
			presetDict = IdentityDictionary.new;
			//"ixi-NOTE: NO WORRIES! 'presets.ixi' gets created when you store a preset".postln;
		}, {
			presetDict = Object.readArchive(file);
		});
	}
	// here you load the file if it exists


	load { | name |
		var setting;
		var s = Server.default;



		//EE.preferences;
		fork{
			EE.clear;
			s.sync;

			EE.recallBufs;

			s.sync;
			3.wait;

			setting = presetDict.at(name.asSymbol);

			setting.do{ | preset, i |
				var index = preset[0];
				var sets = preset[1];
				var thisVoice;

				if (index != \scenes){
					EE.voices.insert(index, ElliVoice.new);

					s.sync;

					thisVoice = EE.voices[index];
					//thisVoice.name.postln;
					//sets.postcs;


					thisVoice.name = sets[1];

					if (sets[2] == \midi){
						thisVoice.midiChan = sets[10];
						thisVoice.midiOut = sets[9];
					};
					if ((sets[2] == \buf) || (sets[2] == \sample)){
						thisVoice.defPbindCol = sets[10];
						thisVoice.bufCollection = sets[9];
					};
					thisVoice.setVoiceType(sets[2]);
					thisVoice.seqDict_(sets[3]);
					thisVoice.rhythmDict_(sets[4]);
					thisVoice.pitchDict_(sets[5]);
					thisVoice.fxDict_(sets[6]);

				}{
					EE.scenes = sets;
				}
			};
			ElliControls.new;

		};

		//("the size is "++setting.size).postln;
		//setting.size.postln;
		// load
		// for ElliVoice
		// setVoiceType seqDict_
	}

	store { | name |

		var preset, file;
		"*********** STORE PRESET ******************".postln;
		preset = List.new;

		EE.voices.do{ |voice, i|
			preset.add([ voice.id, voice.getState]);
		};

		preset.add([\scenes, EE.scenes]);

		presetDict.add(name.asSymbol -> preset);
		presetDict.writeArchive("/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/presets.elli");
		//preset.postcs;
		// write
		// for ElliVoice:
		// voiceType seqDict rhythmDict[transpose] pitchDict[transpose] fxDict
		// b = EE.voices.size.collect{|i| EE.voices[i].type} // get each Voice's type
	}

	remove {
		// remove sthng from the Dict
	}

	entries {
		^this.presetDict.keys
	}


}



