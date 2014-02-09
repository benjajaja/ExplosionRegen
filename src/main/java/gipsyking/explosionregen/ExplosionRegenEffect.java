package gipsyking.explosionregen;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;

public class ExplosionRegenEffect implements Runnable {

	private World world;
	private Location location;

	public ExplosionRegenEffect(World world, Location location) {
		this.world = world;
		this.location = location.add(0, 2, 0);
	}

	@Override
	public void run() {
		world.playEffect(location, Effect.ENDER_SIGNAL, 5);
		world.playEffect(location, Effect.ZOMBIE_DESTROY_DOOR, 1);
	}

}
