package gui; 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Pdi;
import model.Status;
import model.Usuario;
import dao.ColaboradorDAO;
import model.Colaborador;
import dao.PdiDAO;
import javafx.scene.control.Button;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.List;
import util.PDFExporter;
import util.POIExcelExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MetasGA extends Application {

    private TableView<Pdi> tabela;
    private ObservableList<Pdi> dados;
    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    
    private VBox criarCaixaMeta(String titulo, int valor) {
	    Text tituloText = new Text(titulo);
	    tituloText.setStyle("-fx-fill: #cccccc; -fx-font-size: 12px;");

	    Text valorText = new Text(String.valueOf(valor));
	    valorText.setStyle("-fx-fill: #49ACA0; -fx-font-size: 24px; -fx-font-weight: bold;");

	    VBox box = new VBox(5, tituloText, valorText);
	    box.setStyle("-fx-alignment: center;");
	    return box;
	}
 
    public MetasGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage metasgaStage) {
    	
    	PdiDAO pdiDAO = new PdiDAO();
		ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

		List<Colaborador> colaboradoresSetor = colaboradorDAO.findBySetor(logado.getSetor());
		int totalColaboradores = colaboradoresSetor.size();
 
		 int pdiAtivoCount = 0;
		 int pdiInativoCount = 0;
		 int pdiConcluidoCount = 0;
		 int pdiAtrasadoCount = 0;
		 
		 for (Colaborador c : colaboradoresSetor) {
		     List<Pdi> pdis = pdiDAO.findByColaborador(c.getId());
		     for (Pdi pdi : pdis) {
		    	 switch (pdi.getStatus()) {
		    	    case EM_ANDAMENTO -> pdiAtivoCount++;
		    	    case ATRASADO -> pdiAtrasadoCount++;
		    	    case NAO_INICIADO -> pdiInativoCount++;
		    	    case CONCLUIDO -> pdiConcluidoCount++;
		    	}
		     }
		 }

    	Text colaboradores = new Text("Colaboradores: " + totalColaboradores);
		colaboradores.setId("colaboradores");
	
		HBox hboxColaboradores = new HBox(colaboradores);
		hboxColaboradores.setAlignment(Pos.TOP_LEFT);
		hboxColaboradores.setPadding(new Insets(0, 0, 0, 10));
		hboxColaboradores.getStyleClass().add("responsive-hbox");
		

		VBox andamentoBox = criarCaixaMeta("Metas em andamento", pdiAtivoCount);
		VBox concluidasBox = criarCaixaMeta("Metas concluídas", pdiConcluidoCount);
		VBox atrasadasBox = criarCaixaMeta("Metas atrasadas", pdiAtrasadoCount);

		HBox metasBox = new HBox(50);
		metasBox.setId("metas-box");
		metasBox.setStyle("-fx-alignment: center; -fx-font-family: 'Kodchasan';");
		metasBox.getChildren().addAll(andamentoBox, concluidasBox, atrasadasBox);
		metasBox.getStyleClass().add("responsive-metas-box");

		StackPane boxChart1 = new StackPane();
		boxChart1.setId("boxChart1");
		boxChart1.getStyleClass().add("card");
		boxChart1.setStyle("-fx-alignment: center;");

		boxChart1.getChildren().add(metasBox);

		HBox chartsContainer = new HBox(30);
		chartsContainer.setId("chartsContainer");
		chartsContainer.setPrefWidth(Double.MAX_VALUE);
		chartsContainer.setStyle("-fx-alignment: center;");
		chartsContainer.getStyleClass().add("responsive-charts-container");

		HBox.setHgrow(boxChart1, Priority.ALWAYS);
		boxChart1.setMaxWidth(500);
		boxChart1.setMinWidth(400);

		chartsContainer.getChildren().addAll(boxChart1);

		BarraLateralGA barra = new BarraLateralGA(logado);

        VBox coluna1 = new VBox();
        coluna1.setId("coluna1");
        coluna1.setAlignment(Pos.TOP_CENTER);
        coluna1.setSpacing(15);
        coluna1.setPadding(new Insets(15));

        Text titulo = new Text("Gerenciar Metas");
        titulo.setId("titulo");
        VBox.setMargin(titulo, new Insets(20, 0, 30, 0));
        
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

        tabela = new TableView<>();
        carregarTabela(); 
        tabela.setMinHeight(350);
        tabela.setMaxHeight(350);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Pdi, String> colObjetivo = new TableColumn<>("Descrição");
        colObjetivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getObjetivo()));

        TableColumn<Pdi, String> colDivisao = new TableColumn<>("Divisão");
        colDivisao.setCellValueFactory(c -> {
            Colaborador colab = colaboradorDAO.getColaboradorById(c.getValue().getColaborador_id());
            return new javafx.beans.property.SimpleStringProperty(colab != null ? colab.getSetor() : "N/D");
        });

        TableColumn<Pdi, String> colResponsavel = new TableColumn<>("Responsável");
        colResponsavel.setCellValueFactory(c -> {
            Colaborador colab = colaboradorDAO.getColaboradorById(c.getValue().getColaborador_id());
            return new javafx.beans.property.SimpleStringProperty(colab != null ? colab.getNome() : "Desconhecido");
        });
        
        TableColumn<Pdi, String> colPrazo = new TableColumn<>("Prazo");
        colPrazo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrazo().toString()));

        TableColumn<Pdi, Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (status) {
                        case NAO_INICIADO -> { setText("Não Iniciado"); setStyle("-fx-text-fill: gray;"); }
                        case EM_ANDAMENTO -> { setText("Em Andamento"); setStyle("-fx-text-fill: orange;"); }
                        case CONCLUIDO -> { setText("Concluído"); setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); }
                        case ATRASADO -> { setText("Atrasado"); setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); }
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });
               
        tabela.getColumns().addAll( colResponsavel,colDivisao, colObjetivo, colPrazo, colStatus);
        
        // Adicionar classes responsivas seguindo o mesmo padrão
        titulo.getStyleClass().add("responsive-title");
        tabela.getStyleClass().add("responsive-table");
        coluna1.getStyleClass().add("responsive-coluna");
        
        coluna1.getChildren().addAll(titulo, hboxColaboradores, chartsContainer, tabela, blob1, blob2, blob3);

        Button btnVerMetas = new Button("Ver Metas");
        btnVerMetas.getStyleClass().add("btnVerMetas"); 
        btnVerMetas.getStyleClass().add("responsive-button");
        
        Button btnExportar = new Button("Exportar PDF");
        btnExportar.getStyleClass().add("btnExportarMetas");
        btnExportar.getStyleClass().add("responsive-button");

        Button btnExportarExcel = new Button("Exportar Excel");
        btnExportarExcel.getStyleClass().add("btnExportarMetas");
        btnExportarExcel.getStyleClass().add("responsive-button");

        HBox containerBotoes = new HBox(15, btnVerMetas, btnExportar, btnExportarExcel);
        containerBotoes.setAlignment(Pos.CENTER);
        containerBotoes.setPadding(new Insets(10, 0, 20, 0));
        containerBotoes.getStyleClass().add("responsive-botoes-container");

        btnVerMetas.setOnAction(e -> {
            MetasGATotais totalPage = new MetasGATotais();

            List<Node> telaOriginal = new ArrayList<>(coluna1.getChildren());

            VBox novaTela = totalPage.criarPagina(
                coluna1,
                logado,
                telaOriginal.toArray(new Node[0])
            );

            coluna1.getChildren().clear();
            coluna1.getChildren().add(novaTela);
        });
        
        btnExportar.setOnAction(e -> {
    		javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    		fileChooser.setTitle("Salvar relatório PDF");
    		
    		String fileName = "relatorio_metas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";
    		fileChooser.setInitialFileName(fileName);
    		
    		File file = fileChooser.showSaveDialog(tabela.getScene().getWindow());
            if (file == null) {
                System.out.println("Operação cancelada pelo usuário.");
                return;
            }
    		
    		List<Pdi> todasMetas = pdiDAO.listAll().stream()
    		        .filter(pdi -> {
    		            Colaborador colab = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
    		            return colab != null && logado.getSetor().equalsIgnoreCase(colab.getSetor());
    		        })
    		        .sorted(Comparator.comparing(Pdi::getPrazo))
    		        .toList();

    		boolean sucesso = PDFExporter.exportarPDIsParaPDF(
    		    FXCollections.observableArrayList(todasMetas),
    		    file.getAbsolutePath()
    		);

    	});

        btnExportarExcel.setOnAction(event -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Escolha onde salvar o arquivo Excel");

            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx")
            );

            String fileName = "metas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
            fileChooser.setInitialFileName(fileName);

            File pastaDownloads = new File(System.getProperty("user.home"), "Downloads");
            if (pastaDownloads.exists()) {
                fileChooser.setInitialDirectory(pastaDownloads);
            }

            File arquivoSelecionado = fileChooser.showSaveDialog(tabela.getScene().getWindow());
            if (arquivoSelecionado == null) {
                System.out.println("Operação cancelada pelo usuário.");
                return;
            }

            if (!arquivoSelecionado.getName().toLowerCase().endsWith(".xlsx")) {
                arquivoSelecionado = new File(arquivoSelecionado.getAbsolutePath() + ".xlsx");
            }

            List<Pdi> todasMetas = pdiDAO.listAll().stream()
                    .filter(pdi -> {
                        Colaborador colab = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
                        return colab != null && logado.getSetor().equalsIgnoreCase(colab.getSetor());
                    })
                    .sorted(Comparator.comparing(Pdi::getPrazo))
                    .toList();
            FXCollections.observableArrayList(todasMetas);

            POIExcelExporter.exportarParaExcel(arquivoSelecionado, todasMetas);
        });

        
        coluna1.getChildren().add(containerBotoes);

        HBox root = new HBox();
        root.getChildren().addAll(barra, coluna1);
        root.setStyle("-fx-background-color: #1E1E1E");

        coluna1.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/MetasGA.css").toExternalForm());

        // Listener para redimensionamento responsivo - MESMO PADRÃO
        scene.widthProperty().addListener((obss, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        scene.heightProperty().addListener((obss, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());

		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(0.05));
		blob1.setManaged(false);

		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.2));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.98));
		blob2.setManaged(false);

		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.6));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.1));
		blob3.setManaged(false);
        
        metasgaStage.setScene(scene);
        metasgaStage.setFullScreen(true);
        metasgaStage.setFullScreenExitHint("");
        metasgaStage.setTitle("Gerenciamento de Metas (PDIs)");
        metasgaStage.show();

        // Aplicar estilos iniciais - MESMO PADRÃO
        updateResponsiveStyles(scene);

        Timeline atualizador = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            carregarTabela();
        }));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();
    }

    // MÉTODO RESPONSIVO - MESMO PADRÃO
    private void updateResponsiveStyles(Scene scene) {
        double width = scene.getWidth();
        double height = scene.getHeight();
        
        // Remover classes de tamanho anteriores
        scene.getRoot().getStyleClass().removeAll("small-screen", "medium-screen", "large-screen", "extra-large-screen", "mobile-landscape");
        
        // Adicionar classe baseada no tamanho da tela - MESMO PADRÃO
        if (width < 768) { // Mobile
            scene.getRoot().getStyleClass().add("small-screen");
            if (width > height) {
                scene.getRoot().getStyleClass().add("mobile-landscape");
            }
        } else if (width < 1024) { // Tablet
            scene.getRoot().getStyleClass().add("medium-screen");
        } else if (width < 1440) { // Desktop
            scene.getRoot().getStyleClass().add("large-screen");
        } else { // Telas grandes
            scene.getRoot().getStyleClass().add("extra-large-screen");
        }
    }

    private void carregarTabela() {
        List<Pdi> todos = pdiDAO.listAll();

        List<Pdi> filtrados = todos.stream()
            .filter(pdi -> {
                Colaborador colab = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
                return colab != null && colab.getSetor() != null 
                       && colab.getSetor().equalsIgnoreCase(logado.getSetor());
            })
            .sorted(Comparator.comparing(Pdi::getPrazo))
            .limit(8)
            .collect(Collectors.toList());

        dados = FXCollections.observableArrayList(filtrados);
        tabela.setItems(dados);
    }
}	