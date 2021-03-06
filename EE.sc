
// the EllicistLive storage for all environmental variables

EE {

	classvar <>topView, <>monome, <>voices, <>scenes;
	classvar <>clock ;
	classvar <>shortBufs, <>longBufs;
	classvar <>group, <>mutes, <>solos, <>mutesBlinkList;
	classvar <>root; // this is for the Pdefns
	classvar <>prefs;
	classvar <>selVoice=0, <>selPage=0, <>selScene=0, <>play=false, <>shift=false , <>alt=false;
	classvar <>prefs, <>midi, <>midiOut, <>midiChanCount, <>midiClock ;


	*new {
		^super.new.initEE;
	}

	initEE {

		topView = GRTopView(16,8);
		monome = GRHMonome128.new(\main, topView, 0@0);
		voices = List.new; // Store all Voices here
		scenes = IdentityDictionary.new; // Store all Scenes here
		mutes = IdentityDictionary.new();
		mutesBlinkList = List.new;
		solos = IdentityDictionary.new();
		group = Group.new;

		// HACK for automatic midi chan assignement
		midiChanCount = 1;

		clock = TempoClock(2).permanent_(false);
		// ADD sounds folder path
		// ADD prefs file path
		// ADD MIDI support and INIT
		"MIDI is ON".postln;
		MIDIClient.init;
		//midiOut = MIDIOut.newByName("FireWire 410", "FireWire 410").latency_(Server.default.latency);
		//midiOut = MIDIOut.newByName("Fireface 800 (FA6)", "Port 1").latency_(Server.default.latency);
		midiOut = MIDIOut.newByName("IAC Driver", "Bus 1").latency_(Server.default.latency);
		midiClock = MIDIClockOut.new(midiOut, tempoClock: clock);

		if (shortBufs.notNil || longBufs.notNil){
			if(shortBufs.size > 0 ){shortBufs.do(_.free); shortBufs.clear};
			if(longBufs.size > 0 ){longBufs.do(_.free); longBufs.clear};
			Buffer.freeAll;
		};
		shortBufs = List.new;
		longBufs = List.new;

	}

	*clear {
		var s = Server.default;
		// here most have to be reset
		// keep monome, view, clock, prefs
		// remove all SimpleControllers
		fork{
			EE.voices.do{ |voice| EE.mutesBlinkList[voice.id].stop };

			Pdef.removeAll;
			s.sync;
			midiClock.stop;
			s.sync;
			if (shortBufs.notNil || longBufs.notNil){
				if(shortBufs.size > 0 ){shortBufs.do(_.free); shortBufs.clear};
				if(longBufs.size > 0 ){longBufs.do(_.free); longBufs.clear};
			};
			Buffer.freeAll;
			s.sync;
			group.freeAll;
			Server.default.freeAll;
			s.sync;

			group = Group.new;
			s.sync;
			voices = List.new; // Store all Voices here
			scenes = IdentityDictionary.new; // Store all Scenes here
			shortBufs = List.new;
			longBufs = List.new;
			mutes = IdentityDictionary.new();
			mutesBlinkList = List.new;
			solos = IdentityDictionary.new();
			//group = Group.new;
			s.sync;
		}
	}

	*bpm { |newBpm|

		this.clock.tempo = (newBpm / 60);
	}

	*preferences {

		this.prefs = ()
		.bitDepth_("float32") // possible: "int16", "int24", "int32"
		.numberOfChannels_(6) // number of audio channels used
		.midi_(true) // using midi or not?
		.midiInPorts_( 2 ) // how many in-ports you are using
		.midiOutPorts_( 3 ); // how many out-ports
	}

	*recallBufs {

		fork{
			var s = Server.default;
			var c = Condition.new;
			SoundFile.collect("/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/Samples/short/*")
			.do{ |item, i|
				var name = item.path;
				c.test = false;

				if ( name.contains("wav") || name.contains("aif") || name.contains("aiff") || name.contains("WAV"))
				{ EE.shortBufs.add(Buffer.read(s, name, action:{ c.test_(true).signal; }).normalize(0.8));
					c.wait;
					EE.shortBufs[i].postln;
				};
			};

			s.sync;

			SoundFile.collect("/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/Samples/long/*")
			.do{ |item, i|
				var name = item.path;
				c.test = false;

				if ( name.contains("wav") || name.contains("aif") || name.contains("aiff") || name.contains("WAV"))
				{	EE.longBufs.add(Buffer.read(s, name, action:{ c.test_(true).signal; }).normalize(0.8));
					c.wait;
					EE.longBufs[i].postln;
				};
			}

		}
	}


}