package guis;

import db_obj.Personnel;

import javax.swing.*;

public class rmGui extends receptionsitGui{
    private JButton personnelButton;
    public rmGui(mainFrame frame, Personnel personnel, JTable table){
        super(frame, personnel, table);

        personnelButton = this.createButtons("Personnel",startX,getStartY(),buttonWidth,buttonHeight);
        setStartY(getStartY()+50);
        leftPanel.add(personnelButton);
    }
}
