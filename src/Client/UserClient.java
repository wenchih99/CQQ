package Client;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;

import GUI.JUtils;
import GUI.QQDemo;
import Utils.*;
import sqlService.SqlExec;
import sqlService.SqlSelect;

public class UserClient {
    private Socket s;
    private DataInputStream din;
    private DataOutputStream dout;
    private int userid;
    private String username;
    private static Scanner sc = new Scanner(System.in);
    private String msgin;//收到的msg消息
    public String msgout="";//发出的msg消息
    public UserClient(int userid,String username)
    {
        this.userid=userid;
        this.username=username;
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
    }
    class RecvMsg extends Thread
    {//接受消息线程
        public void run()
        {
            System.out.println("接收消息线程启动成功！");
            String[] msg;
            try {
                while(true)
                {
                    msgin = din.readUTF();
                    msg = msgin.split("::");
                    if(msg[0].equals("upload"))//上传文件
                    {
                        //upload::fromid::filename::time
                        SqlExec.addSql(SqlString.insertchat(Long.parseLong(msg[3]),Integer.parseInt(msg[1]),1,userid,1,msg[2]));
                        JUtils.addchat(msg[3]+"::"+msg[1]+"::1::"+userid+"::1::"+msg[2]);
                    }
                    else if(msg[0].equals("download"))//下载文件
                    {

                    }
                    else if(msg[0].equals("online"))
                    {
                        //online::id
                        SqlExec.addSql(SqlString.updateuser(Integer.parseInt(msg[1]),1));
                        JUtils.updateuser(msg);
                    }
                    else if(msg[0].equals("offline"))
                    {
                        SqlExec.addSql(SqlString.updateuser(Integer.parseInt(msg[1]),0));
                        JUtils.updateuser(msg);
                    }
                    else if(msg[0].equals("addgroup"))
                    {

                    }
                    else if(msg[0].equals("adduser"))
                    {
                        //adduser::id::name
                        SqlExec.addSql(SqlString.insertrelation(userid,Integer.parseInt(msg[1])));
                        SqlExec.addSql(SqlString.insertuser(Integer.parseInt(msg[1]),msg[2],1,1));
                        JUtils.updateuser(msg);
                    }
                    else if(msg[0].equals("deleteuser"))
                    {
                        //deleteuser::id::name
                        SqlExec.addSql(SqlString.deleterelation(userid,Integer.parseInt(msg[1])));
                        JUtils.updateuser(msg);
                    }
                    else if(msg[0].equals("updateusername"))
                    {
                        //updateusername::username::id
                        SqlExec.addSql(SqlString.updateuser(Integer.parseInt(msg[2]),msg[1]));
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        JUtils.updateuser(msg);
                        //更新全部列表
                    }
                    else if(msg[0].equals("updatedb"))
                    {

                    }
                    else
                    {//接收消息
                        System.out.println(msg[0]+":"+msg[1]);
                        SqlExec.addSql(SqlString.insertchat(Long.parseLong(msg[2]),Integer.parseInt(msg[0]),1,userid,0,msg[1]));
                        JUtils.addchat(Long.parseLong(msg[2])+"::"+msg[0]+"::1::"+userid+"::0::"+msg[1]);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void SendMsg()
    {
        System.out.println("发送消息线程启动成功！");
        String[] msg;
        try {
            msg = msgout.split("::");
            /*
             **/
            if(msg[0].equals("upload"))//上传文件
            {
                //upload::toid::filename::path
                Long t = new Date().getTime();
                SqlExec.addSql(SqlString.insertchat(t,userid,1,Integer.parseInt(msg[1]),1,msg[2]));
                JUtils.addchat(t+"::"+userid+"::1::"+msg[1]+"::1::"+msg[2]);
                dout.writeUTF(msg[0]+"::"+msg[1]+"::"+msg[2]+"::"+t);//upload::toid::filename::time
                new FileClient(msg[0]+"::"+userid+"::"+msg[3]+"::"+msg[2]);//upload::fromid::path::filename
            }
            else if(msg[0].equals("download"))//下载文件
            {
                //download::fromid::filename::localid
                //msgout  download::fromid::filename
                new FileClient(msgout+"::"+userid);
            }
            else if(msg[0].equals("addgroup"))
            {
                //addgroup::groupid::groupname
            }
            else if(msg[0].equals("adduser"))
            {
                SqlExec.addSql(SqlString.insertrelation(userid,Integer.parseInt(msg[1])));
                dout.writeUTF(msgout);
                dout.flush();
            }
            else if(msg[0].equals("deleteuser"))
            {
                SqlExec.addSql(SqlString.deleterelation(userid,Integer.parseInt(msg[1])));
                dout.writeUTF(msgout);
                dout.flush();
            }
            else if(msg[0].equals("updateusername"))
            {
                //updateusername::name
                dout.writeUTF(msgout);
                dout.flush();
            }
            else if(msg[0].equals("updatedb"))
            {

            }
            else
            {
                /*先发送消息，再更新本地数据库*/
                /*toid::msg*/
                System.out.println("消息发送成功:"+msgout);
                Long t = new Date().getTime();
                SqlExec.addSql(SqlString.insertchat(t,userid,1,Integer.parseInt(msg[0]),0,msg[1]));
                JUtils.addchat(t+"::"+userid+"::1::"+msg[0]+"::0::"+msg[1]);
                dout.writeUTF(msgout+"::"+t);
                dout.flush();
            }
            //msgout="";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void DBSync()
    {
        int msg;
        while(true)
        {
            try {
                if((msg=din.readInt())==0) { break; }
                String sql = SqlString.insertchat(din.readLong(),din.readInt(),din.readInt(),din.readInt(),din.readInt(),din.readUTF());
                SqlExec.addSql(sql);
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        while(true)
        {
            try {
                if((msg=din.readInt())==0) { break; }
                SqlExec.addSql(SqlString.insertrelation(din.readInt(),din.readInt()));
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        while(true)
        {
            try {
                if((msg=din.readInt())==0) { break; }
                SqlExec.addSql(SqlString.insertuser(din.readInt(),din.readUTF(),din.readInt(),din.readInt()));
            }catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("数据库同步完成！！！");
    }
    public static void main(String[] args)
    {
        //new UserClient();
    }
}