package db_obj;

import java.sql.Timestamp;
import java.util.Date;

public class Guests {
    private final int id, hotel_id, reservation_id;
    private final String first_name, last_name;
    private String country;
    private int id_number;
    private Date birth_date;
    private Timestamp check_in_datetime, check_out_datetime;
    private boolean kbs_in, kbs_out, checked_in, checked_out;

    public Guests(int id, int hotel_id, int reservation_id, String first_name, String last_name){
        this.id = id;
        this.hotel_id = hotel_id;
        this.reservation_id = reservation_id;
        this.first_name = first_name;
        this.last_name = last_name;

    }

    public Guests(int id, int hotel_id, int reservation_id, String first_name, String last_name, String country,
                  int id_number, Date birth_date, Timestamp check_in_datetime, Timestamp check_out_datetime,
                  boolean kbs_in, boolean kbs_out, boolean checked_in, boolean checked_out) {
        this.id = id;
        this.hotel_id = hotel_id;
        this.reservation_id = reservation_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.country = country;
        this.id_number = id_number;
        this.birth_date = birth_date;
        this.check_in_datetime = check_in_datetime;
        this.check_out_datetime = check_out_datetime;
        this.kbs_in = kbs_in;
        this.kbs_out = kbs_out;
        this.checked_in = checked_in;
        this.checked_out = checked_out;
    }

    public int getId() {
        return id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getReservation_id() {
        return reservation_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getId_number() {
        return id_number;
    }

    public void setId_number(int id_number) {
        this.id_number = id_number;
    }

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public Timestamp getCheck_in_datetime() {
        return check_in_datetime;
    }

    public void setCheck_in_datetime(Timestamp check_in_datetime) {
        this.check_in_datetime = check_in_datetime;
    }

    public Timestamp getCheck_out_datetime() {
        return check_out_datetime;
    }

    public void setCheck_out_datetime(Timestamp check_out_datetime) {
        this.check_out_datetime = check_out_datetime;
    }

    public boolean isKbs_in() {
        return kbs_in;
    }

    public void setKbs_in(boolean kbs_in) {
        this.kbs_in = kbs_in;
    }

    public boolean isKbs_out() {
        return kbs_out;
    }

    public void setKbs_out(boolean kbs_out) {
        this.kbs_out = kbs_out;
    }

    public boolean isChecked_in() {
        return checked_in;
    }

    public void setChecked_in(boolean checked_in) {
        this.checked_in = checked_in;
    }

    public boolean isChecked_out() {
        return checked_out;
    }

    public void setChecked_out(boolean checked_out) {
        this.checked_out = checked_out;
    }
}
