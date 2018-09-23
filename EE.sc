
// the EllicistLive storage for all environmental variables

EE {

	classvar <>topView, <>monome, <>voices, <>scenes;
	classvar <>clock ;
	classvar <>bufferDict;
	classvar <>prefs;
	classvar <>selVoice=0, <>selPage=0, <>selScene=0, <>play=false, <>shift=false, <>bpm ;

	var clear;

	*new {
		^super.new.initEE;
	}

	initEE {

		topView = GRTopView(16,8);
		monome = GRHMonome128.new(\main, topView, 0@0);
		voices = List.new; // Store all Voices here
		scenes = IdentityDictionary.new; // Store all Scenes here
		bufferDict = ();

		// ADD sounds folder path
		// ADD prefs file path
		// ADD MIDI support and INIT
	}

	clear {
		// here most have to be reset
		// keep monome, view, clock, prefs
		// remove all SimpleControllers
	}

}