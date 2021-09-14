package me.optimumportal.servermotd.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerListPingEvent;

import me.optimumportal.servermotd.Main;

public class Listeners implements Listener {
	

	Random rng = new Random();

	public Map<String, String> playerNameStorage = new HashMap<String, String>();
	public Map<String, String> playerUUIDStorage = new HashMap<String, String>();
	public void onEnable() {
		Main.getInstance().reloadPlayerData();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String playerNameConvert = player.getName();
		String playerIP = player.getAddress().getAddress().toString();
		playerIP = playerIP.replaceAll ("/", "");
		playerIP = playerIP.replaceAll("\\.", "-");
		if(!(playerNameStorage.containsKey(playerIP))) {
			playerNameStorage.put(playerIP, playerNameConvert);
			playerUUIDStorage.put(playerIP, player.getUniqueId().toString());
			for(Entry<String, String> entry : playerNameStorage.entrySet()) {
				Main.getInstance().playerData.set("data." + entry.getKey() + ".name", entry.getValue());
				Main.getInstance().savePlayerData();
			}
			for(Entry<String, String> entry : playerUUIDStorage.entrySet()) {
				Main.getInstance().playerData.set("data." + entry.getKey() + ".uuid", entry.getValue());
				Main.getInstance().savePlayerData();
			}
		}
	}

	@EventHandler
	public void onPing(ServerListPingEvent event) {
		List<?> joinedBefore = Main.getInstance().getConfig().getList("joined-before");
		List<?> neverJoined = Main.getInstance().getConfig().getList("never-joined");
		List<?> notWhitelisted = Main.getInstance().getConfig().getList("not-whitelisted");
		List<?> banned = Main.getInstance().getConfig().getList("banned");
		String playerIP = event.getAddress().toString();
		playerIP = playerIP.replaceAll("/", "");
		playerIP = playerIP.replaceAll("\\.", "-");
		if (Main.getInstance().playerData.getString("data." + playerIP + ".name") != null) { 
			//JOINED BEFORE
			UUID uuid = UUID.fromString(Main.getInstance().playerData.getString("data." + playerIP + ".uuid"));
			OfflinePlayer offlineplayer = Bukkit.getOfflinePlayer(uuid);
			if (offlineplayer.isBanned()) {
				// BANNED
				int bannedRandom = rng.nextInt(banned.size());
				String bannedMOTD = Main.getInstance().colorCode(banned.get(bannedRandom).toString());
				event.setMotd(bannedMOTD);
				System.out.println("Server pinged by IP: " + playerIP + " (" + offlineplayer.getName() +" - Banned)");

			} else {
				if (Bukkit.isWhitelistEnforced() == true && Bukkit.getWhitelistedPlayers().contains(offlineplayer)) {
					// NOT WHITELISTED
					int notWhitelistedRandom = rng.nextInt(notWhitelisted.size());
					event.setMotd(Main.getInstance().colorCode(notWhitelisted.get(notWhitelistedRandom).toString()));
					System.out.println("Server pinged by IP: " + playerIP + " (" + offlineplayer.getName() +" - Not Whitelisted)");

				} else {
					// JOINED BEFORE
					int joinedBeforeRandom = rng.nextInt(joinedBefore.size());
					String playerName = Main.getInstance().playerData.getString("data." + playerIP + ".name");
					String joinedBeforeMOTD = joinedBefore.get(joinedBeforeRandom).toString();
					String playerRandomMOTD = joinedBeforeMOTD.replace("%player%", playerName);
					System.out.println("Server pinged by IP: " + playerIP + " (" + playerName + ")");
					event.setMotd(Main.getInstance().colorCode(playerRandomMOTD));
				}
			}
		} else { 
			//NEVER JOINED
			System.out.println("Server pinged by IP: " + playerIP + " (Never Joined)");
			int neverJoinedRandom = rng.nextInt(neverJoined.size());
			String neverJoinedMOTD = neverJoined.get(neverJoinedRandom).toString();
			event.setMotd(Main.getInstance().colorCode(neverJoinedMOTD));
		}
	}
}