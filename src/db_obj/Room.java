package db_obj;

public class Room {
    private final int id, hotel_id, room_type_id;
    private final String room_number;
    private String notes;
    private boolean available,clean,tech_ok,isempty;
    private final boolean virtual_room;
    public Room(int id, int hotel_id, int room_type_id, String room_number){
        this.id = id;
        this.hotel_id = hotel_id;
        this.room_type_id = room_type_id;
        this.room_number = room_number;
        this.available = true;
        this.clean = true;
        this.tech_ok = true;
        this.virtual_room = false;
    }

    public Room(int id, int hotel_id, int room_type_id, String room_number, boolean virtual_room){
        this.id = id;
        this.hotel_id = hotel_id;
        this.room_type_id = room_type_id;
        this.room_number = room_number;
        this.available = true;
        this.clean = true;
        this.tech_ok = true;
        this.virtual_room = virtual_room;
    }

    public Room(int id, int hotel_id, int room_type_id, String room_number, String notes,
                boolean available, boolean clean, boolean tech_ok, boolean isempty, boolean virtual_room){
        this.id = id;
        this.hotel_id = hotel_id;
        this.room_type_id = room_type_id;
        this.room_number = room_number;
        this.notes = notes;
        this.available = available;
        this.clean = clean;
        this.tech_ok = tech_ok;
        this.isempty = isempty;
        this.virtual_room = virtual_room;
    }

    public Room(int id, int hotel_id, int room_type_id, String room_number,
                boolean available, boolean clean, boolean tech_ok, boolean isempty, boolean virtual_room){
        this.id = id;
        this.hotel_id = hotel_id;
        this.room_type_id = room_type_id;
        this.room_number = room_number;
        this.available = available;
        this.clean = clean;
        this.tech_ok = tech_ok;
        this.isempty = isempty;
        this.virtual_room = virtual_room;
    }

    public int getId() {
        return id;
    }

    public int getHotel_id() {
        return hotel_id;
    }

    public int getRoom_type_id() {
        return room_type_id;
    }

    public String getRoom_number() {
        return room_number;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isClean() {
        return clean;
    }

    public void setClean(boolean clean) {
        this.clean = clean;
    }

    public boolean isTech_ok() {
        return tech_ok;
    }

    public void setTech_ok(boolean tech_ok) {
        this.tech_ok = tech_ok;
    }

    public boolean isIsempty() {
        return isempty;
    }

    public void setIsempty(boolean isempty) {
        this.isempty = isempty;
    }

    public boolean isVirtual_room() {
        return virtual_room;
    }
}
