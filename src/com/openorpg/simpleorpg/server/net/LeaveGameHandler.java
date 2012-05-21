package com.openorpg.simpleorpg.server.net;

import java.net.Socket;

import com.openorpg.simpleorpg.server.Player;

public class LeaveGameHandler extends MessageHandler {

	@Override
	public void handleMessage(Socket socket) {
		try {
			synchronized(this) {
				Player yourPlayer = players.get(socket);
				String leaveMessage = "BROADCAST:#FF0000," + yourPlayer.getName() + " has left the game!";
				sendAllMapBut(socket, leaveMessage);
				MessageHandler leaveMapHandler = new LeaveMapHandler();
				leaveMapHandler.handleMessage(socket);
				// Remove you from the game
				players.remove(socket);	
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error(ex);
		}
	}

}
