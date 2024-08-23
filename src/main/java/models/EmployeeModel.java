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

    public static JSONObject getAllEmployees(){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            ResultSet rs = con.createStatement().executeQuery("select * from employee");
            JSONArray jsArr =  JsonUtils.convertResultSetToJSONArray(rs);
            return JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "no employees exist", "employees", jsArr);
        }
        catch(Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("retrieved", false, "error retrieving employees", "employees", null);
        }
    }

    public static JSONObject getEmployeeById(BigDecimal id){
        Connection con = null;
        try{
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("select * from employee where id=?");
            st.setBigDecimal(1, id);
            ResultSet rs = st.executeQuery();
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            JSONObject jsObj = jsArr.optJSONObject(0, new JSONObject());
            return JsonUtils.formatJSONObject("retrieved", (!jsObj.isEmpty()), (!jsObj.isEmpty()) ? "success" : "employee id doesnt exist", "employee", (!jsObj.isEmpty()) ? jsObj : null);

        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("retrieved", false, "error retrieving employee", "employee", null);
        }
    }

    public static JSONObject addEmployee(Employee employee) {
        Connection con = null;
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
            JSONObject jsObj = new JSONObject(gson.toJson(employee));
            return JsonUtils.formatJSONObject("added", true, "success", "employee", jsObj);

        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("added", false, "error adding employee", "employee", null);
        }
    }

    public static JSONObject updateEmployee(Employee employee) {
        Connection con = null;
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
                return JsonUtils.formatJSONObject("updated", false, "id doesnt exist", "employee", null);
            }

            JSONObject jsObj = new JSONObject(gson.toJson(employee));
            return JsonUtils.formatJSONObject("updated", true, "success", "employee", jsObj);

        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("updated", false, "error updating employee", "employee", null);
        }
    }

    public static JSONObject deleteEmployee(BigDecimal id) {
        Connection con = null;
        try {
            con = DatabaseConnection.initializeDatabase();
            PreparedStatement st = con.prepareStatement("delete from employee where id=?");
            st.setBigDecimal(1, id);
            int deleted =  st.executeUpdate();
            return JsonUtils.formatJSONObject("deleted", (deleted > 0), (deleted > 0) ? "success" : "id doesnt exist", "id", (deleted > 0) ? id : null);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtils.formatJSONObject("deleted", false, "error deleting employee", "id", id);
        }
    }

    public static JSONObject getReportingToEmployees(BigDecimal id){
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
            JSONArray jsArr = JsonUtils.convertResultSetToJSONArray(rs);
            return JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "employee id doesnt exist", "employees", jsArr);
        }
        catch (Exception e){
            e.printStackTrace();
            return JsonUtils.formatJSONObject("retrieved", false, "error retrieving employees", "employees", null);
        }
    }

}
