
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


		}{
			"VoiceType Array size should be equal to the number of Voices!".warn;
		};
	}
}