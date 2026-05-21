package edu.cs;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/AdminDashboardServlet")
public class AdminDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("adminUser") == null) {
            response.sendRedirect("admin-login.html");
            return;
        }

        String adminUser = (String) session.getAttribute("adminUser");

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
        response.getWriter().write("<p class='subtitle'>Logged in as: <b>" + adminUser + "</b></p>");

        response.getWriter().write("<div class='card-grid'>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='add-found-item.html'>Add Found Item</a></h2>");
        response.getWriter().write("<p>Create a simple notice for an item received by Public Safety.</p>");
        response.getWriter().write("</div>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='ViewFoundItemsServlet'>View Found Items</a></h2>");
        response.getWriter().write("<p>See all posted found items currently available in the system.</p>");
        response.getWriter().write("</div>");

        response.getWriter().write("<div class='card'>");
        response.getWriter().write("<h2><a href='ViewTicketsServlet'>Review Tickets</a></h2>");
        response.getWriter().write("<p>Review student lost tickets and claim requests.</p>");
        response.getWriter().write("</div>");

        response.getWriter().write("</div>");

        response.getWriter().write("<br>");
        response.getWriter().write("<a class='back-link' href='index.html'>Back to Home</a>");
        response.getWriter().write("<br><br>");
        response.getWriter().write("<a class='back-link' href='LogoutServlet'>Logout</a>");

        response.getWriter().write("</div>");

        response.getWriter().write("</body>");
        response.getWriter().write("</html>");
    }
}