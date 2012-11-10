package com.yermocraft.Gipsy;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.SimpleAttachableMaterialData;

public class ExtendedBlockState {

	private BlockState state;
	private String[] signLines;
	

	public ExtendedBlockState(BlockState state) {
		this.state = state;
		if (state instanceof Sign) {
			signLines = ((Sign)state).getLines();
			for (int i = 0; i < signLines.length; i++) {
				signLines[i] = i + ": " + signLines[i];
				state.getWorld().getPlayers().get(0).sendMessage("getLines " + i + ": " + signLines[i]);
			}
		}
	}

	public World getWorld() {
		return state.getWorld();
	}

	public Location getLocation() {
		return state.getLocation();
	}

	public BlockState getBlockState() {
		return state;
	}

	

	public void cleanUp(Block block) {
		if (state instanceof FlowerPot || state instanceof Sign
				|| state.getType().getData().getClass().equals(SimpleAttachableMaterialData.class)) {
			block.setTypeIdAndData(0, (byte)0, false);
		}
		
	}

	public Block getBlockAt() {
		return state.getWorld().getBlockAt(state.getX(), state.getY(), state.getZ());
	}

	public void update() {
		state.update(true);
		setExendedData();
	}

	private void setExendedData() {
		if (state instanceof Sign) {
			BlockState newState = getBlockAt().getState();
			for (int i = 0; i < ((Sign)state).getLines().length; i++) {
				newState.getWorld().getPlayers().get(0).sendMessage("forced update " + i + ": " + ((Sign) newState).getLine(i));
			}

			for (int i = 0; i < signLines.length; i++) {
				newState.getWorld().getPlayers().get(0).sendMessage("setLine " + i + ": " + signLines[i]);
				((Sign) newState).setLine(i, signLines[i]);
			}
			newState.update();
		}
		
	}
}
