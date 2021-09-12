package me.optimumportal.servermotd;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.optimumportal.servermotd.listeners.Listeners;

public class Main extends JavaPlugin {
  private static Main instance;
  
    public FileConfiguration playerData = null;
    public File playerDataFile = null;
    public void reloadPlayerData() {
	if(playerDataFile == null) {
		getLogger().info("Generating new playerdata.yml file.");
		playerDataFile = new File(getDataFolder() + File.separator + "playerdata.yml");
		playerData = YamlConfiguration.loadConfiguration(playerDataFile);
		playerData.createSection("Data");
	}
	playerData = YamlConfiguration.loadConfiguration(playerDataFile);
}
    public FileConfiguration getPlayerData() {
		if (playerData == null) {
			reloadPlayerData();
		}
		return playerData;
    }
    
    public void savePlayerData() {
    	if (playerData == null | playerDataFile == null) {
    		return;
    	}
    	try {
    		getPlayerData().save(playerDataFile);
    	} catch (IOException e) {
    		getLogger().log(Level.SEVERE, "Could not save config to" + playerDataFile, e);
    	}
    }
    
    @Override
    public void onEnable() {
    	instance = this;
    	reloadPlayerData();
    	savePlayerData();
    	
      File config = new File(getDataFolder() + File.separator + "config.yml");
      if(!config.exists()) {
      	System.out.println("Generating new config.yml file");
      	this.getConfig().options().copyDefaults(true);
      	saveDefaultConfig();
      }
      reloadPlayerData();
      Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }
    
  public void onDisable() {
	System.out.println("ServerMOTD has been disabled.");
    instance = null; //Stops memory leaks.
  } 
  public static Main getInstance() {
	    return instance; //Access methods from other classes.
  }
}