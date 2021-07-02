package sqlService;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;
import Utils.SqlString;

public class SqlQueue extends Thread{
    private static Queue<String> sqlQueue = new LinkedList<>();
    private static ResultSet rs;
    private static Connection conn;
    private static Statement stmt;
    private static String[] sql;
    private static int id=0;
    public SqlQueue(int id){this.id = id;}//有参构造
    public SqlQueue(){}//无参构造
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
            sql = sqlQueue.poll().split("::");
            if(sql[0].equals("false"))//执行更新语句
            {
                try
                {
                    stmt.execute(sql[1]);
                    System.out.println(sql[1]+" 执行成功！");
                }
                catch (SQLException throwables)
                {
                    System.out.println(sql[1]+" 有错!");
                    //throwables.printStackTrace();
                    continue;
                }
            }
            else//执行查询语句
            {
                try
                {
                    rs = stmt.executeQuery(sql[1]);
                    while(rs.next())
                    {
                        int r = rs.getMetaData().getColumnCount();
                        for(int i=1;i<=r;i++)
                        {
                            System.out.print(rs.getString(i)+" ");
                        }
                        System.out.print("\n");
                    }
                }
                catch (SQLException throwables)
                {
                    System.out.println(sql[1]+" 有错!");
                    //throwables.printStackTrace();
                    continue;
                }
            }

        }
    }
    public static void main(String[] args)
    {
        new SqlQueue().start();
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihaome"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nifde"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihaofge"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nifdome"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nsdfhaome"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nsfdaome"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nfdgome"));
        SqlQueue.sqlQueue.add(SqlString.insertchat(1,1,2,0,"nihfdgme"));
        SqlQueue.sqlQueue.add(SqlString.select());
    }
}
