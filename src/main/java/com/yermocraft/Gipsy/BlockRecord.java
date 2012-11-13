package com.yermocraft.Gipsy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;



import net.minecraft.server.EntityItem;
import net.minecraft.server.TileEntity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.SimpleAttachableMaterialData;
import org.bukkit.util.Vector;
import org.shininet.bukkit.playerheads.Skull;

public class BlockRecord extends ErRunnable {

	private static int OBISIDIAN_CUBE_RADIUS = 1;
	private ArrayList<BlockState> list;
	private TaskList taskList;
	
	public BlockRecord(TaskList taskList, boolean ignoreContainers, List<Block> blockList, Location location, int minutes, boolean dropSkulls,
			boolean breakObsidian, List<Integer> dropList) {
		this.taskList = taskList;

		// add surrounding block for obsidian
		for (int x = -OBISIDIAN_CUBE_RADIUS; x <= OBISIDIAN_CUBE_RADIUS; x++) {
			for (int y = -OBISIDIAN_CUBE_RADIUS; y <= OBISIDIAN_CUBE_RADIUS; y++) {
				for (int z = -OBISIDIAN_CUBE_RADIUS; z <= OBISIDIAN_CUBE_RADIUS; z++) {
					Block block = location.clone().add(new Vector(x, y, z)).getBlock();
					if (block.getType() == Material.OBSIDIAN) {
						blockList.add(block);
					}
				}
			}
		}
		
		if (blockList.size() != 0) {
			
			list = new ArrayList<BlockState>();
			for (Block block: blockList) {
				if ((ignoreContainers && block.getState() instanceof InventoryHolder) || dropList.contains(block.getTypeId()) ) {
					drop(block);
					
				} else if (dropSkulls && block.getType() == Material.SKULL) {
					dropSkull(block);
					
				} else {
					record(block);
					
				}
			}
			
			if (list.size() > 0) {
				taskList.add(this, minutes * 1200 + 20);
				taskList.run(new ExplosionRegenEffect(list.get(0).getWorld(), list.get(0).getLocation()), minutes * 1200);
			}
		}
	}
	
	private void dropSkull(Block block) {
		Random prng = new Random();
		
		CraftWorld world = ((CraftWorld)block.getWorld());
		Location location = block.getLocation();
		TileEntity tile = world.getTileEntityAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		Skull skull = new Skull(tile);

		double xs = block.getLocation().getX() + (prng.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D);
		double ys = block.getLocation().getY() + (prng.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D);
		double zs = block.getLocation().getZ() + (prng.nextFloat() * 0.7F + (1.0F - 0.7F) * 0.5D);

		EntityItem entity = new EntityItem(world.getHandle(), xs, ys, zs, skull.getItemStack().getHandle());
		entity.pickupDelay = 10;
		world.getHandle().addEntity(entity);
	}

	/**
	 * Items not correctly recorded:
	 * skulls (direction), paintings
	 * @param block
	 */
	private void record(Block block) {
		list.add(block.getState());
		cleanUp(block.getState(), block);
	}

	private void cleanUp(BlockState state, Block block) {
		if (state instanceof FlowerPot || state instanceof Sign
				|| state.getType().getData().getClass().equals(SimpleAttachableMaterialData.class)) {
			block.setTypeIdAndData(0, (byte)0, false);
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
			list.get(i).update(true);
		}
	}

	private void drop(Block block) {
		Collection<ItemStack> drops = block.getDrops();
		if (drops.size() > 0) {
			block.getWorld().dropItemNaturally(block.getLocation(), drops.iterator().next());
		}
		
	}

	

	

	
}
