package com.khi.scvotingapp.manager;

import com.khi.scvotingapp.data.Candidate;
import com.khi.scvotingapp.data.CandidateResult;
import com.khi.scvotingapp.data.CandidateVoted;
import com.khi.scvotingapp.db.DBConnection;
import com.khi.scvotingapp.util.AppUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CandidateManager {
    public ArrayList<Candidate> getCandidates() throws SQLException {
        ArrayList<Candidate> candidates = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT tbl_candidates.id, tbl_students.firstname, tbl_students.lastname, tbl_students.middlename, tbl_students.program, tbl_candidates.position_id, tbl_partylist.partylist_name " +
                    "FROM tbl_candidates " +
                    "INNER JOIN tbl_students ON tbl_candidates.student_id = tbl_students.id " +
                    "INNER JOIN tbl_partylist ON tbl_candidates.partylist_id = tbl_partylist.id";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fullName = rs.getString("firstname") + " " + (rs.getString("middlename").equals("N/A") ? "" : rs.getString("middlename")) + " " + rs.getString("lastname");
                fullName = AppUtil.toTitleCase(fullName);
                Candidate candidate = new Candidate(rs.getInt("id"), fullName, rs.getString("program"), rs.getString("partylist_name"), rs.getInt("position_id"));
                candidates.add(candidate);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return candidates;
    }

    public ArrayList<CandidateVoted> getCandidatesVoted() throws SQLException {
        ArrayList<CandidateVoted> candidatesVoted = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT tbl_candidates.position_id, tbl_students.firstname, tbl_students.lastname " +
                    "FROM tbl_votes " +
                    "INNER JOIN tbl_candidates ON tbl_votes.candidate_id = tbl_candidates.id " +
                    "INNER JOIN tbl_positions ON tbl_candidates.position_id = tbl_positions.id " +
                    "INNER JOIN tbl_students ON tbl_candidates.student_id = tbl_students.id " +
                    "WHERE tbl_votes.student_id = ?;";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(SignInManager.getInstance().getID()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                fullName = AppUtil.toTitleCase(fullName);
                CandidateVoted votedCandidate = new CandidateVoted(fullName, rs.getInt("position_id"));
                candidatesVoted.add(votedCandidate);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return candidatesVoted;
    }

    public ArrayList<CandidateResult> getCandidatesResult() throws SQLException {
        ArrayList<CandidateResult> candidatesVoted = new ArrayList<>();
        Connection connection = DBConnection.connect();

        if (connection != null) {
            String query = "SELECT c.id, s.firstname, s.lastname, s.middlename, s.program, c.position_id, p.partylist_name, " +
                    "(SELECT COUNT(v.id) FROM tbl_votes v WHERE v.candidate_id = c.id) AS total_votes " +
                    "FROM tbl_candidates c " +
                    "INNER JOIN tbl_students s ON c.student_id = s.id " +
                    "INNER JOIN tbl_partylist p ON c.partylist_id = p.id " +
                    "ORDER BY total_votes DESC;";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String fullName = rs.getString("firstname") + " " + rs.getString("lastname");
                fullName = AppUtil.toTitleCase(fullName);
                CandidateResult candidateResult = new CandidateResult(fullName, rs.getString("program"), rs.getString("partylist_name"), rs.getInt("position_id"), rs.getInt("total_votes"));
                candidatesVoted.add(candidateResult);
            }

            rs.close();
            stmt.close();
            connection.close();
        }

        return candidatesVoted;
    }
}
