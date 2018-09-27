
// the EllicistLive storage for all environmental variables

EE {

	classvar <>topView, <>monome, <>voices, <>scenes;
	classvar <>clock ;
	classvar <>bufferDict;
	classvar <>group;
	classvar <>root; // this is for the Pdefns
	classvar <>prefs;
	classvar <>selVoice=0, <>selPage=0, <>selScene=0, <>play=false, <>shift=false ;
	classvar <>prefs, <>midi, <>midiOut, <>midiChanCount ;


	*new {
		^super.new.initEE;
	}

	initEE {

		topView = GRTopView(16,8);
		monome = GRHMonome128.new(\main, topView, 0@0);
		voices = List.new; // Store all Voices here
		scenes = IdentityDictionary.new; // Store all Scenes here
		bufferDict = ();
		group = Group.new;

		// HACK for automatic midi chan assignement
		midiChanCount = 0;

		clock = TempoClock(2).permanent_(false);
		// ADD sounds folder path
		// ADD prefs file path
		// ADD MIDI support and INIT
		"MIDI is ON".postln;
		MIDIClient.init;
		//midiOut = MIDIOut.newByName("FireWire 410", "FireWire 410");
		midiOut = MIDIOut.newByName("IAC Driver", "Bus 1").latency_(Server.default.latency);


	}

	*clear {
		// here most have to be reset
		// keep monome, view, clock, prefs
		// remove all SimpleControllers
		Buffer.freeAll;
		voices = List.new; // Store all Voices here
		scenes = IdentityDictionary.new; // Store all Scenes here
		bufferDict = ();
	}

	*bpm { |newBpm|

		this.clock.tempo = (newBpm / 60);
	}

	*preferences {

		this.prefs = ()
		.bitDepth_("float32") // possible: "int16", "int24", "int32"
		.numberOfChannels_(6) // number of audio channels used
		.midi_(true) // using midi or not?
		.midiInPorts_( 2 ) // how many inports you are using
		.midiOutPorts_( 3 ); // how many outports
	}

}