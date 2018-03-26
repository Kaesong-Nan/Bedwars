package de.tubeplay.bedwars;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import de.tubeplay.bedwars.commands.*;
import de.tubeplay.bedwars.listener.*;
import de.tubeplay.bedwars.MySQL;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class Bedwars extends JavaPlugin {
	
	/* BUGS
	 * Nach dem Senden in eine anderes Team, kann man nichts mehr aufsammeln?
	 */
	
	private static boolean gameStarted = false;
	private static boolean gameStopped = false;
	private static boolean statsDisabled = false;
	private static HashMap<String, String> alreadyVoted = new HashMap<String, String>();
	private static String map = null;
	private static Scoreboard board = null;
	private static Team spectator = null;
	private static Team red = null;
	private static Team green = null;
	private static Team blue = null;
	private static Team yellow = null;
	private static byte votesRandomMap1 = 0;
	private static byte votesRandomMap2 = 0;
	private static byte votesRandomMap3 = 0;
	private static Random random = new Random();
	private static Vector<String> randomMaps = new Vector<String>();
	private static boolean redTeamBed = true;
	private static boolean greenTeamBed = true;
	private static boolean blueTeamBed = true;
	private static boolean yellowTeamBed = true;
	private static HashSet<String> buildModePlayers = new HashSet<String>(); 
	private static HashMap<Block, Byte> redTeamTraps = new HashMap<Block, Byte>(); 
	private static HashMap<Block, Byte> greenTeamTraps = new HashMap<Block, Byte>();
	private static HashMap<Block, Byte> blueTeamTraps = new HashMap<Block, Byte>();
	private static HashMap<Block, Byte> yellowTeamTraps = new HashMap<Block, Byte>();
	private static int countdown = 60;
	private static int originalCountdown = 60;
	private static Logger logger = Bukkit.getLogger();
	private static MySQL mysql = null;
	private static HashMap<String, Integer> destroyedBeds = new HashMap<String, Integer>();
	private static HashMap<String, Integer> kills = new HashMap<String, Integer>();
	private static HashMap<String, Integer> timeJoined = new HashMap<String, Integer>();
	private static HashSet<String> playersWithBonusPoints = new HashSet<String>(); 
	private static HashMap<String, Boolean> gommeShop = new HashMap<String, Boolean>();
	private static JoinEvent joinEvent = null;
	private static InteractEvent interactEvent = new InteractEvent();
	private static int entityID = random.nextInt(10000);
	private static ItemStack gommeSwitch = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
	private static Bedwars main = null;
	
	@Override
	public void onEnable() {
		
		PluginManager pluginManager = Bukkit.getPluginManager();
		mysql = new MySQL(getConfig().getString("MySQL-password"));
		joinEvent = new JoinEvent();
		main = this;
		
		//Registriert Listener
		pluginManager.registerEvents(joinEvent, this);
		pluginManager.registerEvents(interactEvent, this);
		pluginManager.registerEvents(new LoginEvent(), this);
		pluginManager.registerEvents(new BreakEvent(), this);
		pluginManager.registerEvents(new ClickEvent(), this);
		pluginManager.registerEvents(new DropItemEvent(), this);
		pluginManager.registerEvents(new PlaceEvent(), this);
		pluginManager.registerEvents(new LevelChangeEvent(), this);
		pluginManager.registerEvents(new DamageEvent(), this);
		pluginManager.registerEvents(new ChatEvent(), this);
		pluginManager.registerEvents(new DamageByEntityEvent(), this);
		pluginManager.registerEvents(new PickupItemEvent(), this);
		pluginManager.registerEvents(new QuitEvent(), this);
		pluginManager.registerEvents(new MoveEvent(), this);
		pluginManager.registerEvents(new DeathEvent(), this);
		pluginManager.registerEvents(new InteractEntityEvent(), this);
		pluginManager.registerEvents(new BurnEvent(), this);
		pluginManager.registerEvents(new IgniteEvent(), this);
		pluginManager.registerEvents(new ChangeEvent(), this);
		pluginManager.registerEvents(new ItemEvent(), this);
		pluginManager.registerEvents(new CanBuildEvent(), this);
		pluginManager.registerEvents(new HitEvent(), this);
		pluginManager.registerEvents(new ExplodeEvent(), this);
		pluginManager.registerEvents(new CloseEvent(), this);
		
		//Registriert Befehle
	    getCommand("start").setExecutor(new StartCommand());
	    getCommand("build").setExecutor(new BuildCommand());
	    getCommand("see").setExecutor(new SeeCommand());
	    getCommand("send").setExecutor(new SendCommand());
	    getCommand("gm").setExecutor(new GmCommand());
	    getCommand("map").setExecutor(new MapCommand());
	    getCommand("stats").setExecutor(new StatsCommand());
	    getCommand("top").setExecutor(new TopCommand());
	    getCommand("spawn").setExecutor(new SpawnCommand());
	    getCommand("playtime").setExecutor(new PlaytimeCommand());
	    getCommand("disablestats").setExecutor(new DisableStatsCommand());
	    AllCommand allCommand = new AllCommand();
	    getCommand("all").setExecutor(allCommand);
	    getCommand("a").setExecutor(allCommand);
	    
	    World world = Bukkit.getWorlds().get(0);
	    world.setSpawnFlags(false, false);
		world.setGameRuleValue("doDaylightCycle", "false");
		
        for (byte i = 0; i < 3; i++) {
			
			String randomMap = getRandomMap();
			
			if (i != 0 && randomMaps.contains(randomMap)) {
					
				i--;
				
			} else {
				
				randomMaps.add(randomMap);
			}
		}
        
		//Config
		reloadConfig();
		getConfig().options().copyDefaults(true);
		saveConfig();
        
        //Scoreboard
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective ob = board.registerNewObjective("Status", "Dummy");
		ob.setDisplaySlot(DisplaySlot.SIDEBAR);
		ob.setDisplayName("  §7§l» §aTubePlay §7«");
		ob.getScore("§1").setScore(9);
		ob.getScore("§eOnline§7:").setScore(8);
		ob.getScore("§l§7» §c0§7/§a16").setScore(7);
		ob.getScore("§2").setScore(6);
		ob.getScore("§eStart§7:").setScore(5);
		ob.getScore("§l§7» §c60").setScore(4);
		
		spectator = board.registerNewTeam("ZSpectator");
		spectator.setPrefix("§8");
		spectator.setCanSeeFriendlyInvisibles(true);
		
		red = board.registerNewTeam("Rot");
		red.setPrefix("§4");
		red.setAllowFriendlyFire(false);
		
		green = board.registerNewTeam("Grün");
		green.setPrefix("§2");
		green.setAllowFriendlyFire(false);
		
		blue = board.registerNewTeam("Blau");
		blue.setPrefix("§1");
		blue.setAllowFriendlyFire(false);
		
		yellow = board.registerNewTeam("Gelb");
		yellow.setPrefix("§e");
		yellow.setAllowFriendlyFire(false);
		
		//Top 5 Monthly und Top 5 Daily Wand
		ResultSet rs = mysql.query("SELECT UUID FROM StatsMonthly ORDER BY Points DESC LIMIT 5");
		
		for (byte i = 1; i < 6; i++) {
			
			try {
				
				if (rs.next()) {
					
					String uuid = rs.getString("UUID");
					placeHeadAndSign(uuid, 116, i, "Monthly");
					
				} else {
					
					removeHeadAndSign(116, i);
				}
				
			} catch (SQLException e) {
				
				mysql.errorReadingDatabase(e);
			}
		}
		
		rs = mysql.query("SELECT UUID FROM StatsDaily ORDER BY Points DESC LIMIT 5");
		
		for (byte i = 1; i < 6; i++) {
			
			try {
				
				if (rs.next()) {
					
					String uuid = rs.getString("UUID");
					placeHeadAndSign(uuid, 114, i, "Daily");
					
				} else {
					
					removeHeadAndSign(114, i);
				}
				
			} catch (SQLException e) {
				
				mysql.errorReadingDatabase(e);
			}
		}
		
		//Erstellt ItemStack
        SkullMeta skullMetaGommeSwitch = (SkullMeta) gommeSwitch.getItemMeta();
        skullMetaGommeSwitch.setDisplayName("§6Zum normalen Shop wechseln");
        gommeSwitch.setItemMeta(setTexture(skullMetaGommeSwitch, "e9013c2f-da01-425f-a48b-516f55e94386", "GommeHD", "eyJ0aW1lc3RhbXAiOjE1MDk3NDI1MDkxNTEsInByb2ZpbGVJZCI6ImU5MDEzYzJmZGEwMTQyNWZhNDhiNTE2ZjU1ZTk0Mzg2IiwicHJvZmlsZU5hbWUiOiJHb21tZUhEIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS84ZWE0ZDYxMjQ4MTMyZDAxY2JlYzU0YWZmNWNkNDk5YzcyMTg5YzFiYmVlZGFhNWEzMTczM2QxYTdiZGFjNCJ9LCJDQVBFIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWMzY2E3ZWUyYTQ5OGYxYjVkMjU4ZDVmYTkyN2U2M2U0MzMxNDNhZGQ1NTM4Y2Y2M2I2YTliNzhhZTczNSJ9fX0=", "AYFIeYgMZxVE2RymevTmuBkNrzOm5bOPhfbEyL8+LPCFwHTCFKu+0qc0UZPPB1yXQdt2LQjT7G3xWK17/TB2AgtczFNy26dfF/SYQuhRBsO23JBmOaPVSEPEPVr2+y+PL73Lh8sMFCbQ6YwAeG6ss3R1ZJPeKkoqLfCUa1jtSipQ7ngIKQGARHfXw3xo8V+n6FeCI4nZZQPO5OrZGz2nKTuqGEXI+BRoTK23VIA8qB0uyS9sX7X65prAXNZeOsTfmyFbAIea1h/mvRm42N5Py93ffrzA8iCV/4Omi816bRHhLjyHNenDmBqmjYacM9cS+X0uQcm21plaRr5c7aV8fGvAcrBmY/uP0v2kvvPcTDoLg0q9eW0d9hUDMT7j/Y4uxxdUuVI/zEr7CDAq39IeXS1E6ezY+Q+q/vgSM1dN+nUeT2pegZTgXOg1gZFYCyE8s2LWr7tcwmReY6f1nQyd9tHOtmArCiAh6+D6dTUTETt5qBs33lRGWwBGk7D0v0WTteLIB8sxpjEviPcaDKsU8kxQvoTyrRKbJUdTbW1QGjGrPZlB5qK+iMI9YSolZhBaJlxGZu6DYSXaIdOa71QAzcKj985a62yw/WtL+Y2H0XBpMmfffPLtv1l2DkIMA++4gMori0wXw9Qwud13SoAkXgDFtacJ98x7VEIclr3koek="));
	}
	
	@Override
	public void onDisable() {
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			calculateMinutesOnline(op);
		}
		
        if (isGameStarted()) {
        	
        	for (Player op : Bukkit.getOnlinePlayers()) {
        		
        		Location loc =  new Location(Bukkit.getWorlds().get(0), 28.5, 118, -7.5, 90, 0);
        		op.teleport(loc);
        	}
			getServer().unloadWorld(map, true);
			
			File fWorld = new File(map);
			File backup = new File("backup_" + map);
			
			logger.info("Setze Welt zurück...");
			
			try {
				
				FileUtils.deleteDirectory(fWorld);
				FileUtils.moveDirectory(backup, fWorld);
				
			} catch (IOException e) {
				
				logger.severe("Welt konnte nicht erfolgreich zurückgesetzt werden!");
				e.printStackTrace();
			}
		}
		mysql.disconnect();
	}
	
	public static Bedwars getMain() {
		
		return main;
	}
	
	public static ItemStack getGommeSwitch() {
		
		return gommeSwitch;
	}
	
	public static void setStatsDisabled(boolean disable) {
		
		statsDisabled = disable;
	}
	
	public static boolean isStatsDisabled() {
		
		return statsDisabled;
	}
	
	public static int getEntityID() {
		
		return entityID;
	}
	
	public static HashSet<String> getPlayersWithBonusPoints() {
		
		return playersWithBonusPoints;
	}
	
	public static HashMap<String, Integer> getTimeJoined() {
		
		return timeJoined;
	}
	
	public static HashMap<String, Integer> getDestroyedBeds() {
		
		return destroyedBeds;
	}
	
	public static int getVotesRandomMap1() {
		
		return votesRandomMap1;
	}
	
	public static int getVotesRandomMap2() {
		
		return votesRandomMap2;
	}
	
	public static int getVotesRandomMap3() {
		
		return votesRandomMap3;
	}
	
	public static void addToTeleporter(ItemStack head) {
		
		interactEvent.addToTeleporter(head);
	}
	
	public static HashMap<String, Integer> getKills() {
		
		return kills;
	}
	
	public static HashMap<Block, Byte> getRedTeamTraps() {
		
		return redTeamTraps;
	}
	
	public static HashMap<Block, Byte> getGreenTeamTraps() {
		
		return greenTeamTraps;
	}
	
	public static HashMap<Block, Byte> getBlueTeamTraps() {
		
		return blueTeamTraps;
	}
	
	public static HashMap<Block, Byte> getYellowTeamTraps() {
		
		return yellowTeamTraps;
	}
	
	public static MySQL getMySQL() {
		
		return mysql;
	}
	
	public static int getCountdown() {
		
		return countdown;
	}
	
	public static void setCountdown(int seconds) {
		
		countdown = seconds;
	}
	
	public static void updateCountdown() {
		
		Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
		
		for (String entry : board.getEntries()) {
			
            if (ob.getScore(entry).getScore() == 4) {
				
            	board.resetScores(entry);
			}
		}
		
		if (countdown < 21) {
			
			ob.getScore("§l§7» §a" + countdown).setScore(4);
			
		} else if (countdown < 41) {
			
			ob.getScore("§l§7» §6" + countdown).setScore(4);
			
		} else if (countdown < 61) {
			
			ob.getScore("§l§7» §c" + countdown).setScore(4);
			
		} else {
			
			ob.getScore("§l§7» §4" + countdown).setScore(4);
		}
	}
	
    public static int getOriginalCountdown() {
		
		return originalCountdown;
	}
	
	public static void setOriginalCountdown(int seconds) {
		
		originalCountdown = seconds;
		countdown = originalCountdown;
		updateCountdown();
	}
	
	public static void setRedBed(boolean bed) {
		
		redTeamBed = bed;
	}
	
    public static void setGreenBed(boolean bed) {
		
		greenTeamBed = bed;
	}
    
    public static void setBlueBed(boolean bed) {
		
		blueTeamBed = bed;
	}
    
    public static void setYellowBed(boolean bed) {
		
		yellowTeamBed = bed;
	}
    
    public static HashMap<String, Boolean> getGommeShop() {
    	
    	return gommeShop;
    }
	
	public static boolean isRedTeamBed() {
		
		return redTeamBed;
	}
	
    public static boolean isGreenTeamBed() {
		
		return greenTeamBed;
	}
    
    public static boolean isBlueTeamBed() {
		
		return blueTeamBed;
	}
    
    public static boolean isYellowTeamBed() {
		
		return yellowTeamBed;
	}
	
    public static HashSet<String> getBuildModePlayers() {
    	
    	return buildModePlayers;
    }
    
	public static Vector<String> getRandomMaps() {
		
		return randomMaps;
	}
	
	public static void vote(String map, Player p) {
		
    	map = map.replace("§3", "");
    	String uuid = p.getUniqueId().toString();
    	
		if (map.equalsIgnoreCase(randomMaps.get(0))) {
			
			votesRandomMap1++;
			
		} else if (map.equalsIgnoreCase(randomMaps.get(1))) {
			
			votesRandomMap2++;
			
		} else {
			
			votesRandomMap3++;
		}
		
		if (alreadyVoted.containsKey(uuid)) {
			
			String mapVotedBefore = alreadyVoted.get(uuid);
			
			if (mapVotedBefore.equalsIgnoreCase(map)) {
				
				p.sendMessage("§6[§3Bedwars§6] §cDu hast bereits für diese Karte abgestimmt!");
				return;
				
			} else {
				
				if (mapVotedBefore.equalsIgnoreCase(randomMaps.get(0))) {
					
					votesRandomMap1--;
					
				} else if (mapVotedBefore.equalsIgnoreCase(randomMaps.get(1))) {
					
					votesRandomMap2--;
					
				} else {
					
					votesRandomMap3--;
				}
				p.sendMessage("§6[§3Bedwars§6] §aDa du vorher bereits abgestimmt hast, wurde deine vorherige Stimme wieder entfernt!");
			}
		}
		p.closeInventory();
		alreadyVoted.put(p.getUniqueId().toString(), map);
		p.sendMessage("§6[§3Bedwars§6] §aDu hast erfolgreich für die Karte §6" + map + " §aabgestimmt!");
	}
	
	public static void cancelCountdown() {
		
		Bukkit.getScheduler().cancelTask(joinEvent.getCount());
	}
	
	public static String getMap() {
		
		return map;
	}
	
    public static boolean isGameStarted() {
		
		return gameStarted;
	}
    
    public static boolean isGameStopped() {
    	
    	return gameStopped;
    }
    
    public static Scoreboard getScoreboard() {
    	
    	return board;
    }
    
    public static void setMap(String forcedMap) {
    	
    	map = forcedMap.replace("§3", "");
    	Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDie Karte §6" + map + " §awurde erzwungen!");
    }
    
    public static SkullMeta setTexture(SkullMeta meta, String uuid, String name, String value, String signature) {
		
		try {
			
			Field profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, getGameProfile(UUID.fromString(uuid), name, value, signature));
			
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			
			e.printStackTrace();
		}
		return meta;
    }
    
    public static GameProfile getGameProfile(UUID uuid, String name, String value, String signature) {
    	
		GameProfile profile = new GameProfile(uuid, name);
		Property property = new Property("textures", value, signature);
		profile.getProperties().put("textures", property);
		return profile;
    }
    
	public static Player isPlayerOnline(String arg, CommandSender sender) {
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			if (op.getName().equalsIgnoreCase(arg)) {
				
				return op;
			}
		}
		sender.sendMessage("§6[§3Tribes§6] §cDer Spieler §6" + arg + " §ckonnte nicht gefunden wernden!");
		return null;
	}
    
	public static void spectate(Player p) {
		
		String name = p.getName();
		Inventory inv = p.getInventory();
		Location loc;
		p.setDisplayName("§7" + name + "§r");
		spectator.addEntry(name);
		PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1);
		p.addPotionEffect(invisibility);
		p.setGameMode(GameMode.ADVENTURE);
		p.setAllowFlight(true);
		p.setFlying(true);
		p.spigot().setCollidesWithEntities(false);
		clearInventoryCompletely(p);
		
		//Gibt dem Spieler das Item, mit dem er zurück zur Lobby kommt
		ItemStack lobby = new ItemStack(Material.WOOL, 1, (byte) 14);
		ItemMeta meta = lobby.getItemMeta();
		meta.setDisplayName("§4§lZurück zur Lobby");
		lobby.setItemMeta(meta);
		inv.setItem(8, lobby);
		
		//Gibt dem Spieler das Item, mit dem er sich zu anderen Spielern teleportieren kann
		ItemStack teleporter = new ItemStack(Material.COMPASS);
		meta.setDisplayName("§6Teleporter");
		teleporter.setItemMeta(meta);
		inv.setItem(0, teleporter);
		
        for (Player op : Bukkit.getOnlinePlayers()) {
			
			if (!op.getAllowFlight()) {
				
				op.hidePlayer(p);
			}
			p.showPlayer(op);
		}
        
        switch (map) {
        
        case "Pilz":
        	
    		loc = new Location(Bukkit.getWorld(map), 1.5, 81, 67.5);
    		break;
    		
        case "Phizzle":
        	
        	loc = new Location(Bukkit.getWorld(map), -4.5, 113, 57.5);
        	break;
        	
        case "Atlantis":
        	
        	loc = new Location(Bukkit.getWorld(map), -85.5, 115, 1.5);
        	break;
        	
        case "Industry":
        	
    		loc = new Location(Bukkit.getWorld(map), 79.5, 104, 0.5);
    		break;
    		
        case "Sonic":
        	
    		loc = new Location(Bukkit.getWorld(map), 0.5, 81, 0.5);
    		break;
    		
        case "Frozen":
        	
    		loc = new Location(Bukkit.getWorld(map), 34.5, 127, 6.5);
    		break;
    		
        default:
        	
    		loc = new Location(Bukkit.getWorld(map), 42.5, 66, 41.5);
    		break;
        }
    	p.teleport(loc);
	}
    
	public static void clearInventoryCompletely(Player p) {
		
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
	}
	
	public static void findOutIfSomeoneWon(int redTeamPlayers, int greenTeamPlayers, int blueTeamPlayers, int yellowTeamPlayers) {
		
		if (greenTeamPlayers == 0 && blueTeamPlayers == 0 && yellowTeamPlayers == 0) {
			
			shutdown("§4Rot");
			
		} else if (redTeamPlayers == 0 && greenTeamPlayers == 0 && blueTeamPlayers == 0) {
			
			shutdown("§eGelb");
			
		} else if (redTeamPlayers == 0 && blueTeamPlayers == 0 && yellowTeamPlayers == 0) {
			
			shutdown("§2Grün");
			
		} else if (redTeamPlayers == 0 && greenTeamPlayers == 0 && yellowTeamPlayers == 0) {
			
			shutdown("§1Blau");
		}
	}
	
	public static String getMapBuilder() {
		
    	String mapBuilder;
    	
    	switch(map) {
    	
    	case "Pilz":
    		
    		mapBuilder = "Truuz";
    		break;
    		
    	case "Phizzle":
    		
    		mapBuilder = "Phizzle";
    		break;
    		
    	case "Sonic":
    		
    		mapBuilder = "SimMicKie";
    		break;
    		
    	case "Skyland":
    		
    		mapBuilder = "Nikita_Banane";
    		break;
    		
    	default:
    		
    		mapBuilder = "Toventox";
    		break;
    	}
    	return mapBuilder;
	}
	
    public static void start() {
    	
    	cancelCountdown();
    	Bukkit.broadcastMessage("§6[§3Bedwars§6] §aRunde startet...");
    	
    	if (map == null) {
    		
    		if (votesRandomMap1 > votesRandomMap2 && votesRandomMap1 > votesRandomMap3) {
        		
        		map = randomMaps.get(0);
        		
        	} else if (votesRandomMap2 > votesRandomMap1 && votesRandomMap2 > votesRandomMap3) {
        		
        		map = randomMaps.get(1);
        		
        	} else if (votesRandomMap3 > votesRandomMap1 && votesRandomMap3 > votesRandomMap2) {
        		
        		map = randomMaps.get(2);
        		
        	} else {
        		
    			Bukkit.broadcastMessage("§6[§3Bedwars§6] §cEs gab kein eindeutiges Ergebnis bei der Abstimmung!\n"
    					+ "§6[§3Bedwars§6] §aZufällige Karte wird ausgewählt...");
    			map = getRandomMap();
        	}
    	}
    	Bukkit.broadcastMessage("§6[§3Bedwars§6] §aEs wird die Karte §6" + map + " §avon §3" + getMapBuilder() + " §agespielt!");
    	WorldCreator creator = new WorldCreator(map);
    	World world = Bukkit.createWorld(creator);
    	world.setGameRuleValue("doDaylightCycle", "false");
    	world.setGameRuleValue("keepInventory", "true");
    	world.setSpawnFlags(false, false);
		
		File fWorld = new File(map);
		File backup = new File("backup_" + map);
    	
		logger.info("Erstelle Backup der gewählten Welt...");
		
		try {
			
			FileUtils.copyDirectory(fWorld, backup);
			
		} catch (IOException e) {
			
			logger.severe("Es konnte kein Backup der Welt erstellt werden!");
			e.printStackTrace();
		}
		
    	Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
    	int redTeamPlayers = red.getSize();
    	int greenTeamPlayers = green.getSize();
    	int blueTeamPlayers = blue.getSize();
    	int yellowTeamPlayers = yellow.getSize();
    	BukkitScheduler scheduler = Bukkit.getScheduler();
    	gameStarted = true;
    	PacketPlayOutEntityDestroy entityDestroy = new PacketPlayOutEntityDestroy(entityID);
		
        for (Player op : onlinePlayers) {
    		
        	String realName = op.getName();
        	String name = op.getDisplayName();
        	CraftPlayer cp = (CraftPlayer) op;
        	cp.getHandle().playerConnection.sendPacket(entityDestroy);
    		op.closeInventory();
    		op.getInventory().clear();
    		op.setFireTicks(0);
			op.setFallDistance((float) 0);
			
			if (!statsDisabled) {
				
				scheduler.runTaskAsynchronously(main, new Runnable() {
					
					@Override
					public void run() {
						
						mysql.addToPlayedGames(op.getUniqueId().toString());
					}
				});
			}
    		
            if (buildModePlayers.contains(realName)) {
				
				buildModePlayers.remove(realName);
				op.sendMessage("§6[§3Bedwars§6] §cDu bist nun nicht mehr im Bau-Modus!");
				op.setGameMode(GameMode.SURVIVAL);
            }
			
            if (!name.contains("§")) {
				
				String newName;
				
				if (redTeamPlayers <= greenTeamPlayers && redTeamPlayers <= blueTeamPlayers && redTeamPlayers <= yellowTeamPlayers) {
					
					redTeamPlayers++;
					newName = "§4" + name + "§r";
					red.addEntry(name);
					
				} else if (map.equalsIgnoreCase("industry") || map.equalsIgnoreCase("frozen") || map.equalsIgnoreCase("skyland")) {
					
					if (greenTeamPlayers <= blueTeamPlayers && greenTeamPlayers <= yellowTeamPlayers) {
						
						greenTeamPlayers++;
						newName = "§2" + name + "§r";
						green.addEntry(name);
						
					} else if (blueTeamPlayers <= yellowTeamPlayers) {
						
						blueTeamPlayers++;
						newName = "§1" + name + "§r";
						blue.addEntry(name);
						
					} else {
						
						yellowTeamPlayers++;
						newName = "§e" + name + "§r";
						yellow.addEntry(name);
					}
					
				} else {
					
					if (yellowTeamPlayers <= greenTeamPlayers && yellowTeamPlayers <= blueTeamPlayers) {
						
						yellowTeamPlayers++;
						newName = "§e" + name + "§r";
						yellow.addEntry(name);
						
					} else if (greenTeamPlayers <= blueTeamPlayers) {
						
						greenTeamPlayers++;
						newName = "§2" + name + "§r";
						green.addEntry(name);
						
					} else {
						
						blueTeamPlayers++;
						newName = "§1" + name + "§r";
						blue.addEntry(name);
					}
				}
				op.setDisplayName(newName);
			}
        	op.teleport(Bedwars.getSpawnLocation(op.getDisplayName()));
    	}
        board.clearSlot(DisplaySlot.SIDEBAR);
        Objective ob = board.registerNewObjective("Teams", "Players");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);
        ob.setDisplayName("  §l§7» §aTubePlay §7«");
		ob.getScore("§a✔ §4Rot").setScore(redTeamPlayers);
		ob.getScore("§a✔ §2Grün").setScore(greenTeamPlayers);
		ob.getScore("§a✔ §1Blau").setScore(blueTeamPlayers);
		ob.getScore("§a✔ §eGelb").setScore(yellowTeamPlayers);
		
        switch (map) {
        
        case "Pilz":
        	
        	new BronzeSpawner(new Location(world, -63.5, 92.5, 64.5), new Location(world, 4.5, 92.5, 2.5), new Location(world, 66.5, 92.5, 70.5), new Location(world, -2.5, 92.5, 132.5));
        	new IronSpawner(new Location(world, 2.5, 92.5, 141.5), new Location(world, -72.5, 92.5, 68.5), new Location(world, 0.5, 92.5, -6.5), new Location(world, 75.5, 92.5, 66.5));
        	new GoldSpawner(new Location(world, 1.5, 81.5, 67.5), new Location(world, 1.5, 86.5, 78.5), new Location(world, -9.5, 86.5, 67.5), new Location(world, 1.5, 86.5, 56.5), new Location(world, 12.5, 86.5, 67.5));
    		break;
    		
        case "Phizzle":
        	
        	new BronzeSpawner(new Location(world, 48.5, 110.5, 54.5), new Location(world, -7.5, 110.5, 4.5), new Location(world, -57.5, 110.5, 60.5), new Location(world, -1.5, 110.5, 110.5));
        	new IronSpawner(new Location(world, -0.5, 110.5, 119.5), new Location(world, 57.5, 110.5, 53.5), new Location(world, -8.5, 110.5, -4.5), new Location(world, -66.5, 110.5, 61.5));
        	new GoldSpawner(new Location(world, -0.5, 110.5, 61.5), new Location(world, -0.5, 110.5, 53.5), new Location(world, -9.5, 110.5, 53.5), new Location(world, -9.5, 110.5, 61.5), new Location(world, -4.5, 112.5, 57.5));
        	break;
        	
        case "Atlantis":
        	
        	new BronzeSpawner(new Location(world, -79.5, 121.5, -80.5), new Location(world, -3.5, 121.5, 7.5), new Location(world, -91.5, 121.5, 83.5), new Location(world, -167.5, 121.5, -4.5));
        	new IronSpawner(new Location(world, -105.5, 119.5, -62.5), new Location(world, -21.5, 119.5, -18.5), new Location(world, -65.5, 119.5, 65.5), new Location(world, -149.5, 119.5, 21.5));
        	new GoldSpawner(new Location(world, -85.5, 115.5, 1.5), new Location(world, -85.5, 115.5, 3.5), new Location(world, -87.5, 115.5, 1.5), new Location(world, -85.5, 115.5, -0.5), new Location(world, -83.5, 115.5, 1.5));
        	break;
        	
        case "Industry":
        	
        	new BronzeSpawner(new Location(world, 71.5, 111.5, 77.5), new Location(world, 2.5, 111.5, -7.5), new Location(world, 87.5, 111.5, -76.5), new Location(world, 156.5, 111.5, 8.5));
        	new IronSpawner(new Location(world, 156.5, 116.5, 8.5), new Location(world, 71.5, 116.5, 77.5), new Location(world, 2.5, 116.5, -7.5), new Location(world, 87.5, 116.5, -76.5));
        	new GoldSpawner(new Location(world, 80.5, 105.5, -0.5), new Location(world, 80.5, 105.5, 1.5), new Location(world, 78.5, 105.5, 1.5), new Location(world, 78.5, 105.5, -0.5));
    		break;
    		
        case "Sonic":
        	
        	new BronzeSpawner(new Location(world, 86.5, 67.5, -13.5), new Location(world, 14.5, 67.5, 86.5), new Location(world, -85.5, 67.5, 14.5), new Location(world, -13.5, 67.5, -85.5));
        	new IronSpawner(new Location(world, 43.5, 66.5, 0.5), new Location(world, 0.5, 66.5, 43.5), new Location(world, -42.5, 66.5, 0.5), new Location(world, 0.5, 66.5, -42.5));
        	new GoldSpawner(new Location(world, 3.5, 65.5, 0.5), new Location(world, 0.5, 65.5, 3.5), new Location(world, -2.5, 65.5, 0.5), new Location(world, 0.5, 65.5, -2.5));
    		break;
    		
        case "Frozen":
        	
        	new BronzeSpawner(new Location(world, 34.5, 106.5, -45.5), new Location(world, 81.5, 106.5, 3.5), new Location(world, 32.5, 106.5, 50.5), new Location(world, -14.5, 106.5, 1.5));
        	new IronSpawner(new Location(world, 25.5, 106.5, -50.5), new Location(world, 86.5, 106.5, -5.5), new Location(world, 41.5, 106.5, 55.5), new Location(world, -19.5, 106.5, 10.5));
        	new GoldSpawner(new Location(world, 29.5, 106.5, 2.5), new Location(world, 33.5, 106.5, -1.5), new Location(world, 37.5, 106.5, 2.5), new Location(world, 33.5, 106.5, 7.5));
    		break;
    		
        default:
        	
        	new BronzeSpawner(new Location(world, -3.5, 48.5, 38.5), new Location(world, 47.5, 48.5, -6.5), new Location(world, 92.5, 48.5, 44.5), new Location(world, 41.5, 48.5, 89.5));
        	new IronSpawner(new Location(world, 39.5, 46.5, 95.5), new Location(world, -9.5, 46.5, 36.5), new Location(world, 49.5, 46.5, -12.5), new Location(world, 98.5, 46.5, 46.5));
        	new GoldSpawner(new Location(world, 42.5, 48.5, 45.5), new Location(world, 46.5, 48.5, 41.5), new Location(world, 42.5, 48.5, 38.5), new Location(world, 38.5, 48.5, 41.5));
    		break;
        }
    	
    	if (redTeamPlayers == 0) {
			
    		Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDas Bett von Team §4Rot §cwurde abgebaut, da das Team keine Spieler hat!");
    		destroyBedRed();
		}
    	
        if (greenTeamPlayers == 0) {
			
    		Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDas Bett von Team §2Grün §cwurde abgebaut, da das Team keine Spieler hat!");
    		destroyBedGreen();
		}
        
        if (blueTeamPlayers == 0) {
			
        	Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDas Bett von Team §1Blau §cwurde abgebaut, da das Team keine Spieler hat!");
        	destroyBedBlue();
		}
        
        if (yellowTeamPlayers == 0) {
			
        	Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDas Bett von Team §eGelb §cwurde abgebaut, da das Team keine Spieler hat!");
    		destroyBedYellow();
		}
        
        scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
			
			@Override
			public void run() {
				
				for (Player op : onlinePlayers) {
					
					double distance = 0;
					Location nearestLocation = null;
					
					for (Player op2 : onlinePlayers) {
						
						Location opLoc = op.getLocation();
						Location op2Loc = op2.getLocation();
						
						if (opLoc.getWorld().equals(op2Loc.getWorld())) {
							
							double distanceBetweenOpAndOp2 = op.getLocation().distanceSquared(op2Loc);
							
							if (!op.getDisplayName().contains(op2.getDisplayName().substring(0, 2)) && distanceBetweenOpAndOp2 > distance) {
								
								distance = distanceBetweenOpAndOp2;
								nearestLocation = op2Loc;
							}
						}
					}
					
					if (nearestLocation != null) {
						
						op.setCompassTarget(nearestLocation);
					}
				}
			}
		}, 20, 20);
    }
    
    public static Location getRedTeamBedLocation() {
    	
    	World world = Bukkit.getWorld(map);
    	Location bedLoc;
    	
        switch (map) {
        
        case "Pilz":
        	
    		bedLoc = new Location(world, -66.5, 93, 75.5);
    		break;
    		
        case "Phizzle":
        	
        	bedLoc = new Location(world, 56.5, 111, 61.5);
        	break;
        	
        case "Atlantis":
        	
        	bedLoc = new Location(world, -85.5, 121, -90.5);
        	break;
        	
        case "Industry":
        	
        	bedLoc = new Location(world, 85.5, 113, 84.5);
    		break;
    		
        case "Sonic":
        	
        	bedLoc = new Location(world, -95.5, 71, 5.5);
    		break;
    		
        case "Frozen":
        	
        	bedLoc = new Location(world, 35.5, 105, 55.5);
    		break;
    		
        default:
        	
        	bedLoc = new Location(world, 44.5, 48, -14.5);
    		break;
        }
        return bedLoc;
    }
    
    public static Location getGreenTeamBedLocation() {
    	
    	World world = Bukkit.getWorld(map);
    	Location bedLoc;
    	
        switch (map) {
        
        case "Pilz":
        	
    		bedLoc = new Location(world, 9.5, 93, 135.5);
    		break;
    		
        case "Phizzle":
        	
        	bedLoc = new Location(world, -8.5, 111, 118.5);
        	break;
        	
        case "Atlantis":
        	
        	bedLoc = new Location(world, 6.5, 121, 1.5);
        	break;
        	
        case "Industry":
        	
        	bedLoc = new Location(world, 73.5, 113, -83.5);
    		break;
    		
        case "Sonic":
        	
        	bedLoc = new Location(world, 5.5, 71, 96.5);
    		break;
    		
        case "Frozen":
        	
        	bedLoc = new Location(world, 31.5, 105, -50.5);
    		break;
    		
        default:
        	
        	bedLoc = new Location(world, 44.5, 48, 97.5);
    		break;
        }
        return bedLoc;
    }
    
    public static Location getBlueTeamBedLocation() {
    	
    	World world = Bukkit.getWorld(map);
    	Location bedLoc;
    	
        switch (map) {
        
        case "Pilz":
        	
    		bedLoc = new Location(world, -6.5, 93, -0.5);
    		break;
    		
        case "Phizzle":
        	
        	bedLoc = new Location(world, -0.5, 111, -3.5);
        	break;
        	
        case "Atlantis":
        	
        	bedLoc = new Location(world, -177.5, 121, 1.5);
        	break;
        	
        case "Industry":
        	
        	bedLoc = new Location(world, -4.5, 113, 6.5);
    		break;
    		
        case "Sonic":
        	
        	bedLoc = new Location(world, -4.5, 71, -95.5);
    		break;
    		
        case "Frozen":
        	
        	bedLoc = new Location(world, -19.5, 105, 4.5);
    		break;
    		
        default:
        	
        	bedLoc = new Location(world, 100.5, 48, 41.5);
    		break;
        }
        return bedLoc;
    }
    
    public static Location getYellowTeamBedLocation() {
    	
    	World world = Bukkit.getWorld(map);
    	Location bedLoc;
    	
        switch (map) {
        
        case "Pilz":
        	
    		bedLoc = new Location(world, 69.5, 93, 59.5);
    		break;
    		
        case "Phizzle":
        	
        	bedLoc = new Location(world, -65.5, 111, 53.5);
        	break;
        	
        case "Atlantis":
        	
        	bedLoc = new Location(world, -85.5, 121, 93.5);
        	break;
        	
        case "Industry":
        	
        	bedLoc = new Location(world, -163.5, 113, -5.5);
    		break;
    		
        case "Sonic":
        	
        	bedLoc = new Location(world, 96.5, 71, -4.5);
    		break;
    		
        case "Frozen":
        	
        	bedLoc = new Location(world, 86.5, 105, 0.5);
    		break;
    		
        default:
        	
        	bedLoc = new Location(world, -11.5, 48, 41.5);
    		break;
        }
        return bedLoc;
    }
    
    public static void shutdown(String team) {
    	
    	gameStopped = true;
    	BukkitScheduler scheduler = Bukkit.getScheduler();
    	PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + team + "\"}"), 20, 40, 20);
    	PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, ChatSerializer.a("{\"text\":\"§ahat gewonnen!\"}"), 20, 40, 20);
		
    	for (Player op : Bukkit.getOnlinePlayers()) {
    		
    		String opRealName = op.getName();
			int opDestroyedBeds = 0;
			int opKills = 0;
			CraftPlayer cp = (CraftPlayer) op;
			PlayerConnection connection = cp.getHandle().playerConnection;
			connection.sendPacket(title);
			connection.sendPacket(subtitle);
			op.setLevel(0);
			op.setExp(0);
			op.setFallDistance((float) 0);
			
			if (destroyedBeds.containsKey(opRealName)) {
				
				opDestroyedBeds = destroyedBeds.get(opRealName);
			}
			
			if (kills.containsKey(opRealName)) {
				
				opKills = kills.get(opRealName);
			}
			int points = opDestroyedBeds * 20 + opKills * 10;
			
    		if (!(op.getAllowFlight() || statsDisabled)) {
    			
    			points += 10;
    			
    			scheduler.runTaskAsynchronously(main, new Runnable() {
    				
    				@Override
    				public void run() {
    					
    					mysql.addToPoints(op.getUniqueId().toString(), 10);
    				}
    			});
    			
    		} else if (playersWithBonusPoints.contains(opRealName)) {
    			
    			points += 10;
    		}
			
    		if (op.getDisplayName().contains(team.substring(0, 2)) && !statsDisabled) {
    			
    			scheduler.runTaskAsynchronously(main, new Runnable() {
					
					@Override
					public void run() {
						
		    			mysql.addToWonGames(op.getUniqueId().toString());
					}
				});
    			
    			points += 50;
    		}
    		
			op.sendMessage("§3Deine Stats der Runde\n"
					+ "§6Zerstörte Betten: §3" +  opDestroyedBeds + "\n"
					+ "§6Kills: §3" + opKills +"\n"
					+ "§6Punkte: §3" + points + "\n"
					+ "§6Gespielte Spiele §3+1");
			
    		if (op.getDisplayName().contains(team.substring(0, 2))) {
    			
    			op.sendMessage("§6Gewonnene Spiele §3+1");
    			
    		} else {
    			
    			op.sendMessage("§6Verlorene Spiele §3+1\n"
    					+ "§6Deaths §3+1");
    		}
    		spectate(op);
		}
    	
    	scheduler.runTaskLater(main, new Runnable() {
			
			@Override
			public void run() {
				
				Bukkit.shutdown();
			}
		}, 200);
    }
    
    public static Location getSpawnLocation(String name) {
    	
    	World world = Bukkit.getWorld(map);
    	Location loc;
    	
        switch (map) {
        
        case "Pilz":
        	
			if (name.contains("§4")) {
				
				loc = new Location(world, -66.5, 93, 67.5, -90, 0);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, 1.5, 93, 135.5, 180, 0);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, 1.5, 93, -0.5);
				
			} else {
				
				loc = new Location(world, 69.5, 93, 67.5, 90, 0);
			}
    		break;
    		
        case "Phizzle":
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, 49.5, 111, 57.5, 90, 0);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, -4.5, 111, 111.5, 180, 0);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, -4.5, 111, 3.5);
				
			} else {
				
				loc = new Location(world, -58.5, 111, 57.5, -90, 0);
			}
        	break;
        	
        case "Atlantis":
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, -85.5, 120, -81.5);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, -2.5, 120, 1.5, 90, 0);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, -168.5, 120, 1.5, -90, 0);
				
			} else {
				
				loc = new Location(world, -85.5, 120, 84.5, 180, 0);
			}
        	break;
        	
        case "Industry":
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, 80.5, 110, 76.5, 180, 0);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, 78.5, 110, -75.5);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, 3.5, 110, 1.5, -90, 0);
				
			} else {
				
				loc = new Location(world, 155.5, 110, -0.5, 90, 0);
			}
    		break;
    		
        case "Sonic":
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, -87.5, 66, -1.5, -90, 0);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, -1.5, 66, 88.5, 180, 0);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, 2.5, 66, -87.5);
				
			} else {
				
				loc = new Location(world, 88.5, 66, 2.5, 90, 0);
			}
    		break;
    		
        case "Frozen":
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, 36.5, 105.25, 49.5, 180, 0);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, 30.5, 105.25, -44.5);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, -13.5, 105.25, 5.5, -90, 0);
				
			} else {
				
				loc = new Location(world, 80.5, 105.25, -0.5, 90, 0);
			}
    		break;
    		
        default:
        	
            if (name.contains("§4")) {
				
				loc = new Location(world, 44.5, 48, -9.5);
				
			} else if (name.contains("§2")) {
				
				loc = new Location(world, 44.5, 48, 92.5, 180, 0);
				
			} else if (name.contains("§1")) {
				
				loc = new Location(world, 95.5, 48, 41.5, 90, 0);
				
			} else {
				
				loc = new Location(world, -6.5, 48, 41.5, -90, 0);
			}
    		break;
        }
    	return loc;
    }
    
    public static void giveLobbyItems(Inventory inv, Player p) {
    	
    	//Gibt dem Spieler das Item, mit dem er sich für ein Team entscheiden kann
    	ItemStack team = new ItemStack(Material.STAINED_CLAY);
    	ItemMeta meta = team.getItemMeta();
    	meta.setDisplayName("§6Teamauswahl");
    	team.setItemMeta(meta);
    	inv.setItem(0, team);
    	
    	//Gibt dem Spieler das Item, mit dem er eine Karte erzwingen kann
    	if (p.hasPermission("tubeplay.forcemap") && getMap() == null || p.hasPermission("tubeplay.forcemap.admin")) {
    		
    		ItemStack forcemap = new ItemStack(Material.ENDER_CHEST);
    		meta.setDisplayName("§6Karte erzwingen");
    		forcemap.setItemMeta(meta);
        	inv.setItem(7, forcemap);
    	}
    	
    	//Gibt dem Spieler das Item, mit dem er für eine Karte abstimmen kann
    	ItemStack map = new ItemStack(Material.CHEST);
    	
    	if (getMap() != null) {
    		
    		meta.setDisplayName("§4Es wurde bereits eine Karte erzwungen!");
    		
    	} else {
    		
    		meta.setDisplayName("§6Kartenauswahl");
    	}
    	map.setItemMeta(meta);
    	inv.setItem(8, map);
    	
    	//Gibt dem Spieler das Item, mit dem er die Runde sofort starten kann
        if (p.hasPermission("tubeplay.start")) {
    		
        	ItemStack start = new ItemStack(Material.WOOL, 1, (byte) 13);
    		meta.setDisplayName("§6Start");
    		start.setItemMeta(meta);
        	inv.setItem(22, start);
    	}
    }
    
    public static void giveShopItems(Inventory inv) {
    	
		ItemStack item = null;
		Player p = (Player) inv.getHolder();
    	String name = p.getDisplayName();
        
    	if (name.contains("§4")) {
			
    		item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
			
		} else if (name.contains("§2")) {
			
			item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
			
		} else if (name.contains("§1")) {
			
			item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
			
		} else {
			
			item = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
		}
		
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§3Blöcke");
		item.setItemMeta(meta);
		inv.setItem(0, item);
		
		item = new ItemStack(Material.DIAMOND_PICKAXE);
		meta.setDisplayName("§3Spitzhacken");
		item.setItemMeta(meta);
		inv.setItem(1, item);
		
		item = new ItemStack(Material.GOLD_SWORD);
		meta.setDisplayName("§3Schwerter");
		item.setItemMeta(meta);
		inv.setItem(2, item);
		
		item = new ItemStack(Material.DIAMOND_CHESTPLATE);
		meta.setDisplayName("§3Rüstung");
		item.setItemMeta(meta);
		inv.setItem(3, item);
		
		item = new ItemStack(Material.BOW);
		meta.setDisplayName("§3Bögen");
		item.setItemMeta(meta);
		inv.setItem(4, item);
		
		item = new ItemStack(Material.BAKED_POTATO);
		meta.setDisplayName("§3Nahrung/Tränke");
		item.setItemMeta(meta);
		inv.setItem(5, item);
		
		item = new ItemStack(Material.CARPET);
		meta.setDisplayName("§3Fallen");
		item.setItemMeta(meta);
		inv.setItem(6, item);
		
		item = new ItemStack(Material.BLAZE_ROD);
		meta.setDisplayName("§3Spezial");
		item.setItemMeta(meta);
		inv.setItem(7, item);
		
        if (gommeShop.get(p.getName())) {
        	
        	inv.setItem(15, gommeSwitch);
        	
        } else {
        	
            item = new ItemStack(Material.SLIME_BALL);
			meta.setDisplayName("§6Zum Gomme-Shop wechseln");
			item.setItemMeta(meta);
			inv.setItem(8, item);
        }
    }
    
    public static void removeItem(ItemStack item, Player p) {
    	
		int amount = item.getAmount();
		
		if (amount > 1) {
			
			item.setAmount(amount - 1);
			
		} else {
			
			item.setType(Material.BOWL);
			p.getInventory().remove(item);
		}
    }
    
    public static void manageBedDestroy(Block block, Player p) {
    	
    	String name = p.getDisplayName();
    	Location blockLoc = block.getLocation();
    	int blockX = blockLoc.getBlockX();
    	int blockZ = blockLoc.getBlockZ();
    	String realName = p.getName();
    	Location redLoc = getRedTeamBedLocation();
    	Location greenLoc = getGreenTeamBedLocation();
    	Location blueLoc = getBlueTeamBedLocation();
    	Location yellowLoc = getYellowTeamBedLocation();
		
		if (Math.abs(blockX - redLoc.getBlockX()) <= 1 && Math.abs(blockZ - redLoc.getBlockZ()) <= 1 && !name.contains("§4")) {
			
			redTeamBed = false;
			sendThingsAfterBedDestroy(redLoc.getBlock(), "§4Rot", p);
			
			if (destroyedBeds.containsKey(realName)) {
				
				destroyedBeds.put(realName, destroyedBeds.get(realName) + 1);
				
			} else {
				
				destroyedBeds.put(realName, 1);
			}
			
		} else if (Math.abs(blockX - greenLoc.getBlockX()) <= 1 && Math.abs(blockZ - greenLoc.getBlockZ()) <= 1 && !name.contains("§2")) {
			
			greenTeamBed = false;
			sendThingsAfterBedDestroy(greenLoc.getBlock(), "§2Grün", p);
			
			if (destroyedBeds.containsKey(realName)) {
				
				destroyedBeds.put(realName, destroyedBeds.get(realName) + 1);
				
			} else {
				
				destroyedBeds.put(realName, 1);
			}
			
		} else if (Math.abs(blockX - blueLoc.getBlockX()) <= 1 && Math.abs(blockZ - blueLoc.getBlockZ()) <= 1 && !name.contains("§1")) {
			
			blueTeamBed = false;
			sendThingsAfterBedDestroy(blueLoc.getBlock(), "§1Blau", p);
			
			if (destroyedBeds.containsKey(realName)) {
				
				destroyedBeds.put(realName, destroyedBeds.get(realName) + 1);
				
			} else {
				
				destroyedBeds.put(realName, 1);
			}
			
		} else if (Math.abs(blockX - yellowLoc.getBlockX()) <= 1 && Math.abs(blockZ - yellowLoc.getBlockZ()) <= 1 && !name.contains("§e")) {
			
			yellowTeamBed = false;
			sendThingsAfterBedDestroy(yellowLoc.getBlock(), "§eGelb", p);
			
			if (destroyedBeds.containsKey(realName)) {
				
				destroyedBeds.put(realName, destroyedBeds.get(realName) + 1);
				
			} else {
				
				destroyedBeds.put(realName, 1);
			}
		}
    }
    
    public static void destroyBedRedAndSendMessage() {
    	
		destroyBedRed();
		Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §4Rot §awurde ausgelöscht!");
    }
    
    public static void destroyBedGreenAndSendMessage() {
    	
		destroyBedGreen();
		Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §2Grün §awurde ausgelöscht!");
    }
    
    public static void destroyBedBlueAndSendMessage() {
    	
		destroyBedBlue();
		Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §1Blau §awurde ausgelöscht!");
    }
    
    public static void destroyBedYellowAndSendMessage() {
    	
		destroyBedYellow();
		Bukkit.broadcastMessage("§6[§3Bedwars§6] §aDas Team §eGelb §awurde ausgelöscht!");
    }
    
    public static void destroyBedRed() {
    	
		destroyBed("§4Rot", getRedTeamBedLocation().getBlock());
		redTeamBed = false;
    }
    
    public static void destroyBedGreen() {
    	
		destroyBed("§2Grün", getGreenTeamBedLocation().getBlock());
		greenTeamBed = false;
    }
    
    public static void destroyBedBlue() {
    	
		destroyBed("§1Blau", getBlueTeamBedLocation().getBlock());
		blueTeamBed = false;
    }
    
    public static void destroyBedYellow() {
    	
		destroyBed("§eGelb", getYellowTeamBedLocation().getBlock());
		yellowTeamBed = false;
    }
    
    public static void destroyBed(String team, Block block) {
		
		Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
		ob.getScore("§c✘ " + team).setScore(ob.getScore("§a✔ " + team).getScore());
		board.resetScores("§a✔ " + team);
		block.setType(Material.AIR);
		
		for (Player op : Bukkit.getOnlinePlayers()) {
			
			op.playSound(op.getLocation(), Sound.IRONGOLEM_DEATH, 3, 1);
		}
	}
    
    public static void calculateMinutesOnline(Player p) {
    	
		int timeJoinedP = timeJoined.get(p.getName());
		int now = LocalTime.now().toSecondOfDay();
		
		if (timeJoinedP > now) {
			
			now += 86400;
			
		}
		final int finalNow = now;
		
		if (main.isEnabled()) {
			
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				
				@Override
				public void run() {
					
					mysql.addToMinutes(p.getUniqueId().toString(), (finalNow - timeJoinedP) / 60);
				}
			});
			
		} else {
			
			mysql.addToMinutes(p.getUniqueId().toString(), (finalNow - timeJoinedP) / 60);
		}
    }
    
    private static void sendThingsAfterBedDestroy(Block block, String team, Player p) {
    	
		destroyBed(team, block);
		Bukkit.broadcastMessage("§6[§3Bedwars§6] §cDas Bett von Team " + team + " §cwurde von " + p.getDisplayName() + " §cabgebaut!");
		
		if (statsDisabled) {
			
			p.sendMessage("§6[§3Bedwars§6] §aStats: §4deaktiviert");
			
		} else {
			
			p.sendMessage("§6[§3Bedwars§6] §aStats: §6Zerstörte Betten §3+1");
			
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				
				@Override
				public void run() {
					
					mysql.addToDestroyedBeds(p.getUniqueId().toString());
				}
			});
		}
    }
    
	private static String getRandomMap() {
		
		String[] maps = {"Pilz", "Phizzle", "Atlantis", "Industry", "Sonic", "Frozen", "Skyland"};
		int number = random.nextInt(7);
		return maps[number];
	}
	
	private static void removeHeadAndSign(int y, byte i) {
		
		Location loc = new Location(Bukkit.getWorlds().get(0), -6, y, 3 - i);
		loc.getBlock().setType(Material.AIR);
		loc.setY(loc.getY() - 1);
		loc.getBlock().setType(Material.AIR);
	}
	
	@SuppressWarnings("deprecation")
	private static void placeHeadAndSign(String uuid, int y, int i, String stats) {
		
		if (mysql.playerExists(uuid)) {
			
			String playername = mysql.getPlayername(uuid);
			Location loc = new Location(Bukkit.getWorlds().get(0), -6, y, 3 - i);
			Block block = loc.getBlock();
			
			if (block.getType().equals(Material.SKULL)) {
				
				Skull head = (Skull) block.getState();
				
				if (!head.getOwner().equalsIgnoreCase(playername)) {
					
					updateAndSetOwner(head, playername);
				}
				
			} else {
				
				block.setType(Material.SKULL);
				Skull head = (Skull) block.getState();
				head.setSkullType(SkullType.PLAYER);
				head.setRotation(BlockFace.EAST);
				head.getData().setData((byte) 5);
				updateAndSetOwner(head, playername);
			}
			loc.setY(loc.getY() - 1);
			Block block2 = loc.getBlock();
			block2.setType(Material.WALL_SIGN);
			block2.setData((byte) 5);
			Sign sign = (Sign) block2.getState();
			sign.setLine(0, i + ".");
			sign.setLine(1, playername);
			sign.setLine(2, "Punkte:");
			sign.setLine(3, String.valueOf(mysql.getPoints(uuid, stats)));
			sign.update();
		}
	}
	
	private static void updateAndSetOwner(Skull head, String owner) {
		
		head.setOwner(owner);
		head.update();
	}
}
