package gui; 

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ScrollPane;
import model.Pdi;
import model.Status;
import model.Usuario;
import util.PDFExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dao.ColaboradorDAO;
import model.Colaborador;
import dao.PdiDAO;
import dao.ColaboradorDAO;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import util.POIExcelExporter;

import java.util.*;
import java.util.stream.Collectors;

public class MetasGG extends Application {

    private TableView<Pdi> tabela;
    private ObservableList<Pdi> dados;
    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    
    private Label alerta1, alerta2, alerta3, alerta4;
    private XYChart.Data<String, Number> dataD, dataS, dataM, dataF, dataP;
 

    public MetasGG(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage metasggStage) {

        BarraLateralGG barra = new BarraLateralGG(logado);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox coluna1 = new VBox();
        coluna1.setId("coluna1");
        coluna1.setAlignment(Pos.TOP_CENTER);
        coluna1.setSpacing(15);
        coluna1.setPadding(new Insets(15));
        scrollPane.setContent(coluna1);
        
        Text titulo = new Text("Gerenciar Metas");
        titulo.setId("titulo");
        titulo.setTextAlignment(TextAlignment.CENTER);
        titulo.setStyle("-fx-font-size: 32px; -fx-fill: white;");
        VBox.setMargin(titulo, new Insets(4, 0, 30, 0));

        CategoryAxis eixoX = new CategoryAxis();
        eixoX.setTickLabelsVisible(false);
        eixoX.setTickMarkVisible(false);

        NumberAxis eixoY = new NumberAxis(0, 100, 20);
        eixoY.setAutoRanging(false);

        BarChart<String, Number> grafico = new BarChart<>(eixoX, eixoY);
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        grafico.setStyle("-fx-background-color: transparent;");
        grafico.setCategoryGap(0);
        grafico.setBarGap(-15);
        grafico.setPrefWidth(270);
        grafico.setPrefHeight(230);

        XYChart.Series<String, Number> serieD = new XYChart.Series<>();
        dataD = new XYChart.Data<>("Desenvolvimento", 0);
        serieD.getData().add(dataD);

        XYChart.Series<String, Number> serieS = new XYChart.Series<>();
        dataS = new XYChart.Data<>("Suporte", 0);
        serieS.getData().add(dataS);

        XYChart.Series<String, Number> serieM = new XYChart.Series<>();
        dataM = new XYChart.Data<>("Marketing", 0);
        serieM.getData().add(dataM);

        XYChart.Series<String, Number> serieF = new XYChart.Series<>();
        dataF = new XYChart.Data<>("Financeiro", 0);
        serieF.getData().add(dataF);

        XYChart.Series<String, Number> serieP = new XYChart.Series<>();
        dataP = new XYChart.Data<>("Pesquisa", 0);
        serieP.getData().add(dataP);

        grafico.getData().addAll(serieD, serieS, serieM, serieF, serieP);

        grafico.widthProperty().addListener((obs, oldVal, newVal) -> {
            grafico.applyCss();
            grafico.layout();
            for (XYChart.Series<String, Number> serie : grafico.getData()) {
                for (XYChart.Data<String, Number> data : serie.getData()) {
                    if (data.getNode() != null) {
                        data.getNode().setTranslateX(5);
                    }
                }
            }
        });

        VBox legenda = new VBox(10);
        legenda.setAlignment(Pos.CENTER_LEFT);
        legenda.setPadding(new Insets(10));
        legenda.getChildren().addAll(
                criarCaixaLegenda("#c2f0ef", "Desenvolvimento"),
                criarCaixaLegenda("#88ccc9", "Suporte"),
                criarCaixaLegenda("#5ab2b0", "Marketing"),
                criarCaixaLegenda("#267a78", "Financeiro"),
                criarCaixaLegenda("#0b514f", "Pesquisa e Inovação")
        );

        Label tituloGrafico = new Label("Percentual de Conclusão por Setor");
        tituloGrafico.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        HBox painelGrafico = new HBox(90, grafico, legenda);
        painelGrafico.setAlignment(Pos.CENTER);
        painelGrafico.setPadding(new Insets(10));

        VBox caixaGraficoCompleta = new VBox(15, tituloGrafico, painelGrafico);
        caixaGraficoCompleta.setAlignment(Pos.TOP_CENTER);
        caixaGraficoCompleta.setPadding(new Insets(20));
        caixaGraficoCompleta.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 15;");
        caixaGraficoCompleta.setPrefWidth(745);
        caixaGraficoCompleta.setMinHeight(250);
        caixaGraficoCompleta.setMaxHeight(250);

        Label tituloAlertas = new Label("Alertas");
        tituloAlertas.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");

        alerta1 = new Label();
        alerta2 = new Label();
        alerta3 = new Label();
        alerta4 = new Label();
        atualizarAlertas();

        for (Label lbl : new Label[]{alerta1, alerta2, alerta3, alerta4}) {
            lbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        }

        StackPane tituloAlertasContainer = new StackPane(tituloAlertas);
        tituloAlertasContainer.setAlignment(Pos.CENTER);

        VBox conteudoAlertas = new VBox(20, alerta1, alerta2, alerta3, alerta4);
        conteudoAlertas.setAlignment(Pos.CENTER_LEFT);
        conteudoAlertas.setPadding(new Insets(0, 30, 30 , 30));

        VBox painelAlertas = new VBox(15, tituloAlertasContainer, conteudoAlertas);
        painelAlertas.setPadding(new Insets(20));
        painelAlertas.setStyle("-fx-background-color: #2C2C2C; -fx-background-radius: 15;");
        painelAlertas.setAlignment(Pos.TOP_LEFT);
        painelAlertas.setPrefWidth(415);
        painelAlertas.setMinHeight(250);
        painelAlertas.setMaxHeight(250);

        HBox topContainer = new HBox(30, caixaGraficoCompleta, painelAlertas);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));

        tabela = new TableView<>();
        carregarTabela(); 
        tabela.setMinHeight(350);
        tabela.setMaxHeight(350);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(tabela, Priority.ALWAYS); 
        
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
        
        coluna1.getChildren().addAll(titulo, topContainer, tabela);

        Button btnVerMetas = new Button("Ver Todas as Metas");
        btnVerMetas.getStyleClass().add("btnVerMetas"); 
        
        Button btnExportar = new Button("Exportar PDF");
        btnExportar.getStyleClass().add("botao-exportar");

        Button btnExportarExcel = new Button("Exportar Excel");
        btnExportarExcel.getStyleClass().add("botão-exportar");
    	

        HBox containerBotoes = new HBox(15, btnVerMetas, btnExportar, btnExportarExcel);
        containerBotoes.setAlignment(Pos.CENTER);
        containerBotoes.setPadding(new Insets(10, 0, 20, 0));

        btnVerMetas.setOnAction(e -> {
            MetasGGTotais totalPage = new MetasGGTotais();

            VBox novaTela = totalPage.criarPagina(
                coluna1,           
                titulo,            
                topContainer,      
                tabela,            
                containerBotoes    
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
    		
    		List<Pdi> todasMetas = pdiDAO.listAll();
    		todasMetas.sort(Comparator.comparing(Pdi::getPrazo)); 

    		boolean sucesso = PDFExporter.exportarPDIsParaPDF(
    		    FXCollections.observableArrayList(todasMetas),
    		    file.getAbsolutePath()
    		);

    	});

        btnExportarExcel.setOnAction(e -> {
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
        root.getChildren().addAll(barra, scrollPane);
        root.setStyle("-fx-background-color: #1E1E1E");

        coluna1.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("Metas.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("BarraLateral.css").toExternalForm());

        metasggStage.setScene(scene);
        metasggStage.setFullScreen(true);
        metasggStage.setFullScreenExitHint("");
        metasggStage.setTitle("Gerenciamento de Metas (PDIs)");
        metasggStage.show();

        Timeline atualizador = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            carregarTabela();
            atualizarAlertas();
            atualizarGrafico();
        }));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();

        atualizarGrafico();
    }

    private HBox criarCaixaLegenda(String cor, String texto) {
        Label corBox = new Label("  ");
        corBox.setMinSize(20, 20);
        corBox.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 5;");
        Label nome = new Label(texto);
        nome.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
        HBox item = new HBox(10, corBox, nome);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private void carregarTabela() {
        List<Pdi> todos = pdiDAO.listAll();

        todos.sort(Comparator.comparing(Pdi::getPrazo));

        List<Pdi> proximosOito = todos.stream().limit(8).collect(Collectors.toList());
        dados = FXCollections.observableArrayList(proximosOito);
        tabela.setItems(dados);   
    }
    private void atualizarAlertas() {
        int andamento = pdiDAO.contarPorStatus(Status.EM_ANDAMENTO);
        int concluidas = pdiDAO.contarPorStatus(Status.CONCLUIDO);
        int futuras = pdiDAO.contarPorStatus(Status.NAO_INICIADO);
        int atrasadas = pdiDAO.contarPorStatus(Status.ATRASADO);

        alerta1.setText("•  " + andamento + " " + (andamento == 1 ? "meta em andamento" : "metas em andamento"));
        alerta2.setText("•  " + concluidas + " " + (concluidas == 1 ? "meta concluída" : "metas concluídas"));
        alerta3.setText("•  " + futuras + " " + (futuras == 1 ? "meta planejada" : "metas planejadas"));
        alerta4.setText("•  " + atrasadas + " " + (atrasadas == 1 ? "meta atrasada" : "metas atrasadas"));
    }

    private void atualizarGrafico() {
        Map<String, List<Pdi>> porSetor = pdiDAO.listAll().stream()
                .collect(Collectors.groupingBy(p -> {
                    Colaborador c = colaboradorDAO.getColaboradorById(p.getColaborador_id());
                    return (c != null) ? c.getSetor() : "Desconhecido";
                }));

        dataD.setYValue(calcularPercentual(porSetor, "Desenvolvimento"));
        dataS.setYValue(calcularPercentual(porSetor, "Suporte"));
        dataM.setYValue(calcularPercentual(porSetor, "Marketing"));
        dataF.setYValue(calcularPercentual(porSetor, "Financeiro"));
        dataP.setYValue(calcularPercentual(porSetor, "Pesquisa e Inovação"));
    }

    private double calcularPercentual(Map<String, List<Pdi>> mapa, String setor) {
        List<Pdi> lista = mapa.getOrDefault(setor, Collections.emptyList());
        if (lista.isEmpty()) return 0;
        long concluidos = lista.stream().filter(p -> p.getStatus() == Status.CONCLUIDO).count();
        return (concluidos * 100.0) / lista.size();
    }
    
}	
