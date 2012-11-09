package com.yermocraft.Gipsy;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPainting;
import net.minecraft.server.EnumArt;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Painting;



public class HangingRecord implements Runnable {

	private World world;
	private Location location;
	private EntityType type;
	private BlockFace face;
	private int data;
	

	public HangingRecord(ExplosionRegen plugin, Hanging entity, int delay) {
		this.world = entity.getWorld();
		this.location = entity.getLocation().clone();
		this.type = entity.getType();
		this.face = entity.getAttachedFace();
		
		if (entity instanceof Painting) {
			this.data = ((Painting) entity).getArt().getId();
		}
		
		entity.remove();

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
	}

	@Override
	public void run() {
		CraftWorld cWorld = (CraftWorld) world;
		Entity entity = null;
		int dir = getFace(this.face);
		
		
		if (type == EntityType.PAINTING) {
			location = location.getBlock().getRelative(face.getOppositeFace()).getLocation();
			entity = new EntityPainting(cWorld.getHandle(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), dir);
			((EntityPainting)entity).art = EnumArt.values()[data];
		}
		if (entity != null) {
			cWorld.getHandle().addEntity(entity);
		}
	}

	private int getFace(BlockFace face) {
		switch(face) {
			case WEST: return 0;
			case SOUTH: return 1;
			case EAST: return 2;
			case NORTH: return 3;
			default: return -1;
		}
	}

}
