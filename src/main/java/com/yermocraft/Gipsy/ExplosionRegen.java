package com.yermocraft.Gipsy;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplosionRegen extends JavaPlugin {

	public void onEnable() {
    	saveDefaultConfig();
    	
    	FileConfiguration config = getConfig();
    	
    	getServer().getPluginManager().registerEvents(
        		new ExplosionListener(this, new FactionHandler(getServer().getPluginManager().getPlugin("Factions")), config), this);
	}
	
	public void onDisable() {
    	getServer().getScheduler().cancelTasks(this);

    	getLogger().info(getName() + " ended remaining tasks");
    }

	public void debug(String string) {
		getServer().getWorlds().get(0).getPlayers().get(0).sendMessage(string);
		
	}

}
