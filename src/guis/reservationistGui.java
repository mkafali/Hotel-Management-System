package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class reservationistGui extends leftPanelGui{
    private JButton newReservButton;
    private JButton calculateButton;
    private JButton deleteReservButton;
    public reservationistGui(mainFrame frame, Personnel personnel, JTable table){
        super(frame,personnel, table);
        newReservButton = createButtons("New Reserv",startX,getStartY(),
                buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        newReservButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new NewReservDialog(frame,personnel).setVisible(true);
            }
        });

        leftPanel.add(newReservButton);


        calculateButton = createButtons("Calculate",startX,getStartY(),
                buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CalculateGui(frame,personnel.getHotel_id()).setVisible(true);
            }
        });

        leftPanel.add(calculateButton);

        deleteReservButton = createButtons("DEL RESERV",startX,getStartY(),buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        deleteReservButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Integer> reservs = MyJDBC.getFutureReservs(personnel);
                Integer[] reservsArray = reservs.toArray(new Integer[0]);
                JComboBox<Integer> comboBox = new JComboBox<>(reservsArray);

                int result = JOptionPane.showConfirmDialog(null, comboBox, "Select an Reservation", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    if(comboBox.getSelectedItem() != null) {
                        MyJDBC.deleteReservation((Integer) comboBox.getSelectedItem());
                        JOptionPane.showMessageDialog(null, "Reservation " + comboBox.getSelectedItem() + " deleted successfully!");
                    }
                }



            }
        });

        leftPanel.add(deleteReservButton);

    }
}
