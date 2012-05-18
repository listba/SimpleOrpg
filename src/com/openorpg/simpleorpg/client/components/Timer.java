package com.openorpg.simpleorpg.client.components;

import java.util.Date;

import com.artemis.Component;

public class Timer extends Component {
	private long finishedTime;
	int waitTime;

	public Timer(int waitTime) {
		this.waitTime = waitTime;
		this.finishedTime = new Date().getTime() + waitTime;
	}
	
	public boolean isFinished() {
		if (new Date().getTime() > finishedTime) {
			return true;
		} else {
			return false;
		}
	}
	
	public void reset() {
		this.finishedTime = new Date().getTime() + waitTime;
	}

}
