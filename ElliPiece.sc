

ElliPiece : Object{

	var <>clock, <>bpm, <numVoices;

	classvar  <monome, <topView, <voices;


	*initClass {
		voices = []
	}


	*new { |numVoices|
		^super.new.initPiece(numVoices)
	}



	initPiece { |argNumVoices|

		topView = GRTopView(16,8);
		monome = GRHMonome128.new(\main, topView, 0@0);

		numVoices = argNumVoices;

		{
			argNumVoices.do{
				voices = voices.add( ElliVoice.new( ) );
			};
			0.5.wait;

			ElliMain.new();

		}.fork(SystemClock);


	}


}