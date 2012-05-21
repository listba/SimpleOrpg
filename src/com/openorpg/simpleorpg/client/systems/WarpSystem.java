package com.openorpg.simpleorpg.client.systems;

import org.newdawn.slick.geom.Vector2f;

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.ImmutableBag;
import com.openorpg.simpleorpg.client.components.Fade;
import com.openorpg.simpleorpg.client.components.Location;
import com.openorpg.simpleorpg.client.components.ResourceRef;
import com.openorpg.simpleorpg.client.components.Warp;

public class WarpSystem extends BaseEntityProcessingSystem {
	private ComponentMapper<Warp> warpMapper;
	private ComponentMapper<Location> locationMapper;

	@SuppressWarnings("unchecked")
	public WarpSystem() {
		super(Warp.class);
	}

	@Override
	protected void initialize() {
		warpMapper = new ComponentMapper<Warp>(Warp.class, world);
		locationMapper = new ComponentMapper<Location>(Location.class, world);
	}

	@Override
	protected void process(Entity e) {
		Warp warp = warpMapper.get(e);
		if (warp != null) {
			logger.info("Warping to " + warp.getMapRef());
			ImmutableBag<Entity> maps = world.getGroupManager().getEntities("MAP");
			
			// Delete existing maps
			for (int i=0; i<maps.size(); i++) {
				Entity map = maps.get(i);
				world.deleteEntity(map);
				map.refresh();
			}
			
			// Add the newly warped map
			Entity map = world.createEntity();
			map.setGroup("MAP");
			map.addComponent(new ResourceRef(warp.getMapRef()));
			map.addComponent(new Fade(8, false));
			map.refresh();
			
			Location playerLocation = locationMapper.get(e);
			if (playerLocation != null) {
				Vector2f playerPosition = playerLocation.getPosition();
				playerPosition.set(warp.getPosition());
			} else {
				e.addComponent(new Location(warp.getPosition()));
			}
			
			e.removeComponent(warp);
			
		}
	}
}
