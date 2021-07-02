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
    private String cmd;
    private String[] msg;
    private String filename;
    public FileClient(String cmd)
    {
        this.cmd = cmd;
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
            dout=new DataOutputStream(socket.getOutputStream());
            dout.writeUTF("download::wenchih::algorithm.jpeg");
            dout.flush();
            msg = cmd.split("::");
            //filename = din.readUTF();
            if(msg[0].equals("download")) { new DownloadFile(socket,msg[1]); }
            else if(msg[0].equals("upload")) { new UploadFile(socket,msg[1]); }
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
