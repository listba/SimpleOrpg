package com.openorpg.simpleorpg.server;

import java.net.Socket;
import java.util.HashMap;

public class Map {
	private final HashMap<Socket, Player> players = new HashMap<Socket, Player>();
	
	public Map() {
	}

	public HashMap<Socket, Player> getPlayers() {
		return players;
	}

}
