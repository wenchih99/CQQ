package Utils;

import java.util.Calendar;
import java.util.Date;

public class SqlString {
    public static String insertuser(int id,String name,int isSingle,int onLine)//插入用户
    {
        String sql = "false::INSERT INTO USER (ID,NAME,ISSINGLE,ISONLINE) VALUES ("+id+",'"+name+"',"+isSingle+","+onLine+");";
        return sql;
    }
    public static String insertrelation(int firstid,int secondid)//插入用户好友关系
    {
        if(firstid>secondid)
        {
            int tmp = firstid;
            firstid = secondid;
            secondid = tmp;
        }
        String sql = "false::INSERT INTO RELATION (FIRSTID,SECONDID) VALUES ("+firstid+","+secondid+");";
        return sql;
    }
    public static String insertfile()//插入文件
    {
        return null;
    }
    public static String insertchat(int from,int isSingleCast,int to,int isFile,String msg)//插入聊天信息
    {
        long time = new Date().getTime();
        String sql = "false::INSERT INTO CHAT (POSTTIME,FROMUSER,ISSINGLECAST,TOUSER,ISFILE,MESSAGE) VALUES ("+time+","+from+","+isSingleCast+","+to+","+isFile+",'"+msg+"');";
        return sql;
    }
    public static String updateuser(int id,String name,int onLine)//更新用户信息
    {
        String sql = "false::UPDATE USER SET NAME='"+name+"',ISONLINE="+onLine+" WHERE ID = "+id+";";
        return sql;
    }
    public static String deleterelation(int firstid,int secondid)//删除用户好友关系
    {
        if(firstid>secondid)
        {
            int tmp = firstid;
            firstid = secondid;
            secondid = tmp;
        }
        String sql = "false::DELETE FROM RELATION WHERE FIRSTID="+firstid+" AND SECONDID="+secondid+";";
        return sql;
    }
    public static String select()
    {
        return "true::select * from chat;";
    }
    public static void main(String[] args)
    {
        System.out.println(SqlString.insertchat(2,0,1,0,"nihao a"));
        System.out.println(SqlString.insertuser(2,"wenchih",1,1));
        System.out.println(SqlString.insertrelation(2,1));
        System.out.println(SqlString.updateuser(2,"BOB1",0));
        System.out.println(SqlString.deleterelation(2,1));
    }
}
