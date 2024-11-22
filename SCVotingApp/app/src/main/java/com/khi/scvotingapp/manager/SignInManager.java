package com.khi.scvotingapp.manager;

import com.khi.scvotingapp.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignInManager {
    private static SignInManager instance;
    private String ID;
    private boolean isLoggedIn = false;
    private String name;

    private SignInManager() {
        // Private constructor to enforce singleton pattern
    }

    // Singleton pattern to ensure only one instance of SignInManager
    public static SignInManager getInstance() {
        if (instance == null) {
            instance = new SignInManager();
        }
        return instance;
    }

    // Method to validate login
    public boolean validateLogin(int loginID, String password) throws SQLException {
        Connection connection = DBConnection.connect();
        if (connection == null) {
            return false; // Database connection failed
        }

        String query = "SELECT id, firstname FROM tbl_students WHERE login_id = ? AND password = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, loginID);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            // Store the user ID securely after successful login
            ID = rs.getString("id");
            name = rs.getString("firstname");
            isLoggedIn = true;
        }

        rs.close();
        stmt.close();
        connection.close();

        return isLoggedIn; // Return true if login was successful
    }

    // Method to get the user ID
    public String getID() {
        if (!isLoggedIn) {
            throw new IllegalStateException("User is not logged in");
        }
        return ID;
    }

    // Method to get the user name
    public String getName() {
        if (!isLoggedIn) {
            throw new IllegalStateException("User is not logged in");
        }
        return name;
    }

    // Method to check if the user is logged in
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    // Method to log out the user
    public void logout() {
        ID = null;
        isLoggedIn = false;
    }
}
