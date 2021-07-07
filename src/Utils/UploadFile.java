package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UploadFile{
    private Socket sc;
    private String path;
    public UploadFile(Socket sc,String path){
        this.sc=sc;
        this.path=path;
        run();
    }
    public void run()
    {
        try {
            File file = new File(path);
            System.out.println(file.getName());
            FileOutputStream fos = (FileOutputStream) sc.getOutputStream();
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[1000];
            int n;
            while((n=fis.read(b))!=-1)
            {
                fos.write(b,0,n);
            }
            fos.flush();
            fis.close();
            System.out.println("文件上传成功！");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
