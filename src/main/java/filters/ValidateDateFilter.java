package filters;


import java.io.*;
import java.util.Set;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

import filterUtils.FilterHelper;
import customUtils.Validator;
import org.json.JSONObject;

@WebFilter(filterName = "ValidateDateFilter")
public class ValidateDateFilter extends HttpFilter{

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        FilterHelper helper = new FilterHelper(request);
        Set<String> fields = helper.getBodyFilterFields("ValidateDateFilter");
        for(String field : fields){
            String value = ((JSONObject) request.getAttribute("requestBody")).optString(field, null);
            boolean valid = Validator.validateDate(value);
            System.out.println("Field: " + field + " Value: " + value + " Valid: " + valid);
            if(!valid){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new JSONObject().put("message", "Invalid date format for field " + field).toString());
                return;
            }
        }

        chain.doFilter(request, response);

    }

}
