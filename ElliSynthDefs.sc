

ElliSynthDefs {

	var s;

	*new { |server|

		^super.new.initElliSynthDefs(server)
	}

	initElliSynthDefs { |server|

		SynthDef(\elliSine, { |out=0, gate=1, freq=200, decay=0.5, amp=0.5|
			var source, env, exit;
			source = SinOsc.ar(freq, 0);
			env = EnvGen.kr(Env.sine(decay, 1), gate, amp, doneAction:2);
			exit = OffsetOut.ar(out, (source*env)!2);
		}).add;
		/*
		SynthDef(\elliMidi,{ |out=0|

		}).add;
		*/
		SynthDef(\elliBuf,{ |out=0, sndbuf=0, rate=1, start=0, gate=1, pan=0.5, amp=0.5, loop=0, att=0.01, rel=1|
			var source, env, exit;
			env = EnvGen.kr(Env.asr( att,1,rel ), gate, amp, doneAction:2);
			source = PlayBuf.ar(2, sndbuf, BufRateScale.kr(sndbuf)*rate, 1, start * BufFrames.kr(sndbuf), loop,2);
			source = LeakDC.ar(source);
			//env = Linen.kr(gate, susLevel:amp, doneAction:2);
			exit = OffsetOut.ar(out, Pan2.ar(source*env, pan))
		}).add;

		SynthDef(\elliRing, {|freq=200, trig=1, gate=1, out=0, decay=0.5, amp=0.5|
			var sig, exit, env;

			sig = Ringz.ar(Impulse.ar(trig).lag(0.002)+ WhiteNoise.ar(0.003),freq.lag(0.02),decay);
			env = EnvGen.ar(Env.perc(0.001,0.8),gate,amp,doneAction:2);
			exit = Out.ar(out, (sig*env).clip2(0.3)!2)

		}).add;

		SynthDef(\elliBeat, {|freq=0, phase=0, amp=0.2, ring =300, at=0.001, decay=1, rel=1, out=0, gate=1|
			var src ,filt, env, exit;
			src = Impulse.ar(freq,phase,0.5,0.1);
			filt = Ringz.ar(src, ring, decay,0.4);
			env = EnvGen.ar(Env.perc(at,rel),gate,amp,doneAction:2);
			env = env * AmpComp.kr(ring, 300);
			exit = OffsetOut.ar(out, filt*env!2 )
		}).add;

		SynthDef(\elliMinor,{| freq, dur, gate=1, amp=0.5, out=0, rel=2 |
			var sig,env,exit;

			sig = FSinOsc.ar(freq* SinOsc.kr(Rand(1.0,3.0)).range(1,1.01), SinOsc.kr(2)).tanh.softclip;

			env = sig * EnvGen.ar(Env.linen(0.0006,dur,rel,0.5),gate,amp,doneAction:2);
			env = env * AmpComp.kr(freq, 523.25,1.2)*0.2;
			exit = Out.ar(out,env!2);

		}).add;

		SynthDef(\elliWave, { |out, freq=440, amp=0.1, sustain=0.4, mod=0.2|
			OffsetOut.ar(out,
				EnvGen.ar(Env.perc(0.0001, sustain, amp), doneAction: 2)
				*
				SinOsc.ar(freq, SinOsc.ar(sustain.reciprocal * 8, [0, Rand(0, pi)], mod))
			)
		}).add;



	}
}


