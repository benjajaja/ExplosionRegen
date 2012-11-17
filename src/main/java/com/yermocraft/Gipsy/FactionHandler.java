package com.yermocraft.Gipsy;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;

public class FactionHandler {

	private boolean isFactionsEnabled = false;
	
	public FactionHandler(Plugin plugin, boolean ignoreWilderness) {
		isFactionsEnabled = plugin != null && plugin instanceof P && !ignoreWilderness;
	}

	public boolean shouldIgnore(List<Block> list, Location location) {
		if (!isFactionsEnabled) {
			return false;
		}
		
		if (list != null) {
			for(Block block : list) {
				if(!Board.getFactionAt(new FLocation(block.getLocation())).isNone())
					return false;
			}
			
		} else if(!Board.getFactionAt(new FLocation(location)).isNone()) {
			return false;
			
		}
		return true;
	}

	
}
