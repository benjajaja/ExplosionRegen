package com.yermocraft.Gipsy;

import java.util.ArrayList;
import java.util.List;



import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.MaterialData;
import org.bukkit.material.SimpleAttachableMaterialData;
import org.bukkit.material.Skull;

public class ExplosionRecord implements Runnable {

	ArrayList<BlockState> list;
	private World world;
	private ExplosionRegen plugin;
	
	public ExplosionRecord(ExplosionRegen plugin, boolean ignoreContainers, List<Block> blockList, int delay) {
		if (blockList.size() != 0) {
			
			this.plugin = plugin;
			
			world = blockList.get(0).getWorld();
			list = new ArrayList<BlockState>();
			for (Block block: blockList) {
				if (ignoreContainers && block.getState() instanceof InventoryHolder) {
					drop(block);
				} else {
					record(block);
				}
			}
			
			if (list.size() > 0) {
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RegenEffect(list.get(0).getWorld(), list.get(0).getLocation()), delay);
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay + 20);
			}
		}
	}

	/**
	 * Items not correctly recorded:
	 * skulls (direction), paintings
	 * @param block
	 */
	private void record(Block block) {
		BlockState state = block.getState();
		MaterialData data = state.getData();
		if (data instanceof Skull) {
			plugin.debug("Skull: " + data.getData());
			
			
		} else {
			
		}
		list.add(state);
		cleanup(state, block);
	}

	private void cleanup(BlockState state, Block block) {
		/**
		 * Items still dropped: paintings
		 */
		if (state instanceof FlowerPot || block instanceof SimpleAttachableMaterialData) {
			block.setTypeIdAndData(0, (byte)0, false);
		}
		
		
	}

	private void drop(Block block) {
		Location location = block.getLocation();
		for(ItemStack drop: block.getDrops()) {
			block.getWorld().dropItemNaturally(location, drop);
		}
		Inventory inventory = ((InventoryHolder) block.getState()).getInventory();
		for(ItemStack stack: inventory.getContents()) {
			if (stack != null) {
				block.getWorld().dropItemNaturally(location, stack);
			}
		}
	}

	@Override
	public void run() {
		for(int i = list.size() - 1; i >= 0; i--) {
			BlockState state = list.get(i);
			Block block = world.getBlockAt(state.getX(), state.getY(), state.getZ());
			teleportEntitiesAway(block);
			block.setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
		}
		
	}

	private void teleportEntitiesAway(Block block) {
		for(Entity entity: block.getChunk().getEntities()) {
			if (entity instanceof LivingEntity) {
				Location location = entity.getLocation();
				if (isInside(location, block)) {
					teleportToSafety(entity, location);
				}
			}
		}
		
	}

	private boolean isInside(Location location, Block block) {
		return location.getBlockX() == block.getX()
				&& location.getBlockX() == block.getY()
				&& location.getBlockZ() == block.getZ();
	}

	private void teleportToSafety(Entity entity, Location location) {
		location.setY(location.getChunk().getChunkSnapshot().getHighestBlockYAt(location.getBlockX(), location.getBlockZ()));
		entity.teleport(location);
		
	}

	
}
