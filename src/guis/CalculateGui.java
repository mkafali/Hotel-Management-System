package guis;

import db_obj.MyJDBC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CalculateGui extends JDialog {
    private mainFrame frame;
    private JTextField inField;
    private JTextField outField;
    private JTextField priceField;
    private JTextArea detailsArea;
    private JScrollPane detailsScroll;
    private JButton calculateButton;
    private JSeparator separator;
    private JComboBox roomTypeBox;

    public CalculateGui(mainFrame frame, int hotelId){
        this.frame = frame;

        setTitle("CALCULATE PRICE");
        setSize(600,600);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel inLabel = new JLabel("CHECK IN DATE");
        inLabel.setHorizontalAlignment(SwingConstants.CENTER);
        inLabel.setBounds(100,0,400,30);
        add(inLabel);
        inField = new JTextField();
        inField.setHorizontalAlignment(SwingConstants.CENTER);
        inField.setBounds(200,30,200,30);
        add(inField);

        JLabel outLabel = new JLabel("CHECK OUT DATE");
        outLabel.setHorizontalAlignment(SwingConstants.CENTER);
        outLabel.setBounds(100,70,400,30);
        add(outLabel);
        outField = new JTextField();
        outField.setHorizontalAlignment(SwingConstants.CENTER);
        outField.setBounds(200,100,200,30);
        add(outField);

        JLabel typeLabel = new JLabel("CHECK OUT DATE");
        typeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        typeLabel.setBounds(100,140,400,30);
        add(typeLabel);

        ArrayList<String> roomTypes = MyJDBC.getRoomTypes(hotelId);
        roomTypeBox = new JComboBox<>();
        roomTypeBox.setBounds(250,180,90,30);
        for(String type : roomTypes){
            roomTypeBox.addItem(type);
        }
        roomTypeBox.setSelectedIndex(-1);

        add(roomTypeBox);



        calculateButton = new JButton("CALCULATE");
        calculateButton.setFocusable(false);
        calculateButton.setBounds(235,230,130,30);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
                try {
                    Date inDate = formatter.parse(inField.getText());
                    Date outDate = formatter.parse(outField.getText());
                    Object typeValue = roomTypeBox.getSelectedItem();
                    if(typeValue != null) {
                        String[] calculatedPrice = MyJDBC.calculatePrice(1, inDate, outDate, (String) typeValue);
                        priceField.setText(calculatedPrice[0] + " TL");
                        detailsArea.setText(calculatedPrice[1]);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Please Select a room type.");
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null,"Please enter CHECK IN and CHECK OUT dates properly.");
                }
            }
        });

        add(calculateButton);

        separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(Color.black);
        separator.setBackground(Color.black);
        separator.setBounds(0,285,600,3);
        add(separator);

        JLabel priceLabel = new JLabel("PRICE");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setBounds(100,300,400,30);
        add(priceLabel);
        priceField = new JTextField();
        priceField.setHorizontalAlignment(SwingConstants.CENTER);
        priceField.setBounds(200,330,200,30);
        priceField.setEnabled(false);
        priceField.setDisabledTextColor(Color.black);
        add(priceField);

        JLabel detailsLabel = new JLabel("DETAILS");
        detailsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailsLabel.setBounds(100,380,400,30);
        add(detailsLabel);
        detailsArea = new JTextArea();
        detailsArea.setEnabled(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setDisabledTextColor(Color.black);
        detailsScroll = new JScrollPane(detailsArea);
        detailsScroll.setBounds(100,410,400,120);
        add(detailsScroll);

    }
}
