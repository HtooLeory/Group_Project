package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

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

            String sql = "UPDATE lost_tickets SET status = ? WHERE ticket_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, ticketId);

            ps.executeUpdate();

            response.sendRedirect("ViewTicketsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error updating ticket</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
        }
    }
}