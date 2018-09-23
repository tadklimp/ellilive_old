
// the EllicistLive storage for all environmental variables

EE {

	classvar <>monome, <>topView, <>voices, <>scenes;
	classvar <>clock, <>bpm;
	classvar <>bufferDict;
	classvar <>prefs;

	*new {
		^super.new.initEE;
	}

	initEE {

		topView = GRTopView(16,8);
		monome = GRHMonome128.new(\main, topView, 0@0);
		voices = List.new; // Store all Voices here
		scenes = (); // Store all Scenes here
		bufferDict = ();

		// ADD sounds folder path
		// ADD prefs file path
		// ADD MIDI support and INIT
	}

}