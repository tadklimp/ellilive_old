
ElliLive {


	*new { |numVoices, newType|
		^super.new.initElliLive(numVoices, newType)
	}



	initElliLive { | numVoices, voiceTypeArray |

		var s = Server.default;
		var settings, midi, midiOutPorts, midiInPorts;

		// settings = ElliPresets.new;
		// check if a voiceTypeArray is supplied and valid
		if (voiceTypeArray.size == numVoices)
		{
			fork{

				EE.new;

				ElliSynthDefs.new;
				s.sync;

				//cond.test_(true).signal;
				EE.recallBufs;

				ElliPiece.new(numVoices, voiceTypeArray);

				ElliControls.new;

				EE.preferences;

				midi = EE.prefs.midi;
				midiOutPorts = EE.prefs.midiOutPorts;
				midiInPorts = EE.prefs.midiInPorts;
			}

		}{
			"VoiceType Array size should be equal to the number of Voices!".warn;
		};
	}
}