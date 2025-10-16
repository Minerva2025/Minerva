package service;
import util.FileUploader;
import util.AlertUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AnexoService {

    private final Path pastaUploads = Path.of("uploads");

    //  Método para anexar arquivo (PDF/PNG)
    public void anexarArquivo(Long idMeta, Window window) {
        File arquivo = FileUploader.escolherArquivo(window);
        if (arquivo == null) {
            AlertUtils.aviso("Nenhum arquivo selecionado.");
            return;
        }

        if (!FileUploader.validarExtensao(arquivo)) {
            AlertUtils.erro("Formato inválido. Use apenas PDF ou PNG.");
            return;
        }

        try {
            Path destinoMeta = pastaUploads.resolve(String.valueOf(idMeta));
            Files.createDirectories(destinoMeta);

            Path destino = destinoMeta.resolve(arquivo.getName());
            Files.copy(arquivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

            AlertUtils.info("Arquivo salvo com sucesso:\n" + destino);

        } catch (IOException e) {
            AlertUtils.erro("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
    
}
