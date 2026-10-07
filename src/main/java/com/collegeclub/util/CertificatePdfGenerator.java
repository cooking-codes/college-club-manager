package com.collegeclub.util;

import com.collegeclub.model.ClubRegistration;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Generates club-membership certificates with a shared ARMIET identity and
 * a distinct visual theme for each of the four project clubs.
 */
public final class CertificatePdfGenerator {

    private static final PDRectangle PAGE = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
    private static final Color ARMIET_MAROON = new Color(109, 16, 40);
    private static final Color ARMIET_GOLD = new Color(199, 162, 58);
    private static final Color TEXT = new Color(40, 40, 44);
    private static final Color PAPER = new Color(252, 250, 246);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    private CertificatePdfGenerator() {}

    public static void generate(ClubRegistration registration, OutputStream output) throws IOException {
        Theme theme = Theme.forClub(registration.getClubName());
        String certificateId = certificateId(registration);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PAGE);
            document.addPage(page);

            float width = PAGE.getWidth();
            float height = PAGE.getHeight();

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.setNonStrokingColor(PAPER);
                cs.addRect(0, 0, width, height);
                cs.fill();

                // Theme bands and ARMIET gold framing.
                cs.setNonStrokingColor(theme.primary);
                cs.addRect(0, height - 18, width, 18);
                cs.fill();
                cs.addRect(0, 0, 18, height);
                cs.fill();
                cs.setStrokingColor(ARMIET_GOLD);
                cs.setLineWidth(2.2f);
                cs.addRect(28, 28, width - 56, height - 56);
                cs.stroke();
                cs.setStrokingColor(theme.secondary);
                cs.setLineWidth(0.8f);
                cs.addRect(38, 38, width - 76, height - 76);
                cs.stroke();

                // ARMIET emblem-style mark.
                cs.setNonStrokingColor(ARMIET_MAROON);
                drawCircle(cs, 78, height - 78, 27);
                cs.fill();
                drawCentered(cs, "ARMIET", 78, height - 82, PDType1Font.HELVETICA_BOLD, 10, Color.WHITE);

                drawCentered(cs, "ALAMURI RATNAMALA INSTITUTE OF ENGINEERING & TECHNOLOGY", width / 2,
                        height - 67, PDType1Font.HELVETICA_BOLD, 15, ARMIET_MAROON);
                drawCentered(cs, "ARMIET • STUDENT CLUBS", width / 2, height - 88,
                        PDType1Font.HELVETICA_BOLD, 8.5f, ARMIET_GOLD);

                // Theme-specific motif on the right.
                drawThemeMotif(cs, theme, width - 82, height - 78);

                drawCentered(cs, "CERTIFICATE OF CLUB MEMBERSHIP", width / 2, height - 145,
                        PDType1Font.HELVETICA_BOLD, 22, theme.primary);
                drawCentered(cs, "This certificate is proudly presented to", width / 2, height - 188,
                        PDType1Font.HELVETICA, 12, TEXT);

                drawCentered(cs, safe(registration.getStudentName()), width / 2, height - 230,
                        PDType1Font.HELVETICA_BOLD, 27, ARMIET_MAROON);

                cs.setStrokingColor(theme.secondary);
                cs.setLineWidth(1.4f);
                cs.moveTo(width / 2 - 150, height - 242);
                cs.lineTo(width / 2 + 150, height - 242);
                cs.stroke();

                drawCentered(cs, "for being a registered member of", width / 2, height - 272,
                        PDType1Font.HELVETICA, 11.5f, TEXT);
                drawCentered(cs, safe(registration.getClubName()), width / 2, height - 305,
                        PDType1Font.HELVETICA_BOLD, 21, theme.primary);

                drawCentered(cs, "during the Academic Year 2026–2027", width / 2, height - 336,
                        PDType1Font.HELVETICA, 11, TEXT);

                // Information cards.
                drawInfo(cs, 92, 100, "CERTIFICATE ID", certificateId, theme);
                drawInfo(cs, 300, 100, "ISSUE DATE", LocalDate.now().format(DATE_FORMAT), theme);
                drawInfo(cs, 508, 100, "ROLL NUMBER", safe(registration.getStudentRollNumber()), theme);

                // Signature blocks.
                drawSignature(cs, 170, 75, "Faculty Coordinator");
                drawSignature(cs, width - 170, 75, "Club Coordinator");

                drawCentered(cs, "Official ARMIET Student Club Record", width / 2, 48,
                        PDType1Font.HELVETICA_OBLIQUE, 8, new Color(100, 100, 100));
            }

            document.save(output);
        }
    }

    public static String certificateId(ClubRegistration registration) {
        return "ARMIET-" + clubCode(registration.getClubName()) + "-2026-" + String.format("%04d", registration.getId());
    }

    public static String clubCode(String clubName) {
        if (clubName == null) return "CLUB";
        String normalized = clubName.toLowerCase();
        if (normalized.contains("internet") || normalized.contains("iot")) return "IOT";
        if (normalized.contains("drone")) return "DRONE";
        if (normalized.contains("electric vehicle") || normalized.contains("ev")) return "EV";
        if (normalized.contains("dance")) return "DANCE";
        return "CLUB";
    }

    private static void drawInfo(PDPageContentStream cs, float x, float y, String label, String value, Theme theme) throws IOException {
        cs.setNonStrokingColor(new Color(248, 245, 238));
        cs.addRect(x, y, 190, 42);
        cs.fill();
        cs.setStrokingColor(theme.secondary);
        cs.setLineWidth(1);
        cs.addRect(x, y, 190, 42);
        cs.stroke();
        drawCentered(cs, label, x + 95, y + 26, PDType1Font.HELVETICA_BOLD, 7.5f, theme.primary);
        drawCentered(cs, value, x + 95, y + 11, PDType1Font.HELVETICA, 8.5f, TEXT);
    }

    private static void drawSignature(PDPageContentStream cs, float x, float y, String label) throws IOException {
        cs.setStrokingColor(new Color(100, 100, 100));
        cs.setLineWidth(0.8f);
        cs.moveTo(x - 70, y);
        cs.lineTo(x + 70, y);
        cs.stroke();
        drawCentered(cs, label, x, y - 14, PDType1Font.HELVETICA_BOLD, 8, TEXT);
    }

    private static void drawThemeMotif(PDPageContentStream cs, Theme theme, float x, float y) throws IOException {
        cs.setStrokingColor(theme.secondary);
        cs.setNonStrokingColor(theme.primary);
        cs.setLineWidth(2);
        switch (theme.code) {
            case "IOT" -> {
                for (int i = 0; i < 3; i++) {
                    drawCircle(cs, x - 18 + i * 18, y, 5);
                    cs.fill();
                    if (i < 2) { cs.moveTo(x - 13 + i * 18, y); cs.lineTo(x + i * 18, y); cs.stroke(); }
                }
                drawCircle(cs, x, y + 18, 5);
                cs.moveTo(x, y + 13); cs.lineTo(x, y + 5); cs.stroke();
            }
            case "DRONE" -> {
                cs.addRect(x - 20, y - 5, 40, 10); cs.fill();
                drawCircle(cs, x - 24, y + 10, 7); drawCircle(cs, x + 24, y + 10, 7);
                drawCircle(cs, x - 24, y - 10, 7); drawCircle(cs, x + 24, y - 10, 7);
                cs.setStrokingColor(theme.secondary); cs.moveTo(x, y - 5); cs.lineTo(x, y - 24); cs.stroke();
            }
            case "EV" -> {
                cs.addRect(x - 24, y - 10, 48, 20); cs.fill();
                cs.setNonStrokingColor(PAPER); drawCircle(cs, x - 14, y - 10, 6); drawCircle(cs, x + 14, y - 10, 6);
                cs.setStrokingColor(theme.secondary); cs.setLineWidth(3); cs.moveTo(x + 2, y + 14); cs.lineTo(x - 6, y + 4); cs.lineTo(x + 3, y + 4); cs.lineTo(x - 3, y - 5); cs.stroke();
            }
            default -> {
                cs.setLineWidth(2);
                for (int i = 0; i < 3; i++) {
                    cs.moveTo(x - 25, y - 12 + i * 12); cs.curveTo(x - 5, y + 12 - i * 6, x + 10, y - 20 + i * 10, x + 28, y + i * 8); cs.stroke();
                }
            }
        }
    }

    private static void drawCircle(PDPageContentStream cs, float cx, float cy, float r) throws IOException {
        final float k = 0.5522848f;
        cs.moveTo(cx + r, cy);
        cs.curveTo(cx + r, cy + k * r, cx + k * r, cy + r, cx, cy + r);
        cs.curveTo(cx - k * r, cy + r, cx - r, cy + k * r, cx - r, cy);
        cs.curveTo(cx - r, cy - k * r, cx - k * r, cy - r, cx, cy - r);
        cs.curveTo(cx + k * r, cy - r, cx + r, cy - k * r, cx + r, cy);
        cs.fill();
    }

    private static void drawCentered(PDPageContentStream cs, String text, float centerX, float baselineY,
                                     PDType1Font font, float fontSize, Color color) throws IOException {
        String value = safe(text);
        float width = font.getStringWidth(value) / 1000f * fontSize;
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.setNonStrokingColor(color);
        cs.newLineAtOffset(centerX - width / 2f, baselineY);
        cs.showText(value);
        cs.endText();
    }

    private static String safe(String value) {
        return value == null || value.isBlank() ? "—" : value.replaceAll("[\\r\\n]+", " ");
    }

    private static final class Theme {
        final String code;
        final Color primary;
        final Color secondary;

        Theme(String code, Color primary, Color secondary) {
            this.code = code;
            this.primary = primary;
            this.secondary = secondary;
        }

        static Theme forClub(String clubName) {
            String code = clubCode(clubName);
            return switch (code) {
                case "IOT" -> new Theme("IOT", new Color(0, 112, 122), new Color(40, 190, 190));
                case "DRONE" -> new Theme("DRONE", new Color(32, 73, 130), new Color(76, 153, 219));
                case "EV" -> new Theme("EV", new Color(24, 103, 75), new Color(73, 174, 119));
                case "DANCE" -> new Theme("DANCE", new Color(111, 42, 94), new Color(198, 111, 163));
                default -> new Theme("CLUB", ARMIET_MAROON, ARMIET_GOLD);
            };
        }
    }
}
