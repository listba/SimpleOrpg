package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Map;
import com.openorpg.simpleorpg.server.Player;

public class JoinMapHandler extends MessageHandler {
	
	public JoinMapHandler() {
	}

	@Override
	public void handleMessage(Socket socket) {
		try {
			synchronized(this) {
				Player yourPlayer = players.get(socket);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				String warpMessage = "WARP:" + yourPlayer.getMapRef() + "," + yourPlayer.getX() + "," + yourPlayer.getY();
				out.println(warpMessage);
				Map map = maps.get(yourPlayer.getMapRef());		
				
				// Send all other players on the map your player
				String youJoinedMap = "PLAYER_JOINED_MAP:" + yourPlayer.getId() + "," + 
															 yourPlayer.getName() + "," + 
															 yourPlayer.getRef() + "," + 
															 yourPlayer.getX() + "," + 
															 yourPlayer.getY();
				
				String otherJoinedMap = "";
				for (Socket otherSocket : map.getPlayers().keySet()) {
					Player player = map.getPlayers().get(otherSocket);
					// Send you all other players on the map
					otherJoinedMap += "PLAYER_JOINED_MAP:" + player.getId() + "," + 
														  	 player.getName() + "," + 
														     player.getRef() + "," + 
														     player.getX() + "," + 
														     player.getY() + "\n";
					
					new PrintWriter(otherSocket.getOutputStream(), true).println(youJoinedMap);
				}
				out.println(otherJoinedMap);
				
				// Add the player to the map
				map.getPlayers().put(socket, yourPlayer);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
