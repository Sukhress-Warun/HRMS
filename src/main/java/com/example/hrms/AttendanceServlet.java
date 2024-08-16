package com.example.hrms;


import java.io.*;
import java.math.BigDecimal;
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
        if(path.equals("/check-in")){
            checkIn(request, response);
            return;
        }
        else if (path.equals("/check-out")){
            checkOut(request, response);
            return;
        }
        else if (path.equals("/apply-leave")){
            applyLeave(request, response);
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
//        String date = data.optString("date", null);
//        if(date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")){
//            res = JsonUtils.formatJSONObject("checked-in", false, (date != null) ? "date format is invalid" : "date is required", "log", new JSONObject().put("date", JSONObject.NULL).put("time", JSONObject.NULL));
//            response.getWriter().write(res.toString());
//            return;
//        }

        String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String time = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());




    }

    public static void checkOut(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

    public static void applyLeave(HttpServletRequest request, HttpServletResponse response) throws IOException {

    }

}
