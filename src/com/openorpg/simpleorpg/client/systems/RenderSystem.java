package com.openorpg.simpleorpg.client.systems;
import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.openorpg.simpleorpg.client.components.ColorComponent;
import com.openorpg.simpleorpg.client.components.DrawableText;
import com.openorpg.simpleorpg.client.components.Fade;
import com.openorpg.simpleorpg.client.components.Location;
import com.openorpg.simpleorpg.client.components.Networking;
import com.openorpg.simpleorpg.client.components.ResourceRef;
import com.openorpg.simpleorpg.client.components.Say;
import com.openorpg.simpleorpg.client.components.Timer;
import com.openorpg.simpleorpg.managers.ResourceManager;

public class RenderSystem extends BaseEntitySystem {
	private ComponentMapper<ResourceRef> resourceRefMapper;
	private ComponentMapper<Location> locationMapper;
	private GameContainer container;
	private ComponentMapper<ColorComponent> colorMapper;
	private ComponentMapper<Networking> networkingMapper;
	private ComponentMapper<DrawableText> drawableTextMapper;
	private ComponentMapper<Timer> timerMapper;
	private ComponentMapper<Say> sayMapper;
	private ComponentMapper<Fade> fadeMapper;
	private TrueTypeFont broadcastFont;
	private TrueTypeFont nameFont;
	private TrueTypeFont inputFont;
	private TrueTypeFont saysFont;

	@SuppressWarnings("unchecked")
	public RenderSystem(GameContainer container) {
		super(ResourceRef.class, Location.class);
		this.container = container;
	}

	@Override
	protected void initialize() {
		resourceRefMapper = new ComponentMapper<ResourceRef>(ResourceRef.class, world);
		drawableTextMapper = new ComponentMapper<DrawableText>(DrawableText.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		colorMapper = new ComponentMapper<ColorComponent>(ColorComponent.class, world);
		networkingMapper = new ComponentMapper<Networking>(Networking.class, world);
		timerMapper = new ComponentMapper<Timer>(Timer.class, world);
		sayMapper = new ComponentMapper<Say>(Say.class, world);
		fadeMapper = new ComponentMapper<Fade>(Fade.class, world);
		broadcastFont = new TrueTypeFont(new java.awt.Font("Verdana", Font.BOLD, 14), false);
		saysFont = new TrueTypeFont(new java.awt.Font("Verdana", Font.BOLD, 12), false);
		nameFont = new TrueTypeFont(new java.awt.Font("Verdana", Font.PLAIN, 12), false);
		inputFont = new TrueTypeFont(new java.awt.Font("Verdana", Font.PLAIN, 12), false);
	}


	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Graphics graphics = container.getGraphics();
		ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
		ImmutableBag<Entity> players = world.getGroupManager().getEntities("PLAYER");
		ImmutableBag<Entity> broadcasts = world.getGroupManager().getEntities("BROADCAST");
		ImmutableBag<Entity> says = world.getGroupManager().getEntities("SAY");
		ResourceManager manager = ResourceManager.getInstance();
		int tw = 32, th = 32;
		
		Entity yourPlayer = players.get(0);
		Networking networking = networkingMapper.get(yourPlayer);
		if (!networking.isConnected()) {
			String connectingString = "Connecting to " + networking.getIp() + ":" + networking.getPort() + "...";
			int cw = graphics.getFont().getWidth(connectingString);
			int ch = graphics.getFont().getHeight(connectingString);
			graphics.getFont().drawString(container.getWidth()/2 - cw/2, container.getHeight()/2 - ch/2, connectingString, Color.white);
		} else {
			// Render the ground & background
			for (int i=0; i<maps.size(); i++) {
				Entity mapEntity = maps.get(i);
				String ref = resourceRefMapper.get(mapEntity).getResourceName();
				TiledMap tiledMap = (TiledMap)manager.getResource(ref).getObject();
				tiledMap.render(0, 0, 0);
				tiledMap.render(0, 0, 1);
				tw = tiledMap.getTileWidth();
				th = tiledMap.getTileHeight();
			}
			
			// Render players
			for (int i=0; i<players.size(); i++) {
				Entity playerEntity = players.get(i);
				if (resourceRefMapper.get(playerEntity) != null) {
					String ref = resourceRefMapper.get(playerEntity).getResourceName();
					Image playerImage = (Image)manager.getResource(ref).getObject();
					Location playerLocation = locationMapper.get(playerEntity);
					if (playerLocation != null && playerImage != null) {
						Vector2f playerPosition = playerLocation.getPosition();
						graphics.drawImage(playerImage, playerPosition.x * tw, playerPosition.y * th);
					}
				}
			}
			
			// Render the foreground
			for (int i=0; i<maps.size(); i++) {
				Entity mapEntity = maps.get(i);
				String ref = resourceRefMapper.get(mapEntity).getResourceName();
				TiledMap tiledMap = (TiledMap)manager.getResource(ref).getObject();
				tiledMap.render(0, 0, 2);
			}
			

			
			// Render player names and say text
			for (int i=0; i<players.size(); i++) {
				Entity playerEntity = players.get(i);
				DrawableText drawableText = drawableTextMapper.get(playerEntity);
				Location playerLocation = locationMapper.get(playerEntity);
				if (playerLocation != null && drawableText != null) {
					String playerName = drawableTextMapper.get(playerEntity).getText();
					Vector2f playerPosition = playerLocation.getPosition();
					int h = nameFont.getLineHeight();
					int w = nameFont.getWidth(playerName);
					nameFont.drawString(playerPosition.x*tw - w/2 + tw/2, playerPosition.y*th - h, playerName, Color.white);
					
					if (sayMapper.get(playerEntity) != null) {
						Say playerSay = sayMapper.get(playerEntity);
					    w = saysFont.getWidth(playerSay.getText());
						h = saysFont.getLineHeight();
						
						graphics.setColor(new Color(0,0,0,100));
						graphics.fillRoundRect(playerPosition.x*tw - w/2 + tw/2 - 3,  playerPosition.y*th - (int)(h*2.5) - 3, w + 6, h + 6, 5);
						graphics.setColor(new Color(255,255,255,100));
						saysFont.drawString(playerPosition.x*tw - w/2 + tw/2, playerPosition.y*th - (int)(h*2.5), playerSay.getText());
						
						if (playerSay.isFinished()) {
							playerEntity.removeComponent(playerSay);
							playerEntity.refresh();
						}
					}
				}
			}
			
			// Render the fading effect
			for (int i=0; i<maps.size(); i++) {
				Entity mapEntity = maps.get(i);
				if (fadeMapper.get(mapEntity) != null) {
					Fade fade = fadeMapper.get(mapEntity);
					int alpha = fade.getAlpha();
					graphics.setColor(new Color(0,0,0,alpha));
					graphics.fillRect(0, 0, container.getWidth(), container.getHeight());
					fade.tick();
					if (alpha <= 0) {
						mapEntity.removeComponent(fadeMapper.get(mapEntity));
					}
				}
			}

			
			// Render broadcast text
			for (int i=0; i<broadcasts.size(); i++) {
				Entity broadcastEntity = broadcasts.get(i);
				Color color = colorMapper.get(broadcastEntity).getColor();
	
				String message = drawableTextMapper.get(broadcastEntity).getText();
				int h = broadcastFont.getLineHeight();
				int w = broadcastFont.getWidth(message);
				broadcastFont.drawString((int)container.getWidth()-w-20, (int)h*i + h, message, color);
				if (timerMapper.get(broadcastEntity) != null) {
					if (timerMapper.get(broadcastEntity).isFinished()) {
						broadcastEntity.delete();
					}
				}
			}
			
			// Render input text
			if (world.getTagManager().getEntity("INPUT") != null) {
				graphics.setColor(new Color(0,0,0,100));
				graphics.fillRect(0, container.getHeight()-20, container.getWidth(), 20);
				graphics.setColor(new Color(255,255,255));
				String message = drawableTextMapper.get(world.getTagManager().getEntity("INPUT")).getText();
				message = "Say: " + message;
				graphics.drawString(message,5, container.getHeight()-inputFont.getLineHeight()-3);
			}
		}
		
	}
}
