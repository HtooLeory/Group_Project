package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DeleteFoundItemServlet")
public class DeleteFoundItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = request.getParameter("itemId");

        try (Connection conn = DBUtil.getConnection()) {

            // First disconnect any lost tickets matched to this found item
            String unmatchTicketsSql =
                "UPDATE lost_tickets " +
                "SET matched_found_item_id = NULL, status = 'Pending' " +
                "WHERE matched_found_item_id = ?";

            PreparedStatement unmatchPs = conn.prepareStatement(unmatchTicketsSql);
            unmatchPs.setString(1, itemId);
            unmatchPs.executeUpdate();

            // Then delete the found item
            String deleteFoundItemSql =
                "DELETE FROM found_items WHERE id = ?";

            PreparedStatement deletePs = conn.prepareStatement(deleteFoundItemSql);
            deletePs.setString(1, itemId);
            deletePs.executeUpdate();

            response.sendRedirect("ViewFoundItemsServlet");

        } catch (Exception e) {

            response.setContentType("text/html");

            response.getWriter().write("<h2>Error deleting found item</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            response.getWriter().write("<br><a href='ViewFoundItemsServlet'>Back to Found Items</a>");

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