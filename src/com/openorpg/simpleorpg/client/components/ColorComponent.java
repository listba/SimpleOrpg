package com.openorpg.simpleorpg.client.components;

import org.newdawn.slick.Color;

import com.artemis.Component;

public class ColorComponent extends Component {
	private Color color;
	
	public ColorComponent(Color color) {
		setColor(color);
	}
	
	public ColorComponent(String color) {
		setColor(Color.decode(color));
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
