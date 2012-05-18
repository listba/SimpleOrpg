package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Map;
import com.openorpg.simpleorpg.server.Player;

public class LeaveMapHandler extends MessageHandler {

	@Override
	public void handleMessage(Socket socket) {
		try {			
			// I think this is better than using synchronized collections
			synchronized(this) {
				Player yourPlayer = players.get(socket);
				Map map = maps.get(yourPlayer.getMapRef());
				// Remove you from the map
				map.getPlayers().remove(socket);
				
				// Send all other players on the map your player
				String youLeftMap = "PLAYER_LEFT_MAP:" + yourPlayer.getId();
				
				for (Socket otherSocket : map.getPlayers().keySet()) {
					// Send you all other players on the map
					new PrintWriter(otherSocket.getOutputStream(), true).println(youLeftMap);					
				}
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
