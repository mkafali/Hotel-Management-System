package guis;

import db_obj.Guests;
import db_obj.MyJDBC;
import db_obj.Personnel;
import db_obj.Reservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class ReservDialog{
    private mainFrame frame;
    private JDialog dialog;
    private Reservations reservations;
    private OldNewReservsDialog oldNewReservsDialog;
    private JCheckBox makeCurrentReservBox;
    private JButton applyButton;
    private JTable guestsTable;
    private JScrollPane guestScroll;
    private JTextArea notesArea;
    private JButton paidButton;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JButton reCalculateButton;
    private JComboBox<String> changeAgency;
    private JComboBox<String> changeType;
    private JButton changeInBttn;
    private JButton changeOutBttn;
    private JButton changeNumberBttn;
    private boolean applicable;

    public ReservDialog(mainFrame frame, OldNewReservsDialog oldNewReservsDialog, Reservations reservations, Personnel personnel, boolean newOld){
        //this.frame = frame;
        this.oldNewReservsDialog = oldNewReservsDialog;
        this.reservations = reservations;
        applicable = true;

        String titleName = MyJDBC.getTitle(personnel);

        this.dialog = new JDialog();
        dialog.setTitle("RESERV: " + reservations.getId());
        dialog.setSize(500,500);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(oldNewReservsDialog.getDialog());
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setLayout(null);

        guestsTable = MyJDBC.getReservGuestsTable(reservations);

        assert guestsTable != null;
        guestsTable.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if(e.getClickCount() == 2){
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = 0;
                    Object value = target.getValueAt(row,column);
                    //Selected row maybe empty. So, we should check.
                    if(value!=null) {
                        Guests guests = MyJDBC.getGuest(reservations,row);
                        guestDialog guestInfo = new guestDialog(frame,oldNewReservsDialog,guests,ReservDialog.this,personnel,reservations);
                        guestInfo.changeComponents(newOld);
                        guestInfo.setVisible(true);
                    }
                }
            }
        });

        guestScroll = new JScrollPane(guestsTable);
        guestScroll.setBounds(10,10,200,250);
        dialog.add(guestScroll);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        JLabel inLabel = new JLabel("CHECK IN");
        inLabel.setBounds(250,10,200,30);
        inLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(inLabel);

        checkInField = new JTextField();
        if(newOld){checkInField.setBounds(250,40,100,30);}
        else{checkInField.setBounds(300,40,100,30);}
        checkInField.setText(formatter.format(reservations.getExpected_check_in()));
        checkInField.setEnabled(false);
        checkInField.setDisabledTextColor(Color.black);
        dialog.add(checkInField);

        changeInBttn = new JButton("CHANGE");
        changeInBttn.setFocusable(false);
        changeInBttn.setBounds(360,40,100,30);
        changeInBttn.setEnabled(titleName.equals("reservationist"));

        changeInBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkInField.setEnabled(true);
                applicable = false;
            }
        });

        if(newOld) dialog.add(changeInBttn);

        JLabel outLabel = new JLabel("CHECK OUT");
        outLabel.setBounds(250,80,200,30);
        outLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(outLabel);

        checkOutField = new JTextField();
        if(newOld){checkOutField.setBounds(250,110,100,30);}
        else{checkOutField.setBounds(300,110,100,30);}
        checkOutField.setText(formatter.format(reservations.getExpected_check_out()));
        checkOutField.setEnabled(false);
        checkOutField.setDisabledTextColor(Color.black);
        dialog.add(checkOutField);

        changeOutBttn = new JButton("CHANGE");
        changeOutBttn.setFocusable(false);
        changeOutBttn.setBounds(360,110,100,30);
        changeOutBttn.setEnabled(titleName.equals("reservationist"));

        changeOutBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkOutField.setEnabled(true);
                applicable = false;
            }
        });

        if(newOld) dialog.add(changeOutBttn);

        JLabel roomTypeLabel = new JLabel();
        roomTypeLabel.setText("TYPE: " + MyJDBC.getRoomTypeName(reservations.getRoom_type_id()));
        roomTypeLabel.setBounds(250,150,200,30);
        roomTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(roomTypeLabel);

        JLabel roomNumberLabel = new JLabel();
        roomNumberLabel.setText("NO: " + reservations.getRoom_number());
        if(newOld){roomNumberLabel.setBounds(250,180,100,30);}
        else{roomNumberLabel.setBounds(250,180,200,30);}

        roomNumberLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(roomNumberLabel);

        changeNumberBttn = new JButton("CHANGE");
        changeNumberBttn.setFocusable(false);
        changeNumberBttn.setBounds(360,180,100,30);
        changeNumberBttn.setEnabled(titleName.equals("reservationist"));

        changeNumberBttn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> options = MyJDBC.getRoomsWithTypes(personnel);
                String[] optionsArray = options.toArray(new String[0]);
                JComboBox<String> comboBox = new JComboBox<>(optionsArray);

                int result = JOptionPane.showConfirmDialog(null, comboBox, "Select an Option", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {

                    String selectedOption = (String) comboBox.getSelectedItem();
                    String selectedType = selectedOption.split(" ")[1];
                    if(!selectedType.equals(roomTypeLabel.getText().substring(6))){
                        int okResult = JOptionPane.showConfirmDialog(null,"Booked Type in reservation is " + roomTypeLabel.getText().substring(6) +
                                ". But Room " + selectedOption.split(" ")[0] + " 's Type is " + selectedOption.split(" ")[1]+
                                ". Do you confirm that?");
                        if (okResult == JOptionPane.YES_OPTION) {
                            roomNumberLabel.setText("NO: " + selectedOption.split(" ")[0]);
                        }
                    }
                    else{
                        roomNumberLabel.setText("NO: " + selectedOption.split(" ")[0]);
                    }
                }
            }
        });

        if(newOld) dialog.add(changeNumberBttn);

        JLabel agencyLabel = new JLabel();
        agencyLabel.setText("AGENCY: " + MyJDBC.getAgency(reservations.getId()));
        agencyLabel.setBounds(250,210,200,30);
        agencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(agencyLabel);

        JLabel priceLabel = new JLabel();
        priceLabel.setText("PRICE: " + reservations.getReservation_price() + " TL");
        priceLabel.setBounds(250,240,200,30);
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(priceLabel);

        JLabel paidLabel = new JLabel();
        paidLabel.setText("PAID: " + reservations.isPaid());
        paidLabel.setBounds(250,270,200,30);
        paidLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(paidLabel);

        JLabel notesLabel = new JLabel("NOTES");
        notesLabel.setBounds(10,260,200,30);
        notesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(notesLabel);

        notesArea = new JTextArea();
        notesArea.setBounds(10,290,200,110);
        notesArea.setText(reservations.getNotes());
        notesArea.setEnabled(newOld && titleName.equals("reservationist"));
        notesArea.setDisabledTextColor(Color.black);
        dialog.add(notesArea);

        makeCurrentReservBox = new JCheckBox();
        makeCurrentReservBox.setText("ADD HOTEL");
        makeCurrentReservBox.setFocusable(false);
        makeCurrentReservBox.setBounds(250,300,200,30);
        makeCurrentReservBox.setHorizontalAlignment(SwingConstants.CENTER);
        makeCurrentReservBox.setEnabled(newOld && titleName.equals("reservationist"));
        if(newOld) dialog.add(makeCurrentReservBox);

        JLabel changeAgencyLabel = new JLabel("CHANGE AGENCY");
        changeAgencyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        changeAgencyLabel.setBounds(250,330,200,30);
        if(newOld){dialog.add(changeAgencyLabel);}


        ArrayList<String> agencies = MyJDBC.getAgencies();
        changeAgency = new JComboBox<>();
        changeAgency.setBounds(300,360,100,30);
        for(String agency : agencies){
            changeAgency.addItem(agency);
        }
        changeAgency.setSelectedItem(agencyLabel.getText().substring(8));
        changeAgency.setEnabled(titleName.equals("reservationist"));
        if(newOld){dialog.add(changeAgency);}


        JLabel changeTypeLabel = new JLabel("CHANGE TYPE");
        changeTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        changeTypeLabel.setBounds(250,390,200,30);
        if(newOld){dialog.add(changeTypeLabel);}

        ArrayList<String> roomTypes = MyJDBC.getRoomTypes(personnel.getHotel_id());
        changeType = new JComboBox<>();
        changeType.setBounds(300,420,100,30);
        for(String roomType : roomTypes){
            changeType.addItem(roomType);
        }
        changeType.setSelectedItem(roomTypeLabel.getText().substring(6));

        changeType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applicable = false;
            }
        });

        changeType.setEnabled(titleName.equals("reservationist"));
        if(newOld){dialog.add(changeType);}

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(10,420,72,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(applicable) {
                    String inText = checkInField.getText();
                    String outText = checkOutField.getText();
                    String typeText = roomTypeLabel.getText().substring(6);
                    String noText = roomNumberLabel.getText().substring(4);
                    String priceText = priceLabel.getText().substring(7);
                    String paidText = paidLabel.getText().substring(6);
                    boolean addHotelBoolean = makeCurrentReservBox.isSelected();
                    String changeAgencyText = changeAgency.getSelectedItem().toString();
                    Object changeTypeText = changeType.getSelectedItem();
                    String notesText = notesArea.getText();
                    String finalType = null;

                    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-M-d HH:mm");


                    try {
                        Timestamp inTime = null;
                        Timestamp outTime = null;
                        try {
                            Date inDate = dateTimeFormatter.parse(inText);
                            inTime = new Timestamp(inDate.getTime());
                            Date outDate = dateTimeFormatter.parse(outText);
                            outTime = new Timestamp(outDate.getTime());
                        } catch (ParseException ex) {
                            //if format does not fit the input (if check in hour and minute did not declarated),
                            // then add default check in and check out times to inputs(check in date - check out date).
                            String defaultInDate = inText + " 14:00";
                            String defaultOutDate = outText + " 12:00";
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
                            if(!changeTypeText.toString().equals(typeText)){
                                finalType = changeTypeText.toString();
                            }
                            else{
                                finalType = typeText;
                            }
                            int price = Integer.parseInt(priceText.split(" ")[0]);
                            boolean paid = paidText.equals("true");
                            boolean update = false;
                            if(!noText.equals("null")){
                                update = MyJDBC.applyReservation(reservations,inTime,outTime,finalType,noText,price,paid,
                                        addHotelBoolean,changeAgencyText,notesText);
                            }
                            if(!update){
                                if(!noText.equals("null")) {
                                    JOptionPane.showMessageDialog(null, "Room " + noText + " is not empty");
                                }
                                else{
                                    JOptionPane.showMessageDialog(null,"Please Choose Room!");
                                }
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"Changes saved successfully!");
                                refreshReservDialog(frame,oldNewReservsDialog,personnel,ReservDialog.this,reservations);
                                dialog.dispose();
                            }

                        }
                    }
                    catch (ParseException ex){
                        JOptionPane.showMessageDialog(null,"Wrong Date");
                    }


                }
                else{
                    JOptionPane.showMessageDialog(null,"Please re-calculate or control inputs!");
                }

            }
        });

        if(newOld && titleName.equals("reservationist")){dialog.add(applyButton);}


        reCalculateButton = new JButton("CALCULATE");
        reCalculateButton.setFocusable(false);
        reCalculateButton.setBounds(90,420,105,30);

        reCalculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d");
                try {
                    Date inDate = formatter.parse(checkInField.getText());
                    Date outDate = formatter.parse(checkOutField.getText());
                    Object typeValue = changeType.getSelectedItem();
                    if(typeValue != null) {
                        String[] calculatedPrice = MyJDBC.calculatePrice(1, inDate, outDate, (String) typeValue);
                        priceLabel.setText("PRICE: " + calculatedPrice[0] + " TL");
                        applicable = true;
                        checkInField.setEnabled(false);
                        checkOutField.setEnabled(false);
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"Please Select a room type.");
                        applicable = false;
                    }
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null,"Please enter CHECK IN and CHECK OUT dates properly.");
                }
            }
        });

        if(titleName.equals("reservationist")){dialog.add(reCalculateButton);}



    }

    public void refreshReservDialog(mainFrame frame, OldNewReservsDialog oldNewReservsDialog, Personnel personnel, ReservDialog reservDialog,Reservations reservations){
        boolean addHotel = makeCurrentReservBox.isSelected();
        ReservDialog newReservDialog = new ReservDialog(frame,oldNewReservsDialog,reservations,personnel,true);
        reservDialog.getDialog().remove(reservDialog.getGuestScroll());
        newReservDialog.setDialog(reservDialog.getDialog());
        reservDialog.setGuestsTable(newReservDialog.getGuestsTable());
        reservDialog.setGuestScroll(newReservDialog.getGuestScroll());
        reservDialog.getDialog().add(reservDialog.getGuestScroll());
        reservDialog.getDialog().invalidate();
        reservDialog.getDialog().validate();
        reservDialog.getDialog().repaint();

        oldNewReservsDialog.refreshDialog(frame,oldNewReservsDialog,personnel,addHotel);
        reservDialog.setOldNewReservsDialog(oldNewReservsDialog);
        reservDialog.setReservations(newReservDialog.reservations);
        reservDialog.setDialog(newReservDialog.getDialog());
    }

    public mainFrame getFrame() {
        return frame;
    }

    public void setFrame(mainFrame frame) {
        this.frame = frame;
    }

    public OldNewReservsDialog getOldNewReservsDialog() {
        return oldNewReservsDialog;
    }

    public void setOldNewReservsDialog(OldNewReservsDialog oldNewReservsDialog) {
        this.oldNewReservsDialog = oldNewReservsDialog;
    }


    public JCheckBox getMakeCurrentReservBox() {
        return makeCurrentReservBox;
    }

    public void setMakeCurrentReservBox(JCheckBox makeCurrentReservBox) {
        this.makeCurrentReservBox = makeCurrentReservBox;
    }

    public JButton getApplyButton() {
        return applyButton;
    }

    public void setApplyButton(JButton applyButton) {
        this.applyButton = applyButton;
    }

    public JTable getGuestsTable() {
        return guestsTable;
    }

    public void setGuestsTable(JTable guestsTable) {
        this.guestsTable = guestsTable;
    }

    public JScrollPane getGuestScroll() {
        return guestScroll;
    }

    public void setGuestScroll(JScrollPane guestScroll) {
        this.guestScroll = guestScroll;
    }

    public JTextArea getNotesArea() {
        return notesArea;
    }

    public void setNotesArea(JTextArea notesArea) {
        this.notesArea = notesArea;
    }


    public JButton getPaidButton() {
        return paidButton;
    }

    public void setPaidButton(JButton paidButton) {
        this.paidButton = paidButton;
    }

    public JTextField getCheckInField() {
        return checkInField;
    }

    public void setCheckInField(JTextField checkInField) {
        this.checkInField = checkInField;
    }

    public JTextField getCheckOutField() {
        return checkOutField;
    }

    public void setCheckOutField(JTextField checkOutField) {
        this.checkOutField = checkOutField;
    }

    public JButton getReCalculateButton() {
        return reCalculateButton;
    }

    public void setReCalculateButton(JButton reCalculateButton) {
        this.reCalculateButton = reCalculateButton;
    }

    public JComboBox<String> getChangeAgency() {
        return changeAgency;
    }

    public void setChangeAgency(JComboBox<String> changeAgency) {
        this.changeAgency = changeAgency;
    }

    public JComboBox<String> getChangeType() {
        return changeType;
    }

    public void setChangeType(JComboBox<String> changeType) {
        this.changeType = changeType;
    }

    public JButton getChangeInBttn() {
        return changeInBttn;
    }

    public void setChangeInBttn(JButton changeInBttn) {
        this.changeInBttn = changeInBttn;
    }

    public JButton getChangeOutBttn() {
        return changeOutBttn;
    }

    public void setChangeOutBttn(JButton changeOutBttn) {
        this.changeOutBttn = changeOutBttn;
    }

    public JButton getChangeNumberBttn() {
        return changeNumberBttn;
    }

    public void setChangeNumberBttn(JButton changeNumberBttn) {
        this.changeNumberBttn = changeNumberBttn;
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public Reservations getReservations() {
        return reservations;
    }

    public void setReservations(Reservations reservations) {
        this.reservations = reservations;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public void setApplicable(boolean applicable) {
        this.applicable = applicable;
    }
}
