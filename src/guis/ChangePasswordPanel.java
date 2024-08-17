package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class ChangePasswordPanel extends JPanel {
    private mainFrame frame;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton changeButton;

    public ChangePasswordPanel(mainFrame frame, Personnel personnel){
        this.frame = frame;

        setBounds(0,0,750,600);
        setLayout(null);

        JLabel currentPasswordLabel = new JLabel("CURRENT PASSWORD");
        currentPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        currentPasswordLabel.setBounds(230,50,250,40);
        currentPasswordLabel.setFont(currentPasswordLabel.getFont().deriveFont(15.0f));
        add(currentPasswordLabel);

        currentPasswordField = new JPasswordField();
        currentPasswordField.setFont(currentPasswordLabel.getFont());
        currentPasswordField.setBounds(230,90,250,40);
        add(currentPasswordField);

        JLabel newPasswordLabel = new JLabel("NEW PASSWORD");
        newPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        newPasswordLabel.setBounds(230,150,250,40);
        newPasswordLabel.setFont(newPasswordLabel.getFont().deriveFont(15.0f));
        add(newPasswordLabel);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(currentPasswordLabel.getFont());
        newPasswordField.setBounds(230,190,250,40);
        add(newPasswordField);

        JLabel confirmPasswordLabel = new JLabel("CONFIRM PASSWORD");
        confirmPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        confirmPasswordLabel.setBounds(230,250,250,40);
        confirmPasswordLabel.setFont(confirmPasswordLabel.getFont().deriveFont(15.0f));
        add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(currentPasswordLabel.getFont());
        confirmPasswordField.setBounds(230,290,250,40);
        add(confirmPasswordField);

        changeButton = new JButton("CHANGE");
        changeButton.setFont(currentPasswordLabel.getFont());
        changeButton.setHorizontalAlignment(SwingConstants.CENTER);
        changeButton.setFocusable(false);
        changeButton.setBounds(280,370,150,40);

        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char[] currentPassword = currentPasswordField.getPassword();
                char[] newPassword = newPasswordField.getPassword();
                char[] confirmPassword = confirmPasswordField.getPassword();

                if (Arrays.equals(newPassword, confirmPassword)) {
                    String currentPasswordStr = new String(currentPassword);
                    String newPasswordStr = new String(newPassword);

                    if (MyJDBC.saveNewPassword(personnel, currentPasswordStr, newPasswordStr)) {
                        JOptionPane.showMessageDialog(null, "PASSWORD CHANGED!");
                    } else {
                        JOptionPane.showMessageDialog(null, "CURRENT PASSWORD ENTERED WRONG!");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "ENTER CONFIRMED NEW PASSWORD CORRECTLY!");
                }


                Arrays.fill(currentPassword, '0');
                Arrays.fill(newPassword, '0');
                Arrays.fill(confirmPassword, '0');
            }
        });

        add(changeButton);
    }
}
