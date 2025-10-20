package gui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Colaborador;
import model.Pdi;
import model.Usuario;

public class EquipesGG extends Application{
	
    private Usuario logado;

    public EquipesGG(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

	public void start(Stage equipesggStage) {
		
		PdiDAO pdiDAO = new PdiDAO();
		ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

		List<Colaborador> colaboradorescard = colaboradorDAO.listAll();
		
		Map<String, List<Colaborador>> colaboradoresPorSetor = colaboradorescard.stream()
			    .collect(Collectors.groupingBy(Colaborador::getSetor));
		
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
		
		Text titulo = new Text("Visão Geral");
		titulo.getStyleClass().add("titulo");

		HBox tituloBox = new HBox(titulo);
		tituloBox.setAlignment(Pos.CENTER_LEFT);
		
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
				
		VBox setoresContainer = new VBox(60);
		setoresContainer.setId("setoresContainer");
		setoresContainer.setAlignment(Pos.TOP_CENTER);

		for (Map.Entry<String, List<Colaborador>> entry : colaboradoresPorSetor.entrySet()) {
		    String nomeSetor = entry.getKey();
		    List<Colaborador> colaboradoresSetor = entry.getValue();

		    Text tituloSetor = new Text(nomeSetor);
		    tituloSetor.getStyleClass().add("titulo");

		    TextField searchField = new TextField();
		    searchField.setPromptText("Pesquisar");
		    searchField.setPrefWidth(280);
		    searchField.getStyleClass().add("search-field");

		    Button searchButton = new Button("\uD83D\uDD0D");
		    searchButton.getStyleClass().add("icon-button");

		    HBox searchBar = new HBox(10, searchField, searchButton);
		    searchBar.setAlignment(Pos.CENTER_LEFT);
		    searchBar.setPadding(new Insets(5, 0, 10, 80));
		    searchBar.setPrefWidth(600);
		    searchBar.getStyleClass().add("search-bar");
		    HBox.setHgrow(searchField, Priority.ALWAYS);

		    VBox tituloEbarra = new VBox(20, tituloSetor, searchBar);
		    tituloEbarra.setAlignment(Pos.CENTER_LEFT);
		    tituloEbarra.setPadding(new Insets(0, 0, 0, 0	));

		    GridPane gridSetor = new GridPane();
		    gridSetor.setHgap(40);
		    gridSetor.setVgap(40);
		    gridSetor.setAlignment(Pos.CENTER);
		    gridSetor.setPadding(new Insets(20, 0, 60, 0));

		    int col = 0;
		    int row = 0;
		    for (Colaborador c : colaboradoresSetor) {
		        double progresso = calcularProgressoMedio(pdiDAO, c.getId());
		        VBox balao = criarBalaoColaborador(c, progresso);
		        balao.prefWidthProperty().bind(gridSetor.widthProperty().divide(2));
		        gridSetor.add(balao, col, row);

		        col++;
		        if (col > 1) {
		            col = 0;
		            row++;
		        }
		    }

		    VBox setorBox = new VBox(20, tituloEbarra, gridSetor);
		    setorBox.setAlignment(Pos.TOP_CENTER);
		    setoresContainer.getChildren().add(setorBox);
		}
	
	    VBox center = new VBox(60);
	    center.setId("center");
	    center.setAlignment(Pos.TOP_CENTER);
	    center.setPadding(new Insets(60, 80, 80, 80));
	    center.getChildren().addAll(tituloBox, container, setoresContainer);

		ScrollPane scrollCenter = new ScrollPane(center);
		scrollCenter.setFitToWidth(true);
		scrollCenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollCenter.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollCenter.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		
	    BarraLateralGG barra = new BarraLateralGG(logado);
	
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(barra, scrollCenter);
		
		scrollCenter.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("EquipesGG.css").toExternalForm());

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
	
		equipesggStage.setScene(scene);
		equipesggStage.setFullScreen(true);
		equipesggStage.setFullScreenExitHint("");
		equipesggStage.show();
	
		equipesggStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
		    if (isNowFocused) {
		    	equipesggStage.setFullScreen(true);
		    }
		});
	
	}
	private double calcularProgressoMedio(PdiDAO pdiDAO, int colaboradorId) {
        List<Pdi> pdis = pdiDAO.findByColaborador(colaboradorId);
        if (pdis.isEmpty()) return 0.0;

        double soma = 0;
        for (Pdi p : pdis) {
            switch (p.getStatus()) {
                case CONCLUIDO -> soma += 1.0;
                case EM_ANDAMENTO -> soma += 0.5;
                case ATRASADO -> soma -= 0.25;
                case NAO_INICIADO -> soma += 0.0;
            }
        }
        return soma / pdis.size();
    }

    private VBox criarBalaoColaborador(Colaborador colaborador, double progresso) {
        VBox card = new VBox(8);
        card.getStyleClass().add("colaborador-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);

        Text nome = new Text(colaborador.getNome());
        nome.getStyleClass().add("colaborador-nome");


        Text setor = new Text(colaborador.getSetor());
        setor.getStyleClass().add("colaborador-setor");

        Text cargo = new Text(colaborador.getCargo());
        cargo.getStyleClass().add("colaborador-cargo");

        ProgressBar progressoBar = new ProgressBar(progresso);
        progressoBar.setMaxWidth(Double.MAX_VALUE); 
        HBox.setHgrow(progressoBar, Priority.ALWAYS);
        progressoBar.setId("barra-progresso");
        

        Text progressoTxt = new Text(String.format("%.0f%%", progresso * 100));
        progressoTxt.getStyleClass().add("colaborador-progresso");

        HBox progressoBox = new HBox(10, progressoBar, progressoTxt);
        progressoBox.setAlignment(Pos.CENTER_LEFT);
        
        Text progressotexto = new Text("Progresso:");
        progressotexto.getStyleClass().add("colaborador-progresso");
        
        

        card.getChildren().addAll(nome, setor, cargo, progressotexto, progressoBox);
        return card;
    }
		
	public static void main (String[]args) {
		launch(args);
	}
}
