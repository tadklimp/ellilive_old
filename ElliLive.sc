
ElliLive {


	*new { |numVoices|
		^super.new.initElliLive(numVoices)
	}



	initElliLive { | numVoices = 3 |
		var settings, midi, midiOutPorts, midiInPorts;

		// settings = ElliPresets.new;

		EE.new;

		ElliPiece.new(numVoices);

		ElliControls.new;

		EE.preferences;

		midi = EE.prefs.midi;
		midiOutPorts = EE.prefs.midiOutPorts;
		midiInPorts = EE.prefs.midiInPorts;

		// init MIDI
		if(midi == true, {
			"MIDI is ON".postln;
		MIDIClient.init;
		//midiOut = MIDIOut.newByName("FireWire 410", "FireWire 410");
		EE.midiOut = MIDIOut.newByName("IAC Driver", "Bus 1").latency_(Server.default.latency);
		});
	}
}