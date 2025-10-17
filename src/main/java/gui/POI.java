package gui;



import dao.PdiDAO;
import model.Pdi;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class POI {
    public static void main(String[] args) throws IOException {

        List<Pdi> lista = new PdiDAO().listAll();


        String filePath = "D:\\Documentos\\Project\\Metas.xlsx";
        File myFile = new File(filePath);

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Metas");

        Row headerRow = sheet.createRow(0);

        headerRow.createCell(0).setCellValue("Colaborador");
        headerRow.createCell(1).setCellValue("Setor");
        headerRow.createCell(2).setCellValue("Objetivo");
        headerRow.createCell(3).setCellValue("Prazo");
        headerRow.createCell(4).setCellValue("Status");

        int rowNum = 1;
        for (Pdi pdi : lista){
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(pdi.getId());
            row.createCell(1).setCellValue(pdi.getObjetivo());
        }






        FileOutputStream fout = new FileOutputStream(myFile);
        workbook.write(fout);
        fout.close();

        System.out.println("File Created");



    }

}
