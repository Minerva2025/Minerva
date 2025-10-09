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

        // Botões
        Button inicio = new Button("Início");
        inicio.setId("inicio");
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

        ToggleButton relatorios = new ToggleButton("Relatórios");
        relatorios.setId("relatorios");

        Button metas = new Button("Metas");
        metas.setOnAction(e -> {
            Stage metasStage = new Stage();
            new Metas(logado).start(metasStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        metas.setId("metas");

        ToggleButton avaliacoes = new ToggleButton("Avaliações");
        avaliacoes.setId("avaliacoes");

        // organiza os botões na vertical com espaçamento
        this.setSpacing(30);
        this.getChildren().addAll(inicio, equipe, relatorios, metas, avaliacoes);
    }
}
