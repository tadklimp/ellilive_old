

+ MIDIClockOut {


	start {
		CmdPeriod.add(this);
		click = 0;
		sched.beat = 0.0;
		port.songPtr(sched.beat);
		//port.start;
		isPlaying = true;
		this.next;
	}

	stop {
		sched.clear;
		//port.stop;
		isPlaying = false;
		CmdPeriod.remove(this);
	}

}