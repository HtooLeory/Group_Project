package edu.cs;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/UpdateFoundItemStatusServlet")
public class UpdateFoundItemStatusServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String itemId = request.getParameter("itemId");
        String status = request.getParameter("status");

        try (Connection conn = DBUtil.getConnection()) {

            String sql = "UPDATE found_items SET status = ? WHERE id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, itemId);

            ps.executeUpdate();

            response.sendRedirect("ViewFoundItemsServlet");

        } catch (Exception e) {
            response.setContentType("text/html");
            response.getWriter().write("<h2>Error updating found item status</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
        }
    }
}