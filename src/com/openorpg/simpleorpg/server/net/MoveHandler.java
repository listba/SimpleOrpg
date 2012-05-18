package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Map;
import com.openorpg.simpleorpg.server.Player;

public class MoveHandler extends MessageHandler {
	
	private String payload;
	public MoveHandler(String payload) {
		this.payload = payload;
	}

	@Override
	public void handleMessage(Socket socket) {
		synchronized(this) {
			Player yourPlayer = players.get(socket);
			Map map = maps.get(yourPlayer.getMapRef());
			
			if (payload.equals("UP")) {
				yourPlayer.setLocation(yourPlayer.getX(), yourPlayer.getY()-1);
			} else if (payload.equals("DOWN")) {
				yourPlayer.setLocation(yourPlayer.getX(), yourPlayer.getY()+1);
			} else if (payload.equals("LEFT")) {
				yourPlayer.setLocation(yourPlayer.getX()-1, yourPlayer.getY());
			} else if (payload.equals("RIGHT")) {
				yourPlayer.setLocation(yourPlayer.getX()+1, yourPlayer.getY());
			}
			String playerMoved = "PLAYER_MOVED:" + yourPlayer.getId() + "," + 
												   yourPlayer.getX() + "," + 
												   yourPlayer.getY();
			for (Socket otherSocket : map.getPlayers().keySet()) {
				// Send you all other players on the map
				
				if (players.get(otherSocket).getId() != yourPlayer.getId()) {
					try {
						new PrintWriter(otherSocket.getOutputStream(), true).println(playerMoved);
					} catch (Exception ex) {
						logger.error(ex);
					}
				}
				
			}
			
		}
	}

}
