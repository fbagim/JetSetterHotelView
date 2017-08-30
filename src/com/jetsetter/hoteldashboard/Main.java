package com.jetsetter.hoteldashboard;

import javax.swing.*;

public class Main {
    public static void main(String oo[]){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AgentBackOffice agentBackOffice =new AgentBackOffice();
                agentBackOffice.setVisible(true);
            }
        });

    }

}
