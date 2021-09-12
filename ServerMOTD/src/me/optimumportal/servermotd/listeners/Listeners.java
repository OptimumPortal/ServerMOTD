package me.optimumportal.servermotd.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import me.optimumportal.servermotd.Main;

public class Listeners implements Listener {
	List<?> neverJoined = Main.getInstance().getConfig().getList("never-joined");
	List<?> joinedBefore = Main.getInstance().getConfig().getList("joined-before");
	int neverJoinedSize = neverJoined.size();
	int joinedBeforeSize = joinedBefore.size();
	Random rng = new Random();
	
	public Map<String, String> playerDataStorage = new HashMap<String, String>();
	public void HashMapSetup() {
	for(String playerIP : Main.getInstance().playerData.getConfigurationSection("data").getKeys(false)) {
		  playerDataStorage.put(playerIP, Main.getInstance().playerData.getString("data." + playerIP));
	  }
	}
	public void HashMapSave() {
		for(Entry<String, String> entry : playerDataStorage.entrySet()) {
			Main.getInstance().playerData.set("data." + entry.getKey(), entry.getValue());
			Main.getInstance().savePlayerData();
		}
	}
	public void onEnable() {
		Main.getInstance().reloadPlayerData();
		HashMapSetup();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerNameConvert = player.getName();
		String playerIP = player.getAddress().getAddress().toString();
		playerIP = playerIP.replaceAll ("/", "");
		playerIP = playerIP.replaceAll("\\.", "-");
			if(!(playerDataStorage.containsKey(playerIP))) {
			playerDataStorage.put(playerIP, playerNameConvert);
			HashMapSave();
		}
	}
	
	
	@EventHandler
	public void onPing(ServerListPingEvent event) {
			String playerIP = event.getAddress().toString();
			playerIP = playerIP.replaceAll("/", "");
			playerIP = playerIP.replaceAll("\\.", "-");
			if (Main.getInstance().playerData.getString("data." + playerIP) != null) { //Joined Before
				int joinedBeforeRandom = rng.nextInt(joinedBeforeSize);
				String playerName = Main.getInstance().getPlayerData().getString("data." + playerIP);
				String joinedBeforeMOTD = this.joinedBefore.get(joinedBeforeRandom).toString();
				String playerRandomMOTD = joinedBeforeMOTD.replace("%player%", playerName);
				String joinedBeforeColor = ChatColor.translateAlternateColorCodes('&', playerRandomMOTD);
				System.out.println("Server pinged by IP: " + playerIP + " (" + playerName + ")");
				event.setMotd(joinedBeforeColor);
			} else { //Never Joined Before
				System.out.println("Server pinged by IP: " + playerIP + " (Has not played before)");
				int neverJoinedRandom = rng.nextInt(neverJoinedSize);
				String neverJoinedMOTD = this.neverJoined.get(neverJoinedRandom).toString();
				String neverJoinedColor = ChatColor.translateAlternateColorCodes('&', neverJoinedMOTD);
				event.setMotd(neverJoinedColor);
			}
	}
}