package com.openorpg.simpleorpg.server.net;

import java.net.Socket;
import java.util.Date;

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
			int newX = yourPlayer.getX(), newY = yourPlayer.getY();
			if (payload.equals("UP")) {
				newY -= 1;
			} else if (payload.equals("DOWN")) {
				newY += 1;
			} else if (payload.equals("LEFT")) {
				newX -= 1;
			} else if (payload.equals("RIGHT")) {
				newX += 1;
			}
			// Check to make sure the player isn't trying to move too fast
			//Long curTime = new Date().getTime();
			//if (curTime - yourPlayer.getLastMovedTime() > 25) {
				//yourPlayer.setLastMovedTime(curTime);
				yourPlayer.setLocation(newX, newY);
				
				// Send you all other players on the map
				String playerMoved = "PLAYER_MOVED:" + yourPlayer.getId() + "," + 
													   yourPlayer.getX() + "," + 
													   yourPlayer.getY();
				
				sendAllMapBut(socket, playerMoved);
			//} else {
			//	logger.info(socket.getInetAddress().getHostAddress() + " is trying to move too fast!");
			//}
			
		}
	}

}
