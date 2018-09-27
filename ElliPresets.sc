

ElliPresets {

	var presetDict;


	*new {
		^super.new.initElliPresets
	}

	initElliPresets {

		// here you load the file if it exists
	}

	load {
		// load
		// for ElliVoice
		// setVoiceType seqDict_
	}

	store {
		// write
		// for ElliVoice:
		// voiceType seqDict rhythmDict[transpose] pitchDict[transpose] fxDict
		// b = EE.voices.size.collect{|i| EE.voices[i].type} // get each Voice's type
	}

	remove {
		// remove sthng from the Dict
	}


}


