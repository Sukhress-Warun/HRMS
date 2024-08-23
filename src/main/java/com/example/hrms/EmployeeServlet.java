package com.example.hrms;

import java.io.*;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import org.json.*;
import com.google.gson.*;

import entities.*;
import models.*;
import customUtils.*;


@WebServlet("/employee/*")
public class EmployeeServlet extends HttpServlet {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            String path = request.getPathInfo();

            if (path == null || path.isEmpty() || path.equals("/")) {
                getAllEmployees(request, response);
            } else if (path.split("/")[1].equals("higher")) {
                getHigherEmployees(request, response);
            } else {
                getEmployeeById(request, response);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonUtils.prepareResponse(response);
            JSONObject res = JsonUtils.formatJSONObject("retrieved", false, "error occurred", null, null);
            response.getWriter().write(res.toString());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        Employee employee = gson.fromJson(request.getReader(), Employee.class);
        // * id is auto generated
        employee.setId(null);
        JSONObject jsObj = EmployeeModel.addEmployee(employee);
        if(jsObj == null){
            res = JsonUtils.formatJSONObject("added", false, "error adding employee", "employee", null);
        }
        else {
            res = JsonUtils.formatJSONObject("added", true, "success", "employee", jsObj);
        }

        response.getWriter().write(res.toString());

    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        Employee employee = gson.fromJson(request.getReader(), Employee.class);
        employee.setId(null);
        String path = request.getPathInfo();

        if(path == null || path.isEmpty() || path.equals("/")){
            res = JsonUtils.formatJSONObject("updated", false, "error updating employee : no id specified in uri", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }
        try{
            employee.setId(new BigDecimal(path.split("/")[1]));
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("updated", false, "error updating employee : invalid id specified in uri", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }
        JSONObject jsObj = EmployeeModel.updateEmployee(employee);
        if(jsObj == null){
            res = JsonUtils.formatJSONObject("updated", false, "error updating employee", "employee", null);
        }
        else {
            res = JsonUtils.formatJSONObject("updated", true, "success", "employee", jsObj);
        }


        response.getWriter().write(res.toString());

    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        String path = request.getPathInfo();
        if(path == null || path.isEmpty() || path.equals("/")){
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting employee : no id specified in uri", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }
        BigDecimal id;
        try{
            id = new BigDecimal(path.split("/")[1]);
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting employee : invalid id specified in uri", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }
        Integer deleted = EmployeeModel.deleteEmployee(id);
        if(deleted == null){
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting employee", "employee", null);
        }
        else {
            res = JsonUtils.formatJSONObject("deleted", (deleted > 0), (deleted > 0) ? "success" : "id doesnt exist", "id", (deleted > 0) ? id : null);
        }

        response.getWriter().write(res.toString());

    }

    public void getAllEmployees(HttpServletRequest request, HttpServletResponse response) throws IOException {

        //      TODO : search , pagination via params

        JsonUtils.prepareResponse(response);
        JSONObject res;

        // get all employees
        JSONArray jsArr = EmployeeModel.getAllEmployees();
        if(jsArr == null){
            res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving employees", "employees", null);
        }
        else {
            res = JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "no employees exist", "employees", jsArr);
        }

        response.getWriter().write(res.toString());

    }

    public void getEmployeeById(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        // get single employee
        res = EmployeeModel.getEmployeeById(request.getPathInfo().split("/")[1]);
        if (res == null) {
            res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving employee", "employee", null);
        } else {
            res = JsonUtils.formatJSONObject("retrieved", (!res.isEmpty()), (!res.isEmpty()) ? "success" : "employee id doesnt exist", "employee", (!res.isEmpty()) ? res : null);
        }

        response.getWriter().write(res.toString());
    }

    public void getHigherEmployees(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        BigDecimal id;
        try{
            id = new BigDecimal(request.getParameter("id"));
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("retrieved", false, "invalid employee id", "higher", null);
            response.getWriter().write(res.toString());
            return;
        }

        JSONArray highers = EmployeeModel.getReportingToEmployees(id);
        if(highers == null){
            res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving highers", "higher", null);
        }
        else {
            res = JsonUtils.formatJSONObject("retrieved", (!highers.isEmpty()), (!highers.isEmpty()) ? "success" : "no highers exist", "higher", highers);
        }

        response.getWriter().write(res.toString());

    }
}