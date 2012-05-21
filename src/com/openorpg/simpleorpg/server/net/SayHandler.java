package com.openorpg.simpleorpg.server.net;

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
			String sayMessage = "SAY:" + yourPlayer.getId() + "," + payload;
			String youSayMessage = "SAY:YOU," + payload;
			logger.info(payload.length());
			logger.info(sayMessage);
			try {
				sendTo(socket, youSayMessage);
			} catch (Exception ex) {
				logger.error(ex);
			}
			sendAllMapBut(socket, sayMessage);
		}
	}

}
