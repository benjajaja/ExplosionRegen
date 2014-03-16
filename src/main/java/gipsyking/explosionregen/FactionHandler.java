package gipsyking.explosionregen;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.mcore.ps.PS;

public class FactionHandler {

	private boolean isFactionsEnabled = false;
	
	public FactionHandler(Plugin plugin, boolean ignoreWilderness) {
		isFactionsEnabled = plugin != null && /*plugin instanceof P &&*/ ignoreWilderness;
	}

	public boolean shouldIgnore(List<Block> list, Location location) {
		if (!isFactionsEnabled) {
			return false;
		}
		
		BoardColls board = BoardColls.get();
		
		if (list != null) {
			for(Block block : list) {
				if(!board.getFactionAt(PS.valueOf(block.getLocation())).isNone()) {
					
					return false;
				}
			}
			
		} else if(!board.getFactionAt(PS.valueOf(location)).isNone()) {
			return false;
			
		}
		
		return true;
	}

	
}
