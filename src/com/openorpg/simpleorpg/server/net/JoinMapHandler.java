package com.openorpg.simpleorpg.server.net;

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
					// Send you to all other players on the map
					otherJoinedMap += "PLAYER_JOINED_MAP:" + player.getId() + "," + 
														  	 player.getName() + "," + 
														     player.getRef() + "," + 
														     player.getX() + "," + 
														     player.getY() + "\n";
					
					sendTo(otherSocket, youJoinedMap);
				}

				// Add you to the map
				map.getPlayers().put(socket, yourPlayer);
				
				// Send you all other players on the map
				sendTo(socket, otherJoinedMap);
				
				// Send you the warp message
				String warpMessage = "WARP:" + yourPlayer.getMapRef() + "," + yourPlayer.getX() + "," + yourPlayer.getY();
				sendTo(socket, warpMessage);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
