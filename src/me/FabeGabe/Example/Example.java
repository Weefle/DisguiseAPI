package me.FabeGabe.Example;

import java.util.ArrayList;
import java.util.List;

import me.FabeGabe.DisguiseAPI;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Example extends JavaPlugin {

	@Override
	public void onEnable() {
		// Doing all the hard work for me.
		DisguiseAPI.getAPI().initialize(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label,
			String[] args) {
		if (!(s instanceof Player))
			return true;
		Player p = (Player) s;
		if (cmd.getName().equalsIgnoreCase("dis")) {
			if (args.length == 0) {
				p.sendMessage(ChatColor.RED + "Usage: /dis <player>");
			} else if (args.length == 1) {
				if (DisguiseAPI.getAPI().isDisguised(p)) {
					p.sendMessage(ChatColor.RED
							+ "Undisguise before you disguise again!");
				} else {
					DisguiseAPI.getAPI().disguisePlayer(p,
							Bukkit.getOnlinePlayers(), args[0]);
					p.sendMessage(ChatColor.GREEN + "Disguised as " + args[0]
							+ "!");
				}
			} else if (args.length == 2) {
				// Splitting players by a comma.
				String[] players = args[1].split(",");
				List<String> dis = new ArrayList<String>();
				for (String sp : players) {
					if (Bukkit.getPlayer(sp) == null)
						continue;
					dis.add(sp);
				}
				DisguiseAPI.getAPI().disguisePlayer(p, dis, args[0]);
				p.sendMessage(ChatColor.GREEN + "Disguised as " + args[0] + "!");
			} else {

			}
		} else if (cmd.getName().equalsIgnoreCase("udis")) {
			if (DisguiseAPI.getAPI().isDisguised(p)) {
				p.sendMessage(ChatColor.GREEN + "You have been undisguised!");
				DisguiseAPI.getAPI().unDisguisePlayer(p);
			} else {
				p.sendMessage(ChatColor.RED + "You are not disguised!");
			}
		}
		return true;
	}

}