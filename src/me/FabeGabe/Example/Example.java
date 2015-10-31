package me.FabeGabe.Example;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.FabeGabe.DisguiseAPI;
import me.FabeGabe.Util.Disguise;
import me.FabeGabe.Util.PlayerDisguise;

public class Example extends JavaPlugin {

	@Override
	public void onEnable() {
		// Doing all the hard work for me.
		DisguiseAPI.getAPI().initialize(this);
	}

	@SuppressWarnings("deprecation")
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
					Disguise d = new PlayerDisguise(p, args[0]);
					DisguiseAPI.getAPI().disguisePlayer(p, d,
							DisguiseAPI.getAPI().online());
					p.sendMessage(
							ChatColor.GREEN + "Disguised as " + args[0] + "!");
				}
			} else if (args.length == 2) {
				// Splitting players by a comma.
				String[] players = args[1].split("\\,");
				List<Player> dis = new ArrayList<Player>();
				for (String sp : players) {
					if (Bukkit.getPlayer(sp) == null)
						continue;
					dis.add(Bukkit.getPlayer(sp));
				}
				Disguise d = new PlayerDisguise(p, args[0]);
				DisguiseAPI.getAPI().disguisePlayer(p, d, dis);
				p.sendMessage(
						ChatColor.GREEN + "Disguised as " + args[0] + "!");
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