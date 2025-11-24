package gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Usuario;

public class BarraLateralGG extends VBox {

    private Usuario logado;

    public BarraLateralGG(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
        this.setId("left");

        Button inicio = new Button("Início");
        inicio.setId("inicio");
        inicio.getStyleClass().add("responsive-button");
        inicio.setOnAction(e -> {
            Stage homeggStage = new Stage();
            new HomeGestorGeral(logado).start(homeggStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        
        Button equipe = new Button("Equipe");
        equipe.setOnAction(e -> {
            Stage equipesggStage = new Stage();
            new EquipesGG(logado).start(equipesggStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        equipe.setId("equipe");
        equipe.getStyleClass().add("responsive-button");

        Button relatorios = new Button("Relatorios");
        relatorios.setOnAction(event -> {
            Stage relatoriosggStage = new Stage();
            new RelatoriosGG(logado).start(relatoriosggStage);

            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        } );
        relatorios.setId("relatorios");
        relatorios.getStyleClass().add("responsive-button");

        Button metas = new Button("Metas");
        metas.setOnAction(e -> {
            Stage metasggStage = new Stage();
            new MetasGG(logado).start(metasggStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        metas.setId("metas");
        metas.getStyleClass().add("responsive-button");

        this.setSpacing(30);
        this.getStyleClass().add("responsive-barralateral");
        this.getChildren().addAll(inicio, equipe, relatorios, metas);
    }
}