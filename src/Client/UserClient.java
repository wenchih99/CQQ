package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import Utils.*;
import sqlService.SqlExec;

public class UserClient {
    private Socket s;
    private DataInputStream din;
    private DataOutputStream dout;
    private int userid;
    private String username;
    private static Scanner sc = new Scanner(System.in);
    private String msgin;//收到的msg消息
    private String msgout;//发出的msg消息
    public UserClient()
    {
        System.out.print("请输入用户id: ");
        userid = sc.nextInt();
        System.out.print("请输入用户名: ");
        username = sc.next();
        try {
            //8.130.52.255
            s = new Socket("8.130.52.255",9999);
            System.out.println("服务器已连接");
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeInt(userid);//发送用户id
            dout.writeUTF(username);//发送用户名
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new InitDir(userid);//初始化用户目录
        new SqlExec(userid).start();//启动sql执行队列服务
        new InitDB("client");//初始化DB
        DBSync();//与服务器进行同步
        new RecvMsg().start();//启动接收消息线程
        new SendMsg().start();//启动发送消息线程
    }
    class RecvMsg extends Thread
    {//接受消息线程
        public void run()
        {
            try {
                while(true)
                {
                    msgin = din.readUTF();
                    System.out.println(msgin);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class SendMsg extends Thread
    {//发送消息线程
        public void run()
        {
            try {
                while(true)
                {
                    msgout=sc.nextLine();

                    //System.out.println(msgout);
                    if(msgout.equals("")) { continue; }//防止发送空消息
                    if(msgout.equals("upload"))//上传文件
                    {
                        new UploadFile(s,sc.next());
                    }
                    else if(msgout.equals("download"))//下载文件
                    {

                    }
                    else
                    {
                        dout.writeUTF(msgout);
                        dout.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void DBSync()
    {

    }
    public static void main(String[] args)
    {
        new UserClient();
    }
}