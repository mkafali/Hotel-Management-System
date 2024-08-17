package db_obj;

import java.util.Date;

public class Personnel {
    private final int id, hotel_id;
    private int job_title_id;
    private String first_name, last_name, password, username;
    private final Date start_date;

    public Personnel(int id, int hotel_id, int job_title_id,
                     String first_name, String last_name, String password, String username,
                     Date start_date){
        this.id = id;
        this.hotel_id = hotel_id;
        this.job_title_id = job_title_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.username = username;
        this.start_date = start_date;

    }

    public int getId() {
        return id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getJob_title_id() {
        return job_title_id;
    }

    public void setJob_title_id(int job_title_id) {
        this.job_title_id = job_title_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getStart_date() {
        return start_date;
    }

}
