package filters;

import customUtils.JsonUtils;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebFilter(filterName = "PrepareResponseFilter")
public class PrepareResponseFilter extends HttpFilter {

        public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            JsonUtils.prepareResponse(response);
            chain.doFilter(request, response);
        }

}
