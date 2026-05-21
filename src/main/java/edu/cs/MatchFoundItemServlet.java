package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/MatchFoundItemServlet")
public class MatchFoundItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String ticketId = request.getParameter("ticketId");

        response.setContentType("text/html");

        try (Connection conn = DBUtil.getConnection()) {

            String ticketSql = "SELECT * FROM lost_tickets WHERE ticket_id = ?";
            PreparedStatement ticketPs = conn.prepareStatement(ticketSql);
            ticketPs.setString(1, ticketId);
            ResultSet ticketRs = ticketPs.executeQuery();

            if (!ticketRs.next()) {
                response.getWriter().write("<h2>Ticket not found.</h2>");
                return;
            }

            String itemType = ticketRs.getString("item_type");

            response.getWriter().write("<html><head>");
            response.getWriter().write("<title>Find Match</title>");
            response.getWriter().write("<link rel='stylesheet' href='style.css'>");
            response.getWriter().write("</head><body>");

            response.getWriter().write("<div class='container'>");
            response.getWriter().write("<h1>Find Matching Found Item</h1>");

            response.getWriter().write("<h2>Matching Ticket</h2>");
            response.getWriter().write("<p><b>Student:</b> " + escapeHtml(ticketRs.getString("student_name")) + "</p>");
            response.getWriter().write("<p><b>Item:</b> " + escapeHtml(itemType) + "</p>");
            response.getWriter().write("<p><b>Description:</b> " + escapeHtml(ticketRs.getString("description")) + "</p>");
            response.getWriter().write("<p><b>Lost Location:</b> " + escapeHtml(ticketRs.getString("lost_location")) + "</p>");
            response.getWriter().write("<p><b>Lost Date:</b> " + escapeHtml(ticketRs.getString("lost_date")) + "</p>");

            response.getWriter().write("<br><h2>Possible Found Item Matches</h2>");

            response.getWriter().write("<div class='table-box'><table>");
            response.getWriter().write("<tr>");
            response.getWriter().write("<th>ID</th>");
            response.getWriter().write("<th>Item Type</th>");
            response.getWriter().write("<th>Description</th>");
            response.getWriter().write("<th>Found Location</th>");
            response.getWriter().write("<th>Found Date</th>");
            response.getWriter().write("<th>Status</th>");
            response.getWriter().write("<th>Action</th>");
            response.getWriter().write("</tr>");

            String foundSql =
                "SELECT * FROM found_items " +
                "WHERE item_type LIKE ? " +
                "AND status <> 'Matched' " +
                "AND status <> 'Claimed' " +
                "ORDER BY id ASC";

            PreparedStatement foundPs = conn.prepareStatement(foundSql);
            foundPs.setString(1, "%" + itemType + "%");

            ResultSet foundRs = foundPs.executeQuery();

            boolean foundAny = false;

            while (foundRs.next()) {
                foundAny = true;

                int foundItemId = foundRs.getInt("id");

                response.getWriter().write("<tr>");
                response.getWriter().write("<td>" + foundItemId + "</td>");
                response.getWriter().write("<td>" + escapeHtml(foundRs.getString("item_type")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(foundRs.getString("description")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(foundRs.getString("found_location")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(foundRs.getString("found_date")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(foundRs.getString("status")) + "</td>");

                response.getWriter().write("<td>");
                response.getWriter().write("<form action='MatchItemServlet' method='post'>");
                response.getWriter().write("<input type='hidden' name='ticketId' value='" + ticketId + "'>");
                response.getWriter().write("<input type='hidden' name='foundItemId' value='" + foundItemId + "'>");
                response.getWriter().write("<button type='submit'>Match This Item</button>");
                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                response.getWriter().write("</tr>");
            }

            if (!foundAny) {
                response.getWriter().write("<tr><td colspan='7'>No possible matches found.</td></tr>");
            }

            response.getWriter().write("</table></div>");

            response.getWriter().write("<br><a href='ViewTicketsServlet'>Back to Tickets</a>");
            response.getWriter().write("<br><br><a href='AdminDashboardServlet'>Back to Dashboard</a>");

            response.getWriter().write("</div></body></html>");

        } catch (Exception e) {
            response.getWriter().write("<h2>Error</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            e.printStackTrace();
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;");
    }
}