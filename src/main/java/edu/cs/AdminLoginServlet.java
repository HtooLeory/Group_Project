package edu.cs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (
            (username.equals("publicsafety") && password.equals("qc123")) ||
            (username.equals("admin1") && password.equals("admin123")) ||
            (username.equals("security") && password.equals("pass456"))
        ) {

            HttpSession session = request.getSession();
            session.setAttribute("adminUser", username);

            response.sendRedirect("AdminDashboardServlet");

        } else {
            response.setContentType("text/html");

            response.getWriter().write("<h2>Invalid username or password</h2>");
            response.getWriter().write("<a href='admin-login.html'>Try Again</a>");
        }
    }
}