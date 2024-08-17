package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateGMPanel extends JPanel{
    private mainFrame frame;
    private JComboBox<String> hotelsBox;
    private JTextField firstNameField, lastNameField, usernameField;
    private JPasswordField passwordField;
    private JButton applyGMButton;

    public CreateGMPanel(mainFrame frame, Personnel personnel){
        this.frame = frame;

        setBounds(0,0,750,600);
        setLayout(null);

        JLabel hotelNameLabel = new JLabel("HOTEL NAME");
        hotelNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hotelNameLabel.setBounds(230,20,250,40);
        hotelNameLabel.setFont(hotelNameLabel.getFont().deriveFont(15.0f));
        add(hotelNameLabel);

        ArrayList<String> hotels = MyJDBC.getHotelNames();
        String[] hotelsArray = hotels.toArray(new String[0]);
        hotelsBox = new JComboBox<>(hotelsArray);
        Font oldHotelsBoxFont = hotelsBox.getFont();
        hotelsBox.setFont(oldHotelsBoxFont.deriveFont(15.0f));
        hotelsBox.setBounds(230,70,250,40);
        add(hotelsBox);

        JLabel firstNameLabel = new JLabel("FIRST NAME");
        firstNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        firstNameLabel.setBounds(230,120,250,40);
        firstNameLabel.setFont(firstNameLabel.getFont().deriveFont(15.0f));
        add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setFont(firstNameField.getFont().deriveFont(15.0f));
        firstNameField.setBounds(230,160,250,40);
        firstNameField.setHorizontalAlignment(SwingConstants.CENTER);
        add(firstNameField);

        JLabel lastNameLabel = new JLabel("LAST NAME");
        lastNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lastNameLabel.setBounds(230,210,250,40);
        lastNameLabel.setFont(lastNameLabel.getFont().deriveFont(15.0f));
        add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setFont(lastNameField.getFont().deriveFont(15.0f));
        lastNameField.setBounds(230,250,250,40);
        lastNameField.setHorizontalAlignment(SwingConstants.CENTER);
        add(lastNameField);

        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setBounds(230,300,250,40);
        usernameLabel.setFont(usernameLabel.getFont().deriveFont(15.0f));
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(usernameField.getFont().deriveFont(15.0f));
        usernameField.setBounds(230,340,250,40);
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameField);

        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setBounds(230,390,250,40);
        passwordLabel.setFont(passwordLabel.getFont().deriveFont(15.0f));
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(passwordField.getFont().deriveFont(15.0f));
        passwordField.setBounds(230,430,250,40);
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        add(passwordField);

        applyGMButton = new JButton("APPLY");
        applyGMButton.setFocusable(false);
        applyGMButton.setFont(applyGMButton.getFont().deriveFont(15.0f));
        applyGMButton.setBounds(280,490,150,40);
        applyGMButton.setHorizontalAlignment(SwingConstants.CENTER);

        applyGMButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hotelsBox.getSelectedItem() != null && firstNameField.getText() != null && lastNameField.getText() != null &&
                        usernameField.getText() != null && passwordField.getPassword() != null){
                    if(MyJDBC.hire((String) hotelsBox.getSelectedItem(),firstNameField.getText(),lastNameField.getText(),
                            usernameField.getText(),new String(passwordField.getPassword()),"gm")){
                        JOptionPane.showMessageDialog(CreateGMPanel.this, "GM CREATED SUCCESSFULLY");
                    }
                    else {
                        JOptionPane.showMessageDialog(CreateGMPanel.this,"THIS USERNAME HAS ALREADY TAKEN!");
                    }
                }
            }
        });

        add(applyGMButton);

    }
}
