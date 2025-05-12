package filters;

import org.json.JSONObject;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebFilter(filterName = "RequiredRequestBodyFilter")
public class RequiredRequestBodyFilter extends HttpFilter {

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String method = request.getMethod();
        if (method.equals("GET") || method.equals("DELETE")) {
            chain.doFilter(request, response);
            return;
        }
        if (request.getAttribute("requestBody") == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject().put("message", "Request body is required.").toString());
            return;
        }
        chain.doFilter(request, response);

    }
}
