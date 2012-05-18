package com.openorpg.simpleorpg.server.net;

import java.io.PrintWriter;
import java.net.Socket;

import com.openorpg.simpleorpg.server.Player;

public class SayHandler extends MessageHandler {
	
	private String payload;
	public SayHandler(String payload) {
		this.payload = payload;
	}

	@Override
	public void handleMessage(Socket socket) {
		synchronized(this) {
			Player yourPlayer = players.get(socket);
			if (payload.length() > 50) { 
				payload = payload.substring(0, 50) + "..."; 
			}
			String message = "SAY:" + yourPlayer.getId() + "," + payload;
			String youMessage = "SAY:YOU," + payload;
			logger.info(payload.length());
			logger.info(message);
			for (Socket playerSocket : players.keySet()) {
				try {
					PrintWriter playerOut = new PrintWriter(playerSocket.getOutputStream(), true);
					if (players.get(playerSocket).getId() != yourPlayer.getId()) {
						playerOut.println(message);
					} else {
						playerOut.println(youMessage);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
			}
			
		}
	}

}
