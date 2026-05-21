package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/ViewFoundItemsServlet")
public class ViewFoundItemsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String search = request.getParameter("search");
        String statusFilter = request.getParameter("status");
        String itemId = request.getParameter("itemId");

        if (search == null) search = "";
        if (statusFilter == null) statusFilter = "";
        if (itemId == null) itemId = "";

        response.getWriter().write("<html><head>");
        response.getWriter().write("<title>Found Items</title>");
        response.getWriter().write("<link rel='stylesheet' href='style.css'>");
        response.getWriter().write("</head><body>");

        response.getWriter().write("<div class='container'>");

        response.getWriter().write("<h1>Found Items</h1>");
        response.getWriter().write("<p>These items are loaded from the database.</p>");

        response.getWriter().write("<form method='get' action='ViewFoundItemsServlet'>");

        response.getWriter().write("<label>Search Item</label>");
        response.getWriter().write("<input type='text' name='search' placeholder='Search by item type, description, or location' value='" + escapeHtml(search) + "'>");

        response.getWriter().write("<label>Filter by Status</label>");
        response.getWriter().write("<select name='status'>");
        response.getWriter().write("<option value=''>All</option>");
        response.getWriter().write("<option value='Available' " + selected(statusFilter, "Available") + ">Available</option>");
        response.getWriter().write("<option value='Matched' " + selected(statusFilter, "Matched") + ">Matched</option>");
        response.getWriter().write("<option value='Released' " + selected(statusFilter, "Released") + ">Released</option>");
        response.getWriter().write("</select>");

        response.getWriter().write("<input type='submit' value='Search / Filter'>");

        response.getWriter().write("</form>");

        response.getWriter().write("<br>");

        response.getWriter().write("<div class='table-box'>");
        response.getWriter().write("<table>");

        response.getWriter().write("<tr>");
        response.getWriter().write("<th>ID</th>");
        response.getWriter().write("<th>Item Type</th>");
        response.getWriter().write("<th>Description</th>");
        response.getWriter().write("<th>Found Location</th>");
        response.getWriter().write("<th>Found Date</th>");
        response.getWriter().write("<th>Status</th>");
        response.getWriter().write("<th>Matched Lost Ticket ID</th>");
        response.getWriter().write("<th>Update Status</th>");
        response.getWriter().write("<th>Delete</th>");
        response.getWriter().write("</tr>");

        String sql =
            "SELECT f.*, l.ticket_id AS matched_ticket_id " +
            "FROM found_items f " +
            "LEFT JOIN lost_tickets l ON f.id = l.matched_found_item_id " +
            "WHERE 1=1 ";

        if (!itemId.isEmpty()) {
            sql += "AND f.id = ? ";
        } else {
            sql += "AND (f.item_type LIKE ? OR f.description LIKE ? OR f.found_location LIKE ?) ";
        }

        if (!statusFilter.isEmpty()) {
            sql += "AND f.status = ? ";
        }

        sql += "ORDER BY f.id ASC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int paramIndex = 1;

            if (!itemId.isEmpty()) {
                ps.setString(paramIndex++, itemId);
            } else {
                String searchPattern = "%" + search + "%";
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
                ps.setString(paramIndex++, searchPattern);
            }

            if (!statusFilter.isEmpty()) {
                ps.setString(paramIndex++, statusFilter);
            }

            ResultSet rs = ps.executeQuery();

            boolean hasData = false;

            while (rs.next()) {

                hasData = true;

                int id = rs.getInt("id");
                String currentStatus = rs.getString("status");
                String matchedTicketId = rs.getString("matched_ticket_id");

                response.getWriter().write("<tr>");

                response.getWriter().write("<td>" + id + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("item_type")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("description")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("found_location")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(rs.getString("found_date")) + "</td>");
                response.getWriter().write("<td>" + escapeHtml(currentStatus) + "</td>");

                if (matchedTicketId != null) {
                    response.getWriter().write("<td><a href='ViewTicketsServlet?search=" + matchedTicketId + "'>" + matchedTicketId + "</a></td>");
                } else {
                    response.getWriter().write("<td>Not matched</td>");
                }

                response.getWriter().write("<td>");
                response.getWriter().write("<form action='UpdateFoundItemStatusServlet' method='post'>");
                response.getWriter().write("<input type='hidden' name='itemId' value='" + id + "'>");
                response.getWriter().write("<select name='status'>");
                response.getWriter().write("<option value='Available' " + selected(currentStatus, "Available") + ">Available</option>");
                response.getWriter().write("<option value='Matched' " + selected(currentStatus, "Matched") + ">Matched</option>");
                response.getWriter().write("<option value='Released' " + selected(currentStatus, "Released") + ">Released</option>");
                response.getWriter().write("</select>");
                response.getWriter().write("<button type='submit'>Update</button>");
                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                response.getWriter().write("<td>");
                response.getWriter().write("<form action='DeleteFoundItemServlet' method='post' onsubmit=\"return confirm('Are you sure you want to delete this item?');\">");
                response.getWriter().write("<input type='hidden' name='itemId' value='" + id + "'>");
                response.getWriter().write("<button type='submit'>Delete</button>");
                response.getWriter().write("</form>");
                response.getWriter().write("</td>");

                response.getWriter().write("</tr>");
            }

            if (!hasData) {
                response.getWriter().write("<tr><td colspan='9'>No found items found.</td></tr>");
            }

        } catch (Exception e) {
            response.getWriter().write("<tr><td colspan='9'>Error: " + escapeHtml(e.getMessage()) + "</td></tr>");
            e.printStackTrace();
        }

        response.getWriter().write("</table>");
        response.getWriter().write("</div>");

        response.getWriter().write("<br><a class='back-link' href='ViewTicketsServlet'>Review Lost Item Tickets</a>");
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