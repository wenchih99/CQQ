package Utils;

public class SqlString {
    public static String insertuser(int id,String name,int isSingle,int onLine)//插入用户
    {
        String sql = "INSERT INTO USER (ID,NAME,ISSINGLE,ISONLINE) VALUES ("+id+",'"+name+"',"+isSingle+","+onLine+");";
        return sql;
    }
    public static String insertrelation(int firstid,int secondid)//插入用户好友关系
    {
        String sql = "INSERT INTO RELATION (FIRSTID,SECONDID) VALUES ("+firstid+","+secondid+");";
        return sql;
    }
    public static String insertfile()//插入文件
    {
        return null;
    }
    public static String insertchat(long time,int from,int isSingleCast,int to,int isFile,String msg)//插入聊天信息
    {
        String sql = "INSERT INTO CHAT (POSTTIME,FROMUSER,ISSINGLECAST,TOUSER,ISFILE,MESSAGE) VALUES ("+time+","+from+","+isSingleCast+","+to+","+isFile+",'"+msg+"');";
        return sql;
    }
    public static String updateuser(int id,String name,int onLine)//更新用户信息
    {
        String sql = "UPDATE USER SET NAME='"+name+"',ISONLINE="+onLine+" WHERE USER.ID = "+id+";";
        return sql;
    }
    public static String updateuser(int id,int onLine)//更新用户信息
    {
        String sql = "UPDATE USER SET ISONLINE="+onLine+" WHERE USER.ID = "+id+";";
        return sql;
    }
    public static String deleterelation(int firstid,int secondid)//删除用户好友关系
    {
        String sql = "DELETE FROM RELATION WHERE RELATION.FIRSTID="+firstid+" AND RELATION.SECONDID="+secondid+";";
        return sql;
    }
    public static String selectchat(int id)//筛选指定用户的聊天记录
    {
        String sql = "SELECT * FROM CHAT WHERE CHAT.FROMUSER = "+id+" OR CHAT.TOUSER = "+id+" ORDER BY POSTTIME;";
        return sql;
    }
    public static String selectrelation(int id)//筛选指定用户的好友
    {
        String sql = "SELECT * FROM RELATION WHERE RELATION.FIRSTID = "+id+";";
        return sql;
    }
    public static String selectuser(int id)//筛选指定用户的好友的信息
    {
        String sql = "SELECT * FROM USER WHERE USER.ID IN (SELECT RELATION.SECONDID FROM RELATION WHERE RELATION.FIRSTID = "+id+");";
        return sql;
    }
    public static String selectonlineuser(int id)//给该用户的在线好友发消息
    {
        String sql = "SELECT USER.ID FROM USER WHERE USER.ID IN (SELECT RELATION.SECONDID FROM RELATION WHERE RELATION.FIRSTID = "+id+") AND USER.ISONLINE = 1;";
        return sql;
    }
    public static String isuserexist(int id)//判断某个用户是否存在
    {
        String sql = "SELECT USER.ID FROM USER WHERE USER.ID = "+id+";";
        return sql;
    }

    public static void main(String[] args)
    {
        //System.out.println(SqlString.insertchat(2,0,1,0,"nihao a"));
        System.out.println(SqlString.updateuser(2,"MIKE",0));
    }
}

