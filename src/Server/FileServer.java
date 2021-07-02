package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import Utils.*;

public class FileServer extends Thread{
    public void run() {
        try {
            ServerSocket fileserver = new ServerSocket(8888);
            System.out.println("文件服务器启动成功！");
            while(true)
            {
                new FileLink(fileserver.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException
    {
        new FileServer().start();
    }
}
class FileLink extends Thread
{
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private String[] msg;
    private String message;
    private String path;
    public FileLink(Socket socket) throws SocketException {
        this.socket = socket;
        System.out.println("用户连接成功！");
    }
    public void run()
    {
        try
        {
            din = new DataInputStream(socket.getInputStream());
            dout = new DataOutputStream(socket.getOutputStream());
            message = din.readUTF();
            msg = message.split("::");
            path = "my_data"+File.separator+msg[1]+File.separator+"filerecv"+File.separator+msg[2];
            if(msg[0].equals("download")) { new UploadFile(socket,path); }
            else if(msg[0].equals("upload")) { new DownloadFile(socket,path); }
            din.close();
            dout.flush();
            dout.close();
            socket.close();
            System.out.println("文件传输成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}