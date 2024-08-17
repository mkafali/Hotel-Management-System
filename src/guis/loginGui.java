package guis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import db_obj.*;

public class loginGui implements ActionListener {
    private static final int loginStartX = 350;
    private int loginStartY;
    private static final int loginWidth = 200;
    private int loginHeight;

    private mainFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    JButton loginButton;
    JPanel loginPanel;
    public loginGui(mainFrame frame){
        this.frame = frame;
        loginPanel = new JPanel();
        loginPanel.setBounds(0,0,900,600);
        loginPanel.setLayout(null);
        JLabel userLabel = new JLabel("USERNAME");
        loginStartY = 50;
        loginHeight = 30;
        userLabel.setBounds(loginStartX, loginStartY, loginWidth, loginHeight);
        Font currentFont = userLabel.getFont();
        Font newFont = currentFont.deriveFont(20.0f);
        userLabel.setFont(newFont);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        userLabel.setVerticalAlignment(SwingConstants.CENTER);
        loginPanel.add(userLabel);

        usernameField = new JTextField();
        loginStartY = 100;
        loginHeight = 50;
        usernameField.setBounds(loginStartX, loginStartY, loginWidth, loginHeight);
        Font textFieldFont = usernameField.getFont();
        Font newTextFont = textFieldFont.deriveFont(30.0f);
        usernameField.setFont(newTextFont);
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("PASSWORD");
        loginStartY = 200;
        loginHeight = 30;
        passwordLabel.setBounds(loginStartX, loginStartY, loginWidth, loginHeight);
        passwordLabel.setFont(userLabel.getFont());
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setVerticalAlignment(SwingConstants.CENTER);
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        loginStartY = 250;
        loginHeight = 50;
        passwordField.setBounds(loginStartX, loginStartY, loginWidth, loginHeight);
        passwordField.setFont(usernameField.getFont());
        loginPanel.add(passwordField);

        loginButton = new JButton("LOG IN");
        loginButton.setBounds(400,350,100,50);
        loginButton.setFocusable(false);
        loginButton.addActionListener(this);
        loginPanel.add(loginButton);
        frame.add(loginPanel);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==loginButton){
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());
            Personnel personnel = MyJDBC.validateLogin(username,password);
            if(personnel != null){
                //Change panels.
                frame.remove(loginPanel);
                //Prevent encountering with rendering problems.
                SwingUtilities.updateComponentTreeUI(frame);
                logIn(frame,personnel);
                
            }
            else{
                JOptionPane.showMessageDialog(frame, "Login failed...");
            }
        }
    }
    private void logIn(mainFrame frame, Personnel personnel){
        //int job_title_id = personnel.getJob_title_id();
        String job_title = MyJDBC.getTitle(personnel);
        JTable table = MyJDBC.createHotelTable(personnel);
        assert job_title != null;
        switch (job_title){
            case "receptionist":
                new receptionsitGui(frame,personnel,table);
                break;
            case "reception manager":
                new rmGui(frame,personnel,table);
                break;
            case "hk":
                new hkGui(frame,personnel,table);
                break;
            case "reservationist":
                new reservationistGui(frame,personnel,table);
                break;
            case "gm":
                new gmGui(frame,personnel,table);
                break;
            case "admin":
                new AdminGui(frame,personnel);
                break;

        }
    }
}
