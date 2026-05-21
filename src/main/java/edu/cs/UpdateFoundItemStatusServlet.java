package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdateFoundItemStatusServlet")
public class UpdateFoundItemStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = request.getParameter("itemId");
        String status = request.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {

            String updateFoundSql =
                "UPDATE found_items SET status = ? WHERE id = ?";

            PreparedStatement foundPs = conn.prepareStatement(updateFoundSql);
            foundPs.setString(1, status);
            foundPs.setString(2, itemId);
            foundPs.executeUpdate();

            String ticketStatus = status;

            if (status.equals("Available")) {
                ticketStatus = "Pending";
            }

            String updateTicketSql =
                "UPDATE lost_tickets SET status = ? WHERE matched_found_item_id = ?";

            PreparedStatement ticketPs = conn.prepareStatement(updateTicketSql);
            ticketPs.setString(1, ticketStatus);
            ticketPs.setString(2, itemId);
            ticketPs.executeUpdate();

            response.sendRedirect("ViewFoundItemsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error updating found item status</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace();
        }
    }
}