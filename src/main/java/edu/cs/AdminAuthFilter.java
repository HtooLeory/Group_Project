package edu.cs;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class AdminAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No setup needed
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
            (HttpServletRequest) request;

        HttpServletResponse httpResponse =
            (HttpServletResponse) response;

        HttpSession session =
            httpRequest.getSession(false);

        boolean loggedIn =
            session != null &&
            session.getAttribute("adminUser") != null;

        if (loggedIn) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(
                httpRequest.getContextPath() + "/admin-login.html"
            );
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}