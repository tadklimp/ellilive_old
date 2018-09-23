

ElliPiece {

	*new { |numVoices|

		^super.new.initElliPiece(numVoices);
	}


	initElliPiece { |argNumVoices|

		argNumVoices.do{
			EE.voices.add( ElliVoice.new( ) );
		};

	}

}