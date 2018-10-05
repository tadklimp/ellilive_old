
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
				var c = Condition.new;

				EE.new;

				ElliSynthDefs.new;
				s.sync;


				SoundFile.collect("/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/Samples/short/*")
				.do{ |item, i|
					var name = item.path;
					c.test = false;

					if ( name.contains("wav") || name.contains("aif") || name.contains("aiff") || name.contains("WAV"))
					{ EE.shortBufs.add(Buffer.readChannel(s, name, action:{ c.test_(true).signal; }));
						c.wait;
						EE.shortBufs[i].postln;
					};
				};

				SoundFile.collect("/Users/Makis/Library/Application Support/SuperCollider/Extensions/Tadklimp/Classes/ElliLive/Samples/long/*")
				.do{ |item, i|
					var name = item.path;
					c.test = false;

					if ( name.contains("wav") || name.contains("aif") || name.contains("aiff") || name.contains("WAV"))
					{	EE.longBufs.add(Buffer.read(s, name, action:{ c.test_(true).signal; }));
					c.wait;
					EE.longBufs[i].postln;
					};
				};


				//cond.test_(true).signal;


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