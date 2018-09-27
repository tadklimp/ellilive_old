
ElliLive {


	*new { |numVoices, newType|
		^super.new.initElliLive(numVoices, newType)
	}



	initElliLive { | numVoices, voiceTypeArray |
		var settings, midi, midiOutPorts, midiInPorts;

		//newType = type;
		// settings = ElliPresets.new;
		if (voiceTypeArray.size == numVoices)
		{
			EE.new;

			ElliPiece.new(numVoices, voiceTypeArray);

			//numVoices.do{ |i| EE.voices[i].setVoiceType(newType[i])};

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
		}{
			"VoiceType Array size should be equal to the number of Voices!".warn;
		};
	}
}