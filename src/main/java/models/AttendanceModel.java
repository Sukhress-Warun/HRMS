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

    }

    public static JSONObject checkIn(BigDecimal employeeId, String date, String time){
        Attendance attendance = getAttendance(employeeId, date);
        if(attendance == null){

        }

    }

}
