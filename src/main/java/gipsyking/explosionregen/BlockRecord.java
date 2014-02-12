package gipsyking.explosionregen;

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

	public BlockRecord(Block block) {
		this.world = block.getWorld();
		this.location = block.getLocation();
		this.state = block.getState();
		this.newState = null;
	}

	public void reset() {
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
	}
	
	public void update() {
		newState.update(true);
	}

	protected void setData(BlockState currentState) {
		if (BlockStateHandlerConfiguration.recordHandlers.containsKey(state.getType())) {
			BlockStateHandlerConfiguration.recordHandlers.get(state.getType()).setData(currentState, state);
			
		} else {
			currentState.setData(state.getData());
		}
		
	}

}
