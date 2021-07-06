package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import Utils.*;

public class FileClient extends Thread{
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private String[] msg;
    public FileClient(String message)
    {

        this.msg = message.split("::");
        try
        {
            socket = new Socket("8.130.52.255",8888);
            System.out.println("文件服务器连接成功！");
        }catch (IOException e) {
            e.printStackTrace();
        }
        this.start();
    }
    public void run()
    {
        try
        {
            //发给服务器
            dout=new DataOutputStream(socket.getOutputStream());


            //客户端启动收发文件服务
            if(msg[0].equals("download"))
            {
                //download::fromid::filename::localid
                //在服务器上下载文件到本地仓库
                dout.writeUTF(msg[0]+"::"+msg[1]+"::"+msg[2]);
                dout.flush();
                new DownloadFile(socket,"my_data"+File.separator+msg[3]+File.separator+"filerecv"+File.separator+msg[2]);
            }
            else if(msg[0].equals("upload"))
            {
                //upload::addtoid::path::filename
                //将文件上传到远程个人仓库，
                dout.writeUTF(msg[0]+"::"+msg[1]+"::"+msg[3]);//upload::addtoid::filename
                dout.flush();
                new UploadFile(socket,msg[2]);
            }
            dout.close();
            socket.close();
            System.out.println("文件传输成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args)
    {
        new FileClient("download::my_data"+File.separator+"wenchih"+File.separator+"filerecv"+File.separator+"algorithm.jpeg");
    }
}
