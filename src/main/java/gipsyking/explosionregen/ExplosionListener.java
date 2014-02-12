package gipsyking.explosionregen;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public class ExplosionListener implements Listener {

	private FactionHandler factionHandler;
	private TaskList taskList;
	private boolean ignoreContainers;
	private int regenerationMinutes;
	private List<String> worldNames;
	private boolean breakObsidian;
	private boolean clearLiquids;
	private List<Integer> dropList;

	public ExplosionListener(TaskList taskList, FactionHandler factionHandler,
			FileConfiguration config) {
		this.taskList = taskList;
		this.factionHandler = factionHandler;
		
		this.ignoreContainers = config.getBoolean("ignore-containers");
		this.regenerationMinutes = config.getInt("regeneration-minutes");
		this.worldNames = config.getStringList("worlds");
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
		
		new ExplosionRecord(taskList, ignoreContainers, event.blockList(), event.getLocation(), regenerationMinutes, breakObsidian, dropList);
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
