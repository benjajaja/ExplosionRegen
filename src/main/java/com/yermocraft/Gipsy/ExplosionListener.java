package com.yermocraft.Gipsy;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

public class ExplosionListener implements Listener {

	private FactionHandler factionHandler;
	private FileConfiguration config;
	private ExplosionRegen plugin;

	public ExplosionListener(ExplosionRegen plugin, FactionHandler factionHandler,
			FileConfiguration config) {
		this.plugin = plugin;
		this.factionHandler = factionHandler;
		this.config = config;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.isCancelled() || !shouldHandle(event.getLocation(), event.blockList())) {
			return;
		}
		
		event.setYield(0);
		new ExplosionRecord(plugin, config.getBoolean("ignore-containers"), event.blockList(), 60);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPainting(HangingBreakEvent event) {
		if (event.isCancelled()
				|| event.getCause() != RemoveCause.EXPLOSION
				|| !shouldHandle(event.getEntity().getLocation(), null)) {
			return;
		}
		
		event.setCancelled(true);
		new HangingRecord(plugin, event.getEntity(), 60 + 40);
	}

	private boolean shouldHandle(Location location, List<Block> list) {
		if (!config.getStringList("worlds").contains(location.getWorld().getName())) {
			return false;
		}
		
		if (factionHandler.shouldIgnore(list, location)) {
			return false;
		}
		return true;
	}
}
