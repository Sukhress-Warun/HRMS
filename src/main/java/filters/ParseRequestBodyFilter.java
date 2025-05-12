package filters;

import customUtils.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebFilter(filterName = "ParseRequestBodyFilter")
public class ParseRequestBodyFilter extends HttpFilter {

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        JSONObject requestBody;
        try {
            requestBody = JsonUtils.parseRequestBody(request);
        }
        catch (JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("message", "Invalid JSON").toString());
            return;
        }
        request.setAttribute("requestBody", requestBody);
        System.out.println("Request Body: " + (requestBody == null ? "null" : requestBody.toString()));
        chain.doFilter(request, response);
    }
}

