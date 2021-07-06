package GUI;

import Utils.GUIutils;
import Utils.SqlString;
import sqlService.SqlExec;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JUtils {
    public static void addchat(String message)
    {
        //time::fromid::issingle::toid::isfile::msg
        String[] msg = message.split("::");
        if(QQDemo.nowid!=Integer.parseInt(msg[1])&&QQDemo.nowid!=Integer.parseInt(msg[3])){return;}
        JButton jb=null;
        if(msg[4].equals("1"))//是否为文件
        {
            jb = new JButton("文件下载");
            final String fromid = msg[1];
            final String filename = msg[5];
            jb.setSize(200, 30);
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    QQDemo.me.client.msgout = "download::" + fromid + "::" + filename;
                    QQDemo.me.client.SendMsg();
                }
            });
        }
        if(Integer.parseInt(msg[1])==QQDemo.me.nowid)
        {
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),false));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[5],false));
            if(msg[4].equals("1"))//是否为文件
            {
                JPanel jPanel = new JPanel(new BorderLayout());
                jPanel.add(jb,BorderLayout.WEST);
                QQDemo.me.panel.add(jPanel);
            }
        }
        else
        {
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),true));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[5],true));
            if(msg[4].equals("1"))//是否为文件
            {
                JPanel jPanel = new JPanel(new BorderLayout());
                jPanel.add(jb,BorderLayout.EAST);
                QQDemo.me.panel.add(jPanel);
            }
        }
        QQDemo.me.panel.updateUI();
        QQDemo.me.jS2.getViewport().setViewPosition(new Point(0,QQDemo.me.jSb.getMaximum()));
        QQDemo.me.panel.validate();
    }
    public static void addgroupchat(String message)
    {
        //time::fromid::issingle::toid::isfile::msg
        //group::fromid::toid::isfile::msg::time
        String[] msg = message.split("::");
        if(QQDemo.nowid!=Integer.parseInt(msg[3])){return;}
        JButton jb=null;
        if(msg[4].equals("1"))//是否为文件
        {
            jb = new JButton("文件下载");
            final String fromid = msg[1];
            final String filename = msg[5];
            jb.setSize(200, 30);
            jb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    QQDemo.me.client.msgout = "download::" + fromid + "::" + filename;
                    QQDemo.me.client.SendMsg();
                }
            });
        }
        if(Integer.parseInt(msg[1])!=QQDemo.me.userid)
        {
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),false));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[1]+"::"+msg[5],false));
            if(msg[4].equals("1"))//是否为文件
            {
                JPanel jPanel = new JPanel(new BorderLayout());
                jPanel.add(jb,BorderLayout.WEST);
                QQDemo.me.panel.add(jPanel);
            }
        }
        else
        {
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),true));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[5],true));
            if(msg[4].equals("1"))//是否为文件
            {
                JPanel jPanel = new JPanel(new BorderLayout());
                jPanel.add(jb,BorderLayout.EAST);
                QQDemo.me.panel.add(jPanel);
            }
        }
        QQDemo.me.panel.updateUI();
        QQDemo.me.jS2.getViewport().setViewPosition(new Point(0,QQDemo.me.jSb.getMaximum()));
        QQDemo.me.panel.validate();
    }
    public static void updateuser(String[] msg)
    {
        if(msg[0].equals("online"))
        {
            //online::id
            if(Integer.parseInt(msg[1])==QQDemo.nowid)
            {
                QQDemo.me.label_isonline.setText(msg[0]);
                QQDemo.me.label_isonline.setForeground(Color.GREEN);
                QQDemo.me.label_isonline.validate();
            }
        }
        else if(msg[0].equals("offline"))
        {
            //offline::id
            if(Integer.parseInt(msg[1])==QQDemo.nowid)
            {
                QQDemo.me.label_isonline.setText(msg[0]);
                QQDemo.me.label_isonline.setForeground(Color.RED);
                QQDemo.me.label_isonline.validate();
            }
        }
        else if(msg[0].equals("updateusername"))
        {
            //updateusername::username::id
            JUtils.SelectFriendInfo(QQDemo.me.userid);
            DefaultListModel<String> listModel= (DefaultListModel<String>) QQDemo.me.list_friend.getModel();
            listModel.removeAllElements();
            for(String e:QQDemo.me.friends.keySet())
            {
                listModel.addElement(e);
            }
            QQDemo.me.list_friend.setSelectedIndex(-1);
            QQDemo.me.list_friend.repaint();
        }
        else if(msg[0].equals("adduser"))
        {
            //adduser::id::name
            QQDemo.me.friends.put(msg[2],msg[1]+"::1");
            DefaultListModel<String> listModel= (DefaultListModel<String>) QQDemo.me.list_friend.getModel();
            listModel.addElement(msg[2]);
            QQDemo.me.list_friend.setSelectedIndex(-1);
            QQDemo.me.list_friend.repaint();
        }
        else if(msg[0].equals("addgroup"))
        {
            //addgroup::id::name
            QQDemo.me.friends.put(msg[2],msg[1]+"::0");
            DefaultListModel<String> listModel= (DefaultListModel<String>) QQDemo.me.list_friend.getModel();
            listModel.addElement(msg[2]);
            QQDemo.me.list_friend.setSelectedIndex(-1);
            QQDemo.me.list_friend.repaint();
        }
        else if(msg[0].equals("deleteuser"))
        {
            //deleteuser::id::name
            //本地都是自己按键删除的
            QQDemo.me.friends.remove(msg[2]);
            DefaultListModel<String> listModel= (DefaultListModel<String>) QQDemo.me.list_friend.getModel();
            listModel.removeElement(msg[2]);
            QQDemo.me.list_friend.setSelectedIndex(-1);
            QQDemo.me.list_friend.repaint();
        }
    }
    public static void SelectFriendInfo(int userid)
    {
        //name::id::issingle
        ResultSet rs = SqlExec.Select(SqlString.selectuser(userid));
        QQDemo.me.friends.clear();
        try {
            while(rs.next())
            {
                QQDemo.me.friends.put(rs.getString(2),rs.getInt(1)+"::"+rs.getInt(3));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
