package guis;

import db_obj.Guests;
import db_obj.MyJDBC;
import db_obj.Personnel;
import db_obj.Reservations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

public class OldNewReservsDialog{
    private JDialog dialog;
    private mainFrame frame;
    private hotelGui hotelGui;
    private JFormattedTextField reservIdSearch;
    private JFormattedTextField roomNoSearch;
    private JFormattedTextField namesSearch;
    private JFormattedTextField agencySearch;
    private JFormattedTextField typeSearch;
    private JFormattedTextField inSearch;
    private JFormattedTextField outSearch;
    private  JFormattedTextField notesSearch;
    private JPanel searchPanel;
    private JTable reservationsTable;
    private JScrollPane reservationsScroll;
    protected JFormattedTextField[] searchFields;
    protected final int[] columnWidths = {70, 70, 70, 70, 70, 70, 70, 80};


    public OldNewReservsDialog(mainFrame frame, hotelGui hotelGui, Personnel personnel,boolean newOld){
        //newOld : new => true , old => false.
        this.frame = frame;
        this.hotelGui = hotelGui;
        this.dialog = new JDialog();

        if(newOld){
            dialog.setTitle("NEW RESERVATIONS");
        }
        else{
            dialog.setTitle("FORMER RESERVATIONS");
        }
        dialog.setSize(600,600);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(frame);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setLayout(null);

        //Add search bars.
        searchPanel = new JPanel();
        searchPanel.setLayout(null);
        searchPanel.setBounds(0, 0, 700, 30);
        searchFields = new JFormattedTextField[columnWidths.length];

        int startX = 10;
        //roomSearch
        reservIdSearch = new JFormattedTextField();
        reservIdSearch.setBounds(startX,0,columnWidths[0],30);
        startX += columnWidths[0];
        reservIdSearch.setBackground(Color.white);
        searchPanel.add(reservIdSearch);
        searchFields[0] = reservIdSearch;

        roomNoSearch = new JFormattedTextField();
        roomNoSearch.setBounds(startX,0,columnWidths[1],30);
        startX += columnWidths[0];
        roomNoSearch.setBackground(Color.white);
        searchPanel.add(roomNoSearch);
        searchFields[1] = roomNoSearch;

        namesSearch = new JFormattedTextField();
        namesSearch.setBounds(startX,0,columnWidths[2],30);
        startX += columnWidths[0];
        namesSearch.setBackground(Color.white);
        searchPanel.add(namesSearch);
        searchFields[2] = namesSearch;

        agencySearch = new JFormattedTextField();
        agencySearch.setBounds(startX,0,columnWidths[3],30);
        startX += columnWidths[0];
        agencySearch.setBackground(Color.white);
        searchPanel.add(agencySearch);
        searchFields[3] = agencySearch;

        typeSearch = new JFormattedTextField();
        typeSearch.setBounds(startX,0,columnWidths[4],30);
        startX += columnWidths[0];
        typeSearch.setBackground(Color.white);
        searchPanel.add(typeSearch);
        searchFields[4] = typeSearch;

        inSearch = new JFormattedTextField();
        inSearch.setBounds(startX,0,columnWidths[5],30);
        startX += columnWidths[0];
        inSearch.setBackground(Color.white);
        searchPanel.add(inSearch);
        searchFields[5] = inSearch;

        outSearch = new JFormattedTextField();
        outSearch.setBounds(startX,0,columnWidths[6],30);
        startX += columnWidths[0];
        outSearch.setBackground(Color.white);
        searchPanel.add(outSearch);
        searchFields[6] = outSearch;

        notesSearch = new JFormattedTextField();
        notesSearch.setBounds(startX,0,columnWidths[7],30);
        startX += columnWidths[0];
        notesSearch.setBackground(Color.white);
        searchPanel.add(notesSearch);
        searchFields[7] = notesSearch;

        dialog.add(searchPanel);


        reservationsTable = MyJDBC.createReservsTable(personnel,newOld);


        reservationsTable.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if(e.getClickCount() == 2){
                    JTable target = (JTable)e.getSource();
                    int row = target.getSelectedRow();
                    int column = 0;
                    Object value = target.getValueAt(row,column);
                    //Selected row maybe empty. So, we should check.
                    if(value!=null) {
                        Reservations reservations = MyJDBC.getReservations(personnel,(int) value);
                        new ReservDialog(frame,OldNewReservsDialog.this,reservations,personnel,newOld).getDialog().setVisible(true);
                    }
                }

            }
        });


        // Get the model from the table and create the TableRowSorter
        DefaultTableModel tableModel = (DefaultTableModel) reservationsTable.getModel();
        TableRowSorter<DefaultTableModel> rowSorter = new TableRowSorter<>(tableModel);
        reservationsTable.setRowSorter(rowSorter);

        //Add document listener to all search text fields.
        for (JFormattedTextField searchField : searchFields) {
            searchField.getDocument().addDocumentListener(hotelGui.createDocumentListener(reservIdSearch,roomNoSearch,namesSearch,
                    agencySearch,typeSearch,inSearch,outSearch,notesSearch,null, null,rowSorter));
        }

        reservationsScroll = new JScrollPane(reservationsTable);
        reservationsScroll.setBounds(10,30,580,530);
        dialog.add(reservationsScroll);


    }

    public void refreshDialog(mainFrame frame, OldNewReservsDialog oldNewReservsDialog,Personnel personnel,boolean refreshHotel){
        //Refresh OldNewReservsDialog (Reservations table)
        OldNewReservsDialog newOldNew = new OldNewReservsDialog(frame,hotelGui,personnel,true);
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getReservationsScroll());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getReservIdSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getRoomNoSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getNamesSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getAgencySearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getTypeSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getInSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getOutSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getNotesSearch());
        oldNewReservsDialog.getDialog().remove(oldNewReservsDialog.getSearchPanel());
        newOldNew.setDialog(oldNewReservsDialog.getDialog());

        oldNewReservsDialog.setReservationsTable(newOldNew.getReservationsTable());
        oldNewReservsDialog.setReservationsScroll(newOldNew.getReservationsScroll());
        oldNewReservsDialog.setReservIdSearch(newOldNew.getReservIdSearch());
        oldNewReservsDialog.setRoomNoSearch(newOldNew.getRoomNoSearch());
        oldNewReservsDialog.setNamesSearch(newOldNew.getNamesSearch());
        oldNewReservsDialog.setAgencySearch(newOldNew.getAgencySearch());
        oldNewReservsDialog.setTypeSearch(newOldNew.getTypeSearch());
        oldNewReservsDialog.setInSearch(newOldNew.getInSearch());
        oldNewReservsDialog.setOutSearch(newOldNew.getOutSearch());
        oldNewReservsDialog.setNotesSearch(newOldNew.getNotesSearch());
        oldNewReservsDialog.setSearchPanel(newOldNew.getSearchPanel());

        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getReservationsScroll());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getSearchPanel());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getReservIdSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getRoomNoSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getNamesSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getAgencySearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getTypeSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getInSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getOutSearch());
        oldNewReservsDialog.getDialog().add(oldNewReservsDialog.getNotesSearch());

        if(refreshHotel){
            hotelGui.refreshHotelGui(hotelGui);
            oldNewReservsDialog.setHotelGui(hotelGui);
        }


        oldNewReservsDialog.getDialog().invalidate();
        oldNewReservsDialog.getDialog().validate();
        oldNewReservsDialog.getDialog().repaint();



    }

    public mainFrame getFrame() {
        return frame;
    }

    public void setFrame(mainFrame frame) {
        this.frame = frame;
    }

    public JFormattedTextField getReservIdSearch() {
        return reservIdSearch;
    }

    public void setReservIdSearch(JFormattedTextField reservIdSearch) {
        this.reservIdSearch = reservIdSearch;
    }

    public JFormattedTextField getRoomNoSearch() {
        return roomNoSearch;
    }

    public void setRoomNoSearch(JFormattedTextField roomNoSearch) {
        this.roomNoSearch = roomNoSearch;
    }

    public JFormattedTextField getNamesSearch() {
        return namesSearch;
    }

    public void setNamesSearch(JFormattedTextField namesSearch) {
        this.namesSearch = namesSearch;
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

    public JFormattedTextField getNotesSearch() {
        return notesSearch;
    }

    public void setNotesSearch(JFormattedTextField notesSearch) {
        this.notesSearch = notesSearch;
    }

    public JPanel getSearchPanel() {
        return searchPanel;
    }

    public void setSearchPanel(JPanel searchPanel) {
        this.searchPanel = searchPanel;
    }

    public JTable getReservationsTable() {
        return reservationsTable;
    }

    public void setReservationsTable(JTable reservationsTable) {
        this.reservationsTable = reservationsTable;
    }

    public JScrollPane getReservationsScroll() {
        return reservationsScroll;
    }

    public void setReservationsScroll(JScrollPane reservationsScroll) {
        this.reservationsScroll = reservationsScroll;
    }

    public JFormattedTextField[] getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(JFormattedTextField[] searchFields) {
        this.searchFields = searchFields;
    }

    public int[] getColumnWidths() {
        return columnWidths;
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

    public guis.hotelGui getHotelGui() {
        return hotelGui;
    }

    public void setHotelGui(guis.hotelGui hotelGui) {
        this.hotelGui = hotelGui;
    }
}
