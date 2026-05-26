package edu.cs;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/AddFoundItemServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class AddFoundItemServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String FOUND_UPLOAD_DIR = "found_item_uploads";

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");

        String itemType = request.getParameter("itemType");
        String description = request.getParameter("description");
        String foundLocation = request.getParameter("foundLocation");
        String foundDate = request.getParameter("foundDate");
        String status = request.getParameter("status");

        if (!isValidDate(foundDate)) {
            response.getWriter().write("<h2>Invalid Found Date</h2>");
            response.getWriter().write("<p>The found date must be between 2023-01-01 and today.</p>");
            response.getWriter().write("<a href='add-found-item.html'>Go Back</a>");
            return;
        }

        String imagePath = null;

        try {

            Part imagePart = request.getPart("foundImage");

            if (imagePart != null && imagePart.getSize() > 0) {

                String fileName = new File(imagePart.getSubmittedFileName()).getName();
                String lowerName = fileName.toLowerCase();

                if (!(lowerName.endsWith(".jpg") ||
                      lowerName.endsWith(".jpeg") ||
                      lowerName.endsWith(".png"))) {

                    response.getWriter().write("<h2>Upload Rejected</h2>");
                    response.getWriter().write("<p>Only JPG, JPEG, and PNG image files are allowed.</p>");
                    response.getWriter().write("<a href='add-found-item.html'>Try Again</a>");
                    return;
                }

                String applicationPath = request.getServletContext().getRealPath("");
                String uploadPath = applicationPath + File.separator + FOUND_UPLOAD_DIR;

                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String savedFileName = System.currentTimeMillis() + "_" + fileName;
                String fullPath = uploadPath + File.separator + savedFileName;

                imagePart.write(fullPath);

                imagePath = FOUND_UPLOAD_DIR + "/" + savedFileName;
            }

            String sql =
                "INSERT INTO found_items " +
                "(item_type, description, found_location, found_date, image_path, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection conn = DBUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, itemType);
                ps.setString(2, description);
                ps.setString(3, foundLocation);
                ps.setString(4, foundDate);
                ps.setString(5, imagePath);

                if (status == null || status.trim().isEmpty()) {
                    ps.setString(6, "Available");
                } else {
                    ps.setString(6, status);
                }

                ps.executeUpdate();
            }

            response.getWriter().write("<h2>Found item added successfully!</h2>");
            response.getWriter().write("<p>The item was saved in the database.</p>");

            if (imagePath != null) {
                response.getWriter().write("<p><b>Uploaded Image:</b> <a href='" + escapeHtml(imagePath) + "' target='_blank'>View Image</a></p>");
            }

            response.getWriter().write("<a href='ViewFoundItemsServlet'>View Found Items</a>");
            response.getWriter().write("<br><br>");
            response.getWriter().write("<a href='AdminDashboardServlet'>Back to Dashboard</a>");

        } catch (Exception e) {
            response.getWriter().write("<h2>Error adding found item</h2>");
            response.getWriter().write("<pre>" + escapeHtml(e.getMessage()) + "</pre>");
            e.printStackTrace();
        }
    }

    private boolean isValidDate(String dateText) {
        if (dateText == null || dateText.trim().isEmpty()) {
            return false;
        }

        try {
            LocalDate selectedDate = LocalDate.parse(dateText);
            LocalDate minDate = LocalDate.of(2023, 1, 1);
            LocalDate today = LocalDate.now();

            return !selectedDate.isBefore(minDate) && !selectedDate.isAfter(today);

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";

        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}