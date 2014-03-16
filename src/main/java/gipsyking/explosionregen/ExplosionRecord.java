package gipsyking.explosionregen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class ExplosionRecord extends ErRunnable {

	private static int OBISIDIAN_CUBE_RADIUS = 1;
	private static Comparator<BlockRecord> heightSorterLowFirst = new Comparator<BlockRecord>() {

		@Override
		public int compare(BlockRecord o1, BlockRecord o2) {
			return o1.getY() - o2.getY();
		}
	};
	
	private static Comparator<BlockRecord> heightSorterHighFirst = new Comparator<BlockRecord>() {

		@Override
		public int compare(BlockRecord o1, BlockRecord o2) {
			return o2.getY() - o1.getY();
		}
	};
	
	private ArrayList<BlockRecord> recordList;
	private TaskList taskList;
	
	public ExplosionRecord(TaskList taskList, boolean ignoreContainers, List<Block> blockList, Location location, int minutes,
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
			
			recordList = new ArrayList<BlockRecord>();
			for (Block block: blockList) {
				if ((ignoreContainers && block.getState() instanceof InventoryHolder) || dropList.contains(block.getType().ordinal()) ) {
					drop(block);
					
				} else {
					recordList.add(new BlockRecord(block));
				}
			}
			
			Collections.sort(recordList, ExplosionRecord.heightSorterHighFirst);
			
			for (BlockRecord block: recordList) {
				// try to never drop accidentally
				block.getBlockAt().setType(Material.AIR);
			}
			
			if (recordList.size() > 0) {
				taskList.add(this, minutes * 1200 + 20);
				taskList.runUnrecoverable(new ExplosionRegenEffect(blockList.get(0).getWorld(), blockList.get(0).getLocation()), minutes * 1200);
			}
		}
	}
	
	/*private void dropSkull(Block block) {
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
	}*/


	@Override
	public void run() {
		taskList.remove(this);
		runWithoutSchedule();
	}
	
	@Override
	public void runWithoutSchedule() {
		
		Collections.sort(recordList, ExplosionRecord.heightSorterLowFirst);
		
		for(int i = recordList.size() - 1; i >= 0; i--) {
			recordList.get(i).reset(false);
		}
		
		for(int i = recordList.size() - 1; i >= 0; i--) {
			recordList.get(i).reset(true);
		}
	}

	private void drop(Block block) {
		for(ItemStack drop: block.getDrops()) {
			block.getWorld().dropItemNaturally(block.getLocation(), drop);
		}
		
		block.setType(Material.AIR);
	}
}
