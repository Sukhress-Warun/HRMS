package entities;

//create table attendance (id bigint auto_increment,
//                         date date not null,
//                         employee_id bigint not null ,
//                         worked_time time,
//                         applied_leave boolean,
//                         first_check_in time,
//                         last_check_out time,
//                         primary key (id),
//foreign key (employee_id) references employee(id) on delete cascade );

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class Attendance {

    private BigDecimal id;
    private String date;
    @SerializedName("employee_id")
    private BigDecimal employeeId;
    @SerializedName("worked_time")
    private String workedTime;
    @SerializedName("applied_leave")
    private boolean appliedLeave;
    @SerializedName("first_check_in")
    private String firstCheckIn;
    @SerializedName("last_check_out")
    private String lastCheckOut;

    public Attendance(BigDecimal id, String date, BigDecimal employeeId, String workedTime, boolean appliedLeave, String firstCheckIn, String lastCheckOut) {
        this.id = id;
        this.date = date;
        this.employeeId = employeeId;
        this.workedTime = workedTime;
        this.appliedLeave = appliedLeave;
        this.firstCheckIn = firstCheckIn;
        this.lastCheckOut = lastCheckOut;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(BigDecimal employeeId) {
        this.employeeId = employeeId;
    }

    public String getWorkedTime() {
        return workedTime;
    }

    public void setWorkedTime(String workedTime) {
        this.workedTime = workedTime;
    }

    public boolean getAppliedLeave() {
        return appliedLeave;
    }

    public void setAppliedLeave(boolean appliedLeave) {
        this.appliedLeave = appliedLeave;
    }

    public String getFirstCheckIn() {
        return firstCheckIn;
    }

    public void setFirstCheckIn(String firstCheckIn) {
        this.firstCheckIn = firstCheckIn;
    }

    public String getLastCheckOut() {
        return lastCheckOut;
    }

    public void setLastCheckOut(String lastCheckOut) {
        this.lastCheckOut = lastCheckOut;
    }

}
