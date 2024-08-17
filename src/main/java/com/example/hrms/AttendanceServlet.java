package com.example.hrms;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import org.json.*;
import com.google.gson.*;

import entities.*;
import models.*;
import customUtils.*;


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

@WebServlet("/attendance/*")
public class AttendanceServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getPathInfo();
        try {
            if (path.equals("/check-in")) {
                checkIn(request, response);
                return;
            } else if (path.equals("/check-out")) {
                checkOut(request, response);
                return;
            } else if (path.equals("/apply-leave")) {
                applyLeave(request, response);
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            JsonUtils.prepareResponse(response);
            response.getWriter().write(JsonUtils.formatJSONObject("error", true, "error occurred", null, null).toString());
            return;
        }

    }

    public static void checkIn(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonUtils.prepareResponse(response);
        JSONObject res = null;

        JSONObject data = JsonUtils.getRequestJSONObject(request);
        BigDecimal id;
        id = data.optBigDecimal("id", null);
        if (id == null) {
            res = JsonUtils.formatJSONObject("checked-in", false, "id is required as a Number", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }
        String date = data.optString("date", null);
        if(date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")){
            res = JsonUtils.formatJSONObject("checked-in", false, (date != null) ? "date format is invalid" : "date is required", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }
        date = Date.valueOf(date).toString();

//        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        String time = data.optString("time", null);
        if(time == null || !time.matches("\\d{2}:\\d{2}:\\d{2}")){
            res = JsonUtils.formatJSONObject("checked-in", false, (time != null) ? "time format is invalid" : "time is required", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }
        time = Time.valueOf(time).toString();

//        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

        res = AttendanceModel.checkIn(id, date, time);
        if(res == null){
            res = JsonUtils.formatJSONObject("checked-in", false, "error checking in", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            return;
        }
        response.getWriter().write(res.toString());

    }

    public static void checkOut(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        JSONObject data = JsonUtils.getRequestJSONObject(request);
        BigDecimal id;
        id = data.optBigDecimal("id", null);
        if (id == null) {
            res = JsonUtils.formatJSONObject("checked-out", false, "id is required as a Number", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }

        String date = data.optString("date", null);
        if(date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")){
            res = JsonUtils.formatJSONObject("checked-in", false, (date != null) ? "date format is invalid" : "date is required", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }
        date = Date.valueOf(date).toString();

//        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        String time = data.optString("time", null);
        if(time == null || !time.matches("\\d{2}:\\d{2}:\\d{2}")){
            res = JsonUtils.formatJSONObject("checked-in", false, (time != null) ? "time format is invalid" : "time is required", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            response.getWriter().write(res.toString());
            return;
        }
        time = Time.valueOf(time).toString();

//        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());

        res = AttendanceModel.checkOut(id, date, time);
        if(res == null){
            res = JsonUtils.formatJSONObject("checked-out", false, "error checking out", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
            return;
        }
        response.getWriter().write(res.toString());

    }

    public static void applyLeave(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        JSONObject data = JsonUtils.getRequestJSONObject(request);
        BigDecimal id;

        id = data.optBigDecimal("id", null);
        if (id == null) {
            res = JsonUtils.formatJSONObject("applied_leave", false, "id is required as a Number", "date", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }

        String date = data.optString("date", null);
        if(date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")){
            res = JsonUtils.formatJSONObject("applied-leave", false, (date != null) ? "date format is invalid" : "date is required", "date", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }
        date = Date.valueOf(date).toString();
//        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        res = AttendanceModel.applyLeave(id, date);
        if(res == null){
            res = JsonUtils.formatJSONObject("applied-leave", false, "error applying leave", "date", JSONObject.NULL);
            return;
        }
        response.getWriter().write(res.toString());


    }

}
