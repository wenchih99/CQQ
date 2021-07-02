package sqlService;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;
import Utils.SqlString;

public class SqlExec extends Thread{
    private static Queue<String> sqlQueue = new LinkedList<>();
    private static ResultSet rs;
    private static Connection conn;
    private static Statement stmt;
    private static String sql;
    private static int id=0;
    public SqlExec(int id){this.id = id;}//有参构造
    public SqlExec(){}//无参构造
    public static void addSql(String sql)
    {
        sqlQueue.add(sql);
    }
    public void run()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:my_data/"+id+"/data.db");
            stmt = conn.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        while(true)
        {
            if(sqlQueue.isEmpty()){continue;}
            try
            {
                stmt.execute(sql);
                System.out.println(sql+" 执行成功！");
            }
            catch (SQLException throwables)
            {
                System.out.println(sql+" 有错!");
                //throwables.printStackTrace();
                continue;
            }
        }
    }
    public static void main(String[] args)
    {
        new SqlExec().start();
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihaome"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nifde"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihaofge"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nifdome"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nsdfhaome"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nsfdaome"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nfdgome"));
        SqlExec.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihfdgme"));
    }
}
