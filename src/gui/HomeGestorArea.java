package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;
import javafx.scene.shape.Ellipse;
import javafx.scene.effect.GaussianBlur;

public class HomeGestorArea extends Application{
	
    private Usuario logado;

    public HomeGestorArea(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }
	
	
	@Override
	public void start(Stage stage) {
		
		Text titulo = new Text("BEM-VINDO");
		titulo.setId("titulo");
		
		Ellipse blob1 = new Ellipse();
		blob1.setId("blob1");
		
		Ellipse blob2 = new Ellipse();
		blob2.setId("blob2");
		
		Ellipse blob3 = new Ellipse();
		blob3.setId("blob3");
		
		GaussianBlur blur = new GaussianBlur(40);
		blob1.setEffect(blur);
		blob2.setEffect(blur);
		blob3.setEffect(blur);
		
		Text colaboradores = new Text("Colaboradores:");
		colaboradores.setId("colaboradores");
		
		Text pdiAtivo = new Text("PDIs ativos:");
		pdiAtivo.setId("pdiAtivo");
		
		HBox container = new HBox(150);
		container.setId("container");
		container.getChildren().addAll(colaboradores, pdiAtivo);
		
		VBox center = new VBox();
		center.setId("center");
		center.getChildren().addAll(titulo, container, blob1, blob2, blob3);
		
		ToggleButton inicio = new ToggleButton("Início");
		inicio.setId("inicio");
		ToggleButton equipe = new ToggleButton("Equipe");
		equipe.setId("equipe");
		ToggleButton relatorios = new ToggleButton("Relatórios");
		relatorios.setId("relatorios");
		ToggleButton metas = new ToggleButton("Metas");
		metas.setId("metas");
		
		VBox left = new VBox(30, inicio, equipe, relatorios, metas);
		left.setId("left");
		
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(left, center);
		
		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		left.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("HomeGestorArea.css").toExternalForm());
		
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
		
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}