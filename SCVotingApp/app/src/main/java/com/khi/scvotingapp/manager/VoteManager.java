package com.khi.scvotingapp.manager;

import android.util.Log;

import com.khi.scvotingapp.data.Candidate;
import com.khi.scvotingapp.data.Position;
import com.khi.scvotingapp.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class VoteManager {
    // OLD METHOD
    public boolean submitVoteOld(int userId, int candidateId) throws SQLException {
        boolean voteSuccess = false;
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "INSERT INTO tbl_votes VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId); // Set the userId
            stmt.setInt(2, candidateId); // Set the candidateId

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                voteSuccess = true; // Vote was successfully inserted
            }

            stmt.close();
            connection.close();
        }

        return voteSuccess;
    }

    public boolean submitVote(ArrayList<Candidate> candidates, ArrayList<Position> positions) throws SQLException {

        StringBuilder queryBuilder = new StringBuilder("INSERT INTO tbl_votes VALUES ");
        boolean hasSelections = false;

        for (int i = 0; i < positions.size(); i++) {
            for (int j = 0; j < candidates.size(); j++) {
                if (candidates.get(j).getPositionID() == positions.get(i).getId()) {
                    if (candidates.get(j).isSelected()) {
                        if (hasSelections)
                            queryBuilder.append(", ");
                        queryBuilder.append("(" + SignInManager.getInstance().getID() + ", " + candidates.get(j).getId() + ")");
                        hasSelections = true;
                    }
                }
            }
        }

        String query = queryBuilder.toString();
        boolean voteSuccess = false;
        Connection connection = DBConnection.connect();

        if (connection != null) {
            PreparedStatement stmt = connection.prepareStatement(query);
            Log.d("vote", query);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                voteSuccess = true; // Vote was successfully inserted
            }

            stmt.close();
            connection.close();
        }



        return voteSuccess;

    }
}
