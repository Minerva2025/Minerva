package gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
		
		Ellipse blob1 = new Ellipse(155, 155);
		blob1.setId("blob1");
		
		Ellipse blob2 = new Ellipse(40, 40);
		blob2.setId("blob2");
		
		Ellipse blob3 = new Ellipse(40, 40);
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
		
		VBox coluna1 = new VBox();
		coluna1.setId("coluna1");
		coluna1.getChildren().addAll(titulo, container, blob1, blob2, blob3);
		
		ToggleButton inicio = new ToggleButton("Início");
		inicio.setId("inicio");
		ToggleButton equipe = new ToggleButton("Equipe");
		equipe.setId("equipe");
		ToggleButton relatorios = new ToggleButton("Relatórios");
		relatorios.setId("relatorios");
		ToggleButton metas = new ToggleButton("Metas");
		metas.setId("metas");
		
		VBox coluna2 = new VBox(30, inicio, equipe, relatorios, metas);
		coluna2.setId("coluna2");
		
		HBox root = new HBox();
		root.getChildren().addAll(coluna2, coluna1);
		
		coluna1.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		coluna2.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("HomeGestorArea.css").toExternalForm());
		
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}