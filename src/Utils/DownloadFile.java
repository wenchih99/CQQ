package Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownloadFile {
    private Socket sc;
    private String path;
    public DownloadFile(Socket sc, String path){
        this.sc=sc;
        this.path=path;
        run();
    }
    public void run()
    {
        try {
            File file = new File(path);
            System.out.println(file.getName());
            System.out.println("download to "+file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            FileInputStream fis = (FileInputStream) sc.getInputStream();
            byte[] b = new byte[1000];
            int n;
            while((n=fis.read(b))!=-1)
            {
                fos.write(b,0,n);
            }
            fos.flush();
            fos.close();
            System.out.println("文件下载成功！");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
