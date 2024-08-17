package guis;

import db_obj.Personnel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class leftPanelGui{
    protected JPanel leftPanel;
    protected JButton reservsButton;
    protected JButton formersButton;
    protected hotelGui hotelGui;
    protected static final int startX = 40;
    protected int startY;
    protected static final int buttonWidth = 120;
    protected static final int buttonHeight = 30;
    public leftPanelGui(mainFrame frame, Personnel personnel, JTable table){
        hotelGui = new hotelGui(frame,leftPanelGui.this,personnel,table);
        leftPanel = new JPanel();
        leftPanel.setBounds(0,0,200,600);
        leftPanel.setLayout(null);
        leftPanel.setBackground(new Color(0x1E1E1E));
        //Username nameLabel
        JLabel nameLabel = new JLabel(personnel.getFirst_name());
        Font currentFont = nameLabel.getFont();
        Font newFont = currentFont.deriveFont(15.0f);
        nameLabel.setFont(newFont);
        nameLabel.setForeground(new Color(0xE0E0E0));
        nameLabel.setSize(new Dimension(200,30));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.CENTER);
        leftPanel.add(nameLabel);

        //Buttons for common actions
        startY = 50;


        reservsButton = createButtons("Reservs",startX,startY,buttonWidth,buttonHeight);

        reservsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OldNewReservsDialog(frame,hotelGui,personnel,true).getDialog().setVisible(true);
            }
        });

        leftPanel.add(reservsButton);
        startY += 50;

        formersButton = createButtons("Formers",startX,startY,buttonWidth,buttonHeight);

        formersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OldNewReservsDialog(frame,hotelGui,personnel,false).getDialog().setVisible(true);
            }
        });

        leftPanel.add(formersButton);
        startY += 50;



        frame.add(leftPanel);
    }

    protected JButton createButtons(String text, int x, int y, int width, int height){
        JButton button = new JButton();
        button.setBounds(x,y,width,height);
        button.setText(text);
        button.setFocusable(false);
        return button;
    }

    public JPanel getLeftPanel() {
        return leftPanel;
    }

    public void setLeftPanel(JPanel leftPanel) {
        this.leftPanel = leftPanel;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public guis.hotelGui getHotelGui() {
        return hotelGui;
    }

    public void setHotelGui(guis.hotelGui hotelGui) {
        this.hotelGui = hotelGui;
    }
}
