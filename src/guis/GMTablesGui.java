package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class GMTablesGui extends JDialog {
    private mainFrame frame;
    private JTable table;
    private JScrollPane scrollPane;
    private JPanel searchPanel;
    private JFormattedTextField[] searchFields;

    public GMTablesGui(mainFrame frame, Personnel personnel, String tableOfWho){
        this.frame = frame;

        setTitle(tableOfWho);
        if(tableOfWho.equals("PERSONNELS")) {
            setSize(650, 400);
        }
        else if (tableOfWho.equals("EXTRAS")){
            setSize(330,400);
        }
        else{
            setSize(430,400);
        }
        setLocationRelativeTo(frame);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(null);

        if(tableOfWho.equals("PERSONNELS")) {
            table = MyJDBC.createPersonnelsTable(personnel);
        }
        else if (tableOfWho.equals("EXTRAS")){
            table = MyJDBC.createProductsTable(personnel);
        }
        else {
            table = MyJDBC.createDatesTable(personnel);
        }

        // Get the model from the table and create the TableRowSorter
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);


        int startX = 10;

        scrollPane = new JScrollPane(table);

        if(tableOfWho.equals("PERSONNELS")) {
            scrollPane.setBounds(10, 30, 630, 330);

            searchPanel = new JPanel();
            searchPanel.setLayout(null);
            searchPanel.setBounds(0, 0, 630, 30);

            int[] columnWidths = {70,120,120,120,120,80};

            searchFields = new JFormattedTextField[columnWidths.length];

            JFormattedTextField firstSearch = new JFormattedTextField();
            firstSearch.setBounds(startX,0,columnWidths[0],30);
            startX += columnWidths[0];
            firstSearch.setBackground(Color.white);
            searchPanel.add(firstSearch);
            searchFields[0] = firstSearch;

            JFormattedTextField secondSearch = new JFormattedTextField();
            secondSearch.setBounds(startX,0,columnWidths[1],30);
            startX += columnWidths[1];
            secondSearch.setBackground(Color.white);
            searchPanel.add(secondSearch);
            searchFields[1] = secondSearch;

            JFormattedTextField thirdSearch = new JFormattedTextField();
            thirdSearch.setBounds(startX,0,columnWidths[2],30);
            startX += columnWidths[2];
            thirdSearch.setBackground(Color.white);
            searchPanel.add(thirdSearch);
            searchFields[2] = thirdSearch;

            JFormattedTextField fourthSearch = new JFormattedTextField();
            fourthSearch.setBounds(startX,0,columnWidths[3],30);
            startX += columnWidths[3];
            fourthSearch.setBackground(Color.white);
            searchPanel.add(fourthSearch);
            searchFields[3] = fourthSearch;

            JFormattedTextField fifthSearch = new JFormattedTextField();
            fifthSearch.setBounds(startX,0,columnWidths[4],30);
            startX += columnWidths[4];
            fifthSearch.setBackground(Color.white);
            searchPanel.add(fifthSearch);
            searchFields[4] = fifthSearch;

            JFormattedTextField sixthSearch = new JFormattedTextField();
            sixthSearch.setBounds(startX,0,columnWidths[5],30);
            startX += columnWidths[5];
            sixthSearch.setBackground(Color.white);
            searchPanel.add(sixthSearch);
            searchFields[5] = sixthSearch;

            add(searchPanel);

            for (JFormattedTextField searchField : searchFields) {
                searchField.getDocument().addDocumentListener(hotelGui.createDocumentListener(firstSearch,secondSearch,thirdSearch,
                        fourthSearch,fifthSearch,sixthSearch,null,null,null, null,rowSorter));
            }
        }
        else if (tableOfWho.equals("EXTRAS")){
            scrollPane.setBounds(10, 30, 300, 330);

            searchPanel = new JPanel();
            searchPanel.setLayout(null);
            searchPanel.setBounds(0, 0, 310, 30);

            int[] columnWidths = {200,100};

            searchFields = new JFormattedTextField[columnWidths.length];

            JFormattedTextField firstSearch = new JFormattedTextField();
            firstSearch.setBounds(startX,0,columnWidths[0],30);
            startX += columnWidths[0];
            firstSearch.setBackground(Color.white);
            searchPanel.add(firstSearch);
            searchFields[0] = firstSearch;

            JFormattedTextField secondSearch = new JFormattedTextField();
            secondSearch.setBounds(startX,0,columnWidths[1],30);
            startX += columnWidths[1];
            secondSearch.setBackground(Color.white);
            searchPanel.add(secondSearch);
            searchFields[1] = secondSearch;

            add(searchPanel);

            for (JFormattedTextField searchField : searchFields) {
                searchField.getDocument().addDocumentListener(hotelGui.createDocumentListener(firstSearch,secondSearch,null,
                        null,null,null,null,null,null, null,rowSorter));
            }
        }
        else{
            scrollPane.setBounds(10, 30, 400, 330);

            searchPanel = new JPanel();
            searchPanel.setLayout(null);
            searchPanel.setBounds(0, 0, 410, 30);

            int[] columnWidths = {100,100,100,100};

            searchFields = new JFormattedTextField[columnWidths.length];

            JFormattedTextField firstSearch = new JFormattedTextField();
            firstSearch.setBounds(startX,0,columnWidths[0],30);
            startX += columnWidths[0];
            firstSearch.setBackground(Color.white);
            searchPanel.add(firstSearch);
            searchFields[0] = firstSearch;

            JFormattedTextField secondSearch = new JFormattedTextField();
            secondSearch.setBounds(startX,0,columnWidths[1],30);
            startX += columnWidths[1];
            secondSearch.setBackground(Color.white);
            searchPanel.add(secondSearch);
            searchFields[1] = secondSearch;

            JFormattedTextField thirdSearch = new JFormattedTextField();
            thirdSearch.setBounds(startX,0,columnWidths[2],30);
            startX += columnWidths[2];
            thirdSearch.setBackground(Color.white);
            searchPanel.add(thirdSearch);
            searchFields[2] = thirdSearch;

            JFormattedTextField fourthSearch = new JFormattedTextField();
            fourthSearch.setBounds(startX,0,columnWidths[3],30);
            startX += columnWidths[3];
            fourthSearch.setBackground(Color.white);
            searchPanel.add(fourthSearch);
            searchFields[3] = fourthSearch;

            add(searchPanel);

            for (JFormattedTextField searchField : searchFields) {
                searchField.getDocument().addDocumentListener(hotelGui.createDocumentListener(firstSearch,secondSearch,thirdSearch,
                        fourthSearch,null,null,null,null,null, null,rowSorter));
            }
        }

        add(scrollPane);
    }
}
