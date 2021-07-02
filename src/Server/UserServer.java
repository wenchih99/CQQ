package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import Utils.*;
import sqlService.SqlExec;
import sqlService.SqlSelect;

public class UserServer {
    public static Dictionary<Integer,Client> socklist;
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(9999);
        System.out.println("会话服务器启动成功,端口为:"+ss.getLocalPort());
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
                s = new Scanner(System.in).next();
                if(s.equals("lsusers")) { lsUsers(); }//显示当前用户状态
                else{}
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
        Enumeration<Integer> s = socklist.keys();
        if(!s.hasMoreElements()) { System.out.println("no users"); }
        else
        {
            while(s.hasMoreElements())
            {
                Client tmp = socklist.get(s.nextElement());
                if(tmp.isOnline) { System.out.println(tmp.userid+" is on line!"); }
                else { System.out.println(tmp.userid+" is off line!"); }
            }
        }
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
    public boolean isOnline = false;//用户是否在线
    private String message;//命令消息
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        isOnline = true;
        try
        {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            userid = din.readInt();
            username = din.readUTF();//设置当前用户名
            System.out.println("user:"+userid+" is on line!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        new InitDir(userid);//初始化用户目录
        DBSync();//与客户端同步消息
    }
    public void run()
    {
        String[] msg;
        try {
            while(true)
            {//接收的消息当中不能含有冒号,且消息不能为空
                message = din.readUTF();
                msg = message.split("::");
                /*
                * 对当前收到的message进行分割
                * msg[0]存的是命令
                * msg[1]存的是数据*/
                if(msg[0].equals("upload"))
                {
                    //要求向服务器上传文件
                    //无论给谁发，都加到个人仓库
                    //upload::path
                }
                else if(msg[0].equals("download"))
                {
                    //要求从服务器下载文件
                    //从指定仓库下载文件(指定仓库指的是上传文件的用户的仓库)
                    //download::path
                }
                else if(msg[0].equals("adduser"))
                {
                    //添加好友
                    //双向添加用户
                    //adduser::userid
                }
                else if(msg[0].equals("deleteuser"))
                {
                    //删除好友
                    /*
                    一是删除我方好友
                    二是删除对方好友
                     */
                    //deleteuser::userid
                }
                else if(msg[0].equals("updateusername"))
                {
                    //更新用户名称
                    //updateusername::username
                }
                else if((toClient=UserServer.socklist.get(msg[0]))!=null)
                {
                    //给用户或组发送消息
                    System.out.println(username+" to "+toClient.username+":"+msg[1]);
                    toClient.sendMsg(username,msg[1]);
                }
                else
                {

                }
            }
        } catch (IOException e) {
            //如果报错,则该用户下线
            this.interrupt();
            System.out.println("user:"+username+" is offline!");
            isOnline = false;
            e.printStackTrace();
        }
    }
    public void sendMsg(String fromuser,String msg){//给指定用户发消息
        try
        {
            dout.writeUTF(fromuser+"::"+msg);
            dout.flush();
        } catch (IOException e) {
            UserServer.socklist.get(fromuser).interrupt();
            System.out.println("user:"+fromuser+" is offline!");
            UserServer.socklist.get(fromuser).isOnline=false;
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

        rs = SqlSelect.Select(SqlString.selectchat(userid),0);
        while(true)
        {
            try {
                if(!rs.next())
                {
                    dout.writeInt(0);//已结束
                    break;
                }
                else{dout.writeInt(-1);}
                dout.writeLong(rs.getLong(2));//时间
                dout.writeInt(rs.getInt(3));//from
                dout.writeInt(rs.getInt(4));//是否单聊
                dout.writeInt(rs.getInt(5));//to
                dout.writeInt(rs.getInt(6));//是否为文件
                dout.writeUTF(rs.getString(7));//消息或路径
                dout.flush();
            }catch (SQLException | IOException e)
            {
                e.printStackTrace();
            }
        }

        rs = SqlSelect.Select(SqlString.selectrelation(userid),0);
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

        rs = SqlSelect.Select(SqlString.selectuser(userid),0);
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
}