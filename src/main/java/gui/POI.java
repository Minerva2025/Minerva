package gui;



import dao.ColaboradorDAO;
import dao.PdiDAO;
import model.Colaborador;
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

        String filePath = "C:\\Users\\Fatec\\Documents\\Project\\Metas.xlsx";
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
            int colaboradorId = pdi.getColaborador_id();
            ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
            Colaborador colaborador = colaboradorDAO.getColaboradorById(colaboradorId);

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(colaborador.getNome());
            row.createCell(1).setCellValue(colaborador.getSetor());
            row.createCell(2).setCellValue(pdi.getObjetivo());
            row.createCell(3).setCellValue(pdi.getPrazo());
            row.createCell(4).setCellValue(pdi.getStatus().ordinal());
        }






        FileOutputStream fout = new FileOutputStream(myFile);
        workbook.write(fout);
        fout.close();

        System.out.println("File Created");



    }

}
