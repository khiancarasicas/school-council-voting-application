package com.khi.scvotingapp.manager;

import com.khi.scvotingapp.data.Position;
import com.khi.scvotingapp.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PositionManager {
    public ArrayList<Position> getAvailablePositions() throws SQLException {
        ArrayList<Position> positions = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT DISTINCT p.* FROM tbl_positions p " +
                    "INNER JOIN tbl_candidates c ON p.id = c.position_id " +
                    "WHERE p.id NOT IN ( " +
                        "SELECT c.position_id " +
                        "FROM tbl_votes v " +
                        "INNER JOIN tbl_candidates c " +
                        "ON v.candidate_id = c.id " +
                        "WHERE v.student_id = ?" +
                    ") " +
                    "ORDER BY p.priority";
            PreparedStatement stmt = connection.prepareStatement(query);
            try {
                stmt.setInt(1, Integer.parseInt(SignInManager.getInstance().getID()));
            } catch (Exception e) {
                return null;
            }
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Position position = new Position(rs.getInt("id"), rs.getString("position_name"), rs.getInt("max_vote"));
                positions.add(position);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return positions;
    }

    public ArrayList<Position> getAllPositions() throws SQLException {
        ArrayList<Position> positions = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT DISTINCT p.* FROM tbl_positions p " +
                    "INNER JOIN tbl_candidates c ON p.id = c.position_id " +
                    "ORDER BY p.priority";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Position position = new Position(rs.getInt("id"), rs.getString("position_name"), rs.getInt("max_vote"));
                positions.add(position);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return positions;
    }

}
