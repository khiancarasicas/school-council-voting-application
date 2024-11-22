package com.khi.scvotingapp.manager;

import com.khi.scvotingapp.data.CandidateResult;
import com.khi.scvotingapp.data.PartylistResult;
import com.khi.scvotingapp.db.DBConnection;
import com.khi.scvotingapp.util.AppUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PartylistManager {
    public ArrayList<PartylistResult> getPartylistResult() throws SQLException {
        ArrayList<PartylistResult> partylistResults = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT pl.partylist_name, " +
                    "COUNT(v.id) AS total_votes " +
                    "FROM tbl_partylist pl " +
                    "JOIN tbl_candidates c ON pl.id = c.partylist_id " +
                    "JOIN tbl_votes v ON c.id = v.candidate_id " +
                    "GROUP BY pl.partylist_name " +
                    "ORDER BY total_votes DESC;";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PartylistResult partylistResult = new PartylistResult(rs.getString("partylist_name"), rs.getInt("total_votes"));
                partylistResults.add(partylistResult);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return partylistResults;
    }
}
