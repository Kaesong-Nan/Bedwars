package de.tubeplay.bedwars.listener;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mojang.authlib.GameProfile;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.PlayerInfoData;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class JoinEvent implements Listener {
	
	private UUID uuid = UUID.randomUUID();
    private String value = null;
    private String signature = null;
	private int count;
	private String name;
	private GameProfile profile;
	
	public JoinEvent() {
		
		MySQL mysql = Bedwars.getMySQL();
		ResultSet rs = mysql.query("SELECT UUID FROM StatsGlobal ORDER BY Points DESC LIMIT 1");
		String realUUID = null;
		
		try {
			
			if (rs.first()) {
				
				realUUID = rs.getString("UUID");
				
				if (mysql.playerExists(realUUID)) {
					
					name = mysql.getPlayername(realUUID);
					
				} else {
					
					name = "§0Unknown";
					value = "eyJ0aW1lc3RhbXAiOjE1MDkzOTA4NTA3MzQsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxNjNkYWZhYzFkOTFhOGM5MWRiNTc2Y2FhYzc4NDMzNjc5MWE2ZTE4ZDhmN2Y2Mjc3OGZjNDdiZjE0NmI2In19fQ==";
					signature = "MxJpmqEF11HRd7emaUSPYix5jxRJ04UFA9cHIBqL6pflk54b9u0vmgyJPb6y2kkz3mseiU8Wmyzjm+cYnuY3yZxTDj2YMrAyXh3CI8zwA6v8HPuBiRPppaDv1QkqZfZDeiw0oZiFz3ORt4BzevI9WYc4FolI564uqGSVBtk2iHJ6MW4rvIszIxsYWeNaasIBMNmf4q3EylvrAGMXFgGIkK3ERfChbDjjfvKd0AgVKaQNn3e489KZDs0hDl4/lamtfNiNICMb3dC4kIK1ef1qYnkeufq/0X8ts3y6YzMZ6J1BnJAQycGQVf9gA4LtANlH1aIkUV90siFFj7DHA4jfrJe3U+W9V1JGHhOkg7ynaIJM9evQt8NkZ1jbP16BYaDsXEPfep/USHOYOzHU/NfYkNdXOFDrE5LBUTzmQXSuTg5f6k2YJAm0mUF3z+t6JWtDdoZC1G5lWO4858Tlrut/BVRESmJhfKdwnX+8IuxgU0r3YEjSulzXeCgtSecfag7cR4+J3JGT9zCx2H8kjDUmq/9BDU7aQzq6GzqZP1Pl+WzAHVE47jE6RbVOUXRXG4Uaf4L8WhFW0OoLnOphJrz7/Yu2HrvZs9ulTXafWYOUsPqUjEFweHaE+z9D5VIu14XSK7FGRPwHlly6CTwVa0a5JwGnpdwOcODs2oIGxHjjKQ8=";
				}
				
			} else {
				
				name = "§";
			}
			
		} catch (SQLException e) {
			
			mysql.errorReadingDatabase(e);
		}
		
		if (!name.equalsIgnoreCase("§")) {
			
			if (value == null) {
				
				try {
					
	                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + realUUID.replace("-", "") + "?unsigned=false");
	                URLConnection uc = url.openConnection();
	                uc.setUseCaches(false);
	                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
	                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
	                uc.addRequestProperty("Pragma", "no-cache");
	                Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
	                scanner.useDelimiter("\\A");
	                JSONParser parser = new JSONParser();
	                JSONObject obj = (JSONObject) parser.parse(scanner.next());
	                JSONArray properties = (JSONArray) obj.get("properties");
	                
	                for (int i = 0; i < properties.size(); i++) {
	                	
	                    JSONObject property = (JSONObject) properties.get(i);
	                    value = (String) property.get("value");
	                    signature = (String) property.get("signature");
	                }
	                scanner.close();
					
				} catch (IOException | ParseException e) {
					
					value = "eyJ0aW1lc3RhbXAiOjE1MDkzOTA4NTA3MzQsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxNjNkYWZhYzFkOTFhOGM5MWRiNTc2Y2FhYzc4NDMzNjc5MWE2ZTE4ZDhmN2Y2Mjc3OGZjNDdiZjE0NmI2In19fQ==";
					signature = "MxJpmqEF11HRd7emaUSPYix5jxRJ04UFA9cHIBqL6pflk54b9u0vmgyJPb6y2kkz3mseiU8Wmyzjm+cYnuY3yZxTDj2YMrAyXh3CI8zwA6v8HPuBiRPppaDv1QkqZfZDeiw0oZiFz3ORt4BzevI9WYc4FolI564uqGSVBtk2iHJ6MW4rvIszIxsYWeNaasIBMNmf4q3EylvrAGMXFgGIkK3ERfChbDjjfvKd0AgVKaQNn3e489KZDs0hDl4/lamtfNiNICMb3dC4kIK1ef1qYnkeufq/0X8ts3y6YzMZ6J1BnJAQycGQVf9gA4LtANlH1aIkUV90siFFj7DHA4jfrJe3U+W9V1JGHhOkg7ynaIJM9evQt8NkZ1jbP16BYaDsXEPfep/USHOYOzHU/NfYkNdXOFDrE5LBUTzmQXSuTg5f6k2YJAm0mUF3z+t6JWtDdoZC1G5lWO4858Tlrut/BVRESmJhfKdwnX+8IuxgU0r3YEjSulzXeCgtSecfag7cR4+J3JGT9zCx2H8kjDUmq/9BDU7aQzq6GzqZP1Pl+WzAHVE47jE6RbVOUXRXG4Uaf4L8WhFW0OoLnOphJrz7/Yu2HrvZs9ulTXafWYOUsPqUjEFweHaE+z9D5VIu14XSK7FGRPwHlly6CTwVa0a5JwGnpdwOcODs2oIGxHjjKQ8=";
				}
			}
			profile = Bedwars.getGameProfile(uuid, name, value, signature);
		}
	}
	
	public int getCount() {
		
		return count;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		
		Player p = event.getPlayer();
		Inventory inv = p.getInventory();
		BukkitScheduler scheduler = Bukkit.getScheduler();
		String realName = p.getName();
		Bedwars main = Bedwars.getMain();
    	Scoreboard board = Bedwars.getScoreboard();
    	Bedwars.getTimeJoined().put(realName, LocalTime.now().toSecondOfDay());
		Location loc;
		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.setScoreboard(board);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setLevel(0);
		p.setExp(0);
		p.spigot().respawn();
		
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
		skullMeta.setDisplayName(realName);
		
		scheduler.runTaskAsynchronously(main, new Runnable() {
			
			@Override
			public void run() {
				
				MySQL mysql = Bedwars.getMySQL();
				HashMap<String, Boolean> gommeShop = Bedwars.getGommeShop();
				String realUUID = p.getUniqueId().toString();
				
				if (mysql.playerExistsInGommeShop(realUUID)) {
					
					gommeShop.put(realName, mysql.isGommeShopEnabled(realUUID));
					
				} else {
					
					gommeShop.put(realName, false);
				}
				String valueTeleporter = null;
				String signatureTeleporter = null;
				
				if (name.equalsIgnoreCase(realName)) {
					
					valueTeleporter = value;
					signatureTeleporter = signature;
					
				} else {
					
					try {
						
		                URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + realUUID.replace("-", "") + "?unsigned=false");
		                URLConnection uc = url.openConnection();
		                uc.setUseCaches(false);
		                uc.addRequestProperty("User-Agent", "Mozilla/5.0");
		                uc.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
		                uc.addRequestProperty("Pragma", "no-cache");
		                Scanner scanner = new Scanner(uc.getInputStream(), "UTF-8");
		                scanner.useDelimiter("\\A");
		                JSONParser parser = new JSONParser();
		                JSONObject obj = (JSONObject) parser.parse(scanner.next());
		                JSONArray properties = (JSONArray) obj.get("properties");
		                
		                for (int i = 0; i < properties.size(); i++) {
		                	
		                    JSONObject property = (JSONObject) properties.get(i);
		                    valueTeleporter = (String) property.get("value");
		                    signatureTeleporter = (String) property.get("signature");
		                }
		                scanner.close();
						
					} catch (IOException | ParseException e) {
						
						valueTeleporter = "eyJ0aW1lc3RhbXAiOjE1MDkzOTA4NTA3MzQsInByb2ZpbGVJZCI6IjYwNmUyZmYwZWQ3NzQ4NDI5ZDZjZTFkMzMyMWM3ODM4IiwicHJvZmlsZU5hbWUiOiJNSEZfUXVlc3Rpb24iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzUxNjNkYWZhYzFkOTFhOGM5MWRiNTc2Y2FhYzc4NDMzNjc5MWE2ZTE4ZDhmN2Y2Mjc3OGZjNDdiZjE0NmI2In19fQ==";
						signatureTeleporter = "MxJpmqEF11HRd7emaUSPYix5jxRJ04UFA9cHIBqL6pflk54b9u0vmgyJPb6y2kkz3mseiU8Wmyzjm+cYnuY3yZxTDj2YMrAyXh3CI8zwA6v8HPuBiRPppaDv1QkqZfZDeiw0oZiFz3ORt4BzevI9WYc4FolI564uqGSVBtk2iHJ6MW4rvIszIxsYWeNaasIBMNmf4q3EylvrAGMXFgGIkK3ERfChbDjjfvKd0AgVKaQNn3e489KZDs0hDl4/lamtfNiNICMb3dC4kIK1ef1qYnkeufq/0X8ts3y6YzMZ6J1BnJAQycGQVf9gA4LtANlH1aIkUV90siFFj7DHA4jfrJe3U+W9V1JGHhOkg7ynaIJM9evQt8NkZ1jbP16BYaDsXEPfep/USHOYOzHU/NfYkNdXOFDrE5LBUTzmQXSuTg5f6k2YJAm0mUF3z+t6JWtDdoZC1G5lWO4858Tlrut/BVRESmJhfKdwnX+8IuxgU0r3YEjSulzXeCgtSecfag7cR4+J3JGT9zCx2H8kjDUmq/9BDU7aQzq6GzqZP1Pl+WzAHVE47jE6RbVOUXRXG4Uaf4L8WhFW0OoLnOphJrz7/Yu2HrvZs9ulTXafWYOUsPqUjEFweHaE+z9D5VIu14XSK7FGRPwHlly6CTwVa0a5JwGnpdwOcODs2oIGxHjjKQ8=";
					}
				}
				head.setItemMeta(Bedwars.setTexture(skullMeta, realUUID, realName, valueTeleporter, signatureTeleporter));
				Bedwars.addToTeleporter(head);
				mysql.updateUUID(realUUID, realName);
			}
		});
        
        if (Bedwars.isGameStarted()) {
        	
        	event.setJoinMessage(null);
        	Bedwars.spectate(p);
        	
        } else {
        	
        	loc = new Location(Bukkit.getWorlds().get(0), 28.5, 118, -7.5, 90, 0);
        	Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
        	int onlinePlayers = Bukkit.getOnlinePlayers().size();
        	int oldOnlinePlayers = onlinePlayers - 1;
        	event.setJoinMessage("§a[+] §r" + p.getDisplayName());
        	p.setAllowFlight(false);
        	p.setGameMode(GameMode.SURVIVAL);
        	Bedwars.clearInventoryCompletely(p);
        	Bedwars.giveLobbyItems(inv, p);
            p.teleport(loc);
            
            if (!name.equals("§")) {
            	
            	CraftPlayer cp = (CraftPlayer) p;
            	PlayerConnection connection = cp.getHandle().playerConnection;
            	
            	PacketPlayOutPlayerInfo tablist = new PacketPlayOutPlayerInfo();
            	Class<?> packetClass = tablist.getClass();
            	
            	try {
            		
                	Field fAction = packetClass.getDeclaredField("a");
                	fAction.setAccessible(true);
                	fAction.set(tablist, EnumPlayerInfoAction.ADD_PLAYER);
                	Field f = packetClass.getDeclaredField("b");
                	f.setAccessible(true);
            		PlayerInfoData data = tablist.new PlayerInfoData(profile, 1, EnumGamemode.NOT_SET, ChatSerializer.a("{\"text\":\"" + name + "\"}"));
            		ArrayList<PlayerInfoData> infoData = new ArrayList<PlayerInfoData>();
            		infoData.add(data);
                	f.set(tablist, infoData);
                	connection.sendPacket(tablist);
                	
                	PacketPlayOutNamedEntitySpawn spawnEntity = new PacketPlayOutNamedEntitySpawn();
                	packetClass = spawnEntity.getClass();
                	int entityID = Bedwars.getEntityID();
                	f = packetClass.getDeclaredField("a");
                	f.setAccessible(true);
                	f.set(spawnEntity, entityID);
                	f = packetClass.getDeclaredField("i");
                	f.setAccessible(true);
                	DataWatcher watcher = new DataWatcher(null);
                	watcher.a(0, (byte) 0);
                	f.set(spawnEntity, watcher);
                	f = packetClass.getDeclaredField("b");
                	f.setAccessible(true);
                	f.set(spawnEntity, uuid);
                	f = packetClass.getDeclaredField("c");
                	f.setAccessible(true);
                	f.set(spawnEntity, -176);
                	f = packetClass.getDeclaredField("d");
                	f.setAccessible(true);
                	f.set(spawnEntity, 3616);
                	f = packetClass.getDeclaredField("e");
                	f.setAccessible(true);
                	f.set(spawnEntity, -80);
                	f = packetClass.getDeclaredField("f");
                	f.setAccessible(true);
                	f.set(spawnEntity, (byte) -48);
                	f = packetClass.getDeclaredField("g");
                	f.setAccessible(true);
                	f.set(spawnEntity, (byte) 14);
                	f = packetClass.getDeclaredField("h");
                	f.setAccessible(true);
                	f.set(spawnEntity, 0);
                	connection.sendPacket(spawnEntity);
                	
                	PacketPlayOutEntityHeadRotation headRotation = new PacketPlayOutEntityHeadRotation();
                	packetClass = headRotation.getClass();
                	f = packetClass.getDeclaredField("a");
                	f.setAccessible(true);
                	f.set(headRotation, entityID);
                	f = packetClass.getDeclaredField("b");
                	f.setAccessible(true);
                	f.set(headRotation, (byte) -34);
                	connection.sendPacket(headRotation);
            		
            		scheduler.runTaskLater(main, new Runnable() {
        				
        				@Override
        				public void run() {
        					
        	            	try {
        	            		
								fAction.set(tablist, EnumPlayerInfoAction.REMOVE_PLAYER);
								
							} catch (IllegalArgumentException | IllegalAccessException e) {
								
								e.printStackTrace();
							}
        	            	connection.sendPacket(tablist);
        				}
        			}, 10);
            		
            	} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            		
            		e.printStackTrace();
            	}
            }
            
        	if (onlinePlayers == 16) {
        		
        		Bedwars.start();
        		
        	} else if (onlinePlayers == 8) {
        		
        		Bukkit.broadcastMessage("§6[§3Bedwars§6] §aCountdown startet...");
        		board.resetScores("§l§7» §c7§7/§a16");
        		ob.getScore("§l§7» §a8§7/§a16").setScore(7);
        		
        		count = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
					
					@Override
					public void run() {
						
						int countdown = Bedwars.getCountdown() - 1;
						Bedwars.setCountdown(countdown);
						
						if (countdown == 0) {
							
							Bedwars.start();
							
						} else {
							
							Bedwars.updateCountdown();
						}
					}
				}, 20, 20);
        		
        	} else if (onlinePlayers < 8) {
        		
        		board.resetScores("§l§7» §c" + oldOnlinePlayers + "§7/§a16");
        		ob.getScore("§l§7» §c" + onlinePlayers + "§7/§a16").setScore(7);
        		
        	} else {
        		
        		board.resetScores("§l§7» §a" + oldOnlinePlayers + "§7/§a16");
        		ob.getScore("§l§7» §a" + onlinePlayers + "§7/§a16").setScore(7);
        	}
        }
	}
}
