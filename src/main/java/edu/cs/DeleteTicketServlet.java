package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/DeleteTicketServlet")
public class DeleteTicketServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String ticketId =
            request.getParameter("ticketId");

        try (Connection conn = DBUtil.getConnection()) {

            String sql =
                "DELETE FROM lost_tickets WHERE ticket_id = ?";

            PreparedStatement ps =
                conn.prepareStatement(sql);

            ps.setString(1, ticketId);

            ps.executeUpdate();

            response.sendRedirect("ViewTicketsServlet");

        } catch (Exception e) {

            response.setContentType("text/html");

            response.getWriter().write(
                "<h2>Error deleting ticket</h2>"
            );

            response.getWriter().write(
                "<pre>" + e.getMessage() + "</pre>"
            );
        }
    }
}