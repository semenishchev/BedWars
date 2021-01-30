package com.ruverq.rubynex.economics.Database;

import com.ruverq.rubynex.economics.Main;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLThing {

    Connection connection;
    String name = "RubyNexEconomy";

    public Connection getConnection(){
        try {
            if(connection != null && !connection.isClosed()) return connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FileConfiguration config = Main.getInstance().getConfig();
        String url = config.getString("mySQL.url", "localhost:3306/" + name + "?autoReconnect=true&useSSL=false");
        String username = config.getString("mySQL.username", "root");
        String password = config.getString("mySQL.password", "secret");
        url = "jdbc:mysql://" + url;

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    };

    public void closeConnection() {
        if(connection == null){
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createMoneyTableIfNotExists(String name){

        try{
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS " + name + " (" +
                    "id INTEGER PRIMARY KEY auto_increment," +
                    "uuid VARCHAR(100) NOT NULL," +
                    "username VARCHAR(50) NOT NULL," +
                    "ruby INTEGER NOT NULL," +
                    "silver INTEGER NOT NULL" +
                    ");");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

}
