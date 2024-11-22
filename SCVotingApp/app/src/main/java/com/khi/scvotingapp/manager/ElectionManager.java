package com.khi.scvotingapp.manager;

import com.khi.scvotingapp.data.Election;
import com.khi.scvotingapp.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ElectionManager {

    // Method to retrieve election data (title, description, start date, end date, and current date)
    public Election getElectionDetails() throws SQLException {
        Election election = null;
        Connection connection = DBConnection.connect();

        if (connection != null) {
            // SQL query to get election details and current date from SQL server
            String query = "SELECT *, GETDATE() AS date_now FROM tbl_election WHERE id = 1";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Create Election object with retrieved data
                election = new Election(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("start_date"),
                        rs.getDate("end_date"),
                        rs.getDate("date_now")  // Fetch current date from SQL
                );
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return election; // Return the election details including current date
    }

    // Method to check if the election has started or ended based on current date from SQL
    public String checkElectionStatus(Election election) {
        Date currentDate = election.getCurrentDate();  // Use the date from SQL

        // Compare current date with start and end dates
        if (currentDate.before(election.getStartDate())) {
            return "The election hasn't started yet!";
        } else if (currentDate.after(election.getEndDate())) {
            return "The election has ended.";
        } else {
            return "The election is ongoing.";
        }
    }
}
