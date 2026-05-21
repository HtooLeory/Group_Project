package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddFoundItemServlet")
public class AddFoundItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String itemType = request.getParameter("itemType");
        String description = request.getParameter("description");
        String foundLocation = request.getParameter("foundLocation");
        String foundDate = request.getParameter("foundDate");
        String status = request.getParameter("status");

        response.setContentType("text/html");

        try {

            Connection conn = DBUtil.getConnection();

            String sql =
                "INSERT INTO found_items " +
                "(item_type, description, found_location, found_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, itemType);
            ps.setString(2, description);
            ps.setString(3, foundLocation);
            ps.setString(4, foundDate);
            ps.setString(5, status);

            ps.executeUpdate();

            conn.close();

            response.getWriter().write("<h2>Found item added successfully!</h2>");
            response.getWriter().write(
                "<a href='AdminDashboardServlet'>Back to Dashboard</a>"
            );

        } catch (Exception e) {

            response.getWriter().write("<h2>Error</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");

            e.printStackTrace();
        }
    }
}