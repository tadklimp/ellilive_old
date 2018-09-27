

ElliPiece {

	*new { |numVoices, type|
		// voiceType should be array, i.e. [\osc, \sample, \midi]
		^super.new.initElliPiece(numVoices, type);
	}


	initElliPiece { |argNumVoices, type|
		var voiceType = type;

		// max num. of voices = 10
		if (argNumVoices <= 10){

			argNumVoices.do{ |i|
				var voice;

				EE.voices.add( ElliVoice.new);

				voice = EE.voices[i];
				voice.setVoiceType(voiceType[i]);
			};
			// make General Group
			// make Master Busses
		}
		{
			"MAX NUM. VOICES = 10".warn;
		}
	}
	*load {
		// etwas
	}

}