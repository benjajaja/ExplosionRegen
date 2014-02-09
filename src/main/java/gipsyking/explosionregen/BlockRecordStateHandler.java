package gipsyking.explosionregen;

import org.bukkit.block.BlockState;

public interface BlockRecordStateHandler {
	void setData(BlockState currentState, BlockState previousState);
}
