package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChangePriceGui extends JDialog {
    private mainFrame frame;
    private JTextField startDateField, endDateField, priceField;
    private JComboBox<String> roomTypeBox;
    private JButton applyButton;

    public  ChangePriceGui(mainFrame frame, Personnel personnel){
        this.frame = frame;

        setTitle("CHANGE PRICE");
        setSize(300, 430);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel startLabel = new JLabel("START DATE");
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);
        startLabel.setBounds(100,0,100,30);
        add(startLabel);

        startDateField = new JTextField();
        startDateField.setBounds(100,30,100,30);
        add(startDateField);

        JLabel endLabel = new JLabel("END DATE");
        endLabel.setHorizontalAlignment(SwingConstants.CENTER);
        endLabel.setBounds(100,80,100,30);
        add(endLabel);

        endDateField = new JTextField();
        endDateField.setBounds(100,110,100,30);
        add(endDateField);

        JLabel typeLabel = new JLabel("ROOM TYPE");
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setBounds(100,160,100,30);
        add(typeLabel);

        ArrayList<String> types = MyJDBC.getRoomTypes(personnel.getHotel_id());
        String[] typesArray = types.toArray(new String[0]);
        roomTypeBox = new JComboBox<>(typesArray);
        roomTypeBox.setSelectedIndex(0);
        roomTypeBox.setBounds(100,190,100,30);
        add(roomTypeBox);

        JLabel priceLabel = new JLabel("PRICE");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setBounds(100,240,100,30);
        add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(100,270,100,30);
        priceField.setHorizontalAlignment(SwingConstants.CENTER);
        add(priceField);

        applyButton = new JButton("APPLY");
        applyButton.setHorizontalAlignment(SwingConstants.CENTER);
        applyButton.setFocusable(false);
        applyButton.setBounds(110,330,80,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                if(startDateField.getText() != null && endDateField.getText() != null && priceField.getText() != null){
                        try {
                            Date startParsed = dateFormat.parse(startDateField.getText());
                            Date endParsed = dateFormat.parse(endDateField.getText());
                            if(endParsed.compareTo(startParsed) >= 0){
                                int price = Integer.parseInt(priceField.getText());
                                if(MyJDBC.changeDates(personnel, startParsed, endParsed, (String) roomTypeBox.getSelectedItem(), price)){
                                    JOptionPane.showMessageDialog(null,"CHANGES SAVED SUCCESSFULLY!");
                                    ChangePriceGui.this.dispose();
                                }
                                else{
                                    JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG!");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(null, "ENTER DATES PROPERLY! END DATE HAS TO BE EQUALS OR BIGGER THAN START DATE LOGICALLY.");
                            }
                        }
                        catch (ParseException exc){
                            JOptionPane.showMessageDialog(null,"ENTER DATES PROPERLY!");
                        }
                        catch (NumberFormatException numExc) {
                            JOptionPane.showMessageDialog(null, "PLEASE ENTER INTEGER VALUE TO PRICE FIELD!");
                        }
                }
                else {
                    JOptionPane.showMessageDialog(null, "FILL EVERY TEXT FIELDS!");
                }
            }
        });

        add(applyButton);
    }
}
