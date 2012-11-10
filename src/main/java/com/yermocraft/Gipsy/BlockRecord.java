package com.yermocraft.Gipsy;

import java.util.ArrayList;
import java.util.List;



import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.SimpleAttachableMaterialData;

public class BlockRecord extends ErRunnable {

	ArrayList<BlockState> list;
	private TaskList taskList;
	
	public BlockRecord(TaskList taskList, boolean ignoreContainers, List<Block> blockList, int minutes) {
		this.taskList = taskList;
		if (blockList.size() != 0) {
			
			
			list = new ArrayList<BlockState>();
			for (Block block: blockList) {
				if (ignoreContainers && block.getState() instanceof InventoryHolder) {
					drop(block);
				} else {
					record(block);
				}
			}
			
			if (list.size() > 0) {
				taskList.run(new RegenEffect(list.get(0).getWorld(), list.get(0).getLocation()), minutes * 1200);
				taskList.add(this, minutes * 1200 + 20);
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
		list.add(state);
		cleanUp(state, block);
	}

	private void cleanUp(BlockState state, Block block) {
		if (state instanceof FlowerPot || state instanceof Sign
				|| state.getType().getData().getClass().equals(SimpleAttachableMaterialData.class)) {
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
		taskList.remove(this);
		runWithoutSchedule();
	}
	
	@Override
	public void runWithoutSchedule() {
		for(int i = list.size() - 1; i >= 0; i--) {
			BlockState state = list.get(i);
			Block block = state.getWorld().getBlockAt(state.getX(), state.getY(), state.getZ());
			teleportEntitiesAway(block);
			if (block.getType() != Material.AIR) {
				block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(block.getType(), block.getData()));
			}
			state.update(true);
			
			BlockState newState = block.getState();
			if (newState instanceof Sign) {
				for(int lineIndex = 0; lineIndex < ((Sign) state).getLines().length; lineIndex++) {
					((Sign) newState).setLine(lineIndex, ((Sign) state).getLine(lineIndex));
				}
				newState.update();
			}
			/*block.setTypeIdAndData(state.getBlockState().getTypeId(), state.getBlockState().getRawData(), false);
			state.setExendedData(block);*/
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
		while (!isSafe(location) && location.getY() < 256) {
			location.add(0, 1, 0);
		}
		entity.teleport(location);
		
	}

	private boolean isSafe(Location location) {
		return location.getBlock().getType() == Material.AIR
				&& location.getBlock().getRelative(BlockFace.UP).getType() == Material.AIR;
	}

	
}
