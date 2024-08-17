package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class RCDialog extends JDialog{
    private mainFrame frame;
    private hotelGui hotelGui;
    private JTextField fromField;
    private JTextField toField;
    private JButton applyButton;

    public RCDialog(mainFrame frame, hotelGui hotelGui, Personnel personnel){
        this.frame = frame;
        this.hotelGui = hotelGui;

        setTitle("ROOM CHANGE");
        setSize(300,300);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel fromLabel = new JLabel("FROM");
        fromLabel.setHorizontalAlignment(SwingConstants.CENTER);
        fromLabel.setBounds(100,0,100,30);
        add(fromLabel);

        fromField = new JTextField();
        fromField.setBounds(100,30,100,30);
        fromField.setHorizontalAlignment(SwingConstants.CENTER);
        add(fromField);

        JLabel toLabel = new JLabel("TO");
        toLabel.setHorizontalAlignment(SwingConstants.CENTER);
        toLabel.setBounds(100,70,100,30);
        add(toLabel);

        toField = new JTextField();
        toField.setBounds(100,100,100,30);
        toField.setHorizontalAlignment(SwingConstants.CENTER);
        add(toField);

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(100,180,100,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fromText = fromField.getText();
                String toText = toField.getText();
                if((Objects.equals(fromText, "") || Objects.equals(toText, "")) ||
                Objects.equals(fromText,toText)){
                    JOptionPane.showMessageDialog(null,"CHOOSE ROOMS PROPERLY");
                }
                else {
                    boolean changed = MyJDBC.roomChange(personnel,fromText,toText);
                    if(changed){
                        JOptionPane.showMessageDialog(null,"ROOM CHANGE ACHIEVED SUCCESSFULLY");
                        hotelGui.refreshHotelGui(hotelGui);
                        RCDialog.this.dispose();
                    }
                    else JOptionPane.showMessageDialog(null,"Something got wrong. Please review the room numbers!");
                }
            }
        });

        add(applyButton);

    }
}
