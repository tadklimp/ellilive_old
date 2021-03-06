

ElliPiece {

	*new { |numVoices, type|
		// voiceType should be array, i.e. [\osc, \sample, \midi]
		^super.new.initElliPiece(numVoices, type);
	}


	initElliPiece { |argNumVoices, type|
		var voiceType = type;
		var s = Server.default;

		// max num. of voices = 10
		if (argNumVoices <= 10){

			argNumVoices.do{ |i|
				var voice;

				EE.voices.add( ElliVoice.new); // add a new Voice to the global List
				voice = EE.voices[i]; // access it

				// HACK: for now, automatically assign midiChans to new midiVoices
				if( voiceType[i] == \midi){
					voice.midiOut = EE.midiOut;
					voice.midiChan = EE.midiChanCount;
					EE.midiChanCount = EE.midiChanCount + 1;
				};

				voice.setVoiceType(voiceType[i]);
			};
			// make General Group
			// make Master Busses
		}

		{
			"MAX NUM. VOICES = 10".warn;
		};

	}
	*load {
		// etwas
	}

}