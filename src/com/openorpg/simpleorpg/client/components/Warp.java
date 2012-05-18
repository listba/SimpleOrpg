package com.openorpg.simpleorpg.client.components;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.Component;

public class Warp extends Component {
	private Vector2f position;
	private String mapRef;
	
	public Warp() {
		setMapRef("");
		setPosition(new Vector2f(0,0));
	}
	
	public Warp(String map, Vector2f position) {
		setMapRef(map);
		setPosition(position);
	}
	
	public Vector2f getPosition() {
		return position;
	}
	
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	
	public String getMapRef() {
		return mapRef;
	}
	
	public void setMapRef(String mapRef) {
		this.mapRef = mapRef;
	}

}
