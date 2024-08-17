package guis;

import db_obj.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class roomDialog{
    private mainFrame frame;
    private hotelGui hotelGui;
    private JDialog dialog;
    private Room room;
    private Reservations reservations;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JCheckBox availableCheckBox;
    private JCheckBox is_emptyCheckBox;
    private JCheckBox cleanCheckBox;
    private JCheckBox techOkCheckBox;
    private JCheckBox virtCheckBox;
    private JCheckBox KBSCheckBox;
    private JTextArea notes;
    private JScrollPane notesScroll;
    private JSeparator separator;
    private JButton applyButton;
    private JTable guestTable;
    private JButton checkInButton;
    private JButton checkOutButton;
    private JTextArea reservationNotesField;
    private JTextArea extrasField;
    private JScrollPane reservationScroll;
    private  JScrollPane extrasScroll;


    public roomDialog(mainFrame frame, hotelGui hotelGui, Room room, Personnel personnel){
        this.frame = frame;
        this.hotelGui = hotelGui;
        this.room = room;
        this.reservations = MyJDBC.getReservations(room);

        String titleName = MyJDBC.getTitle(personnel);

        this.dialog = new JDialog();
        dialog.setTitle("ROOM: " + room.getRoom_number());
        dialog.setSize(750,600);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setLayout(null);

        //Left Panel
        leftPanel = new JPanel();
        leftPanel.setLayout(null);
        leftPanel.setBounds(0,0,150,600);

        //Right Panel
        rightPanel = new JPanel();
        rightPanel.setLayout(null);
        rightPanel.setBounds(152,0,598,600); //x: 152 because of separator.

        //Left Panel Components:

        //Room Number:
        JLabel roomNumberLabel = new JLabel("Room: " + room.getRoom_number());
        roomNumberLabel.setBounds(15,0,150,30);
        dialog.add(roomNumberLabel);

        //isAvailable
        /*
        if any guest stay in the room then, it will be false.
        Also, personnel can change it when technical or cleaning problem occurs.
         */
        availableCheckBox = new JCheckBox();
        availableCheckBox.setText("available");
        availableCheckBox.setFocusable(false);
        availableCheckBox.setBounds(10,40,150,30);
        availableCheckBox.setSelected(room.isAvailable());
        availableCheckBox.setEnabled(titleName.equals("receptionist") || titleName.equals("hk")  || titleName.equals("reception manager"));
        leftPanel.add(availableCheckBox);

        is_emptyCheckBox = new JCheckBox();
        is_emptyCheckBox.setText("empty");
        is_emptyCheckBox.setFocusable(false);
        is_emptyCheckBox.setBounds(10,80,150,30);
        is_emptyCheckBox.setEnabled(false);
        is_emptyCheckBox.setSelected(room.isIsempty());
        leftPanel.add(is_emptyCheckBox);

        //isClean
        cleanCheckBox = new JCheckBox();
        cleanCheckBox.setText("clean");
        cleanCheckBox.setFocusable(false);
        cleanCheckBox.setBounds(10,120,150,30);
        cleanCheckBox.setSelected(room.isClean());
        cleanCheckBox.setEnabled(titleName.equals("receptionist") || titleName.equals("hk")  || titleName.equals("reception manager"));
        leftPanel.add(cleanCheckBox);

        //is there any technical problem
        techOkCheckBox = new JCheckBox();
        techOkCheckBox.setText("tech ok");
        techOkCheckBox.setFocusable(false);
        techOkCheckBox.setBounds(10,160,150,30);
        techOkCheckBox.setSelected(room.isTech_ok());
        techOkCheckBox.setEnabled(titleName.equals("receptionist") || titleName.equals("hk")  || titleName.equals("reception manager"));
        leftPanel.add(techOkCheckBox);

        //isVirtualRoom
        virtCheckBox = new JCheckBox();
        virtCheckBox.setText("Virtual Room");
        virtCheckBox.setFocusable(false);
        virtCheckBox.setBounds(10,200,150,30);
        virtCheckBox.setSelected(room.isVirtual_room());
        virtCheckBox.setEnabled(false);
        leftPanel.add(virtCheckBox);

        //KBS Situation
        KBSCheckBox = new JCheckBox();
        KBSCheckBox.setText("KBS");
        KBSCheckBox.setFocusable(false);
        KBSCheckBox.setBounds(10,240,150,30);
        KBSCheckBox.setSelected(MyJDBC.getKBS(room));
        KBSCheckBox.setEnabled(false);
        leftPanel.add(KBSCheckBox);

        //Notes Label
        JLabel  notesLabel = new JLabel("Notes");
        notesLabel.setBounds(10,280,150,30);
        leftPanel.add(notesLabel);

        //getNotes
        notes = new JTextArea(room.getNotes());
        //Line settings
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setDisabledTextColor(Color.black);
        notes.setEnabled(titleName.equals("receptionist") || titleName.equals("hk")  || titleName.equals("reception manager"));

        //Event Listener for limiting word number.
        notes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (notes.getText().length() >= 50) {
                    e.consume(); // Prevent adding new character.
                    Toolkit.getDefaultToolkit().beep(); // Beep sound.
                }
            }
        });

        notesScroll = new JScrollPane(notes);
        notesScroll.setBounds(10,320,120,200);
        leftPanel.add(notesScroll);

        //Separator
        separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setForeground(Color.black);
        separator.setBackground(Color.black);
        separator.setBounds(150,0,2,600);
        dialog.add(separator);

        dialog.add(leftPanel);

        //Right Panel Components:

        //Apply Button
        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(490,520,80,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean available = availableCheckBox.isSelected();
                boolean clean = cleanCheckBox.isSelected();
                boolean tech_ok = techOkCheckBox.isSelected();
                String notesText = notes.getText();
                MyJDBC.applyRoom(room,available,clean,tech_ok,notesText);
                //Room features changed. So, we should update our room object.
                roomDialog.this.room = MyJDBC.getRoom(personnel,room.getRoom_number());
                JOptionPane.showMessageDialog(null,"Changes saved succesfully!");
                refreshTablesByRoom(frame,hotelGui,roomDialog.this,roomDialog.this.room,personnel);
            }
        });
        if(titleName.equals("receptionist") || titleName.equals("hk")  || titleName.equals("reception manager")) {
            rightPanel.add(applyButton);
        }
        //Check out button
        checkOutButton = new JButton("CHECK OUT");
        checkOutButton.setFocusable(false);
        checkOutButton.setBounds(370,520,110,30);

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyJDBC.checkInOutActivity(room,false);
                //Room features changed. So, we should update our room object.
                roomDialog.this.room = MyJDBC.getRoom(personnel,room.getRoom_number());
                JOptionPane.showMessageDialog(null,"Check Out achieved successfully!");
                refreshTablesByRoom(frame,hotelGui,roomDialog.this,roomDialog.this.room,personnel);

            }
        });

        if(titleName.equals("receptionist") || titleName.equals("reception manager")) {
            rightPanel.add(checkOutButton);
        }

        //Check in button
        checkInButton = new JButton("CHECK IN");
        checkInButton.setFocusable(false);
        checkInButton.setBounds(250,520,110,30);
        checkInButton.setEnabled(titleName.equals("receptionist") || titleName.equals("reception manager"));

        checkInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyJDBC.checkInOutActivity(room,true);
                //Room features changed. So, we should update our room object.
                roomDialog.this.room = MyJDBC.getRoom(personnel,room.getRoom_number());
                JOptionPane.showMessageDialog(null,"Check In achieved successfully!");
                refreshTablesByRoom(frame,hotelGui,roomDialog.this,roomDialog.this.room,personnel);
            }
        });

        if(titleName.equals("receptionist") || titleName.equals("reception manager")) {
            rightPanel.add(checkInButton);
        }


        //Guest Label
        JLabel  guestLabel = new JLabel();
        guestLabel.setText("GUESTS");
        Font oldFont = guestLabel.getFont();
        Font newFont = oldFont.deriveFont(15.0f);
        guestLabel.setFont(newFont);
        guestLabel.setBounds(20,0,200,50);
        rightPanel.add(guestLabel);

        //Guest Table
        guestTable = MyJDBC.getGuestsTable(room);

        //Add event listener for new window.
        assert guestTable != null;
        guestTable.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if(e.getClickCount() == 2){
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = 0;
                    Object value = target.getValueAt(row,column);
                    //Selected row maybe empty. So, we should check.
                    if(value!=null) {
                        Guests guests = MyJDBC.getGuest(room,row);
                        new guestDialog(frame,hotelGui,roomDialog.this,guests,personnel).setVisible(true);
                    }
                }
            }
        });

        assert guestTable != null;
        JScrollPane guestScroll = new JScrollPane(guestTable);
        guestScroll.setBounds(20,60,550,263);

        rightPanel.add(guestScroll);

        //Reservation Notes Label
        JLabel reservationNotesLabel = new JLabel("NOTES");
        reservationNotesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Font oldReservFont = reservationNotesLabel.getFont();
        Font newReservFont = oldReservFont.deriveFont(12.0f);
        reservationNotesLabel.setFont(newReservFont);
        reservationNotesLabel.setBounds(30,330,250,50);
        rightPanel.add(reservationNotesLabel);

        //Extras Label
        JLabel extrasLabel = new JLabel("EXTRA");
        extrasLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Font oldExtraFont = extrasLabel.getFont();
        Font newExtraFont = oldExtraFont.deriveFont(12.0f);
        extrasLabel.setFont(newExtraFont);
        extrasLabel.setBounds(310,330,250,50);
        rightPanel.add(extrasLabel);

        //Reservation Notes
        reservationNotesField = new JTextArea();
        if(reservations != null){
            reservationNotesField.setText(reservations.getNotes());
        }
        //Line settings
        reservationNotesField.setLineWrap(true);
        reservationNotesField.setWrapStyleWord(true);
        reservationNotesField.setDisabledTextColor(Color.black);
        reservationNotesField.setEnabled(false);


        reservationScroll = new JScrollPane(reservationNotesField);
        reservationScroll.setBounds(40,380,250,120);
        rightPanel.add(reservationScroll);

        //Extras

        extrasField = new JTextArea();
        if(reservations != null){
            extrasField.setText(MyJDBC.extraDeCoder(reservations));
        }
        //Line settings
        extrasField.setLineWrap(true);
        extrasField.setWrapStyleWord(true);
        extrasField.setDisabledTextColor(Color.black);
        extrasField.setEnabled(false);


        extrasScroll = new JScrollPane(extrasField);
        extrasScroll.setBounds(310,380,250,120);
        rightPanel.add(extrasScroll);



        dialog.add(rightPanel);
    }



    public void refreshTablesByRoom(mainFrame frame, hotelGui hotelGui, roomDialog dialog, Room room, Personnel personnel){
        //Refresh Room Dialog
        dialog.getDialog().remove(dialog.getRightPanel());
        dialog.getDialog().remove(dialog.getLeftPanel());
        guis.roomDialog newDialog = new roomDialog(frame,hotelGui,room,personnel);
        //Since new room dialog's is different from  roomDialog's, it will not work properly.So,
        newDialog.setDialog(dialog.getDialog());

        JPanel rightPanel = newDialog.getRightPanel();
        JPanel leftPanel = newDialog.getLeftPanel();

        dialog.getDialog().add(rightPanel);
        dialog.getDialog().add(leftPanel);
        dialog.setRightPanel(rightPanel);
        dialog.setLeftPanel(leftPanel);
        dialog.getDialog().invalidate();
        dialog.getDialog().validate();
        dialog.getDialog().repaint();


        //Refresh Hotel Gui

        hotelGui.refreshHotelGui(hotelGui);
        dialog.setHotelGui(hotelGui);

    }



    public mainFrame getFrame() {
        return frame;
    }

    public void setFrame(mainFrame frame) {
        this.frame = frame;
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    public void setLeftPanel(JPanel leftPanel) {
        this.leftPanel = leftPanel;
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public void setRightPanel(JPanel rightPanel) {
        this.rightPanel = rightPanel;
    }


    public JCheckBox getAvailableCheckBox() {
        return availableCheckBox;
    }

    public void setAvailableCheckBox(JCheckBox availableCheckBox) {
        this.availableCheckBox = availableCheckBox;
    }

    public JCheckBox getCleanCheckBox() {
        return cleanCheckBox;
    }

    public void setCleanCheckBox(JCheckBox cleanCheckBox) {
        this.cleanCheckBox = cleanCheckBox;
    }

    public JCheckBox getTechOkCheckBox() {
        return techOkCheckBox;
    }

    public void setTechOkCheckBox(JCheckBox techOkCheckBox) {
        this.techOkCheckBox = techOkCheckBox;
    }

    public JCheckBox getVirtCheckBox() {
        return virtCheckBox;
    }

    public void setVirtCheckBox(JCheckBox virtCheckBox) {
        this.virtCheckBox = virtCheckBox;
    }

    public JCheckBox getKBSCheckBox() {
        return KBSCheckBox;
    }

    public void setKBSCheckBox(JCheckBox KBSCheckBox) {
        this.KBSCheckBox = KBSCheckBox;
    }


    public JTextArea getNotes() {
        return notes;
    }

    public void setNotes(JTextArea notes) {
        this.notes = notes;
    }

    public JSeparator getSeparator() {
        return separator;
    }

    public void setSeparator(JSeparator separator) {
        this.separator = separator;
    }

    public JButton getApplyButton() {
        return applyButton;
    }

    public void setApplyButton(JButton applyButton) {
        this.applyButton = applyButton;
    }


    public JTable getGuestTable() {
        return guestTable;
    }

    public void setGuestTable(JTable guestTable) {
        this.guestTable = guestTable;
    }

    public guis.hotelGui getHotelGui() {
        return hotelGui;
    }

    public void setHotelGui(guis.hotelGui hotelGui) {
        this.hotelGui = hotelGui;
    }

    public JScrollPane getNotesScroll() {
        return notesScroll;
    }

    public void setNotesScroll(JScrollPane notesScroll) {
        this.notesScroll = notesScroll;
    }

    public JButton getCheckInButton() {
        return checkInButton;
    }

    public void setCheckInButton(JButton checkInButton) {
        this.checkInButton = checkInButton;
    }

    public JButton getCheckOutButton() {
        return checkOutButton;
    }

    public void setCheckOutButton(JButton checkOutButton) {
        this.checkOutButton = checkOutButton;
    }

    public JCheckBox getIs_emptyCheckBox() {
        return is_emptyCheckBox;
    }

    public void setIs_emptyCheckBox(JCheckBox is_emptyCheckBox) {
        this.is_emptyCheckBox = is_emptyCheckBox;
    }
}
