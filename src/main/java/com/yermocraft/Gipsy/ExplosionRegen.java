package com.yermocraft.Gipsy;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosionRegen extends JavaPlugin {

	private TaskList taskList;

	@Override
	public void onEnable() {
    	saveDefaultConfig();
    	
    	FileConfiguration config = getConfig();
    	
    	this.taskList = new TaskList(this);
    	
    	getServer().getPluginManager().registerEvents(
        		new ExplosionListener(taskList, new FactionHandler(getServer().getPluginManager().getPlugin("Factions")), config), this);
    	
    	getCommand("er").setExecutor(new ErCommandExecutor(taskList));
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Regenerated " + taskList.runAllPending() + " explosions");
		
    }

}
