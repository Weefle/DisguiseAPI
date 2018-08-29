package me.FabeGabe.Util;

import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public abstract class Disguise {

	private Player player;
	private EntityType type;

	protected Disguise(Player player, EntityType type) {
		this.player = player;
		this.type = type;
	}

	public abstract void applyDisguise(Collection<Player> players);

	public abstract void revertDisguise(Collection<Player> players);

	public abstract void move(Location old, Location newloc);
	
	public final Player getPlayer() {
		return player;
	}

	public final EntityType getType() {
		return type;
	}

}