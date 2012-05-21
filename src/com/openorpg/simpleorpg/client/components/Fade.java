package com.openorpg.simpleorpg.client.components;

import com.artemis.Component;

public class Fade extends Component {
	
	private int speed;
	private boolean fadeOut;
	private int alpha;
	
	public Fade(int speed, boolean fadeOut) {
		this.speed = speed;
		setFadeOut(fadeOut);
		
	}

	public int getAlpha() {
		return alpha;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void tick() {
		if (fadeOut) {
			alpha += speed;
		} else {
			alpha -= speed;
		}
	}

	public boolean isFadeOut() {
		return fadeOut;
	}

	public void setFadeOut(boolean fadeOut) {
		this.fadeOut = fadeOut;
		if (fadeOut) {
			alpha = 0;
		} else {
			alpha = 255;
		}
	}


}
