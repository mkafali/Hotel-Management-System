package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class receptionsitGui extends leftPanelGui{
    private JButton rcButton;
    private  JButton virtButton;
    private JButton kbsButton;
    private JButton deleteVirtButton;
    private JButton extrasButton;
    public receptionsitGui(mainFrame frame, Personnel personnel, JTable table){
        super(frame, personnel, table);

        rcButton = createButtons("RC",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        rcButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RCDialog(frame,hotelGui,personnel).setVisible(true);
            }
        });

        leftPanel.add(rcButton);

        virtButton = createButtons("VIRT",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        virtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new VIRTGui(frame,hotelGui,personnel).setVisible(true);

            }
        });

        leftPanel.add(virtButton);

        deleteVirtButton = createButtons("DEL VIRT",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        deleteVirtButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> numbers = MyJDBC.getVirtRooms(personnel);
                String[] numbersArray = numbers.toArray(new String[0]);
                JComboBox<String> virtNumbers = new JComboBox<>(numbersArray);
                int result = JOptionPane.showConfirmDialog(null, virtNumbers, "Select a Room", JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String selectedOption = (String) virtNumbers.getSelectedItem();
                    if (selectedOption != null){
                        if(MyJDBC.deleteVirt(personnel,selectedOption)){
                            JOptionPane.showMessageDialog(null,"ROOM " + selectedOption + " DELETED SUCCESSFULLY!");
                            hotelGui.refreshHotelGui(hotelGui);
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"THERE IS NO EMPTY VIRTUAL ROOM CURRENTLY!");
                    }

                }
            }
        });

        leftPanel.add(deleteVirtButton);

        kbsButton = createButtons("KBS",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        kbsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(MyJDBC.readKBS(personnel)){
                    JOptionPane.showMessageDialog(null,"KBS PROCESS HANDLED SUCCESSFULLY!");
                    hotelGui.refreshHotelGui(hotelGui);
                }
                else{
                    JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG");
                }
            }
        });

        leftPanel.add(kbsButton);

        extrasButton = createButtons("ADD EXTRA",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        extrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RoomExtrasGui(frame,hotelGui,personnel).setVisible(true);
            }
        });

        leftPanel.add(extrasButton);

    }
}
