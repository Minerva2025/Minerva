package gui;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
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
    private StackPane boxChart1;
    private StackPane boxChart2;

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
		
		HBox tituloBox = new HBox(titulo);
		tituloBox.setAlignment(Pos.CENTER_LEFT);
		
		
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
		
        HBox chartsContainer = new HBox(30);
        chartsContainer.setId("chartsContainer");

        chartsContainer.setPrefWidth(Double.MAX_VALUE);
        
        VBox.setMargin(chartsContainer, new Insets(20, 45, 0, 45)); // Alterado: padding-left para 45px

  
        boxChart1 = new StackPane();
        boxChart1.setId("boxChart1");
        boxChart1.getStyleClass().add("card");
     
        HBox.setHgrow(boxChart1, Priority.ALWAYS);
        boxChart1.setMaxWidth(Double.MAX_VALUE);
        boxChart1.setMinWidth(0);

      
        boxChart2 = new StackPane();
        boxChart2.setId("boxChart2");
        boxChart2.getStyleClass().add("card");
    
        HBox.setHgrow(boxChart1, Priority.ALWAYS);
        boxChart2.setMaxWidth(Double.MAX_VALUE);
        boxChart2.setMinWidth(0); 

        chartsContainer.getChildren().addAll(boxChart1, boxChart2);
        

        double spacing = chartsContainer.getSpacing();
        boxChart1.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.5).subtract(spacing / 2.0)
        );
        boxChart2.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.5).subtract(spacing / 2.0)
        );

        createCharts(todosPdis);

     
				
		VBox setoresContainer = new VBox(60);
		setoresContainer.setId("setoresContainer");
		setoresContainer.setAlignment(Pos.TOP_LEFT); // Alterado: de TOP_CENTER para TOP_LEFT

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
		    searchBar.setPadding(new Insets(5, 0, 10, 0)); // Alterado: removido padding-left 80px
		    searchBar.setPrefWidth(600);
		    searchBar.getStyleClass().add("search-bar");
		    HBox.setHgrow(searchField, Priority.ALWAYS);

		    VBox tituloEbarra = new VBox(20, tituloSetor, searchBar);
		    tituloEbarra.setAlignment(Pos.CENTER_LEFT);
		    tituloEbarra.setPadding(new Insets(0, 0, 0, 0));

		    GridPane gridSetor = new GridPane();
		    gridSetor.setHgap(40);
		    gridSetor.setVgap(40);
		    gridSetor.setAlignment(Pos.TOP_LEFT); // Alterado: de CENTER para TOP_LEFT
		    gridSetor.setPadding(new Insets(20, 0, 60, 0));

		    Runnable atualizarGrid = () -> {
		        gridSetor.getChildren().clear();
		        String filtro = searchField.getText().toLowerCase();
		        int col = 0;
		        int row = 0;
		        boolean encontrou = false;

		        for (Colaborador c : colaboradoresSetor) {
		            if (c.getNome().toLowerCase().contains(filtro)) {
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
		        }
		    };

		    // Preenche inicialmente todos
		    atualizarGrid.run();

		    // Atualiza conforme o usuário digita
		    searchField.textProperty().addListener((obs, oldVal, newVal) -> atualizarGrid.run());

		    VBox setorBox = new VBox(20, tituloEbarra, gridSetor);
		    setorBox.setAlignment(Pos.TOP_LEFT); // Alterado: de TOP_CENTER para TOP_LEFT
		    setoresContainer.getChildren().add(setorBox);
		}
	
	    VBox center = new VBox(60);
	    center.setId("center");
	    center.setAlignment(Pos.TOP_LEFT); // Alterado: de TOP_CENTER para TOP_LEFT
	    center.setPadding(new Insets(60, 80, 80, 80)); // Já está com padding-left 80px
	    center.getChildren().addAll(tituloBox, container, chartsContainer, setoresContainer, blob1, blob3);

		ScrollPane scrollCenter = new ScrollPane(center);
		scrollCenter.setFitToWidth(true);
		scrollCenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
		scrollCenter.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollCenter.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
		
	    BarraLateralGG barra = new BarraLateralGG(logado);
	
		HBox root = new HBox();
		root.setId("root-equipes-gg");
		root.getChildren().addAll(barra, scrollCenter);
		
		scrollCenter.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		
		Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/EquipesGG.css").toExternalForm());

		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.06));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.03));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.025));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());
		
		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.35));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.01));
		blob1.setManaged(false);

		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.83));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.53));
		blob2.setManaged(false);

		StackPane.setAlignment(blob3, Pos.TOP_RIGHT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.48));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.062));
		blob3.setManaged(false);
		
		// Listener para redimensionamento responsivo
		scene.widthProperty().addListener((obs, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});
		
		scene.heightProperty().addListener((obs, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});
	
		equipesggStage.setScene(scene);
		equipesggStage.setFullScreen(true);
		equipesggStage.setFullScreenExitHint("");
		equipesggStage.show();
		
		// Aplicar estilos iniciais
		updateResponsiveStyles(scene);
	
		equipesggStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
		    if (isNowFocused) {
		    	equipesggStage.setFullScreen(true);
		    }
		});
	
	}
	
	private void updateResponsiveStyles(Scene scene) {
		double width = scene.getWidth();
		double height = scene.getHeight();
		
		// Remover classes de tamanho anteriores
		scene.getRoot().getStyleClass().removeAll("small-screen", "medium-screen", "large-screen", "extra-large-screen", "mobile-landscape", "tablet-portrait");
		
		// Adicionar classe baseada no tamanho da tela
		if (width < 768) { // Mobile
			scene.getRoot().getStyleClass().add("small-screen");
			if (width > height) {
				scene.getRoot().getStyleClass().add("mobile-landscape");
			}
		} else if (width < 1024) { // Tablet
			scene.getRoot().getStyleClass().add("medium-screen");
			if (height > width) {
				scene.getRoot().getStyleClass().add("tablet-portrait");
			}
		} else if (width < 1440) { // Desktop
			scene.getRoot().getStyleClass().add("large-screen");
		} else { // Telas grandes
			scene.getRoot().getStyleClass().add("extra-large-screen");
		}
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
    
    private void createCharts(List<Pdi> todosPdis) {
        boxChart1.getChildren().clear();
        boxChart2.getChildren().clear();

        // Gráfico de Pizza
        int concluidos = 0, ativos = 0, aIniciar = 0, atrasados = 0;
        for (Pdi pdi : todosPdis) {
            switch (pdi.getStatus()) {
                case CONCLUIDO -> concluidos++;
                case EM_ANDAMENTO -> ativos++;
                case NAO_INICIADO -> aIniciar++;
                case ATRASADO -> atrasados++;
                default -> {} // Necessário para compilação
            }
        }
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("PDIs concluídos", concluidos),
            new PieChart.Data("PDIs ativos", ativos),
            new PieChart.Data("PDIs a iniciar", aIniciar),
            new PieChart.Data("PDIs atrasados", atrasados)
        );
        
        final PieChart pieChart = new PieChart(pieChartData);
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(false);
        pieChart.setLegendSide(javafx.geometry.Side.RIGHT);
        
        // Adicionar classe para controle responsivo
        pieChart.getStyleClass().add("pie-chart-responsive");
        
        boxChart1.getChildren().add(pieChart);
        
        // Gráfico de Barras
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Divisão");
        yAxis.setLabel("Evolução");

        // Ocultar rótulos e ticks do eixo Y
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        
        barChart.setStyle("-fx-category-gap: 70px; -fx-bar-gap: 5px;");
        
        // Adicionar classe para controle responsivo
        barChart.getStyleClass().add("bar-chart-responsive");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("PDI's"); 

        // Dados 
        XYChart.Data<String, Number> dataDm = new XYChart.Data<>("Dm", concluidos);
        dataDm.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-bar-fill: #A8E6CF;");
            }
        });
       
        XYChart.Data<String, Number> dataCm = new XYChart.Data<>("Cm", ativos);
        dataCm.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-bar-fill: #70C1B3;");
            }
        });

        XYChart.Data<String, Number> dataSm = new XYChart.Data<>("Sm", aIniciar);
        dataSm.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) {
                newNode.setStyle("-fx-bar-fill: #2E8B78;");
            }
        });
        
        // Adiciona os dados
        series.getData().addAll(dataDm, dataCm, dataSm);

        // Adiciona a série ao gráfico
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        boxChart2.getChildren().add(barChart);
    }

		
	public static void main (String[]args) {
		launch(args);
	}
}