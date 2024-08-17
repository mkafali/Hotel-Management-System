package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.ArrayList;

public class RoomExtrasGui extends JDialog {
    private mainFrame frame;
    private hotelGui hotelGui;
    private JComboBox<String> roomNoBox, extraBox;
    private JTextField pieceField;
    private JButton applyButton;
    private JLabel totalLabel;

    public RoomExtrasGui(mainFrame frame, hotelGui hotelGui, Personnel personnel){
        this.frame = frame;
        this.hotelGui = hotelGui;

        setTitle("ADD EXTRA TO ROOM");
        setSize(300, 400);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel roomNoLabel = new JLabel("ROOM");
        roomNoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        roomNoLabel.setBounds(100,0,100,30);
        add(roomNoLabel);

        ArrayList<String> rooms = MyJDBC.getRoomsWithTypes(personnel);
        String[] roomsArray = rooms.toArray(new String[0]);
        roomNoBox = new JComboBox<>(roomsArray);
        roomNoBox.setSelectedIndex(-1);
        roomNoBox.setBounds(100,30,100,30);
        add(roomNoBox);

        JLabel productLabel = new JLabel("PRODUCT");
        productLabel.setHorizontalAlignment(SwingConstants.CENTER);
        productLabel.setBounds(100,70,100,30);
        add(productLabel);

        ArrayList<String> extras = MyJDBC.getExtras(personnel);
        String[] extrasArray = extras.toArray(new String[0]);
        extraBox = new JComboBox<>(extrasArray);
        extraBox.setSelectedIndex(-1);
        extraBox.setBounds(50,100,200,30);
        add(extraBox);

        JLabel pieceLabel = new JLabel("PIECE");
        pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pieceLabel.setBounds(100,140,100,30);
        add(pieceLabel);

        pieceField = new JTextField();
        pieceField.setHorizontalAlignment(SwingConstants.CENTER);
        pieceField.setBounds(100,170,100,30);
        pieceField.getDocument().addDocumentListener(createListener());
        add(pieceField);

        totalLabel = new JLabel("TOTAL: ");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setBounds(50,210,200,30);
        add(totalLabel);


        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(50,260,200,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //If required items selected in Combo Boxs and entered piece properly.
                try {
                    if(!pieceField.getText().isEmpty() && (roomNoBox.getSelectedIndex() != -1) &&
                            (extraBox.getSelectedIndex() != -1) && totalLabel.getText().length() > 7){
                        int piece = Integer.parseInt(pieceField.getText());
                        String product = extraBox.getSelectedItem().toString()
                                .split("\\(")[0].substring(0,extraBox.getSelectedItem().toString()
                                .split("\\(")[0].length()-1);

                        BigDecimal productPrice = new BigDecimal(extraBox.getSelectedItem().toString()
                                                        .split("\\(")[1].split("\\)")[0]
                                                        .substring(0,extraBox.getSelectedItem().toString()
                                                        .split("\\(")[1].split("\\)")[0].length() - 3));

                        BigDecimal totalPrice = productPrice.multiply(BigDecimal.valueOf(piece));

                        BigDecimal totalPriceInLabel = new BigDecimal(totalLabel.getText().split(" ")[1]);

                        //Controls every possibility to prevent problems.
                        if (totalPrice.equals(totalPriceInLabel)){
                            System.out.println("memsa");
                            if(MyJDBC.addExtraToRoom(personnel,roomNoBox.getSelectedItem().toString().split(" ")[0],product,piece,productPrice)){
                                JOptionPane.showMessageDialog(null,"CHANGES SAVED SUCCESSFULLY!");
                                RoomExtrasGui.this.dispose();
                            }
                            else{
                                JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG");
                            }
                        }
                        else {
                            JOptionPane.showMessageDialog(null,"You changed something mistakenly. Review your inputs OR enter piece again.");
                        }
                    }
                }
                catch (NumberFormatException ex){
                    //System.out.println("mehmet");
                }
            }
        });

        add(applyButton);

    }

    private DocumentListener createListener(){
        return new DocumentListener(){
        @Override
        public void insertUpdate(DocumentEvent e) {
            action();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            action();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            action();
        }
        private void action() {
            //If required items selected in Combo Boxs and entered piece properly.
            try {
                if(!pieceField.getText().isEmpty() && (roomNoBox.getSelectedIndex() != -1) && (extraBox.getSelectedIndex() != -1)){
                    System.out.println("adam");
                    int piece = Integer.parseInt(pieceField.getText());

                    BigDecimal productPrice = new BigDecimal(extraBox.getSelectedItem().toString()
                            .split("\\(")[1].split("\\)")[0]
                            .substring(0,extraBox.getSelectedItem().toString()
                                    .split("\\(")[1].split("\\)")[0].length() - 3));

                    //System.out.println(productPrice);
                    totalLabel.setText("TOTAL: " + productPrice.multiply(BigDecimal.valueOf(piece)) + " TL");
                    //System.out.println("TOTAL: " + piece *productPrice);
                }
                else{
                    totalLabel.setText("TOTAL: ");
                }
            }
            catch (NumberFormatException e){
                //System.out.println("mehmet");
            }

            }
        };
    }
}

