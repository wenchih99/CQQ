package GUI;

import javax.swing.*;
import java.awt.*;

public class Login extends JFrame {
    private JPanel panel1;
    private JPanel panel2;
    private JPanel panel3;
    private JPanel panel;//多余排布面板
    private JLabel labelid;
    private JLabel labelname;
    private JTextField textid;
    private JTextField textname;
    private JButton login;
    public Login()
    {
        super("CQQ");
        //创建面板
        panel1=new JPanel();
        panel2=new JPanel();
        panel3=new JPanel();
        panel=new JPanel();
        //创建按钮
        login=new JButton("登陆");
        labelid = new JLabel("账号");
        labelname = new JLabel("网名");
        textid = new JTextField(10);
        textname = new JTextField(10);

        //设置布局管理
        this.setLayout(new GridLayout(5, 1));//网格式布局

        panel1.add(labelid);
        panel1.add(textid);
        panel2.add(labelname);
        panel2.add(textname);
        panel3.add(login);
        //加入到JFrame
        this.add(panel);
        this.add(panel1);

        this.add(panel2);
        this.add(panel3);

        //设置窗体
        this.setSize(250, 200);//窗体大小
        this.setLocationRelativeTo(null);//在屏幕中间显示(居中显示)
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//退出关闭JFrame
        this.setVisible(true);//显示窗体

        //锁定窗体
        this.setResizable(false);

        login.addActionListener(e -> {
            this.setVisible(false);
            try {
                new QQDemo(Integer.parseInt(textid.getText()),textname.getText());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
    }
    public static void main(String[] args){
        new Login();
    }
}
