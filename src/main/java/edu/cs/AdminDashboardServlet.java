package edu.cs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminUser") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        String adminUser = (String) session.getAttribute("adminUser");
        String adminRole = (String) session.getAttribute("adminRole");

        if (adminRole == null) {
            adminRole = "PUBLIC_SAFETY";
        }

        response.setContentType("text/html");

        response.getWriter().write("<!DOCTYPE html>");
        response.getWriter().write("<html>");
        response.getWriter().write("<head>");
        response.getWriter().write("<meta charset='UTF-8'>");
        response.getWriter().write("<title>Public Safety Dashboard</title>");
        response.getWriter().write("<link rel='stylesheet' href='style.css'>");
        response.getWriter().write("</head>");
        response.getWriter().write("<body>");

        response.getWriter().write("<div class='container'>");

        response.getWriter().write("<h1>Public Safety Dashboard</h1>");

        response.getWriter().write("<p class='subtitle'>");
        response.getWriter().write("Logged in as: <b>" + escapeHtml(adminUser) + "</b>");
        response.getWriter().write(" | Role: <b>" + escapeHtml(adminRole) + "</b>");
        response.getWriter().write("</p>");

        response.getWriter().write("<div class='card-grid'>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='add-found-item.html'>Add Found Item</a></h2>");
        response.getWriter().write("<p>Create a found-item notice for an item received by Public Safety.</p>");
        response.getWriter().write("</div>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='ViewFoundItemsServlet'>View Found Items</a></h2>");
        response.getWriter().write("<p>View, search, update, match, release, or delete recovered items.</p>");
        response.getWriter().write("</div>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='ViewTicketsServlet'>Review Lost Tickets</a></h2>");
        response.getWriter().write("<p>Review student lost-item tickets and update their workflow status.</p>");
        response.getWriter().write("</div>");

        if ("ADMIN".equals(adminRole)) {
            response.getWriter().write("<div class='card admin'>");
            response.getWriter().write("<h2><a href='ManageAdminsServlet'>Manage Admin Accounts</a></h2>");
            response.getWriter().write("<p>Create, revoke, or reactivate Public Safety login accounts.</p>");
            response.getWriter().write("</div>");
        }

        response.getWriter().write("</div>");

        response.getWriter().write("<br>");

        response.getWriter().write("<a class='back-link' href='index.html'>Back to Home</a>");
        response.getWriter().write("<br><br>");
        response.getWriter().write("<a class='back-link' href='LogoutServlet'>Logout</a>");

        response.getWriter().write("</div>");

        response.getWriter().write("</body>");
        response.getWriter().write("</html>");
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}