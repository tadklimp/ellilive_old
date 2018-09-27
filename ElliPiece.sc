

ElliPiece {

	*new { |numVoices|

		^super.new.initElliPiece(numVoices);
	}


	initElliPiece { |argNumVoices|

		argNumVoices.do{ |i|
			EE.voices.add( ElliVoice.new);
		};

	}

	*load {
		// etwas
	}

}