package db_obj;

import java.util.Date;

public class Reservations {
    private final int id, hotel_id, agency_id, room_type_id;
    private int reservation_price, extra_debt, number_of_people;
    private final Date expected_check_in;
    private Date expected_check_out;
    private String notes , room_number, extras;
    private boolean old_reservation, current_reservation, future_reservation, paid;

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        int number_of_people, Date expected_check_in, Date expected_check_out) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        old_reservation = false;
        current_reservation = false;
        future_reservation = true;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        int number_of_people, Date expected_check_in, Date expected_check_out,
                        boolean old_reservation, boolean current_reservation, boolean future_reservation) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.old_reservation = old_reservation;
        this.current_reservation = current_reservation;
        this.future_reservation = future_reservation;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        String room_number, int number_of_people, Date expected_check_in, Date expected_check_out) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.room_number = room_number;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        old_reservation = false;
        current_reservation = false;
        future_reservation = true;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        String room_number, int number_of_people, Date expected_check_in, Date expected_check_out,
                        boolean old_reservation, boolean current_reservation, boolean future_reservation) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.room_number = room_number;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.old_reservation = old_reservation;
        this.current_reservation = current_reservation;
        this.future_reservation = future_reservation;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        int number_of_people, Date expected_check_in, Date expected_check_out, String notes)
    {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.notes = notes;
        old_reservation = false;
        current_reservation = false;
        future_reservation = true;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        int number_of_people, Date expected_check_in, Date expected_check_out, String notes,
                        boolean old_reservation, boolean current_reservation, boolean future_reservation)
    {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.notes = notes;
        this.old_reservation = old_reservation;
        this.current_reservation = current_reservation;
        this.future_reservation = future_reservation;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        String room_number, int number_of_people, Date expected_check_in,
                        Date expected_check_out, String notes) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.room_number = room_number;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.notes = notes;
        old_reservation = false;
        current_reservation = false;
        future_reservation = true;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price,
                        String room_number, int number_of_people, Date expected_check_in,
                        Date expected_check_out, String notes,
                        boolean old_reservation, boolean current_reservation, boolean future_reservation) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.room_number = room_number;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.notes = notes;
        this.old_reservation = old_reservation;
        this.current_reservation = current_reservation;
        this.future_reservation = future_reservation;
    }

    public Reservations(int id, int hotel_id, int agency_id, int room_type_id, int reservation_price, int extra_debt, String room_number, int number_of_people, Date expected_check_in, Date expected_check_out, String notes, boolean old_reservation, boolean current_reservation, boolean future_reservation, String extras, boolean paid) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.agency_id = agency_id;
        this.room_type_id = room_type_id;
        this.reservation_price = reservation_price;
        this.extra_debt = extra_debt;
        this.room_number = room_number;
        this.number_of_people = number_of_people;
        this.expected_check_in = expected_check_in;
        this.expected_check_out = expected_check_out;
        this.notes = notes;
        this.old_reservation = old_reservation;
        this.current_reservation = current_reservation;
        this.future_reservation = future_reservation;
        this.extras = extras;
        this.paid = paid;
    }

    public int getId() {
        return id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getAgency_id() {
        return agency_id;
    }

    public int getRoom_type_id() {
        return room_type_id;
    }

    public int getReservation_price() {
        return reservation_price;
    }

    public void setReservation_price(int reservation_price) {
        this.reservation_price = reservation_price;
    }

    public int getExtra_debt() {
        return extra_debt;
    }

    public void setExtra_debt(int extra_debt) {
        this.extra_debt = extra_debt;
    }


    public String getRoom_number() {
        return room_number;
    }

    public void setRoom_number(String room_number) {
        this.room_number = room_number;
    }

    public int getNumber_of_people() {
        return number_of_people;
    }

    public void setNumber_of_people(int number_of_people) {
        this.number_of_people = number_of_people;
    }

    public Date getExpected_check_in() {
        return expected_check_in;
    }

    public Date getExpected_check_out() {
        return expected_check_out;
    }

    public void setExpected_check_out(Date expected_check_out) {
        this.expected_check_out = expected_check_out;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isOld_reservation() {
        return old_reservation;
    }

    public void setOld_reservation(boolean old_reservation) {
        this.old_reservation = old_reservation;
    }

    public boolean isCurrent_reservation() {
        return current_reservation;
    }

    public void setCurrent_reservation(boolean current_reservation) {
        this.current_reservation = current_reservation;
    }

    public boolean isFuture_reservation() {
        return future_reservation;
    }

    public void setFuture_reservation(boolean future_reservation) {
        this.future_reservation = future_reservation;
    }

    public String getExtras() {
        return extras;
    }

    public void setExtras(String extras) {
        this.extras = extras;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
