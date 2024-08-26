package models;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import com.google.gson.*;

import customUtils.*;
import database.*;
import entities.*;


//create table attendance (id bigint auto_increment,
//                         date date not null,
//                         employee_id bigint not null ,
//                         worked_time time default '00:00:00',
//                         applied_leave boolean,
//                         first_check_in time,
//                         last_check_out time,
//                         primary key (id),
//foreign key (employee_id) references employee(id) on delete cascade );
//
//create table log (id bigint auto_increment,
//                  check_in time,
//                  check_out time,
//                  attendance_id bigint not null,
//                  primary key (id),
//foreign key (attendance_id) references attendance(id) on delete cascade );

public class AttendanceModel {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public static Attendance getAttendance(BigDecimal employeeId, String date) throws Exception {
        Connection con = null;
        con = DatabaseConnection.initializeDatabase();
        PreparedStatement st = con.prepareStatement("select * from attendance where employee_id=? and date=?");
        st.setBigDecimal(1, employeeId);
        st.setDate(2, Date.valueOf(date));
        ResultSet rs = st.executeQuery();
        JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
        JSONObject jsObj = jsArr.optJSONObject(0, null);
        if(jsObj == null){
            return null;
        }
        return gson.fromJson(jsObj.toString(), Attendance.class);

    }

    public static Attendance addAttendance(BigDecimal employeeId, String date, String time, boolean appliedLeave){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into attendance (employee_id, date, first_check_in, applied_leave) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setBigDecimal(1, employeeId);
            st.setDate(2, Date.valueOf(date));
            st.setTime(3, (time != null) ? Time.valueOf(time) : null);
            st.setBoolean(4, appliedLeave);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            return getAttendance(employeeId, date);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject checkIn(BigDecimal employeeId, String date, String time){

        JSONObject holiday = HolidayModel.getHolidayOnDate(date);
        if(holiday != null){
            return JsonUtils.formatJSONObject("checked-in", false, "holiday on this date", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL)).put("holiday", holiday);
        }

        Attendance attendance ;
        try{
            attendance = getAttendance(employeeId, date);
        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("checked-in", false, "error retrieving attendance", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }

        if(attendance == null){
            attendance = addAttendance(employeeId, date, null, false);
            if (attendance == null){
                return JsonUtils.formatJSONObject("checked-in", false, "error adding attendance", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            }
        }
        else if(attendance.getAppliedLeave()){
            return JsonUtils.formatJSONObject("checked-in", false, "can't check-in after applying leave", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }

        JSONObject status = getStatus(attendance.getId(), false);
//         ! getStatus can return null if error, so we should reset the database to the previous state if error occurs
        if(status.getString("current_status").equals("checked-in")){
            return JsonUtils.formatJSONObject("checked-in", false, "already checked-in", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL))
                    .put("info", status);
        }
        // check time greater than previous checkout
        if(status.get("last_check_out") != JSONObject.NULL){ // null if status is fresh
            String lastCheckOut = status.getString("last_check_out");
            if(Time.valueOf(time).before(Time.valueOf(lastCheckOut)) || Time.valueOf(time).equals(Time.valueOf(lastCheckOut))){
                return JsonUtils.formatJSONObject("checked-in", false, "check-in time should be greater than last check-out", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL))
                        .put("info", status);
            }
        }
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into log (check_in, attendance_id) values (?,?)");
            st.setTime(1, Time.valueOf(time));
            st.setBigDecimal(2, attendance.getId());
            st.executeUpdate();
            if(status.getString("current_status").equals("fresh")) {
                PreparedStatement st2 = con.prepareStatement("update attendance set first_check_in=? where id=?");
                st2.setTime(1, Time.valueOf(time));
                st2.setBigDecimal(2, attendance.getId());
                st2.executeUpdate();
            }
            return JsonUtils.formatJSONObject("checked-in", true, "success", "log", new JSONObject().put("date", date).put("time", time));
        }
        catch (Exception e){
            e.printStackTrace();
            // ! reset the database to the previous state
            return null;
        }
    }

    public static JSONObject checkOut(BigDecimal employeeId, String date, String time) {

        Attendance attendance;
        try {
            attendance = getAttendance(employeeId, date);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("checked-out", false, "error retrieving attendance", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }

        if (attendance == null) {
            return JsonUtils.formatJSONObject("checked-out", false, "no attendance record found", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }
        else if(attendance.getAppliedLeave()){
            return JsonUtils.formatJSONObject("checked-out", false, "leave applied and no check in found", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }

        JSONObject status = getStatus(attendance.getId(), true);
        if (status == null){
            return JsonUtils.formatJSONObject("checked-out", false, "error retrieving status", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }

        BigDecimal logId = status.getBigDecimal("log_id");
        status.remove("log_id");

        if (status.getString("current_status").equals("checked-out")) {
            return JsonUtils.formatJSONObject("checked-out", false, "already checked-out", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL))
                    .put("info", status);
        }
        // check time greater than previous check-in
        String lastCheckIn = status.getString("last_check_in");
        if (Time.valueOf(time).before(Time.valueOf(lastCheckIn)) || Time.valueOf(time).equals(Time.valueOf(lastCheckIn))) {
            return JsonUtils.formatJSONObject("checked-out", false, "check-out time should be greater than last check-in", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL))
                    .put("info", status);
        }

        Connection con = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update log set check_out=? where id=?");
            st.setTime(1, Time.valueOf(time));
            st.setBigDecimal(2, logId);
            st.executeUpdate();
            PreparedStatement st2 = con.prepareStatement("update attendance set last_check_out=? , worked_time = ADDTIME(worked_time, TIMEDIFF(?, ?)) where id=?");
            st2.setTime(1, Time.valueOf(time));
            st2.setTime(2, Time.valueOf(time));
            st2.setTime(3, Time.valueOf(lastCheckIn));
            st2.setBigDecimal(4, attendance.getId());
            st2.executeUpdate();
            return JsonUtils.formatJSONObject("checked-out", true, "success", "log", new JSONObject().put("date", date).put("time", time));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("checked-out", false, "error updating log", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
        }
    }

    public static JSONObject cancelLeave(BigDecimal employeeId, String date){
        JSONObject res;
        Attendance attendance = null;
        try{
            attendance = AttendanceModel.getAttendance(employeeId, date);
        }
        catch (Exception e){
            e.printStackTrace();
            res = JsonUtils.formatJSONObject("cancelled-leave", false, "error cancelling leave : error retrieving attendance", "date", JSONObject.NULL);
            return res;
        }

        if (attendance == null) {
            res = JsonUtils.formatJSONObject("cancelled-leave", false, "error cancelling leave : no record found", "date", JSONObject.NULL);
            return res;
        }
        else if (!attendance.getAppliedLeave()) {
            res = JsonUtils.formatJSONObject("cancelled-leave", false, "error cancelling leave : leave not applied", "date", JSONObject.NULL);
            return res;
        }

        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update attendance set applied_leave=false where id=?");
            st.setBigDecimal(1, attendance.getId());
            st.executeUpdate();
            PreparedStatement st2 = con.prepareStatement("update employee set leave_available=leave_available+1 where id=?");
            st2.setBigDecimal(1, employeeId);
            st2.executeUpdate();
            return JsonUtils.formatJSONObject("cancelled-leave", true, "success", "date", date);
        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("cancelled-leave", false, "error cancelling leave", "date", JSONObject.NULL);
        }
    }

    public static JSONObject applyLeave(BigDecimal employeeId, String date){

        // check for holiday
        JSONObject holiday = HolidayModel.getHolidayOnDate(date);
        if(holiday != null){
            return JsonUtils.formatJSONObject("applied-leave", false, "holiday on this date", "date", date).put("holiday", holiday);
        }

        Attendance attendance;
        try{
            attendance = getAttendance(employeeId, date);
        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("applied-leave", false, "error retrieving attendance", "date", JSONObject.NULL);
        }


        if(attendance == null){
            attendance = addAttendance(employeeId, date, null, false);
            if(attendance == null){
                return JsonUtils.formatJSONObject("applied-leave", false, "error adding attendance", "date", JSONObject.NULL);
            }
        }
        else if(attendance.getAppliedLeave()){
            return JsonUtils.formatJSONObject("applied-leave", false, "leave already applied", "date", date);
        }
        else if(!attendance.getAppliedLeave()){
            JSONObject status = getStatus(attendance.getId(), false);
            if(status == null){
                return JsonUtils.formatJSONObject("applied-leave", false, "error retrieving status", "date", date);
            }
            if(!status.getString("current_status").equals("fresh")) {
                return JsonUtils.formatJSONObject("applied-leave", false, "leave cannot be applied after check-in", "date", date);
            }
        }

        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update attendance set applied_leave=true where id=?");
            st.setBigDecimal(1, attendance.getId());
            st.executeUpdate();
            PreparedStatement st2 = con.prepareStatement("update employee set leave_available=leave_available-1 where id=?");
            st2.setBigDecimal(1, employeeId);
            st2.executeUpdate();
            return JsonUtils.formatJSONObject("applied-leave", true, "success", "date", date);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    public static JSONObject getStatus(BigDecimal attendanceId, boolean getLogId){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            // ! if everything in database is added through the application, then it works. else if already loaded in database then all logs should be checked.
            PreparedStatement st = con.prepareStatement("select * from log where attendance_id=? order by check_in desc limit 1");
            st.setBigDecimal(1, attendanceId);
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            JSONObject jsObj = jsArr.optJSONObject(0, null);
            if(jsObj == null){
                return new JSONObject().put("current_status", "fresh")
                        .put("last_check_in", JSONObject.NULL)
                        .put("last_check_out", JSONObject.NULL);
            }
            String checkIn = jsObj.optString("check_in", null);
            String checkOut = jsObj.optString("check_out", null);
            return new JSONObject().put("current_status", (checkOut == null) ? "checked-in" : "checked-out")
                    .put("last_check_in", (checkIn == null) ? JSONObject.NULL : checkIn)
                    .put("last_check_out", (checkOut == null) ? JSONObject.NULL : checkOut).put("log_id", (getLogId) ? jsObj.getBigDecimal("id") : null);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<Attendance> getAttendanceBetweenDates(BigDecimal employeeId, String fromDate, String toDate) {
        Connection con = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from attendance where employee_id=? and date between ? and ?");
            st.setBigDecimal(1, employeeId);
            st.setDate(2, Date.valueOf(fromDate));
            st.setDate(3, Date.valueOf(toDate));
            ResultSet rs = st.executeQuery();
            List<Attendance> attendanceList = new ArrayList<>();
            for(; rs.next(); ){
                Attendance attendance = new Attendance();
                attendance.setId(rs.getBigDecimal("id"));
                attendance.setDate(rs.getDate("date").toString());
                attendance.setEmployeeId(rs.getBigDecimal("employee_id"));
                attendance.setWorkedTime(rs.getTime("worked_time").toString());
                attendance.setAppliedLeave(rs.getBoolean("applied_leave"));
                Time fct = rs.getTime("first_check_in");
                attendance.setFirstCheckIn((fct != null) ? fct.toString() : null);
                Time lco = rs.getTime("last_check_out");
                attendance.setLastCheckOut((lco != null) ? lco.toString() : null);
                attendanceList.add(attendance);
            }
            return attendanceList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
