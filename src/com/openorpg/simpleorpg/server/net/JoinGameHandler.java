package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
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
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println("SET_REF:YOU," + yourPlayer.getRef());
			out.println("SET_NAME:YOU," + yourPlayer.getName());
			synchronized(this) {
				// Add the player to the game
				players.put(socket, yourPlayer);
				
				for (Socket playerSocket : players.keySet()) {
					PrintWriter playerOut = new PrintWriter(playerSocket.getOutputStream(), true);
					playerOut.println("BROADCAST:#FFCC11," + yourPlayer.getName() + " has joined the game!");
				}
			}
			MessageHandler joinMapHandler = new JoinMapHandler();
			joinMapHandler.handleMessage(socket);

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
