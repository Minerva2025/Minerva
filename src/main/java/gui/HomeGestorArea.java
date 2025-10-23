package gui;

import java.util.List;

import dao.ColaboradorDAO;
import dao.PdiDAO;
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
import model.Pdi;
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
		
		String primeiroNome = logado.getNome().split(" ")[0];
		 ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
		 PdiDAO pdiDAO = new PdiDAO();
		 
		 int totalColaboradores = colaboradorDAO.listAll().size();
		 
		 List<Pdi> todosPdis = pdiDAO.listAll();
		 
		 int pdiAtivoCount = 0; // EM_ANDAMENTO e ATRASADO
		 int pdiInativoCount = 0; // NAO_INICIADO
		 int pdiConcluidoCount = 0; // CONCLUIDO
		 
		 for (Pdi pdi : todosPdis) {
		        switch (pdi.getStatus()) {
		            case EM_ANDAMENTO, ATRASADO -> pdiAtivoCount++;
		            case NAO_INICIADO -> pdiInativoCount++;
		            case CONCLUIDO -> pdiConcluidoCount++;
		        }
		    }
		
		Text titulo = new Text("Bem-vindo " + primeiroNome + "!");
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
		
		Text colaboradores = new Text("Colaboradores: " + totalColaboradores);
		colaboradores.setId("colaboradores");
		Text pdiAtivo = new Text("PDIs ativos: " + pdiAtivoCount);
		pdiAtivo.setId("pdiAtivo");
		Text pdiInativo = new Text("PDIs não iniciados: " + pdiInativoCount);
		pdiInativo.setId("pdiInativo");
		Text pdiConcluido = new Text("PDIs concluídos: " + pdiConcluidoCount);
		pdiConcluido.setId("pdiConcluido");
		
		HBox container = new HBox(150);
		container.setId("container");
		container.getChildren().addAll(colaboradores, pdiAtivo, pdiInativo, pdiConcluido);
		
		VBox center = new VBox();
		center.setId("center");
		center.getChildren().addAll(titulo, container, blob1, blob2, blob3);
		
		BarraLateralGA barra = new BarraLateralGA(logado);
		
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(barra, center);
		
		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/HomeGestorArea.css").toExternalForm());
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
		
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.setFullScreenExitHint("");
		stage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}