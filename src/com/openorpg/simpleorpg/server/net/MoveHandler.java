package com.openorpg.simpleorpg.server.net;

import java.net.Socket;

import org.newdawn.slick.tiled.TiledMap;

import com.openorpg.simpleorpg.managers.ResourceManager;
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
			
			// Check for collisions
			ResourceManager manager = ResourceManager.getInstance();
			TiledMap tiledMap = (TiledMap)manager.getResource(yourPlayer.getMapRef(), true).getObject();
			
			// Bounds check
			if (newY < tiledMap.getHeight() && newX < tiledMap.getWidth() &&
				newY >= 0 && newX >= 0) {
				// Collision layer check
				if (tiledMap.getTileId(newX, newY, 3) == 0) {
			
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
			// if you failed the bounds check we might warp you to the next map
			} else {
				// Right
				String warpTo = "";
				if (newX >= tiledMap.getWidth()) {
					warpTo = tiledMap.getMapProperty("Right", "");
					newX = 0;
				}
				
				// Left
				if (newX <= -1) {
					warpTo = tiledMap.getMapProperty("Left", "");
					newX = tiledMap.getWidth()-1;
				}
				
				// Up
				if (newY <= -1) {
					warpTo = tiledMap.getMapProperty("Up", "");
					newY = tiledMap.getHeight()-1;
				}
				
				// Down
				if (newY >= tiledMap.getHeight()) {
					warpTo = tiledMap.getMapProperty("Down", "");
					newY = 0;
				}
				
				if (!warpTo.equals("")) {
					// Leave the current map
					MessageHandler leaveMapHandler = new LeaveMapHandler();
					leaveMapHandler.handleMessage(socket);
					
					yourPlayer.setLocation(newX, newY);
					yourPlayer.setMapRef(warpTo);
					
					// Join the new map
					MessageHandler joinMapHandler = new JoinMapHandler();
					joinMapHandler.handleMessage(socket);
				}
			}
		}
	}

}
