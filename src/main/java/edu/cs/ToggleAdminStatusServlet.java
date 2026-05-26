package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ToggleAdminStatusServlet")
public class ToggleAdminStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String adminId = request.getParameter("adminId");
        String active = request.getParameter("active");

        try (Connection conn = DBUtil.getConnection()) {

            String sql =
                "UPDATE admins SET active = ? WHERE id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setBoolean(1, Boolean.parseBoolean(active));
            ps.setString(2, adminId);

            ps.executeUpdate();

            response.sendRedirect("ManageAdminsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error updating admin account</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
            response.getWriter().write("<br><a href='ManageAdminsServlet'>Back</a>");
            e.printStackTrace();
        }
    }
}