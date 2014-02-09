package gipsyking.explosionregen;

import java.util.ArrayList;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Keeps track of pending tasks
 * Runs HangingRecords after BlockRecord task so that paintings dont't fall off walls
 * @author benja
 *
 */
public class TaskList {

	private ArrayList<ErRunnable> list = new ArrayList<ErRunnable>();
	private ArrayList<ErRunnable> delayedList = new ArrayList<ErRunnable>(); // hanging etc
	
	private JavaPlugin plugin;
	
	public TaskList(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public int runAllPending() {
		try {
			plugin.getServer().getScheduler().cancelTasks(plugin);
		} catch (NoSuchMethodError e) {
			plugin.getLogger().warning("Could not cancel plugin's tasks, performing global regeneration anyway");
		}
		int size = list.size();
		for(ErRunnable runnable: list) {
			runnable.runWithoutSchedule();
		}
		list.clear();
		size += delayedList.size();
		for(ErRunnable runnable: delayedList) {
			runnable.runWithoutSchedule();
		}
		list.clear();
		return size;
	}

	public void add(ErRunnable runnable, int ticks) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, ticks);
		list.add(runnable);
	}
	
	/*public void add(HangingRecord runnable, int ticks) {
		delayedList.add(runnable.setId(plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, ticks)));
	}*/

	public void remove(ErRunnable runnable) {
		list.remove(runnable);
	}
	
	/*public void remove(HangingRecord runnable) {
		delayedList.remove(runnable);
	}*/

	public void runUnrecoverable(Runnable runnable, int ticks) {
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, ticks);
		
	}

}
