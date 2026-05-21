package edu.cs;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TestDBServlet")
public class TestDBServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(
            javax.servlet.http.HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        try {

            Connection conn = DBUtil.getConnection();

            response.getWriter().write(
                "<h2>Database Connected Successfully!</h2>"
            );

            conn.close();

        } catch (Exception e) {

            response.getWriter().write(
                "<h2>Database Connection Failed</h2>"
            );

            response.getWriter().write(
                "<pre>" + e.getMessage() + "</pre>"
            );

            e.printStackTrace();
        }
    }
}