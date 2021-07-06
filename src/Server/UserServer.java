package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import Utils.*;
import sqlService.SqlExec;

public class UserServer {
    public static Dictionary<Integer, Client> socklist;
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(9999);
        System.out.println("会话服务器启动成功,端口为:"+ss.getLocalPort());
        new FileServer().start();
        socklist = new Hashtable<>();
        new InitDir(0);
        new SqlExec(0).start();//sql语句执行队列
        new InitDB("server");
        while (true)
        {
            String s;
            try
            {
                Client tmp = new Client(ss.accept());
                socklist.put(tmp.userid,tmp);
                tmp.start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                break;
            }
        }
    }
    public static void lsUsers()
    {
        //显示当前用户
        System.out.println("okokokokok");
    }
}
class Client extends Thread
{
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    public String username;//用户名
    public int userid;//用户id
    private Client toClient;//向谁发送消息
    private String message;//命令消息
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        try
        {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            userid = din.readInt();
            username = din.readUTF();//设置当前用户名
            sendmodifynamemsg();
            System.out.println("user:"+userid+" is on line!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new InitDir(userid);//初始化用户目录
        DBSync();//与客户端同步消息
        //更新用户数据库
        sendonlinemsg();
        if(SqlExec.isExist(SqlString.isuserexist(userid)))
        {
            SqlExec.addSql(SqlString.updateuser(userid,username,1));
        }
        else
        {
            SqlExec.addSql(SqlString.insertuser(userid,username,1,1));
        }
    }
    public void run()
    {
        System.out.println(userid+":线程启动成功");
        String[] msg;
        try {
            while(true) {//接收的消息当中不能含有冒号,且消息不能为空
                message = din.readUTF();
                System.out.println("message:" + message);
                msg = message.split("::");
                /*
                 * 对当前收到的message进行分割
                 * msg[0]存的是命令
                 * msg[1]存的是数据*/
                if (msg[0].equals("upload")) {
                    //要求向服务器上传文件
                    //无论给谁发，都加到个人仓库
                    //upload::toid::filename::time::issingle
                    SqlExec.addSql(SqlString.insertchat(Long.parseLong(msg[3]),userid , Integer.parseInt(msg[4]), Integer.parseInt(msg[1]), 1, msg[2]));
                    //group::fromid::toid::isfile::msg::time
                    if(msg[4].equals("1"))
                    {
                        if ((toClient = UserServer.socklist.get(Integer.parseInt(msg[1]))) != null) {
                            toClient.sendMsg("upload::"+userid + "::" + msg[2]+"::"+msg[3]);
                        }
                    }
                    else
                    {
                        sendGroupMsg("group::"+userid+"::"+msg[1]+"::1::"+msg[2]+"::"+msg[3]);
                    }
                } else if (msg[0].equals("download")) {
                    //要求从服务器下载文件
                    //从指定仓库下载文件(指定仓库指的是上传文件的用户的仓库)
                    //download::fromid::filename
                } else if (msg[0].equals("adduser")) {
                    //添加好友
                    //双向添加用户
                    //接收adduser::userid
                    //发送adduser::userid::username
                    SqlExec.addSql(SqlString.insertrelation(userid, Integer.parseInt(msg[1])));
                    SqlExec.addSql(SqlString.insertrelation(Integer.parseInt(msg[1]), userid));
                    if ((toClient = UserServer.socklist.get(Integer.parseInt(msg[1]))) != null) {
                        toClient.sendMsg(msg[0] + "::" + userid+"::"+username);
                        //同步好友信息
                        dout.writeUTF(msg[0]+"::"+toClient.userid+"::"+toClient.username);
                    }
                }else if(msg[0].equals("deletegroup"))
                {
                    //deletegroup::id
                    //只需在服务器中删除即可
                    SqlExec.addSql(SqlString.deleterelation(userid, Integer.parseInt(msg[1])));
                } else if (msg[0].equals("deleteuser")) {
                    //删除好友
                    //双向删除好友
                    //deleteuser::userid
                    SqlExec.addSql(SqlString.deleterelation(userid, Integer.parseInt(msg[1])));
                    SqlExec.addSql(SqlString.deleterelation(Integer.parseInt(msg[1]), userid));
                    if ((toClient = UserServer.socklist.get(Integer.parseInt(msg[1]))) != null) {
                        //deleteuser::userid::username
                        toClient.sendMsg(msg[0] + "::" + userid+"::"+username);
                    }
                }else if(msg[0].equals("buildgroup"))
                {
                    //buildgroup::id::name
                    SqlExec.addSql(SqlString.insertrelation(userid, Integer.parseInt(msg[1])));
                    SqlExec.addSql(SqlString.insertuser(Integer.parseInt(msg[1]),msg[2],0,1));
                } else if(msg[0].equals("addgroup"))
                {
                    //addgroup::id
                    //请求加入群组
                    SqlExec.addSql(SqlString.insertrelation(userid, Integer.parseInt(msg[1])));
                    syncGroupMsg(Integer.parseInt(msg[1]));//同步群消息
                } else if (msg[0].equals("updateusername")) {
                    //更新用户名称
                    //接收updateusername::username
                    //发送updateusername::username::id
                    username=msg[1];
                    SqlExec.addSql(SqlString.updateuser(userid,username));
                    sendmodifynamemsg();
                } else if (msg[0].equals("updatedb")) {
                    //同步更新数据库

                }else if(msg[0].equals("group"))
                {
                    //group::groupid::msg::time
                    SqlExec.addSql(SqlString.insertchat(Long.parseLong(msg[3]),userid,0,Integer.parseInt(msg[1]),0,msg[2]));
                    sendGroupMsg("group::"+userid+"::"+msg[1]+"::0::"+msg[2]+"::"+msg[3]);//group::fromid::toid::isfile::msg::time
                } else{
                    //给用户发送消息
                    //userid::message::time
                    SqlExec.addSql(SqlString.insertchat(Long.parseLong(msg[2]), userid, 1, Integer.parseInt(msg[0]), 0, msg[1]));
                    if ((toClient = UserServer.socklist.get(Integer.parseInt(msg[0]))) != null) {
                        toClient.sendMsg(userid + "::" + msg[1]+"::"+msg[2]);
                    }
                }
            }
        } catch (IOException e) {
            //如果报错,则该用户下线
            sendofflinemsg();
            UserServer.socklist.remove(userid);
            System.out.println("user:"+username+" is offline!");
            SqlExec.addSql(SqlString.updateuser(userid,username,0));
            //e.printStackTrace();
        }
    }
    public void sendGroupMsg(String msg1)
    {
        //group::fromid::toid::isfile::msg::time
        String[] msg = msg1.split("::");
        ResultSet rs = null;
        //群发消息
        rs = SqlExec.Select(SqlString.selectonlinegroupuser(Integer.parseInt(msg[2])));
        try {
            //group::fromid::toid::isfile::msg::time
            while(rs.next())
            {
                //id
                if(rs.getInt(1)==userid){continue;}
                if ((toClient = UserServer.socklist.get(rs.getInt(1))) != null) {
                    toClient.sendMsg(msg1);
                }
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
    public void syncGroupMsg(int groupid)
    {
        ResultSet rs = null;
        //同步群信息
        rs = SqlExec.Select(SqlString.selectgroupinfo(groupid));
        try {
            //group::fromid::toid::isfile::msg::time
            while(rs.next())
            {
                //id::name::0::1
                sendMsg("addgroup::"+rs.getInt(1)+"::"+rs.getString(2));
            }
        }catch (SQLException e)
        {
            e.printStackTrace();
        }
        //同步聊天记录
        rs = SqlExec.Select(SqlString.selectchat(groupid));
        try {
            //group::fromid::toid::isfile::msg::time
            while(rs.next())
            {
                dout.writeUTF("group::"+rs.getInt(2)+"::"+groupid+"::"+rs.getInt(5)+"::"+rs.getString(6)+"::"+rs.getLong(1));
                dout.flush();
            }
        }catch (SQLException | IOException e)
        {
            e.printStackTrace();
        }

    }
    public void sendMsg(String msg){//给指定用户发消息
        try
        {
            dout.writeUTF(msg);
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void DBSync()
    {
        /*一、该用户发送的消息
         *    该用户收到的消息
         * 二、该用户的好友
         * 三、该用户好友的信息
         * */
        ResultSet rs = null;
        //同步聊天记录
        rs = SqlExec.Select(SqlString.selectchat(userid));
        while(true)
        {
            try {
                if(!rs.next())
                {
                    dout.writeInt(0);//已结束
                    break;
                }
                else{dout.writeInt(-1);}
                dout.writeLong(rs.getLong(1));//时间
                dout.writeInt(rs.getInt(2));//from
                dout.writeInt(rs.getInt(3));//是否单聊
                dout.writeInt(rs.getInt(4));//to
                dout.writeInt(rs.getInt(5));//是否为文件
                dout.writeUTF(rs.getString(6));//消息或路径
                dout.flush();
            }catch (SQLException | IOException e)
            {
                e.printStackTrace();
            }
        }
        //同步好友列表
        rs = SqlExec.Select(SqlString.selectrelation(userid));
        while(true)
        {
            try {
                if(!rs.next())
                {
                    dout.writeInt(0);
                    break;
                }
                else {dout.writeInt(-1);}
                dout.writeInt(rs.getInt(1));//第一好友
                dout.writeInt(rs.getInt(2));//第二好友
                dout.flush();
            }catch (SQLException | IOException e)
            {
                e.printStackTrace();
            }
        }
        //同步好友信息
        rs = SqlExec.Select(SqlString.selectuser(userid));
        while(true)
        {
            try {
                if(!rs.next())
                {
                    dout.writeInt(0);
                    break;
                }
                else{dout.writeInt(-1);}
                dout.writeInt(rs.getInt(1));//用户id
                dout.writeUTF(rs.getString(2));//用户名称
                dout.writeInt(rs.getInt(3));//是否为单人
                dout.writeInt(rs.getInt(4));//是否在线
                dout.flush();
            }catch (SQLException | IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("数据库同步完成！！！");
    }
    public void sendonlinemsg()
    {
        //online::id
        ResultSet rs = null;
        //发送在线消息
        rs = SqlExec.Select(SqlString.selectonlineuser(userid));
        try {
            while(rs.next())
            {
                if ((toClient = UserServer.socklist.get(rs.getInt(1))) != null) {
                    toClient.sendMsg("online::" + userid);
                }
            }
        }catch (SQLException  e)
        {
            e.printStackTrace();
        }

    }
    public void sendofflinemsg()
    {
        //offline::id
        ResultSet rs = null;
        //发送离线消息
        rs = SqlExec.Select(SqlString.selectonlineuser(userid));
        try {
            while(rs.next())
            {
                if ((toClient = UserServer.socklist.get(rs.getInt(1))) != null) {
                    toClient.sendMsg("offline::" + userid);
                }
            }
        }catch (SQLException  e)
        {
            e.printStackTrace();
        }
    }
    public void sendmodifynamemsg()
    {
        //updateusername::username::id
        ResultSet rs = null;
        //发送离线消息
        rs = SqlExec.Select(SqlString.selectonlineuser(userid));
        try {
            while(rs.next())
            {
                if ((toClient = UserServer.socklist.get(rs.getInt(1))) != null) {
                    toClient.sendMsg("updateusername::" +username+"::"+ userid);
                }
            }
        }catch (SQLException  e)
        {
            e.printStackTrace();
        }
    }
}