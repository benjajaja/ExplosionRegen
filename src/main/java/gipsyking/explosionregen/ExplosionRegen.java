package gipsyking.explosionregen;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


public class ExplosionRegen extends JavaPlugin {

	private TaskList taskList;
	private static ExplosionRegen _singleton_for_debug_only;

	@Override
	public void onEnable() {
		ExplosionRegen._singleton_for_debug_only = this;
		
		BlockStateHandlerConfiguration.setup();
		
    	saveDefaultConfig();
    	
    	FileConfiguration config = getConfig();
    	
    	this.taskList = new TaskList(this);
    	
    	getServer().getPluginManager().registerEvents(
        		new ExplosionListener(taskList, new FactionHandler(getServer().getPluginManager().getPlugin("Factions"), config.getBoolean("ignore-wilderness")), config), this);
    	
    	getCommand("explosionregen").setExecutor(new ErCommandExecutor(taskList));
    	
    	getLogger().info("registered " + BlockStateHandlerConfiguration.recordHandlers.size() + " special handlers for block regeneration.");
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Regenerated " + taskList.runAllPending() + " explosions");
		
    }

	public static void debug(String string) {
		_singleton_for_debug_only.getLogger().info(string);
		for (Player player: _singleton_for_debug_only.getServer().getWorld("world").getPlayers()) {
			player.sendMessage(string);
		}
	}

	
}
