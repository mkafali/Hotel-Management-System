package guis;

import db_obj.MyJDBC;
import db_obj.Personnel;
import db_obj.Room;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.ArrayList;

public class hotelGui{
    private leftPanelGui leftPanelGui;
    private Personnel personnel;
    private mainFrame frame;
    private JPanel hotelPanel;
    private JTable hotelTable;
    private JScrollPane scrollPane;
    private JPanel searchPanel;
    private JFormattedTextField roomSearch;
    private JFormattedTextField nameSearch;
    private JFormattedTextField agencySearch;
    private JFormattedTextField typeSearch;
    private JFormattedTextField inSearch;
    private JFormattedTextField outSearch;
    private JFormattedTextField okSearch;
    private JFormattedTextField notesSearch;
    private JFormattedTextField kbsSearch;
    private JFormattedTextField emptySearch;
    //protected JFormattedTextField
    private JFormattedTextField[] searchFields;
    private final String[] cols = new String[] {"NUMBER","PEOPLE","AGENCY","TYPE","IN","OUT","OK","NOTES","KBS","VIRT"};
    private final int[] columnWidths = {100, 100, 70, 70, 70, 70, 30, 100, 30, 60};

    //Create Hotel Gui and Hotel Panel
    public hotelGui(mainFrame frame, leftPanelGui leftPanelGui, Personnel personnel, JTable table){
        this.frame = frame;//Creating Frame
        this.leftPanelGui = leftPanelGui;
        this.personnel = personnel;
        hotelTable = table;
        hotelPanel = new JPanel();
        hotelPanel.setBounds(203, 0, 700, 600);
        hotelPanel.setLayout(null);
        /*
        Creating hotelTable which contains rooms infos and infos of people that resides in a spesific room.
        This hotelTable will be same for every title. But their authorization will be different.
        The hotelTable will be the right side of the frame. Left side will be spesific for each title.
        */
        //Creating columns of the hotelTable.

        //Add search bars.
        searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(0, 0, 700, 30);

        int startX = 0;
        //roomSearch
        roomSearch = new JFormattedTextField();
        roomSearch.setBounds(startX,0,columnWidths[0],30);
        startX += columnWidths[0];
        roomSearch.setBackground(Color.white);
        searchPanel.add(roomSearch);


        //nameSearch
        nameSearch = new JFormattedTextField();
        nameSearch.setBounds(startX,0,columnWidths[1],30);
        startX += columnWidths[1];
        nameSearch.setBackground(Color.white);
        searchPanel.add(nameSearch);

        //agencySearch
        agencySearch = new JFormattedTextField();
        agencySearch.setBounds(startX,0,columnWidths[2],30);
        startX += columnWidths[2];
        agencySearch.setBackground(Color.white);
        searchPanel.add(agencySearch);

        //typeSearch
        typeSearch = new JFormattedTextField();
        typeSearch.setBounds(startX,0,columnWidths[3],30);
        startX += columnWidths[3];
        typeSearch.setBackground(Color.white);
        searchPanel.add(typeSearch);

        //inSearch
        inSearch = new JFormattedTextField();
        inSearch.setBounds(startX,0,columnWidths[4],30);
        startX += columnWidths[4];
        inSearch.setBackground(Color.white);
        searchPanel.add(inSearch);

        //outSearch
        outSearch = new JFormattedTextField();
        outSearch.setBounds(startX,0,columnWidths[5],30);
        startX += columnWidths[5];
        outSearch.setBackground(Color.white);
        searchPanel.add(outSearch);

        //okSearch
        okSearch = new JFormattedTextField();
        okSearch.setBounds(startX,0,columnWidths[6],30);
        startX += columnWidths[6];
        okSearch.setBackground(Color.white);
        searchPanel.add(okSearch);

        //notesSearch
        notesSearch = new JFormattedTextField();
        notesSearch.setBounds(startX,0,columnWidths[7],30);
        startX += columnWidths[7];
        notesSearch.setBackground(Color.white);
        searchPanel.add(notesSearch);

        //kbsSearch
        kbsSearch = new JFormattedTextField();
        kbsSearch.setBounds(startX,0,columnWidths[8],30);
        startX += columnWidths[8];
        kbsSearch.setBackground(Color.white);
        searchPanel.add(kbsSearch);

        //virtSearch
        emptySearch = new JFormattedTextField();
        emptySearch.setBounds(startX,0,columnWidths[9],30);
        startX += columnWidths[9];
        emptySearch.setBackground(Color.white);
        searchPanel.add(emptySearch);


        // Get the model from the table and create the TableRowSorter
        DefaultTableModel tableModel = (DefaultTableModel) hotelTable.getModel();
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        hotelTable.setRowSorter(rowSorter);

        //Add document listener to all search text fields.
        searchFields = new JFormattedTextField[]{
                roomSearch, nameSearch, agencySearch, typeSearch, inSearch,
                outSearch, okSearch, notesSearch, kbsSearch, emptySearch
        };
        for (JFormattedTextField searchField : searchFields) {
            searchField.getDocument().addDocumentListener(createDocumentListener(roomSearch,nameSearch,
                    agencySearch,typeSearch,inSearch,outSearch,okSearch,notesSearch,kbsSearch, emptySearch,rowSorter));
        }


        hotelTable.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if(e.getClickCount() == 2){
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = 0;
                    Object value = target.getValueAt(row,column);
                    //Selected row maybe empty. So, we should check.
                    if(value!=null) {
                        Room room = MyJDBC.getRoom(personnel, String.valueOf(value));
                        assert room != null;
                        new roomDialog(frame, hotelGui.this, room,personnel).getDialog().setVisible(true);
                    }
                }

            }
        });

        scrollPane = new JScrollPane(hotelTable);
        scrollPane.setBounds(0, 30, 718, 530);
        hotelPanel.add(searchPanel);
        hotelPanel.add(scrollPane);
        frame.add(hotelPanel);

    }


    protected static DocumentListener createDocumentListener(JFormattedTextField roomSearch,
                                            JFormattedTextField nameSearch,JFormattedTextField agencySearch,JFormattedTextField typeSearch,
                                            JFormattedTextField inSearch,JFormattedTextField outSearch,JFormattedTextField okSearch,
                                            JFormattedTextField notesSearch,JFormattedTextField kbsSearch, JFormattedTextField virtSearch,TableRowSorter<DefaultTableModel> rowSorter) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                java.util.List<RowFilter<Object, Object>> filters = new ArrayList<>();
                //Get every value in search fields and filter table properly in harmony with all those data.
                if (roomSearch != null && !roomSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + roomSearch.getText(), 0));
                }
                if (nameSearch != null && !nameSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + nameSearch.getText(), 1));
                }
                if (agencySearch != null && !agencySearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + agencySearch.getText(), 2));
                }
                if (typeSearch != null && !typeSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + typeSearch.getText(), 3));
                }
                if (inSearch != null && !inSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + inSearch.getText(), 4));
                }
                if (outSearch != null && !outSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + outSearch.getText(), 5));
                }
                if (okSearch != null && !okSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + okSearch.getText(), 6));
                }
                if (notesSearch != null && !notesSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + notesSearch.getText(), 7));
                }
                if (kbsSearch != null && !kbsSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + kbsSearch.getText(), 8));
                }
                if (virtSearch != null && !virtSearch.getText().trim().isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + virtSearch.getText(), 9));
                }

                RowFilter<Object, Object> combinedFilter = RowFilter.andFilter(filters);
                rowSorter.setRowFilter(combinedFilter);
            }
        };
    }

    public void refreshHotelGui(hotelGui hotelGui){
        guis.hotelGui newHotelGui = new hotelGui(frame,leftPanelGui,personnel,MyJDBC.createHotelTable(personnel));
        hotelGui.hotelPanel.remove(hotelGui.scrollPane);
        hotelGui.hotelPanel.remove(hotelGui.searchPanel);

        hotelGui.setHotelPanel(newHotelGui.hotelPanel);
        hotelGui.setHotelTable(newHotelGui.hotelTable);
        hotelGui.setScrollPane(newHotelGui.scrollPane);

        //Update search fields.
        hotelGui.setRoomSearch(newHotelGui.roomSearch);
        hotelGui.setNameSearch(newHotelGui.nameSearch);
        hotelGui.setAgencySearch(newHotelGui.agencySearch);
        hotelGui.setTypeSearch(newHotelGui.typeSearch);
        hotelGui.setInSearch(newHotelGui.inSearch);
        hotelGui.setOutSearch(newHotelGui.outSearch);
        hotelGui.setOkSearch(newHotelGui.okSearch);
        hotelGui.setNotesSearch(newHotelGui.notesSearch);
        hotelGui.setKbsSearch(newHotelGui.kbsSearch);
        hotelGui.setEmptySearch(newHotelGui.emptySearch);
        hotelGui.setSearchPanel(newHotelGui.searchPanel);

        //dialog.setFrame(hotelGui.getFrame());
        hotelGui.hotelPanel.invalidate();
        hotelGui.hotelPanel.validate();
        hotelGui.hotelPanel.repaint();

        leftPanelGui.setHotelGui(hotelGui);

    }


    public mainFrame getFrame() {
        return frame;
    }

    public void setFrame(mainFrame frame) {
        this.frame = frame;
    }

    public JPanel getHotelPanel() {
        return hotelPanel;
    }

    public void setHotelPanel(JPanel hotelPanel) {
        this.hotelPanel = hotelPanel;
    }

    public JTable getHotelTable() {
        return hotelTable;
    }

    public void setHotelTable(JTable hotelTable) {
        this.hotelTable = hotelTable;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

    public JPanel getSearchPanel() {
        return searchPanel;
    }

    public void setSearchPanel(JPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    public JFormattedTextField getRoomSearch() {
        return roomSearch;
    }

    public void setRoomSearch(JFormattedTextField roomSearch) {
        this.roomSearch = roomSearch;
    }

    public JFormattedTextField getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(JFormattedTextField nameSearch) {
        this.nameSearch = nameSearch;
    }

    public JFormattedTextField getAgencySearch() {
        return agencySearch;
    }

    public void setAgencySearch(JFormattedTextField agencySearch) {
        this.agencySearch = agencySearch;
    }

    public JFormattedTextField getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(JFormattedTextField typeSearch) {
        this.typeSearch = typeSearch;
    }

    public JFormattedTextField getInSearch() {
        return inSearch;
    }

    public void setInSearch(JFormattedTextField inSearch) {
        this.inSearch = inSearch;
    }

    public JFormattedTextField getOutSearch() {
        return outSearch;
    }

    public void setOutSearch(JFormattedTextField outSearch) {
        this.outSearch = outSearch;
    }

    public JFormattedTextField getOkSearch() {
        return okSearch;
    }

    public void setOkSearch(JFormattedTextField okSearch) {
        this.okSearch = okSearch;
    }

    public JFormattedTextField getNotesSearch() {
        return notesSearch;
    }

    public void setNotesSearch(JFormattedTextField notesSearch) {
        this.notesSearch = notesSearch;
    }

    public JFormattedTextField getKbsSearch() {
        return kbsSearch;
    }

    public void setKbsSearch(JFormattedTextField kbsSearch) {
        this.kbsSearch = kbsSearch;
    }

    public JFormattedTextField getEmptySearch() {
        return emptySearch;
    }

    public void setEmptySearch(JFormattedTextField emptySearch) {
        this.emptySearch = emptySearch;
    }

    public JFormattedTextField[] getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(JFormattedTextField[] searchFields) {
        this.searchFields = searchFields;
    }

    public String[] getCols() {
        return cols;
    }

    public int[] getColumnWidths() {
        return columnWidths;
    }

    public guis.leftPanelGui getLeftPanelGui() {
        return leftPanelGui;
    }

    public void setLeftPanelGui(guis.leftPanelGui leftPanelGui) {
        this.leftPanelGui = leftPanelGui;
    }
}
