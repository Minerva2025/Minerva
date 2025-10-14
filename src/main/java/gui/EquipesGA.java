package gui;

import java.util.List;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Pdi;
import model.Usuario;

public class EquipesGA extends Application{
	
    private Usuario logado;

    public EquipesGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

	public void start(Stage equipesgaStage) {
		
		PdiDAO pdiDAO = new PdiDAO();
		ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
		 int totalColaboradores = colaboradorDAO.listAll().size();
		 
		 List<Pdi> todosPdis = pdiDAO.listAll();
		 
		 int pdiAtivoCount = 0;
		 int pdiInativoCount = 0;
		 int pdiConcluidoCount = 0;
		 
		 for (Pdi pdi : todosPdis) {
		        switch (pdi.getStatus()) {
		            case EM_ANDAMENTO, ATRASADO -> pdiAtivoCount++;
		            case NAO_INICIADO -> pdiInativoCount++;
		            case CONCLUIDO -> pdiConcluidoCount++;
		        }
		    }
		
		Text titulo = new Text("Setor");
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
		
		
		Text buscarColaboradores = new Text("Buscar colaboradores");
		buscarColaboradores.setId("buscarColaboradores");
		
		//barra de pesquisa
		
		TextField searchField = new TextField();
        searchField.setPromptText("Pesquisar");
        searchField.setPrefWidth(280);
        searchField.getStyleClass().add("search-field");
		
        Button searchButton = new Button("\uD83D\uDD0D");
        searchButton.getStyleClass().add("icon-button");
        
        
        Button filterButton = new Button("Filtrar");
        filterButton.getStyleClass().add("filter-button");
        
        
        HBox searchBar = new HBox(10, searchField, searchButton, filterButton);
        searchBar.setPadding(new Insets(10, 15, 10, 15));
        searchBar.getStyleClass().add("search-bar");
        
        //fim da barra de pesquisa
        
        VBox colaboradoresContainer = new VBox(90);
        colaboradoresContainer.setId("colaboradoresContainer");
        colaboradoresContainer.getChildren().addAll(buscarColaboradores, searchBar);
        
		VBox center = new VBox();
		center.setId("center");
		center.getChildren().addAll(titulo, container, blob1, blob2, blob3, colaboradoresContainer);

	    BarraLateralGA barra = new BarraLateralGA(logado);
	
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(barra, center);
		
		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("EquipesGA.css").toExternalForm());

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
	
		equipesgaStage.setScene(scene);
		equipesgaStage.setFullScreen(true);
		equipesgaStage.setFullScreenExitHint("");
		equipesgaStage.show();
	
		equipesgaStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
		    if (isNowFocused) {
		    	equipesgaStage.setFullScreen(true);
		    }
		});
	
	}
		
	public static void main (String[]args) {
		launch(args);
	}
}
