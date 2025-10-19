package gui;



import dao.ColaboradorDAO;
import dao.PdiDAO;
import model.Colaborador;
import model.Pdi;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;



public class POI {
    public static void main(String[] args) {

        try {
            List<Pdi> lista = new PdiDAO().listAll();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Escolha onde salvar o arquivo Excel");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Arquivos Excel (*.xlsx)", "xlsx"));
            fileChooser.setSelectedFile(new File("Metas.xlsx"));


            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"), "Downloads"));
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection != JFileChooser.APPROVE_OPTION) {
                System.out.println("Operação cancelada pelo usuário.");
                return;
            }

            File arquivoSelecionado = fileChooser.getSelectedFile();
            if (!arquivoSelecionado.getName().toLowerCase().endsWith(".xlsx")) {
                arquivoSelecionado = new File(arquivoSelecionado.getAbsolutePath() + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fout = new FileOutputStream(arquivoSelecionado)) {

                Sheet sheet = workbook.createSheet("Metas");

                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Colaborador");
                headerRow.createCell(1).setCellValue("Setor");
                headerRow.createCell(2).setCellValue("Objetivo");
                headerRow.createCell(3).setCellValue("Prazo");
                headerRow.createCell(4).setCellValue("Status");

                int rowNum = 1;
                for (Pdi pdi : lista) {
                    int colaboradorId = pdi.getColaborador_id();
                    ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
                    Colaborador colaborador = colaboradorDAO.getColaboradorById(colaboradorId);

                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(colaborador.getNome());
                    row.createCell(1).setCellValue(colaborador.getSetor());
                    row.createCell(2).setCellValue(pdi.getObjetivo());
                    row.createCell(3).setCellValue(pdi.getPrazo().toString());
                    row.createCell(4).setCellValue(pdi.getStatus().name());
                }

                workbook.write(fout);
                System.out.println("✅ Arquivo Excel criado com sucesso em: " + arquivoSelecionado.getAbsolutePath());
            }

        } catch (IOException e) {
            System.err.println("❌ Erro ao criar o arquivo Excel: " + e.getMessage());
            e.printStackTrace();
        }
    }
}