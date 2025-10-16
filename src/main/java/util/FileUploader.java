package util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.util.List;

public class FileUploader {

    public static File escolherArquivo(Window window) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar arquivo");

        // filtros permitidos
        List<FileChooser.ExtensionFilter> filtros = List.of(
                new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"),
                new FileChooser.ExtensionFilter("PNG images (*.png)", "*.png")
        );

        fileChooser.getExtensionFilters().addAll(filtros);
        return fileChooser.showOpenDialog(window);
    }

    public static boolean validarExtensao(File arquivo) {
        if (arquivo == null) return false;
        String nome = arquivo.getName().toLowerCase();
        return nome.endsWith(".pdf") || nome.endsWith(".png");
    }
}

