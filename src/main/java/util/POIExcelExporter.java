package util;



import dao.ColaboradorDAO;
import model.Colaborador;
import model.Pdi;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;



public class POIExcelExporter {

    public static void exportarParaExcel(File arquivo, List<Pdi> lista) {
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fout = new FileOutputStream(arquivo)) {

            Sheet sheet = workbook.createSheet("Metas");

            var headerStyle = workbook.createCellStyle();
            var headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short)12);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Colaborador", "Setor", "Objetivo", "Prazo", "Status"};
            for (int i = 0; i < headers.length; i++) {
                var cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
            int rowNum = 1;

            var dataStyle = workbook.createCellStyle();
            dataStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.LEFT);

            var dateStyle = workbook.createCellStyle();
            var dataFormat = workbook.createDataFormat();
            dateStyle.setDataFormat(dataFormat.getFormat("dd/MM/yyyy"));
            dateStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);

            for (Pdi pdi : lista) {
                Colaborador colaborador = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(colaborador.getNome());
                row.getCell(0).setCellStyle(dataStyle);

                row.createCell(1).setCellValue(colaborador.getSetor());
                row.getCell(1).setCellStyle(dataStyle);

                row.createCell(2).setCellValue(pdi.getObjetivo());
                row.getCell(2).setCellStyle(dataStyle);

                var cellPrazo = row.createCell(3);
                cellPrazo.setCellValue(java.sql.Date.valueOf(pdi.getPrazo())); // usa Date para POIExcelExporter
                cellPrazo.setCellStyle(dateStyle);

                row.createCell(4).setCellValue(pdi.getStatus().name());
                row.getCell(4).setCellStyle(dataStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fout);
            System.out.println("✅ Arquivo Excel criado em: " + arquivo.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Erro ao criar o arquivo Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}