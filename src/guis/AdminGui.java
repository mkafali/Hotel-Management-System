package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminGui {
    private mainFrame frame;
    private Personnel personnel;
    private JPanel adminPanel, leftPanel, rightPanel, hotelPanel, roomPanel, GMPanel, logsPanel;
    private JButton statsButton, hotelButton, roomButton, gmButton, changePasswordButton, applyHotelButton;
    private JButton applyGM;
    private JComboBox<String> hotelBox, typeBox;
    private JTextField hotelField;
    private final int buttonX = 15, buttonWidth = 170, buttonHeight = 30;

    public AdminGui(mainFrame frame , Personnel personnel){
        this.frame = frame;
        this.personnel = personnel;

        adminPanel = new JPanel();
        adminPanel.setBounds(0,0,920,600);
        adminPanel.setLayout(null);

        leftPanel = new JPanel();
        leftPanel.setBounds(0,0,200,600);
        leftPanel.setLayout(null);

        rightPanel = new JPanel();
        rightPanel.setBounds(200,0,750,600);
        rightPanel.setLayout(null);



        JLabel adminLabel = new JLabel("ADMIN PANEL");
        adminLabel.setBounds(10,0,180,30);
        adminLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(adminLabel);

        JSeparator adminSep = new JSeparator(SwingConstants.HORIZONTAL);
        adminSep.setForeground(Color.black);
        adminSep.setBackground(Color.black);
        adminSep.setBounds(0,34,200,2);
        leftPanel.add(adminSep);

        
        JLabel usernameLabel = new JLabel(personnel.getUsername());
        usernameLabel.setBounds(10,40,180,30);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(usernameLabel);

        JSeparator usernameSep = new JSeparator(SwingConstants.HORIZONTAL);
        usernameSep.setForeground(Color.black);
        usernameSep.setBackground(Color.black);
        usernameSep.setBounds(0,79,200,2);
        leftPanel.add(usernameSep);

        int buttonY = 110;


        hotelButton = new JButton("CREATE HOTEL");
        hotelButton.setFocusable(false);
        hotelButton.setBounds(buttonX,buttonY,buttonWidth,buttonHeight);
        hotelButton.setHorizontalAlignment(SwingConstants.CENTER);
        buttonY += 50;

        hotelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createHotelPanel();
            }
        });

        leftPanel.add(hotelButton);

        roomButton = new JButton("CREATE ROOMS");
        roomButton.setFocusable(false);
        roomButton.setBounds(buttonX,buttonY,buttonWidth,buttonHeight);
        roomButton.setHorizontalAlignment(SwingConstants.CENTER);

        roomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createRoomPanel();
            }
        });

        leftPanel.add(roomButton);
        buttonY += 50;

        gmButton = new JButton("CREATE GM");
        gmButton.setFocusable(false);
        gmButton.setBounds(buttonX,buttonY,buttonWidth,buttonHeight);
        gmButton.setHorizontalAlignment(SwingConstants.CENTER);

        gmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createGMPanel();
            }
        });

        leftPanel.add(gmButton);
        buttonY += 50;

        changePasswordButton = new JButton("CHANGE PASSWORD");
        changePasswordButton.setFocusable(false);
        changePasswordButton.setBounds(buttonX,buttonY,buttonWidth,buttonHeight);
        changePasswordButton.setHorizontalAlignment(SwingConstants.CENTER);

        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createChangePassword();
            }
        });

        leftPanel.add(changePasswordButton);
        buttonY += 50;

        JSeparator separator= new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(198,0,2,600);
        separator.setBackground(Color.black);
        separator.setForeground(Color.black);
        leftPanel.add(separator);

        adminPanel.add(leftPanel);
        adminPanel.add(rightPanel);

        frame.add(adminPanel);
    }

    public void createHotelPanel(){
        adminPanel.remove(rightPanel);
        rightPanel.removeAll();

        hotelPanel = new JPanel();
        hotelPanel.setBounds(0,0,750,600);
        hotelPanel.setLayout(null);

        JLabel hotelLabel = new JLabel("HOTEL NAME");
        Font oldLabelFont = hotelLabel.getFont();
        hotelLabel.setFont(oldLabelFont.deriveFont(30.0f));
        hotelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        hotelLabel.setBounds(230,100,250,50);
        hotelPanel.add(hotelLabel);

        hotelField = new JTextField();
        Font oldFieldFont = hotelField.getFont();
        hotelField.setFont(oldFieldFont.deriveFont(30.0f));
        hotelField.setBounds(230,170,250,50);
        hotelField.setHorizontalAlignment(SwingConstants.CENTER);
        hotelPanel.add(hotelField);

        applyHotelButton = new JButton("APPLY");
        Font oldapplyFont = applyHotelButton.getFont();
        applyHotelButton.setFont(oldapplyFont.deriveFont(20.0f));
        applyHotelButton.setFocusable(false);
        applyHotelButton.setHorizontalAlignment(SwingConstants.CENTER);
        applyHotelButton.setBounds(280,270,150,50);

        applyHotelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(hotelField.getText() != null){
                    if(MyJDBC.createHotel(personnel,hotelField.getText())){
                        JOptionPane.showMessageDialog(rightPanel,"HOTEL CREATED SUCCESSFULLY!");
                    }
                    else{
                        JOptionPane.showMessageDialog(rightPanel,"HOTEL IS ALREADY EXISTS!");
                    }
                }
            }
        });

        hotelPanel.add(applyHotelButton);

        rightPanel.add(hotelPanel);
        adminPanel.add(rightPanel);
        rightPanel.invalidate();
        rightPanel.validate();
        rightPanel.repaint();


    }

    public void createGMPanel(){
        adminPanel.remove(rightPanel);
        rightPanel.removeAll();

        rightPanel.add(new CreateGMPanel(frame,personnel));
        adminPanel.add(rightPanel);

        rightPanel.invalidate();
        rightPanel.validate();
        rightPanel.repaint();
    }

    public void createRoomPanel(){
        adminPanel.remove(rightPanel);
        rightPanel.removeAll();

        rightPanel.add(new CreateRoomPanel(frame,personnel));
        adminPanel.add(rightPanel);

        rightPanel.invalidate();
        rightPanel.validate();
        rightPanel.repaint();
    }

    public void createChangePassword(){
        adminPanel.remove(rightPanel);
        rightPanel.removeAll();

        rightPanel.add(new ChangePasswordPanel(frame,personnel));
        adminPanel.add(rightPanel);

        rightPanel.invalidate();
        rightPanel.validate();
        rightPanel.repaint();
    }

}
