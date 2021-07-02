package sqlService;

import Utils.SqlString;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class SqlSelect {


    public static ResultSet Select(String sql,int id){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:my_data/"+id+"/data.db");
            stmt = conn.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try{
            rs = stmt.executeQuery(sql);
        }catch (SQLException e)
        {
            e.printStackTrace();
        }

        return rs;
    }
}
