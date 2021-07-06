package GUI;

import Utils.GUIutils;

import java.awt.*;

public class JUtils {
    public static void addchat(String message)
    {
        //time::from::issingle::to::isfile::msg
        String[] msg=message.split("::");
        if(Integer.parseInt(msg[1])==QQDemo.nowid)
        {
            //其他人发给自己
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),false));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[5],false));
        }
        else if(Integer.parseInt(msg[3])==QQDemo.nowid)
        {
            //自己发给其他人的
            QQDemo.me.panel.add(GUIutils.StringToJlabel(Long.parseLong(msg[0]),true));
            QQDemo.me.panel.add(GUIutils.StringToJlabel(msg[5],true));
        }
        QQDemo.me.panel.updateUI();
        QQDemo.me.jS2.getViewport().setViewPosition(new Point(0,QQDemo.me.jSb.getMaximum()));
    }
    public static void adduser(String message)
    {
        //username::id::issingle::online
        String[] msg=message.split("::");
        QQDemo.me.friends.put(msg[0],msg[1]+"::"+msg[2]+"::"+msg[3]);
        QQDemo.me.list_friend.setListData(QQDemo.me.friends.keySet().toArray(new String[0]));
        QQDemo.me.list_friend.validate();
    }
    public static void deleteuser(String username)
    {
        //username
        //本地都是自己按键删除的
        QQDemo.me.friends.remove(username);
        QQDemo.me.list_friend.setListData(QQDemo.me.friends.keySet().toArray(new String[0]));
        QQDemo.me.list_friend.validate();
    }
}
