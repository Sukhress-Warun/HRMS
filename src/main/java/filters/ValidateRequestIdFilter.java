package filters;

import customUtils.JsonUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebFilter(filterName = "ValidateRequestIdFilter")
public class ValidateRequestIdFilter extends HttpFilter {

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {


        BigDecimal id = ((JSONObject) request.getAttribute("requestBody")).optBigDecimal("id", null);
        if (id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("message", "id is required as Number").toString());
            return;
        }
        chain.doFilter(request, response);

    }
}
