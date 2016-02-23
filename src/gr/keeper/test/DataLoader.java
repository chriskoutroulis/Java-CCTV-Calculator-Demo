package gr.keeper.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

public class DataLoader {
	private static final String DATABASE_DRIVER_H2 = "org.h2.Driver";
	private static final String DATABASE_DRIVER_MYSQL = "com.mysql.jdbc.Driver"; // For
																					// use
																					// with
																					// MySQL
	private static final String DATABASE_URL_H2 = "jdbc:h2:file:./data/keeper_demo_copy;IFEXISTS=TRUE"; // do
																										// not
																										// create
																										// a
																										// new
																										// database
																										// if
																										// this
																										// one
																										// does
																										// not
																										// exit
	private static final String DATABASE_URL_MYSQL = "jdbc:mysql://localhost:3306/keeper_demo"; // For
																								// use
																								// with
																								// MySQL
	protected Connection mySQL_database_conn;
	protected Connection H2_database_conn;

	public DataLoader() {
		try {
			Class.forName(DATABASE_DRIVER_MYSQL);
			mySQL_database_conn = DriverManager.getConnection(
					DATABASE_URL_MYSQL, "sa", "");
			Class.forName(DATABASE_DRIVER_H2);
			H2_database_conn = DriverManager.getConnection(DATABASE_URL_H2,
					"sa", "");
			/*copyFromDatabase();*/ // /Copy the online database to the local one
			checkForChanges();
			mySQL_database_conn.close();
			H2_database_conn.close();
		} catch (Exception ex) {
		}
		
	}

	protected void connectToH2Database() {
		try {
			Class.forName(DATABASE_DRIVER_H2);
			H2_database_conn = DriverManager.getConnection(DATABASE_URL_H2,
					"sa", "");
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, "Δε βρέθηκε η βάση δεδομένων.");
			System.exit(1);
		}
	}
	
	private void checkForChanges() {
		Boolean updateNow = false;
		try{
			Statement statementMysql = mySQL_database_conn.createStatement();
			Statement statementH2 = H2_database_conn.createStatement();
			ResultSet tableNames = statementH2.executeQuery("SELECT table_name FROM  "
					+ "INFORMATION_SCHEMA.TABLES "
					+ "WHERE table_schema = 'PUBLIC' AND table_name <> 'CHECKSUMS'");
			String tableName = null;
			ResultSet checksumResult = null;
			HashMap<String,Long> checksumMap = new HashMap<String,Long>(8);
			while (tableNames.next()){
				tableName = tableNames.getString(1).toLowerCase();
				checksumResult = statementMysql.executeQuery(""
						+ "CHECKSUM TABLE " + tableName + " EXTENDED");
				while (checksumResult.next()) {
					/*JOptionPane.showMessageDialog(null, tableName + "'s checksum is: " 
							+ checksumResult.getLong("checksum"));*/
					checksumMap.put(tableName, checksumResult.getLong("checksum"));
				}				
			}
			ResultSet matchedChecksum = null;
			Statement statementH2Difference = H2_database_conn.createStatement();
			for (String table:checksumMap.keySet()){
				matchedChecksum = statementH2.executeQuery("SELECT checksum FROM checksums "
						+ "WHERE table =  '" + table + "'");
				while (matchedChecksum.next()) {
					/*JOptionPane.showMessageDialog(null, matchedChecksum.getLong("checksum"));*/
					if (checksumMap.get(table) != matchedChecksum.getLong("checksum")) {						 
						statementH2Difference.executeUpdate("UPDATE checksums "
								+ "SET checksum = " + checksumMap.get(table) + 
								" WHERE table = '" + table + "'");
						updateNow = true;
					} 
				}				
			}
			statementH2Difference.close();
			statementH2.close();
			statementMysql.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		} finally {
			if (updateNow) {
				copyFromDatabase();
			}			
		}		
	}

	private void copyFromDatabase() {
		JOptionPane.showMessageDialog(null, "Updading your database.");
		try {
			Statement statement_H2 = H2_database_conn.createStatement();	
			// Delete all records from all tables, in a sequence that is allowed
			// by all the foreign key
			// constraints
			statement_H2.execute("DELETE FROM models_resolutions");
			statement_H2.execute("DELETE FROM models_fps");
			statement_H2.execute("DELETE FROM models");
			statement_H2
					.execute("ALTER TABLE models ALTER COLUMN model_id RESTART WITH 1");
			// Reset the AUTO_INCREMENT value to 1
			statement_H2.execute("DELETE FROM resolutions_fps_combo_bitrates");
			statement_H2.execute("DELETE FROM bitrates");
			statement_H2
					.execute("ALTER TABLE bitrates ALTER COLUMN bitrate_id RESTART WITH 1");
			statement_H2.execute("DELETE FROM resolutions_fps_combo");
			statement_H2
					.execute("ALTER TABLE resolutions_fps_combo ALTER COLUMN "
							+ "resolution_fps_combo_id RESTART WITH 1");
			statement_H2.execute("DELETE FROM resolutions");
			statement_H2
					.execute("ALTER TABLE resolutions ALTER COLUMN resolution_id RESTART WITH 1");
			statement_H2.execute("DELETE FROM fps");
			statement_H2
					.execute("ALTER TABLE fps ALTER COLUMN fps_id RESTART WITH 1");
			statement_H2.close();
			// Copy to TABLE models
			PreparedStatement prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO models VALUES(?,?)");

			Statement statement_mySQL = mySQL_database_conn.createStatement();
			ResultSet result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM models");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				String value2 = result_mySQL.getString(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setString(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE resolutions
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO resolutions VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM resolutions");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				String value2 = result_mySQL.getString(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setString(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE models_resolutions
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO models_resolutions VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM models_resolutions");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				int value2 = result_mySQL.getInt(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setInt(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE fps
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO fps VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL.executeQuery("SELECT * FROM fps");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				String value2 = result_mySQL.getString(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setString(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE models_fps
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO models_fps VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM models_fps");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				int value2 = result_mySQL.getInt(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setInt(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE resolutions_fps_combo
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO resolutions_fps_combo VALUES(?,?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM resolutions_fps_combo");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				int value2 = result_mySQL.getInt(2);
				int value3 = result_mySQL.getInt(3);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setInt(2, value2);
				prepStatement_H2.setInt(3, value3);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE bitrates
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO bitrates VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM bitrates");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				String value2 = result_mySQL.getString(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setString(2, value2);
				prepStatement_H2.executeUpdate();
			}
			// Copy to TABLE resolutions_fps_combo_bitrates
			prepStatement_H2 = H2_database_conn
					.prepareStatement("INSERT INTO resolutions_fps_combo_bitrates VALUES(?,?)");

			statement_mySQL = mySQL_database_conn.createStatement();
			result_mySQL = statement_mySQL
					.executeQuery("SELECT * FROM resolutions_fps_combo_bitrates");
			while (result_mySQL.next()) {
				int value1 = result_mySQL.getInt(1);
				int value2 = result_mySQL.getInt(2);
				prepStatement_H2.setInt(1, value1);
				prepStatement_H2.setInt(2, value2);
				prepStatement_H2.executeUpdate();
			}

			prepStatement_H2.close();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.toString());
		}
	}

	public String[] getModels() {
		
		connectToH2Database();
		
		try {
			Statement statement = H2_database_conn.createStatement();

			ResultSet result2 = statement
					.executeQuery("SELECT COUNT(*) FROM models");
			int size = 0;
			while (result2.next()) {
				size = result2.getInt(1);
			}

			String[] column = new String[size + 1];
			column[0] = " -- ";
			int i = 1;

			ResultSet result = statement
					.executeQuery("SELECT * FROM models ORDER BY model_id");
			while (result.next()) {
				column[i] = result.getString("model");
				i++;
			}
			statement.close();
			H2_database_conn.close();
			return column;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, e.toString());
			return null;
		}
	}

	public ArrayList<String> findResolutionRange(String selectedModel) {
		if (selectedModel == " -- ") {
			return null;
		}

		connectToH2Database();

		try {
			Statement statement = H2_database_conn.createStatement();
			ResultSet result = statement.executeQuery(""
					+ "SELECT resolutions.resolution FROM models "
					+ "NATURAL JOIN models_resolutions "
					+ "NATURAL JOIN resolutions " + "WHERE models.model = '"
					+ selectedModel + "'"
					+ " ORDER BY resolutions.resolution_id");
			ArrayList<String> matchedResolutions = new ArrayList<String>();
			while (result.next()) {
				matchedResolutions.add(result.getString("resolution"));
			}

			statement.close();
			H2_database_conn.close();
			return matchedResolutions;

		} catch (SQLException e) {
			/* JOptionPane.showMessageDialog(null, e.toString()); */
			System.err.println(e.toString());
			return null;
		}

	}

	public ArrayList<String> findFrameRange(String selectedModel,
			String selectedResolution) {
		if (selectedResolution == " -- ") {
			return null;
		}
		connectToH2Database();
		try {
			Statement statement = H2_database_conn.createStatement();
			ResultSet result = statement.executeQuery(""
					+ "SELECT fps.fps FROM models "
					+ "NATURAL JOIN models_fps " + "NATURAL JOIN fps "
					+ "WHERE models.model = '" + selectedModel + "' "
					+ "ORDER BY fps.fps_id");
			ArrayList<String> matchedFrames = new ArrayList<String>();
			while (result.next()) {
				matchedFrames.add(result.getString("fps"));
			}

			statement.close();
			H2_database_conn.close();
			return matchedFrames;

		} catch (SQLException e) {
			/* JOptionPane.showMessageDialog(null, e.toString()); */
			System.err.println(e.toString());
			return null;
		}

	}

	public ArrayList<String> findBitrateRange(String selectedFps,
			String selectedResolution) {
		if (selectedFps == " -- ") {
			return null;
		}
		connectToH2Database();
		try {
			Statement statement = H2_database_conn.createStatement();
			ResultSet result = statement
					.executeQuery(""
							+ "SELECT bitrates.bitrate FROM resolutions "
							+ "INNER JOIN resolutions_fps_combo "
							+ "ON resolutions.resolution_id = resolutions_fps_combo.resolution_id "
							+ "INNER JOIN fps ON resolutions_fps_combo.fps_id = fps.fps_id "
							+ "INNER JOIN resolutions_fps_combo_bitrates "
							+ "ON resolutions_fps_combo.resolution_fps_combo_id = "
							+ "resolutions_fps_combo_bitrates.resolution_fps_combo_id "
							+ "INNER JOIN bitrates "
							+ "ON resolutions_fps_combo_bitrates.bitrate_id = bitrates.bitrate_id "
							+ "WHERE resolutions.resolution = '"
							+ selectedResolution + "' " + "AND fps.fps = '"
							+ selectedFps + "' "
							+ "ORDER BY bitrates.bitrate_id");
			ArrayList<String> matchedBitrates = new ArrayList<String>();
			while (result.next()) {
				matchedBitrates.add(result.getString("bitrate"));
			}

			statement.close();
			H2_database_conn.close();
			return matchedBitrates;

		} catch (SQLException e) {
			/* JOptionPane.showMessageDialog(null, e.toString()); */
			System.err.println(e.toString());
			return null;
		}

	}

}
