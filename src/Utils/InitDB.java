package Utils;

import sqlService.SqlQueue;

public class InitDB {
    private String sql1 = "CREATE TABLE CHAT(SERIALNUM INTEGER PRIMARY KEY   AUTOINCREMENT,POSTTIME INTEGER NOT NULL,FROMUSER TEXT NOT NULL,ISSINGLECAST INTEGER NOT NULL,TOUSER TEXT NOT NULL,ISFILE INTEGER NOT NULL,MESSAGE TEXT NOT NULL);";
    private String sql2 = "CREATE TABLE RELATION(FIRSTID INTEGER NOT NULL,SECONDID INTEGER NOT NULL,PRIMARY KEY (FIRSTID,SECONDID));";
    private String sql3 = "CREATE TABLE USER(ID INTEGER NOT NULL PRIMARY KEY,NAME TEXT NOT NULL,ISSINGLE INTEGER NOT NULL,ISONLINE INTEGER NOT NULL);";
    public InitDB(String target)
    {
        if(target.equals("server"))
        {
            SqlQueue.addSql(sql1);
            SqlQueue.addSql(sql2);
            SqlQueue.addSql(sql3);
        }
        else if(target.equals("client"))
        {
            SqlQueue.addSql(sql1);
            SqlQueue.addSql(sql2);
        }
    }
}
