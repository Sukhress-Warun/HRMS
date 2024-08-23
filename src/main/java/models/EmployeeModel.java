package models;

import java.math.BigDecimal;
import java.sql.*;

import org.json.*;
import com.google.gson.*;

import customUtils.*;
import database.*;
import entities.*;


public class EmployeeModel {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public static JSONArray getAllEmployees(){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            ResultSet rs = con.createStatement().executeQuery("select * from employee");
            return JsonUtils.convertResultSetToJSONArray(rs);
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getEmployeeById(String id){
        Connection con = null;
        JSONObject res = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from employee where id=?");
            st.setBigDecimal(1, new BigDecimal(id));
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            return jsArr.optJSONObject(0, new JSONObject());
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject addEmployee(Employee employee) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("insert into employee (name, role_id, doj, dob, gender, reporting_to, dept_id, leave_available) values (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            st.setString(1, employee.getName());
            st.setBigDecimal(2, employee.getRoleId());
            st.setString(3, employee.getDoj());
            st.setString(4, employee.getDob());
            st.setString(5, employee.getGender());
            st.setBigDecimal(6, employee.getReportingTo());
            st.setBigDecimal(7, employee.getDeptId());
            st.setInt(8, employee.getLeaveAvailable());
            st.executeUpdate();
            // get inserted id
            ResultSet rs = st.getGeneratedKeys();
            rs.next();
            BigDecimal id = rs.getBigDecimal(1);
            employee.setId(id);
            res = new JSONObject(gson.toJson(employee));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject updateEmployee(Employee employee) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("update employee set name=?, role_id=?, doj=?, dob=?, gender=?, reporting_to=?, dept_id=?, leave_available=? where id=?");
            st.setString(1, employee.getName());
            st.setBigDecimal(2, employee.getRoleId());
            st.setString(3, employee.getDoj());
            st.setString(4, employee.getDob());
            st.setString(5, employee.getGender());
            st.setBigDecimal(6, employee.getReportingTo());
            st.setBigDecimal(7, employee.getDeptId());
            st.setInt(8, employee.getLeaveAvailable());
            st.setBigDecimal(9, employee.getId());
            int updated = st.executeUpdate();
            if(updated == 0){
                return null;
            }
            res = new JSONObject(gson.toJson(employee));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer deleteEmployee(BigDecimal id) {
        Connection con = null;
        JSONObject res = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("delete from employee where id=?");
            st.setBigDecimal(1, id);
            return st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray getReportingToEmployees(BigDecimal id){
        Connection con = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("with recursive ascendants as( \n" +
                    "select 0 as lvl, employee.* from employee where id = ?\n" +
                    "union all \n" +
                    "select lvl+1,employee.* from ascendants join employee on ascendants.reporting_to = employee.id \n" +
            ") \n"+
            "select * from ascendants order by lvl;");
            st.setBigDecimal(1, id);
            ResultSet rs = st.executeQuery();
            return JsonUtils.convertResultSetToJSONArray(rs);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
