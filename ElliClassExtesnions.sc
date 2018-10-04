

+ MIDIClockOut {


	*new { arg deviceName, portName, tempoClock;
		var port;
		if(deviceName.notNil , {
			//port = MIDIOut.newByName(deviceName, portName);
			port = EE.midiOut;
		}, {
			port = MIDIOut.new(0, MIDIClient.destinations[0].uid);
		});
		if(port.isNil, {
			Error("Device not found " + deviceName + portName).throw;
		});
		^super.newCopyArgs(BeatSched.new(tempoClock:tempoClock), port)
	}

	start {
		CmdPeriod.add(this);
		click = 0;
		sched.beat = 0.0;
		port.songPtr(sched.beat);
		port.start;
		isPlaying = true;
		this.next;
	}

	stop {
		sched.clear;
		port.stop;
		isPlaying = false;
		CmdPeriod.remove(this);
	}

}


+String {

	// open a new Pbindef window to edit it. On close don't ask to save.
	newEditWindow { arg title="Untitled", makeListener=false;
		var doc = Document.new(title, this, makeListener)
		.promptToSave_(false);
		/*.keyDownAction = { |view, char, mod, unicode, keycode, key|
		//[view, char, mod, unicode, keycode, key].postln; // Uncomment this to check what key you are pressing.
		if(key == 16777220 && mod.isCtrl, {this.interpret; doc.close}
			)};
		*/
	}
}
