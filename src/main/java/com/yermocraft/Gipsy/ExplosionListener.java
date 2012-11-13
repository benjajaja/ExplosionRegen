package com.yermocraft.Gipsy;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;

public class ExplosionListener implements Listener {

	private FactionHandler factionHandler;
	private TaskList taskList;
	private boolean ignoreContainers;
	private int regenerationMinutes;
	private List<String> worldNames;
	private boolean dropSkulls;
	private boolean breakObsidian;
	private boolean clearLiquids;
	private List<Integer> dropList;

	public ExplosionListener(TaskList taskList, FactionHandler factionHandler,
			FileConfiguration config, boolean dropSkulls) {
		this.taskList = taskList;
		this.factionHandler = factionHandler;
		
		this.ignoreContainers = config.getBoolean("ignore-containers");
		this.regenerationMinutes = config.getInt("regeneration-minutes");
		this.worldNames = config.getStringList("worlds");
		this.dropSkulls = dropSkulls;
		this.breakObsidian = config.getBoolean("break-obsidian");
		this.clearLiquids = config.getBoolean("clear-liquids");
		this.dropList = config.getIntegerList("drops");
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		if(!clearLiquids || event.isCancelled() || !shouldHandle(event.getEntity().getLocation(), null)) {
			return;
		}
		
		new LiquidClearance(taskList, event.getEntity().getLocation());
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.isCancelled() || !shouldHandle(event.getLocation(), event.blockList())) {
			return;
		}
		
		event.setYield(0);
		
		new BlockRecord(taskList, ignoreContainers, event.blockList(), event.getLocation(), regenerationMinutes, dropSkulls, breakObsidian, dropList);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void onPainting(HangingBreakEvent event) {
		if (event.isCancelled()
				|| event.getCause() != RemoveCause.EXPLOSION
				|| !shouldHandle(event.getEntity().getLocation(), null)) {
			return;
		}
		
		event.setCancelled(true);
		new HangingRecord(taskList, event.getEntity(), regenerationMinutes);
	}

	private boolean shouldHandle(Location location, List<Block> list) {
		if (!worldNames.contains(location.getWorld().getName())) {
			return false;
		}
		
		if (factionHandler.shouldIgnore(list, location)) {
			return false;
		}
		return true;
	}
}
