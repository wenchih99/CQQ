package sqlService;

import Utils.SqlString;

import java.sql.*;

public class SqlSelect {
    public static ResultSet Select(String sql,int id){
        //执行sql查询指令
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:my_data/"+id+"/data.db");
            stmt = conn.createStatement();
            System.out.println(sql);
            rs = stmt.executeQuery(sql);
            return rs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return rs;
    }
    public static boolean isExist(String sql,int id)
    {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean flag = false;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:my_data/"+id+"/data.db");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            if(rs.next()){flag=true;};
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return flag;
    }
    public static void main(String[] args)
    {
        SqlSelect.Select(SqlString.selectchat(12),0);
    }
}
