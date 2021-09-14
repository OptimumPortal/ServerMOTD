package me.optimumportal.servermotd.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.optimumportal.servermotd.Main;

public class Commands implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("reloadconfig")) {
			if(sender.hasPermission("servermotd.reloadconfig")) {
				Main.getInstance().reloadConfig();
				sender.sendMessage(ChatColor.AQUA + "Successfully reloaded config.yml!");
				Bukkit.getLogger().info(ChatColor.AQUA + "Config was reloaded by: " + sender);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
			}
		}
		return true;
	}
}