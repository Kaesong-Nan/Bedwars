package de.tubeplay.bedwars.commands;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import de.tubeplay.bedwars.Bedwars;

public class SendCommand implements CommandExecutor {
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (command.getName().equalsIgnoreCase("send")) {
			
            int argsLength = args.length;
			
			if (argsLength != 0) {
				
	            Player p = null;
	            String teamName = null;
	            String senderName;
	        	
	        	if (argsLength == 1) {
	        		
	        		if (sender instanceof Player) {
	        			
	        			p = (Player) sender;
	        			teamName = args[0];
	        		}
	        		
	        	} else {
	        		
	        		p = Bedwars.isPlayerOnline(args[0], sender);
	        		
	        		if (p == null) {
	        			
	        			sender.sendMessage("§6[§3Bedwars§6] §cSpieler konnte nicht gefunden werden!");
	        			return true;
	        		}
	        		teamName = args[1];
	        	}
	        	
	        	if (sender instanceof Player) {
	        		
	        		Player senderP = (Player) sender;
	        		senderName = senderP.getDisplayName();
	        		
	        	} else {
	        		
	        		senderName = "§0Konsole";
	        	}
	        	teamName = teamName.toLowerCase();
	        	teamName = teamName.substring(0, 1).toUpperCase() + teamName.substring(1);
	        	
	        	if (p != null && sender.hasPermission("tubeplay.send")) {
	        		
	        		String realName = p.getName();
					HashSet<String> buildModePlayers = Bedwars.getBuildModePlayers();
					String team = null;
					ItemStack newItem = null;
					Scoreboard board = Bedwars.getScoreboard();
	            	String name = p.getDisplayName();
	            	String map = Bedwars.getMap();
					int redTeamPlayers = board.getTeam("Rot").getSize();
					int greenTeamPlayers = board.getTeam("Grün").getSize();
					int blueTeamPlayers = board.getTeam("Blau").getSize();
					int yellowTeamPlayers = board.getTeam("Gelb").getSize();
					
	                if (name.contains("§4")) {
						
						redTeamPlayers--;
						
						if (redTeamPlayers == 0) {
							
							Bedwars.destroyBedRedAndSendMessage();
						}
						
					} else if (name.contains("§2")) {
						
	                    greenTeamPlayers--;
						
						if (greenTeamPlayers == 0) {
							
							Bedwars.destroyBedGreenAndSendMessage();
						}
						
					} else if (name.contains("§1")) {
						
	                    blueTeamPlayers--;
						
						if (blueTeamPlayers == 0) {
							
							Bedwars.destroyBedBlueAndSendMessage();
						}
						
					} else if (name.contains("§e")){
						
	                    yellowTeamPlayers--;
						
						if (yellowTeamPlayers == 0) {
							
							Bedwars.destroyBedYellowAndSendMessage();
						}
					}
			        
	                if (teamName.equalsIgnoreCase("rot") && !name.contains("§4")) {
						
						team = "§4";
						newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 14);
						
						if (redTeamPlayers == 0) {
							
							Location bedLoc = Bedwars.getRedTeamBedLocation();
							Block bed = bedLoc.getBlock();
					    	bed.setType(Material.BED_BLOCK);
					    	Bedwars.setRedBed(true);
							reviveTeam("§4Rot");
							
							if (map.equalsIgnoreCase("atlantis") || map.equalsIgnoreCase("skyland")) {
								
								bed.setData((byte) 2);
								bedLoc.setZ(bedLoc.getZ() - 1);
								Block bed2 = bedLoc.getBlock();
								bed2.setType(Material.BED_BLOCK);
								bed2.setData((byte) 10);
								
							} else {
								
								switch (map) {
								
								case "Industry":
									
									bed.setData((byte) 3);
									bedLoc.setX(bedLoc.getX() + 1);
									Block bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 11);
									break;
									
								case "Sonic":
									
									bed.setData((byte) 1);
									bedLoc.setX(bedLoc.getX() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 9);
									break;
									
								default:
									
									bed.setData((byte) 0);
									bedLoc.setZ(bedLoc.getZ() + 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 8);
									break;
								}
							}
						}
						redTeamPlayers++;
						
					} else if (teamName.equalsIgnoreCase("grün") && !name.contains("§2")) {
						
						team = "§2";
						newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 13);
						
						if (greenTeamPlayers == 0) {
							
							Location bedLoc = Bedwars.getGreenTeamBedLocation();
							Block bed = bedLoc.getBlock();
					    	bed.setType(Material.BED_BLOCK);
					    	Bedwars.setGreenBed(true);
							reviveTeam("§2Grün");
							
							if (map.equalsIgnoreCase("pilz") || map.equalsIgnoreCase("atlantis")) {
								
								bed.setData((byte) 3);
								bedLoc.setX(bedLoc.getX() + 1);
								Block bed2 = bedLoc.getBlock();
								bed2.setType(Material.BED_BLOCK);
								bed2.setData((byte) 11);
								
							} else if (map.equalsIgnoreCase("skyland") || map.equalsIgnoreCase("sonic")) {
								
								bed.setData((byte) 0);
								bedLoc.setZ(bedLoc.getZ() + 1);
								Block bed2 = bedLoc.getBlock();
								bed2.setType(Material.BED_BLOCK);
								bed2.setData((byte) 8);
								
							} else {
								
								switch (map) {
								
								case "Frozen":
									
									bed.setData((byte) 2);
									bedLoc.setZ(bedLoc.getZ() - 1);
									Block bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 10);
									break;
									
								default:
									
									bed.setData((byte) 1);
									bedLoc.setX(bedLoc.getX() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 9);
									break;
								}
							}
						}
	                    greenTeamPlayers++;
						
					} else if (teamName.equalsIgnoreCase("blau") && !name.contains("§1")) {
						
						team = "§1";
						newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 11);
						
						if (blueTeamPlayers == 0) {
							
							Location bedLoc = Bedwars.getBlueTeamBedLocation();
							Block bed = bedLoc.getBlock();
					    	bed.setType(Material.BED_BLOCK);
					    	Bedwars.setBlueBed(true);
							reviveTeam("§1Blau");
							
							if (map.equalsIgnoreCase("phizzle") || map.equalsIgnoreCase("skyland")) {
								
								bed.setData((byte) 3);
								bedLoc.setX(bedLoc.getX() + 1);
								Block bed2 = bedLoc.getBlock();
								bed2.setType(Material.BED_BLOCK);
								bed2.setData((byte) 11);
								
							} else {
								
								switch (map) {
								
								case "Industry":
									
									bed.setData((byte) 0);
									bedLoc.setZ(bedLoc.getZ() + 1);
									Block bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 8);
									break;
									
								case "Sonic":
									
									bed.setData((byte) 2);
									bedLoc.setZ(bedLoc.getZ() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 10);
									break;
									
								default:
									
									bed.setData((byte) 1);
									bedLoc.setX(bedLoc.getX() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 9);
									break;
								}
							}
						}
	                    blueTeamPlayers++;
						
					} else if (teamName.equalsIgnoreCase("gelb") && !name.contains("§e")) {
						
						team = "§e";
						newItem = new ItemStack(Material.STAINED_CLAY, 1, (byte) 4);
						
						if (yellowTeamPlayers == 0) {
							
							Location bedLoc = Bedwars.getYellowTeamBedLocation();
							Block bed = bedLoc.getBlock();
					    	bed.setType(Material.BED_BLOCK);
					    	Bedwars.setYellowBed(true);
							reviveTeam("§eGelb");
							
							if (map.equalsIgnoreCase("sonic") || map.equalsIgnoreCase("frozen")) {
								
								bed.setData((byte) 3);
								bedLoc.setX(bedLoc.getX() + 1);
								Block bed2 = bedLoc.getBlock();
								bed2.setType(Material.BED_BLOCK);
								bed2.setData((byte) 11);
								
							} else {
								
								switch (map) {
								
								case "Atlantis":
									
									bed.setData((byte) 0);
									bedLoc.setZ(bedLoc.getZ() + 1);
									Block bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 8);
									break;
									
								case "Skyland":
									
									bed.setData((byte) 1);
									bedLoc.setX(bedLoc.getX() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 9);
									break;
									
								default:
									
									bed.setData((byte) 2);
									bedLoc.setZ(bedLoc.getZ() - 1);
									bed2 = bedLoc.getBlock();
									bed2.setType(Material.BED_BLOCK);
									bed2.setData((byte) 10);
									break;
								}
							}
						}
	                    yellowTeamPlayers++;
						
					} else if (teamName.equalsIgnoreCase("spec") && !name.contains("§7")) {
						
						if (Bedwars.isGameStarted()) {
							
							Bedwars.clearInventoryCompletely(p);
							board.getTeam("ZSpectator").addEntry(realName);
							Bedwars.spectate(p);
							p.setDisplayName("§7" + realName + "§r");
							Bedwars.findOutIfSomeoneWon(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
							updateScores(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
							
							if (argsLength == 1) {
								
								p.sendMessage("§6[§3Bedwars§6] §aDu hast dich in den Spectator-Mode gesetzt!");
								
							} else {
								
								p.sendMessage("§6[§3Bedwars§6] §aDu wurdest von " + senderName + " §ain den Spectator-Mode gesetzt!");
								sender.sendMessage("§6[§3Bedwars§6] §aDie Person wurde in den Spectator-Mode gesetzt!");
							}
							
							if (buildModePlayers.contains(realName)) {
	        					
								removeFromBuildMode(p);
	        				}
							return true;
							
						} else {
							
							sender.sendMessage("§6[§3Bedwars§6] §cEs gibt keine Spectator, das Spiel ist noch nicht gestartet!");
							return true;
						}
						
					} else {
						
						sender.sendMessage("§6[§3Bedwars§6] §cUngültiges Team oder die Person ist/du bist schon in diesem Team!");
						return true;
					}
	                
	                if (team != null) {
	                	
	                	Inventory inv = p.getInventory();
	                	p.setGameMode(GameMode.SURVIVAL);
	                	board.getTeam(teamName).addEntry(realName);
	                	
	                	if (argsLength == 1) {
							
							p.sendMessage("§6[§3Bedwars§6] §aDu hast dich in das Team " + team + teamName + " §agesetzt!");
							
						} else {
							
							p.sendMessage("§6[§3Bedwars§6] §aDu wurdest von " + senderName + " §ain das Team " + team + teamName + " §agesetzt!");
							sender.sendMessage("§6[§3Bedwars§6] §aDie Person wurde in das Team " + team + teamName + " §agesetzt!");
						}
	                	
	                    if (Bedwars.isGameStarted()) {
	                    	
	                    	Bedwars.clearInventoryCompletely(p);
	                    	p.removePotionEffect(PotionEffectType.INVISIBILITY);
	                    	
	                    	for (Player op : Bukkit.getOnlinePlayers()) {
	    						
	    						op.showPlayer(p);
	    					}
	                        updateScores(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
	                        Bedwars.findOutIfSomeoneWon(redTeamPlayers, greenTeamPlayers, blueTeamPlayers, yellowTeamPlayers);
	                    	
	                    } else {
	                    	
	                    	ItemMeta metaTeam = newItem.getItemMeta();
							metaTeam.setDisplayName("§6Teamauswahl");
							newItem.setItemMeta(metaTeam);
							inv.setItem(0, newItem);
	                    }
	                    p.setDisplayName(team + realName + "§r");
	                    
	                    if (Bedwars.isGameStarted()) {
	                    	
	                    	p.teleport(Bedwars.getSpawnLocation(p.getDisplayName()));
	                    }
	                    
	                    if (buildModePlayers.contains(realName)) {
	    					
	                    	Bedwars.clearInventoryCompletely(p);
	    					removeFromBuildMode(p);
	    					
	    					if (!Bedwars.isGameStarted()) {
	    						
	    						Bedwars.giveLobbyItems(p.getInventory(), p);
	    					}
	    				}
	                    return true;
	                }
	        	}
			}
		}
		return false;
	}
	
	private void reviveTeam(String team) {
		
		Scoreboard board = Bedwars.getScoreboard();
		Objective ob = board.getObjective(DisplaySlot.SIDEBAR);
		board.resetScores("§c✘ " + team);
		ob.getScore("§a✔ " + team).setScore(1);
	}
	
	private void removeFromBuildMode(Player p) {
		
		Bedwars.getBuildModePlayers().remove(p.getName());
		p.sendMessage("§6[§3Bedwars§6] §cDu bist nun nicht mehr im Bau-Modus!");
	}
	
	private void updateScores(int redTeamPlayers, int greenTeamPlayers, int blueTeamPlayers, int yellowTeamPlayers) {
		
		Objective ob = Bedwars.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
		
		if (Bedwars.isRedTeamBed()) {
			
			ob.getScore("§a✔ §4Rot").setScore(redTeamPlayers);
			
		} else {
			
			ob.getScore("§c✘ §4Rot").setScore(redTeamPlayers);
		}
        
        if (Bedwars.isGreenTeamBed()) {
			
			ob.getScore("§a✔ §2Grün").setScore(greenTeamPlayers);
			
		} else {
			
			ob.getScore("§c✘ §2Grün").setScore(greenTeamPlayers);
		}
        
        if (Bedwars.isBlueTeamBed()) {
			
			ob.getScore("§a✔ §1Blau").setScore(blueTeamPlayers);
			
		} else {
			
			ob.getScore("§c✘ §1Blau").setScore(blueTeamPlayers);
		}
        
        if (Bedwars.isYellowTeamBed()) {
			
			ob.getScore("§a✔ §eGelb").setScore(yellowTeamPlayers);
			
		} else {
			
			ob.getScore("§c✘ §eGelb").setScore(yellowTeamPlayers);
		}
	}
}
