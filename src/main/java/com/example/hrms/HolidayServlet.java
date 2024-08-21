package com.example.hrms;

import java.io.*;
import java.math.BigDecimal;
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
            JSONArray jsArr = HolidayModel.getAllHolidays();
            if(jsArr == null){
                res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving holidays", "holidays", null);
            }
            else {
                res = JsonUtils.formatJSONObject("retrieved", (!jsArr.isEmpty()), (!jsArr.isEmpty()) ? "success" : "no holidays exist", "holidays", jsArr);
            }
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
            if(res == null){
                res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving holiday", "holiday", null);
            }
            else {
                res = JsonUtils.formatJSONObject("retrieved", (!res.isEmpty()), (!res.isEmpty()) ? "success" : "holiday id doesnt exist", "holiday", (!res.isEmpty()) ? res : null);
            }

        }
        response.getWriter().write(res.toString());

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        try{
            Holiday holiday = gson.fromJson(request.getReader(), Holiday.class);
            // * id is auto generated
            holiday.setId(null);
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