package me.mrfunny.api;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.UUID;

public class MySQLManager {

    private Connection connection;
    String database;
    String user;
    String password;
    String hostname;
    int port;

    public MySQLManager(JavaPlugin plugin){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            hostname = plugin.getConfig().getString("mysql.hostname");
            port = plugin.getConfig().getInt("mysql.port");
            database = plugin.getConfig().getString("mysql.database");
            user = plugin.getConfig().getString("mysql.username");
            password = plugin.getConfig().getString("mysql.password");
            this.connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true", user, password);
            this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS lang(`id` int(16) not null auto_increment,`uuid` varchar(100),`locale` varchar(10),PRIMARY KEY(`id`))")
                    .executeUpdate();
//            this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS bedwars(`id` int(16) not null auto_increment,`uuid` varchar(100),`kills` unsigned int default 0, `beds` unsigned int, `quits` unsigned int," +
//                    "`x6blocks` boolean default true, `speed` boolean default false, `generator` boolean default false, `strength` boolean default false," +
//                    "PRIMARY KEY(`id`))")
//                    .executeUpdate();
        } catch (ClassNotFoundException | SQLException ex){
            plugin.getLogger().severe("MySQL cannot be loaded, disabling plugin: exception: " + ex);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void execute(String sql, Object... args) throws SQLException {
        if(connection.isClosed()){
            this.connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true", user, password);
        }
        try(PreparedStatement ps = this.connection.prepareStatement(sql)) {
            if (args != null) {
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                    if (i - 1 == args.length - 1)
                        break;
                }
            }
            ps.executeUpdate();
        } catch (SQLException e){
            try(PreparedStatement ps = this.connection.prepareStatement(sql)) {
                if (args != null) {
                    for (int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                        if (i - 1 == args.length - 1)
                            break;
                    }
                }
                ps.executeUpdate();
            }
        }
    }

    public ResultSet executeQuery(String sql, Object... args) throws SQLException {
        if(connection.isClosed()){
            this.connection = DriverManager.getConnection("jdbc:mysql://" + hostname + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true", user, password);
        }
        try(PreparedStatement ps = this.connection.prepareStatement(sql)) {
            if (args != null)
                for (int i = 1; i <= args.length; i++) {
                    ps.setObject(i, args[i - 1]);
                    if (i - 1 == args.length - 1)
                        break;
                }

            return ps.executeQuery();
        }catch (SQLException exception){
            try(PreparedStatement ps = this.connection.prepareStatement(sql)) {
                if (args != null)
                    for (int i = 1; i <= args.length; i++) {
                        ps.setObject(i, args[i - 1]);
                        if (i - 1 == args.length - 1)
                            break;
                    }

                return ps.executeQuery();
            }
        }
    }

    public void insert(UUID uuid) throws SQLException {
        execute("INSERT INTO `bedwars` (`uuid`) VALUES(?)", uuid.toString());
    }

    public boolean exists(UUID uuid) throws SQLException {
        ResultSet rs = executeQuery("SELECT `uuid` FROM `lang` WHERE `uuid`=?", uuid.toString());
        return rs.next();
    }

    public void setLocale(UUID uuid, String string) throws SQLException {
        execute("UPDATE `lang` SET `locale`=? WHERE `uuid`=?", string, uuid.toString());
    }

    public String getLocale(UUID uuid) throws SQLException {
//        ResultSet rs = executeQuery("SELECT `locale` FROM `lang` WHERE `uuid`=?", uuid.toString());
//        if(rs.next()){
//            return rs.getString("locale");
//        }
        return "ru_ru";
    }
}
