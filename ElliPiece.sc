

ElliPiece {

	*new { |numVoices, type|
		// voiceType should be array, i.e. [\osc, \sample, \midi]
		^super.new.initElliPiece(numVoices, type);
	}


	initElliPiece { |argNumVoices, type|
		var voiceType = type;

		argNumVoices.do{ |i|
			EE.voices.add( ElliVoice.new);
			EE.voices[i].setVoiceType(voiceType[i]);
		};

	}

	*load {
		// etwas
	}

}