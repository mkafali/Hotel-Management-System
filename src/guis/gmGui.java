package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class gmGui extends leftPanelGui{
    private JButton addExtraButton, addPriceButton, hireButton, fireButton, personnelsButton, pricesButton, extrasButton;
    public gmGui(mainFrame frame , Personnel personnel, JTable table){
        super(frame, personnel, table);

        addExtraButton = createButtons("Add Extras",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        addExtraButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddExtraGui(frame,personnel).setVisible(true);
            }
        });

        leftPanel.add(addExtraButton);

        addPriceButton = createButtons("Change Price",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        addPriceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChangePriceGui(frame,personnel).setVisible(true);
            }
        });

        leftPanel.add(addPriceButton);

        hireButton = createButtons("Hire",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        hireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HireGui(frame,personnel).setVisible(true);
            }
        });

        leftPanel.add(hireButton);

        fireButton = createButtons("Fire",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        fireButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> personnelNames = new ArrayList<>();
                ArrayList<ArrayList<String>> personnelsData = MyJDBC.getPersonnels(personnel);
                if(personnelsData != null){
                    for (ArrayList<String> personnelData : personnelsData){
                        personnelNames.add(personnelData.get(1));
                    }
                }
                String[] personnelNamesArray = personnelNames.toArray(new String[0]);
                JComboBox<String> personnelBox = new JComboBox<>(personnelNamesArray);

                int result = JOptionPane.showConfirmDialog(null,personnelBox,"FIRE",JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String selectedOption = (String) personnelBox.getSelectedItem();
                    if(MyJDBC.fire(personnel,selectedOption)){
                        if(selectedOption.equals(personnel.getUsername())){
                            JOptionPane.showMessageDialog(null,"BYE CAPTAIN!");
                            frame.dispose();
                        }
                        else {
                            JOptionPane.showMessageDialog(null,selectedOption + " FIRED!");
                        }
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG!");
                    }
                }

            }
        });

        leftPanel.add(fireButton);

        personnelsButton = createButtons("Personnels",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        personnelsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GMTablesGui(frame,personnel,"PERSONNELS").setVisible(true);
            }
        });

        leftPanel.add(personnelsButton);

        pricesButton = createButtons("Date Prices",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        pricesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GMTablesGui(frame,personnel,"DATES").setVisible(true);
            }
        });

        leftPanel.add(pricesButton);

        extrasButton = createButtons("Extras",startX,getStartY(), buttonWidth,buttonHeight);
        setStartY(getStartY()+50);

        extrasButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GMTablesGui(frame,personnel,"EXTRAS").setVisible(true);
            }
        });

        leftPanel.add(extrasButton);
    }
}
