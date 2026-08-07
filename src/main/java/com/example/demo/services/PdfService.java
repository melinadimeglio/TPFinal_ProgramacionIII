package com.example.demo.services;

import com.example.demo.entities.ActivityEntity;
import com.example.demo.entities.ExpenseEntity;
import com.example.demo.entities.ItineraryEntity;
import com.example.demo.entities.TripEntity;
import com.example.demo.repositories.ExpenseRepository;
import com.example.demo.repositories.TripRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final TripRepository tripRepository;
    private final ExpenseRepository expenseRepository;

    private static final DeviceRgb TEAL       = new DeviceRgb(32, 148, 143);
    private static final DeviceRgb TEAL_LIGHT = new DeviceRgb(220, 242, 241);
    private static final DeviceRgb TEAL_DARK  = new DeviceRgb(20, 100, 96);
    private static final DeviceRgb GRAY       = new DeviceRgb(95, 94, 90);

    public byte[] generatePdf(Long tripId, Long userId) {
        TripEntity trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("Trip not found: " + tripId));

        boolean belongs = trip.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (!belongs) {
            throw new IllegalArgumentException("You are not part of this trip.");
        }

        List<ExpenseEntity> expenses = expenseRepository.findByTripIdAndActiveTrue(tripId);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            document.setMargins(40, 50, 40, 50);

            document.add(new Paragraph("TravelPlanner")
                    .setFontSize(10)
                    .setFontColor(TEAL)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(trip.getName())
                    .setFontSize(24)
                    .setBold()
                    .setFontColor(TEAL_DARK)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(trip.getDestination())
                    .setFontSize(14)
                    .setFontColor(GRAY)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(trip.getStartDate() + "  →  " + trip.getEndDate())
                    .setFontSize(11)
                    .setFontColor(GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new LineSeparator(new SolidLine()).setMarginBottom(16));


            if (trip.getItineraries() != null && !trip.getItineraries().isEmpty()) {
                document.add(new Paragraph("Itinerario")
                        .setFontSize(16)
                        .setBold()
                        .setFontColor(TEAL)
                        .setMarginTop(10)
                        .setMarginBottom(10));

                for (ItineraryEntity itinerary : trip.getItineraries()) {
                    if (!itinerary.isActive()) continue;

                    document.add(new Paragraph(itinerary.getItineraryDate().toString())
                            .setFontSize(13)
                            .setBold()
                            .setFontColor(TEAL_DARK)
                            .setBackgroundColor(TEAL_LIGHT)
                            .setMarginTop(12)
                            .setMarginBottom(6)
                            .setPadding(6));

                    if (itinerary.getNotes() != null && !itinerary.getNotes().isEmpty()) {
                        document.add(new Paragraph("Notas: " + itinerary.getNotes())
                                .setFontSize(10)
                                .setFontColor(GRAY)
                                .setItalic()
                                .setMarginBottom(6));
                    }

                    if (itinerary.getActivities() != null && !itinerary.getActivities().isEmpty()) {
                        Table actTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2}))
                                .setWidth(UnitValue.createPercentValue(100));

                        for (String header : new String[]{"Actividad", "Horario", "Categoría", "Precio"}) {
                            actTable.addHeaderCell(new Cell()
                                    .add(new Paragraph(header).setBold().setFontSize(9))
                                    .setBackgroundColor(TEAL)
                                    .setFontColor(ColorConstants.WHITE)
                                    .setPadding(5));
                        }

                        boolean alternate = false;
                        for (ActivityEntity activity : itinerary.getActivities()) {
                            String horario = (activity.getStartTime() != null ? activity.getStartTime().toString() : "-")
                                    + " - " + (activity.getEndTime() != null ? activity.getEndTime().toString() : "-");
                            String precio = activity.getPrice() != null ? "$" + String.format("%.0f", activity.getPrice()) : "-";
                            String categoria = activity.getCategory() != null ? activity.getCategory().toString() : "-";

                            DeviceRgb rowBg = alternate ? TEAL_LIGHT : new DeviceRgb(255, 255, 255);
                            for (String val : new String[]{activity.getName(), horario, categoria, precio}) {
                                actTable.addCell(new Cell()
                                        .add(new Paragraph(val).setFontSize(9))
                                        .setBackgroundColor(rowBg)
                                        .setPadding(5));
                            }
                            alternate = !alternate;
                        }

                        document.add(actTable);
                    } else {
                        document.add(new Paragraph("Sin actividades para este día.")
                                .setFontSize(10)
                                .setFontColor(GRAY)
                                .setItalic());
                    }
                }
            }


            document.add(new LineSeparator(new SolidLine()).setMarginTop(20).setMarginBottom(10));

            document.add(new Paragraph("Gastos del viaje")
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(TEAL)
                    .setMarginBottom(10));

            if (!expenses.isEmpty()) {
                Table expTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 3}))
                        .setWidth(UnitValue.createPercentValue(100));

                for (String header : new String[]{"Descripción", "Categoría", "Monto", "Fecha"}) {
                    expTable.addHeaderCell(new Cell()
                            .add(new Paragraph(header).setBold().setFontSize(9))
                            .setBackgroundColor(TEAL)
                            .setFontColor(ColorConstants.WHITE)
                            .setPadding(5));
                }

                double total = 0;
                boolean alternate = false;
                for (ExpenseEntity expense : expenses) {
                    String monto = "$" + String.format("%.0f", expense.getAmount());
                    String categoria = expense.getCategory() != null ? expense.getCategory().toString() : "-";
                    String fecha = expense.getDate() != null ? expense.getDate().toString() : "-";
                    String desc = expense.getDescription() != null ? expense.getDescription() : "-";

                    DeviceRgb rowBg = alternate ? TEAL_LIGHT : new DeviceRgb(255, 255, 255);
                    for (String val : new String[]{desc, categoria, monto, fecha}) {
                        expTable.addCell(new Cell()
                                .add(new Paragraph(val).setFontSize(9))
                                .setBackgroundColor(rowBg)
                                .setPadding(5));
                    }
                    total += expense.getAmount();
                    alternate = !alternate;
                }

                document.add(expTable);

                document.add(new Paragraph("Total: $" + String.format("%.0f", total))
                        .setFontSize(13)
                        .setBold()
                        .setFontColor(TEAL_DARK)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMarginTop(8));
            } else {
                document.add(new Paragraph("Sin gastos registrados.")
                        .setFontSize(10)
                        .setFontColor(GRAY)
                        .setItalic());
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }
}