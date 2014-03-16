package gipsyking.explosionregen;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;


public class BlockRecord {

	private BlockState state;
	private World world;
	private Location location;
	private BlockState newState;
	
	private static ArrayList<Material> dependantBlockTypes = new ArrayList<Material>(Arrays.asList(new Material[]{
			Material.ACTIVATOR_RAIL, Material.BED_BLOCK, Material.BREWING_STAND, Material.BROWN_MUSHROOM,
			Material.CACTUS, Material.CAKE_BLOCK, Material.CARPET, Material.CARROT, Material.CROPS, Material.DEAD_BUSH,
			Material.DETECTOR_RAIL, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.FLOWER_POT,
			Material.IRON_DOOR, Material.IRON_DOOR_BLOCK, Material.LEVER, Material.LONG_GRASS, Material.MELON_STEM,
			Material.NETHER_WARTS, Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.POTATO,
			Material.POWERED_RAIL, Material.PUMPKIN_STEM, Material.RAILS, Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON, Material.REDSTONE_WIRE,
			Material.RED_ROSE, Material.RED_MUSHROOM, Material.SAPLING,
			Material.SIGN, Material.SIGN_POST, Material.SKULL, Material.STONE_BUTTON, Material.STONE_PLATE,
			Material.SUGAR_CANE_BLOCK,
			Material.TORCH, Material.TRAP_DOOR, Material.TRIPWIRE, Material.TRIPWIRE_HOOK, Material.VINE,
			Material.WATER_LILY, Material.WOOD_BUTTON, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.WOOD_PLATE,
			Material.YELLOW_FLOWER,
			
	}));

	public BlockRecord(Block block) {
		this.world = block.getWorld();
		this.location = block.getLocation();
		this.state = block.getState();
		this.newState = null;
	}
	
	public int getY() {
		return state.getY();
	}

	public void reset(boolean force) {
		if (force ^ dependantBlockTypes.contains(state.getType())) {
			return;
		}
		
		Block currentBlock = world.getBlockAt(location);
		
		if (currentBlock.getType() == Material.ANVIL) {
			Block blockAbove = currentBlock.getRelative(BlockFace.UP);
			while (blockAbove.getType() != Material.AIR && blockAbove.getY() < 255) {
				blockAbove = blockAbove.getRelative(BlockFace.UP);
			}
			blockAbove.setType(Material.ANVIL);
			BlockState anvilState = blockAbove.getState();
			anvilState.setData(currentBlock.getState().getData());
			anvilState.update(true);
		}
		
		currentBlock.setType(state.getType());
		newState = currentBlock.getState();
		
		newState.setType(state.getType());
		
		setData(newState);
		
		newState.update(true);
	}

	protected void setData(BlockState currentState) {
		if (BlockStateHandlerConfiguration.recordHandlers.containsKey(state.getType())) {
			BlockStateHandlerConfiguration.recordHandlers.get(state.getType()).setData(currentState, state);
			
		} else {
			currentState.setData(state.getData());
		}
		
	}

	public Block getBlockAt() {
		return this.world.getBlockAt(this.location);
	}

}
