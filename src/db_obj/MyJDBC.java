package db_obj;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MyJDBC {
    private static  final Map<String, String> env = System.getenv();
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/hotel";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = env.get("DATABASE_PASSWORD");

    public static Personnel validateLogin(String username, String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            statement = connection.prepareStatement("SELECT * FROM personnels WHERE username = ?");
            statement.setString(1, username);
            result = statement.executeQuery();

            if (result.next()) {
                String storedHash = result.getString("password");

                String hashedPassword = PasswordHasher.hashPassword(password);

                if (hashedPassword.equals(storedHash)) {
                    int id = result.getInt("id");
                    int hotel_id = result.getInt("hotel_id");
                    int job_title_id = result.getInt("job_title_id");
                    String first_name = result.getString("first_name");
                    String last_name = result.getString("last_name");
                    Date date = result.getDate("start_date");

                    return new Personnel(id, hotel_id, job_title_id, first_name, last_name, storedHash, username, date);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) result.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static JTable createHotelTable(Personnel personnel){
        /*
        This method will be used for show every room information with their guests that stay in currently or
        will be stayed soon information. So, this is very complex. We should get each room information,
        current reservations for each room, each guest for each current_reservation. So, we should do
        some queries on 7 database tables and gather obtained data properly.
         */
        Connection connection = null;
        PreparedStatement roomStatement = null;
        ResultSet resultRoom = null;
        PreparedStatement typeState = null;
        ResultSet resultType = null;
        PreparedStatement roomReservationState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement reservationState = null;
        ResultSet resultReservation = null;
        PreparedStatement agencyState = null;
        ResultSet resultAgency = null;
        PreparedStatement guestReservationState = null;
        ResultSet resultGuestReservation = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        try {
            JTable hotelTable;
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //int personnel_id = personnel.getId();
            int hotelId = personnel.getHotel_id();
            //rows
            String[] cols = new String[] {"NUMBER","PEOPLE","AGENCY","TYPE","IN","OUT","OK","NOTES","KBS","EMPTY"};
            //int columnNumber = 0; //It corresponds to each column.
            //String[][] rowsOfTable = new String[100][10];
            DefaultTableModel model = new DefaultTableModel(cols, 0);


            ArrayList<Object[]> data = new ArrayList<>();

            //Get rooms data. Each room and reservation data will reside in different rows.
            roomStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ?");
            roomStatement.setInt(1, hotelId);
            resultRoom = roomStatement.executeQuery();
            while (resultRoom.next()){
                //Column data:
                int id = resultRoom.getInt("id"); //Get room id
                String room_number = resultRoom.getString("room_number"); //Get room number.
                boolean available = resultRoom.getBoolean("available"); // Get available situation.
                String notes = resultRoom.getString("notes"); //Get notes.
                boolean is_empty = resultRoom.getBoolean("is_empty"); //Get whether virtual room.
                //These data will obtain from reservation data.
                Date check_in_date = new Date(2024-12-12);
                Date check_out_date = new Date(2024-12-12);;
                boolean kbs = true;
                String people = "";
                String agency = "";
                String roomType = "";
                boolean currentReservation = false;

                //Get room type:
                int typeId = resultRoom.getInt("room_type_id");
                typeState = connection.prepareStatement("SELECT * FROM room_types where id = ?");
                typeState.setInt(1,typeId);
                resultType = typeState.executeQuery();
                if(resultType.next()){
                    roomType = resultType.getString("type_name");
                }

                //Get reservation number of room.
                roomReservationState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
                roomReservationState.setInt(1,id);
                resultRoomReserv = roomReservationState.executeQuery();
                if(resultRoomReserv.next()){
                    int reservId = resultRoomReserv.getInt("reservation_id");

                    //Get  reservation infos.
                    reservationState = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
                    reservationState.setInt(1,reservId);
                    resultReservation = reservationState.executeQuery();
                    if(resultReservation.next()){
                        //Get agency infos.
                        currentReservation = resultReservation.getBoolean("current_reservation");
                        //If this reservation is placed any room.
                        if(currentReservation){
                            check_in_date = resultReservation.getDate("expected_check_in");
                            check_out_date = resultReservation.getDate("expected_check_out");
                            int agencyId = resultReservation.getInt("agency_id");

                            //Get agency name.
                            agencyState = connection.prepareStatement("SELECT * FROM agencies where id = ?");
                            agencyState.setInt(1,agencyId);
                            resultAgency = agencyState.executeQuery();
                            if(resultAgency.next()){
                                agency = resultAgency.getString("agency");
                            }
                        }

                    }
                    //If this reservation is placed any room.
                    if(currentReservation) {
                        //Get every person which has same reservation.
                        guestReservationState = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
                        guestReservationState.setInt(1, reservId);
                        resultGuestReservation = guestReservationState.executeQuery();
                        int counter = 0; //It will affect how to print guests name.
                        while (resultGuestReservation.next()) {
                            int guestId = resultGuestReservation.getInt("guest_id");

                            //Get guest infos.
                            guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                            guestState.setInt(1, guestId);
                            resultGuest = guestState.executeQuery();
                            if (resultGuest.next()) {
                                String guestFirstName = resultGuest.getString("first_name"); //Get guest's first name.
                                String guestLastName = resultGuest.getString("last_name"); //Get guest's last name.

                                //It provides adding comma for each guest name.
                                if (counter == 0) {
                                    people += guestFirstName + " " + guestLastName;
                                } else {
                                    people += ", " + guestFirstName + " " + guestLastName;
                                }

                                //For each person in the same reservation, if somebody is checked in but kbs has not updated yet or,
                                //If somebody checked out but kbs has not updated yet, kbs will be false. Otherwise, it will be true.
                                if (kbs) {
                                    if ((!(resultGuest.getBoolean("checked_in") == resultGuest.getBoolean("kbs_in"))) ||
                                            (!(resultGuest.getBoolean("checked_out") == resultGuest.getBoolean("kbs_out")))) {
                                        kbs = false;
                                    }
                                }
                            }
                            counter = 1;
                        }
                    }
                }

                //Place each data to column.
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                data.add(new Object[]{room_number,people,agency,roomType,formatter.format(check_in_date),formatter.format(check_out_date),String.valueOf(available),
                        notes,String.valueOf(kbs),String.valueOf(is_empty)});

            }

            for (Object[] row : data) {
                model.addRow(row);
            }

            hotelTable = new JTable(model);

            //Resize column widths.
            int[] columnWidths = {100, 100, 70, 70, 70, 70, 30, 100, 30, 60};
            for (int i = 0; i < columnWidths.length; i++) {
                TableColumn column = hotelTable.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
                column.setMinWidth(columnWidths[i]);
                column.setMaxWidth(columnWidths[i]);
            }

            //Prevents changing table data manually.
            hotelTable.setDefaultEditor(Object.class,null);

            //Prevent changing columns manually.
            hotelTable.getTableHeader().setReorderingAllowed(false);

            return hotelTable;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReservation != null) resultGuestReservation.close();
                if(guestReservationState != null) guestReservationState.close();
                if(resultAgency != null) resultAgency.close();
                if(agencyState != null) agencyState.close();
                if(resultReservation != null) resultReservation.close();
                if(reservationState != null) reservationState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservationState != null) roomReservationState.close();
                if(resultType != null) resultType.close();
                if(typeState != null) typeState.close();
                if(resultRoom != null) resultRoom.close();
                if(roomStatement != null) roomStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }


    public static Room getRoom(Personnel personnel, String roomNumber) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            int hotel_id = personnel.getHotel_id();
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get room information.
            preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ? AND room_number = ?");
            preparedStatement.setInt(1, hotel_id);
            preparedStatement.setString(2, roomNumber);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                int room_type_id = resultSet.getInt("room_type_id");
                boolean available = resultSet.getBoolean("available");
                boolean clean = resultSet.getBoolean("clean");
                boolean tech_ok = resultSet.getBoolean("tech_ok");
                boolean is_empty = resultSet.getBoolean("is_empty");
                boolean virt = resultSet.getBoolean("virtual_room");
                String notes = resultSet.getString("notes");
                if(notes!=null){
                    return new Room(id,hotel_id,room_type_id,roomNumber,notes,available,clean,tech_ok,is_empty,virt);
                }
                return new Room(id,hotel_id,room_type_id,roomNumber,available,clean,tech_ok,is_empty,virt);
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static JTable getGuestsTable(Room room){
        /*
        This method will be used for display guests that reside in specific room currently or
        will reside soon. Thus, related information about each guest should be obtained from
        related database tables.
         */
        Connection connection = null;
        PreparedStatement roomReservationState = null;
        ResultSet resultRoomReservation = null;
        PreparedStatement reservationState = null;
        ResultSet resultReservation = null;
        PreparedStatement agencyState = null;
        ResultSet resultAgency = null;
        PreparedStatement guestReservationState = null;
        ResultSet resultGuestReservation = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        try{
            String[] columns = {"NAME","AGENCY","AGE","IN DATE","OUT DATE","IN","OUT"};
            //I just wonder how it would be that table has constant rows. So I did not change it.
            String[][] rows = new String[15][7];
            String agency = "";
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            roomReservationState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
            roomReservationState.setInt(1,room.getId());
            resultRoomReservation = roomReservationState.executeQuery();
            boolean current_reservation = false;
            if(resultRoomReservation.next()) {
                //Get reservation id.
                int reservationId = resultRoomReservation.getInt("reservation_id");
                //Get agency id.
                reservationState = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
                reservationState.setInt(1, reservationId);
                resultReservation = reservationState.executeQuery();
                if (resultReservation.next()) {
                    current_reservation = resultReservation.getBoolean("current_reservation");
                    //If this reservation is placed any room.
                    if (current_reservation) {
                        int agency_id = resultReservation.getInt("agency_id");
                        //Get agency name
                        agencyState = connection.prepareStatement("SELECT * FROM agencies WHERE id = ?");
                        agencyState.setInt(1, agency_id);
                        resultAgency = agencyState.executeQuery();
                        if (resultAgency.next()) {
                            agency = resultAgency.getString("agency");
                        }
                    }
                }
                //If this reservation is placed any room.
                if (current_reservation) {
                    guestReservationState = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
                    guestReservationState.setInt(1, reservationId);
                    resultGuestReservation = guestReservationState.executeQuery();
                    //Get every guest which have same reservation.
                    int guestNumber = 0;
                    while (resultGuestReservation.next()) {
                        int id = resultGuestReservation.getInt("guest_id");
                        guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                        guestState.setInt(1, id);
                        resultGuest = guestState.executeQuery();
                        if (resultGuest.next()) {
                            String first_name = resultGuest.getString("first_name");
                            String last_name = resultGuest.getString("last_name");
                            java.sql.Date birth_date = resultGuest.getDate("birth_date");
                            Timestamp check_in_datetime = resultGuest.getTimestamp("check_in_datetime");
                            Timestamp check_out_datetime = resultGuest.getTimestamp("check_out_datetime");
                            boolean checked_in = resultGuest.getBoolean("checked_in");
                            boolean checked_out = resultGuest.getBoolean("checked_out");

                            rows[guestNumber][0] = first_name + " " + last_name;
                            rows[guestNumber][1] = agency;
                            if (birth_date != null) {
                                rows[guestNumber][2] = calculateAge(birth_date);
                            }
                            rows[guestNumber][3] = String.valueOf(check_in_datetime);
                            rows[guestNumber][4] = String.valueOf(check_out_datetime);
                            rows[guestNumber][5] = String.valueOf(checked_in);
                            rows[guestNumber][6] = String.valueOf(checked_out);
                        }
                        guestNumber++;
                    }
                }
            }

            DefaultTableModel tableModel = new DefaultTableModel(rows,columns);
            JTable table = new JTable(tableModel);
            int[] columnWidths = {170,70,30,100,100,40,40};
            for (int i = 0; i < columnWidths.length; i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
            }
            table.setDefaultEditor(Object.class,null);

            //Prevent changing columns manually.
            table.getTableHeader().setReorderingAllowed(false);

            return table;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReservation != null) resultGuestReservation.close();
                if(guestReservationState != null) guestReservationState.close();
                if(resultAgency != null) resultAgency.close();
                if(agencyState != null) agencyState.close();
                if(resultReservation != null) resultReservation.close();
                if(reservationState != null) reservationState.close();
                if(resultRoomReservation != null) resultRoomReservation.close();
                if(roomReservationState != null) roomReservationState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean getKBS(Room room){
        /*
        KBS is abbreviation of KIMLIK BILDIRIM SISTEMI in Turkish. Hotels in Turkey must report
        each guest's check in and check out. It is important because of some legal stuff. It is really
        elaboration, but I just want to simulate it because this feature will help for approach
        creating like real projects. So, I took care every detail.
         */
        Connection connection = null;
        PreparedStatement roomReservState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement guestReservState = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        try{
            boolean kbs = true;
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            roomReservState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
            roomReservState.setInt(1,room.getId());
            resultRoomReserv = roomReservState.executeQuery();
            //Get reservation id.
            if(resultRoomReserv.next()){
                int reservation_id = resultRoomReserv.getInt("reservation_id");
                guestReservState = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
                guestReservState.setInt(1,reservation_id);
                resultGuestReserv = guestReservState.executeQuery();
                //Get every guest have the same reservation
                while (resultGuestReserv.next() && kbs){
                    int guest_id = resultGuestReserv.getInt("guest_id");
                    guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                    guestState.setInt(1,guest_id);
                    resultGuest = guestState.executeQuery();
                    //Guest infos
                    if(resultGuest.next()){
                        boolean checked_in = resultGuest.getBoolean("checked_in");
                        boolean checked_out = resultGuest.getBoolean("checked_out");
                        boolean kbs_in = resultGuest.getBoolean("kbs_in");
                        boolean kbs_out = resultGuest.getBoolean("kbs_out");

                        //KBS Control
                        if((!(checked_in == kbs_in)) || (!(checked_out == kbs_out))){
                            kbs = false;
                        }
                    }
                }

            }
            return kbs;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReserv != null) resultGuestReserv.close();
                if(guestReservState != null) guestReservState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservState != null) roomReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Guests getGuest(Room room, int row){
        /*
        In the same room there must be more than one people have the same name and surname.
        Although, our table placed by guest's id. So, if we choose first one which has the same name, then
        we should look for guest which has the smallest id that reside in that room.
        Thus, we should go with the id in the room_reservations.
         */
        /*
        row parameter obtained from table. We search guest by row instead of it's name, surname reservation etc.
        because while creating new reservation, resarvationist does not have to declare every information about
        every guest. This is how works in real hotels. So, these limited information may be cause of problems.
        For example, we have 2 guests have same name and surname in the same room. Since they book for same room,
        they have same reservation. Thus, every information about them are same. What is the difference?
        Difference are their guest id, but it is not placed in the table. But, since guest names placed table
        in ascending order, we can search the guest by his row number in the table.
         */
        Connection connection = null;
        PreparedStatement roomReservationState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement guestReservState = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;
        try{
            int roomId = room.getId();
            int hotel_id = room.getHotel_id();
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            roomReservationState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
            roomReservationState.setInt(1,roomId);
            resultRoomReserv = roomReservationState.executeQuery();
            //Get reservation
            if(resultRoomReserv.next()){
                int reservationId = resultRoomReserv.getInt("reservation_id");
                //Get guest id
                guestReservState = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
                guestReservState.setInt(1,reservationId);
                resultGuestReserv = guestReservState.executeQuery();
                int guestNumber = 0;
                //Get user according to row.
                while(resultGuestReserv.next() && guestNumber <= row){
                    if(guestNumber == row){
                        int guestId = resultGuestReserv.getInt("guest_id");
                        //Get guest
                        guestState = connection.prepareStatement("SELECT * FROM guests where id = ?");
                        guestState.setInt(1,guestId);
                        resultGuest = guestState.executeQuery();
                        if(resultGuest.next()){
                            String first_name = resultGuest.getString("first_name");
                            String last_name = resultGuest.getString("last_name");
                            String country = resultGuest.getString("country");
                            int id_number = resultGuest.getInt("id_number");
                            Date birth_date = resultGuest.getDate("birth_date");
                            Timestamp check_in_datetime = resultGuest.getTimestamp("check_in_datetime");
                            Timestamp check_out_datetime = resultGuest.getTimestamp("check_out_datetime");
                            boolean kbs_in = resultGuest.getBoolean("kbs_in");
                            boolean kbs_out = resultGuest.getBoolean("kbs_out");
                            boolean checked_in = resultGuest.getBoolean("checked_in");
                            boolean checked_out = resultGuest.getBoolean("checked_out");
                            return new Guests(guestId,hotel_id,reservationId,first_name,last_name,country,
                                    id_number,birth_date,check_in_datetime,check_out_datetime,
                                    kbs_in,kbs_out,checked_in,checked_out);
                        }
                    }
                    guestNumber ++;
                }
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }

        finally {
            // Close resources
            try {
                if (resultGuest != null) resultGuest.close();
                if (guestState != null) guestState.close();
                if (resultGuestReserv != null) resultGuestReserv.close();
                if (guestReservState != null) guestReservState.close();
                if (resultRoomReserv != null) resultRoomReserv.close();
                if (roomReservationState != null) roomReservationState.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;

    }

    private static String getAgency(Connection connection, int reservation_id){
        /*
        This method is private and Connection is one of the parameter of it.
        It uses connection of the function this function called by. Because if I create connection and reconnection again,
        it causes a lot of performance problem because connection and reconnection to database take too much time.
         */
        PreparedStatement reservationState = null;
        ResultSet resultReserv = null;
        PreparedStatement agencyState = null;
        ResultSet resultAgency = null;
        try{
            reservationState = connection.prepareStatement("SELECT * FROM reservations where id = ?");
            reservationState.setInt(1,reservation_id);
            resultReserv = reservationState.executeQuery();
            if(resultReserv.next()){
                int agency_id = resultReserv.getInt("agency_id");
                //Get agency name
                agencyState = connection.prepareStatement("SELECT * FROM  agencies WHERE id = ?");
                agencyState.setInt(1,agency_id);
                resultAgency = agencyState.executeQuery();
                if(resultAgency.next()){
                    return resultAgency.getString("agency");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultAgency != null) resultAgency.close();
                if(agencyState != null) agencyState.close();
                if(resultReserv != null) resultReserv.close();
                if(reservationState != null) reservationState.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getAgency(int reservation_id){
        Connection connection = null;
        PreparedStatement reservationState = null;
        ResultSet resultReserv = null;
        PreparedStatement agencyState = null;
        ResultSet resultAgency = null;
        try{
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            reservationState = connection.prepareStatement("SELECT * FROM reservations where id = ?");
            reservationState.setInt(1,reservation_id);
            resultReserv = reservationState.executeQuery();
            if(resultReserv.next()){
                int agency_id = resultReserv.getInt("agency_id");
                //Get agency name
                agencyState = connection.prepareStatement("SELECT * FROM  agencies WHERE id = ?");
                agencyState.setInt(1,agency_id);
                resultAgency = agencyState.executeQuery();
                if(resultAgency.next()){
                    return resultAgency.getString("agency");
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultAgency != null) resultAgency.close();
                if(agencyState != null) agencyState.close();
                if(resultReserv != null) resultReserv.close();
                if(reservationState != null) reservationState.close();
                if (connection != null)  connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void applyGuest(Guests guests, String first_name, String last_name, String country,
                                  int id_number, Date birth_date, int reservation_id, boolean checked_in, boolean checked_out){


        //This method using for update room and guest information when personnel changes the specific guest information.

        Connection connection = null;
        PreparedStatement roomReservState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement roomState = null;
        PreparedStatement guestStatement = null;
        PreparedStatement kbsState = null;

        //These controls using for checking related guest checked out or checked in.
        boolean checked_in_control = guests.isChecked_in();
        boolean checked_out_control = guests.isChecked_out();

        //Update guests information.
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            //If guest checked in
            if(checked_in != checked_in_control && checked_out == checked_out_control){
                roomReservState = connection.prepareStatement("SELECT * FROM room_reservations WHERE reservation_id = ?");
                roomReservState.setInt(1,guests.getReservation_id());
                resultRoomReserv = roomReservState.executeQuery();
                if(resultRoomReserv.next()){
                    int roomId = resultRoomReserv.getInt("room_id");
                    roomState = connection.prepareStatement("UPDATE rooms SET available = 0, is_empty = 0 WHERE id = ?");
                    roomState.setInt(1,roomId);
                    int affected = roomState.executeUpdate();
                }

                kbsState = connection.prepareStatement("INSERT INTO kbs (guest_id,kbs_in,in_time,hotel_id) VALUES (?,1,NOW(),?)");
                kbsState.setInt(1,guests.getId());
                kbsState.setInt(2,guests.getHotel_id());
                int rowsKbsState = kbsState.executeUpdate();

                guestStatement = connection.prepareStatement("UPDATE guests SET first_name = ? , " +
                        "last_name = ? , country = ? , id_number = ? , birth_date = ? , reservation_id = ? , checked_in = ? , checked_out = ? , " +
                        "check_in_datetime = NOW() WHERE id = ?");

            }
            //If guest checked out
            else if(checked_in == checked_in_control && checked_out != checked_out_control){

                kbsState = connection.prepareStatement("INSERT INTO kbs (guest_id,kbs_out,out_time,hotel_id) VALUES (?,1,NOW(),?)");
                kbsState.setInt(1,guests.getId());
                kbsState.setInt(2,guests.getHotel_id());
                int rowsKbsState = kbsState.executeUpdate();

                guestStatement = connection.prepareStatement("UPDATE guests SET first_name = ? , " +
                        "last_name = ? , country = ? , id_number = ? , birth_date = ? , reservation_id = ? , checked_in = ? , checked_out = ? , " +
                        "check_out_datetime = NOW() WHERE id = ?");
            }
            //If guest checked in and checked out for some reason.
            else if(checked_in != checked_in_control && checked_out != checked_out_control){

                kbsState = connection.prepareStatement("INSERT INTO kbs (guest_id,kbs_in,kbs_out,in_time,out_time,hotel_id) VALUES (?,1,1,NOW(),NOW(),?)");
                kbsState.setInt(1,guests.getId());
                kbsState.setInt(2,guests.getHotel_id());
                int rowsKbsState = kbsState.executeUpdate();

                guestStatement = connection.prepareStatement("UPDATE guests SET first_name = ? , " +
                        "last_name = ? , country = ? , id_number = ? , birth_date = ? , reservation_id = ? , checked_in = ? , checked_out = ? , " +
                        "check_in_datetime = NOW() , check_out_datetime = NOW() WHERE id = ?");
            }
            else {
                guestStatement = connection.prepareStatement("UPDATE guests SET first_name = ? , " +
                        "last_name = ? , country = ? , id_number = ? , birth_date = ? , reservation_id = ? , checked_in = ? , checked_out = ? WHERE id = ?");
            }
            guestStatement.setString(1,first_name);
            guestStatement.setString(2,last_name);
            guestStatement.setString(3,country);
            guestStatement.setInt(4,id_number);
            guestStatement.setDate(5,(java.sql.Date)(birth_date));
            guestStatement.setInt(6,reservation_id);
            guestStatement.setBoolean(7,checked_in);
            guestStatement.setBoolean(8,checked_out);
            guestStatement.setInt(9,guests.getId());
            int rowsAffected = guestStatement.executeUpdate();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(kbsState != null) kbsState.close();
                if(guestStatement != null) guestStatement.close();
                if(roomState != null) roomState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservState != null) roomReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void checkInOutActivity(Room room, boolean in_out){
        //boolean in_out: Check In = true, Check Out = false
        //Check in or check out every guest stay in that room.
        Connection connection = null;
        PreparedStatement roomReservState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement reservState = null;
        PreparedStatement deleteState = null;
        PreparedStatement guestReservState = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement roomState = null;
        PreparedStatement guestState = null;
        PreparedStatement kbsState = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get reservation id.
            roomReservState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
            roomReservState.setInt(1,room.getId());
            resultRoomReserv = roomReservState.executeQuery();
            if(resultRoomReserv.next()){
                int reservationId = resultRoomReserv.getInt("reservation_id");

                //If all the guests checked out, then that reservation is not current reservation anymore.
                if (!in_out){
                    reservState = connection.prepareStatement("UPDATE reservations SET current_reservation = 0 , old_reservation = 1 WHERE id = ?");
                    reservState.setInt(1,reservationId);
                    int rowsReservState = reservState.executeUpdate();

                    deleteState = connection.prepareStatement("DELETE FROM room_reservations WHERE reservation_id = ?");
                    deleteState.setInt(1,reservationId);
                    int rowsDelete = deleteState.executeUpdate();
                }

                //Get guest id.
                guestReservState = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
                guestReservState.setInt(1,reservationId);
                resultGuestReserv = guestReservState.executeQuery();
                while(resultGuestReserv.next()){
                    int guestId = resultGuestReserv.getInt("guest_id");
                    //Check in
                    if(in_out) {
                        //Change kbs situation if guest did not checked in before. Otherwise, kbs situation has already changed.
                        if(!isCheckedInOut(connection,guestId,in_out)) {
                            kbsState = connection.prepareStatement("INSERT INTO kbs (guest_id,kbs_in,in_time,hotel_id) VALUES (?,1,NOW(),?)");
                            kbsState.setInt(1, guestId);
                            kbsState.setInt(2, room.getHotel_id());
                            int rowsKbsState = kbsState.executeUpdate();
                        }
                        guestState = connection.prepareStatement("UPDATE guests SET checked_in = 1, check_in_datetime = NOW() WHERE id = ? AND checked_in = 0");
                    }
                    //Check out
                    else{
                        //Change kbs situation if guest did not checked out before. Otherwise, kbs situation has already changed.
                        if(!isCheckedInOut(connection,guestId,in_out)) {
                            kbsState = connection.prepareStatement("INSERT INTO kbs (guest_id,kbs_out,out_time,hotel_id) VALUES (?,1,NOW(),?)");
                            kbsState.setInt(1, guestId);
                            kbsState.setInt(2, room.getHotel_id());
                            int rowsKbsState = kbsState.executeUpdate();
                        }
                        guestState = connection.prepareStatement("UPDATE guests SET checked_out = 1, check_out_datetime = NOW() WHERE id = ? AND checked_out = 0");
                    }
                    guestState.setInt(1, guestId);
                    int rowsAffected = guestState.executeUpdate();
                }
            }
            if(in_out) {
                //After check in set room availability false.
                roomState = connection.prepareStatement("UPDATE rooms SET available = 0, is_empty = 0 WHERE id = ?");
                roomState.setInt(1,room.getId());
                int affected = roomState.executeUpdate();
            }
            else{
                //After check out set room availability and clean status false for cleaning etc.
                roomState = connection.prepareStatement("UPDATE rooms SET clean = 0 , available = 0, is_empty = 1 WHERE id = ?");
                roomState.setInt(1,room.getId());
                int affected = roomState.executeUpdate();
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(roomState != null) roomState.close();
                if(guestState != null) guestState.close();
                if(kbsState != null) kbsState.close();
                if(resultGuestReserv != null) resultGuestReserv.close();
                if(guestReservState != null) guestReservState.close();
                if(deleteState != null) deleteState.close();
                if(reservState != null) reservState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservState != null) roomReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static Room getRoomByGuests(Guests guests){
        //Get guest's room information.
        Connection connection = null;
        PreparedStatement roomReservState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement roomState = null;
        ResultSet resultRoom = null;
        try {
            int hotel_id = guests.getHotel_id();
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get room id.
            roomReservState = connection.prepareStatement("SELECT * FROM room_reservations WHERE reservation_id = ?");
            roomReservState.setInt(1,guests.getReservation_id());
            resultRoomReserv = roomReservState.executeQuery();
            if(resultRoomReserv.next()){
                int roomId = resultRoomReserv.getInt("room_id");
                //Get room information.
                roomState = connection.prepareStatement("SELECT * FROM rooms WHERE id = ?");
                roomState.setInt(1,roomId);
                resultRoom = roomState.executeQuery();
                if (resultRoom.next()) {
                    int id = resultRoom.getInt("id");
                    String roomNumber = resultRoom.getString("room_number");
                    int room_type_id = resultRoom.getInt("room_type_id");
                    boolean available = resultRoom.getBoolean("available");
                    boolean clean = resultRoom.getBoolean("clean");
                    boolean tech_ok = resultRoom.getBoolean("tech_ok");
                    boolean virt = resultRoom.getBoolean("virtual_room");
                    boolean is_empty = resultRoom.getBoolean("is_empty");
                    String notes = resultRoom.getString("notes");
                    if(notes!=null){
                        return new Room(id,hotel_id,room_type_id,roomNumber,notes,available,clean,tech_ok,is_empty,virt);
                    }
                    return new Room(id,hotel_id,room_type_id,roomNumber,available,clean,tech_ok,is_empty,virt);
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultRoom != null) resultRoom.close();
                if(roomState != null) roomState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservState != null) roomReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;

    }

    public static void applyRoom(Room room,boolean available, boolean clean, boolean tech_ok, String notes){
        //Update changed room data.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        boolean controlClean = room.isClean();
        boolean controlTech = room.isTech_ok();
        try {

            //if room is not clean or room has some technical problems, it means room is not available.
            if(((controlClean) && (clean != controlClean)) || ((controlTech) && (tech_ok != controlTech))){
                available = false;
            }

            //Update room information.
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("UPDATE rooms SET available = ? , clean = ? , tech_ok = ? , notes = ? where id = ?");
            preparedStatement.setBoolean(1,available);
            preparedStatement.setBoolean(2,clean);
            preparedStatement.setBoolean(3,tech_ok);
            preparedStatement.setString(4,notes);
            preparedStatement.setInt(5,room.getId());
            int rowAffected = preparedStatement.executeUpdate();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static Reservations getReservations(Room room){
        Connection connection = null;
        PreparedStatement roomReservState = null;
        ResultSet resultRoomReserv = null;
        PreparedStatement reservState = null;
        ResultSet resultReserv = null;
        try {
            //Get room_reservation id to connect reservations.
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            roomReservState = connection.prepareStatement("SELECT * FROM room_reservations WHERE room_id = ?");
            roomReservState.setInt(1,room.getId());
            resultRoomReserv = roomReservState.executeQuery();
            if(resultRoomReserv.next()){
                int reservId = resultRoomReserv.getInt("reservation_id");
                reservState = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
                reservState.setInt(1,reservId);
                resultReserv = reservState.executeQuery();
                //Get reservation info.
                if(resultReserv.next()){
                    int id = resultReserv.getInt("id");
                    int hotel_id = resultReserv.getInt("hotel_id");
                    int agency_id = resultReserv.getInt("agency_id");
                    int room_type_id = resultReserv.getInt("room_type_id");
                    int reservation_price = resultReserv.getInt("reservation_price");
                    int extra_debt = resultReserv.getInt("extra_debt");
                    String room_number = resultReserv.getString("room_number");
                    int number_of_people = resultReserv.getInt("number_of_people");
                    Date expected_check_in = resultReserv.getDate("expected_check_in");
                    Date expected_check_out = resultReserv.getDate("expected_check_out");
                    String notes = resultReserv.getString("notes");
                    boolean old_reservation = resultReserv.getBoolean("old_reservation");
                    boolean current_reservation = resultReserv.getBoolean("current_reservation");
                    boolean future_reservation = resultReserv.getBoolean("future_reservation");
                    String extras = resultReserv.getString("extras");
                    boolean paid = resultReserv.getBoolean("paid");
                    return new Reservations(id,hotel_id,agency_id,room_type_id,reservation_price,extra_debt,
                            room_number,number_of_people,expected_check_in,expected_check_out,notes,
                            old_reservation,current_reservation,future_reservation,extras,paid);

                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultReserv != null) resultReserv.close();
                if(reservState != null) reservState.close();
                if(resultRoomReserv != null) resultRoomReserv.close();
                if(roomReservState != null) roomReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String getTitle(Personnel personnel){
        Connection connection = null;
        PreparedStatement titleState = null;
        ResultSet resultTitle = null;
        try {
            //Get job title name
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            titleState = connection.prepareStatement("SELECT * FROM job_titles WHERE id = ?");
            titleState.setInt(1,personnel.getJob_title_id());
            resultTitle = titleState.executeQuery();
            if (resultTitle.next()){
                return resultTitle.getString("title_name");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultTitle != null) resultTitle.close();
                if(titleState != null) titleState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<String> getAgencies(){
        //Get every agency to display in front end.
        Connection connection = null;
        PreparedStatement agencyState = null;
        ResultSet resultAgency = null;
        ArrayList<String> agencies = new ArrayList<>();
        try {
            //Get all the agencies to display on front end.
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            agencyState = connection.prepareStatement("SELECT * FROM agencies");
            resultAgency = agencyState.executeQuery();
            while (resultAgency.next()){
                String agencyName = resultAgency.getString("agency");
                agencies.add(agencyName);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultAgency != null) resultAgency.close();
                if(agencyState != null) agencyState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return agencies;
    }

    public static String[] calculatePrice(int hotelId, Date inDate, Date outDate, String typeName){
        /*
        In hotels, every room type has different prices in every different era. So, every room type price
        in this app can be decidable and modifiable. Thus, reservation price should be calculated for each
        reservation request. Guest may be booked hotel included 5 different price era. So every possibility
        should be checked and calculate.
         */
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        long inDateLong = inDate.getTime();
        java.sql.Date inDateSql = new java.sql.Date(inDateLong);
        long outDateLong = outDate.getTime();
        java.sql.Date outDateSql = new java.sql.Date(outDateLong);
        ArrayList<Integer> prices = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        int result = 0;
        int totalDate = 0;
        String details = "";
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            int typeId = MyJDBC.getRoomTypeId(connection,typeName);
            //Get room_type_dates information for calculate price and crate explanation.
            /*Get room_type_dates according to given parameters in ascending order.
            Ascending order is very important for explanation. Because, what is the room price in related era
             will be demonstrated in explanation. If we do not get price with connected dates in ascending order
              explanation would be so confusing since, explanation would not have an order.
             */
            preparedStatement = connection.prepareStatement("SELECT * FROM room_type_dates WHERE hotel_id = ? AND room_type_id = ? AND end_date >= ? AND start_date <=  ? ORDER BY start_date ASC");
            preparedStatement.setInt(1,hotelId);
            preparedStatement.setInt(2,typeId);
            preparedStatement.setDate(3,inDateSql);
            preparedStatement.setDate(4,outDateSql);
            resultSet = preparedStatement.executeQuery();

            //details is the explanation.
            details += "For " + typeName + " room type\n\n";
            long diffInMillies = 0;
            long diffInDays = 0;
            while(resultSet.next()){
                Date startDate = resultSet.getDate("start_date");
                Date endDate = resultSet.getDate("end_date");
                int price = resultSet.getInt("price");

                //If guest check in and check out in the same price era.
                if(inDate.compareTo(startDate) > 0 && outDate.compareTo(endDate) < 0){
                    diffInMillies = Math.abs(outDate.getTime() - inDate.getTime());
                    details += "PRICE BETWEEN " + formatter.format(inDate) + " and " + formatter.format(outDate) + " : " + String.valueOf(price) + " TL\n";
                }
                //if guest will be checked in about middle of the era (Not end of the era or not beginning of the era).
                else if(inDate.compareTo(startDate) > 0){
                    diffInMillies = Math.abs(endDate.getTime() - inDate.getTime());
                    details += "PRICE BETWEEN " + formatter.format(inDate) + " and " + formatter.format(endDate) + " : " + String.valueOf(price) + " TL\n";
                }
                //if guest will be checked out about middle of the era (Not end of the era or not beginning of the era).
                else if(outDate.compareTo(endDate) < 0){
                    diffInMillies = Math.abs(outDate.getTime() - startDate.getTime());
                    details += "PRICE BETWEEN " + formatter.format(startDate) + " and " + formatter.format(outDate) + " : " + String.valueOf(price) + " TL\n";
                }
                else{
                    diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
                    details += "PRICE BETWEEN " + formatter.format(startDate) + " and " + formatter.format(endDate) + " : " + String.valueOf(price) + " TL\n";
                }
                diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
                int eraPrice = (int)(price*diffInDays);
                result += eraPrice;
                details += String.format("%d TL x %d days = %d TL\n\n",price,diffInDays,eraPrice);
                prices.add(eraPrice);
                totalDate += (int)diffInDays;

            }
            details += "----------------------------------\n";
            for(int price : prices){
                details += "   " + String.valueOf(price) + " TL\n";
            }
            details += "+\n---------------------\n";
            details += "   " + result + " TL\n\n";
            details += String.valueOf(result) + " TL for " + totalDate + " days in " + typeName + " room type.\n";

            return new String[]{String.valueOf(result),details};

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
             if(resultSet != null) resultSet.close();
             if(preparedStatement != null) preparedStatement.close();
             if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void createReservationWGuest(int hotelId, ArrayList<Object> names, Timestamp inDate, Timestamp outDate, String agency, String roomType, int price, String notes){

        //This method is using for creating new reservations and new guests that connected to that new reservation.

        Connection connection = null;
        PreparedStatement reservState = null;
        ResultSet resultReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;
        PreparedStatement guestReservState = null;

        try {
            int[] guestIds = new int[names.size()];
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            int roomTypeId = getRoomTypeId(connection,roomType);
            int agencyId = getAgencyId(connection,agency);
            //Create new reservation and get that reservation infos.
            /*
            Statement.RETURN_GENERATED_KEYS is used for getting reservation that just created information.
            We placed every argument except id. But, we need id for connecting new guests to new reservation.
             */
            reservState = connection.prepareStatement("INSERT INTO reservations (hotel_id,expected_check_in,expected_check_out,reservation_price,agency_id,room_type_id,number_of_people,notes) VALUES (?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            reservState.setInt(1,hotelId);
            reservState.setTimestamp(2,inDate);
            reservState.setTimestamp(3,outDate);
            reservState.setInt(4,price);
            reservState.setInt(5,agencyId);
            reservState.setInt(6,roomTypeId);
            reservState.setInt(7,names.size());
            reservState.setString(8,notes);
            int rowsAffected = reservState.executeUpdate();
            resultReserv = reservState.getGeneratedKeys();
            if (resultReserv.next()) {
                int index = 0;
                int reservationId = resultReserv.getInt(1);
                for(Object name : names) {
                    //Create new Guests.
                    //Statement.RETURN_GENERATED_KEYS is used for getting guests that just created information.
                    guestState = connection.prepareStatement("INSERT INTO guests (first_name,last_name,check_in_datetime,check_out_datetime,hotel_id,reservation_id) VALUES (?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    String[] nameSurname = ((String) name).split(" ");

                    //Name and surname adjustment.
                    String first_name = nameSurname[0];
                    String last_name = nameSurname[nameSurname.length-1];
                    if(nameSurname.length > 2){
                        //If guest has more than 2 names, it means he has 1 surname and more than 1 name. So we should rearrange it.
                        for (int i = 1; i < nameSurname.length -1; i++){
                            first_name += " " + nameSurname[i];
                        }
                    } else if (nameSurname.length == 1) {
                        last_name = "";
                    }
                    guestState.setString(1,first_name);
                    guestState.setString(2,last_name);
                    guestState.setTimestamp(3,inDate);
                    guestState.setTimestamp(4,outDate);
                    guestState.setInt(5,hotelId);
                    guestState.setInt(6,reservationId);
                    int affected = guestState.executeUpdate();
                    resultGuest = guestState.getGeneratedKeys();
                    if(resultGuest.next()){
                        guestIds[index] = resultGuest.getInt(1);
                        index ++;
                    }
                    else {
                        throw new SQLException("Creating reservation failed");
                    }

                }
                //Connect each guest to reservation after while loop.
                for(int guestId : guestIds){
                    guestReservState = connection.prepareStatement("INSERT INTO guest_reservations (reservation_id,guest_id) VALUES (?,?)");
                    guestReservState.setInt(1,reservationId);
                    guestReservState.setInt(2,guestId);
                    int affected = guestReservState.executeUpdate();
                }

            } else {
                throw new SQLException("Creating reservation failed");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(guestReservState != null) guestReservState.close();
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultReserv != null) resultReserv.close();
                if(reservState != null) reservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }

    }

    private static int getAgencyId(Connection connection, String agencyName){
        /*
        This method is private and Connection is one of the parameter of it.
        It uses connection of the function this function called by. Because if I create connection and reconnection again,
        it causes a lot of performance problem because connection and reconnection to database take too much time.
         */
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM agencies WHERE agency = ?");
            preparedStatement.setString(1,agencyName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public static ArrayList<String> getRoomTypes(int hotelId){
        //Get every room type to display in front end.
        Connection connection = null;
        PreparedStatement hotelRoomStatement = null;
        ResultSet resultHotelRoom = null;
        PreparedStatement roomTypeState = null;
        ResultSet resultRoomType = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<String> roomTypes = new ArrayList<>();
            hotelRoomStatement = connection.prepareStatement("SELECT * FROM hotel_room_types WHERE hotel_id = ?");
            hotelRoomStatement.setInt(1,hotelId);
            resultHotelRoom = hotelRoomStatement.executeQuery();
            while (resultHotelRoom.next()){
                int room_type_id = resultHotelRoom.getInt("room_type_id");
                roomTypeState = connection.prepareStatement("SELECT * FROM room_types WHERE id = ?");
                roomTypeState.setInt(1,room_type_id);
                resultRoomType = roomTypeState.executeQuery();
                if(resultRoomType.next()){
                    roomTypes.add(resultRoomType.getString("type_name"));
                }
            }
            return roomTypes;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultRoomType != null) resultRoomType.close();
                if(roomTypeState != null) roomTypeState.close();
                if(resultHotelRoom != null) resultHotelRoom.close();
                if(hotelRoomStatement != null) hotelRoomStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static JTable createReservsTable(Personnel personnel, boolean newOld){
        //newOld : old => false, new => true.
        // Creates future or old reservations tables.
        Connection connection = null;
        PreparedStatement reservStatement = null;
        ResultSet resultReserv = null;
        // Create a table model with column names
        String[] columnNames = {"ID", "NUMBER", "NAMES", "AGENCY", "TYPE", "IN", "OUT","NOTES"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);


        ArrayList<Object[]> data = new ArrayList<>();

        try {
            //Get every future or old reservation information.
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            if(newOld){
                reservStatement = connection.prepareStatement("SELECT * FROM reservations WHERE hotel_id = ? AND future_reservation = 1");
            }
            else{
                reservStatement = connection.prepareStatement("SELECT * FROM reservations WHERE hotel_id = ? AND old_reservation = 1");
            }
            reservStatement.setInt(1,personnel.getHotel_id());
            resultReserv = reservStatement.executeQuery();
            while (resultReserv.next()){
                int reservId = resultReserv.getInt("id");
                String roomNumber = resultReserv.getString("room_number");
                Timestamp expected_check_in = resultReserv.getTimestamp("expected_check_in");
                Timestamp expected_check_out = resultReserv.getTimestamp("expected_check_out");
                int roomTypeId = resultReserv.getInt("room_type_id");
                String agencyName = getAgency(connection,reservId);
                String roomType = getRoomTypeName(connection,roomTypeId);
                String notes = resultReserv.getString("notes");
                ArrayList<String> names = getReservationGuestsName(connection,reservId);
                String namesField = "";
                if(names != null) {
                    for (int i = 0; i < names.size() - 1; i += 2) {
                        namesField += (String) names.get(i) + " " + (String) names.get(i + 1);
                        if (i != names.size() - 2) {
                            namesField += ", ";
                        }
                    }
                }
                data.add(new Object[]{reservId,roomNumber,namesField,agencyName,roomType,expected_check_in,expected_check_out,notes});

            }

            for (Object[] row : data) {
                model.addRow(row);
            }

            JTable table = new JTable(model);

            int[] columnWidths = {70, 70, 70, 70, 70, 70, 70, 80};
            for (int i = 0; i < columnWidths.length; i++) {
                TableColumn column = table.getColumnModel().getColumn(i);
                column.setPreferredWidth(columnWidths[i]);
                column.setMinWidth(columnWidths[i]);
                column.setMaxWidth(columnWidths[i]);
            }

            //Prevents changing table data manually.
            table.setDefaultEditor(Object.class,null);

            //Prevent changing columns manually.
            table.getTableHeader().setReorderingAllowed(false);

            return table;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultReserv != null) resultReserv.close();
                if(reservStatement != null) reservStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }


    public static JTable getReservGuestsTable(Reservations reservations){
        //Creates table about guests of the specific old or new reservation.
        Connection connection = null;
        PreparedStatement guestReservStatement = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        String[] columnNames = {"NAME"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        ArrayList<Object[]> data = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get every guest that have that reservation.
            guestReservStatement = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
            guestReservStatement.setInt(1,reservations.getId());
            resultGuestReserv = guestReservStatement.executeQuery();
            while (resultGuestReserv.next()) {
                int guestId = resultGuestReserv.getInt("guest_id");
                //Get guest infos.
                guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                guestState.setInt(1, guestId);
                resultGuest = guestState.executeQuery();
                if (resultGuest.next()) {
                    String first_name = resultGuest.getString("first_name");
                    String last_name = resultGuest.getString("last_name");
                    String name = first_name + " " + last_name;
                    data.add(new Object[]{name});
                }
            }

            for (Object[] row : data) {
                model.addRow(row);
            }

            JTable table = new JTable(model);

            TableColumn column = table.getColumnModel().getColumn(0);
            column.setPreferredWidth(200);
            column.setMinWidth(200);
            column.setMaxWidth(200);


            //Prevents changing table data manually.
            table.setDefaultEditor(Object.class,null);

            //Prevent changing columns manually.
            table.getTableHeader().setReorderingAllowed(false);

            return table;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReserv != null) resultGuestReserv.close();
                if(guestReservStatement != null) guestReservStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Reservations getReservations(Personnel personnel, int reservId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
            preparedStatement.setInt(1,reservId);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int hotel_id = resultSet.getInt("hotel_id");
                Timestamp expected_check_in = resultSet.getTimestamp("expected_check_in");
                Timestamp expected_check_out = resultSet.getTimestamp("expected_check_out");
                int reservation_price = resultSet.getInt("reservation_price");
                int extra_debt = resultSet.getInt("extra_debt");
                int agency_id = resultSet.getInt("agency_id");
                int room_type_id = resultSet.getInt("room_type_id");
                int number_of_people = resultSet.getInt("number_of_people");
                String notes = resultSet.getString("notes");
                boolean old_reservation = resultSet.getBoolean("old_reservation");
                boolean current_reservation = resultSet.getBoolean("current_reservation");
                boolean future_reservation = resultSet.getBoolean("future_reservation");
                String room_number = resultSet.getString("room_number");
                String extras = resultSet.getString("extras");
                boolean paid = resultSet.getBoolean("paid");
                return new Reservations(reservId,hotel_id,agency_id,room_type_id,reservation_price,extra_debt,room_number,number_of_people,
                        expected_check_in,expected_check_out,notes,old_reservation,current_reservation,future_reservation,extras,paid);
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Guests getGuest(Reservations reservations, int row){

        //Get guest using reservation data and row data. It has same logic with getGuest(Room room, int row)
        /*
        row parameter obtained from table. We search guest by row instead of it's name, surname reservation etc.
        because while creating new reservation, resarvationist does not have to declare every information about
        every guest. This is how works in real hotels. So, these limited information may be cause of problems.
        For example, we have 2 guests have same name and surname in the same room. Since they book for same room,
        they have same reservation. Thus, every information about them are same. What is the difference?
        Difference are their guest id, but it is not placed in the table. But, since guest names placed table
        in ascending order, we can search the guest by his row number in the table.
         */

        Connection connection = null;
        PreparedStatement guestReservStatement = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get guests that has that reservation.
            guestReservStatement = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
            guestReservStatement.setInt(1,reservations.getId());
            int guestNumber = 0;
            resultGuestReserv = guestReservStatement.executeQuery();
            //Select guest by row number in table.
            while (resultGuestReserv.next() && guestNumber <= row){
                if(guestNumber == row){
                    int guestId = resultGuestReserv.getInt("guest_id");
                    //Get guest infos.
                    guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                    guestState.setInt(1,guestId);
                    resultGuest = guestState.executeQuery();
                    if(resultGuest.next()){
                        String first_name = resultGuest.getString("first_name");
                        String last_name = resultGuest.getString("last_name");
                        String country = resultGuest.getString("country");
                        int id_number = resultGuest.getInt("id_number");
                        Date birth_date = resultGuest.getDate("birth_date");
                        Timestamp check_in_datetime = resultGuest.getTimestamp("check_in_datetime");
                        Timestamp check_out_datetime = resultGuest.getTimestamp("check_out_datetime");
                        boolean kbs_in = resultGuest.getBoolean("kbs_in");
                        boolean kbs_out = resultGuest.getBoolean("kbs_out");
                        boolean checked_in = resultGuest.getBoolean("checked_in");
                        boolean checked_out = resultGuest.getBoolean("checked_out");
                        return new Guests(guestId,reservations.getHotel_id(),reservations.getId(),first_name,last_name,country,
                                id_number,birth_date,check_in_datetime,check_out_datetime,
                                kbs_in,kbs_out,checked_in,checked_out);
                    }
                }
                guestNumber ++;
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReserv != null) resultGuestReserv.close();
                if(guestReservStatement != null) guestReservStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<String> getRoomsWithTypes(Personnel personnel){
        //Get room numbers with its room types.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<String> data = new ArrayList<>();
            preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ?");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int roomTypeId = resultSet.getInt("room_type_id");
                String roomNumber = resultSet.getString("room_number");
                String roomType = getRoomTypeName(connection,roomTypeId);
                data.add(roomNumber + " " + roomType);
            }
            return data;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean applyReservation(Reservations reservations, Timestamp inDate,
                                        Timestamp outDate, String typeText, String noText, int price,
                                        boolean paid, boolean addHotel, String agency, String notes){

        //Update changes in reservation.
        //This method using in reservDialog.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            if (addHotel){
                boolean isOk = controlRoom(connection,reservations,noText);
                if(!isOk){
                    return false;
                }
            }
            int typeId = getRoomTypeId(connection,typeText);
            int agencyId = getAgencyId(connection,agency);
            preparedStatement = connection.prepareStatement("UPDATE reservations SET expected_check_in = ? ," +
                    " expected_check_out = ? , reservation_price = ? , agency_id = ? , room_type_id = ? , " +
                    "notes = ? , future_reservation = ? , current_reservation = ? , room_number = ? , paid = ?" +
                    "  WHERE id = ?");
            preparedStatement.setTimestamp(1,inDate);
            preparedStatement.setTimestamp(2,outDate);
            preparedStatement.setInt(3,price);
            preparedStatement.setInt(4,agencyId);
            preparedStatement.setInt(5,typeId);
            preparedStatement.setString(6,notes);
            preparedStatement.setBoolean(7,!addHotel);
            preparedStatement.setBoolean(8,addHotel);
            preparedStatement.setString(9,noText);
            preparedStatement.setBoolean(10,paid);
            preparedStatement.setInt(11,reservations.getId());
            int rowsAffected = preparedStatement.executeUpdate();
            return true;

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<Integer> getFutureReservs(Personnel personnel){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<Integer> data = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM reservations WHERE hotel_id = ? AND future_reservation = 1");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                data.add(id);
            }
            return data;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void deleteReservation(int reservId){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("DELETE FROM reservations WHERE id = ?");
            preparedStatement.setInt(1,reservId);
            int rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static boolean createVirt(Personnel personnel, String roomNumber, String typeName){
        //Create virtual room.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            boolean isRoomExists = isRoomExists(connection,personnel,roomNumber);
            if(!isRoomExists){
                int roomTypeId = getRoomTypeId(connection,typeName);
                preparedStatement = connection.prepareStatement("INSERT INTO rooms (hotel_id,room_number,room_type_id,virtual_room) VALUES (?,?,?,1)");
                preparedStatement.setInt(1,personnel.getHotel_id());
                preparedStatement.setString(2,roomNumber);
                preparedStatement.setInt(3,roomTypeId);
                int rowsAffected = preparedStatement.executeUpdate();
                return true;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean deleteVirt(Personnel personnel, String roomNumber){
        Connection connection = null;
        PreparedStatement roomStatement = null;
        PreparedStatement reservState = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            reservState = connection.prepareStatement("UPDATE reservations SET room_number = null WHERE hotel_id = ? AND room_number = ?");
            reservState.setInt(1,personnel.getHotel_id());
            reservState.setString(2,roomNumber);
            int affectedRows = reservState.executeUpdate();

            roomStatement = connection.prepareStatement("DELETE FROM rooms WHERE hotel_id = ? AND room_number = ?");
            roomStatement.setInt(1,personnel.getHotel_id());
            roomStatement.setString(2,roomNumber);
            int rowsAffected = roomStatement.executeUpdate();
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(reservState != null) reservState.close();
                if(roomStatement != null) roomStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    private static boolean isRoomExists(Connection connection, Personnel personnel, String roomNumber){
        //Controls that whether the room number exists in the hotel that the personnel works there.
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ? AND room_number = ?");
            preparedStatement.setInt(1,personnel.getHotel_id());
            preparedStatement.setString(2,roomNumber);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean roomChange(Personnel personnel, String fromRoomNo , String toRoomNo){
        /*
        First checks that entered rooms exists. If test is completed successfully then controls whether target room
        is empty. If target room is  empty then room change realized and present rooms features changed (Now present room is empty
        and needed to clean, so it is not available. Also target room is not empty anymore and not available because it is not empty).
        If target room is not empty, interchange reservations that stay in present room and target room. Also change
        present room and target room features (Target room and present room were not empty anyway. Now we should do
         their availability false for prevent any problems. Not available means that the room have some problem
         or room is in maintenance or room needs to check).
         */

        Connection connection = null;
        PreparedStatement deleteToStatement = null;
        PreparedStatement deleteFromStatement = null;
        PreparedStatement insertFromReservStatement = null;
        PreparedStatement insertToReservStatement = null;
        PreparedStatement updateFromReserv = null;
        PreparedStatement updateToReserv = null;
        PreparedStatement updateToRoom = null;
        PreparedStatement updateFromRoom = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            if(isRoomExists(connection,personnel,fromRoomNo) && isRoomExists(connection,personnel,toRoomNo)){
                Room fromRoom = getRoom(personnel,fromRoomNo);
                Reservations fromReserv = getReservations(fromRoom);
                Room toRoom = getRoom(personnel,toRoomNo);
                Reservations toReserv = getReservations(toRoom);
                if(fromReserv == null){
                    return false;
                }
                if(toReserv != null) {
                    deleteToStatement = connection.prepareStatement("DELETE FROM room_reservations WHERE room_id = ? AND reservation_id = ?");
                    deleteToStatement.setInt(1,toRoom.getId());
                    deleteToStatement.setInt(2,toReserv.getId());
                    int rowsDeleteTo = deleteToStatement.executeUpdate();
                }
                deleteFromStatement = connection.prepareStatement("DELETE FROM room_reservations WHERE room_id = ? AND reservation_id = ?");
                deleteFromStatement.setInt(1,fromRoom.getId());
                deleteFromStatement.setInt(2,fromReserv.getId());
                int rowsDeleteFrom = deleteFromStatement.executeUpdate();

                insertFromReservStatement = connection.prepareStatement("INSERT INTO room_reservations (room_id,reservation_id) VALUES (?,?)");
                insertFromReservStatement.setInt(1,toRoom.getId());
                insertFromReservStatement.setInt(2,fromReserv.getId());
                int rowsInsertFrom = insertFromReservStatement.executeUpdate();

                if(toReserv != null) {
                    insertToReservStatement = connection.prepareStatement("INSERT INTO room_reservations (room_id,reservation_id) VALUES (?,?)");
                    insertToReservStatement.setInt(1,fromRoom.getId());
                    insertToReservStatement.setInt(2,toReserv.getId());
                    int rowsInsterTo = insertToReservStatement.executeUpdate();

                    updateToReserv = connection.prepareStatement("UPDATE reservations SET room_number = ? WHERE id = ?");
                    updateToReserv.setString(1,fromRoom.getRoom_number());
                    updateToReserv.setInt(2,toReserv.getId());
                    int rowsUpdateTo = updateToReserv.executeUpdate();
                }

                updateFromReserv = connection.prepareStatement("UPDATE reservations SET room_number = ? WHERE id = ?");
                updateFromReserv.setString(1,toRoom.getRoom_number());
                updateFromReserv.setInt(2,fromReserv.getId());
                int rowsUpdateFrom = updateFromReserv.executeUpdate();

                if(toReserv != null){
                    updateFromRoom = connection.prepareStatement("UPDATE rooms SET is_empty = 0 , available = 0 WHERE id = ? OR id = ?");
                    updateFromRoom.setInt(1,fromRoom.getId());
                    updateFromRoom.setInt(2,toRoom.getId());
                    int rowUpdateFromRoom = updateFromRoom.executeUpdate();
                }
                else{
                    updateFromRoom = connection.prepareStatement("UPDATE rooms SET is_empty = 1 , available = 0 , clean = 0 WHERE id = ?");
                    updateFromRoom.setInt(1,fromRoom.getId());
                    int rowUpdateFromRoom = updateFromRoom.executeUpdate();

                    updateToRoom = connection.prepareStatement("UPDATE rooms SET is_empty = 0 , available = 0 WHERE id = ?");
                    updateToRoom.setInt(1,toRoom.getId());
                    int rowUpdateToRoom = updateToRoom.executeUpdate();
                }
                
                return true;
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(updateToRoom != null) updateToRoom.close();
                if(updateFromRoom != null) updateFromRoom.close();
                if(updateFromReserv != null) updateFromReserv.close();
                if(updateToReserv != null) updateToReserv.close();
                if(insertToReservStatement != null) insertToReservStatement.close();
                if(insertFromReservStatement != null) insertFromReservStatement.close();
                if(deleteFromStatement != null) deleteFromStatement.close();
                if(deleteToStatement != null) deleteToStatement.close();
                if(connection != null) connection.close();

            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean deleteGuest(Guests guests, Reservations reservations){
        Connection connection = null;
        PreparedStatement guestReservState = null;
        PreparedStatement guestState = null;
        PreparedStatement reservState = null;
        ResultSet resultReserv = null;
        PreparedStatement deleteState = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            guestReservState = connection.prepareStatement("DELETE FROM guest_reservations WHERE guest_id = ? AND reservation_id = ?");
            guestReservState.setInt(1,guests.getId());
            guestReservState.setInt(2,reservations.getId());
            int rowsAffected = guestReservState.executeUpdate();

            guestState = connection.prepareStatement("DELETE FROM guests WHERE id = ?");
            guestState.setInt(1,guests.getId());
            int affectedRows = guestState.executeUpdate();

            reservState = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
            reservState.setInt(1,reservations.getId());
            resultReserv = reservState.executeQuery();
            if(resultReserv.next()){
                int numberOfPeople = resultReserv.getInt("number_of_people");

                deleteState = connection.prepareStatement("UPDATE reservations SET number_of_people = ? WHERE id = ?");
                deleteState.setInt(1,numberOfPeople-1);
                deleteState.setInt(2,reservations.getId());
                int rowsReservState = deleteState.executeUpdate();

                return true;
            }


        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(deleteState != null) deleteState.close();
                if(guestState != null) guestState.close();
                if(guestReservState != null) guestReservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<String> getVirtRooms(Personnel personnel){
        //Get virtual eempty room numbers.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<String> data = new ArrayList<>();
            preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ? AND virtual_room = 1 AND is_empty = 1");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String roomNumber = resultSet.getString("room_number");
                data.add(roomNumber);
            }
            return data;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean readKBS(Personnel personnel){
        /*
        KBS is "KIMLIK BILDIRIM SISTEMI" in Turkish. It is an API that provides to
        share guest data (check in data, check out data) with Turkish Police. Unfortunately,
        I did not able to use that API, but I wanted to use kbs feature and simulate it in my program
        just because make my program more realistic.
        So, this method first, process data in rows then, delete rows. But does not send data to POLICE.
        It is just a simulation.
         */

        Connection connection = null;
        PreparedStatement kbsState = null;
        ResultSet resultKbs = null;
        PreparedStatement guestInState = null;
        PreparedStatement guestOutState = null;
        PreparedStatement deleteState = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            kbsState = connection.prepareStatement("SELECT * FROM kbs WHERE hotel_id = ?");
            kbsState.setInt(1,personnel.getHotel_id());
            resultKbs = kbsState.executeQuery();
            while(resultKbs.next()){
                int id = resultKbs.getInt("id");
                int guest_id = resultKbs.getInt("guest_id");
                boolean kbs_in = resultKbs.getBoolean("kbs_in");
                boolean kbs_out = resultKbs.getBoolean("kbs_out");


                if (kbs_in){
                    //Timestamp in_time = resultKbs.getTimestamp("in_time");
                    guestInState = connection.prepareStatement("UPDATE guests SET kbs_in = 1 WHERE id = ?");
                    guestInState.setInt(1,guest_id);
                    int rowsAffected = guestInState.executeUpdate();

                }
                if (kbs_out){
                    //Timestamp out_time = resultKbs.getTimestamp("out_time");
                    guestOutState = connection.prepareStatement("UPDATE guests SET kbs_out = 1 WHERE id = ?");
                    guestOutState.setInt(1,guest_id);
                    int affectedRows = guestOutState.executeUpdate();
                }

                deleteState = connection.prepareStatement("DELETE FROM kbs WHERE id = ?");
                deleteState.setInt(1,id);
                int rowsDelete = deleteState.executeUpdate();

            }
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(guestOutState != null) guestOutState.close();
                if(guestInState != null) guestInState.close();
                if(resultKbs != null) resultKbs.close();
                if(kbsState != null) kbsState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }


    public static ArrayList<String> getExtras(Personnel personnel){
        //Get extras of hotel.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> extrasArray = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM extras WHERE hotel_id = ?");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String data = "";
                String product = resultSet.getString("product");
                BigDecimal price = resultSet.getBigDecimal("price");
                data += product + " (" +  String.valueOf(price) + " TL)";
                extrasArray.add(data);
            }
            return extrasArray;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean addExtraToRoom(Personnel personnel, String roomNo, String productName, int piece, BigDecimal price){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PreparedStatement updateState = null;
        String extrasDefault = "";
        int extraDebtDefault = 0;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            String codedVersion = extraCoder(connection,productName,piece,personnel);
            Room room = getRoom(personnel,roomNo);
            if(!room.isIsempty()) {
                Reservations reservations = getReservations(room);
                preparedStatement = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
                preparedStatement.setInt(1, reservations.getId());
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int extraDebt = resultSet.getInt("extra_debt");
                    String extras = resultSet.getString("extras");
                    if(extras != null){
                        extrasDefault = extras;
                    }

                    updateState = connection.prepareStatement("UPDATE reservations SET extra_debt = ? , extras = ? WHERE id = ?");
                    updateState.setInt(1, (new BigDecimal(extraDebt).add(price.multiply(new BigDecimal(piece)))).intValue());
                    updateState.setString(2, extrasDefault + codedVersion);
                    updateState.setInt(3,reservations.getId());
                    int rowsAffected = updateState.executeUpdate();
                    return true;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(updateState != null) updateState.close();
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static ArrayList<ArrayList<String>> getDatePrices(Personnel personnel){
        //Get hotel room prices with its dates.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<ArrayList<String>> allPrices = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM room_type_dates WHERE hotel_id = ? ORDER BY start_date ASC");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ArrayList<String> eachPrice = new ArrayList<>();
                eachPrice.add(getRoomTypeName(resultSet.getInt("room_type_id")));
                eachPrice.add(String.valueOf(resultSet.getInt("price")));
                eachPrice.add(dateFormat.format(resultSet.getDate("start_date")));
                eachPrice.add(dateFormat.format(resultSet.getDate("end_date")));
                allPrices.add(eachPrice);
            }
            return allPrices;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static JTable createDatesTable(Personnel personnel){
        //Create table of hotel room prices with its dates
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String[] columnNames = {"TYPE", "PRICE", "START", "END"};
        int[] columnWidths = {100,100,100,100};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<ArrayList<String>> datesDate = getDatePrices(personnel);

            if(datesDate != null){
                for(ArrayList<String> eachDateDate : datesDate){
                    model.addRow(eachDateDate.toArray(new Object[0]));
                }
                JTable table = new JTable(model);

                for (int i = 0; i < columnWidths.length; i++) {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    column.setPreferredWidth(columnWidths[i]);
                    column.setMinWidth(columnWidths[i]);
                    column.setMaxWidth(columnWidths[i]);
                }

                //Prevents changing table data manually.
                table.setDefaultEditor(Object.class,null);

                //Prevent changing columns manually.
                table.getTableHeader().setReorderingAllowed(false);

                return table;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static JTable createProductsTable(Personnel personnel){
        //Create extra product.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String[] columnNames = {"NAME", "PRICE"};
        int[] columnWidths = {200,100};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<ArrayList<String>> products = getProductPrices(connection,personnel);

            if(products != null){
                for(ArrayList<String> eachProduct : products){
                    model.addRow(eachProduct.toArray(new Object[0]));
                }
                JTable table = new JTable(model);

                for (int i = 0; i < columnWidths.length; i++) {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    column.setPreferredWidth(columnWidths[i]);
                    column.setMinWidth(columnWidths[i]);
                    column.setMaxWidth(columnWidths[i]);
                }

                //Prevents changing table data manually.
                table.setDefaultEditor(Object.class,null);

                //Prevent changing columns manually.
                table.getTableHeader().setReorderingAllowed(false);

                return table;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static ArrayList<ArrayList<String>> getProductPrices(Connection connection,Personnel personnel){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<ArrayList<String>> allProducts = new ArrayList<>();

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM extras WHERE hotel_id = ?");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                ArrayList<String> eachProduct = new ArrayList<>();
                eachProduct.add(resultSet.getString("product"));
                eachProduct.add(resultSet.getBigDecimal("price").toString());
                allProducts.add(eachProduct);
            }
            return allProducts;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static String extraCoder(Connection connection, String productName, int piece, Personnel personnel){
        //I preferred to coded extras because of reduce taking space.
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM extras WHERE hotel_id = ? AND product = ?");
            preparedStatement.setInt(1,personnel.getHotel_id());
            preparedStatement.setString(2,productName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int id = resultSet.getInt("id");
                return id + "." + piece + ",";
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static String extraDeCoder(Reservations reservations){
        Connection connection = null;
        PreparedStatement reservState = null;
        ResultSet resultReserv = null;
        PreparedStatement extrasState = null;
        ResultSet resultExtras = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            reservState = connection.prepareStatement("SELECT * FROM reservations WHERE id = ?");
            reservState.setInt(1,reservations.getId());
            resultReserv = reservState.executeQuery();
            if (resultReserv.next()){
                String extras = resultReserv.getString("extras");
                if(extras != null){
                    String result = "";
                    String[] extrasArray = extras.split(",");
                    for(String extra : extrasArray){
                        int productId = Integer.parseInt(extra.split("\\.")[0]);
                        int piece = Integer.parseInt(extra.split("\\.")[1]);
                        extrasState = connection.prepareStatement("SELECT * FROM extras WHERE id = ?");
                        extrasState.setInt(1,productId);
                        resultExtras = extrasState.executeQuery();
                        if(resultExtras.next()){
                            String productName = resultExtras.getString("product");
                            BigDecimal price = resultExtras.getBigDecimal("price");
                            result += productName + " (" + price + " TL) x " + piece + "\n";
                        }
                    }
                    return result;
                }
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultExtras != null) resultExtras.close();
                if(extrasState != null) extrasState.close();
                if(resultReserv != null) resultReserv.close();
                if(reservState != null) reservState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean addExtra(Personnel personnel, String product, int price){
        //Add or change extra product.
        Connection connection = null;
        PreparedStatement controlStatement = null;
        ResultSet resultControl = null;
        PreparedStatement addState = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            controlStatement = connection.prepareStatement("SELECT * FROM extras WHERE product = ? AND hotel_id = ?");
            controlStatement.setString(1,product);
            controlStatement.setInt(2,personnel.getHotel_id());
            resultControl = controlStatement.executeQuery();
            //If that extra product have existed
            if(resultControl.next()){
                int id = resultControl.getInt("id");
                addState = connection.prepareStatement("UPDATE extras SET price = ? WHERE id = ?");
                addState.setBigDecimal(1,new BigDecimal(price));
                addState.setInt(2,id);
                int rowsAffected = addState.executeUpdate();
            }
            //If that extra product have not existed yet.
            else{
                addState = connection.prepareStatement("INSERT INTO extras (hotel_id,product,price) VALUES (?,?,?)");
                addState.setInt(1,personnel.getHotel_id());
                addState.setString(2,product);
                addState.setBigDecimal(3, new BigDecimal(price));
                int affectedRows = addState.executeUpdate();
            }
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(addState != null) addState.close();
                if(resultControl != null) resultControl.close();
                if(controlStatement != null) controlStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<String> getTitles(){
        //Get job titles.
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> titles = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM job_titles");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                titles.add(resultSet.getString("title_name"));
            }
            return titles;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean hire(Personnel personnel, String firstName, String lastName, String username, String password, String title){
        //This method works for GM (General Manager).
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            if(controlUsername(connection,username)) {
                int titleId = getTitleId(connection, title);
                preparedStatement = connection.prepareStatement("INSERT INTO personnels (first_name,last_name,password,start_date,job_title_id,hotel_id,username) VALUES (?,?,?,NOW(),?,?,?)");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, PasswordHasher.hashPassword(password));
                preparedStatement.setInt(4, titleId);
                preparedStatement.setInt(5, personnel.getHotel_id());
                preparedStatement.setString(6, username);
                int rowsAffected = preparedStatement.executeUpdate();
                return true;
            }
        }
        catch (SQLException e){
            return false;
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static boolean hire(String hotelName, String firstName, String lastName, String username, String password, String title){
        //This method works for admin.
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            if(controlUsername(connection,username)) {
                int titleId = getTitleId(connection, title);
                preparedStatement = connection.prepareStatement("INSERT INTO personnels (first_name,last_name,password,start_date,job_title_id,hotel_id,username) VALUES (?,?,?,NOW(),?,?,?)");
                preparedStatement.setString(1, firstName);
                preparedStatement.setString(2, lastName);
                preparedStatement.setString(3, PasswordHasher.hashPassword(password));
                preparedStatement.setInt(4, titleId);
                preparedStatement.setInt(5, getHotelId(hotelName));
                preparedStatement.setString(6, username);
                int rowsAffected = preparedStatement.executeUpdate();
                return true;
            }
        }
        catch (SQLException e){
            return false;
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static ArrayList<ArrayList<String>> getPersonnels(Personnel personnel){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<ArrayList<String>> personnels = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Prevent getting admin information.
            preparedStatement = connection.prepareStatement("SELECT * FROM personnels WHERE hotel_id = ? AND NOT job_title_id = 7");
            preparedStatement.setInt(1,personnel.getHotel_id());
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                int id = resultSet.getInt("id");
                String username = resultSet.getString("username");
                String job_title = getTitleName(connection,resultSet.getInt("job_title_id"));
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                Date start_date = resultSet.getDate("start_date");
                ArrayList<String> eachPersonnel = new ArrayList<>();
                eachPersonnel.add(String.valueOf(id));
                eachPersonnel.add(username);
                eachPersonnel.add(job_title);
                eachPersonnel.add(first_name);
                eachPersonnel.add(last_name);
                eachPersonnel.add(dateFormat.format(start_date));
                personnels.add(eachPersonnel);
            }
            return personnels;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean fire(Personnel personnel, String username){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("DELETE FROM personnels WHERE username = ?");
            preparedStatement.setString(1,username);
            int rowsAffected = preparedStatement.executeUpdate();

            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static JTable createPersonnelsTable(Personnel personnel){
        Connection connection = null;
        String[] columnNames = {"ID", "USERNAME", "TITLE", "NAME", "SURNAME", "START"};
        int[] columnWidths = {70,120,120,120,120,70};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            ArrayList<ArrayList<String>> personnelsData = getPersonnels(personnel);
            if (personnelsData != null){
                for(ArrayList<String> eachPersonnel : personnelsData){
                    model.addRow(eachPersonnel.toArray(new Object[0]));
                }
                JTable table = new JTable(model);

                for (int i = 0; i < columnWidths.length; i++) {
                    TableColumn column = table.getColumnModel().getColumn(i);
                    column.setPreferredWidth(columnWidths[i]);
                    column.setMinWidth(columnWidths[i]);
                    column.setMaxWidth(columnWidths[i]);
                }

                //Prevents changing table data manually.
                table.setDefaultEditor(Object.class,null);

                //Prevent changing columns manually.
                table.getTableHeader().setReorderingAllowed(false);

                return table;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }

        return null;
    }

    public static boolean changeDates(Personnel personnel, Date startDate, Date endDate, String roomType, int price){
        //Change hotel room prices or its dates. If new date overlap with another date that existed before, then shift old date automatically.
        Connection connection = null;
        PreparedStatement controlState = null;
        ResultSet resultControl = null;
        PreparedStatement updateState = null;
        PreparedStatement extraUpdateState = null;
        PreparedStatement createState = null;
        long startDateLong = startDate.getTime();
        java.sql.Date startDateSql = new java.sql.Date(startDateLong);
        long endDateLong = endDate.getTime();
        java.sql.Date endDateSql = new java.sql.Date(endDateLong);

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            int roomTypeId = getRoomTypeId(connection,roomType);
            controlState = connection.prepareStatement("SELECT * FROM room_type_dates WHERE hotel_id = ? AND room_type_id = ? AND end_date >= ? AND start_date <=  ? ORDER BY start_date ASC");
            controlState.setInt(1,personnel.getHotel_id());
            controlState.setInt(2,roomTypeId);
            controlState.setDate(3,startDateSql);
            controlState.setDate(4,endDateSql);
            resultControl = controlState.executeQuery();

            boolean shouldCreate = true;
            int eraPrice = 0;

            while (resultControl.next()){
                int id = resultControl.getInt("id");
                Date eraStart = resultControl.getDate("start_date");
                Date eraEnd = resultControl.getDate("end_date");
                eraPrice = resultControl.getInt("price");

                if(eraStart.equals(startDate) && eraEnd.equals(endDate)){
                    if(price != 0){
                        updateState = connection.prepareStatement("UPDATE room_type_dates SET price = ? WHERE id = ?");
                        updateState.setInt(1,price);
                        updateState.setInt(2,id);
                    }
                    else{
                        updateState = connection.prepareStatement("DELETE FROM room_type_dates WHERE id = ?");
                        updateState.setInt(1,id);
                    }
                    shouldCreate = false;
                }

                //If new era includes all the era, then delete the era.
                else if(startDate.compareTo(eraStart) <= 0  && eraEnd.compareTo(endDate) <= 0){
                    updateState = connection.prepareStatement("DELETE FROM room_type_dates WHERE id = ?");
                    updateState.setInt(1,id);
                }

                //If new era starts before the era and ends before the era.
                else if(startDate.compareTo(eraStart) <= 0 && eraEnd.compareTo(endDate) >= 0 && endDate.compareTo(eraStart) >= 0){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(endDateSql);

                    calendar.add(Calendar.DAY_OF_MONTH, +1);

                    java.sql.Date newStart = new java.sql.Date(calendar.getTimeInMillis());

                    updateState = connection.prepareStatement("UPDATE room_type_dates SET start_date = ? WHERE id = ?");
                    updateState.setDate(1,newStart);
                    updateState.setInt(2,id);
                }

                //If new era starts after the era and ends after the era.
                else if(startDate.compareTo(eraStart) >= 0 && eraEnd.compareTo(endDate) <= 0 && startDate.compareTo(eraEnd) <= 0){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startDateSql);

                    calendar.add(Calendar.DAY_OF_MONTH, -1);

                    java.sql.Date newEnd = new java.sql.Date(calendar.getTimeInMillis());

                    updateState = connection.prepareStatement("UPDATE room_type_dates SET end_date = ? WHERE id = ?");
                    updateState.setDate(1,newEnd);
                    updateState.setInt(2,id);
                }

                //If new era starts after the era but ends before the era.
                else if(startDate.compareTo(eraStart) >= 0 && eraEnd.compareTo(endDate) >= 0){
                    Calendar calendarNewStart = Calendar.getInstance();
                    calendarNewStart.setTime(endDateSql);

                    calendarNewStart.add(Calendar.DAY_OF_MONTH, +1);

                    java.sql.Date newStart = new java.sql.Date(calendarNewStart.getTimeInMillis());

                    long eraEndLong = eraEnd.getTime();
                    java.sql.Date eraEndSql = new java.sql.Date(eraEndLong);

                    extraUpdateState = connection.prepareStatement("INSERT INTO room_type_dates (hotel_id,room_type_id,price,start_date,end_date) VALUES (?,?,?,?,?)");
                    extraUpdateState.setInt(1,personnel.getHotel_id());
                    extraUpdateState.setInt(2,roomTypeId);
                    extraUpdateState.setInt(3,resultControl.getInt("price"));
                    extraUpdateState.setDate(4,newStart);
                    extraUpdateState.setDate(5,eraEndSql);
                    int affectedRows = extraUpdateState.executeUpdate();

                    Calendar calendarNewEnd = Calendar.getInstance();
                    calendarNewEnd.setTime(startDateSql);

                    calendarNewEnd.add(Calendar.DAY_OF_MONTH, -1);

                    java.sql.Date newEnd = new java.sql.Date(calendarNewEnd.getTimeInMillis());

                    updateState = connection.prepareStatement("UPDATE room_type_dates SET end_date = ? WHERE id = ?");
                    updateState.setDate(1,newEnd);
                    updateState.setInt(2,id);

                }
                int rowsAffected = updateState.executeUpdate();

            }
            if(shouldCreate){
                createState = connection.prepareStatement("INSERT INTO room_type_dates (hotel_id,room_type_id,price,start_date,end_date) VALUES (?,?,?,?,?)");
                createState.setInt(1,personnel.getHotel_id());
                createState.setInt(2,roomTypeId);
                createState.setInt(3,price);
                createState.setDate(4,startDateSql);
                createState.setDate(5,endDateSql);
                int rowsCreate = createState.executeUpdate();
            }

            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(createState != null) createState.close();
                if(extraUpdateState != null) extraUpdateState.close();
                if(updateState != null) updateState.close();
                if(resultControl != null) resultControl.close();
                if(controlState != null) controlState.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }

        return false;
    }

    public static ArrayList<String> getHotelNames(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> hotelsList = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM hotels WHERE id != 3");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                hotelsList.add(resultSet.getString("name"));
            }
            return hotelsList;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static boolean createHotel(Personnel personnel, String hotelName){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("INSERT INTO hotels (name) VALUES (?)");
            preparedStatement.setString(1,hotelName);
            int rowsAffected = preparedStatement.executeUpdate();
            return true;
        }
        catch (SQLException e){
            return false;
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static boolean createRooms(String hotelName, String typeName,
                                       String fromRoom, String toRoom){
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            int hotelId = getHotelId(hotelName);
            int typeId = getRoomTypeId(connection,typeName);
            ArrayList<String> rooms = getIntendedRooms(fromRoom,toRoom);
            if (rooms != null){
                for (String room : rooms){
                    createRoom(connection,hotelId,typeId,room);
                }
            }
            addHotelRoomTypes(connection,hotelId,typeId);
            return true;
        }
        catch (SQLException e){
            return false;
        }
        finally {
            try {
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    private static void addHotelRoomTypes(Connection connection, int hotelId, int roomTypeId){
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("INSERT INTO hotel_room_types (hotel_id,room_type_id) VALUES (?,?)");
            preparedStatement.setInt(1,hotelId);
            preparedStatement.setInt(2,roomTypeId);
            int rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            //DO NOTHING
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    private static void createRoom(Connection connection, int hotelId, int typeId,
                                   String roomNumber){

        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement("INSERT INTO rooms (hotel_id,room_number,room_type_id) VALUES (?,?,?)");
            preparedStatement.setInt(1,hotelId);
            preparedStatement.setString(2,roomNumber);
            preparedStatement.setInt(3,typeId);
            int rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e){
            //DO NOTHING
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }

    public static ArrayList<String> getAllTypes(){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        ArrayList<String> types = new ArrayList<>();

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM room_types");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                types.add(resultSet.getString("type_name"));
            }
            return types;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }


    public static int getHotelId(String hotelName){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            preparedStatement = connection.prepareStatement("SELECT * FROM hotels where name = ?");
            preparedStatement.setString(1,hotelName);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return 0;
    }

    public static boolean saveNewPassword(Personnel personnel, String currentPassword, String newPassword){
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);

            if (checkCurrentPassword(connection, personnel, currentPassword)) {
                preparedStatement = connection.prepareStatement("UPDATE personnels SET password = ? WHERE id = ?");
                preparedStatement.setString(1, PasswordHasher.hashPassword(newPassword));
                preparedStatement.setInt(2, personnel.getId());
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private static boolean checkCurrentPassword(Connection connection, Personnel personnel, String currentPassword) {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT password FROM personnels WHERE id = ?");
            preparedStatement.setInt(1, personnel.getId());
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");
                String hashedCurrentPassword = PasswordHasher.hashPassword(currentPassword);

                return hashedCurrentPassword.equals(storedHashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    private static ArrayList<String> getIntendedRooms(String fromRoom, String toRoom){
        //Create room numbers according to entered room numbers.
        /*
        fromRoom : a , endRoom : c => rooms = {a,b,c},
        fromRoom : 1 , endRoom : 3 => rooms = {1,2,3},
        fromRoom : a1 , endRoom : a1 => rooms = {a1,a2,a3},
        fromRoom : a1 , endRoom : c1 => rooms = {a1,b1,c1},
        fromRoom : a1 , endRoom : c3 => rooms = {a1,a2,a3,b1,b2,b3,c1,c2,c3},
        fromRoom : a3 , endRoom : c1 => rooms = {a3,a2,a1,b3,b2,b1,c3,c2,c1},
        fromRoom : c1 , endRoom : a3 => rooms = {c1,c2,c3,b1,b2,b3,a1,a2,a3},
        fromRoom : c3 , endRoom : a1 => rooms = {c3,c2,c1,b3,b2,b1,a3,a2,a1},
         */
        try {
            ArrayList<String> rooms = new ArrayList<>();

            if(Character.isLetter(fromRoom.charAt(0)) && Character.isLetter(toRoom.charAt(0))){
                char charFrom = Character.toLowerCase(fromRoom.charAt(0));
                char charTo = Character.toLowerCase(toRoom.charAt(0));
                if (fromRoom.length() > 1 && toRoom.length() > 1){
                    int intFrom = Integer.parseInt(fromRoom.substring(1));
                    int intTo = Integer.parseInt(toRoom.substring(1));

                    for (char ch = charFrom ; ch <= charTo; ch++){
                        for (int i = intFrom; i <= intTo; i++){
                            rooms.add(ch + String.valueOf(i));
                        }
                        for (int i = intFrom; i >= intTo; i--){
                            rooms.add(ch + String.valueOf(i));
                        }
                    }

                    for (char ch = charFrom ; ch >= charTo; ch--){
                        for (int i = intFrom; i <= intTo; i++){
                            rooms.add(ch + String.valueOf(i));
                        }
                        for (int i = intFrom; i >= intTo; i--){
                            rooms.add(ch + String.valueOf(i));
                        }
                    }
                }
                else{
                    for (char ch = charFrom ; ch <= charTo; ch++){
                        rooms.add(String.valueOf(ch));
                    }

                    for (char ch = charFrom ; ch >= charTo; ch--){
                        rooms.add(String.valueOf(ch));
                    }
                }

            }
            else{
                int intFrom = Integer.parseInt(fromRoom);
                int intTo = Integer.parseInt(toRoom);

                for (int i = intFrom; i <= intTo; i++){
                    rooms.add(String.valueOf(i));
                }
                for (int i = intFrom; i >= intTo; i--){
                    rooms.add(String.valueOf(i));
                }
            }

            return rooms;
        }
        catch (NumberFormatException e){
            return null;
        }
    }

    private static String getTitleName(Connection connection, int titleId){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM job_titles WHERE id = ?");
            preparedStatement.setInt(1,titleId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("title_name");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }
    private static int getTitleId(Connection connection, String title){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM job_titles WHERE title_name = ?");
            preparedStatement.setString(1,title);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return 0;
    }

    private static boolean controlUsername(Connection connection, String username){
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM personnels WHERE username = ?");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            //If username exists.
            if(resultSet.next()){
                return false;
            }
            return true;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    private static boolean controlRoom(Connection connection, Reservations reservations, String roomNumber){
        //Controls the room that wanted to place new reservation into. If the room is not empty then return false else place reservation guests to the room and return true.
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        PreparedStatement roomReservState = null;
        PreparedStatement roomState = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM rooms WHERE hotel_id = ? AND room_number = ?");
            preparedStatement.setInt(1,reservations.getHotel_id());
            preparedStatement.setString(2,roomNumber);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                if(!resultSet.getBoolean("is_empty")){
                    return false;
                };
                int roomId = resultSet.getInt("id");
                roomReservState = connection.prepareStatement("INSERT INTO room_reservations (room_id,reservation_id) VALUES (?,?)");
                roomReservState.setInt(1,roomId);
                roomReservState.setInt(2,reservations.getId());
                int rowsAffected = roomReservState.executeUpdate();

                roomState = connection.prepareStatement("UPDATE rooms SET is_empty = 0, available = 0 WHERE id = ?");
                roomState.setInt(1,roomId);
                int affectedRows = roomState.executeUpdate();
                return true;

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(roomReservState != null) roomReservState.close();
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    private static ArrayList<String> getReservationGuestsName(Connection connection ,int reservId){
        /*
        This method is private and Connection is one of the parameter of it.
        It uses connection of the function this function called by. Because if I create connection and reconnection again,
        it causes a lot of performance problem because connection and reconnection to database take too much time.
         */
        //Get every guests name that has that reservation (id).
        PreparedStatement guestReservStatement = null;
        ResultSet resultGuestReserv = null;
        PreparedStatement guestState = null;
        ResultSet resultGuest = null;

        try {
            guestReservStatement = connection.prepareStatement("SELECT * FROM guest_reservations WHERE reservation_id = ?");
            guestReservStatement.setInt(1,reservId);
            resultGuestReserv = guestReservStatement.executeQuery();
            ArrayList<String> names = new ArrayList<>();
            while (resultGuestReserv.next()){
                int guestId = resultGuestReserv.getInt("guest_id");
                guestState = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
                guestState.setInt(1,guestId);
                resultGuest = guestState.executeQuery();
                if (resultGuest.next()){
                    String first_name = resultGuest.getString("first_name");
                    String last_name = resultGuest.getString("last_name");
                    names.add(first_name);
                    names.add(last_name);
                }
            }
            return names;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultGuest != null) resultGuest.close();
                if(guestState != null) guestState.close();
                if(resultGuestReserv != null) resultGuestReserv.close();
                if(guestReservStatement != null) guestReservStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static int getRoomTypeId(Connection connection, String typeName){
        /*
        This method is private and Connection is one of the parameter of it.
        It uses connection of the function this function called by. Because if I create connection and reconnection again,
        it causes a lot of performance problem because connection and reconnection to database take too much time.
         */

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM room_types WHERE type_name = ?");
            preparedStatement.setString(1,typeName);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getInt("id");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return 0;
    }


    private static String getRoomTypeName(Connection connection, int id){
        /*
        This method is private and Connection is one of the parameter of it.
        It uses connection of the function this function called by. Because if I create connection and reconnection again,
        it causes a lot of performance problem because connection and reconnection to database take too much time.
         */

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM room_types WHERE id = ?");
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("type_name");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static boolean isCheckedInOut(Connection connection, int guestId, boolean inOut){
        //InOut: in controls isCheckedIn, out controls isCheckedOut
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM guests WHERE id = ?");
            preparedStatement.setInt(1,guestId);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                if(inOut){
                    return resultSet.getBoolean("checked_in");
                }
                return resultSet.getBoolean("checked_out");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return false;
    }

    public static String getRoomTypeName(int id){
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            //Get room types.
            preparedStatement = connection.prepareStatement("SELECT * FROM room_types WHERE id = ?");
            preparedStatement.setInt(1,id);
            resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getString("type_name");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }
        return null;
    }

    private static String calculateAge(java.sql.Date birthDate) {
        // Convert Date to LocalDate
        LocalDate localBirthDate = birthDate.toLocalDate();
        // Get current date
        LocalDate currentDate = LocalDate.now();
        // Calculate the age
        Period period = Period.between(localBirthDate, currentDate);
        int years = period.getYears();
        int months = period.getMonths();
        //We should convert months to decimal.
        int decimalMonths = (int)(months*(10.0/12.0));
        return years + "," + decimalMonths;
    }

}
