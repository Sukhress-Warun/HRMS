package models;

import java.math.BigDecimal;
import java.sql.*;

import org.json.*;
import com.google.gson.*;

import customUtils.*;
import database.*;
import entities.*;


//create table holiday (id bigint auto_increment,
//                      from_date date not null,
//                      to_date date not null,
//                      description varchar(100),
//primary key (id));

public class HolidayModel{

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public static JSONObject getAllHolidays(int perPage, int page, String sortCol, String sortOrder, String searchDescription) {

        Connection con = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from holiday where description like ? order by " + sortCol + " " + sortOrder + " limit ? offset ?");
            st.setString(1, "%" + searchDescription + "%");
            st.setInt(2, perPage);
            st.setInt(3, (page - 1) * perPage);
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            JSONObject pageDetails = new JSONObject();
            pageDetails.put("per_page", perPage);
            pageDetails.put("page", page);
            pageDetails.put("search_description", searchDescription);
            pageDetails.put("sort_column", sortCol);
            pageDetails.put("sort_order", sortOrder);

            return JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "no holidays exist", "holidays", jsArr).put("page_details", pageDetails);

        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("retrieved", false, "error retrieving holidays", "holidays", null);
        }
    }

    public static JSONObject getHolidayById(BigDecimal id) {
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from holiday where id=?");
            st.setBigDecimal(1, id);
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            JSONObject jsObj = jsArr.optJSONObject(0, new JSONObject());
            return JsonUtils.formatJSONObject("retrieved", (!jsObj.isEmpty()), (!jsObj.isEmpty()) ? "success" : "holiday id doesnt exist", "holiday", (!jsObj.isEmpty()) ? jsObj : null);

        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("retrieved", false, "error retrieving holiday", "holiday", null);
        }
    }

    public static JSONObject addHoliday(Holiday holiday) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into holiday (from_date, to_date, description) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setDate(1, Date.valueOf(holiday.getFromDate()));
            st.setDate(2, Date.valueOf(holiday.getToDate()));
            st.setString(3, holiday.getDescription());
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            BigDecimal id = rs.getBigDecimal(1);
            holiday.setId(id);
            res = JsonUtils.formatJSONObject("added", true, "holiday added", "holiday", new JSONObject(gson.toJson(holiday)));
            return res;
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject updateHoliday(Holiday holiday) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update holiday set from_date=?, to_date=?, description=? where id=?");
            st.setDate(1, Date.valueOf(holiday.getFromDate()));
            st.setDate(2, Date.valueOf(holiday.getToDate()));
            st.setString(3, holiday.getDescription());
            st.setBigDecimal(4, holiday.getId());
            int updated = st.executeUpdate();
            if(updated != 1){
                res = JsonUtils.formatJSONObject("updated", false, "id doesnt exist", "holiday", null);
                return res;
            }
            res = JsonUtils.formatJSONObject("updated", true, "holiday updated", "holiday", new JSONObject(gson.toJson(holiday)));
            return res;
        }
        catch (Exception e){
            e.printStackTrace();
            res = JsonUtils.formatJSONObject("updated", false, "error updating holiday", "holiday", null);
            return res;
        }
    }

    public static JSONObject deleteHoliday(BigDecimal id) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("delete from holiday where id=?");
            st.setBigDecimal(1, id);
            int status = st.executeUpdate();
            if(status == 1){
                res = JsonUtils.formatJSONObject("deleted", true, "holiday deleted", null, null);
                return res;
            }
            else{
                res = JsonUtils.formatJSONObject("deleted", false, "id doesnt exist", null, null);
                return res;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting holiday", null, null);
            return res;
        }
    }

    public static JSONArray getHolidaysBetweenDates(String fromDate, String toDate) {
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            // overlapping range find in holidays
            PreparedStatement st = con.prepareStatement("select * from holiday where (from_date between ? and ?) or (to_date between ? and ?) or (from_date <= ? and to_date >= ?) order by from_date");
            st.setDate(1, Date.valueOf(fromDate));
            st.setDate(2, Date.valueOf(toDate));
            st.setDate(3, Date.valueOf(fromDate));
            st.setDate(4, Date.valueOf(toDate));
            st.setDate(5, Date.valueOf(fromDate));
            st.setDate(6, Date.valueOf(toDate));
            ResultSet rs = st.executeQuery();
            return JsonUtils.convertResultSetToJSONArray(rs);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getHolidayOnDate(String date) {
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from holiday where ? between from_date and to_date");
            st.setDate(1, Date.valueOf(date));
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            return jsArr.optJSONObject(0, null);

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
