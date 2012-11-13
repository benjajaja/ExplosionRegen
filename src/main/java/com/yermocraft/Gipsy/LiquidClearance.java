package com.yermocraft.Gipsy;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public class LiquidClearance implements Runnable {

	private static int CLEARANCE_CUBE_RADIUS = 3;
	private ArrayList<BlockState> list = new ArrayList<BlockState>();
	
	public LiquidClearance(TaskList taskList, Location location) {
		
		for (int x = -CLEARANCE_CUBE_RADIUS; x <= CLEARANCE_CUBE_RADIUS; x++) {
			for (int y = -CLEARANCE_CUBE_RADIUS; y <= CLEARANCE_CUBE_RADIUS; y++) {
				for (int z = -CLEARANCE_CUBE_RADIUS; z <= CLEARANCE_CUBE_RADIUS; z++) {
					
					Block block = location.clone().add(new Vector(x, y, z)).getBlock();

					if (block.getType() == Material.STATIONARY_WATER || block.getType() == Material.STATIONARY_LAVA
							|| block.getType() == Material.WATER || block.getType() == Material.LAVA) {
						list.add(block.getState());
						block.setType(Material.AIR);
					}
				}
			}
		}
		
		taskList.run(this, 1);
	}

	@Override
	public void run() {
		for(int i = list.size() - 1; i >= 0; i--) {
			list.get(i).update(true);
		}
		
	}

}
