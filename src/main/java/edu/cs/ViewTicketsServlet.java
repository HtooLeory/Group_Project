package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ViewTicketsServlet")
public class ViewTicketsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String search = request.getParameter("search");
        String statusFilter = request.getParameter("status");

        if (search == null) search = "";
        if (statusFilter == null) statusFilter = "";

        response.getWriter().write("<html><head>");
        response.getWriter().write("<title>Review Lost Tickets</title>");
        response.getWriter().write("<link rel='stylesheet' href='style.css'>");
        response.getWriter().write("</head><body>");

        response.getWriter().write("<div class='container'>");

        response.getWriter().write("<h1>Review Lost Item Tickets</h1>");
        response.getWriter().write("<p>These tickets are submitted by students.</p>");

        // Search and filter form
        response.getWriter().write("<form method='get' action='ViewTicketsServlet'>");

        response.getWriter().write("<label>Search Ticket</label>");
        response.getWriter().write(
            "<input type='text' name='search' " +
            "placeholder='Search by ticket ID, student, item, email, or location' " +
            "value='" + escapeHtml(search) + "'>"
        );

        response.getWriter().write("<label>Filter by Status</label>");
        response.getWriter().write("<select name='status'>");
        response.getWriter().write("<option value=''>All</option>");
        response.getWriter().write("<option value='Pending' " + selected(statusFilter, "Pending") + ">Pending</option>");
        response.getWriter().write("<option value='Matched' " + selected(statusFilter, "Matched") + ">Matched</option>");
        response.getWriter().write("<option value='Released' " + selected(statusFilter, "Released") + ">Released</option>");
        response.getWriter().write("<option value='Rejected' " + selected(statusFilter, "Rejected") + ">Rejected</option>");
        response.getWriter().write("</select>");

        response.getWriter().write("<input type='submit' value='Search / Filter'>");
        response.getWriter().write("</form>");

        response.getWriter().write("<br>");

        // Table
        response.getWriter().write("<div class='table-box'>");
        response.getWriter().write("<table>");

        response.getWriter().write("<tr>");
        response.getWriter().write("<th>Ticket ID</th>");
        response.getWriter().write("<th>Details</th>");
        response.getWriter().write("<th>Student</th>");
        response.getWriter().write("<th>Item</th>");
        response.getWriter().write("<th>Location</th>");
        response.getWriter().write("<th>File</th>");
        response.getWriter().write("<th>Status</th>");
        response.getWriter().write("<th>Matched Found Item ID</th>");
        response.getWriter().write("<th>Find Match</th>");
        response.getWriter().write("<th>Update</th>");
        response.getWriter().write("<th>Delete</th>");
        response.getWriter().write("</tr>");

        String sql =
            "SELECT * FROM lost_tickets " +
            "WHERE (" +
            "CAST(ticket_id AS CHAR) LIKE ? OR " +
            "student_name LIKE ? OR " +
            "student_email LIKE ? OR " +
            "item_type LIKE ? OR " +
            "description LIKE ? OR " +
            "lost_location LIKE ?" +
            ") ";

        if (!statusFilter.isEmpty()) {
            sql += "AND status = ? ";
        }

        sql += "ORDER BY ticket_id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String pattern = "%" + search + "%";

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            ps.setString(5, pattern);
            ps.setString(6, pattern);

            if (!statusFilter.isEmpty()) {
                ps.setString(7, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                int ticketId = rs.getInt("ticket_id");
                String currentStatus = rs.getString("status");
                String filePath = rs.getString("proof_file_path");
                Object matchedItemId = rs.getObject("matched_found_item_id");

                response.getWriter().write("<tr>");

                // Ticket ID
                response.getWriter().write("<td>" + ticketId + "</td>");

                // Details button/link
                response.getWriter().write(
                    "<td><a href='TicketDetailsServlet?ticketId=" + ticketId + "'>View Details</a></td>"
                );

                // Student name
                response.getWriter().write(
                    "<td>" + escapeHtml(rs.getString("student_name")) + "</td>"
                );

                // Item type
                response.getWriter().write(
                    "<td>" + escapeHtml(rs.getString("item_type")) + "</td>"
                );

                // Lost location
                response.getWriter().write(
                    "<td>" + escapeHtml(rs.getString("lost_location")) + "</td>"
                );

                // Uploaded file
                if (filePath != null && !filePath.isEmpty()) {
                    response.getWriter().write(
                        "<td><a href='" + escapeHtml(filePath) + "' target='_blank'>View File</a></td>"
                    );
                } else {
                    response.getWriter().write("<td>No file</td>");
                }

                // Status
                response.getWriter().write(
                    "<td>" + escapeHtml(currentStatus) + "</td>"
                );

                // Matched found item ID
                if (matchedItemId != null) {
                    response.getWriter().write(
                        "<td><a href='ViewFoundItemsServlet?itemId=" + matchedItemId + "'>" +
                        matchedItemId +
                        "</a></td>"
                    );
                } else {
                    response.getWriter().write("<td>Not matched</td>");
                }

                // Find Match
                response.getWriter().write("<td>");
                response.getWriter().write(
                    "<a href='MatchFoundItemServlet?ticketId=" + ticketId + "'>Find Match</a>"
                );
                response.getWriter().write("</td>");

                // Update status
                response.getWriter().write("<td>");
                response.getWriter().write("<form action='UpdateTicketStatusServlet' method='post'>");
                response.getWriter().write("<input type='hidden' name='ticketId' value='" + ticketId + "'>");

                response.getWriter().write("<select name='status'>");
                response.getWriter().write("<option value='Pending' " + selected(currentStatus, "Pending") + ">Pending</option>");
                response.getWriter().write("<option value='Matched' " + selected(currentStatus, "Matched") + ">Matched</option>");
                response.getWriter().write("<option value='Released' " + selected(currentStatus, "Released") + ">Released</option>");
                response.getWriter().write("<option value='Rejected' " + selected(currentStatus, "Rejected") + ">Rejected</option>");
                response.getWriter().write("</select>");

                response.getWriter().write("<button type='submit'>Update</button>");
                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                // Delete
                response.getWriter().write("<td>");
                response.getWriter().write(
                    "<form action='DeleteTicketServlet' method='post' " +
                    "onsubmit=\"return confirm('Delete this ticket?');\">"
                );
                response.getWriter().write("<input type='hidden' name='ticketId' value='" + ticketId + "'>");
                response.getWriter().write("<button type='submit'>Delete</button>");
                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                response.getWriter().write("</tr>");
            }

            if (!hasData) {
                response.getWriter().write("<tr><td colspan='11'>No tickets found.</td></tr>");
            }

        } catch (Exception e) {
            response.getWriter().write(
                "<tr><td colspan='11'>Error: " + escapeHtml(e.getMessage()) + "</td></tr>"
            );
            e.printStackTrace();
        }

        response.getWriter().write("</table>");
        response.getWriter().write("</div>");

        response.getWriter().write("<br><a class='back-link' href='ViewFoundItemsServlet'>View Found Items</a>");
        response.getWriter().write("<br><br><a class='back-link' href='AdminDashboardServlet'>Back to Dashboard</a>");
        response.getWriter().write("<br><br><a class='back-link' href='index.html'>Back to Home</a>");

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