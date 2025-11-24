package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.shape.Ellipse;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;

public class RelatoriosGA extends Application {
       
    private Usuario logado;

    public RelatoriosGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }
    
    @Override
    public void start(Stage relatoriosgaStage) {
        
        VBox center = new VBox();
        center.setId("center");
        
        // Título principal - ALINHADO À ESQUERDA
        Text titulo = new Text("Relatórios de PDIs Marketing Digital ");
        titulo.setId("titulo");
        
        Ellipse blob1 = new Ellipse();
        blob1.setId("blob1");
        
        Ellipse blob2 = new Ellipse();
        blob2.setId("blob2");
        
        GaussianBlur blur = new GaussianBlur(40);
        blob1.setEffect(blur);
        blob2.setEffect(blur);
     
        // Container para alinhar o título à esquerda
        HBox tituloContainer = new HBox(titulo);
        tituloContainer.setAlignment(Pos.CENTER_LEFT);
        tituloContainer.setPadding(new Insets(0, 0, 20, 0));
        
        // Adiciona o título ao VBox central
        center.getChildren().addAll(tituloContainer, blob1, blob2);
        
        // Chama o método que cria TODOS os gráficos e cards
        Parent mainReportContent = createContent();
        
        // Adiciona os gráficos/cards abaixo do título
        center.getChildren().add(mainReportContent);
        
        // A BARRA LATERAL
        BarraLateralGA barraLateral = new BarraLateralGA(logado);
        
        ScrollPane scrollPane = new ScrollPane(center);
        scrollPane.setFitToWidth(true); 
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    
        HBox root = new HBox();
        root.setId("root-relatorios");
        root.getChildren().addAll(barraLateral, scrollPane);
        
        scrollPane.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barraLateral.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        
        // CSSs
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGA.css").toExternalForm()); 

        // Bindings responsivos para os blobs
        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.057));
        blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

        blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.04));
        blob2.radiusYProperty().bind(blob2.radiusXProperty());

        StackPane.setAlignment(blob1, Pos.BOTTOM_LEFT);
        blob1.translateXProperty().bind(scene.widthProperty().multiply(0.75));
        blob1.translateYProperty().bind(scene.heightProperty().multiply(0.01));
        blob1.setManaged(false);

        StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
        blob2.translateXProperty().bind(scene.widthProperty().multiply(0.6));
        blob2.translateYProperty().bind(scene.heightProperty().multiply(0.02));
        blob2.setManaged(false);
        
        // Listener para redimensionamento responsivo
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        relatoriosgaStage.setFullScreen(true);
        relatoriosgaStage.setFullScreenExitHint("");
        relatoriosgaStage.setScene(scene);
        relatoriosgaStage.show();
        
        // Aplicar estilos iniciais
        updateResponsiveStyles(scene);
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

    // Método principal que será chamado pela sua tela HomeGestorArea
    public Parent createContent() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(0, 45, 25, 0)); 
        content.getStyleClass().add("relatorios-content"); 

        // Container para os gráficos superiores (HBox) - CENTRALIZADO
        HBox topChartsContainer = new HBox(30);
        topChartsContainer.setAlignment(Pos.CENTER); // ALTERADO: de CENTER_LEFT para CENTER
        topChartsContainer.getChildren().addAll(createBarChartCard(), createPieChartCard("Status Geral de PDIs"));

        // Container para a busca
        HBox searchArea = createSearchArea();

        // Container para o card do colaborador - CENTRALIZADO
        VBox employeeCard = createEmployeeCard();
        
        // Botão "Ver mais"
        Button btnVerMais = new Button("Ver mais relatórios de colaboradores");
        btnVerMais.getStyleClass().add("btn-ver-mais");
        
        // Centraliza o botão
        HBox buttonBox = new HBox(btnVerMais);
        buttonBox.setAlignment(Pos.CENTER);

        // Adicionar tudo ao VBox principal do conteúdo
        content.getChildren().addAll(topChartsContainer, searchArea, employeeCard, buttonBox);
        
        return content;
    }
    
    // Métodos privados para criar cada componente

    // Método que cria um CARD com o BarChart dentro
    private VBox createBarChartCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("chart-card");
        
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Evolução");
        barChart.getStyleClass().add("bar-chart-responsive");

        xAxis.setLabel("Setor"); 
        yAxis.setLabel("Evolução"); 
        
        // Ocultar rótulos e ticks do eixo Y
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        
        barChart.setLegendVisible(false);
        barChart.setStyle("-fx-category-gap: 30px; -fx-bar-gap: 5px;");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Dados e estilos inline para as barras
        XYChart.Data<String, Number> dataDM = new XYChart.Data<>("DM", 20);
        dataDM.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #A8E6CF;"); }
        });
        
        XYChart.Data<String, Number> dataDR = new XYChart.Data<>("DR", 35);
        dataDR.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #70C1B3;"); }
        });
        
        XYChart.Data<String, Number> dataCR = new XYChart.Data<>("CR", 10);
        dataCR.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #2E8B78;"); }
        });
        
        series.getData().addAll(dataDM, dataDR, dataCR);
        barChart.getData().add(series);

        card.getChildren().add(barChart);
        return card;
    }

    // Método que cria um CARD com o PieChart (donut) dentro
    private VBox createPieChartCard(String title) {
        VBox card = new VBox(10);
        card.getStyleClass().add("chart-card");
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle(title);
        pieChart.setLabelsVisible(false);
        pieChart.setLegendSide(javafx.geometry.Side.RIGHT);
        pieChart.getStyleClass().add("pie-chart-responsive");

        // Dados de exemplo para o PieChart
        pieChart.getData().addAll(
            new PieChart.Data("Metas em andamento", 60),
            new PieChart.Data("Metas atrasadas", 15)
        );
        
        // Estilos inline para as fatias do PieChart
        int i = 0;
        for (PieChart.Data data : pieChart.getData()) {
            String color;
            switch (i) {
                case 0: color = "#A8E6CF"; break;
                case 1: color = "#70C1B3"; break;
                case 2: color = "#2E8B78"; break;
                default: color = "#A9A9A9";
            }
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
        
        card.getChildren().add(pieChart);
        return card;
    }

    private HBox createSearchArea() {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("search-area");
        
        Label lblBuscar = new Label("Buscar relatórios");
        lblBuscar.getStyleClass().add("search-label");
        
        TextField txtPesquisar = new TextField();
        txtPesquisar.setPromptText("Pesquisar");
        txtPesquisar.getStyleClass().add("search-field");
        
        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.getStyleClass().add("btn-filtrar");
        
        hbox.getChildren().addAll(lblBuscar, txtPesquisar, btnFiltrar);
        return hbox;
    }

    private VBox createEmployeeCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("employee-card");
        card.setMaxWidth(1200); 
        card.setPrefWidth(1200);
        card.setMinWidth(900);
        card.setAlignment(Pos.CENTER); // NOVO: Centralizar o card
        
        HBox cardContent = new HBox(20);
        cardContent.setAlignment(Pos.CENTER_LEFT);
        cardContent.getStyleClass().add("employee-card-content");

        VBox infoColaborador = new VBox(5);
        infoColaborador.getStyleClass().add("employee-info");
        
        Label nome = new Label("Gabrielle Novaes Gonçalves");
        nome.getStyleClass().add("employee-name");
        Label marketing = new Label("Marketing Digital");
        marketing.getStyleClass().add("employee-detail");
        Label diretor = new Label("Diretor de Marketing");
        diretor.getStyleClass().add("employee-detail");
        Label gerente = new Label("Gerente responsável: Leticia Gabrielly Furtado");
        gerente.getStyleClass().add("employee-detail");
        
        infoColaborador.getChildren().addAll(nome, marketing, diretor, gerente);
        
        // Criar outro gráfico de rosca para o colaborador
        PieChart colaboradorChart = new PieChart();
        colaboradorChart.setLabelsVisible(false);
        colaboradorChart.setLegendSide(javafx.geometry.Side.RIGHT);
        colaboradorChart.getStyleClass().add("pie-chart-responsive");
        
        colaboradorChart.getData().addAll(
            new PieChart.Data("Metas em andamento", 70),
            new PieChart.Data("Metas concluídas", 20),
            new PieChart.Data("Metas atrasadas", 10)
        );

        // Estilos inline para as fatias do PieChart do colaborador
        int i = 0;
        for (PieChart.Data data : colaboradorChart.getData()) {
            String color;
            switch (i) {
                case 0: color = "#A8E6CF"; break;
                case 1: color = "#70C1B3"; break;
                case 2: color = "#2E8B78"; break;
                default: color = "#A9A9A9";
            }
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
        
        cardContent.getChildren().addAll(infoColaborador, colaboradorChart);
        card.getChildren().add(cardContent);
        
        return card;
    }
}