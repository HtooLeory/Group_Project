package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddFoundItemServlet")
public class AddFoundItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String itemType = request.getParameter("itemType");
        String description = request.getParameter("description");
        String foundLocation = request.getParameter("foundLocation");
        String foundDate = request.getParameter("foundDate");
        String status = request.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {

            String sql =
                "INSERT INTO found_items " +
                "(item_type, description, found_location, found_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, itemType);
            ps.setString(2, description);
            ps.setString(3, foundLocation);

            if (foundDate == null || foundDate.trim().isEmpty()) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setString(4, foundDate);
            }

            if (status == null || status.trim().isEmpty()) {
                ps.setString(5, "Available");
            } else {
                ps.setString(5, status);
            }

            ps.executeUpdate();

            response.getWriter().write("<h2>Found item added successfully!</h2>");
            response.getWriter().write("<p>The item was saved in the database.</p>");
            response.getWriter().write("<a href='ViewFoundItemsServlet'>View Found Items</a>");
            response.getWriter().write("<br><br>");
            response.getWriter().write("<a href='AdminDashboardServlet'>Back to Dashboard</a>");

        } catch (Exception e) {
            response.getWriter().write("<h2>Error adding found item</h2>");
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