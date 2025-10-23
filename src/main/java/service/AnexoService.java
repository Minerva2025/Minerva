package service;

import util.FileUploader;
import util.AlertUtils;
import javafx.stage.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AnexoService {

    private final Path pastaUploads = Path.of("uploads");

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
            Files.copy(arquivo.toPath(), destino, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            AlertUtils.info("Arquivo salvo com sucesso em: " + destino);

        } catch (IOException e) {
            AlertUtils.erro("Erro ao salvar arquivo: " + e.getMessage());
        }
    }

    
}
