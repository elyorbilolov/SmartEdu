package com.smartedu.service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.smartedu.model.*;
import com.smartedu.repository.UserRepository;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    private final CourseService courseService;
    private final QuizService quizService;
    private final UserRepository userRepository;

    public ReportService(CourseService courseService, QuizService quizService, UserRepository userRepository) {
        this.courseService = courseService;
        this.quizService = quizService;
        this.userRepository = userRepository;
    }

    /**
     * Talabaning akademik hisoboti (PDF formatida)
     */
    public byte[] generateStudentTranscriptPdf(User student) throws DocumentException {
        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Fonts
        com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(30, 58, 138));
        com.lowagie.text.Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(71, 85, 105));
        com.lowagie.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        com.lowagie.text.Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        com.lowagie.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);

        // Header
        Paragraph title = new Paragraph("SmartEdu - TAL'IM PLATFORMASI", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph subTitle = new Paragraph("TALABA REYTING DAFTARCHASI VA BAHOLAR HISOBOTI", subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(15);
        document.add(subTitle);

        // Student Information Box
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15);

        addInfoCell(infoTable, "Talaba F.I.Sh:", student.getFullName(), boldFont, normalFont);
        addInfoCell(infoTable, "Login (ID):", student.getUsername(), boldFont, normalFont);
        addInfoCell(infoTable, "Elektron pochta:", student.getEmail() != null ? student.getEmail() : "-", boldFont, normalFont);
        addInfoCell(infoTable, "Hisobot sanasi:", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), boldFont, normalFont);
        document.add(infoTable);

        // Section 1: Topshiriqlar (Assignments) Baholari
        Paragraph aTitle = new Paragraph("1. Topshiriqlar va Amaliy Vazifalar Baholari", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(30, 58, 138)));
        aTitle.setSpacingAfter(8);
        document.add(aTitle);

        List<Submission> submissions = courseService.findSubmissionsByStudent(student);
        PdfPTable aTable = new PdfPTable(new float[]{1f, 3.5f, 3.5f, 1.5f, 1.5f, 2f});
        aTable.setWidthPercentage(100);
        aTable.setSpacingAfter(15);

        addHeaderCell(aTable, "#", headerFont);
        addHeaderCell(aTable, "Fan nomi", headerFont);
        addHeaderCell(aTable, "Topshiriq nomi", headerFont);
        addHeaderCell(aTable, "Ball", headerFont);
        addHeaderCell(aTable, "Maks", headerFont);
        addHeaderCell(aTable, "Holati", headerFont);

        int aIndex = 1;
        int totalAssignmentScore = 0;
        int maxAssignmentScore = 0;

        if (submissions.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Topshiriqlar yechimlari mavjud emas", normalFont));
            emptyCell.setColspan(6);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(8);
            aTable.addCell(emptyCell);
        } else {
            for (Submission s : submissions) {
                addBodyCell(aTable, String.valueOf(aIndex++), normalFont, Element.ALIGN_CENTER);
                addBodyCell(aTable, s.getAssignment().getCourse().getTitle(), normalFont, Element.ALIGN_LEFT);
                addBodyCell(aTable, s.getAssignment().getTitle(), normalFont, Element.ALIGN_LEFT);
                addBodyCell(aTable, s.getScore() != null ? s.getScore().toString() : "-", boldFont, Element.ALIGN_CENTER);
                addBodyCell(aTable, String.valueOf(s.getAssignment().getMaxScore()), normalFont, Element.ALIGN_CENTER);
                addBodyCell(aTable, s.getStatus().getDisplayName(), normalFont, Element.ALIGN_CENTER);

                if (s.getScore() != null) {
                    totalAssignmentScore += s.getScore();
                    maxAssignmentScore += s.getAssignment().getMaxScore();
                }
            }
        }
        document.add(aTable);

        // Section 2: Onlayn Testlar (Quizzes) Natijalari
        Paragraph qTitle = new Paragraph("2. Onlayn Testlar Natijalari", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(30, 58, 138)));
        qTitle.setSpacingAfter(8);
        document.add(qTitle);

        List<QuizResult> quizResults = quizService.findResultsByStudent(student);
        PdfPTable qTable = new PdfPTable(new float[]{1f, 3.5f, 3.5f, 1.5f, 1.5f, 2f});
        qTable.setWidthPercentage(100);
        qTable.setSpacingAfter(20);

        addHeaderCell(qTable, "#", headerFont);
        addHeaderCell(qTable, "Fan nomi", headerFont);
        addHeaderCell(qTable, "Test nomi", headerFont);
        addHeaderCell(qTable, "Savollar", headerFont);
        addHeaderCell(qTable, "To'g'ri", headerFont);
        addHeaderCell(qTable, "Natija (%)", headerFont);

        int qIndex = 1;
        double totalQuizPercent = 0;

        if (quizResults.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Onlayn test natijalari mavjud emas", normalFont));
            emptyCell.setColspan(6);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(8);
            qTable.addCell(emptyCell);
        } else {
            for (QuizResult qr : quizResults) {
                addBodyCell(qTable, String.valueOf(qIndex++), normalFont, Element.ALIGN_CENTER);
                addBodyCell(qTable, qr.getQuiz().getCourse().getTitle(), normalFont, Element.ALIGN_LEFT);
                addBodyCell(qTable, qr.getQuiz().getTitle(), normalFont, Element.ALIGN_LEFT);
                addBodyCell(qTable, String.valueOf(qr.getTotalQuestions()), normalFont, Element.ALIGN_CENTER);
                addBodyCell(qTable, String.valueOf(qr.getCorrectAnswers()), boldFont, Element.ALIGN_CENTER);
                addBodyCell(qTable, String.format("%.1f %%", qr.getPercentage()), boldFont, Element.ALIGN_CENTER);
                totalQuizPercent += qr.getPercentage();
            }
        }
        document.add(qTable);

        // Section 3: Xulosa va O'rtacha ko'rsatkich
        double avgQuiz = !quizResults.isEmpty() ? (totalQuizPercent / quizResults.size()) : 0.0;
        double avgAssignment = maxAssignmentScore > 0 ? ((double) totalAssignmentScore / maxAssignmentScore) * 100.0 : 0.0;
        double totalAverage = (avgQuiz > 0 && avgAssignment > 0) ? (avgQuiz + avgAssignment) / 2.0 : (avgQuiz > 0 ? avgQuiz : avgAssignment);

        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingAfter(30);

        PdfPCell summaryCell = new PdfPCell(new Phrase(
                String.format("UMUMIY O'ZLASHTIRISH KO'RSATKICHI: %.1f %%\nTopshiriqlar bo'yicha: %.1f %% | Testlar bo'yicha: %.1f %%",
                        totalAverage, avgAssignment, avgQuiz),
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(30, 58, 138))
        ));
        summaryCell.setColspan(2);
        summaryCell.setBackgroundColor(new Color(241, 245, 249));
        summaryCell.setPadding(10);
        summaryCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        summaryTable.addCell(summaryCell);
        document.add(summaryTable);

        // Signature and Stamp section
        PdfPTable signTable = new PdfPTable(2);
        signTable.setWidthPercentage(100);

        PdfPCell leftSign = new PdfPCell(new Phrase("Dekanat / O'quv bo'limi boshlig'i: _____________\n\nImzo: _____________________", normalFont));
        leftSign.setBorder(Rectangle.NO_BORDER);

        PdfPCell rightSign = new PdfPCell(new Phrase("«SmartEdu» Tizimi Tasdiqlovi\nQR Kod & Muhr: [Elektron Imzolangan]\nSana: " + java.time.LocalDate.now().toString(), normalFont));
        rightSign.setBorder(Rectangle.NO_BORDER);
        rightSign.setHorizontalAlignment(Element.ALIGN_RIGHT);

        signTable.addCell(leftSign);
        signTable.addCell(rightSign);
        document.add(signTable);

        document.close();
        return out.toByteArray();
    }

    /**
     * Fan bo'yicha baholar jurnali (Excel XLSX formatida)
     */
    public byte[] generateCourseGradesExcel(Course course) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Baholar Jurnali");

            // Header styling
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Title styling
            org.apache.poi.ss.usermodel.Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);

            // Course Info Rows
            Row row0 = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell c0 = row0.createCell(0);
            c0.setCellValue("SmartEdu - Fan Bo'yicha Baholar Jurnali");
            c0.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Fan: " + course.getTitle() + " (" + course.getCode() + ")");
            Row row2 = sheet.createRow(2);
            row2.createCell(0).setCellValue("O'qituvchi: " + (course.getTeacher() != null ? course.getTeacher().getFullName() : "-"));
            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("Chiqarilgan sana: " + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

            // Table Headers
            String[] headers = {"№", "Talaba F.I.Sh", "Login", "Topshiriqlar Balli", "Testlar Natijasi (%)", "Umumiy Ball", "Natija / Holati"};
            Row headerRow = sheet.createRow(5);
            headerRow.setHeightInPoints(24);

            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Cell Styles for data
            CellStyle normalStyle = workbook.createCellStyle();
            normalStyle.setBorderBottom(BorderStyle.THIN);
            normalStyle.setBorderTop(BorderStyle.THIN);
            normalStyle.setBorderLeft(BorderStyle.THIN);
            normalStyle.setBorderRight(BorderStyle.THIN);

            CellStyle centerStyle = workbook.createCellStyle();
            centerStyle.cloneStyleFrom(normalStyle);
            centerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Populate Students
            List<User> students = course.getStudents();
            int rowIndex = 6;
            int num = 1;

            for (User s : students) {
                Row row = sheet.createRow(rowIndex++);

                // Submissions score
                List<Submission> submissions = courseService.findSubmissionsByCourseAndStudent(course, s);
                int totalScore = 0;
                int countGraded = 0;
                for (Submission sub : submissions) {
                    if (sub.getScore() != null) {
                        totalScore += sub.getScore();
                        countGraded++;
                    }
                }
                double avgSubmission = countGraded > 0 ? (double) totalScore / countGraded : 0.0;

                // Quizzes score
                List<QuizResult> quizResults = quizService.findResultsByCourseAndStudent(course, s);
                double totalQuiz = 0;
                for (QuizResult qr : quizResults) {
                    totalQuiz += qr.getPercentage();
                }
                double avgQuiz = !quizResults.isEmpty() ? (totalQuiz / quizResults.size()) : 0.0;

                double finalScore = (avgSubmission > 0 && avgQuiz > 0) ? (avgSubmission + avgQuiz) / 2.0 : (avgSubmission > 0 ? avgSubmission : avgQuiz);
                String status = finalScore >= 55.0 ? "O'zlashtirdi" : "O'zlashtirmadi";

                org.apache.poi.ss.usermodel.Cell cNum = row.createCell(0);
                cNum.setCellValue(num++);
                cNum.setCellStyle(centerStyle);

                org.apache.poi.ss.usermodel.Cell cName = row.createCell(1);
                cName.setCellValue(s.getFullName());
                cName.setCellStyle(normalStyle);

                org.apache.poi.ss.usermodel.Cell cUser = row.createCell(2);
                cUser.setCellValue(s.getUsername());
                cUser.setCellStyle(normalStyle);

                org.apache.poi.ss.usermodel.Cell cSub = row.createCell(3);
                cSub.setCellValue(String.format("%.1f", avgSubmission));
                cSub.setCellStyle(centerStyle);

                org.apache.poi.ss.usermodel.Cell cQuiz = row.createCell(4);
                cQuiz.setCellValue(String.format("%.1f%%", avgQuiz));
                cQuiz.setCellStyle(centerStyle);

                org.apache.poi.ss.usermodel.Cell cFinal = row.createCell(5);
                cFinal.setCellValue(String.format("%.1f", finalScore));
                cFinal.setCellStyle(centerStyle);

                org.apache.poi.ss.usermodel.Cell cStatus = row.createCell(6);
                cStatus.setCellValue(status);
                cStatus.setCellStyle(centerStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    // Helper methods for PDF formatting
    private void addInfoCell(PdfPTable table, String label, String value, com.lowagie.text.Font labelFont, com.lowagie.text.Font valueFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(3);
        Paragraph p = new Paragraph();
        p.add(new Chunk(label + " ", labelFont));
        p.add(new Chunk(value, valueFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addHeaderCell(PdfPTable table, String text, com.lowagie.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(30, 58, 138));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, com.lowagie.text.Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "-", font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        cell.setBorderColor(new Color(226, 232, 240));
        table.addCell(cell);
    }
}
