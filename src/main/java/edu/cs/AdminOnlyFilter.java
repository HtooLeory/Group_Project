package edu.cs;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

public class AdminOnlyFilter implements Filter {

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

        if (!loggedIn) {
            httpResponse.sendRedirect(
                httpRequest.getContextPath() + "/admin-login.html"
            );
            return;
        }

        String role = (String) session.getAttribute("adminRole");

        if ("ADMIN".equals(role)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setContentType("text/html");
            httpResponse.getWriter().write("<h2>Access Denied</h2>");
            httpResponse.getWriter().write("<p>Only ADMIN users can manage Public Safety accounts.</p>");
            httpResponse.getWriter().write("<a href='AdminDashboardServlet'>Back to Dashboard</a>");
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}