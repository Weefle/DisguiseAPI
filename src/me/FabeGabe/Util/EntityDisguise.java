package me.FabeGabe.Util;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;

import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityBlaze;
import net.minecraft.server.v1_8_R3.EntityCaveSpider;
import net.minecraft.server.v1_8_R3.EntityChicken;
import net.minecraft.server.v1_8_R3.EntityCow;
import net.minecraft.server.v1_8_R3.EntityCreeper;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
import net.minecraft.server.v1_8_R3.EntityEnderman;
import net.minecraft.server.v1_8_R3.EntityEndermite;
import net.minecraft.server.v1_8_R3.EntityGhast;
import net.minecraft.server.v1_8_R3.EntityGiantZombie;
import net.minecraft.server.v1_8_R3.EntityGuardian;
import net.minecraft.server.v1_8_R3.EntityHorse;
import net.minecraft.server.v1_8_R3.EntityIronGolem;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.EntityMagmaCube;
import net.minecraft.server.v1_8_R3.EntityMushroomCow;
import net.minecraft.server.v1_8_R3.EntityOcelot;
import net.minecraft.server.v1_8_R3.EntityPig;
import net.minecraft.server.v1_8_R3.EntityPigZombie;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityRabbit;
import net.minecraft.server.v1_8_R3.EntitySheep;
import net.minecraft.server.v1_8_R3.EntitySilverfish;
import net.minecraft.server.v1_8_R3.EntitySkeleton;
import net.minecraft.server.v1_8_R3.EntitySlime;
import net.minecraft.server.v1_8_R3.EntitySnowman;
import net.minecraft.server.v1_8_R3.EntitySpider;
import net.minecraft.server.v1_8_R3.EntitySquid;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.EntityWitch;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.EntityWolf;
import net.minecraft.server.v1_8_R3.EntityZombie;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.World;

public class EntityDisguise extends Disguise {

	private EntityLiving living;

	public EntityDisguise(Player p, EntityType type) {
		super(p, type);
	}

	@SuppressWarnings("deprecation")
	public String getName() {
		String name = "Pig";
		switch (living.getBukkitEntity().getType()) {
		case CAVE_SPIDER:
			name = "Cave Spider";
			break;
		case HORSE:
			name = "Horse";
			break;
		case IRON_GOLEM:
			name = "Iron Golem";
			break;
		case MAGMA_CUBE:
			name = "Magma Cube";
			break;
		case MINECART:
			name = "Minecart";
			break;
		case MINECART_CHEST:
			name = "Minecart w/ Chest";
			break;
		case MINECART_COMMAND:
			name = "Minecart w/ Command Block";
			break;
		case MINECART_FURNACE:
			name = "Minecart w/ Furnace";
			break;
		case MINECART_HOPPER:
			name = "Minecart w/ Hopper";
			break;
		case MINECART_MOB_SPAWNER:
			name = "Minecart w/ Spawner";
			break;
		case MINECART_TNT:
			name = "Minecart w/ TNT";
			break;
		case MUSHROOM_COW:
			name = "Mooshroom";
			break;
		case OCELOT:
			name = "Ocelot";
			break;
		case PAINTING:
			name = "Painting";
			break;
		case PIG_ZOMBIE:
			name = "Zombie Pigman";
			break;
		case SKELETON:
			Skeleton skeleton = (Skeleton) living.getBukkitEntity();
			name = skeleton.getSkeletonType() == SkeletonType.NORMAL
					? "Skeleton" : "Wither Skeleton";
			break;
		case SNOWMAN:
			name = "Snow Golem";
			break;
		case WITHER:
			name = "Wither Boss";
			break;
		default:
			name = living.getBukkitEntity().getType().getName();
			break;
		}
		return name;
	}

	@Override
	public void applyDisguise(Collection<Player> players) {
		EntityPlayer ep = ((CraftPlayer) getPlayer()).getHandle();
		EntityLiving ent = convert(getType());
		ent.setLocation(ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch);
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(
				ep.getId());
		PacketPlayOutPlayerInfo infoRemove = new PacketPlayOutPlayerInfo(
				EnumPlayerInfoAction.REMOVE_PLAYER, ep);
		PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving(
				ent);
		for (Player p : players) {
			if (p == getPlayer())
				continue;
			EntityPlayer aep = ((CraftPlayer) p).getHandle();
			PlayerConnection pc = aep.playerConnection;
			pc.sendPacket(destroy);
			pc.sendPacket(infoRemove);
			pc.sendPacket(spawn);
		}
		this.living = ent;
	}

	@Override
	public void revertDisguise(Collection<Player> players) {
		PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(
				living.getId());
		EntityPlayer ep = ((CraftPlayer) getPlayer()).getHandle();
		PacketPlayOutPlayerInfo infoAdd = new PacketPlayOutPlayerInfo(
				EnumPlayerInfoAction.ADD_PLAYER, ep);
		PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(
				ep);
		for (Player p : players) {
			if (p == getPlayer())
				continue;
			EntityPlayer aep = ((CraftPlayer) p).getHandle();
			PlayerConnection pc = aep.playerConnection;
			pc.sendPacket(destroy);
			pc.sendPacket(infoAdd);
			pc.sendPacket(spawn);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void move(Location old, Location newloc) {
		PacketPlayOutRelEntityMoveLook moveLook = new PacketPlayOutRelEntityMoveLook(
				this.living.getId(),
				(byte) ((newloc.getBlockX() - old.getBlockX()) * 32),
				(byte) ((newloc.getBlockY() - old.getBlockY()) * 32),
				(byte) ((newloc.getBlockZ() - old.getBlockZ()) * 32),
				(byte) newloc.getYaw(), (byte) newloc.getPitch(),
				getPlayer().isOnGround());
		PacketPlayOutEntityHeadRotation rotation = new PacketPlayOutEntityHeadRotation(
				living, (byte) (newloc.getYaw() * 256 / 360));
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p == getPlayer())
				continue;
			EntityPlayer aep = ((CraftPlayer) p).getHandle();
			aep.playerConnection.sendPacket(moveLook);
			aep.playerConnection.sendPacket(rotation);
		}
	}

	private EntityLiving convert(EntityType type) {
		EntityLiving living = null;
		EntityPlayer ep = ((CraftPlayer) getPlayer()).getHandle();
		World world = ep.getWorld();
		switch (type) {
		case BAT:
			EntityBat bat = new EntityBat(world);
			living = bat;
			break;
		case BLAZE:
			living = new EntityBlaze(world);
			break;
		case CAVE_SPIDER:
			living = new EntityCaveSpider(world);
			break;
		case CHICKEN:
			living = new EntityChicken(world);
			break;
		case COW:
			living = new EntityCow(world);
			break;
		case CREEPER:
			living = new EntityCreeper(world);
			break;
		case ENDERMAN:
			living = new EntityEnderman(world);
			break;
		case ENDERMITE:
			living = new EntityEndermite(world);
			break;
		// case ENDER_CRYSTAL:
		// living = new EntityEnderCrystal(world);
		// break;
		case ENDER_DRAGON:
			living = new EntityEnderDragon(world);
			break;
		case GHAST:
			living = new EntityGhast(world);
			break;
		case GIANT:
			living = new EntityGiantZombie(world);
			break;
		case GUARDIAN:
			living = new EntityGuardian(world);
			break;
		case HORSE:
			living = new EntityHorse(world);
			break;
		case IRON_GOLEM:
			living = new EntityIronGolem(world);
			break;
		case MAGMA_CUBE:
			living = new EntityMagmaCube(world);
			break;
		// case MINECART:
		// living = new EntityMinecartRideable(world);
		// break;
		case MUSHROOM_COW:
			living = new EntityMushroomCow(world);
			break;
		case OCELOT:
			living = new EntityOcelot(world);
			break;
		case PIG:
			living = new EntityPig(world);
			break;
		case PIG_ZOMBIE:
			living = new EntityPigZombie(world);
			break;
		case RABBIT:
			living = new EntityRabbit(world);
			break;
		case SHEEP:
			living = new EntitySheep(world);
			break;
		case SILVERFISH:
			living = new EntitySilverfish(world);
			break;
		case SKELETON:
			living = new EntitySkeleton(world);
			break;
		case SLIME:
			living = new EntitySlime(world);
			break;
		case SNOWMAN:
			living = new EntitySnowman(world);
			break;
		case SPIDER:
			living = new EntitySpider(world);
			break;
		case SQUID:
			living = new EntitySquid(world);
			break;
		case VILLAGER:
			living = new EntityVillager(world);
			break;
		case WITCH:
			living = new EntityWitch(world);
			break;
		case WITHER:
			living = new EntityWither(world);
			break;
		case WOLF:
			living = new EntityWolf(world);
			break;
		case ZOMBIE:
			living = new EntityZombie(world);
			break;
		default:
			living = new EntityPig(world);
			break;
		}
		return living;
	}

	public EntityLiving getEntity() {
		return living;
	}

}