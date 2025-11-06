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

public class EquipesGA extends Application{
	
    private Usuario logado;

    public EquipesGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

	public void start(Stage equipesgaStage) {
		
		PdiDAO pdiDAO = new PdiDAO();
		ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

		List<Colaborador> colaboradoresSetor = colaboradorDAO.findBySetor(logado.getSetor());
		int totalColaboradores = colaboradoresSetor.size();
 
		 int pdiAtivoCount = 0;
		 int pdiInativoCount = 0;
		 int pdiConcluidoCount = 0;
		 
		 for (Colaborador c : colaboradoresSetor) {
		     List<Pdi> pdis = pdiDAO.findByColaborador(c.getId());
		     for (Pdi pdi : pdis) {
		          switch (pdi.getStatus()) {
		             case EM_ANDAMENTO, ATRASADO -> pdiAtivoCount++;
		             case NAO_INICIADO -> pdiInativoCount++;
		             case CONCLUIDO -> pdiConcluidoCount++;
		         }
		     }
		 }
		
		Text titulo = new Text(logado.getSetor());
		titulo.setId("titulo");

		HBox tituloBox = new HBox(titulo);
		tituloBox.setAlignment(Pos.CENTER_LEFT);
		
//		Ellipse blob1 = new Ellipse(155, 155);
//		blob1.setId("blob1");
//		
//		Ellipse blob2 = new Ellipse(20, 20);
//		blob2.setId("blob2");
//		
//		Ellipse blob3 = new Ellipse(40, 40);
//		blob3.setId("blob3");
//		
//		GaussianBlur blur = new GaussianBlur(40);
//		blob1.setEffect(blur);
//		blob2.setEffect(blur);
//		blob3.setEffect(blur);
		
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
		
		
//		Text buscarColaboradores = new Text("Buscar colaboradores");
//		buscarColaboradores.setId("buscarColaboradores");

		// Envolve o texto em um HBox alinhado à esquerda
//		HBox buscarBox = new HBox(buscarColaboradores);
//		buscarBox.setAlignment(Pos.CENTER_LEFT);
//		buscarBox.setPadding(new Insets(0, 0, 0, 80));

		TextField searchField = new TextField();
		searchField.setPromptText("Pesquisar");
		searchField.setPrefWidth(280);
		searchField.getStyleClass().add("search-field");

		Button searchButton = new Button("\uD83D\uDD0D");
		searchButton.getStyleClass().add("icon-button");

//		Button filterButton = new Button("Filtrar");
//		filterButton.getStyleClass().add("filter-button");

		HBox searchBar = new HBox(10, searchField, searchButton);
		searchBar.setPadding(new Insets(10, 0, 10, 80)); // recuo igual
		searchBar.setPrefWidth(600);
		searchBar.getStyleClass().add("search-bar");
		searchBar.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(searchField, Priority.ALWAYS);

		searchBar.setSpacing(0);
        
		searchBar.setAlignment(Pos.CENTER_RIGHT);

        //fim da barra de pesquisa
        
		VBox colaboradoresContainer = new VBox(90);
		colaboradoresContainer.setId("colaboradoresContainer");
		colaboradoresContainer.getChildren().add(searchBar);
        
        GridPane gridColaboradores = new GridPane();
        gridColaboradores.setHgap(40);
        gridColaboradores.setVgap(40);
        gridColaboradores.setAlignment(Pos.CENTER);
        gridColaboradores.setPadding(new Insets(20, 80, 60, 80));
        gridColaboradores.setMaxWidth(1400);
	
        Runnable atualizarGrid = () -> {
            gridColaboradores.getChildren().clear();
            String filtro = searchField.getText().toLowerCase();

            int col = 0;
            int row = 0;

            for (Colaborador c : colaboradoresSetor) {
                if (c.getNome().toLowerCase().contains(filtro)) {
                    double progresso = calcularProgressoMedio(pdiDAO, c.getId());
                    VBox balao = criarBalaoColaborador(c, progresso);
                    balao.prefWidthProperty().bind(gridColaboradores.widthProperty().divide(2));
                    gridColaboradores.add(balao, col, row);

                    col++;
                    if (col > 1) {
                        col = 0;
                        row++;
                    }
                }
            }
        };

        atualizarGrid.run();

        searchField.textProperty().addListener((obs, oldVal, newVal) -> atualizarGrid.run());
	
	    colaboradoresContainer.getChildren().add(gridColaboradores);
	    colaboradoresContainer.setSpacing(40);
	    colaboradoresContainer.setPadding(new Insets(30, 0, 0, 0));
	
	    VBox center = new VBox(60);
	    center.setId("center");
	    center.setAlignment(Pos.TOP_CENTER);
	    center.setPadding(new Insets(60, 80, 80, 80));
	    center.getChildren().addAll(tituloBox, container, colaboradoresContainer);
//	    center.getChildren().addAll(tituloBox, container, blob1, blob2, blob3, colaboradoresContainer);

		ScrollPane scrollCenter = new ScrollPane(center);
		scrollCenter.setFitToWidth(true);
		scrollCenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollCenter.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollCenter.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		
	    BarraLateralGA barra = new BarraLateralGA(logado);
	
		HBox root = new HBox();
		root.setStyle("-fx-background-color: #1E1E1E");
		root.getChildren().addAll(barra, scrollCenter);
		
		scrollCenter.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/EquipesGA.css").toExternalForm());

//		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
//		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 
//	
//		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
//		blob2.radiusYProperty().bind(blob2.radiusXProperty());
//	
//		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
//		blob3.radiusYProperty().bind(blob3.radiusXProperty());
//	
//		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
//		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
//		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));
//	
//		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
//		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.4));
//		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.3));
//	
//		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
//		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.52));
//		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.07));
	
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
	private double calcularProgressoMedio(PdiDAO pdiDAO, int colaboradorId) {
        List<Pdi> pdis = pdiDAO.findByColaborador(colaboradorId);
        if (pdis.isEmpty()) return 0.0;

        double soma = 0;
        for (Pdi p : pdis) {
            switch (p.getStatus()) {
                case CONCLUIDO -> soma += 1.0;
                case EM_ANDAMENTO, ATRASADO -> soma += 0.5;
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
        progressoBar.setPrefWidth(260);
        progressoBar.setId("barra-progresso");

        Text progressoTxt = new Text((int) (progresso * 100) + "%");
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
