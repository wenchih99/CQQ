package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import Utils.*;

public class UserServer {
    public static Dictionary<String,Client> socklist;
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(9999);
        System.out.println("会话服务器启动成功,端口为:"+ss.getLocalPort());
        socklist = new Hashtable<>();
        while (true)
        {
            String s;
            try
            {
                Client tmp = new Client(ss.accept());
                socklist.put(tmp.username,tmp);
                tmp.start();
                s = new Scanner(System.in).next();
                if(s.equals("lsusers")) { lsUsers(); }//显示当前用户状态
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
        Enumeration<String> s = socklist.keys();
        if(!s.hasMoreElements()) { System.out.println("no users"); }
        else
        {
            while(s.hasMoreElements())
            {
                Client tmp = socklist.get(s.nextElement());
                if(tmp.isOnline) { System.out.println(tmp.username+" is on line!"); }
                else { System.out.println(tmp.username+" is off line!"); }
            }
        }
    }
}
class Client extends Thread
{
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    public String username;
    private Client toClient;//向谁发送消息
    public boolean isOnline = false;
    private String msg;
    public Client(Socket socket) throws IOException {
        this.socket = socket;
        isOnline = true;
        try
        {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            username = din.readUTF();//设置当前用户名
            System.out.println("user:"+username+" is on line!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run()
    {
        String[] message;
        try {
            while(true)
            {//接收的消息当中不能含有冒号,且消息不能为空
                msg = din.readUTF();
                message = msg.split("::");
                if(message[0].equals("upload"))
                {

                }
                else if(message[0].equals("download"))
                {

                }
                else if(message[0].equals("adduser"))
                {

                }
                else if(message[0].equals("deleteuser"))
                {

                }
                else if((toClient=UserServer.socklist.get(message[0]))!=null)
                {
                    System.out.println(username+" to "+toClient.username+":"+message[1]);
                    toClient.sendMsg(username,message[1]);
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
    public void sendMsg(String fromuser,String msg){
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
}