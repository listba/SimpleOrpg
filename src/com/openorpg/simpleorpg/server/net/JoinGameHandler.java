package com.openorpg.simpleorpg.server.net;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Player;

public class JoinGameHandler extends MessageHandler {
	
	public JoinGameHandler() {
	}

	@Override
	public void handleMessage(Socket socket) {
		// Load the player from the database
		String playerName = socket.getInetAddress().getHostAddress();
		Player yourPlayer = new Player(playerName, 
								   	   "knightImage", 
								   	   "testmap");
		if ((int)(Math.random()*2) == 0) yourPlayer.setRef("mageImage");
		yourPlayer.setLocation(players.size()*2+5, 5-players.size());
		
		try {
			sendTo(socket, "SET_REF:YOU," + yourPlayer.getRef());
			sendTo(socket, "SET_NAME:YOU," + yourPlayer.getName());
			synchronized(this) {
				// Add the player to the game
				players.put(socket, yourPlayer);
				String joinGameMessage = "BROADCAST:#FFCC11," + yourPlayer.getName() + " has joined the game!";
				sendAll(joinGameMessage);
			}
			MessageHandler joinMapHandler = new JoinMapHandler();
			joinMapHandler.handleMessage(socket);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
