package sqlService;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;

public class SqlExec extends Thread{
    public static Queue<String> sqlQueue = new LinkedList<>();
    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static String sql;
    private static int id=0;
    public SqlExec(int id){
        this.id = id;
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:my_data/"+id+"/data.db");
            stmt = conn.createStatement();
        } catch (SQLException|ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }//有参构造
    public static void addSql(String sql)
    {
        sqlQueue.add(sql);
    }
    public void run()
    {
        System.out.println("sql线程启动成功！");
        while(true)
        {
            if(sqlQueue.isEmpty()){
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            try {
                sql=sqlQueue.poll();
                stmt.execute(sql);
                System.out.println(sql+" 执行成功！");
            } catch (SQLException throwables) {
                System.out.println(sql+" 有错!");
                //throwables.printStackTrace();
                continue;
            }
        }
    }
    public static ResultSet Select(String sql){
        //执行sql查询指令
        try {
            rs = stmt.executeQuery(sql);
            System.out.println(sql+"执行成功！");
            return rs;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rs;
    }
    public static boolean isExist(String sql)
    {
        boolean flag = false;
        try {
            rs = stmt.executeQuery(sql);
            if(rs.next()){flag=true;};
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }
    public static boolean isOnline(String sql)
    {
        boolean flag = false;
        try {
            rs = stmt.executeQuery(sql);
            while(rs.next())
            {
                if(rs.getInt(1)==1)
                {
                    flag=true;
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return flag;
    }
    public static void main(String[] args)
    {
    }
}
