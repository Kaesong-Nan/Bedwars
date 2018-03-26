package de.tubeplay.bedwars.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tubeplay.bedwars.Bedwars;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class InteractEvent implements Listener {
	
	private Inventory red = Bukkit.createInventory(null, 27, "§4Rot");
	private Inventory green = Bukkit.createInventory(null, 27, "§2Grün");
	private Inventory blue = Bukkit.createInventory(null, 27, "§1Blau");
	private Inventory yellow = Bukkit.createInventory(null, 27, "§eGelb");
	private Vector<Integer> untilDespawn = new Vector<Integer>();
	private HashMap<String, Integer> untilTeleport = new HashMap<String, Integer>();
	private int countVillager;
	private int countTeleport;
	private ItemStack sonic = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
	private HashSet<ItemStack> teleporter = new HashSet<ItemStack>();
	
	public InteractEvent() {
		
		SkullMeta skullMeta = (SkullMeta) sonic.getItemMeta();
        skullMeta.setDisplayName("§3Sonic");
        sonic.setItemMeta(Bedwars.setTexture(skullMeta, "c00da1a7-12e3-4fd2-bfeb-9dc4b79d90ad", "Sonic", "eyJ0aW1lc3RhbXAiOjE1MDk0NjA3MzU5ODQsInByb2ZpbGVJZCI6ImMwMGRhMWE3MTJlMzRmZDJiZmViOWRjNGI3OWQ5MGFkIiwicHJvZmlsZU5hbWUiOiJTb25pYyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2VkMTQzOWYzMGY5YTI2ZTQwYzM3MzUzYmZkYmQyYjFiZmJkZTRjMTk3ZWU3ZmM3Y2VlNzM4ZjY2NThiNDAzMCJ9fX0=", "GreZs81dSZrTMHmyotf+nSY2a1ZtQFKNvljtso+PypmhdifkLbLHGZUX2EivJKtvrTynvyLkUNJziAdzE2DUFeq6LPfJBv0Cgd/sOTCuH+kZfOlELEx/u+g9vgkkMx6aoCS7TBdEIwwxC1mJK3yDJaUVvgzyqHErTgZwd6epf5Ns+KJGGgTpZXvQTWf/0hSSChM/9aDotb0IuvJmmkSDpXKXEKd8LYW218FIxiz2N6kz/Ub0QwTHwQ9qGbSHLxv5HYWCqVoXnBO0jMJy8AOlAK6a+tHR2W8z3BGVcOON1WYYyU1DWumgSsDu8ic30+HmBvaMx0BbYWBJ+chKWEv8wfrP9aBEvFYXMbnTgSXF3cCg7CFXeEZaX/w6qzbub1hIXG00m5BtPIyYn7fE63WgvHZpXFMm2cF91RkYw8OFy8tesY4U3cd+81aDxDNFtKYIIcEl/eblrqBW9apXAFu+iv6sNaIXeihtzyKBtrFBPtEtq3MxrUg3DnrqiU2L1ES43RtSbcMVoq9+OPxhZUw7FJFVOcEvTz9OPfMkrYSUcqYDN9ykvDfW25WUYr6g2vkctUiLtN+eIuFls74J9Kd6E2snisndRIQ9zMsaKlo8UP7aCprECi3y5a4yKSWQQYar67ufR9WSEFES5ydCsfwV9k1cBvSsx3NXRVfsWEpHCag="));
	}
	
	public void addToTeleporter(ItemStack head) {
		
		teleporter.add(head);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		
		Player p = event.getPlayer();
		HashSet<String> buildModePlayers = Bedwars.getBuildModePlayers();
		Action action = event.getAction();
		String name = p.getDisplayName();
		String realName = p.getName();
		BukkitScheduler scheduler = Bukkit.getScheduler();
		
		if (((p.getAllowFlight() || !Bedwars.isGameStarted()) && !buildModePlayers.contains(event.getPlayer().getName())) || (action == Action.PHYSICAL && event.getClickedBlock().getType().equals(Material.SOIL)) || (action == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.ANVIL && !p.isSneaking())) {
			
			event.setCancelled(true);
		}
		
		if (event.hasBlock() && action == Action.RIGHT_CLICK_BLOCK) {
			
			Material blockType = event.getClickedBlock().getType();
			
			if (blockType == Material.ENDER_CHEST && !p.getAllowFlight()) {
				
				event.setCancelled(true);
				
				if (name.contains("§4")) {
					
					p.openInventory(red);
					
				} else if (name.contains("§2")) {
					
					p.openInventory(green);
					
				} else if (name.contains("§1")) {
					
					p.openInventory(blue);
					
				} else {
					
					p.openInventory(yellow);
				}
				
			} else if (blockType == Material.BED_BLOCK) {
				
				ItemStack inHand = p.getItemInHand();
				
				if (inHand != null) {
					
					Material inHandType = p.getItemInHand().getType();
					
					if (!(inHandType.equals(Material.STAINED_CLAY) || inHandType.equals(Material.ENDER_STONE) || inHandType.equals(Material.IRON_BLOCK))) {
						
						event.setCancelled(true);
					}
				}
				
			} else if (blockType == Material.WALL_SIGN) {
				
				//Secrets
			}
		}
		
		if (event.hasItem()) {
			
			ItemStack usedItem = event.getItem();
			
			if (usedItem.hasItemMeta()) {
				
				ItemMeta usedItemMeta = usedItem.getItemMeta();
				
				if (usedItemMeta.hasDisplayName()) {
					
					String itemName = usedItemMeta.getDisplayName();
					Location playerLoc = p.getLocation();
					Bedwars main = Bedwars.getMain();
					
					if (itemName.equalsIgnoreCase("§6teamauswahl")) {
						
						Scoreboard board = Bedwars.getScoreboard();
						Team redTeam = board.getTeam("Rot");
						Team greenTeam = board.getTeam("Grün");
						Team blueTeam = board.getTeam("Blau");
						Team yellowTeam = board.getTeam("Gelb");
						int redTeamPlayers = redTeam.getSize();
						int greenTeamPlayers = greenTeam.getSize();
						int blueTeamPlayers = blueTeam.getSize();
						int yellowTeamPlayers = yellowTeam.getSize();
						Inventory inv = Bukkit.createInventory(event.getPlayer(), 9, "§6Teamauswahl");
						
						ItemStack red = new ItemStack(Material.REDSTONE, redTeamPlayers);
						ItemMeta meta = red.getItemMeta();
						
						if (redTeamPlayers == 4) {
							
							meta.setDisplayName("§4Rot - Voll");
							
						} else {
							
							meta.setDisplayName("§4Rot");
						}
						ArrayList<String> lore = new ArrayList<String>();
						
						for (String player : redTeam.getEntries()) {
							
							lore.add(player);
						}
						meta.setLore(lore);
						red.setItemMeta(meta);
						inv.setItem(0, red);
						
						ItemStack green = new ItemStack(Material.EMERALD, greenTeamPlayers);

						if (greenTeamPlayers == 4) {
							
							meta.setDisplayName("§2Grün - Voll");
							
						} else {
							
							meta.setDisplayName("§2Grün");
						}
						lore.clear();
						
						for (String player : greenTeam.getEntries()) {
							
							lore.add(player);
						}
						meta.setLore(lore);
						green.setItemMeta(meta);
						inv.setItem(3, green);
						
						ItemStack blue = new ItemStack(Material.INK_SACK, blueTeamPlayers, (byte) 4);

						if (blueTeamPlayers == 4) {
							
							meta.setDisplayName("§1Blau - Voll");
							
						} else {
							
							meta.setDisplayName("§1Blau");
						}
						lore.clear();
						
						for (String player : blueTeam.getEntries()) {
							
							lore.add(player);
						}
						meta.setLore(lore);
						blue.setItemMeta(meta);
						inv.setItem(5, blue);
						
						ItemStack yellow = new ItemStack(Material.GOLD_INGOT, yellowTeamPlayers);

						if (yellowTeamPlayers == 4) {
							
							meta.setDisplayName("§eGelb - Voll");
							
						} else {
							
							meta.setDisplayName("§eGelb");
						}
						lore.clear();
						
						for (String player : yellowTeam.getEntries()) {
							
							lore.add(player);
						}
						meta.setLore(lore);
						yellow.setItemMeta(meta);
						inv.setItem(8, yellow);
						
						p.openInventory(inv);
						
					} else if (itemName.equalsIgnoreCase("§6karte erzwingen")) {
						
						Inventory inv = Bukkit.createInventory(p, 9, "§6Karte erzwingen");
						
						ItemStack kingdoms = new ItemStack(Material.HUGE_MUSHROOM_2);
						ItemMeta meta = kingdoms.getItemMeta();
						meta.setDisplayName("§3Pilz");
						kingdoms.setItemMeta(meta);
						inv.setItem(0, kingdoms);
						
						ItemStack phizzle = new ItemStack(Material.GRASS);
						meta.setDisplayName("§3Phizzle");
						phizzle.setItemMeta(meta);
						inv.setItem(1, phizzle);
						
						ItemStack atlantis = new ItemStack(Material.SAND);
						meta.setDisplayName("§3Atlantis");
						atlantis.setItemMeta(meta);
						inv.setItem(2, atlantis);
						
						ItemStack industry = new ItemStack(Material.SMOOTH_BRICK);
						meta.setDisplayName("§3Industry");
						industry.setItemMeta(meta);
						inv.setItem(3, industry);
						
						inv.setItem(4, sonic);
						
						ItemStack frozen = new ItemStack(Material.ICE);
						meta.setDisplayName("§3Frozen");
						frozen.setItemMeta(meta);
						inv.setItem(5, frozen);
						
						ItemStack skyland = new ItemStack(Material.DIRT, 1, (byte) 2);
						meta.setDisplayName("§3Skyland");
						skyland.setItemMeta(meta);
						inv.setItem(6, skyland);
						
						p.openInventory(inv);
						
					} else if (itemName.equalsIgnoreCase("§6kartenauswahl")) {
						
						Inventory map = Bukkit.createInventory(p, 9, "§6Kartenauswahl");
						byte i = 0;
						
						for (String randomMap : Bedwars.getRandomMaps()) {
							
							int votes;
							ItemStack item = null;
							
							if (i == 0) {
								
								votes = Bedwars.getVotesRandomMap1();
								
							} else if (i == 4) {
								
								votes = Bedwars.getVotesRandomMap2();
								
							} else {
								
								votes = Bedwars.getVotesRandomMap3();
							}
							
							if (randomMap.equalsIgnoreCase("sonic")) {
								
								item = sonic;
								
							} else {
								
								if (randomMap.equalsIgnoreCase("pilz")) {
									
									item = new ItemStack(Material.HUGE_MUSHROOM_2);
									
								} else if (randomMap.equalsIgnoreCase("phizzle")) {
									
									item = new ItemStack(Material.GRASS);
									
								} else if (randomMap.equalsIgnoreCase("atlantis")) {
									
									item = new ItemStack(Material.SAND);
									
								} else if (randomMap.equalsIgnoreCase("industry")) {
									
									item = new ItemStack(Material.SMOOTH_BRICK);
									
								} else if (randomMap.equalsIgnoreCase("frozen")) {
									
									item = new ItemStack(Material.ICE);
									
								} else {
									
									item = new ItemStack(Material.DIRT, 1, (byte) 2);
								}
								ItemMeta meta = item.getItemMeta();
								meta.setDisplayName("§3" + randomMap);
								item.setItemMeta(meta);
							}
							item.setAmount(votes);
							map.setItem(i, item);
							i += 4;
						}
						p.openInventory(map);
						
					} else if (itemName.equalsIgnoreCase("§4§lZurück zur Lobby")) {
						
						p.kickPlayer("lobby");
						
					} else if (itemName.equalsIgnoreCase("§6rettungsplattform")) {
						
						int originalX = playerLoc.getBlockX();
						int y = playerLoc.getBlockY() - 2;
						int originalZ = playerLoc.getBlockZ();
						Bedwars.removeItem(usedItem, p);
						
						for (byte x = -2; x < 3; x++) {
							
							for (byte z = -2; z < 3; z++) {
								
								Location blockLoc = new Location(playerLoc.getWorld(), originalX + x, y, originalZ + z);
								Block block = blockLoc.getBlock();
								
								if (block.getType().equals(Material.AIR)) {
									
									block.setType(Material.SLIME_BLOCK);
								}
							}
						}
						
						scheduler.runTaskLater(main, new Runnable() {
							
							@Override
							public void run() {
								
								for (byte x = -2; x < 3; x++) {
									
									for (byte z = -2; z < 3; z++) {
										
										Location blockLoc = new Location(playerLoc.getWorld(), originalX + x, y, originalZ + z);
										Block block = blockLoc.getBlock();
										
										if (block.getType().equals(Material.SLIME_BLOCK)) {
											
											block.setType(Material.AIR);
										}
									}
								}
							}
						}, 1000);
						
					} else if (itemName.equalsIgnoreCase("§6mobiler shop")) {
						
						Location loc;
						
						if (event.hasBlock()) {
							
							loc = event.getClickedBlock().getLocation();
							loc.setY(loc.getY() + 1);
							
						} else {
							
							loc = playerLoc;
						}
						
						Villager villager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
						villager.setCustomName("§6Mobiler Shop §7- §350");
						villager.setCustomNameVisible(true);
						villager.setCanPickupItems(false);
						Bedwars.removeItem(usedItem, p);
						untilDespawn.add(1000);
						int position = untilDespawn.size() - 1;
						
						countVillager = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
							
							@Override
							public void run() {
								
								int untilDespawnThisVillager = untilDespawn.get(position) - 1;
								untilDespawn.set(position, untilDespawnThisVillager);
								
								if (untilDespawnThisVillager == 0) {
									
									boolean allRemoved = true;
									villager.remove();
									
									for (int untilDespawnVillager : untilDespawn) {
										
										if (untilDespawnVillager > 0) {
											
											allRemoved = false;
											break;
										}
									}
									
									if (allRemoved) {
										
										Bukkit.getScheduler().cancelTask(countVillager);
									}
									
								} else if (untilDespawnThisVillager % 20 == 0) {
									
									villager.setCustomName("§6Mobiler Shop §7- §3" + (untilDespawnThisVillager / 20));
								}
								
								if (!villager.isDead()) {
									
									Location villagerLoc = villager.getLocation();
									
									if (villagerLoc.getX() != loc.getX() || villagerLoc.getZ() != loc.getZ()) {
										
										Location newVillagerLoc = new Location(villagerLoc.getWorld(), loc.getX(), villagerLoc.getY(), loc.getZ(), villagerLoc.getYaw(), villagerLoc.getPitch());
										villager.teleport(newVillagerLoc);
									}
								}
							}
						}, 1, 1);
						
					} else if (itemName.equalsIgnoreCase("§6base-teleporter") && !untilTeleport.containsKey(realName)) {
						
						untilTeleport.put(realName, 3);
						usedItem.setType(Material.GLOWSTONE_DUST);
						
						countTeleport = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
							
							@Override
							public void run() {
								
								if (untilTeleport.containsKey(realName)) {
									
									int countdown = untilTeleport.get(realName);
									Location loc = p.getLocation();
									ItemStack inHand = p.getItemInHand();
									
									if (playerLoc.getBlockX() != loc.getBlockX() || playerLoc.getBlockZ() != loc.getBlockZ() || !usedItem.equals(inHand)) {
										
										endTeleport(p);
										p.sendMessage("§6[§3Bedwars§6] §cTeleport abgebrochen: Du darfst dich während des Teleportvorgangs nicht bewegen und musst das Item die ganze Zeit in der Hand halten!");
										
									} else if (countdown == 0) {
										
						            	p.teleport(Bedwars.getSpawnLocation(p.getDisplayName()));
										p.playSound(playerLoc, Sound.ANVIL_LAND, 1, 1);
										endTeleport(p);
										Bedwars.removeItem(inHand, p);
										
										for (Player op : Bukkit.getOnlinePlayers()) {
											
											Location opLoc = op.getLocation();
											
											if (opLoc.distance(Bedwars.getSpawnLocation(name)) < 21) {
												
												op.playSound(opLoc, Sound.ANVIL_LAND, 1, 1);
											}
										}
										
									} else {
										
										p.playSound(playerLoc, Sound.LEVEL_UP, 1, 1);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 0, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 1, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 2, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 3, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 4, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 5, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 6, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 7, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										p.spigot().playEffect(playerLoc, Effect.LARGE_SMOKE, 12, 8, (float) 0, (float) 0, (float) 0, (float) 0.5, 10, 0);
										untilTeleport.put(realName, countdown - 1);
										PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("\"text\":\"§6" + countdown + "\"}"), 5, 10, 5);
										CraftPlayer cp = (CraftPlayer) p;
										cp.getHandle().playerConnection.sendPacket(title);
									}
								}
							}
						}, 0, 20);
						
					} else if (itemName.equalsIgnoreCase("§6teleporter")) {
						
						Inventory compass = Bukkit.createInventory(p, 18, "§6Teleporter");
						byte i = 0;
						
						for (ItemStack head : teleporter) {
							
							SkullMeta meta = (SkullMeta) head.getItemMeta();
							String headName = meta.getDisplayName();
							headName = headName.replace("§4", "");
							headName = headName.replace("§2", "");
							headName = headName.replace("§1", "");
							headName = headName.replace("§e", "");
							
							for (Player op : Bukkit.getOnlinePlayers()) {
								
								if (!op.getAllowFlight() && op.getName().equalsIgnoreCase(headName)) {
									
									meta.setDisplayName(op.getDisplayName());
									head.setItemMeta(meta);
									compass.setItem(i, head);
									i++;
									break;
								}
							}
						}
						p.openInventory(compass);
					}
				}
			}
		}
	}
	
	private void endTeleport(Player p) {
		
		for (ItemStack content : p.getInventory().getContents()) {
			
			if (content != null && content.getType().equals(Material.GLOWSTONE_DUST)) {
				
				content.setType(Material.SULPHUR);
			}
		}
		untilTeleport.remove(p.getName());
		
		if (untilTeleport.isEmpty()) {
			
			Bukkit.getScheduler().cancelTask(countTeleport);
		}
	}
}
