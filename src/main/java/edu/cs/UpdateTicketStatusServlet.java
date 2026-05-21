package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdateTicketStatusServlet")
public class UpdateTicketStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ticketId = request.getParameter("ticketId");
        String status = request.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {

            String getMatchedItemSql =
                "SELECT matched_found_item_id FROM lost_tickets WHERE ticket_id = ?";

            PreparedStatement getPs = conn.prepareStatement(getMatchedItemSql);
            getPs.setString(1, ticketId);

            ResultSet rs = getPs.executeQuery();

            Integer matchedFoundItemId = null;

            if (rs.next()) {
                Object matchedValue = rs.getObject("matched_found_item_id");

                if (matchedValue != null) {
                    matchedFoundItemId = rs.getInt("matched_found_item_id");
                }
            }

            String updateTicketSql =
                "UPDATE lost_tickets SET status = ? WHERE ticket_id = ?";

            PreparedStatement ticketPs = conn.prepareStatement(updateTicketSql);
            ticketPs.setString(1, status);
            ticketPs.setString(2, ticketId);
            ticketPs.executeUpdate();

            if (matchedFoundItemId != null) {

                String foundItemStatus = status;

                if (status.equals("Rejected")) {
                    foundItemStatus = "Available";
                }

                String updateFoundItemSql =
                    "UPDATE found_items SET status = ? WHERE id = ?";

                PreparedStatement foundPs = conn.prepareStatement(updateFoundItemSql);
                foundPs.setString(1, foundItemStatus);
                foundPs.setInt(2, matchedFoundItemId);
                foundPs.executeUpdate();
            }

            response.sendRedirect("ViewTicketsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error updating ticket status</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace();
        }
    }
}