package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import Utils.*;
import sqlService.SqlQueue;

public class UserServer {
    public static Dictionary<Integer,Client> socklist;
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(9999);
        System.out.println("会话服务器启动成功,端口为:"+ss.getLocalPort());
        socklist = new Hashtable<>();
        new InitDir(0);
        new SqlQueue(0).start();//sql语句执行队列
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
                    //upload::path
                }
                else if(msg[0].equals("download"))
                {
                    //要求从服务器下载文件
                    //download::path
                }
                else if(msg[0].equals("adduser"))
                {
                    //添加好友
                    //adduser::userid
                }
                else if(msg[0].equals("deleteuser"))
                {
                    //删除好友
                    //deleteuser::userid
                }
                else if(msg[0].equals("updateusername"))
                {
                    //更新用户名称
                    //updateusername::username
                }
                else if((toClient=UserServer.socklist.get(msg[0]))!=null)
                {
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

    }
}