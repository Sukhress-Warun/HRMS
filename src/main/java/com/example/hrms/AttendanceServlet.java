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
//                         worked_time time default '00:00:00',
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

    static Gson gson = new GsonBuilder().serializeNulls().create();

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

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String path = request.getPathInfo();
        try {
            if (path.equals("/status")) {
                status(request, response);
            }
            else if(path.equals("/calendar")){
                calendar(request, response);
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

    public static void status(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        BigDecimal id = null;
        try {
            id = new BigDecimal(request.getParameter("id"));
        } catch (Exception e) {
            res = JsonUtils.formatJSONObject("retreived", false, "id is required as a Number", "attendance", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }

        String date = request.getParameter("date");
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            res = JsonUtils.formatJSONObject("retrieved", false, (date != null) ? "date format is invalid" : "date is required", "attendance", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }
        date = Date.valueOf(date).toString();

//        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        Attendance attendance = AttendanceModel.getAttendance(id, date);
        if (attendance == null) {
            res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving attendance", "attendance", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }
        JSONObject status = AttendanceModel.getStatus(attendance.getId(), false);
        if (status == null) {
            res = JsonUtils.formatJSONObject("retrieved", false, "error retrieving status", "attendance", JSONObject.NULL);
            response.getWriter().write(res.toString());
            return;
        }

        JSONObject attendanceJSON = new JSONObject(gson.toJson(attendance));
        attendanceJSON.remove("id");
        attendanceJSON.remove("employee_id");
        if(status.get("last_check_out") == JSONObject.NULL){
            status.remove("last_check_out");
        }
        res = JsonUtils.formatJSONObject("retrieved", true, "success", "attendance", attendanceJSON.put("status", status));
        response.getWriter().write(res.toString());
    }

    public static void calendar(HttpServletRequest request, HttpServletResponse response) throws Exception {

        JsonUtils.prepareResponse(response);
        JSONObject res;

        BigDecimal id = null;
        try {
            id = new BigDecimal(request.getParameter("id"));
        } catch (Exception e) {
            res = JsonUtils.formatJSONObject("retrieved", false, "id is required as a Number", "calendar", new JSONArray());
            response.getWriter().write(res.toString());
            return;
        }

        String fromDate = request.getParameter("from_date");
        if (fromDate == null || !fromDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            res = JsonUtils.formatJSONObject("retrieved", false, (fromDate != null) ? "date format is invalid" : "date is required", "calendar", new JSONArray());
            response.getWriter().write(res.toString());
            return;
        }
        fromDate = Date.valueOf(fromDate).toString();

        String toDate = request.getParameter("to_date");
        if (toDate == null || !toDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            res = JsonUtils.formatJSONObject("retrieved", false, (toDate != null) ? "date format is invalid" : "date is required", "calendar", new JSONArray());
            response.getWriter().write(res.toString());
            return;
        }
        toDate = Date.valueOf(toDate).toString();

        if(Date.valueOf(toDate).before(Date.valueOf(fromDate))){
            res = JsonUtils.formatJSONObject("retrieved", false, "to_date is before from_date", "calendar", new JSONArray());
            response.getWriter().write(res.toString());
            return;
        }

        JSONArray attendance = AttendanceModel.getAttendanceBetweenDates(id, fromDate, toDate);
        JSONArray holidays = HolidayModel.getHolidaysBetweenDates(fromDate, toDate);
        JSONArray calendar = new JSONArray();

        String[] week = {"sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday"};
        // iterate through all dates
        Date date = Date.valueOf(fromDate);
        while(date.before(Date.valueOf(toDate)) || date.equals(Date.valueOf(toDate))){
            JSONObject day = new JSONObject();
            day.put("date", date.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            day.put("day", week[cal.get(Calendar.DAY_OF_WEEK) - 1]);
            String type = null;
            for(int i = 0; i < attendance.length(); i++){
                JSONObject att = attendance.getJSONObject(i);
                if(Date.valueOf(att.getString("date")).equals(date)){
                    type = (att.getBoolean("applied_leave")) ? "leave" : "present";
                    day.put("attendance", att);
                    break;
                }
            }
            if(type == null){
                for(int i = 0; i < holidays.length(); i++){
                    JSONObject hol = holidays.getJSONObject(i);
                    if((Date.valueOf(hol.getString("from_date")).before(date) && Date.valueOf(hol.getString("to_date")).after(date)) || Date.valueOf(hol.getString("from_date")).equals(date) || Date.valueOf(hol.getString("to_date")).equals(date)) {
                        type = "holiday";
                        day.put("holiday", hol);
                        break;
                    }
                }
            }
            if(type == null) {
                if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                    type = "weekend";
                }
                else {
                    type = "no-record";
                }
            }
            day.put("type", type);
            calendar.put(day);
            date = Date.valueOf(date.toLocalDate().plusDays(1));
        }

        res = JsonUtils.formatJSONObject("retrieved", true, "success", "calendar", calendar);
        response.getWriter().write(res.toString());
    }
}
