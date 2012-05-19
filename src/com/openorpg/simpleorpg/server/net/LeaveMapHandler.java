package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Map;
import com.openorpg.simpleorpg.server.Player;

public class LeaveMapHandler extends MessageHandler {

	@Override
	public void handleMessage(Socket socket) {
		try {			
			synchronized(this) {
				Player yourPlayer = players.get(socket);
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				Map map = maps.get(yourPlayer.getMapRef());
				// Remove you from the map
				map.getPlayers().remove(socket);
				
				// Send all other players on the map your player
				String youLeftMap = "PLAYER_LEFT_MAP:" + yourPlayer.getId();
				String otherLeftMap = "";
				for (Socket otherSocket : map.getPlayers().keySet()) {
					Player player = map.getPlayers().get(otherSocket);
					// Send you all other players on the map
					otherLeftMap += "PLAYER_LEFT_MAP:" + player.getId() + "\n";

					new PrintWriter(otherSocket.getOutputStream(), true).println(youLeftMap);
				}
				
				out.println(otherLeftMap);
			}

		} catch (Exception ex) {
			logger.error(ex);
		}
	}

}
