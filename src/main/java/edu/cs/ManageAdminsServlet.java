package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ManageAdminsServlet")
public class ManageAdminsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        response.getWriter().write("<html><head>");
        response.getWriter().write("<title>Manage Admin Accounts</title>");
        response.getWriter().write("<link rel='stylesheet' href='style.css'>");
        response.getWriter().write("</head><body>");

        response.getWriter().write("<div class='container'>");

        response.getWriter().write("<h1>Manage Public Safety Accounts</h1>");
        response.getWriter().write("<p>Create, revoke, or reactivate Public Safety admin accounts.</p>");

        response.getWriter().write("<h2>Create New Admin or Public Safety Account</h2>");

        response.getWriter().write("<form action='CreateAdminServlet' method='post'>");

        response.getWriter().write("<label>Username</label>");
        response.getWriter().write("<input type='text' name='username' required>");

        response.getWriter().write("<label>Password</label>");
        response.getWriter().write("<input type='password' name='password' required>");

        response.getWriter().write("<label>Role</label>");
        response.getWriter().write("<select name='role'>");
        response.getWriter().write("<option value='PUBLIC_SAFETY'>PUBLIC_SAFETY</option>");
        response.getWriter().write("<option value='ADMIN'>ADMIN</option>");
        response.getWriter().write("</select>");

        response.getWriter().write("<input type='submit' value='Create Admin Account'>");

        response.getWriter().write("</form>");

        response.getWriter().write("<br><hr><br>");

        response.getWriter().write("<h2>Existing Admin Accounts</h2>");

        response.getWriter().write("<div class='table-box'>");
        response.getWriter().write("<table>");

        response.getWriter().write("<tr>");
        response.getWriter().write("<th>ID</th>");
        response.getWriter().write("<th>Username</th>");
        response.getWriter().write("<th>Role</th>");
        response.getWriter().write("<th>Status</th>");
        response.getWriter().write("<th>Created At</th>");
        response.getWriter().write("<th>Action</th>");
        response.getWriter().write("</tr>");

        String sql =
            "SELECT id, username, role, active, created_at " +
            "FROM admins " +
            "ORDER BY id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("id");
                boolean active = rs.getBoolean("active");

                response.getWriter().write("<tr>");
                response.getWriter().write("<td>" + id + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("username")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("role")) + "</td>");

                if (active) {
                    response.getWriter().write("<td>Active</td>");
                } else {
                    response.getWriter().write("<td>Revoked</td>");
                }

                response.getWriter().write("<td>" + escapeHtml(rs.getString("created_at")) + "</td>");

                response.getWriter().write("<td>");
                response.getWriter().write("<form action='ToggleAdminStatusServlet' method='post'>");
                response.getWriter().write("<input type='hidden' name='adminId' value='" + id + "'>");

                if (active) {
                    response.getWriter().write("<input type='hidden' name='active' value='false'>");
                    response.getWriter().write("<button type='submit'>Revoke</button>");
                } else {
                    response.getWriter().write("<input type='hidden' name='active' value='true'>");
                    response.getWriter().write("<button type='submit'>Reactivate</button>");
                }

                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                response.getWriter().write("</tr>");
            }

        } catch (Exception e) {
            response.getWriter().write("<tr><td colspan='6'>Error: "
                    + escapeHtml(e.getMessage()) + "</td></tr>");
            e.printStackTrace();
        }

        response.getWriter().write("</table>");
        response.getWriter().write("</div>");

        response.getWriter().write("<br><a class='back-link' href='AdminDashboardServlet'>Back to Dashboard</a>");
        response.getWriter().write("<br><br><a class='back-link' href='LogoutServlet'>Logout</a>");

        response.getWriter().write("</div>");
        response.getWriter().write("</body></html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}