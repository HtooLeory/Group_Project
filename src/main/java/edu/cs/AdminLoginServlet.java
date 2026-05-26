package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminLoginServlet")
public class AdminLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        response.setContentType("text/html");

        String sql =
            "SELECT username, password_salt, password_hash, role, active " +
            "FROM admins " +
            "WHERE username = ? AND active = TRUE";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedSalt = rs.getString("password_salt");
                String storedHash = rs.getString("password_hash");

                boolean passwordCorrect =
                    PasswordUtil.verifyPassword(password, storedSalt, storedHash);

                if (passwordCorrect) {
                    HttpSession session = request.getSession(true);

                    session.setAttribute("adminUser", rs.getString("username"));
                    session.setAttribute("adminRole", rs.getString("role"));

                    // Session expires after 15 minutes of inactivity
                    session.setMaxInactiveInterval(15 * 60);

                    response.sendRedirect("AdminDashboardServlet");
                    return;
                }
            }

            response.getWriter().write("<h2>Invalid username or password</h2>");
            response.getWriter().write("<a href='admin-login.html'>Try Again</a>");

        } catch (Exception e) {
            response.getWriter().write("<h2>Login Error</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            e.printStackTrace();
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}