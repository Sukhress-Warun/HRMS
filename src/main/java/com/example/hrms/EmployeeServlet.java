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

//
//create table dept(name varchar(20),
//id bigint not null auto_increment,
//primary key (id));
//
//create table role(name varchar(20),
//id bigint not null auto_increment,
//primary key (id));
//
//create table employee (name varchar(20),
//id bigint auto_increment,
//role_id bigint,
//doj date,
//dob date,
//gender ENUM('male', 'female', 'others'),
//reporting_to bigint,
//dept_id bigint,
//leave_available int,
//foreign key (reporting_to) references employee(id),
//foreign key (dept_id) references dept(id),
//foreign key (role_id) references role(id),
//primary key (id)
//);
//
//create table attendance (id bigint auto_increment,
//                         date date,
//                         employee_id bigint,
//                         hours_worked double,
//                         applied_leave boolean,
//                         first_check_in time,
//                         last_check_out time,
//                         primary key (id),
//foreign key (employee_id) references employee(id) on delete cascade );
//
//create table log (id bigint auto_increment,
//                  employee_id bigint,
//                  check_in time,
//                  check_out time,
//                  attendance_id bigint,
//                  primary key (id),
//foreign key (employee_id) references employee(id) ,
//foreign key (attendance_id) references attendance(id) on delete cascade );

@WebServlet("/employee/*")
public class EmployeeServlet extends HttpServlet {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

//      TODO search , pagination via params

//        String value = request.getParameter("name")

        JsonUtils.prepareResponse(response);
        JSONObject res;

        String path = request.getPathInfo();
        if(path == null || path.isEmpty() || path.equals("/")){
            // get all employees
            JSONArray jsArr = EmployeeModel.getAllEmployees();
            if(jsArr == null){
                res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving employees", "employees", null);
            }
            else {
                res = JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "no employees exist", "employees", jsArr);
            }
        }
        else {
            // get single employee
            res = EmployeeModel.getEmployeeById(request.getPathInfo().split("/")[1]);
            if(res == null){
                res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving employee", "employee", null);
            }
            else {
                res = JsonUtils.formatJSONObject("retrieved", (!res.isEmpty()), (!res.isEmpty()) ? "success" : "employee id doesnt exist", "employee", (!res.isEmpty()) ? res : null);
            }
        }
        response.getWriter().write(res.toString());

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
}