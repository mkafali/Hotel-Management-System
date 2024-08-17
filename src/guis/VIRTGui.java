package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

public class VIRTGui extends JDialog{
    private mainFrame frame;
    private hotelGui hotelGui;
    private JTextField numberField;
    private JComboBox<String> types;
    private JButton applyButton;

    public VIRTGui(mainFrame frame, hotelGui hotelGui, Personnel personnel) {
        this.frame = frame;
        this.hotelGui = hotelGui;

        setTitle("CREATE VIRTUAL ROOM");
        setSize(300, 300);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel fromLabel = new JLabel("NUMBER");
        fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fromLabel.setBounds(100, 0, 100, 30);
        add(fromLabel);

        numberField = new JTextField();
        numberField.setBounds(100, 30, 100, 30);
        numberField.setHorizontalAlignment(SwingConstants.CENTER);
        add(numberField);

        JLabel toLabel = new JLabel("TYPE");
        toLabel.setHorizontalAlignment(SwingConstants.CENTER);
        toLabel.setBounds(100, 70, 100, 30);
        add(toLabel);

        ArrayList<String> typesList = MyJDBC.getRoomTypes(personnel.getHotel_id());
        String[] typesArray = typesList.toArray(new String[0]);
        types = new JComboBox<>(typesArray);
        types.setSelectedIndex(0);
        types.setBounds(100, 100, 100, 30);
        add(types);

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(100, 180, 100, 30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String numberText = numberField.getText();
                String typeText = (String) types.getSelectedItem();
                if(Objects.equals(numberText, "")){
                    JOptionPane.showMessageDialog(null,"PLEASE ENTER ROOM NUMBER!");
                }
                else {
                    boolean virt = MyJDBC.createVirt(personnel,numberText,typeText);
                    if(virt){
                        JOptionPane.showMessageDialog(null,"VIRTUAL ROOM CREATED SUCCESSFULLY");
                        hotelGui.refreshHotelGui(hotelGui);
                        VIRTGui.this.dispose();
                    }
                    else JOptionPane.showMessageDialog(null,"Something got wrong. Please review the room numbers!");
                }
            }
        });

        add(applyButton);
    }
}