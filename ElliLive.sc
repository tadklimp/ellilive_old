
ElliLive {


	*new { |numVoices, newType|
		^super.new.initElliLive(numVoices, newType)
	}



	initElliLive { | numVoices, voiceTypeArray |

		var settings, midi, midiOutPorts, midiInPorts;

		// settings = ElliPresets.new;
		// check if a voiceTypeArray is supplied and valid
		if (voiceTypeArray.size == numVoices)
		{
			EE.new;

			ElliPiece.new(numVoices, voiceTypeArray);

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


			// HACK: for now, automatically assign midiChans to new midiVoices
			EE.voices.size.do{ |i|
				var voice = EE.voices[i];
				if(voice.type == \midi){
					voice.midiOut = EE.midiOut;
					voice.midiChan = EE.midiChanCount;
					EE.midiChanCount = EE.midiChanCount + 1;
			}};


		}{
			"VoiceType Array size should be equal to the number of Voices!".warn;
		};
	}
}