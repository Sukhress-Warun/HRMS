package filters;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import filterUtils.FilterHelper;

@WebFilter(filterName = "AttendanceFilter")
public class AttendanceFilter extends HttpFilter{

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


    }
}
