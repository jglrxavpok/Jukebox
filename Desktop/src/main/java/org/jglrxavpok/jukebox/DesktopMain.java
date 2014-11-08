package org.jglrxavpok.jukebox;

import javax.swing.*;

public class DesktopMain
{

    @SuppressWarnings("unused")
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        new DesktopJukebox(JOptionPane.showInputDialog("Please enter server name"));
    }
}
