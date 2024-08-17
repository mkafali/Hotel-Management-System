package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewReservDialog extends JDialog {
    private mainFrame frame;
    private JPanel mainPanel;
    private JTextField inField;
    private JTextField outField;
    private JTextField priceField;
    private JTextArea notesArea;
    private JComboBox<String> agencyBox;
    private JComboBox<String> roomTypeBox;
    private JTextField numberOfPeopleField;
    private JTable guestsTable;
    private JScrollPane guestsScroll;
    private JScrollPane notesScroll;
    private JButton calculatePriceButton;
    private JButton applyButton;



    public NewReservDialog(mainFrame frame, Personnel personnel){
        this.frame = frame;

        setTitle("NEW RESERVATION");
        setSize(600,600);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBounds(0,0,600,600);

        String[] columns = {"NAME"};
        String[][] rows = new String[10][1];

        DefaultTableModel tableModel = new DefaultTableModel(rows,columns);
        guestsTable = new JTable(tableModel);
        guestsTable.setRowHeight(30);
        guestsScroll = new JScrollPane(guestsTable);
        guestsScroll.setBounds(10,20,260,323);
        mainPanel.add(guestsScroll);

        JLabel inLabel = new JLabel("CHECK IN");
        inLabel.setBounds(300,20,290,30);
        inLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(inLabel);

        inField = new JTextField();
        inField.setBounds(345,50,200,30);
        mainPanel.add(inField);
        addDocumentListener(inField);

        JLabel outLabel = new JLabel("CHECK OUT");
        outLabel.setBounds(300,100,290,30);
        outLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(outLabel);

        outField = new JTextField();
        outField.setBounds(345,130,200,30);
        mainPanel.add(outField);
        addDocumentListener(outField);

        JLabel priceLabel = new JLabel("PRICE");
        priceLabel.setBounds(300,180,290,30);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(345,210,200,30);
        priceField.setEnabled(false);
        priceField.setDisabledTextColor(Color.black);
        mainPanel.add(priceField);

        JLabel agencyLabel = new JLabel("AGENCY");
        agencyLabel.setBounds(300,260,150,30);
        agencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(agencyLabel);

        JLabel roomTypeLabel = new JLabel("ROOM TYPE");
        roomTypeLabel.setBounds(470,260,150,30);
        //roomTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(roomTypeLabel);

        ArrayList<String> agencies = MyJDBC.getAgencies();
        agencyBox = new JComboBox<>();
        agencyBox.setBounds(330,290,90,30);
        for(String agency : agencies){
            agencyBox.addItem(agency);
        }

        mainPanel.add(agencyBox);

        ArrayList<String> roomTypes = MyJDBC.getRoomTypes(personnel.getHotel_id());
        roomTypeBox = new JComboBox<>();
        roomTypeBox.setBounds(450,290,120,30);
        for(String roomType : roomTypes){
            roomTypeBox.addItem(roomType);
        }
        roomTypeBox.setSelectedIndex(-1);
        mainPanel.add(roomTypeBox);

        JLabel notesLabel = new JLabel("NOTES");
        notesLabel.setBounds(20,350,560,30);
        notesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(notesLabel);

        notesArea = new JTextArea();
        //Line settings
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);


        //Event Listener for limiting word number.
        notesArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (notesArea.getText().length() >= 50) {
                    e.consume(); // Prevent adding new character.
                    Toolkit.getDefaultToolkit().beep(); // Beep sound.
                }
            }
        });

        notesScroll = new JScrollPane(notesArea);
        notesScroll.setBounds(50,380,500,100);
        mainPanel.add(notesScroll);

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setEnabled(false);
        applyButton.setBounds(470,510,80,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String priceFieldText = priceField.getText();
                Object typeText = roomTypeBox.getSelectedItem();
                if(priceFieldText.equals("")){
                    JOptionPane.showMessageDialog(null, "Please calculate price first.");
                }
                else if(priceFieldText.equals("0 TL")){
                    JOptionPane.showMessageDialog(null,"Please enter valid dates OR recalculate price.");
                } else if (typeText == null) {
                    JOptionPane.showMessageDialog(null,"Please select a room type.");
                }
                else{
                    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-M-d HH:mm");
                    try {
                        Timestamp inTime = null;
                        Timestamp outTime = null;
                        try {
                            Date inDate = dateTimeFormatter.parse(inField.getText());
                            inTime = new Timestamp(inDate.getTime());
                            Date outDate = dateTimeFormatter.parse(outField.getText());
                            outTime = new Timestamp(outDate.getTime());
                        }
                        catch (ParseException exc){
                            //if format does not fit the input (if check in hour and minute did not declarated),
                            // then add default check in and check out times to inputs(check in date - check out date).
                            String defaultInDate = inField.getText() + " 14:00";
                            String defaultOutDate = outField.getText() + " 12:00";
                            if(inTime == null){
                                Date inDate = dateTimeFormatter.parse(defaultInDate);
                                inTime = new Timestamp(inDate.getTime());
                            }
                            if(outTime == null){
                                Date outDate = dateTimeFormatter.parse(defaultOutDate);
                                outTime = new Timestamp(outDate.getTime());
                            }
                        }
                        finally {
                            Object agencyText = agencyBox.getSelectedItem();
                            int rows = guestsTable.getRowCount();
                            if (guestsTable.isEditing()) {
                                guestsTable.getCellEditor().stopCellEditing();
                            }
                            ArrayList<Object> names = new ArrayList<>();
                            for(int row = 0; row < rows; row ++){
                                Object name = guestsTable.getValueAt(row,0);
                                if(name != null){
                                    names.add(name);
                                }
                            }

                            if (names.size() == 0){
                                JOptionPane.showMessageDialog(null,"Enter at least 1 guest information");
                            }
                            else{
                                MyJDBC.createReservationWGuest(personnel.getHotel_id(),names,inTime,outTime,(String) agencyText,(String) typeText,Integer.valueOf(priceFieldText.substring(0,priceFieldText.length()-3)),notesArea.getText());
                                JOptionPane.showMessageDialog(null,"New Reservation Created Successfully!");
                            }
                        }
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(null,"Please enter CHECK IN and CHECK OUT dates properly.");
                    }
                }
            }
        });

        mainPanel.add(applyButton);

        calculatePriceButton = new JButton("CALCULATE PRICE");
        calculatePriceButton.setFocusable(false);
        calculatePriceButton.setBounds(300,510,150,30);

        calculatePriceButton.addActionListener(new ActionListener() {
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
                        applyButton.setEnabled(true);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Please select a room type.");
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null,"Please enter CHECK IN and CHECK OUT dates properly.");
                }

            }
        });

        mainPanel.add(calculatePriceButton);


        add(mainPanel);

    }

    private void addDocumentListener(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateLabel();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLabel();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLabel();
            }

            private void updateLabel() {
                NewReservDialog.this.applyButton.setEnabled(false);
            }
        });

    }
}
