package Client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import Utils.*;

public class UserClient {
    private Socket s;
    private DataInputStream din;
    private DataOutputStream dout;
    private String id_name;
    private static Scanner sc = new Scanner(System.in);
    private String msgin;
    private String msgout;
    public UserClient()
    {

        System.out.print("请输入用户名: ");
        id_name = sc.next();
        try {
            //8.130.52.255
            s = new Socket("8.130.52.255",9999);
            System.out.println("服务器已连接");
            din = new DataInputStream(s.getInputStream());
            dout = new DataOutputStream(s.getOutputStream());
            dout.writeUTF(id_name);//发送用户名
            dout.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new RecvMsg().start();
        new SendMsg().start();
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
                    if(msgout.equals("upload"))
                    {
                        new UploadFile(s,sc.next());
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
    public static void main(String[] args)
    {
        new UserClient();
    }
}
