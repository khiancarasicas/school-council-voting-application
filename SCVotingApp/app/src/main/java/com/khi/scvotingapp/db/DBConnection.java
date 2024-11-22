package com.khi.scvotingapp.db;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static String ip = "";
    private static final String port = "1433";
    private static final String Classes = "net.sourceforge.jtds.jdbc.Driver";
    private static final String database = "DB_VoteSystem";
    private static final String username = "khian2";
    private static final String password = "khian123";

    public static Connection connect() {
        Connection connection = null;
        String ConnURL;

        try {
            Class.forName(Classes);
            ConnURL = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + database + ";user=" + username + ";password=" + password + ";encrypt=true;trustServerCertificate=true;";
            DriverManager.setLoginTimeout(10);
            connection = DriverManager.getConnection(ConnURL);
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }

        return connection;
    }


    public static void setIP(String ip) {
        DBConnection.ip = ip;
    }

}
