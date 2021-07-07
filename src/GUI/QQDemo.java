package GUI;

import Client.UserClient;
import Utils.SqlString;
import sqlService.SqlExec;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class QQDemo extends JFrame{
    public static QQDemo me;
    public UserClient client;
    public int userid;
    public String username;
    public JLabel label_name,label_isonline,label_issingle;
    public JButton bt_sendmsg,bt_sendfile;
    public JButton deluser,adduser,modifyname,addgroup,buildgroup;
    public JScrollPane jS1,jS2,jS3;
    public JScrollBar jSb;
    public JPanel panel;
    public JTextField text_common,text_groupid,text_groupname;
    public JTextArea text_send;
    public JList<String> list_friend;
    public Hashtable<String,String> friends;//<name,id::issingle::isonline>
    public ArrayList<String> nowchat;//当前好友聊天信息,随用随从数据库中取
    public static int nowid;//当前聊天对象id
    public static String nowname;
    public DefaultListModel<String> listModel;
    public QQDemo(int userid,String username) throws InterruptedException {
        super("CQQ聊天室");
        me=this;
        client = new UserClient(userid,username);//连接服务器
        System.out.println("QQDemo::服务器连接成功");
        this.userid=userid;
        this.username=username;
        friends = new Hashtable<>();
        int i=1;
        while(!SqlExec.sqlQueue.isEmpty())
        {
            System.out.print(i++);
            continue;
        }
        System.out.print("\n");
        JUtils.SelectFriendInfo(userid);
        GUIinit();
        //显示页面
        this.setSize(710,630);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    private void GUIinit()
    {
        label_name=new JLabel("网名:"+username);
        label_name.setBounds(15, 5, 100, 30);

        label_issingle=new JLabel();
        label_issingle.setBounds(590,5,50,30);

        label_isonline=new JLabel();
        label_isonline.setBounds(650,5,50,30);

        text_common=new JTextField(30);
        text_common.setBounds(120,5,100,30);

        text_groupid=new JTextField("群id",30);
        text_groupid.setBounds(235,550,100,30);
        text_groupname=new JTextField("群名称",30);
        text_groupname.setBounds(345,550,100,30);

        bt_sendmsg=new JButton("发送消息");
        bt_sendmsg.setBounds(580, 550, 100, 30 );

        bt_sendfile = new JButton("发送文件");
        bt_sendfile.setBounds(125, 550, 100, 30);

        adduser = new JButton("添加好友");
        adduser.setBounds(480,5,100,30);
        modifyname = new JButton("修改网名");
        modifyname.setBounds(260,5,100,30);
        addgroup=new JButton("添加群组");
        addgroup.setBounds(370,5,100,30);
        deluser = new JButton("删除好友");
        deluser.setBounds(15,550,100,30);
        buildgroup=new JButton("创建群聊");
        buildgroup.setBounds(455,550,100,30);

        bt_sendmsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = text_send.getText();
                text_send.setText("");
                if(msg.equals("")){System.out.println("禁止发送空消息！");return;}
                if(label_issingle.getText().equals("group"))
                {
                    msg="group::"+nowid+"::"+msg;
                }
                else
                {
                    msg=nowid+"::"+msg;
                }
                client.msgout = msg;
                client.SendMsg();
                bt_sendmsg.validate();
            }
        });
        bt_sendfile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc=new JFileChooser("D:\\");
                int val=fc.showOpenDialog(null);    //文件打开对话框
                if(val==fc.APPROVE_OPTION)
                {
                    //正常选择文件
                    File file = new File(fc.getSelectedFile().toString());
                    if(label_issingle.getText().equals("single"))
                    {
                        client.msgout = "upload::"+nowid+"::"+file.getName()+"::"+fc.getSelectedFile().toString()+"::1";
                    }
                    else
                    {
                        client.msgout = "upload::"+nowid+"::"+file.getName()+"::"+fc.getSelectedFile().toString()+"::0";
                    }
                    client.SendMsg();
                    bt_sendfile.validate();
                }
            }
        });
        adduser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg=text_common.getText();
                if(msg.equals("")){System.out.println("空消息！");return;}
                client.msgout="adduser::"+msg;
                client.SendMsg();
                text_common.setText("");
                text_common.validate();
                adduser.validate();
            }
        });
        addgroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = text_common.getText();
                client.msgout="addgroup::"+msg;
                client.SendMsg();
                text_common.setText("");
                text_common.validate();
                addgroup.validate();
            }
        });
        buildgroup.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg="buildgroup::"+text_groupid.getText()+"::"+text_groupname.getText();
                client.msgout=msg;
                client.SendMsg();
                text_groupid.setText("");
                text_groupname.setText("");
                text_groupid.validate();
                text_groupname.validate();
                buildgroup.validate();
            }
        });
        deluser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //先选中再删除
                if(label_issingle.getText().equals("group"))
                {
                    client.msgout="deletegroup::"+nowid;
                }
                else
                {
                    client.msgout="deleteuser::"+nowid;
                }
                client.SendMsg();
                //本地删除好友直接在这删除了
                String msg = "deleteuser::"+nowid+"::"+nowname;
                JUtils.updateuser(msg.split("::"));
                deluser.validate();
            }
        });
        modifyname.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg=text_common.getText();
                text_common.setText("");
                if(msg.equals("")){System.out.println("空消息！");return;}
                client.msgout="updateusername::"+msg;
                client.SendMsg();
                label_name.setText("网名:"+msg);
                label_name.validate();
                modifyname.validate();
            }
        });

        //好友列表
        list_friend=new JList<String>();
        list_friend.setPreferredSize(new Dimension(150, 100));
        list_friend.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        listModel = new DefaultListModel<String>();
        for(String r:friends.keySet())
        {
            listModel.addElement(r);
        }
        list_friend.setModel(listModel);
        list_friend.setSelectedIndex(-1);
        list_friend.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if(e.getValueIsAdjusting()) { return; }//只有在鼠标放开的时候才可以
                // 获取被选中的选项索引
                int k = list_friend.getSelectedIndex();
                if(k<0){return;}
                // 获取选项数据的 ListModel
                ListModel<String> listModel = list_friend.getModel();
                // 输出选中的选项
                System.out.println("选中: " + k + " = " + listModel.getElementAt(k));
                nowname=listModel.getElementAt(k);
                SelectChat(friends.get(nowname));
            }
        });
        jS1=new JScrollPane(list_friend);
        jS1.setBounds(15,45,165,485);

        //聊天记录
        panel = new JPanel();

        panel.setLayout(new GridLayout(0,1));
        jS2 = new JScrollPane(panel);
        jS2.setBounds(190, 45, 490, 305);
        jSb = jS2.getVerticalScrollBar();

        //发送消息区域
        text_send=new JTextArea();
        jS3=new JScrollPane(text_send);
        jS3.setBounds(190, 360, 490, 170);

        Container container=this.getContentPane();
        container.setLayout(null);
        container.add(adduser);
        container.add(addgroup);
        container.add(deluser);
        container.add(modifyname);
        container.add(buildgroup);
        container.add(label_name);
        container.add(label_isonline);
        container.add(label_issingle);
        container.add(bt_sendfile);
        container.add(bt_sendmsg);
        container.add(text_common);
        container.add(text_groupid);
        container.add(text_groupname);
        container.add(jS1);
        container.add(jS2);
        container.add(jS3);
    }
    public void SelectChat(String message)
    {
        //id::issingle::isonline
        String[] msg = message.split("::");
        nowid = Integer.parseInt(msg[1]);
        if(nowid==1){label_issingle.setText("single");}
        else{label_issingle.setText("group");}
        nowid = Integer.parseInt(msg[0]);
        if(SqlExec.isOnline(SqlString.isuseronline(nowid)))
        {
            label_isonline.setText("online");
            label_isonline.setForeground(Color.GREEN);
        }
        else
        {
            label_isonline.setText("offline");
            label_isonline.setForeground(Color.RED);
        }

        ResultSet rs = SqlExec.Select(SqlString.selectchat(nowid));
        nowchat = new ArrayList<>();
        try {
            while(rs.next())
            {
                //time::from::issingle::to::isfile::msg
                nowchat.add(rs.getLong(1)+"::"+rs.getInt(2)+"::"+rs.getInt(3)+"::"+rs.getInt(4)+"::"+rs.getInt(5)+"::"+rs.getString(6));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        panel.removeAll();
        for(String e:nowchat)
        {
            msg = e.split("::");
            if(msg[2].equals("1"))
            {
                JUtils.addchat(e);
            }
            else {JUtils.addgroupchat(e);}
        }
        panel.updateUI();
        panel.validate();
    }
}
