package me.elieraad.ims.db;

import java.sql.*;

/*THIS IS A SINGLETON CLASS TO ENSURE THAT ONE AND ONLY OBJECT OF THE DATABASE WILL BE INSTANTIATED*/
public class MySQL {

    private MySQL() {
    }

    //MySQL object won't be created until getInstance will get called for the first time
    private static class LazyInit {
        private static final MySQL instance = new MySQL();
    }

    public static MySQL getInstance() {
        return LazyInit.instance;
    }

    private Connection conn = null;



    /*
     *
     * Connect to the database
     *
     * DB NAME: InsuranceDB
     * USERNAME: root
     * PASSWORD: root
     * HOST: localhost
     * PORT: 3306
     *
     */
    public void connect(String username, String password) {

        String url = "jdbc:mysql://localhost:3306/InsuranceDB";

        try {
            conn = DriverManager.getConnection(url, username, password);
            //Set auto commit as false.
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("Access Denied\n");
            e.printStackTrace();
            return;
        }
        System.out.println("Access Granted\n");
    }

    public Connection getConn() {
        return conn;
    }

}