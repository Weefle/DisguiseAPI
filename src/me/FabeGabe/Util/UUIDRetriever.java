package me.FabeGabe.Util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class UUIDRetriever {

	private String name;
	private UUID uuid;
	private final String WEB_PAGE = "http://mcuuid.com/api/";
	private static Map<UUID, Property> map = new HashMap<UUID, Property>();

	public UUIDRetriever(String name) {
		this.name = ChatColor
				.stripColor(ChatColor.translateAlternateColorCodes('&', name));
		retrieve();
	}

	public UUID getUUID() {
		return uuid;
	}

	public static void setSkin(GameProfile gp) {
		if (getMap().containsKey(gp.getId())) {
			PropertyMap map = gp.getProperties();
			map.put("textures", getMap().get(gp.getId()));
			return;
		}
		try {
			URL url = new URL(
					"https://sessionserver.mojang.com/session/minecraft/profile/"
							+ gp.getId().toString().replaceAll("-", "")
							+ "?unsigned=false");
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject root = (JSONObject) parser.parse(reader);
			JSONArray properties = (JSONArray) root.get("properties");
			String data = (String) ((JSONObject) properties.get(0))
					.get("value");
			String signature = (String) ((JSONObject) properties.get(0))
					.get("signature");
			reader.close();
			PropertyMap map = gp.getProperties();
			Property property = new Property("textures", data, signature);
			map.put("textures", property);
			getMap().put(gp.getId(), property);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void retrieve() {
		try {
			URL url = new URL(WEB_PAGE + name);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(url.openStream()));
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(reader);
			String uid = (String) object.get("uuid_formatted");
			this.uuid = UUID.fromString(uid);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Map<UUID, Property> getMap() {
		return map;
	}

}