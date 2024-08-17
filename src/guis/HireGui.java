package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class HireGui extends JDialog {
    private mainFrame frame;
    private JTextField firstNameField, lastNameField, usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> titleBox;
    private JButton applyButton;

    public HireGui(mainFrame frame, Personnel personnel) {
        setTitle("HIRE");
        setSize(300, 500);
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        JLabel firstNameLabel = new JLabel("FIRST NAME");
        firstNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        firstNameLabel.setBounds(50, 0, 200, 30);
        add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setBounds(50, 30, 200, 30);
        add(firstNameField);

        JLabel lastNameLabel = new JLabel("LAST NAME");
        lastNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lastNameLabel.setBounds(50, 80, 200, 30);
        add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setBounds(50, 110, 200, 30);
        add(lastNameField);

        JLabel usernameLabel = new JLabel("USER NAME");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setBounds(50, 160, 200, 30);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(50, 190, 200, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setBounds(50, 240, 200, 30);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(50, 270, 200, 30);

        ((AbstractDocument) passwordField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }

                if ((fb.getDocument().getLength() + string.length()) <= 20) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                if ((fb.getDocument().getLength() + text.length() - length) <= 20) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        add(passwordField);

        JLabel titleLabel = new JLabel("TITLE");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(50, 320, 200, 30);
        add(titleLabel);

        ArrayList<String> titles = MyJDBC.getTitles();
        String[] titlesArray = titles.toArray(new String[0]);
        titleBox = new JComboBox<>(titlesArray);

        titleBox.setSelectedIndex(0);
        titleBox.setBounds(50, 350, 200, 30);
        add(titleBox);

        applyButton = new JButton("APPLY");
        applyButton.setFocusable(false);
        applyButton.setBounds(100, 410, 100, 30);

        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((firstNameField.getText() != null) && (lastNameField.getText() != null) &&
                        (usernameField.getText() != null) && (passwordField != null)){
                if(MyJDBC.hire(personnel,firstNameField.getText(),lastNameField.getText(),usernameField.getText(),
                        new String(passwordField.getPassword()),titleBox.getSelectedItem().toString())){
                    JOptionPane.showMessageDialog(null,usernameField.getText() + " HIRED!");
                }
                else {
                    JOptionPane.showMessageDialog(null,"THIS USERNAME HAS ALREADY BEEN TAKEN!");
                }
                }
            }
        });

        add(applyButton);

        applyCharacterLimit(firstNameField,20);
        applyCharacterLimit(lastNameField,20);
        applyCharacterLimit(usernameField,20);


    }

    private static void applyCharacterLimit(JTextField textField, int maxChars) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) {
                    return;
                }

                if ((fb.getDocument().getLength() + string.length()) <= maxChars) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) {
                    return;
                }

                if ((fb.getDocument().getLength() + text.length() - length) <= maxChars) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }
}
