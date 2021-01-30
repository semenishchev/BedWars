package com.ruverq.rubynex.economics.Economy;

import com.ruverq.rubynex.economics.Database.MySQLThing;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.HashMap;

public class ManagerBank {

    public static MySQLThing mySQLThing;
    public static Connection connection;
    public void setUp(){
        mySQLThing = new MySQLThing();
        connection = mySQLThing.getConnection();

        mySQLThing.createMoneyTableIfNotExists("playerdata");
    }

    public void setUPUser(Player player){
        try {
            String sql = "select username from playerdata where uuid = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, player.getUniqueId().toString());

            ResultSet rs = ps.executeQuery();
            if(!rs.next()){
                System.out.println(" Adding to database | " + player.getName());
                String insertnew = "INSERT INTO playerdata VALUES(null,\"" + player.getUniqueId().toString() + "\",\"" + player.getName() +"\",0,0)";
                Statement statement = connection.createStatement();
                statement.execute(insertnew);
                return;
            }

            if(!rs.getString("username").equalsIgnoreCase(player.getName())){
                String updateNickname = "UPDATE playerdata SET username = " + "\"" + player.getName() + "\" WHERE uuid = \"" + player.getUniqueId().toString() + "\"";
                Statement statement = connection.createStatement();
                statement.execute(updateNickname);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getPlayersValue(String username, String what){

        try {
            String sql = "SELECT " + what + " FROM playerdata WHERE username = \"" + username + "\"";
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery(sql);
            if(!rs.next()){
                return -1;
            }
            return rs.getInt(what);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }


    public boolean removeValueFromPlayer(String username, int amount, String what){
        int oldMoney = getPlayersValue(username, what);

        if(oldMoney == -1){
            return false;
        }

        if(oldMoney - amount < 0){
            return false;
        }

        int newMoney = oldMoney - amount;

        try{
            Statement statement = connection.createStatement();
            String statementstring = "UPDATE playerdata SET " + what + " = " + newMoney + " WHERE username = " + "\"" + username + "\"";
            statement.execute(statementstring);
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public void reset(){
        String reset2 = "UPDATE playerdata SET ruby = 0";
        String reset1 = "UPDATE playerdata SET silver = 0";
        try{
            Statement statementreset = connection.createStatement();
            statementreset.execute(reset1);
            Statement statementreset2 = connection.createStatement();
            statementreset2.execute(reset2);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public boolean addValueToPlayer(String username, int amount, String what){
        int oldMoney = getPlayersValue(username, what);

        if(oldMoney == -1){
            return false;
        }

        int newMoney = oldMoney + amount;

        try{
            Statement statement = connection.createStatement();
            String statementstring = "UPDATE playerdata SET " + what + " = " + newMoney + " WHERE username = " + "\"" + username + "\"";
            statement.execute(statementstring);
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}
