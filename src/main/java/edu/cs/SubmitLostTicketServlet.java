package edu.cs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet("/SubmitLostTicketServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class SubmitLostTicketServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "lost_ticket_uploads";

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String studentName = request.getParameter("studentName");
        String studentId = request.getParameter("studentId");
        String studentEmail = request.getParameter("studentEmail");
        String phone = request.getParameter("phone");
        String itemType = request.getParameter("itemType");
        String description = request.getParameter("description");
        String lostLocation = request.getParameter("lostLocation");
        String lostDate = request.getParameter("lostDate");

        String proofFilePath = null;

        try {
            Part filePart = request.getPart("proofFile");

            if (filePart != null && filePart.getSize() > 0) {

                String fileName = new File(filePart.getSubmittedFileName()).getName();
                String lowerName = fileName.toLowerCase();

                if (!(lowerName.endsWith(".jpg") ||
                      lowerName.endsWith(".jpeg") ||
                      lowerName.endsWith(".png") ||
                      lowerName.endsWith(".pdf") ||
                      lowerName.endsWith(".txt"))) {

                    response.getWriter().write("<h2>Upload Rejected</h2>");
                    response.getWriter().write("<p>Only JPG, PNG, PDF, or TXT files are allowed.</p>");
                    response.getWriter().write("<a href='submit-lost-ticket.html'>Try Again</a>");
                    return;
                }

                String applicationPath = request.getServletContext().getRealPath("");
                String uploadPath = applicationPath + File.separator + UPLOAD_DIR;

                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String savedFileName = System.currentTimeMillis() + "_" + fileName;
                String fullPath = uploadPath + File.separator + savedFileName;

                filePart.write(fullPath);

                proofFilePath = UPLOAD_DIR + "/" + savedFileName;
            }

            String sql =
                "INSERT INTO lost_tickets " +
                "(student_name, student_id, student_email, phone, item_type, description, lost_location, lost_date, proof_file_path, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, studentName);
                ps.setString(2, studentId);
                ps.setString(3, studentEmail);
                ps.setString(4, phone);
                ps.setString(5, itemType);
                ps.setString(6, description);
                ps.setString(7, lostLocation);
                ps.setString(8, lostDate);
                ps.setString(9, proofFilePath);
                ps.setString(10, "Pending");

                ps.executeUpdate();
            }

            response.getWriter().write("<h2>Lost ticket submitted successfully!</h2>");
            response.getWriter().write("<p>Your ticket status is: <b>Pending</b></p>");
            response.getWriter().write("<a href='index.html'>Back to Home</a>");

        } catch (Exception e) {
            response.getWriter().write("<h2>Error submitting ticket</h2>");
            response.getWriter().write("<pre>" + e.getMessage() + "</pre>");
            e.printStackTrace();
        }
    }
}