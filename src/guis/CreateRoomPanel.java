package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;

public class CreateRoomPanel extends JPanel {
    private mainFrame frame;
    private JComboBox<String> hotelsBox, typeBox;
    private JTextField fromField, toField;
    private JButton applyRoomButton;

    public CreateRoomPanel(mainFrame frame, Personnel personnel){
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
        hotelsBox.setSelectedIndex(-1);
        add(hotelsBox);

        JLabel typeLabel = new JLabel("TYPE");
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setBounds(230,120,250,40);
        typeLabel.setFont(typeLabel.getFont().deriveFont(15.0f));
        add(typeLabel);

        typeBox = new JComboBox<>();
        typeBox.setBounds(230,160,250,40);
        typeBox.setFont(typeBox.getFont().deriveFont(15.0f));
        typeBox.setEnabled(false);
        add(typeBox);

        hotelsBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedHotel = (String) hotelsBox.getSelectedItem();
                if (selectedHotel != null) {
                    typeBox.removeAllItems();
                    ArrayList<String> roomTypes = MyJDBC.getAllTypes();
                    for (String roomType : roomTypes) {
                        typeBox.addItem(roomType);
                    }
                    typeBox.setEnabled(true);
                } else {
                    typeBox.setEnabled(false);
                }
            }
        });

        JLabel fromLabel = new JLabel("START FROM");
        fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fromLabel.setBounds(230,210,250,40);
        fromLabel.setFont(fromLabel.getFont().deriveFont(15.0f));
        add(fromLabel);

        fromField = new JTextField();
        fromField.setHorizontalAlignment(SwingConstants.CENTER);
        fromField.setFont(fromField.getFont().deriveFont(15.0f));
        fromField.setBounds(230,250,250,40);
        add(fromField);

        JLabel toLabel = new JLabel("END TO");
        toLabel.setHorizontalAlignment(SwingConstants.CENTER);
        toLabel.setBounds(230,300,250,40);
        toLabel.setFont(toLabel.getFont().deriveFont(15.0f));
        add(toLabel);

        toField = new JTextField();
        toField.setHorizontalAlignment(SwingConstants.CENTER);
        toField.setFont(toField.getFont().deriveFont(15.0f));
        toField.setBounds(230,340,250,40);
        add(toField);

        applyRoomButton = new JButton("APPLY");
        applyRoomButton.setHorizontalAlignment(SwingConstants.CENTER);
        applyRoomButton.setFocusable(false);
        applyRoomButton.setFont(applyRoomButton.getFont().deriveFont(15.0f));
        applyRoomButton.setBounds(280,420,150,40);

        applyRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            if (hotelsBox.getSelectedItem() != null && typeBox.getSelectedItem() != null &&
                    fromField.getText() != null && toField.getText() != null){
                if(MyJDBC.createRooms((String) hotelsBox.getSelectedItem(), (String) typeBox.getSelectedItem(),
                        fromField.getText(),toField.getText())){
                    JOptionPane.showMessageDialog(CreateRoomPanel.this,"ROOMS CREATED. PLEASE CHECK ROOMS JUST IN CASE!");
                }
                else{
                    JOptionPane.showMessageDialog(CreateRoomPanel.this,"SOMETHING WENT WRONG!");
                }
            }
            }
        });

        add(applyRoomButton);
    }
}
