package entities;

//create table log (id bigint auto_increment,
//                  check_in time,
//                  check_out time,
//                  attendance_id bigint not null,
//                  primary key (id),
//foreign key (attendance_id) references attendance(id) on delete cascade );

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Log {

    private BigDecimal id;
    @SerializedName("check_in")
    private String checkIn;
    @SerializedName("check_out")
    private String checkOut;
    @SerializedName("attendance_id")
    private BigDecimal attendanceId;

    public Log(BigDecimal id, String checkIn, String checkOut, BigDecimal attendanceId) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.attendanceId = attendanceId;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public BigDecimal getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(BigDecimal attendanceId) {
        this.attendanceId = attendanceId;
    }

}
