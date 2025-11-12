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

        Button relatorios = new Button("Relatórios");
        relatorios.setId("relatorios");
        relatorios.setOnAction(e -> {
            Stage relatoriosgaStage = new Stage();
            new RelatoriosGA(logado).start(relatoriosgaStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        
        
        Button metas = new Button("Metas");
        metas.setId("metas");
        metas.setOnAction(e -> {
            Stage metasgaStage = new Stage();
            new MetasGA(logado).start(metasgaStage);
            
            Stage stageAtual = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        

        this.setSpacing(30);
        this.getChildren().addAll(inicio, equipe, relatorios, metas);
    }
}
