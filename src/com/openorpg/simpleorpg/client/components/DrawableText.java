package com.openorpg.simpleorpg.client.components;

import com.artemis.Component;

public class DrawableText extends Component {
	
	private String text;
	
	public DrawableText(String text) {
		setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
