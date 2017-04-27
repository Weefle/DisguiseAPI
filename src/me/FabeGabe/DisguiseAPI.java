package me.FabeGabe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.FabeGabe.Util.Disguise;
import me.FabeGabe.Util.PlayerDisguise;

public class DisguiseAPI {

	/**
	 * 
	 * Variables
	 * 
	 */

	// New instance.
	private static DisguiseAPI api = new DisguiseAPI();
	// Set storing players' disguise objects.
	private Set<Disguise> disguises = new HashSet<Disguise>();
	private JavaPlugin plugin;

	private DisguiseAPI() {}
	
	// Returns instance (API).
	public static DisguiseAPI getAPI() {
		return api;
	}
	
	// Returns new API instance.
	public static DisguiseAPI newInstance() {
		return new DisguiseAPI();
	}

	// Just in case you don't want to do the hard stuff. ;)
	public void initialize(final JavaPlugin plugin) {
		this.plugin = plugin;
		// Registers new listener.
		plugin.getServer().getPluginManager().registerEvents(new Listener() {

			// Handles player respawn skin change glitch.
			@EventHandler
			public void onRespawn(PlayerRespawnEvent e) {
				// Runs a scheduled task to refresh. (Won't work instantly.)
				// *Edit: I meant the disguise won't re-apply if the task
				// is not delayed.*
				new BukkitRunnable() {
					@Override
					public void run() {
						refresh();
					}
				}.runTaskLater(plugin, 1l);
			}

			@EventHandler
			public void onMove(PlayerMoveEvent e) {
				Player p = e.getPlayer();
				if (getDisguise(p) == null)
					return;
				Disguise dis = getDisguise(p);
				dis.move(e.getFrom(), e.getTo());
			}

			@EventHandler
			public void onQuit(PlayerQuitEvent e) {
				Player p = e.getPlayer();
				for (Disguise d : disguises) {
					new BukkitRunnable() {

						@Override
						public void run() {
							d.revertDisguise(Arrays.asList(p));
						}

					}.runTaskLater(plugin, 1l);
				}
				if (getDisguise(p) == null)
					return;
				Disguise d = getDisguise(p);
				d.revertDisguise(online());
				disguises.remove(d);
			}

			@EventHandler
			public void onJoin(PlayerJoinEvent e) {
				Player p = e.getPlayer();
				for (Disguise d : disguises) {
					new BukkitRunnable() {

						@Override
						public void run() {
							d.applyDisguise(Arrays.asList(p));
						}

					}.runTaskLater(plugin, 1l);
				}
			}

			@EventHandler
			public void onHoldItem(PlayerItemHeldEvent e) {
				Player p = e.getPlayer();
				if (getDisguise(p) == null)
					return;
				Disguise dis = getDisguise(p);
				if (!(dis instanceof PlayerDisguise))
					return;
				PlayerDisguise pd = (PlayerDisguise) dis;
				pd.setItemInHand(e.getNewSlot());
			}

			@EventHandler
			public void onInteract(PlayerInteractEvent e) {
				if (e.getAction() != Action.LEFT_CLICK_AIR
						&& e.getAction() != Action.LEFT_CLICK_BLOCK)
					return;
				Player p = e.getPlayer();
				if (getDisguise(p) == null)
					return;
				Disguise dis = getDisguise(p);
				if (!(dis instanceof PlayerDisguise))
					return;
				PlayerDisguise pd = (PlayerDisguise) dis;
				pd.swingArm();
			}

			@EventHandler
			public void onSneak(PlayerToggleSneakEvent e) {
				Player p = e.getPlayer();
				if (getDisguise(p) == null)
					return;
				Disguise dis = getDisguise(p);
				if (!(dis instanceof PlayerDisguise))
					return;
				PlayerDisguise pd = (PlayerDisguise) dis;
				pd.sneak(e.isSneaking());
			}

		}, plugin);
	}

	// Returns the list of all disguised people.
	public List<String> getDisguised() {
		List<String> s = new ArrayList<String>();
		for (Disguise d : disguises) {
			s.add(d.getPlayer().getName());
		}
		return s;
	}

	// Returns if a player is disguised or not.
	public boolean isDisguised(Player p) {
		for (Disguise d : disguises) {
			if (d.getPlayer().equals(p))
				return true;
		}
		return false;
	}

	// Returns the disguise applied to 'p'.
	public Disguise getDisguise(Player p) {
		for (Disguise d : disguises) {
			if (d.getPlayer().equals(p))
				return d;
		}
		return null;
	}

	// Undisguise player method.
	public void unDisguisePlayer(Player p) {
		if (!isDisguised(p))
			return;
		getDisguise(p).revertDisguise(online());
		disguises.remove(getDisguise(p));
	}

	public Collection<Player> online() {
		return new ArrayList<Player>(Bukkit.getOnlinePlayers());
	}

	// Disguise player method. (Disguises player as 'newName')
	public void disguisePlayer(Player p, Disguise d,
			Collection<Player> players) {
		if (isDisguised(p))
			return;
		d.applyDisguise(players);
		disguises.add(d);
	}

	public void disguisePlayer(Player p, Disguise d) {
		disguisePlayer(p, d, online());
	}

	public void refresh() {
		// Refreshing every single player.
		for (Disguise d : disguises) {
			refresh(d.getPlayer());
		}
	}

	// Refreshing player 'p', without modifying
	// the list or map, but getting their values.
	private void refresh(Player p) {
		Disguise d = getDisguise(p);
		new BukkitRunnable() {

			@Override
			public void run() {
				d.revertDisguise(online());
				d.applyDisguise(online());
			}

		}.runTaskLater(plugin, 1l);
	}

}
