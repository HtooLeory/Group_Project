package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/TicketDetailsServlet")
public class TicketDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String ticketId = request.getParameter("ticketId");

        response.getWriter().write("<html><head>");
        response.getWriter().write("<title>Ticket Details</title>");
        response.getWriter().write("<link rel='stylesheet' href='style.css'>");
        response.getWriter().write("</head><body>");

        response.getWriter().write("<div class='container'>");
        response.getWriter().write("<h1>Lost Ticket Details</h1>");

        String sql =
            "SELECT lt.*, fi.item_type AS found_item_type, " +
            "fi.description AS found_description, " +
            "fi.found_location, fi.found_date, fi.status AS found_status " +
            "FROM lost_tickets lt " +
            "LEFT JOIN found_items fi ON lt.matched_found_item_id = fi.id " +
            "WHERE lt.ticket_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticketId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                String filePath = rs.getString("proof_file_path");
                Object matchedItemId = rs.getObject("matched_found_item_id");

                response.getWriter().write("<h2>Student Information</h2>");
                response.getWriter().write("<p><b>Ticket ID:</b> " + rs.getInt("ticket_id") + "</p>");
                response.getWriter().write("<p><b>Full Name:</b> " + escapeHtml(rs.getString("student_name")) + "</p>");
                response.getWriter().write("<p><b>Student ID / EMPLID:</b> " + escapeHtml(rs.getString("student_id")) + "</p>");
                response.getWriter().write("<p><b>Email:</b> " + escapeHtml(rs.getString("student_email")) + "</p>");
                response.getWriter().write("<p><b>Phone:</b> " + escapeHtml(rs.getString("phone")) + "</p>");

                response.getWriter().write("<hr>");

                response.getWriter().write("<h2>Lost Item Information</h2>");
                response.getWriter().write("<p><b>Item Type:</b> " + escapeHtml(rs.getString("item_type")) + "</p>");
                response.getWriter().write("<p><b>Description:</b> " + escapeHtml(rs.getString("description")) + "</p>");
                response.getWriter().write("<p><b>Lost Location:</b> " + escapeHtml(rs.getString("lost_location")) + "</p>");
                response.getWriter().write("<p><b>Lost Date:</b> " + escapeHtml(rs.getString("lost_date")) + "</p>");
                response.getWriter().write("<p><b>Ticket Status:</b> " + escapeHtml(rs.getString("status")) + "</p>");
                response.getWriter().write("<p><b>Submitted At:</b> " + escapeHtml(rs.getString("submitted_at")) + "</p>");

                if (filePath != null && !filePath.isEmpty()) {
                    response.getWriter().write("<p><b>Uploaded Proof:</b> <a href='" + escapeHtml(filePath) + "' target='_blank'>View File</a></p>");
                } else {
                    response.getWriter().write("<p><b>Uploaded Proof:</b> No file uploaded</p>");
                }

                response.getWriter().write("<hr>");

                response.getWriter().write("<h2>Matched Found Item</h2>");

                if (matchedItemId != null) {
                    response.getWriter().write("<p><b>Matched Found Item ID:</b> " + matchedItemId + "</p>");
                    response.getWriter().write("<p><b>Found Item Type:</b> " + escapeHtml(rs.getString("found_item_type")) + "</p>");
                    response.getWriter().write("<p><b>Found Description:</b> " + escapeHtml(rs.getString("found_description")) + "</p>");
                    response.getWriter().write("<p><b>Found Location:</b> " + escapeHtml(rs.getString("found_location")) + "</p>");
                    response.getWriter().write("<p><b>Found Date:</b> " + escapeHtml(rs.getString("found_date")) + "</p>");
                    response.getWriter().write("<p><b>Found Item Status:</b> " + escapeHtml(rs.getString("found_status")) + "</p>");
                    response.getWriter().write("<p><a href='ViewFoundItemsServlet?itemId=" + matchedItemId + "'>Open Matched Found Item</a></p>");
                } else {
                    response.getWriter().write("<p>This ticket has not been matched with a found item yet.</p>");
                    response.getWriter().write("<p><a href='MatchFoundItemServlet?ticketId=" + ticketId + "'>Find Match</a></p>");
                }

                response.getWriter().write("<hr>");

                response.getWriter().write("<h2>Update Ticket Status</h2>");

                response.getWriter().write("<form action='UpdateTicketStatusServlet' method='post'>");
                response.getWriter().write("<input type='hidden' name='ticketId' value='" + ticketId + "'>");

                String currentStatus = rs.getString("status");

                response.getWriter().write("<select name='status'>");
                response.getWriter().write("<option value='Pending' " + selected(currentStatus, "Pending") + ">Pending</option>");
                response.getWriter().write("<option value='Matched' " + selected(currentStatus, "Matched") + ">Matched</option>");
                response.getWriter().write("<option value='Released' " + selected(currentStatus, "Released") + ">Released</option>");
                response.getWriter().write("<option value='Rejected' " + selected(currentStatus, "Rejected") + ">Rejected</option>");
                response.getWriter().write("</select>");

                response.getWriter().write("<button type='submit'>Update Status</button>");
                response.getWriter().write("</form>");

            } else {
                response.getWriter().write("<h2>Ticket not found.</h2>");
            }

        } catch (Exception e) {
            response.getWriter().write("<h2>Error loading ticket details</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            e.printStackTrace();
        }

        response.getWriter().write("<br><a class='back-link' href='ViewTicketsServlet'>Back to Review Tickets</a>");
        response.getWriter().write("<br><br><a class='back-link' href='AdminDashboardServlet'>Back to Dashboard</a>");

        response.getWriter().write("</div>");
        response.getWriter().write("</body></html>");
    }

    private String selected(String current, String value) {
        if (current != null && current.equals(value)) {
            return "selected";
        }
        return "";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}