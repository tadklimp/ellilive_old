
ElliLive {


	*new { |numVoices|
		^super.new.initElliLive(numVoices)
	}



	initElliLive { | numVoices = 3 |

		EE.new;

		ElliPiece.new(numVoices);

		ElliControls.new;

	}
}