package Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GUIutils {
    public static void JlabelSetText(JLabel jLabel, String longString)
    {
        StringBuilder builder = new StringBuilder("<html>");
        char[] chars = longString.toCharArray();
        FontMetrics fontMetrics = jLabel.getFontMetrics(jLabel.getFont());
        int start = 0;
        int len = 0;
        while (start + len < longString.length()) {
            while (true) {
                len++;
                if (start + len > longString.length())break;
                if (fontMetrics.charsWidth(chars, start, len)
                        > jLabel.getWidth()) {
                    break;
                }
            }
            builder.append(chars, start, len-1).append("<br/>");
            start = start + len - 1;
            len = 0;
        }
        builder.append(chars, start, longString.length()-start);
        builder.append("</html>");
        jLabel.setText(builder.toString());
    }
    public static JLabel StringToJlabel(String msg,boolean isme)
    {
        JLabel label = new JLabel();
        label.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        label.setSize(400,0);
        label.setForeground(Color.red);
        if(isme)
        {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        GUIutils.JlabelSetText(label,msg);
        return label;
    }
    public static JLabel StringToJlabel(long msg,boolean isme)
    {
        SimpleDateFormat format = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        JLabel label = new JLabel();
        label.setFont(new Font("微软雅黑", Font.BOLD, 15));
        label.setSize(400,0);
        if(isme)
        {
            label.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        GUIutils.JlabelSetText(label,format.format(new Date(msg)));
        return label;
    }
}
