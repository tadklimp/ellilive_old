

ElliPiece {

	classvar
	var


	*new { |numVoices|
		^super.new.initPiece(numVoices)
	}



	initPiece { |argNumVoices|


		argNumVoices.do{
			EE.voices.add( ElliVoice.new( ) );
		};

		ElliMain.new();


	}


}