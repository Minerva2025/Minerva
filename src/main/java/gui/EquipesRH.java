package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;

public class EquipesRH extends Application{
	
    private Usuario logado;

    public EquipesRH(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

	public void start(Stage equipesrhStage) {
				
		Text titulo = new Text("Gerenciar Usuarios/Colaboradores");
		titulo.setId("titulo");
		
		Ellipse blob1 = new Ellipse(155, 155);
		blob1.setId("blob1");
		
		Ellipse blob2 = new Ellipse(20, 20);
		blob2.setId("blob2");
		
		Ellipse blob3 = new Ellipse(40, 40);
		blob3.setId("blob3");
		
		GaussianBlur blur = new GaussianBlur(40);
		blob1.setEffect(blur);
		blob2.setEffect(blur);
		blob3.setEffect(blur);
		
		Button usuariosButton = new Button("Cadastrar Usuários - RH, Gerente Geral e Gerente de Área");
		usuariosButton.getStyleClass().add("botoes");
		
		usuariosButton.setOnAction(e -> {
            Stage cadastroUStage = new Stage();
            new CadastroUsuarios(logado).start(cadastroUStage);
        });
		
		Button colaboradoresButton = new Button("Cadastrar Colaboradores da empresa");
		colaboradoresButton.getStyleClass().add("botoes");
		
		colaboradoresButton.setOnAction(e -> {
            Stage cadastroCStage = new Stage();
            new CadastroColaborador(logado).start(cadastroCStage);
        });
		
		
		
		VBox container = new VBox(15);
		container.setAlignment(Pos.CENTER); 
		container.setId("container");
		container.getChildren().addAll(usuariosButton, colaboradoresButton);
		
		VBox tituloEBotoes = new VBox(20);
		tituloEBotoes.setAlignment(Pos.CENTER);
		tituloEBotoes.getChildren().addAll(titulo, container);
		VBox.setMargin(titulo, new Insets(0, 0, 30, 0));
		
		VBox center = new VBox();
		center.setId("center");
		center.getChildren().addAll(tituloEBotoes, blob1, blob2, blob3);

		
	    BarraLateralRH barra = new BarraLateralRH(logado);
	
		
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(barra, center);
		
		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/HomeRH.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/EquipesRH.css").toExternalForm());



		
		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 
	
		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());
	
		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());
	
		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));
	
		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.4));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.3));
	
		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.52));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.07));
	
	
		equipesrhStage.setScene(scene);
		equipesrhStage.setFullScreen(true);
		equipesrhStage.setFullScreenExitHint("");
		equipesrhStage.show();
	
		equipesrhStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
		    if (isNowFocused) {
		    	equipesrhStage.setFullScreen(true);
		    }
		});
	
	}
		
	public static void main (String[]args) {
		launch(args);
	}
}
