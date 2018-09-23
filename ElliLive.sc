
ElliLive {


	*new { |numVoices|
		^super.new.initElliLive(numVoices)
	}



	initElliLive {

		EE.new;


		ElliPiece.new(3);

		ElliControls.new;

	}
}