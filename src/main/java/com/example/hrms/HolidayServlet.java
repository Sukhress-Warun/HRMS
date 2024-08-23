package com.example.hrms;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import com.google.gson.*;
import org.json.*;

import customUtils.*;
import models.*;
import entities.*;


//create table holiday (id bigint auto_increment,
//                      from_date date not null,
//                      to_date date not null,
//                      description varchar(100),
//primary key (id));

@WebServlet("/holiday/*")
public class HolidayServlet extends HttpServlet {

    static Gson gson = new GsonBuilder().serializeNulls().create();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        String path = request.getPathInfo();
        if(path == null || path.isEmpty() || path.equals("/")){
            // get all holidays

            String searchDescription = request.getParameter("search_description") != null ? request.getParameter("search_description") : "";
            int perPage = 10;
            int page = 1;
            try {
                perPage = Integer.parseInt(request.getParameter("per_page"));
                page = Integer.parseInt(request.getParameter("page"));
            }
            catch (NumberFormatException e) {}

            String sortCol = request.getParameter("sort_column") != null ? request.getParameter("sort_column") : "from_date";
            String sortOrder = request.getParameter("sort_order") != null ? request.getParameter("sort_order") : "asc";

            Set<String> validColumns = new HashSet<>(Arrays.asList("from_date", "to_date", "description"));
            if(!validColumns.contains(sortCol)){
                sortCol = "from_date";
            }
            if(!sortOrder.equals("asc") && !sortOrder.equals("desc")){
                sortOrder = "asc";
            }

            res = HolidayModel.getAllHolidays(perPage, page, sortCol, sortOrder, searchDescription);
            response.getWriter().write(res.toString());
            return;
        }
        else {
            BigDecimal id;
            try{
                id = new BigDecimal(request.getPathInfo().split("/")[1]);
            }
            catch (Exception e){
                res = JsonUtils.formatJSONObject("retrieved", false, "invalid holiday id", "holiday", null);
                response.getWriter().write(res.toString());
                return;
            }

            // get single holiday
            res = HolidayModel.getHolidayById(id);
            response.getWriter().write(res.toString());
            return;
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        try{
            Holiday holiday = gson.fromJson(request.getReader(), Holiday.class);
            // * id is auto generated
            holiday.setId(null);
            if(Date.valueOf(holiday.getToDate()).before(Date.valueOf(holiday.getFromDate()))){
                res = JsonUtils.formatJSONObject("added", false, "error adding holiday : to_date is before from_date", "holiday", null);
                response.getWriter().write(res.toString());
                return;
            }
            res = HolidayModel.addHoliday(holiday);

        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("added", false, "error adding holiday", "holiday", null);
        }

        response.getWriter().write(res.toString());

    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        Holiday holiday = gson.fromJson(request.getReader(), Holiday.class);
        holiday.setId(null);
        String path = request.getPathInfo();

        if(path == null || path.isEmpty() || path.equals("/")){
            res = JsonUtils.formatJSONObject("updated", false, "error updating holiday : no id specified in uri", "holiday", null);
            response.getWriter().write(res.toString());
            return;
        }
        try{
            holiday.setId(new BigDecimal(path.split("/")[1]));
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("updated", false, "error updating holiday : invalid id specified in uri", "holiday", null);
            response.getWriter().write(res.toString());
            return;
        }

        if(Date.valueOf(holiday.getToDate()).before(Date.valueOf(holiday.getFromDate()))){
            res = JsonUtils.formatJSONObject("updated", false, "error updating holiday : to_date is before from_date", "holiday", null);
            response.getWriter().write(res.toString());
            return;
        }
        res = HolidayModel.updateHoliday(holiday);
        response.getWriter().write(res.toString());
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        String path = request.getPathInfo();
        if(path == null || path.isEmpty() || path.equals("/")){
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting holiday : no id specified in uri", "holiday", null);
            response.getWriter().write(res.toString());
            return;
        }
        BigDecimal id;
        try{
            id = new BigDecimal(request.getPathInfo().split("/")[1]);
        }
        catch (Exception e){
            res = JsonUtils.formatJSONObject("deleted", false, "error deleting holiday : invalid id specified in uri", "holiday", null);
            response.getWriter().write(res.toString());
            return;
        }

        res = HolidayModel.deleteHoliday(id);
        response.getWriter().write(res.toString());
    }

}