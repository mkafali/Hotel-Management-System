package guis;

import db_obj.Guests;
import db_obj.MyJDBC;
import db_obj.Personnel;
import db_obj.Reservations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class guestDialog extends JDialog {
    private mainFrame frame;
    private hotelGui hotelGui;
    private roomDialog dialog;
    private Guests guests;
    private OldNewReservsDialog oldNewReservsDialog;
    private ReservDialog reservDialog;
    private Personnel personnel;
    private Reservations reservations;
    private JButton applyButton;
    private JTextField firstNameText;
    private JTextField lastNameText;
    private JTextField countryText;
    private JTextField idNumberText;
    private JTextField birthDateText;
    private JTextField inDateText;
    private JTextField outDateText;
    private JTextField reservationText;
    private JTextField agencyText;
    private JCheckBox checkedInBox;
    private JCheckBox checkedOutBox;
    private JCheckBox kbsInBox;
    private JCheckBox kbsOutBox;
    private final int textX = 200;
    private final int textWidth = 200;
    private final int textHeight = 25;
    private ArrayList<JLabel> labelsList = new ArrayList<>();
    private ArrayList<JSeparator> separatorsList = new ArrayList<>();
    String titleName;

    //Guest dialog of rooms
    public guestDialog(mainFrame frame, hotelGui hotelGui, roomDialog roomDialog, Guests guests, Personnel personnel){
        this.dialog = roomDialog;
        this.hotelGui = hotelGui;
        this.guests = guests;
        this.personnel = personnel;

        titleName = MyJDBC.getTitle(personnel);

        setTitle("GUEST");
        setSize(500,500);
        setModal(true);
        //if roomDialog is null then this dialog for guest in reservations else, this dialog for guest in rooms.
        setLocationRelativeTo(roomDialog.getDialog());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        applyButton = new JButton("APPLY");
        applyButton.setBounds(390,420,80,30);
        applyButton.setFocusable(false);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean control = true;
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String country = countryText.getText();
                int idNumber;

                //idNumber input control
                if (!idNumberText.getText().isEmpty()) {
                    try{
                        idNumber = Integer.valueOf(idNumberText.getText());
                    }
                    //Prevent wrong id inputs like entered letters.
                    catch(NumberFormatException ex){
                        idNumber = guests.getId_number();
                        JOptionPane.showMessageDialog(null,"Wrong id!");
                        control = false;
                    }
                }
                else{
                    idNumber = guests.getId_number();
                }


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = guests.getBirth_date();
                //Try to convert date.
                try {
                    String dateText = birthDateText.getText();
                    if(!dateText.equals("null")){
                        date = (java.util.Date) formatter.parse(dateText);
                    }
                }
                //It prevents wrong date input attempts.
                catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null,"Wrong Date!");
                    control = false;
                }
                java.sql.Date sqlDate;
                if(date!=null) {
                    sqlDate = new java.sql.Date(date.getTime());
                }
                else{
                    sqlDate=null;
                }

                int reservationId = Integer.parseInt(reservationText.getText());
                boolean checkedIn = checkedInBox.isSelected();
                boolean checkedOut = checkedOutBox.isSelected();
                if(control){
                    //Update guest information.
                    MyJDBC.applyGuest(guests,firstName,lastName,country,idNumber,sqlDate,reservationId,checkedIn,checkedOut);
                    JOptionPane.showMessageDialog(null,"Changes saved successfully!");
                    refreshTables(frame, hotelGui, dialog, guests, personnel);
                    guestDialog.this.dispose();



                }
            }
        });

        if(titleName.equals("receptionist") || titleName.equals("reception manager") || titleName.equals("reservationist")){
            add(applyButton);
        }

        createComponents();

    }

    //Guest dialog of reservations
    public guestDialog(mainFrame frame, OldNewReservsDialog oldNewReservsDialog, Guests guests, ReservDialog reservDialog, Personnel personnel, Reservations reservations){
        this.oldNewReservsDialog = oldNewReservsDialog;
        this.guests = guests;
        this.reservDialog = reservDialog;
        this.personnel = personnel;
        this.reservations = reservations;

        titleName = MyJDBC.getTitle(personnel);

        setTitle("GUEST");
        setSize(500,500);
        setModal(true);
        //if roomDialog is null then this dialog for guest in reservations else, this dialog for guest in rooms.
        setLocationRelativeTo(reservDialog.getDialog());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        applyButton = new JButton("APPLY");
        applyButton.setBounds(390,420,80,30);
        applyButton.setFocusable(false);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean control = true;
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String country = countryText.getText();
                int idNumber;

                //idNumber input control
                if (!idNumberText.getText().isEmpty()) {
                    try{
                        idNumber = Integer.valueOf(idNumberText.getText());
                    }
                    //Prevent wrong id inputs like entered letters.
                    catch(NumberFormatException ex){
                        idNumber = guests.getId_number();
                        JOptionPane.showMessageDialog(null,"Wrong id!");
                        control = false;
                    }
                }
                else{
                    idNumber = guests.getId_number();
                }


                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = guests.getBirth_date();
                //Try to convert date.
                try {
                    String dateText = birthDateText.getText();
                    if(!dateText.equals("null")){
                        date = (java.util.Date) formatter.parse(dateText);
                    }
                }
                //It prevents wrong date input attempts.
                catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null,"Wrong Date!");
                    control = false;
                }
                java.sql.Date sqlDate;
                if(date!=null) {
                    sqlDate = new java.sql.Date(date.getTime());
                }
                else{
                    sqlDate=null;
                }

                int reservationId = Integer.parseInt(reservationText.getText());
                boolean checkedIn = checkedInBox.isSelected();
                boolean checkedOut = checkedOutBox.isSelected();
                if(control){
                    //Update guest information.
                    MyJDBC.applyGuest(guests,firstName,lastName,country,idNumber,sqlDate,reservationId,checkedIn,checkedOut);
                    JOptionPane.showMessageDialog(null,"Changes saved successfully!");
                    refreshTables(frame,oldNewReservsDialog,reservDialog,reservations,personnel);
                    guestDialog.this.dispose();



                }
            }
        });

        if(titleName.equals("reservationist")){
            add(applyButton);
        }

        createComponents();

    }


    //Refresh tables method for guest dialog of rooms
    public static void refreshTables(mainFrame frame, hotelGui hotelGui, roomDialog dialog, Guests guests, Personnel personnel){
        //Refresh Room Dialog
        dialog.refreshTablesByRoom(frame,hotelGui,dialog,MyJDBC.getRoomByGuests(guests),personnel);

    }

    //Refresh tables method for guest dialog of reservations
    public void refreshTables(mainFrame frame, OldNewReservsDialog oldNewReservsDialog, ReservDialog reservDialog,
                                     Reservations reservations, Personnel personnel){
        //Refresh ReservDialog
        reservDialog.refreshReservDialog(frame,oldNewReservsDialog,personnel,reservDialog,reservations);
        guestDialog.this.setReservDialog(reservDialog);

    }

    public void changeComponents(boolean newOld){
        //param: new => true old => false.
        //Change, remove or add components and their features if this dialog belongs to old reservations dialog or
        //new reservations dialog.

        int size = labelsList.size();
        for (int i = size - 4; i < size; i++) {
            remove(labelsList.get(i));
            remove(separatorsList.get(i));
        }

        labelsList.subList(size - 4, size).clear();
        separatorsList.subList(size - 4, size).clear();

        firstNameText.setEnabled(newOld && titleName.equals("reservationist"));
        lastNameText.setEnabled(newOld && titleName.equals("reservationist"));
        countryText.setEnabled(newOld && titleName.equals("reservationist"));
        idNumberText.setEnabled(newOld && titleName.equals("reservationist"));
        birthDateText.setEnabled(newOld && titleName.equals("reservationist"));

        if(newOld && titleName.equals("reservationist")){
            JButton deleteGuestBttn = new JButton("DELETE GUEST");
            deleteGuestBttn.setFocusable(false);
            deleteGuestBttn.setBounds(220,420,150,30);
            deleteGuestBttn.setBackground(new Color(234, 62, 62));

            deleteGuestBttn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(MyJDBC.deleteGuest(guests,reservations)){
                        JOptionPane.showMessageDialog(null,"GUEST DELETED SUCCESSFULLY!");
                        reservDialog.refreshReservDialog(frame,oldNewReservsDialog,personnel,reservDialog,reservations);
                        guestDialog.this.dispose();
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG!");
                    }
                }
            });

            add(deleteGuestBttn);
        }
        else{
            remove(applyButton);
        }

        remove(kbsOutBox);
        remove(kbsInBox);
        remove(checkedOutBox);
        remove(checkedInBox);

        revalidate();
        repaint();
    }

    public void createComponents(){
        String[] labels = {"FIRST NAME","LAST NAME","COUNTRY","ID NUMBER","BIRTH DATE"," CHECK IN DATE",
                "CHECK OUT DATE", "RESERVATION","AGENCY","CHECKED IN","CHECKED OUT","KBS IN","KBS OUT"};

        int labelY = 0;
        for(String label : labels){
            JLabel newLabel = new JLabel(label);
            newLabel.setBounds(10,labelY,150,30);
            labelY += 30;
            JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
            separator.setForeground(Color.black);
            separator.setBackground(Color.black);
            separator.setBounds(0,labelY + 2,500,500);
            add(newLabel);
            add(separator);
            labelsList.add(newLabel);
            separatorsList.add(separator);
        }

        int textY = 6;

        firstNameText = new JTextField(guests.getFirst_name());
        firstNameText.setBounds(textX,textY,textWidth,textHeight);
        firstNameText.setEnabled(!guests.isKbs_in() && (titleName.equals("reservationist") || titleName.equals("receptionist") || titleName.equals("reception manager")));
        firstNameText.setDisabledTextColor(Color.black);
        add(firstNameText);
        textY += 30;

        lastNameText = new JTextField(guests.getLast_name());
        lastNameText.setBounds(textX,textY,textWidth,textHeight);
        lastNameText.setEnabled(!guests.isKbs_in() && (titleName.equals("reservationist") || titleName.equals("receptionist") || titleName.equals("reception manager")));
        lastNameText.setDisabledTextColor(Color.black);
        add(lastNameText);
        textY += 30;

        countryText = new JTextField(guests.getCountry());
        countryText.setBounds(textX,textY,textWidth,textHeight);
        countryText.setEnabled(!guests.isKbs_in() && (titleName.equals("reservationist") || titleName.equals("receptionist") || titleName.equals("reception manager")));
        countryText.setDisabledTextColor(Color.black);
        add(countryText);
        textY += 30;

        idNumberText = new JTextField(String.valueOf(guests.getId_number()));
        idNumberText.setBounds(textX,textY,textWidth,textHeight);
        idNumberText.setEnabled(!guests.isKbs_in() && (titleName.equals("reservationist") || titleName.equals("receptionist") || titleName.equals("reception manager")));
        idNumberText.setDisabledTextColor(Color.black);
        add(idNumberText);
        textY += 30;

        birthDateText = new JTextField(String.valueOf(guests.getBirth_date()));
        birthDateText.setBounds(textX,textY,textWidth,textHeight);
        birthDateText.setEnabled(!guests.isKbs_in() && (titleName.equals("reservationist") || titleName.equals("receptionist") || titleName.equals("reception manager")));
        birthDateText.setDisabledTextColor(Color.black);
        add(birthDateText);
        textY += 30;

        inDateText = new JTextField(String.valueOf(guests.getCheck_in_datetime()));
        inDateText.setBounds(textX,textY,textWidth,textHeight);
        inDateText.setEnabled(false);
        inDateText.setDisabledTextColor(Color.black);
        add(inDateText);
        textY += 30;

        outDateText = new JTextField(String.valueOf(guests.getCheck_out_datetime()));
        outDateText.setBounds(textX,textY,textWidth,textHeight);
        outDateText.setEnabled(false);
        outDateText.setDisabledTextColor(Color.black);
        add(outDateText);
        textY += 30;

        reservationText = new JTextField(String.valueOf(guests.getReservation_id()));
        reservationText.setBounds(textX,textY,textWidth,textHeight);
        reservationText.setEnabled(false);
        reservationText.setDisabledTextColor(Color.black);
        add(reservationText);
        textY += 30;

        agencyText = new JTextField(MyJDBC.getAgency(guests.getReservation_id()));
        agencyText.setBounds(textX,textY,textWidth,textHeight);
        agencyText.setEnabled(false);
        agencyText.setDisabledTextColor(Color.black);
        add(agencyText);
        textY += 35;

        checkedInBox = new JCheckBox();
        checkedInBox.setFocusable(false);
        checkedInBox.setBounds(250,textY-5,20,20);
        checkedInBox.setSelected(guests.isChecked_in());
        checkedInBox.setEnabled(!guests.isChecked_in() && (titleName.equals("receptionist") || titleName.equals("reception manager")));
        add(checkedInBox);
        textY += 35;

        checkedOutBox = new JCheckBox();
        checkedOutBox.setFocusable(false);
        checkedOutBox.setBounds(250,textY-10,20,20);
        checkedOutBox.setSelected(guests.isChecked_out());
        checkedOutBox.setEnabled(!guests.isChecked_out() && (titleName.equals("receptionist") || titleName.equals("reception manager")));
        add(checkedOutBox);
        textY += 30;

        kbsInBox = new JCheckBox();
        kbsInBox.setFocusable(false);
        kbsInBox.setBounds(250,textY-10,20,20);
        kbsInBox.setSelected(guests.isKbs_in());
        kbsInBox.setEnabled(false);
        add(kbsInBox);
        textY += 30;

        kbsOutBox = new JCheckBox();
        kbsOutBox.setFocusable(false);
        kbsOutBox.setBounds(250,textY-10,20,20);
        kbsOutBox.setSelected(guests.isKbs_out());
        kbsOutBox.setEnabled(false);
        add(kbsOutBox);
    }

    public mainFrame getFrame() {
        return frame;
    }

    public void setFrame(mainFrame frame) {
        this.frame = frame;
    }

    public guis.hotelGui getHotelGui() {
        return hotelGui;
    }

    public void setHotelGui(guis.hotelGui hotelGui) {
        this.hotelGui = hotelGui;
    }

    public roomDialog getDialog() {
        return dialog;
    }

    public void setDialog(roomDialog dialog) {
        this.dialog = dialog;
    }

    public Guests getGuests() {
        return guests;
    }

    public void setGuests(Guests guests) {
        this.guests = guests;
    }

    public JButton getApplyButton() {
        return applyButton;
    }

    public void setApplyButton(JButton applyButton) {
        this.applyButton = applyButton;
    }

    public JTextField getFirstNameText() {
        return firstNameText;
    }

    public void setFirstNameText(JTextField firstNameText) {
        this.firstNameText = firstNameText;
    }

    public JTextField getLastNameText() {
        return lastNameText;
    }

    public void setLastNameText(JTextField lastNameText) {
        this.lastNameText = lastNameText;
    }

    public JTextField getCountryText() {
        return countryText;
    }

    public void setCountryText(JTextField countryText) {
        this.countryText = countryText;
    }

    public JTextField getIdNumberText() {
        return idNumberText;
    }

    public void setIdNumberText(JTextField idNumberText) {
        this.idNumberText = idNumberText;
    }

    public JTextField getBirthDateText() {
        return birthDateText;
    }

    public void setBirthDateText(JTextField birthDateText) {
        this.birthDateText = birthDateText;
    }

    public JTextField getInDateText() {
        return inDateText;
    }

    public void setInDateText(JTextField inDateText) {
        this.inDateText = inDateText;
    }

    public JTextField getOutDateText() {
        return outDateText;
    }

    public void setOutDateText(JTextField outDateText) {
        this.outDateText = outDateText;
    }

    public JTextField getReservationText() {
        return reservationText;
    }

    public void setReservationText(JTextField reservationText) {
        this.reservationText = reservationText;
    }

    public JTextField getAgencyText() {
        return agencyText;
    }

    public void setAgencyText(JTextField agencyText) {
        this.agencyText = agencyText;
    }

    public JCheckBox getCheckedInBox() {
        return checkedInBox;
    }

    public void setCheckedInBox(JCheckBox checkedInBox) {
        this.checkedInBox = checkedInBox;
    }

    public JCheckBox getCheckedOutBox() {
        return checkedOutBox;
    }

    public void setCheckedOutBox(JCheckBox checkedOutBox) {
        this.checkedOutBox = checkedOutBox;
    }

    public JCheckBox getKbsInBox() {
        return kbsInBox;
    }

    public void setKbsInBox(JCheckBox kbsInBox) {
        this.kbsInBox = kbsInBox;
    }

    public JCheckBox getKbsOutBox() {
        return kbsOutBox;
    }

    public void setKbsOutBox(JCheckBox kbsOutBox) {
        this.kbsOutBox = kbsOutBox;
    }

    public int getTextX() {
        return textX;
    }

    public int getTextWidth() {
        return textWidth;
    }

    public int getTextHeight() {
        return textHeight;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

    public ArrayList<JLabel> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList(ArrayList<JLabel> labelsList) {
        this.labelsList = labelsList;
    }

    public ArrayList<JSeparator> getSeparatorsList() {
        return separatorsList;
    }

    public void setSeparatorsList(ArrayList<JSeparator> separatorsList) {
        this.separatorsList = separatorsList;
    }

    public OldNewReservsDialog getOldNewReservsDialog() {
        return oldNewReservsDialog;
    }

    public void setOldNewReservsDialog(OldNewReservsDialog oldNewReservsDialog) {
        this.oldNewReservsDialog = oldNewReservsDialog;
    }

    public ReservDialog getReservDialog() {
        return reservDialog;
    }

    public void setReservDialog(ReservDialog reservDialog) {
        this.reservDialog = reservDialog;
    }

    public Reservations getReservations() {
        return reservations;
    }

    public void setReservations(Reservations reservations) {
        this.reservations = reservations;
    }
}
