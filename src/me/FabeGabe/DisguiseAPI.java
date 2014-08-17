package me.FabeGabe;

/**
 * 
 * @author fabegabe
 * 
 *         If you use this library, make sure to give credit to the original
 *         author.
 * 
 *         http://www.github.com/fabegabe/DisguiseAPI
 * 
 */

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.v1_7_R1.EntityPlayer;
import net.minecraft.server.v1_7_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DisguiseAPI {

	private static DisguiseAPI instance = new DisguiseAPI();
	private List<String> disguised = new ArrayList<String>();
	private Map<String, String> disguise = new HashMap<String, String>();

	public static DisguiseAPI getAPI() {
		return instance;
	}

	public void initialize(final JavaPlugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(new Listener() {

			@EventHandler
			public void onRespawn(PlayerRespawnEvent e) {
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, new BukkitRunnable() {
							@Override
							public void run() {
								refresh();
							}
						}, 1l);
			}

			@EventHandler
			public void onLeave(PlayerQuitEvent e) {
				Player p = e.getPlayer();
				if (isDisguised(p)) {
					disguisePlayer(p, Bukkit.getOnlinePlayers(), p.getName());
					disguise.remove(p.getName());
					disguised.remove(p.getName());
				}
			}

			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				refresh(e.getPlayer());
			}
		}, plugin);
	}

	public List<String> getDisguised() {
		return disguised;
	}

	public boolean isDisguised(Player p) {
		return disguised.contains(p.getName());
	}

	public String getDisguise(Player p) {
		return disguise.get(p.getName());
	}

	public void disguisePlayer(Player p, Player[] canSee, String newName) {
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(
				player);
		try {
			Field a = packet.getClass().getDeclaredField("a");
			Field b = packet.getClass().getDeclaredField("b");
			b.setAccessible(true);
			b.set(packet, new GameProfile(a.getName(), newName));
			for (Player cS : canSee) {
				if (cS != p) {
					EntityPlayer cP = ((CraftPlayer) cS).getHandle();
					cP.playerConnection.sendPacket(packet);
				}
			}
			disguised.add(p.getName());
			disguise.put(p.getName(), newName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refresh() {
		for (String s : disguised) {
			refresh(Bukkit.getPlayer(s));
		}
	}

	private void refresh(Player p) {
		PacketPlayOutNamedEntitySpawn pack = new PacketPlayOutNamedEntitySpawn(
				((CraftPlayer) p).getHandle());
		try {
			Field f = pack.getClass().getDeclaredField("b");
			GameProfile gp = new GameProfile(pack.getClass()
					.getDeclaredField("a").getName(), disguise.get(p.getName()));
			f.setAccessible(true);
			f.set(pack, gp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Player lp : Bukkit.getOnlinePlayers()) {
			if (lp != p) {
				((CraftPlayer) lp).getHandle().playerConnection
						.sendPacket(pack);
			}
		}
	}

}