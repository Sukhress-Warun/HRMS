package models;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import customUtils.*;
import database.*;
import entities.*;
import org.json.*;
import com.google.gson.*;

import java.math.BigDecimal;
import java.sql.*;


//create table attendance (id bigint auto_increment,
//                         date date not null,
//                         employee_id bigint not null ,
//                         worked_time time,
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

    public static Attendance getAttendance(BigDecimal employeeId, String date){
        Connection con = null;
        try{
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
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static Attendance addAttendance(BigDecimal employeeId, String date, String time){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into attendance (employee_id, date, first_check_in, applied_leave) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setBigDecimal(1, employeeId);
            st.setDate(2, Date.valueOf(date));
            st.setTime(3, Time.valueOf(time));
            st.setBoolean(4, false);
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
        Attendance attendance = getAttendance(employeeId, date);
        if(attendance == null){
            attendance = addAttendance(employeeId, date, time);
            if (attendance == null){
                return null;
            }
        }
        else if(attendance.getAppliedLeave()){
            boolean cancelled = cancelLeave(attendance);
            if(!cancelled){
                return null;
            }
            attendance.setAppliedLeave(false);
        }
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into log (employee_id, check_in, attendance_id) values (?,?,?)");
            st.setBigDecimal(1, employeeId);
            st.setTime(2, Time.valueOf(time));
            st.setBigDecimal(3, attendance.getId());
            st.executeUpdate();
            attendance.setFirstCheckIn(time);
            return new JSONObject(gson.toJson(attendance));
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static boolean cancelLeave(Attendance attendance){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update attendance set applied_leave=false where id=?");
            st.setBigDecimal(1, attendance.getId());
            st.executeUpdate();
            PreparedStatement st2 = con.prepareStatement("update employee set leave_available=leave_available+1 where id=?");
            st2.setBigDecimal(1, attendance.getEmployeeId());
            st2.executeUpdate();
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static JSONObject getStatus(BigDecimal employeeId, String date){
        Attendance attendance = getAttendance(employeeId, date);


    }

}
