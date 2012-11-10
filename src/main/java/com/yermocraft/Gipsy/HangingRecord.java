package com.yermocraft.Gipsy;

import net.minecraft.server.EntityPainting;
import net.minecraft.server.EnumArt;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;



public class HangingRecord extends ErRunnable {

	private World world;
	private Location location;
	private EntityType type;
	private BlockFace face;
	private int data;
	private TaskList taskList;

	public HangingRecord(TaskList taskList, Hanging entity, int minutes) {
		this.taskList = taskList;
		this.world = entity.getWorld();
		this.location = entity.getLocation().clone();
		this.type = entity.getType();
		this.face = entity.getAttachedFace();
		
		if (entity instanceof Painting) {
			this.data = ((Painting) entity).getArt().getId();
			entity.remove();
			taskList.add(this, minutes * 1200 + 30);
			
		} else if (entity instanceof ItemFrame) {
			ItemStack stack = ((ItemFrame) entity).getItem();
			entity.remove();
			location.getWorld().dropItemNaturally(location, stack);
			location.getWorld().dropItemNaturally(location, new ItemStack(Material.ITEM_FRAME));
		} else {
			entity.remove();
		}
	}

	@Override
	public void run() {
		taskList.remove(this);
		runWithoutSchedule();
		
	}

	@Override
	public void runWithoutSchedule() {
		CraftWorld cWorld = (CraftWorld) world;
		
		int dir = getFace(this.face);
		
		
		if (type == EntityType.PAINTING) {
			getPaintingLocation(Art.values()[this.data], location, face);
			
			EntityPainting entity = new EntityPainting(cWorld.getHandle(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), dir);
			entity.art = EnumArt.values()[data];
			cWorld.getHandle().addEntity(entity);
		}
		
	}

	private void getPaintingLocation(Art art, Location loc, BlockFace face) {
		fixHangingDistance(loc);
		
		// fix position on wall
		if (art.getBlockHeight() == 2) {
			loc.add(0, -1, 0);
		}
		
		if (loc.getYaw() == 180 && art.getBlockWidth() >= 2) { // West
			loc.add(-1, 0, 0);

		} else if (loc.getYaw() == 90 && art.getBlockWidth() >= 2) { // South
			loc.add(0, 0, -1);

		}
	}
	
	private void fixHangingDistance(Location loc) {
		// fix distance to wall
		if (face == BlockFace.WEST) {
			loc.add(0, 0, -1);
		} else if (face == BlockFace.SOUTH) {
			loc.add(1, 0, 0);
		} else if (face == BlockFace.EAST) {
			loc.add(0, 0, 1);
		} else if (face == BlockFace.NORTH) {
			loc.add(-1, 0, 0);
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
