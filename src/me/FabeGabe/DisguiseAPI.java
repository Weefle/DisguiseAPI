package me.FabeGabe;

/**
 * 
 * 	   @author fabegabe
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

	/**
	 * 
	 * Variables
	 * 
	 */

	// New instance.
	private static DisguiseAPI api = new DisguiseAPI();
	// List of players who are disguised.
	private List<String> disguised = new ArrayList<String>();
	// Map storing players' disguise name.
	private Map<String, String> disguise = new HashMap<String, String>();

	// Returns instance (new API).
	public static DisguiseAPI getAPI() {
		return api;
	}

	// Just in case you don't want to do the hard stuff. ;)
	public void initialize(final JavaPlugin plugin) {
		// Registers new listener.
		plugin.getServer().getPluginManager().registerEvents(new Listener() {

			// Handles player respawn skin change glitch.
			@EventHandler
			public void onRespawn(PlayerRespawnEvent e) {
				// Runs a scheduled task to refresh. (Won't work instantly.)
				// *Edit: I meant the disguise won't re-apply if the task
				// is not delayed.*
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask(plugin, new BukkitRunnable() {
							@Override
							public void run() {
								refresh();
							}
						}, 1l);
			}

			// Handles map/list duplication prevention
			// and NullPointerExceptions whilst sending packets.
			@EventHandler
			public void onLeave(PlayerQuitEvent e) {
				Player p = e.getPlayer();
				// Checks if the player was disguised.
				if (isDisguised(p)) {
					// Disguises player as (him/her)self, then removes from list
					// and map
					// making sure that there are no duplications in the map nor
					// in the list.
					unDisguisePlayer(p);
					disguise.remove(p.getName());
					disguised.remove(p.getName());
				}
			}

			// Handles join 'not updated' disguises.
			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				// Refreshes the player's view (so players are disguised).
				refresh(e.getPlayer());
			}

		}, plugin);
	}

	// Returns the list of all disguised people.
	public List<String> getDisguised() {
		return disguised;
	}

	// Returns if a player is disguised or not.
	public boolean isDisguised(Player p) {
		return disguised.contains(p.getName());
	}

	// Returns the userName applied to the player in the disguise.
	public String getDisguise(Player p) {
		return disguise.get(p.getName());
	}

	// Undisguise player method.
	public void unDisguisePlayer(Player p) {
		// Getting handle from the player.
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		// Creating our packet.
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(
				player);
		// Trying to set field a and b accessible,
		// then applying the packet to all the other players.
		try {
			// Id field.
			Field a = packet.getClass().getDeclaredField("a");
			// GameProfile field.
			Field b = packet.getClass().getDeclaredField("b");
			// Allowing accessibility. (a is not needed, since we're just
			// getting the name).
			b.setAccessible(true);
			// Setting b (from the player's info/packet) to a
			// new GameProfile with a being the id, then
			// p.getName(), being the player's displayed tag.
			b.set(packet, new GameProfile(a.getName(), p.getName()));
			// Looping through all the players, then checking if cS is not the
			// player,
			// evading freeze glitches and/or any errors.
			for (Player cS : Bukkit.getOnlinePlayers()) {
				if (cS != p) {
					// Getting handle from cS, then sending the packet to them.
					EntityPlayer cP = ((CraftPlayer) cS).getHandle();
					cP.playerConnection.sendPacket(packet);
				}
			}
			// Adding the player to the list, and setting the
			// value for the player's name to be the disguise.
			disguised.remove(p.getName());
			disguise.remove(p.getName());
		} catch (Exception e) {
			// Otherwise, we print the stack trace.
			e.printStackTrace();
		}
	}

	// Disguise player method. (Disguises player as 'newName')
	public void disguisePlayer(Player p, Player[] canSee, String newName) {
		// Getting handle from the player.
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		// Creating our packet.
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(
				player);
		// Trying to set field a and b accessible,
		// then applying the packet to all the other players.
		try {
			// Id field.
			Field a = packet.getClass().getDeclaredField("a");
			// GameProfile field.
			Field b = packet.getClass().getDeclaredField("b");
			// Allowing accessibility. (a is not needed, since we're just
			// getting the name).
			b.setAccessible(true);
			// Setting b (from the player's info/packet) to a
			// new GameProfile with a being the name, then
			// newName, being the player's displayed tag.
			b.set(packet, new GameProfile(a.getName(), newName));
			// Looping through all the players, then checking if cS is not the
			// player,
			// evading freeze glitches and/or any errors.
			for (Player cS : canSee) {
				if (cS != p) {
					// Getting handle from cS, then sending the packet to them.
					EntityPlayer cP = ((CraftPlayer) cS).getHandle();
					cP.playerConnection.sendPacket(packet);
				}
			}
			// Adding the player to the list, and setting the
			// value for the player's name to be the disguise.
			disguised.add(p.getName());
			disguise.put(p.getName(), newName);
		} catch (Exception e) {
			// Otherwise, we print the stack trace.
			e.printStackTrace();
		}
	}

	// Disguise player method. (Disguises player as 'newName')
	public void disguisePlayer(Player p, List<String> canSee, String newName) {
		// Getting handle from the player.
		EntityPlayer player = ((CraftPlayer) p).getHandle();
		// Creating our packet.
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn(
				player);
		// Trying to set field a and b accessible,
		// then applying the packet to all the other players.
		try {
			// Id field.
			Field a = packet.getClass().getDeclaredField("a");
			// GameProfile field.
			Field b = packet.getClass().getDeclaredField("b");
			// Allowing accessibility. (a is not needed, since we're just
			// getting the name).
			b.setAccessible(true);
			// Setting b (from the player's info/packet) to a
			// new GameProfile with a being the name, then
			// newName, being the player's displayed tag.
			b.set(packet, new GameProfile(a.getName(), newName));
			// Looping through all the players, then checking if cS is not the
			// player,
			// evading freeze glitches and/or any errors.
			for (String cS : canSee) {
				if (Bukkit.getPlayer(cS) == null) {
					continue;
				}
				Player sp = Bukkit.getPlayer(cS);
				if (sp != p) {
					// Getting handle from sp, then sending the packet to them.
					EntityPlayer cP = ((CraftPlayer) sp).getHandle();
					cP.playerConnection.sendPacket(packet);
				}
			}
			// Adding the player to the list, and setting the
			// value for the player's name to be the disguise.
			disguised.add(p.getName());
			disguise.put(p.getName(), newName);
		} catch (Exception e) {
			// Otherwise, we print the stack trace.
			e.printStackTrace();
		}
	}

	public void refresh() {
		// Refreshing every single player.
		for (String s : disguised) {
			refresh(Bukkit.getPlayer(s));
		}
	}

	// Refreshing player 'p', without modifying
	// the list or map, but getting their values.
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