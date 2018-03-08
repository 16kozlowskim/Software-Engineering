package ai.api.examples;

import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataStore {

	private static final HashMap<String, String> companyInfo = DataBridge.fillCompany();
	private static final HashMap<String, String> sectorNumbers = getSectorNumbers();
	private static HashMap<String, Double> rollingAverage = fillRollingAvg();

	public static HashMap<String, Double> fillRollingAvg() {

		HashMap<String, Double> rolling = new HashMap<>();

		companyInfo.forEach((k, v) -> {
			rolling.put(v, 0.0);
		});

		return rolling;
	}

	public static double getRollingAvg(String symbol) {
		return rollingAverage.get(symbol);
	}

	public static void updateRollingAvg(String symbol, double newAvg) {
		rollingAverage.replace(symbol, newAvg);
	}

	public static HashMap<String, String> getCompanyInfo() {
		return companyInfo;
	}

	public static String getSectorNum(String name) {
		return sectorNumbers.get(name);
	}

	public static HashMap<String, String> getSectorNumbers() {

		HashMap<String, String> sectorNum = new HashMap<String, String>();

		sectorNum.put("Aerospace & Defense", "2710");
		sectorNum.put("Alternative Energy", "0580");
		sectorNum.put("Automobiles & Parts", "3350");
		sectorNum.put("Banks", "8350");
		sectorNum.put("Beverages", "3530");
		sectorNum.put("Chemicals", "1350");
		sectorNum.put("Construction & Materials", "2350");
		sectorNum.put("Electricity", "7530");
		sectorNum.put("Electronic & Electrical", "2730");
		sectorNum.put("Equity Investment Instruments", "8980");
		sectorNum.put("Financial Services", "8770");
		sectorNum.put("Fixed Line Telecom", "6530");
		sectorNum.put("Food & Drug Retailers", "5330");
		sectorNum.put("Food Producers", "3570");
		sectorNum.put("Forestry & Paper", "1730");
		sectorNum.put("Gas, Water & Multiutilities", "7570");
		sectorNum.put("General Industrials", "2720");
		sectorNum.put("General Retailers", "5370");
		sectorNum.put("Health Care Equipment & Services", "4530");
		sectorNum.put("Household Goods & Home Construction", "3720");
		sectorNum.put("Industrial Enginnering", "2750");
		sectorNum.put("Industrial Metals & Mining", "1750");
		sectorNum.put("Industrial Transportation", "2770");
		sectorNum.put("Leisure Goods", "3740");
		sectorNum.put("Life Insurance", "8570");
		sectorNum.put("Media", "5550");
		sectorNum.put("Mining", "1770");
		sectorNum.put("Mobile Telecommunications", "6570");
		sectorNum.put("Nonequity Investment Instruments", "8990");
		sectorNum.put("Nonlife Insurance", "8530");
		sectorNum.put("Oil & Gas Producers", "0530");
		sectorNum.put("Oil Equipment & Services", "0570");
		sectorNum.put("Personal Goods", "3760");
		sectorNum.put("Pharmaceuticals & Biotechnology", "4570");
		sectorNum.put("Real Estate Investment & Services", "8630");
		sectorNum.put("Real Estate Investment Trusts", "8670");
		sectorNum.put("Software & Computer Sertvices", "9530");
		sectorNum.put("Support Services", "2790");
		sectorNum.put("Technology Hardware & Equipment", "9570");
		sectorNum.put("Tobacco", "3780");
		sectorNum.put("Travel & Leisure", "5750");
		return sectorNum;
	}

	public static void initDB() throws SQLException {

		Connection conn = getConnection();
		Statement s = conn.createStatement();

		ResultSet rs = s.executeQuery("select count(*) from company");

		if (rs.next() && rs.getInt(1) == 0) {
			conn.close();
			resetDB();
		}
		conn.close();

	}

	public static ArrayList<String> getFavouriteCompanies(int num) {
		Connection conn = getConnection();
		Statement s = null;
		ArrayList<String> favourites = new ArrayList<String>();

		try {
			s = conn.createStatement();
			String query = "select name from company order by count desc limit " + num;
			ResultSet rs = s.executeQuery(query);

			while (rs.next()) {
				favourites.add(rs.getString(1));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favourites;
	}

	public static ArrayList<String> getFavouriteSectors(int num) {
		Connection conn = getConnection();
		Statement s = null;
		ArrayList<String> favourites = new ArrayList<String>();
		try {
			s = conn.createStatement();

			String query = "select name from sector order by count desc limit " + num;
			ResultSet rs = s.executeQuery(query);

			while (rs.next()) {
				favourites.add(rs.getString(1));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favourites;
	}

	public static ArrayList<String> getFavouriteAttributes(int num) {
		Connection conn = getConnection();
		Statement s = null;
		ArrayList<String> favourites = new ArrayList<String>();
		try {
			s = conn.createStatement();

			String query = "select name from attribute order by count desc limit " + num;
			ResultSet rs = s.executeQuery(query);

			while (rs.next()) {
				favourites.add(rs.getString(1));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return favourites;
	}

	public static void incrementCompany(String name){
		try {
			Connection conn = getConnection();
			Statement s = null;
			s = conn.createStatement();
			s.executeUpdate("update company set count = count + 1 where name = '"+name+"'");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void incrementSector(String name){
		try {
			Connection conn = getConnection();
			Statement s = null;
			s = conn.createStatement();
			s.executeUpdate("update sector set count = count + 1 where name = '"+name+"'");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void incrementAttribute(String name){
		try {
			Connection conn = getConnection();
			Statement s = null;
			s = conn.createStatement();
			s.executeUpdate("update attribute set count = count + 1 where name = '"+name+"'");
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void resetDB() throws SQLException {
		Connection conn = getConnection();
		Statement s = conn.createStatement();

		s.executeUpdate("drop table company");
		s.executeUpdate("drop table attribute");
		s.executeUpdate("drop table sector");
		s.executeUpdate("create table company (name string, count integer)");
		s.executeUpdate("create table attribute (name string, count integer)");
		s.executeUpdate("create table sector (name string, count integer)");

		companyInfo.forEach((k, v) -> {
			try {
				s.executeUpdate("insert into company values('" + v + "', 0)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		sectorNumbers.forEach((k, v) -> {
			try {
				s.executeUpdate("insert into sector values('" + k + "', 0)");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
		try {
			s.executeUpdate("insert into attribute values('volume', 0)");
			s.executeUpdate("insert into attribute values('price', 0)");
			s.executeUpdate("insert into attribute values('open', 0)");
			s.executeUpdate("insert into attribute values('close', 0)");
			s.executeUpdate("insert into attribute values('day high', 0)");
			s.executeUpdate("insert into attribute values('day low', 0)");
			s.executeUpdate("insert into attribute values('absolute change', 0)");
			s.executeUpdate("insert into attribute values('percentage change', 0)");
			s.executeUpdate("insert into attribute values('day low-high', 0)");
			s.executeUpdate("insert into attribute values('52 week low-high', 0)");
			s.executeUpdate("insert into attribute values('market cap', 0)");
			s.executeUpdate("insert into attribute values('PE ratio', 0)");
			s.executeUpdate("insert into attribute values('dividend yield', 0)");
			s.executeUpdate("insert into attribute values('earnings per share', 0)");
			s.executeUpdate("insert into attribute values('shares outstanding', 0)");
			s.executeUpdate("insert into attribute values('beta', 0)");
			s.executeUpdate("insert into attribute values('institutional ownership', 0)");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn.close();
	}

	public static Connection getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			System.out.println("Database was not found");
		}

		Connection connection = null;
		try {
			// create a database connection

			//connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\ojwoo\\Documents\\Warwick\\CS261\\Coursework\\dialogflow-java-client-master\\samples\\clients\\VirtualTradingAssistant\\src\\main\\java\\ai\\api\\examples\\ai.db");
			//connection = DriverManager.getConnection("jdbc:sqlite:/Users/Michal/Downloads/dialogflow-java-client-master2/samples/clients/VirtualTradingAssistant/src/main/java/ai/api/examples/ai.db");
			//connection = DriverManager.getConnection("jdbc:sqlite:"+ System.getProperty("catalina.base") + "/bin/misc/ai.db");
			connection = DriverManager.getConnection("jdbc:sqlite:"+ System.getProperty("catalina.base") + "\\bin\\misc\\ai.db");


			return connection;
		}
		catch(SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
		return connection;
	}
}