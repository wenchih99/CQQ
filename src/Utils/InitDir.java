package Utils;

import java.io.File;
import java.io.IOException;

public class InitDir {
    //初始化目录结构,方便进行文件管理
    /*
     * -CQQ
     * --可执行文件
     * --my_data目录
     * ---users
     * ----filerecv
     * ----chatinfo
     * */
    public InitDir(int userid)
    {
        File now = new File("my_data");
        if(!now.exists()) { now.mkdir(); }
        now = new File(now.getPath()+File.separator+userid);
        if(!now.exists()) { now.mkdir(); }
        now = new File(now.getPath()+File.separator+"filerecv");
        if(!now.exists()) { now.mkdir(); }
    }
    public static void main(String[] args) throws IOException {new InitDir(1);}
}
