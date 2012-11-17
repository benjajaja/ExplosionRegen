package com.yermocraft.Gipsy;


import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosionRegen extends JavaPlugin {

	private TaskList taskList;

	@Override
	public void onEnable() {
    	saveDefaultConfig();
    	
    	FileConfiguration config = getConfig();
    	
    	this.taskList = new TaskList(this);
    	
    	boolean dropSkulls = false;
    	if (getServer().getPluginManager().getPlugin("PlayerHeads") != null) {
    		dropSkulls = true;
    		getLogger().info("Will drop player head blocks with PlayerHeads plugin");
    	}
    	
    	getServer().getPluginManager().registerEvents(
        		new ExplosionListener(taskList, new FactionHandler(getServer().getPluginManager().getPlugin("Factions"), config.getBoolean("ignore-wilderness")), config, dropSkulls), this);
    	
    	getCommand("er").setExecutor(new ErCommandExecutor(taskList));
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Regenerated " + taskList.runAllPending() + " explosions");
		
    }

	@Deprecated
	public static void debug(World world, String string) {
		for (Player player: world.getPlayers()) {
			player.sendMessage(string);
		}
	}

}
