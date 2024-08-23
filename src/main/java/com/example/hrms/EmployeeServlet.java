package com.example.hrms;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
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
            JSONObject res = JsonUtils.formatJSONObject("error", true, "error occurred", null, null);
            response.getWriter().write(res.toString());
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        Employee employee = gson.fromJson(request.getReader(), Employee.class);

        // * id is auto generated
        employee.setId(null);

        res = EmployeeModel.addEmployee(employee);

        response.getWriter().write(res.toString());

    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        Employee employee = gson.fromJson(request.getReader(), Employee.class);
        employee.setId(null);
        String path = request.getPathInfo();

        try{
            employee.setId(new BigDecimal(path.split("/")[1]));
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("updated", false, "id is required as number", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }

        res = EmployeeModel.updateEmployee(employee);

        response.getWriter().write(res.toString());

    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        String path = request.getPathInfo();

        BigDecimal id;
        try{
            id = new BigDecimal(path.split("/")[1]);
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("deleted", false, "id required as number", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }

        res = EmployeeModel.deleteEmployee(id);

        response.getWriter().write(res.toString());

    }

    public void getAllEmployees(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        String searchName = request.getParameter("search_name") != null ? request.getParameter("search_name") : "";
        int perPage = 10;
        int page = 1;
        try {
            perPage = request.getParameter("per_page") != null ? Integer.parseInt(request.getParameter("per_page")) : 10;
            page = request.getParameter("page") != null ? Integer.parseInt(request.getParameter("page")) : 1;
        } catch (NumberFormatException e) {}
        String sortCol = request.getParameter("sort_column") != null ? request.getParameter("sort_column") : "name";
        String sortOrder = request.getParameter("sort_order") != null ? request.getParameter("sort_order") : "asc";

        Set<String> validColumns = new HashSet<>(Arrays.asList("name", "doj", "dob", "available_leave"));
        if(!validColumns.contains(sortCol)){
            sortCol = "name";
        }
        if(!sortOrder.equals("asc") && !sortOrder.equals("desc")){
            sortOrder = "asc";
        }


        // get all employees
        res = EmployeeModel.getAllEmployees(searchName, perPage, page, sortCol, sortOrder);
        response.getWriter().write(res.toString());

    }

    public void getEmployeeById(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        String path = request.getPathInfo();

        BigDecimal id;
        try{
            id = new BigDecimal(path.split("/")[1]);
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("retrieved", false, "id is required as number", "employee", null);
            response.getWriter().write(res.toString());
            return;
        }

        // get single employee
        res = EmployeeModel.getEmployeeById(id);
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
            res = JsonUtils.formatJSONObject("retrieved", false, "id is required as number", "higher", null);
            response.getWriter().write(res.toString());
            return;
        }

        res = EmployeeModel.getReportingToEmployees(id);

        response.getWriter().write(res.toString());

    }
}