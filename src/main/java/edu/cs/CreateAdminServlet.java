package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/CreateAdminServlet")
public class CreateAdminServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        response.setContentType("text/html");

        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {

            response.getWriter().write("<h2>Username and password are required.</h2>");
            response.getWriter().write("<a href='ManageAdminsServlet'>Back</a>");
            return;
        }

        try (Connection conn = DBUtil.getConnection()) {

            String salt = PasswordUtil.generateSalt();
            String hash = PasswordUtil.hashPassword(password, salt);

            String sql =
                "INSERT INTO admins " +
                "(username, password_salt, password_hash, role, active) " +
                "VALUES (?, ?, ?, ?, TRUE)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, username.trim());
            ps.setString(2, salt);
            ps.setString(3, hash);
            ps.setString(4, role);

            ps.executeUpdate();

            response.sendRedirect("ManageAdminsServlet");

        } catch (Exception e) {
            response.getWriter().write("<h2>Error creating admin account</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            response.getWriter().write("<br><a href='ManageAdminsServlet'>Back</a>");
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