package Utils;

import sqlService.SqlExec;
public class InitDB {
    private String sql1 = "CREATE TABLE CHAT(POSTTIME INTEGER NOT NULL,FROMUSER TEXT NOT NULL,ISSINGLECAST INTEGER NOT NULL,TOUSER TEXT NOT NULL,ISFILE INTEGER NOT NULL,MESSAGE TEXT NOT NULL,PRIMARY KEY (POSTTIME,FROMUSER));";
    private String sql2 = "CREATE TABLE RELATION(FIRSTID INTEGER NOT NULL,SECONDID INTEGER NOT NULL,PRIMARY KEY (FIRSTID,SECONDID));";
    private String sql3 = "CREATE TABLE USER(ID INTEGER NOT NULL PRIMARY KEY,NAME TEXT NOT NULL,ISSINGLE INTEGER NOT NULL,ISONLINE INTEGER NOT NULL);";
    public InitDB(String target)
    {
        if(target.equals("server"))
        {
            SqlExec.addSql(sql1);
            SqlExec.addSql(sql2);
            SqlExec.addSql(sql3);
        }
        else if(target.equals("client"))
        {
            SqlExec.addSql(sql1);
            SqlExec.addSql(sql2);
            SqlExec.addSql(sql3);
        }
        System.out.println("DB初始化成功！");
    }
}
