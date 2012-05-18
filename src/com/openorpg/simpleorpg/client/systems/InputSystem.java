package com.openorpg.simpleorpg.client.systems;
import java.awt.Font;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.openorpg.simpleorpg.client.components.ColorComponent;
import com.openorpg.simpleorpg.client.components.DrawableText;
import com.openorpg.simpleorpg.client.components.Location;
import com.openorpg.simpleorpg.client.components.Movement;
import com.openorpg.simpleorpg.client.components.Networking;
import com.openorpg.simpleorpg.client.components.ResourceRef;
import com.openorpg.simpleorpg.client.components.Timer;
import com.openorpg.simpleorpg.client.managers.ResourceManager;

public class InputSystem extends BaseEntitySystem implements KeyListener {
	private GameContainer container;
	private ComponentMapper<DrawableText> drawableTextMapper;
	private ComponentMapper<Timer> timerMapper;
	private ComponentMapper<Networking> networkingMapper;
	private ComponentMapper<Location> locationMapper;
	private TrueTypeFont inputFont;
	private Character c = null;
	private ComponentMapper<Movement> movementMapper;
	
	private boolean key_back = false,
					key_enter = false,
					key_up = false,
					key_down = false,
					key_left = false,
					key_right = false;

	@SuppressWarnings("unchecked")
	public InputSystem(GameContainer container) {
		super(ResourceRef.class, Location.class);
		this.container = container;
	}

	@Override
	protected void initialize() {
		drawableTextMapper = new ComponentMapper<DrawableText>(DrawableText.class, world);
		timerMapper = new ComponentMapper<Timer>(Timer.class, world);
		networkingMapper = new ComponentMapper<Networking>(Networking.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
		movementMapper = new ComponentMapper<Movement>(Movement.class, world);
		inputFont = new TrueTypeFont(new java.awt.Font("Verdana", Font.PLAIN, 12), false);
		container.getInput().addKeyListener(this);
	}


	@Override
	protected boolean checkProcessing() {
		return true;
	}

	@Override
	protected void processEntities(ImmutableBag<Entity> entities) {
		Entity inputEntity = null;
		Entity yourEntity = world.getTagManager().getEntity("YOU");
		Location yourLocation = locationMapper.get(yourEntity);
		Networking net = networkingMapper.get(yourEntity);
		ArrayBlockingQueue<String> sendMessages = net.getSendMessages();
		
		// Alphanumeric input interface (when you press a key chat shows up)
		if (world.getTagManager().getEntity("INPUT") != null) {
			inputEntity = world.getTagManager().getEntity("INPUT");
		}
		
		if (this.c != null) {
			if (inputEntity == null) {
				inputEntity = world.createEntity();
				inputEntity.setTag("INPUT");
				inputEntity.addComponent(new DrawableText(c.toString()));
				inputEntity.addComponent(new Timer(25));
				inputEntity.refresh();
			} else {
				DrawableText drawableText = drawableTextMapper.get(inputEntity);
				if (drawableText.getText().length() < 50)
					drawableText.setText(drawableText.getText() + c);
			}
		}
		// You can't hold down a letter for input
		c = null;
		
		// Backspace
		if (key_back && inputEntity != null) {
			Timer timer = timerMapper.get(inputEntity);
			if (timer.isFinished()) {
				timer.reset();
				String txt = drawableTextMapper.get(inputEntity).getText();
				drawableTextMapper.get(inputEntity).setText(txt.substring(0, txt.length()-1));
				if (txt.length()-1 == 0) inputEntity.delete();
			}
		}
		
		// Enter
		if (key_enter && inputEntity != null) {
			DrawableText drawableText = drawableTextMapper.get(inputEntity);
			
			sendMessages.add("SAY:" + drawableText.getText());
			inputEntity.delete();
		}
		
		
		// Your movement
		if (yourLocation != null) {
			int newX = (int)yourLocation.getPosition().x, newY = (int)yourLocation.getPosition().y;
			String moveMessage = "";
			
			if (key_up) {
				moveMessage = "MOVE:UP";
				newY -= 1;
			} else if (key_down) {
				moveMessage = "MOVE:DOWN";
				newY += 1;
			} else if (key_left) {
				moveMessage = "MOVE:LEFT";
				newX -= 1;
			} else if (key_right) {
				moveMessage = "MOVE:RIGHT";
				newX += 1;
			}
			
			if (key_up || key_down || key_left || key_right) {
				Movement yourMovement;
				if (movementMapper.get(yourEntity) != null) {
					yourMovement = movementMapper.get(yourEntity);
				} else {
					yourMovement = new Movement(100);
					yourEntity.addComponent(yourMovement);
					yourEntity.refresh();
				}
				
				if (yourMovement.isFinished()) {
					sendMessages.add(moveMessage);
					yourLocation.getPosition().set(newX, newY);
					yourMovement.reset();
				}
			}
		}

		
	}

	@Override
	public void keyPressed(int key, char c) {
		
		logger.info((int)c + " " + key);
		if (c > 31 && c < 127) {
			this.c = c;
		}
		
		switch (key) {
			case Input.KEY_LEFT:
				key_left = true;
				break;
			case Input.KEY_RIGHT:
				key_right = true;
				break;
			case Input.KEY_UP:
				key_up = true;
				break;
			case Input.KEY_DOWN:
				key_down = true;
				break;
			case Input.KEY_DELETE:
			case Input.KEY_BACK:
				key_back = true;
				break;
			case Input.KEY_ENTER:
				key_enter = true;
				break;
			case Input.KEY_CAPITAL:
			case Input.KEY_INSERT:
			case Input.KEY_LSHIFT:
			case Input.KEY_RSHIFT:
			case Input.KEY_LCONTROL:
			case Input.KEY_RCONTROL:
			case Input.KEY_LALT:
			case Input.KEY_RALT:
				break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		switch (key) {
			case Input.KEY_LEFT:
				key_left = false;
				break;
			case Input.KEY_RIGHT:
				key_right = false;
				break;
			case Input.KEY_UP:
				key_up = false;
				break;
			case Input.KEY_DOWN:
				key_down = false;
				break;
			case Input.KEY_DELETE:
			case Input.KEY_BACK:
				key_back = false;
				break;
			case Input.KEY_ENTER:
				key_enter = false;
				break;
		}
		
	}
	@Override
	public void setInput(Input input) {		
	}
	
	@Override
	public boolean isAcceptingInput() {
		return true;
	}
	
	@Override
	public void inputEnded() {		
	}
	
	@Override
	public void inputStarted() {
		
	}
}
