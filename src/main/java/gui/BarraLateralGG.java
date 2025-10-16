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

        // Botões
        Button inicio = new Button("Início");
        inicio.setId("inicio");
        inicio.setOnAction(e -> {
            Stage homeggStage = new Stage();
            new HomeGestorGeral(logado).start(homeggStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        

        Button equipe = new Button("Equipe");
//        equipe.setOnAction(e -> {
//            Stage equipesggStage = new Stage();
//            new EquipesGG(logado).start(equipesggStage);
//            
//            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
//            stageAtual.close();
//        });
        equipe.setId("equipe");

        ToggleButton relatorios = new ToggleButton("Relatórios");
        relatorios.setId("relatorios");

        Button metas = new Button("Metas");
        metas.setOnAction(e -> {
            Stage metasggStage = new Stage();
            new MetasGG(logado).start(metasggStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        metas.setId("metas");


        // organiza os botões na vertical com espaçamento
        this.setSpacing(30);
        this.getChildren().addAll(inicio, equipe, relatorios, metas);
    }
}
