package com.openorpg.simpleorpg.client.components;

public class Say extends Timer {
	private String text;

	public Say(String text, int waitTime) {
		super(waitTime);
		setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
