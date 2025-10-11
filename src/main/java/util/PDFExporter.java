package util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import model.Pdi;
import model.Colaborador;
import dao.ColaboradorDAO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PDFExporter {
    
    private static ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    
    public static boolean exportarPDIsParaPDF(List<Pdi> pdis, String filePath) {
        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            
            // Fontes
            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont fontNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
            // Cabeçalho
            Paragraph titulo = new Paragraph("Relatório de Metas PDI")
                    .setFont(fontBold)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(titulo);
            
            Paragraph data = new Paragraph("Gerado em: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(data);
            
            // Tabela
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2, 4, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Cabeçalho da tabela
            Color headerColor = new DeviceRgb(70, 130, 180); // Azul steel
            String[] headers = {"Colaborador", "Setor", "Objetivo", "Prazo", "Status"};
            
            for (String header : headers) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setFont(fontBold).setFontSize(10))
                        .setBackgroundColor(headerColor)
                        .setFontColor(new DeviceRgb(255, 255, 255))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }
            
            // Dados da tabela
            for (Pdi pdi : pdis) {
                Colaborador colaborador = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
                
                // Colaborador
                table.addCell(createCell(colaborador != null ? colaborador.getNome() : "N/A", fontNormal));
                
                // Setor
                table.addCell(createCell(colaborador != null ? colaborador.getSetor() : "N/A", fontNormal));
                
                // Objetivo
                table.addCell(createCell(pdi.getObjetivo(), fontNormal));
                
                // Prazo
                table.addCell(createCell(pdi.getPrazo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
                
                // Status com cores
                Cell statusCell = createCell(getStatusText(pdi.getStatus()), fontNormal);
                statusCell.setTextAlignment(TextAlignment.CENTER);
                
                // Aplicar cor baseada no status
                switch (pdi.getStatus()) {
                    case CONCLUIDO:
                        statusCell.setBackgroundColor(new DeviceRgb(144, 238, 144)); // Verde claro
                        break;
                    case EM_ANDAMENTO:
                        statusCell.setBackgroundColor(new DeviceRgb(255, 255, 224)); // Amarelo claro
                        break;
                    case ATRASADO:
                        statusCell.setBackgroundColor(new DeviceRgb(255, 182, 193)); // Vermelho claro
                        break;
                    case NAO_INICIADO:
                        statusCell.setBackgroundColor(new DeviceRgb(211, 211, 211)); // Cinza
                        break;
                }
                table.addCell(statusCell);
            }
            
            document.add(table);
            
            // Rodapé
            Paragraph rodape = new Paragraph("Total de metas: " + pdis.size())
                    .setFont(fontNormal)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setMarginTop(20);
            document.add(rodape);
            
            document.close();
            return true;
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private static Cell createCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(9))
                .setPadding(5);
    }
    
    private static String getStatusText(model.Status status) {
        switch (status) {
            case NAO_INICIADO: return "Não Iniciado";
            case EM_ANDAMENTO: return "Em Andamento";
            case CONCLUIDO: return "Concluído";
            case ATRASADO: return "Atrasado";
            default: return status.toString();
        }
    }
}