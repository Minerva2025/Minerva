package gui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Usuario;

public class BarraLateralGA extends VBox {

    private Usuario logado;

    public BarraLateralGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
        this.setId("left");

        Button inicio = new Button("Início");
        inicio.setId("inicio");
        inicio.setOnAction(e -> {
            Stage homegaStage = new Stage();
            new HomeGestorArea(logado).start(homegaStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        
        Button equipe = new Button("Equipe");
        equipe.setOnAction(e -> {
            Stage equipesgaStage = new Stage();
            new EquipesGA(logado).start(equipesgaStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        equipe.setId("equipe");

        ToggleButton relatorios = new ToggleButton("Relatórios");
        relatorios.setId("relatorios");

        ToggleButton metas = new ToggleButton("Metas");
        metas.setId("metas");

        this.setSpacing(30);
        this.getChildren().addAll(inicio, equipe, relatorios, metas);
    }
}
