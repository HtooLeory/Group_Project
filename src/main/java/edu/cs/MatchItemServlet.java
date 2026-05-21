package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/MatchItemServlet")
public class MatchItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ticketId = request.getParameter("ticketId");
        String foundItemId = request.getParameter("foundItemId");

        try (Connection conn = DBUtil.getConnection()) {

            String updateTicketSql =
                "UPDATE lost_tickets " +
                "SET matched_found_item_id = ?, status = 'Matched' " +
                "WHERE ticket_id = ?";

            PreparedStatement ticketPs = conn.prepareStatement(updateTicketSql);
            ticketPs.setString(1, foundItemId);
            ticketPs.setString(2, ticketId);
            ticketPs.executeUpdate();

            String updateFoundSql =
                "UPDATE found_items " +
                "SET status = 'Matched' " +
                "WHERE id = ?";

            PreparedStatement foundPs = conn.prepareStatement(updateFoundSql);
            foundPs.setString(1, foundItemId);
            foundPs.executeUpdate();

            response.sendRedirect("ViewTicketsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error matching item</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace();
        }
    }
}