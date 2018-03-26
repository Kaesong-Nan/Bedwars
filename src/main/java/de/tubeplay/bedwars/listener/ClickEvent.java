package de.tubeplay.bedwars.listener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;

import de.tubeplay.bedwars.Bedwars;
import de.tubeplay.bedwars.MySQL;
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity.EnumEntityUseAction;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.MerchantRecipe;
import net.minecraft.server.v1_8_R3.MerchantRecipeList;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;

public class ClickEvent implements Listener {
	
	private HashSet<CraftVillager> villagerSet = new HashSet<CraftVillager>();
	private int schedulerVillager;
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		
		Player p = (Player) event.getWhoClicked();
		String realName = p.getName();
		
		if ((!Bedwars.isGameStarted() || p.getAllowFlight()) && !Bedwars.getBuildModePlayers().contains(realName)) {
			
			event.setCancelled(true);
		}
		
		InventoryAction action = event.getAction();
		
		if (!(action.equals(InventoryAction.NOTHING) || action.equals(InventoryAction.UNKNOWN))) {
			
			ItemStack item = event.getCurrentItem();
			
			if (item != null && item.hasItemMeta()) {
				
				Material itemType = item.getType();
				
				if (!itemType.equals(Material.AIR)) {
					
					ItemMeta meta = item.getItemMeta();
					Inventory clickedInv = event.getInventory();
					String inventoryName = clickedInv.getName();
					
					if (meta.hasDisplayName()) {
						
						String itemName = meta.getDisplayName();
						List<String> lore = new ArrayList<String>();
						Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
						Location loc = p.getLocation();
						String playerName = p.getDisplayName();
						int rawSlot = event.getRawSlot();
						Inventory inv = p.getInventory();
						BukkitScheduler scheduler = Bukkit.getScheduler();
						
						if (clickedInv.getType().equals(InventoryType.MERCHANT) && rawSlot == 2) {
							
							int itemAmount = item.getAmount();
							event.setCancelled(true);
							ItemStack cost;
							
							if (clickedInv.getItem(0) == null) {
								
								cost = clickedInv.getItem(1);
								
							} else {
								
								cost = clickedInv.getItem(0);
							}
							int costAmount = cost.getAmount();
							int finalPrice = 0;
							int price;
							
							switch (itemName) {
								
							case "§6Eisenblock":
								
								price = 5;
								break;
								
							case "§6Knockback-Stick Level 1":
								
								price = 6;
								break;
								
							case "§6Bogen Level 3":
								
								price = 13;
								break;
								
							case "§6Stärke-Trank":
								
								price = 8;
								break;
								
							default:
								
								if (itemName.equalsIgnoreCase("§6team-truhe") || itemName.equalsIgnoreCase("eisen-spitzhacke") || itemName.equalsIgnoreCase("§6goldener apfel") || itemName.equalsIgnoreCase("§6mobiler shop")) {
									
									price = 2;
									
								} else if (itemName.equalsIgnoreCase("§6spinnennetz") || itemName.equalsIgnoreCase("§6diamant-spitzhacke") || itemName.equalsIgnoreCase("§6granate")) {
									
									price = 10;
									
								} else if (itemName.equalsIgnoreCase("§6gold-schwert level 1") || itemName.equalsIgnoreCase("§6normaler block") || itemName.equalsIgnoreCase("§6leder-stiefel") || itemName.equalsIgnoreCase("§6leder-hose") || itemName.equalsIgnoreCase("§6leder-helm") || itemName.equalsIgnoreCase("§6ketten-brustpanzer level 1") || itemName.equalsIgnoreCase("§6pfeil") || itemName.equalsIgnoreCase("§6apfel")) {
									
									price = 1;
									
								} else if (itemName.equalsIgnoreCase("§6knockback-stick level 1") || itemName.equalsIgnoreCase("§6Gold-Schwert Level 3") || itemName.equalsIgnoreCase("§6regeneration-trank")) {
									
									price = 6;
									
								} else if (itemName.equalsIgnoreCase("§6eisenblock") || itemName.equalsIgnoreCase("§6eisen-schwert") || itemName.equalsIgnoreCase("§6ketten-brustpanzer level 2") || itemName.equalsIgnoreCase("§6spieler-finder")) {
									
									price = 5;
									
								} else if (itemName.equalsIgnoreCase("§6bogen level 2") || itemName.equalsIgnoreCase("§6ketten-brustpanzer level 3") || itemName.equalsIgnoreCase("§6steak") || itemName.equalsIgnoreCase("§6enderperle")) {
									
									price = 7;
									
								} else if (itemName.equalsIgnoreCase("§6holz-spitzhacke") || itemName.equalsIgnoreCase("§6heilung-tank")) {
									
									price = 4;
									
								} else {
									
									price = 3;
								}
							}
							
							if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
								
								finalPrice = costAmount;
								finalPrice -= costAmount % price;
								item.setAmount((costAmount / price) * itemAmount);
								inv.addItem(item);
								
							} else {
								
								finalPrice = price;
								ItemStack currentlyOnCursor = p.getItemOnCursor();
								
								if (currentlyOnCursor.getType().equals(Material.AIR)) {
									
									p.setItemOnCursor(item);
									
								} else if (currentlyOnCursor.getAmount() < 64 && currentlyOnCursor.hasItemMeta() && currentlyOnCursor.getItemMeta().hasDisplayName() && currentlyOnCursor.getItemMeta().getDisplayName().equals(itemName) && currentlyOnCursor.getType().equals(itemType)) {
									
									currentlyOnCursor.setAmount(currentlyOnCursor.getAmount() + itemAmount);
									
								} else {
									
									return;
								}
							}
							
							if (costAmount == finalPrice) {
								
								cost.setType(Material.BOWL);
								clickedInv.remove(cost);
								
							} else {
								
								cost.setAmount(costAmount - finalPrice);
							}
							
						} else if (itemName.equalsIgnoreCase("§6start")) {
							
			            	int onlinePlayersSize = onlinePlayers.size();
			            	Scoreboard board = Bedwars.getScoreboard();
		            		p.closeInventory();
			            	
			            	if (onlinePlayersSize == board.getTeam("Rot").getSize() || onlinePlayersSize == board.getTeam("Grün").getSize() || onlinePlayersSize == board.getTeam("Blau").getSize() || onlinePlayersSize == board.getTeam("Gelb").getSize()) {
			            		
			            		p.sendMessage("§6[§3Bedwars§6] §cDas Spiel kann nicht gestartet werden, wenn alle Spieler im selben Team sind!");
			            		
			            	} else if (onlinePlayersSize == 1) {
			            		
			            		p.sendMessage("§6[§3Bedwars§6] §cDas Spiel kann nicht mit nur einem Spieler gestartet werden!");
								
			            	} else {
			            		
			            		Bedwars.start();
			            	}
							
						} else if (inventoryName.equalsIgnoreCase("§6Teamauswahl") && rawSlot < 9) {
							
							byte playersInSameTeam = 0;
							ItemStack newItem;
							String team = itemName.substring(0, 2);
							p.closeInventory();
							p.playSound(loc, Sound.ANVIL_USE, 1, 8);
							
							if (team.equalsIgnoreCase("§4")) {
								
								newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
								
							} else if (team.equalsIgnoreCase("§2")) {
								
								newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
								
							} else if (team.equalsIgnoreCase("§1")) {
								
								newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
								
							} else {
								
								newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
							}
							
							for (Player op : onlinePlayers) {
								
								if (op.getDisplayName().contains(team)) {
									
									playersInSameTeam++;
								}
							}
							
							if (playersInSameTeam < 4) {
								
								p.setDisplayName(team + realName + "§r");
								Bedwars.getScoreboard().getTeam(itemName.substring(2)).addEntry(realName);
								
								ItemMeta metaTeam = newItem.getItemMeta();
								metaTeam.setDisplayName("§6Teamauswahl");
								newItem.setItemMeta(metaTeam);
								p.setItemInHand(newItem);
								
							} else {
								
								p.sendMessage("§6[§3Bedwars§6] §cEs sind bereits die maximale Anzahl an Spielern in dem Team!");
							}
							
						} else if (inventoryName.equalsIgnoreCase("§6karte erzwingen") && rawSlot < 9) {
							
							Bedwars.setMap(itemName);
							
					    	for (Player op : Bukkit.getOnlinePlayers()) {
					    		
					    		if (!op.getAllowFlight()) {
					    			
					    			inv = op.getInventory();
					    			String invName = op.getOpenInventory().getTopInventory().getName();
					    			
					    			if (invName.equalsIgnoreCase("§6kartenauswahl") || invName.equalsIgnoreCase("§6karte erzwingen")) {
					    				
					    				op.closeInventory();
					    			}
					        		
					        		if (op.hasPermission("tubeplay.forcemap") && !op.hasPermission("tubeplay.forcemap.admin")) {
					        			
					        			inv.remove(Material.ENDER_CHEST);
					        		}
					        		item = inv.getItem(8);
					        		meta = item.getItemMeta();
					        		meta.setDisplayName("§4Es wurde bereits eine Karte erzwungen!");
					        		item.setItemMeta(meta);
					    		}
					    	}
							p.playSound(loc, Sound.ANVIL_USE, 1, 8);
							
						} else if (inventoryName.equalsIgnoreCase("§6teleporter") && rawSlot < 18) {
							
							SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
							String displayName = skullMeta.getDisplayName().replace("§4", "");
							displayName = displayName.replace("§2", "");
							displayName = displayName.replace("§1", "");
							displayName = displayName.replace("§e", "");
							Player targetP = null;
							event.setCancelled(true);
							p.closeInventory();
							p.playSound(loc, Sound.ANVIL_USE, 1, 8);
							
							for (Player op : onlinePlayers) {
								
								if (op.getName().equalsIgnoreCase(displayName) && !op.getAllowFlight()) {
									
									targetP = op;
									break;
								}
							}
							
							if (targetP == null) {
								
								p.sendMessage("§6[§3Bedwars§6] §cDer Spieler " + itemName + " §chat die Runde anscheinend leider schon verlassen!");
								
							} else {
								
								Location targetPLoc = targetP.getLocation();
								targetPLoc.setY(targetPLoc.getY() + 4);
								p.teleport(targetP);
								p.sendMessage("§6[§3Bedwars§6] §aDu wurdest zu " + itemName + " §ateleportiert.");
							}
							
						} else if (inventoryName.equalsIgnoreCase("§6kartenauswahl") && rawSlot < 9) {
							
							Bedwars.vote(itemName, p);
							p.playSound(loc, Sound.ANVIL_USE, 1, 8);
							
						} else if (inventoryName.contains("§6Shop")) {
							
							if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
								
								event.setCancelled(true);	
							}
							
							if ((inventoryName.equalsIgnoreCase("§6shop") && rawSlot < 18) || (inventoryName.equalsIgnoreCase("§6shop - spezial") && rawSlot < 27)) {
								
								HashMap<String, Boolean> gommeShop = Bedwars.getGommeShop();
								MySQL mysql = Bedwars.getMySQL();
								String uuid = p.getUniqueId().toString();
								Bedwars main = Bedwars.getMain();
								event.setCancelled(true);
								
								if (itemName.equalsIgnoreCase("§6zum gomme-shop wechseln")) {
									
						            p.playSound(loc, Sound.ANVIL_USE, 1, 8);
						            clickedInv.setItem(8, Bedwars.getGommeSwitch());
						            gommeShop.put(realName, true);
						            
						            scheduler.runTaskAsynchronously(main, new Runnable() {
										
										@Override
										public void run() {
											
								            if (mysql.playerExistsInGommeShop(uuid)) {
								            	
								    			mysql.update("UPDATE GommeShop SET Enabled='1' WHERE UUID='" + uuid + "'");
								            	
								            } else {
								            	
								    			mysql.update("INSERT INTO GommeShop VALUES ('" + uuid + "', '1')");
								            }
										}
									});
									
								} else if (itemName.equalsIgnoreCase("§6zum normalen shop wechseln")) {
									
						            p.playSound(loc, Sound.ANVIL_USE, 1, 8);
						            
						            ItemStack shopItem = new ItemStack(Material.SLIME_BALL);
						            meta = shopItem.getItemMeta();
									meta.setDisplayName("§6Zum Gomme-Shop wechseln");
									shopItem.setItemMeta(meta);
						            clickedInv.setItem(8, shopItem);
						            gommeShop.put(realName, false);
						            
						            scheduler.runTaskAsynchronously(main, new Runnable() {
										
										@Override
										public void run() {
											
								            if (mysql.playerExistsInGommeShop(uuid)) {
								            	
								    			mysql.update("UPDATE GommeShop SET Enabled='0' WHERE UUID='" + uuid + "'");
								            	
								            } else {
								            	
								    			mysql.update("INSERT INTO GommeShop VALUES ('" + uuid + "', '0')");
								            }
										}
									});
						            
								} else if (meta.hasLore()) {
									
									String material = meta.getLore().get(1);
									
									if (material.equalsIgnoreCase("bronze")) {
										
										buy(event, Material.CLAY_BRICK);
										
									} else if (material.equalsIgnoreCase("eisen")) {
										
										buy(event, Material.IRON_INGOT);
										
									} else {
										
										buy(event, Material.GOLD_INGOT);
									}
									
								} else if (itemName.equalsIgnoreCase("§3blöcke") || itemName.equalsIgnoreCase("§3spitzhacken") || itemName.equalsIgnoreCase("§3schwerter") || itemName.equalsIgnoreCase("§3rüstung") || itemName.equalsIgnoreCase("§3bögen") || itemName.equalsIgnoreCase("§3nahrung/tränke") || itemName.equalsIgnoreCase("§3fallen") || itemName.equalsIgnoreCase("§3spezial")) {
									
									boolean gommeShopEnabled = gommeShop.get(realName);
									
									if (inventoryName.equalsIgnoreCase("§6shop - spezial") && !itemName.equalsIgnoreCase("§3spezial")) {
										
										clickedInv = Bukkit.createInventory(p, 18, "§6Shop");
										Bedwars.giveShopItems(clickedInv);
										p.openInventory(clickedInv);
										
									} else {
										
										item.removeEnchantment(Enchantment.DURABILITY);
										meta = item.getItemMeta();
									}
									
									if (gommeShopEnabled) {
										
										p.closeInventory();
										CraftVillager cVillager = (CraftVillager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
										int entityID = cVillager.getEntityId();
										
										PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata();
										Class<?> packetClass = metadata.getClass();
										
										try {
											
											Field f = packetClass.getDeclaredField("a");
											f.setAccessible(true);
											f.set(metadata, entityID);
											f = packetClass.getDeclaredField("b");
											f.setAccessible(true);
											WatchableObject invisibility = new WatchableObject(0, (byte) 0x20, null);
											ArrayList<WatchableObject> metadataList = new ArrayList<WatchableObject>();
											metadataList.add(invisibility);
											f.set(metadata, metadataList);
											
										} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
											
											e.printStackTrace();
										}
										
										for (Player op : Bukkit.getOnlinePlayers()) {
											
											CraftPlayer cp = (CraftPlayer) op;
											cp.getHandle().playerConnection.sendPacket(metadata);
										}
										PotionEffect invisibilityPotion = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
										cVillager.addPotionEffect(invisibilityPotion);
										cVillager.setCustomName(itemName);
										cVillager.setCanPickupItems(false);
										EntityVillager villager = cVillager.getHandle();
										NBTTagCompound nbt = new NBTTagCompound();
										MerchantRecipeList offers = new MerchantRecipeList();
										villagerSet.add(cVillager);
										
										schedulerVillager = scheduler.scheduleSyncRepeatingTask(main, new Runnable() {
											
											@Override
											public void run() {
												
												if (cVillager.isDead()) {
													
													villagerSet.remove(cVillager);
													boolean allDead = true;
													
													for (CraftVillager cv : villagerSet) {
														
														if (!cv.isDead()) {
															
															allDead = false;
														}
													}
													
													if (allDead) {
														
														scheduler.cancelTask(schedulerVillager);
													}
													
												} else {
													
													Location villagerLoc = cVillager.getLocation();
													
													if (villagerLoc.getX() != loc.getX() || villagerLoc.getZ() != loc.getZ()) {
														
														Location newVillagerLoc = new Location(villagerLoc.getWorld(), loc.getX(), villagerLoc.getY(), loc.getZ(), villagerLoc.getYaw(), villagerLoc.getPitch());
														cVillager.teleport(newVillagerLoc);
													}
												}
											}
										}, 1, 1);
										
										ItemStack bronze = new ItemStack(Material.CLAY_BRICK);
										meta.setDisplayName("Bronze");
										bronze.setItemMeta(meta);
						        		ItemStack iron = new ItemStack(Material.IRON_INGOT);
				                		ItemStack gold = new ItemStack(Material.GOLD_INGOT);
				                		item = new ItemStack(Material.AIR);
				                		net.minecraft.server.v1_8_R3.ItemStack air = CraftItemStack.asNMSCopy(item);
										
										switch (itemName) {
										
										case "§3Blöcke":
											
								            if (playerName.contains("§4")) {
												
								            	item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 14);
												
											} else if (playerName.contains("§2")) {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 13);
												
											} else if (playerName.contains("§1")) {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 11);
												
											} else {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 4);
											}
											
								            meta.setDisplayName("§6Normaler Block");
								            item.setItemMeta(meta);
								            MerchantRecipe recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
								            bronze.setAmount(3);
								            item = new ItemStack(Material.ENDER_STONE);
								            meta.setDisplayName("§6Verstärkter Block");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(5);
								            item = new ItemStack(Material.IRON_BLOCK);
								            meta.setDisplayName("§6Eisenblock");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											bronze.setAmount(10);
								            item = new ItemStack(Material.WEB);
								            meta.setDisplayName("§6Spinnennetz");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(3);
								            item = new ItemStack(Material.CHEST);
								            meta.setDisplayName("§6Truhe");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(2);
								            item = new ItemStack(Material.ENDER_CHEST);
								            meta.setDisplayName("§6Team-Truhe");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Spitzhacken":
											
											bronze.setAmount(4);
											item = new ItemStack(Material.WOOD_PICKAXE);
								            meta.setDisplayName("§6Holz-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
											iron.setAmount(3);
								            item = new ItemStack(Material.STONE_PICKAXE);
								            meta.setDisplayName("§6Stein-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(2);
								            item = new ItemStack(Material.IRON_PICKAXE);
								            meta.setDisplayName("§6Eisen-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(10);
								            item = new ItemStack(Material.DIAMOND_PICKAXE);
								            meta.setDisplayName("§6Diamant-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Schwerter":
											
											removeEverthing(event, clickedInv);
											
											bronze.setAmount(6);
											item = new ItemStack(Material.STICK);
								            meta.setDisplayName("§6Knockback-Stick Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
								            item = new ItemStack(Material.GOLD_SWORD);
								            meta.setDisplayName("§6Gold-Schwert Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(3);
								            item = new ItemStack(Material.GOLD_SWORD);
								            meta.setDisplayName("§6Gold-Schwert Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(6);
								            item = new ItemStack(Material.GOLD_SWORD);
								            meta.setDisplayName("§6Gold-Schwert Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(5);
								            item = new ItemStack(Material.IRON_SWORD);
								            meta.setDisplayName("§6Eisen-Schwert");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Rüstung":
											
											removeEverthing(event, clickedInv);
											
											item = new ItemStack(Material.LEATHER_BOOTS);
											LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
											
							                if (playerName.contains("§4")) {
												
												leatherMeta.setColor(Color.RED);
												
											} else if (playerName.contains("§2")) {
												
												leatherMeta.setColor(Color.GREEN);
												
											} else if (playerName.contains("§1")) {
												
												leatherMeta.setColor(Color.BLUE);
												
											} else {
												
												leatherMeta.setColor(Color.YELLOW);
											}
											
								            leatherMeta.setDisplayName("§6Leder-Stiefel");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
								            item = new ItemStack(Material.LEATHER_LEGGINGS);
								            leatherMeta.setDisplayName("§6Leder-Hose");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
								            item = new ItemStack(Material.LEATHER_HELMET);
								            leatherMeta.setDisplayName("§6Leder-Helm");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
							                
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(5);
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(7);
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Bögen":
											
											removeEverthing(event, clickedInv);
											
											gold.setAmount(3);
											item = new ItemStack(Material.BOW);
								            meta.setDisplayName("§6Bogen Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(7);
								            item = new ItemStack(Material.BOW);
								            meta.setDisplayName("§6Bogen Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(13);
								            item = new ItemStack(Material.BOW);
								            meta.setDisplayName("§6Bogen Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
								            item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(1);
								            item = new ItemStack(Material.ARROW);
								            meta.setDisplayName("§6Pfeil");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Nahrung/Tränke":
											
											bronze.setAmount(3);
											item = new ItemStack(Material.BAKED_POTATO);
								            meta.setDisplayName("§6Gebackene Kartoffel");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
											bronze.setAmount(7);
								            item = new ItemStack(Material.COOKED_BEEF, 2);
								            meta.setDisplayName("§6Steak");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											bronze.setAmount(1);
								            item = new ItemStack(Material.APPLE);
								            meta.setDisplayName("§6Apfel");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(bronze), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(2);
								            item = new ItemStack(Material.GOLDEN_APPLE);
								            meta.setDisplayName("§6Goldener Apfel");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(4);
							                Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
							                item = potion.toItemStack(1);
								            meta.setDisplayName("§6Heilung-Trank");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(6);
								            potion = new Potion(PotionType.REGEN, 1);
								            item = potion.toItemStack(1);
								            meta.setDisplayName("§6Regeneration-Trank");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
							                
											iron.setAmount(3);
								            potion = new Potion(PotionType.SPEED, 2);
								            item = potion.toItemStack(1);
								            meta.setDisplayName("§6Schnelligkeit-Trank");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
							                
											gold.setAmount(8);
								            potion = new Potion(PotionType.STRENGTH, 1);
								            item = potion.toItemStack(1);
								            meta.setDisplayName("§6Stärke-Trank");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            break;
								            
										case "§3Fallen":
											
											iron.setAmount(3);
											item = new ItemStack(Material.CARPET, 1, (byte) 8);
								        	meta.setDisplayName("§6Benachrichtigungs-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
								        	item = new ItemStack(Material.CARPET, 1, (byte) 15);
								        	meta.setDisplayName("§6Blindheits-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 5);
								        	meta.setDisplayName("§6Übelkeits-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 1);
								        	meta.setDisplayName("§6Hunger-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 4);
								        	meta.setDisplayName("§6Langsamkeits-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 14);
								        	meta.setDisplayName("§6Schwäche-Falle");
								        	item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								        	break;
								        	
										default:
											
											gold.setAmount(7);
											item = new ItemStack(Material.ENDER_PEARL);
								            meta.setDisplayName("§6Enderperle");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(3);
								            item = new ItemStack(Material.BLAZE_ROD);
								            meta.setDisplayName("§6Rettungsplattform");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
											
											gold.setAmount(2);
								            item = new ItemStack(Material.PAPER);
								            meta.setDisplayName("§6Mobiler Shop");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(3);
								            item = new ItemStack(Material.SULPHUR);
								            meta.setDisplayName("§6Base-Teleporter");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											gold.setAmount(10);
								            item = new ItemStack(Material.SNOW_BALL);
								            meta.setDisplayName("§6Granate");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(gold), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
											iron.setAmount(5);
								            item = new ItemStack(Material.COMPASS);
								            meta.setDisplayName("§6Spieler-Finder");
								            item.setItemMeta(meta);
								            recipe = new MerchantRecipe(CraftItemStack.asNMSCopy(iron), air, CraftItemStack.asNMSCopy(item), 0, Integer.MAX_VALUE);
											offers.add(recipe);
								            
								            p.openInventory(clickedInv);
								            break;
										}
										nbt.set("Offers", offers.a());
										villager.a(nbt);
										
										PacketPlayInUseEntity useVillager = new PacketPlayInUseEntity();
										packetClass = useVillager.getClass();
										
										try {
											
											Field f = packetClass.getDeclaredField("a");
											f.setAccessible(true);
											f.set(useVillager, entityID);
											f = packetClass.getDeclaredField("b");
											f.setAccessible(true);
											f.set(useVillager, EnumEntityUseAction.INTERACT);
											
										} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
											
											e.printStackTrace();
										}
										CraftPlayer cp = (CraftPlayer) p;
										cp.getHandle().playerConnection.a(useVillager);
										
									} else {
										
										removeEverthing(event, clickedInv);
										
										switch (itemName) {
										
										case "§3Blöcke":
											
								            if (playerName.contains("§4")) {
												
								            	item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 14);
												
											} else if (playerName.contains("§2")) {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 13);
												
											} else if (playerName.contains("§1")) {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 11);
												
											} else {
												
												item = new ItemStack(Material.STAINED_CLAY, 2, (byte) 4);
											}
											
								            lore.add("1");
								            lore.add("Bronze");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Normaler Block");
								            item.setItemMeta(meta);
								            clickedInv.setItem(9, item);
								            
								            item = new ItemStack(Material.ENDER_STONE);
								            lore.remove(0);
								            lore.add(0, "3");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Verstärkter Block");
								            item.setItemMeta(meta);
								            clickedInv.setItem(10, item);
								            
								            item = new ItemStack(Material.IRON_BLOCK);
								            lore.clear();
								            lore.add("5");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Eisenblock");
								            item.setItemMeta(meta);
								            clickedInv.setItem(11, item);
								            
								            item = new ItemStack(Material.WEB);
								            lore.clear();
								            lore.add("10");
								            lore.add("Bronze");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Spinnennetz");
								            item.setItemMeta(meta);
								            clickedInv.setItem(13, item);
								            
								            item = new ItemStack(Material.CHEST);
								            lore.clear();
								            lore.add("3");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Truhe");
								            item.setItemMeta(meta);
								            clickedInv.setItem(15, item);
								            
								            item = new ItemStack(Material.ENDER_CHEST);
								            lore.clear();
								            lore.add("2");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Team-Truhe");
								            item.setItemMeta(meta);
								            clickedInv.setItem(16, item);
								            break;
								            
										case "§3Spitzhacken":
											
											item = new ItemStack(Material.WOOD_PICKAXE);
								            lore.add("4");
								            lore.add("Bronze");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Holz-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            clickedInv.setItem(11, item);
											
								            item = new ItemStack(Material.STONE_PICKAXE);
								            lore.clear();
								            lore.add("3");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Stein-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            clickedInv.setItem(12, item);
								            
								            item = new ItemStack(Material.IRON_PICKAXE);
								            lore.clear();
								            lore.add("2");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Eisen-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            clickedInv.setItem(14, item);
								            
								            item = new ItemStack(Material.DIAMOND_PICKAXE);
								            lore.remove(0);
								            lore.add(0, "10");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Diamant-Spitzhacke");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            clickedInv.setItem(15, item);
								            break;
								            
										case "§3Schwerter":
											
											item = new ItemStack(Material.STICK);
								            lore.add("6");
								            lore.add("Bronze");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Knockback-Stick Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
								            clickedInv.setItem(10, item);
											
								            item = new ItemStack(Material.GOLD_SWORD);
											lore.clear();
								            lore.add("1");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Gold-Schwert Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            clickedInv.setItem(12, item);
								            
								            item = new ItemStack(Material.GOLD_SWORD);
											lore.remove(0);
								            lore.add(0, "3");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Gold-Schwert Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
								            clickedInv.setItem(13, item);
								            
								            item = new ItemStack(Material.GOLD_SWORD);
											lore.remove(0);
								            lore.add(0, "6");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Gold-Schwert Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
								            clickedInv.setItem(14, item);
								            
								            item = new ItemStack(Material.IRON_SWORD);
											lore.clear();
								            lore.add("5");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Eisen-Schwert");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
								            clickedInv.setItem(15, item);
								            break;
								            
										case "§3Rüstung":
											
											item = new ItemStack(Material.LEATHER_BOOTS);
											LeatherArmorMeta leatherMeta = (LeatherArmorMeta) item.getItemMeta();
											
							                if (playerName.contains("§4")) {
												
												leatherMeta.setColor(Color.RED);
												
											} else if (playerName.contains("§2")) {
												
												leatherMeta.setColor(Color.GREEN);
												
											} else if (playerName.contains("§1")) {
												
												leatherMeta.setColor(Color.BLUE);
												
											} else {
												
												leatherMeta.setColor(Color.YELLOW);
											}
											
											lore.add("1");
								            lore.add("Bronze");
								            leatherMeta.setLore(lore);
								            leatherMeta.setDisplayName("§6Leder-Stiefel");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            clickedInv.setItem(10, item);
								            
								            item = new ItemStack(Material.LEATHER_LEGGINGS);
								            leatherMeta.setDisplayName("§6Leder-Hose");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            clickedInv.setItem(11, item);
											
								            item = new ItemStack(Material.LEATHER_HELMET);
								            leatherMeta.setDisplayName("§6Leder-Helm");
								            item.setItemMeta(leatherMeta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            clickedInv.setItem(12, item);
							                
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
											lore.remove(1);
											lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
								            clickedInv.setItem(14, item);
								            
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								            lore.remove(0);
								            lore.add(0, "5");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
								            clickedInv.setItem(15, item);
								            
								            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
								            lore.remove(0);
								            lore.add(0, "7");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Ketten-Brustpanzer Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
								            clickedInv.setItem(16, item);
								            break;
								            
										case "§3Bögen":
											
											item = new ItemStack(Material.BOW);
								            lore.add("3");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Bogen Level 1");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            clickedInv.setItem(10, item);
								            
								            item = new ItemStack(Material.BOW);
								            lore.remove(0);
								            lore.add(0, "7");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Bogen Level 2");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
								            clickedInv.setItem(11, item);
								            
								            item = new ItemStack(Material.BOW);
								            lore.remove(0);
								            lore.add(0, "13");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Bogen Level 3");
								            item.setItemMeta(meta);
								            item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
								            item.addEnchantment(Enchantment.ARROW_INFINITE, 1);
								            item.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
								            item.addEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
								            clickedInv.setItem(12, item);
								            
								            item = new ItemStack(Material.ARROW);
								            lore.remove(0);
								            lore.add(0, "1");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Pfeil");
								            item.setItemMeta(meta);
								            clickedInv.setItem(14, item);
								            break;
								            
										case "§3Nahrung/Tränke":
										
											item = new ItemStack(Material.BAKED_POTATO);
								            lore.add("3");
								            lore.add("Bronze");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Gebackene Kartoffel");
								            item.setItemMeta(meta);
								            clickedInv.setItem(9, item);
											
								            item = new ItemStack(Material.COOKED_BEEF, 2);
											lore.remove(0);
								            lore.add(0, "7");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Steak");
								            item.setItemMeta(meta);
								            clickedInv.setItem(10, item);
								            
								            item = new ItemStack(Material.APPLE);
								            lore.remove(0);
								            lore.add(0, "1");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Apfel");
								            item.setItemMeta(meta);
								            clickedInv.setItem(11, item);
								            
								            item = new ItemStack(Material.GOLDEN_APPLE);
								            lore.clear();
								            lore.add("2");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Goldener Apfel");
								            item.setItemMeta(meta);
								            clickedInv.setItem(12, item);
								            
							                Potion potion = new Potion(PotionType.INSTANT_HEAL, 2);
							                item = potion.toItemStack(1);
							                lore.clear();
											lore.add("4");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Heilung-Trank");
								            item.setItemMeta(meta);
								            clickedInv.setItem(14, item);
								            
								            potion = new Potion(PotionType.REGEN, 1);
								            item = potion.toItemStack(1);
											lore.remove(0);
								            lore.add(0, "6");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Regeneration-Trank");
								            item.setItemMeta(meta);
								            clickedInv.setItem(15, item);
							                
								            potion = new Potion(PotionType.SPEED, 2);
								            item = potion.toItemStack(1);
											lore.remove(0);
								            lore.add(0, "3");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Schnelligkeit-Trank");
								            item.setItemMeta(meta);
								            clickedInv.setItem(16, item);
							                
								            potion = new Potion(PotionType.STRENGTH, 1);
								            item = potion.toItemStack(1);
							                lore.clear();
								            lore.add("8");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Stärke-Trank");
								            item.setItemMeta(meta);
								            clickedInv.setItem(17, item);
								            break;
								            
										case "§3Fallen":
											
											item = new ItemStack(Material.CARPET, 1, (byte) 8);
								        	lore.add("3");
								        	lore.add("Eisen");
								        	meta.setLore(lore);
								        	meta.setDisplayName("§6Benachrichtigungs-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(10, item);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 15);
								        	meta.setDisplayName("§6Blindheits-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(12, item);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 5);
								        	meta.setDisplayName("§6Übelkeits-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(13, item);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 1);
								        	meta.setDisplayName("§6Hunger-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(14, item);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 4);
								        	meta.setDisplayName("§6Langsamkeits-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(15, item);
								        	
								        	item = new ItemStack(Material.CARPET, 1, (byte) 14);
								        	meta.setDisplayName("§6Schwäche-Falle");
								        	item.setItemMeta(meta);
								        	clickedInv.setItem(16, item);
								        	break;
								        	
										default:
											
											clickedInv = Bukkit.createInventory(p, 27, "§6Shop - Spezial");
											Bedwars.giveShopItems(clickedInv);
											clickedInv.getItem(7).addUnsafeEnchantment(Enchantment.DURABILITY, 10);
											
											item = new ItemStack(Material.ENDER_PEARL);
								            lore.add("7");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Enderperle");
								            item.setItemMeta(meta);
								            clickedInv.setItem(10, item);
								            
								            item = new ItemStack(Material.BLAZE_ROD);
								            lore.remove(0);
								            lore.add(0, "3");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Rettungsplattform");
								            item.setItemMeta(meta);
								            clickedInv.setItem(12, item);
											
								            item = new ItemStack(Material.PAPER);
								            lore.remove(0);
								            lore.add(0, "2");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Mobiler Shop");
								            item.setItemMeta(meta);
								            clickedInv.setItem(14, item);
								            
								            item = new ItemStack(Material.SULPHUR);
								            lore.clear();
								            lore.add("3");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Base-Teleporter");
								            item.setItemMeta(meta);
								            clickedInv.setItem(16, item);
								            
								            item = new ItemStack(Material.SNOW_BALL);
								            lore.clear();
								            lore.add("10");
								            lore.add("Gold");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Granate");
								            item.setItemMeta(meta);
								            clickedInv.setItem(20, item);
								            
								            item = new ItemStack(Material.COMPASS);
								            lore.clear();
								            lore.add("5");
								            lore.add("Eisen");
								            meta.setLore(lore);
								            meta.setDisplayName("§6Spieler-Finder");
								            item.setItemMeta(meta);
								            clickedInv.setItem(24, item);
								            
								            p.openInventory(clickedInv);
								            break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	private void removeEverthing(InventoryClickEvent event, Inventory clickedInv) {
		
		Player p = (Player) event.getWhoClicked();
		p.playSound(p.getLocation(), Sound.STEP_SNOW, 1, 1);
		
		for (byte i = 0; i < 9; i++) {
			
			ItemStack content = clickedInv.getItem(i);
			
			if (content.getType().equals(event.getCurrentItem().getType())) {
				
				content.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
				
			} else {
				
				content.removeEnchantment(Enchantment.DURABILITY);
			}
		}
		
		for (byte i = 9; i < 18; i++) {
			
			clickedInv.clear(i);
		}
	}
	
	private void buy(InventoryClickEvent event, Material material) {
		
		Player p = (Player) event.getWhoClicked();
		ItemStack shopItem = event.getCurrentItem();
		int shopItemAmount = shopItem.getAmount();
		InventoryAction action = event.getAction();
		Inventory inv = p.getInventory();
		int amount = getAmount(inv, material);
		byte price = Byte.parseByte(shopItem.getItemMeta().getLore().get(0));
		ItemStack bought = new ItemStack(shopItem);
		ItemMeta meta = bought.getItemMeta();
		meta.setLore(null);
		bought.setItemMeta(meta);
		
		if (amount >= price) {
			
			if (action.equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
				
				int maximumAmount = price * (64 / shopItemAmount);
				
				if (amount > maximumAmount) {
					
					amount = maximumAmount;
				}
				
				amount -= amount % price;
				bought.setAmount((amount / price) * shopItemAmount);
				inv.addItem(bought);
				
			} else {
				
				inv.addItem(bought);
				amount = price;
			}
			removeItems(amount, inv, material);
		}
	}
	
	private int getAmount(Inventory inv, Material material) {
		
		int amount = 0;
		
		for (ItemStack stack : inv.getContents()) {
			
			if (stack != null && stack.getType() == material) {
				
				amount += stack.getAmount();
			}
		}
		return amount;
	}
	
	private void removeItems(int amount, Inventory inv, Material material) {
		
		for (ItemStack stack : inv.getContents()) {
			
			if (stack != null && stack.getType() == material) {
				
				int newAmount = stack.getAmount() - amount;
				
				if (newAmount > 0) {
					
					stack.setAmount(newAmount);
					break;
					
				} else {
					
					stack.setType(Material.BOWL);
					inv.remove(stack);
					amount = -newAmount;
					
					if (amount == 0) {
						
						break;
					}
				}
			}
		}
	}
}
