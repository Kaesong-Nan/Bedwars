package de.tubeplay.bedwars;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

public class MySQL {
	
	private Connection connection;
	private Logger logger = Bukkit.getLogger();
	
	public MySQL(String password) {
		
		LocalDate date = LocalDate.now();
		int year = date.getYear();
		int month = date.getMonth().getValue();
		
		logger.info("Stelle Verbindung zur MySQl-Datenbank her...");
		
		try {
			
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bedwars?autoReconnect=true", "root", password);
			
		} catch (SQLException e) {
			
			logger.severe("Verbindung mit MySQL-Datenbank konnte nicht hergestellt werden!");
			e.printStackTrace();
			logger.severe("Server wird heruntergefahren!");
			Bukkit.shutdown();
		}
		update("CREATE TABLE IF NOT EXISTS UUIDs (UUID VARCHAR(255), Playername VARCHAR(255))");
		update("CREATE TABLE IF NOT EXISTS StatsGlobal (UUID VARCHAR(255), Points INT, DestroyedBeds INT, Kills INT, Deaths INT, WonGames MEDIUMINT, PlayedGames MEDIUMINT)");
		update("CREATE TABLE IF NOT EXISTS StatsMonthly (UUID VARCHAR(255), Points INT, DestroyedBeds INT, Kills INT, Deaths INT, WonGames MEDIUMINT, PlayedGames MEDIUMINT)");
		update("CREATE TABLE IF NOT EXISTS StatsDaily (UUID VARCHAR(255), Points INT, DestroyedBeds INT, Kills INT, Deaths INT, WonGames MEDIUMINT, PlayedGames MEDIUMINT)");
		update("CREATE TABLE IF NOT EXISTS LastResets (StatsTable VARCHAR(255), Date VARCHAR(255))");
		update("CREATE TABLE IF NOT EXISTS Playtime (UUID VARCHAR(255), Minutes MEDIUMINT)");
		update("CREATE TABLE IF NOT EXISTS GommeShop (UUID VARCHAR(255), Enabled TINYINT)");
		//update("CREATE TABLE IF NOT EXISTS Secrets (UUID VARCHAR(255), 1 TINYINT, 2 TINYINT, 3 TINYINT, 4 TINYINT)");
		
		ResultSet rs = query("SELECT * FROM LastResets WHERE StatsTable='Monthly'");
		
		if (exists(rs)) {
			
			String dateInDatabase = null;
			
			try {
				
				dateInDatabase = rs.getString("Date");
				
			} catch (SQLException e) {
				
				errorReadingDatabase(e);
			}
			
			if (Short.parseShort(dateInDatabase.substring(0, 4)) < year || Byte.parseByte(dateInDatabase.substring(5, 7)) < month) {
				
				update("UPDATE LastResets SET Date='" + date + "' WHERE StatsTable='Monthly'");
				update("TRUNCATE Table StatsMonthly");
			}
			
		} else {
			
			update("INSERT INTO LastResets VALUES ('Monthly', '" + date + "')");
		}
		
		rs = query("SELECT * FROM LastResets WHERE StatsTable='Daily'");
		
		if (exists(rs)) {
			
			String dateInDatabase = null;
			
			try {
				
				dateInDatabase = rs.getString("Date");
				
			} catch (SQLException e) {
				
				errorReadingDatabase(e);
			}
			
			if (Short.parseShort(dateInDatabase.substring(0, 4)) < year || Byte.parseByte(dateInDatabase.substring(5, 7)) < month || Byte.parseByte(dateInDatabase.substring(8, 10)) < date.getDayOfMonth()) {
				
				update("UPDATE LastResets SET Date='" + date + "' WHERE StatsTable='Daily'");
				update("TRUNCATE Table StatsDaily");
			}
			
		} else {
			
			update("INSERT INTO LastResets VALUES ('Daily', '" + date + "')");
		}
	}
	
	public void disconnect() {
		
		logger.info("Trenne Verbindung zur MySQL-Datenbank...");
		
		try {
			
			if (connection != null) {
				
				connection.close();
			}
			
		} catch (SQLException e) {
			
			logger.severe("Fehler beim Beenden der Verbindung zur MySQL-Datenbank");
			e.printStackTrace();
		}
	}
	
	private void createPlayerIfNotExists(String uuid) {
		
		if (!playerExistsInStats(uuid, "Global")) {
			
			update("INSERT INTO StatsGlobal VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0')");
		}
		
		if (!playerExistsInStats(uuid, "Monthly")) {
			
			update("INSERT INTO StatsMonthly VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0')");
		}
		
		if (!playerExistsInStats(uuid, "Daily")) {
			
			update("INSERT INTO StatsDaily VALUES ('" + uuid + "', '0', '0', '0', '0', '0', '0')");
		}
	}
	
	public void addToMinutes(String uuid, int howMuch) {
		
		if (playerExistsInPlaytime(uuid)) {
			
			update("UPDATE Playtime SET Minutes='" + (getMinutes(uuid) + howMuch) + "' WHERE UUID='" + uuid + "'");
			
		} else {
			
			update("INSERT INTO Playtime VALUES ('" + uuid + "', '" + howMuch + "')");
		}
	}
	
	public boolean playerExistsInPlaytime(String uuid) {
		
		return exists(query("SELECT * FROM Playtime WHERE UUID='" + uuid + "'"));
	}
	
    public int getMinutes(String uuid) {
		
		ResultSet rs = query("SELECT * FROM Playtime WHERE UUID='" + uuid + "'");
		int minutes = 0;
    	
		try {
			
			rs.first();
			minutes = rs.getInt("Minutes");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return minutes;
	}
	
	public boolean playerExistsInGommeShop(String uuid) {
		
		return exists(query("SELECT * FROM GommeShop WHERE UUID='" + uuid + "'"));
	}
	
    public boolean isGommeShopEnabled(String uuid) {
		
		ResultSet rs = query("SELECT * FROM GommeShop WHERE UUID='" + uuid + "'");
		byte enabledValue = 0;
		boolean enabled;
    	
		try {
			
			rs.first();
			enabledValue = rs.getByte("Enabled");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		
		if (enabledValue == 0) {
			
			enabled = false;
			
		} else {
			
			enabled = true;
		}
		return enabled;
	}
    
	public void addToDestroyedBeds(String uuid) {
		
		createPlayerIfNotExists(uuid);
		addToPoints(uuid, 20);
		
		update("UPDATE StatsGlobal SET DestroyedBeds='" + (getDestroyedBeds(uuid, "Global") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET DestroyedBeds='" + (getDestroyedBeds(uuid, "Monthly") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET DestroyedBeds='" + (getDestroyedBeds(uuid, "Daily") + 1) + "' WHERE UUID='" + uuid + "'");
	}
	
    public int getDestroyedBeds(String uuid, String stats) {
		
		ResultSet rs = query("SELECT * FROM Stats" + stats + " WHERE UUID='" + uuid + "'");
		int destroyedBeds = 0;
    	
		try {
			
			rs.first();
			destroyedBeds = rs.getInt("DestroyedBeds");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return destroyedBeds;
	}
    
	public void addToWonGames(String uuid) {
		
		createPlayerIfNotExists(uuid);
		addToPoints(uuid, 50);
		
		update("UPDATE StatsGlobal SET WonGames='" + (getWonGames(uuid, "Global") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET WonGames='" + (getWonGames(uuid, "Monthly") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET WonGames='" + (getWonGames(uuid, "Daily") + 1) + "' WHERE UUID='" + uuid + "'");
	}
    
    public int getWonGames(String uuid, String stats) {
    	
    	ResultSet rs = query("SELECT * FROM Stats" +  stats + " WHERE UUID='" + uuid + "'");
		int wonGames = 0;
    	
		try {
			
			rs.first();
			wonGames = rs.getInt("WonGames");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return wonGames;
    }
    
	public void addToPlayedGames(String uuid) {
		
		createPlayerIfNotExists(uuid);
		
		update("UPDATE StatsGlobal SET PlayedGames='" + (getPlayedGames(uuid, "Global") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET PlayedGames='" + (getPlayedGames(uuid, "Monthly") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET PlayedGames='" + (getPlayedGames(uuid, "Daily") + 1) + "' WHERE UUID='" + uuid + "'");
	}
    
    public int getPlayedGames(String uuid, String stats) {
    	
    	ResultSet rs = query("SELECT * FROM Stats" +  stats + " WHERE UUID='" + uuid + "'");
		int playedGames = 0;
    	
		try {
			
			rs.first();
			playedGames = rs.getInt("PlayedGames");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return playedGames;
    }
    
	public void addToDeaths(String uuid) {
		
		createPlayerIfNotExists(uuid);
		
		update("UPDATE StatsGlobal SET Deaths='" + (getDeaths(uuid, "Global") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET Deaths='" + (getDeaths(uuid, "Monthly") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET Deaths='" + (getDeaths(uuid, "Daily") + 1) + "' WHERE UUID='" + uuid + "'");
	}
    
    public int getDeaths(String uuid, String stats) {
    	
    	ResultSet rs = query("SELECT * FROM Stats" +  stats + " WHERE UUID='" + uuid + "'");
		int deaths = 0;
    	
		try {
			
			rs.first();
			deaths = rs.getInt("Deaths");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return deaths;
    }
    
	public void addToKills(String uuid) {
		
		createPlayerIfNotExists(uuid);
		addToPoints(uuid, 10);
		
		update("UPDATE StatsGlobal SET Kills='" + (getKills(uuid, "Global") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET Kills='" + (getKills(uuid, "Monthly") + 1) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET Kills='" + (getKills(uuid, "Daily") + 1) + "' WHERE UUID='" + uuid + "'");
	}
    
    public int getKills(String uuid, String stats) {
    	
    	ResultSet rs = query("SELECT * FROM Stats" +  stats + " WHERE UUID='" + uuid + "'");
		int kills = 0;
    	
		try {
			
			rs.first();
			kills = rs.getInt("Kills");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return kills;
    }
    
	public void addToPoints(String uuid, int howMuch) {
		
		createPlayerIfNotExists(uuid);
		
		update("UPDATE StatsGlobal SET Points='" + (getPoints(uuid, "Global") + howMuch) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsMonthly SET Points='" + (getPoints(uuid, "Monthly") + howMuch) + "' WHERE UUID='" + uuid + "'");
		update("UPDATE StatsDaily SET Points='" + (getPoints(uuid, "Daily") + howMuch) + "' WHERE UUID='" + uuid + "'");
	}
    
    public int getPoints(String uuid, String stats) {
    	
    	ResultSet rs = query("SELECT * FROM Stats" +  stats + " WHERE UUID='" + uuid + "'");
		int points = 0;
    	
		try {
			
			rs.first();
			points = rs.getInt("Points");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return points;
    }
	
	public void update(String query) {
		
		try {
			
			PreparedStatement ps = connection.prepareStatement(query);
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			
			logger.severe("Fehler beim Aktualisieren der MySQL-Datenbank!");
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String query) {
		
		ResultSet rs = null;
		
		try {
			
			PreparedStatement ps = connection.prepareStatement(query);
			rs = ps.executeQuery();
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return rs;
	}
	
	public String getUUID(String playername) {
		
		String uuid = null;
		ResultSet rs = query("SELECT * FROM UUIDs WHERE Playername='" + playername + "'");
		
		try {
			
			rs.first();
			uuid = rs.getString("UUID");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return uuid;
	}
	
    public String getPlayername(String uuid) {
    	
    	String playername = null;
    	ResultSet rs = query("SELECT * FROM UUIDs WHERE UUID='" + uuid + "'");
		
		try {
			
			rs.first();
			playername = rs.getString("Playername");
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return playername;
    }
	
    public void updateUUID(String uuid, String playername) {
    	
		if (playerExists(uuid)) {
			
			if (getPlayername(uuid) != playername) {
				
				update("UPDATE UUIDs SET Playername='" + playername + "' WHERE UUID='" + uuid + "'");
			}
			
		} else {
			
			update("INSERT INTO UUIDs VALUES ('" + uuid + "', '" + playername + "')");
		}
    }
    
    public boolean playerExists(String uuid) {
    	
    	return exists(query("SELECT * FROM UUIDs WHERE UUID='" + uuid + "'"));
    }
    
	public boolean uuidExists(String playername) {
		
		return exists(query("SELECT * FROM UUIDs WHERE Playername='" + playername + "'"));
	}
	
	public boolean playerExistsInStats(String uuid, String stats) {
		
		return exists(query("SELECT * FROM Stats" + stats + " WHERE UUID='" + uuid + "'"));
	}
	
	public boolean exists(ResultSet rs) {
		
		boolean exists = true;
		
		try {
			
			exists = rs.first();
			
		} catch (SQLException e) {
			
			errorReadingDatabase(e);
		}
		return exists;
	}
	
    public void errorReadingDatabase(SQLException e) {
    	
    	logger.severe("Fehler beim Auslesen der MySQL-Datenbank!");
		e.printStackTrace();
    }
}
