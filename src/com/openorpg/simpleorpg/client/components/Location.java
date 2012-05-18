package com.openorpg.simpleorpg.client.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Location extends Component {
	private Vector2f position;
	
	public Location(Vector2f position) {
		setPosition(position);
	}
	
	public Location(float x, float y) {
		setPosition(new Vector2f(x, y));
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

}
