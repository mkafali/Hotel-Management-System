package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddExtraGui extends JDialog {
    private mainFrame frame;
    private JTextField nameField, priceField;
    private JButton applyButton;

    public AddExtraGui(mainFrame frame, Personnel personnel){
        setTitle("ADD EXTRA PRODUCT");
        setSize(300, 270);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel nameLabel = new JLabel("NAME");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setBounds(100,0,100,30);
        add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(50,30,200,30);
        add(nameField);

        JLabel priceLabel = new JLabel("PRICE");
        priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        priceLabel.setBounds(100,80,100,30);
        add(priceLabel);

        priceField = new JTextField();
        priceField.setBounds(50,110,200,30);
        priceField.setHorizontalAlignment(SwingConstants.CENTER);
        add(priceField);

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(100,170,100,30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((nameField.getText() != null) && (priceField.getText() != null)){
                    try {
                        int price = Integer.valueOf(priceField.getText());
                        if(MyJDBC.addExtra(personnel,nameField.getText(),price)){
                            JOptionPane.showMessageDialog(null,"CHANGES SAVED SUCCESSFULLY");
                        }
                        else{
                            JOptionPane.showMessageDialog(null,"SOMETHING WENT WRONG");
                        }
                    }
                    catch (NumberFormatException ex){
                        JOptionPane.showMessageDialog(null,"PLEASE ENTER INTEGER VALUE");
                    }
                }
            }
        });

        add(applyButton);
    }
}
