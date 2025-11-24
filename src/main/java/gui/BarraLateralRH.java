package gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Usuario;

public class BarraLateralRH extends VBox {

    private Usuario logado;

    public BarraLateralRH(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
        this.setId("left");

        Button inicio = new Button("Início");
        inicio.setId("inicio");
        inicio.getStyleClass().add("responsive-button");
        inicio.setOnAction(e -> {
            Stage homerhStage = new Stage();
            new HomeRH(logado).start(homerhStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        
        Button equipe = new Button("Equipe");
        equipe.setOnAction(e -> {
            Stage equipesrhStage = new Stage();
            new EquipesRH(logado).start(equipesrhStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        equipe.setId("equipe");
        equipe.getStyleClass().add("responsive-button");

        Button relatorios = new Button("Relatórios");
        relatorios.setId("relatorios");
        relatorios.getStyleClass().add("responsive-button");
        relatorios.setOnAction(e -> {
            Stage relatoriosrhStage = new Stage();
            new RelatoriosRH(logado).start(relatoriosrhStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        Button metas = new Button("Metas");
        metas.setOnAction(e -> {
            Stage metasStage = new Stage();
            new Metas(logado).start(metasStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        metas.setId("metas");
        metas.getStyleClass().add("responsive-button");

        ToggleButton avaliacoes = new ToggleButton("Avaliações");
        avaliacoes.setId("avaliacoes");
        avaliacoes.getStyleClass().add("responsive-button");
        avaliacoes.setOnAction(e -> {
            Stage avaliacaorhStage = new Stage();
            new AvaliacaoRH(logado).start(avaliacaorhStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        this.setSpacing(30);
        this.getStyleClass().add("responsive-barralateral");
        this.getChildren().addAll(inicio, equipe, relatorios, metas, avaliacoes);
    }
}