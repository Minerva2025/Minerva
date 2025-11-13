package gui;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos; // Importar Pos para alinhamento
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

import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text; // Usar Text para o título principal como você já faz
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
        
        // Título principal
        Text titulo = new Text("Relatórios de PDIs Marketing Digital ");
        titulo.setId("titulo");
        
     
       // 2. Adiciona o título ao VBox central
        center.getChildren().add(titulo);
        
        
        // 3. Chama o método que cria TODOS os gráficos e cards
        Parent mainReportContent = createContent();
        
        // 4. Adiciona os gráficos/cards abaixo do título
        center.getChildren().add(mainReportContent);
        // ------------------------------------
        
        // A BARRA LATERAL 
        
        BarraLateralGA barraLateral = new BarraLateralGA(logado);
    
        
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #1E1E1E");
        root.getChildren().addAll(barraLateral, center);
        
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barraLateral.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        
        
        
        // CSSs
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGA.css").toExternalForm()); 

        
        relatoriosgaStage.setFullScreen(true);
        relatoriosgaStage.setFullScreenExitHint("");
        relatoriosgaStage.setScene(scene);
        relatoriosgaStage.show();
    }
    
    // Método principal que será chamado pela sua tela HomeGestorArea

    public Parent createContent() {

    

    VBox content = new VBox(20);
   
    content.setPadding(new Insets(0, 45, 25, 0)); 
    content.getStyleClass().add("relatorios-content"); 

    // 3. Container para os gráficos superiores (HBox)
    HBox topChartsContainer = new HBox(30); // 30px de espaço entre os gráficos
    topChartsContainer.setAlignment(Pos.CENTER_LEFT); // Alinha os gráficos à esquerda
    topChartsContainer.getChildren().addAll(createBarChartCard(), createPieChartCard("Status Geral de PDIs"));

    // 4. Container para a busca
    HBox searchArea = createSearchArea();

    // 5. Container para o card do colaborador
    VBox employeeCard = createEmployeeCard();
    
    // 6. Botão "Ver mais"
    Button btnVerMais = new Button("Ver mais relatórios de colaboradores");
    btnVerMais.getStyleClass().add("btn-ver-mais");
    
    // Centraliza o botão
    HBox buttonBox = new HBox(btnVerMais);
    buttonBox.setAlignment(Pos.CENTER);


    // 7. Adicionar tudo ao VBox principal do conteúdo
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
        
        // 1. Declare e inicialize o BarChart AQUI
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Evolução"); // Título do gráfico

        xAxis.setLabel("Setor"); 
        yAxis.setLabel("Evolução"); 
        
        // Ocultar rótulos e ticks do eixo Y
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        
       
        
        barChart.setLegendVisible(false); // Esconder a legenda
        
        // Estilos específicos para o BarChart (inline ou via CSS)
        barChart.setStyle("-fx-category-gap: 30px; -fx-bar-gap: 5px;"); // Ajusta o espaçamento

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Dados e estilos inline para as barras
        XYChart.Data<String, Number> dataDM = new XYChart.Data<>("DM", 20);
        dataDM.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #A8E6CF;"); } // Verde claro
        });
        
        XYChart.Data<String, Number> dataDR = new XYChart.Data<>("DR", 35);
        dataDR.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #70C1B3;"); } // Verde médio
        });
        
        XYChart.Data<String, Number> dataCR = new XYChart.Data<>("CR", 10);
        dataCR.nodeProperty().addListener((obs, oldNode, newNode) -> {
            if (newNode != null) { newNode.setStyle("-fx-bar-fill: #2E8B78;"); } // Verde escuro
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
        pieChart.setLabelsVisible(false); // Esconder os rótulos de porcentagem na fatia
        pieChart.setLegendSide(javafx.geometry.Side.RIGHT); // Legenda à direita

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
                case 0: color = "#A8E6CF"; break; // Verde claro
                case 1: color = "#70C1B3"; break; // Verde médio
                case 2: color = "#2E8B78"; break; // Verde escuro
                default: color = "#A9A9A9"; // Cinza padrão
            }
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
        
        

        card.getChildren().add(pieChart);
        return card;
    }

    private HBox createSearchArea() {
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("search-area"); // Adiciona classe CSS
        
        Label lblBuscar = new Label("Buscar relatórios");
        TextField txtPesquisar = new TextField();
        txtPesquisar.setPromptText("Pesquisar");
        
        Button btnFiltrar = new Button("Filtrar");
        
        btnFiltrar.getStyleClass().add("btn-filtrar");
        
        hbox.getChildren().addAll(lblBuscar, txtPesquisar, btnFiltrar);
        return hbox;
    }

    private VBox createEmployeeCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("employee-card"); // Aplica classe CSS
        
        HBox cardContent = new HBox(20);
        cardContent.setAlignment(Pos.CENTER_LEFT); // Alinha o conteúdo interno

        VBox infoColaborador = new VBox(5);
        // Adiciona classe CSS para os labels dentro do card, se necessário
        Label nome = new Label("Gabrielle Novaes Gonçalves");
        nome.getStyleClass().add("employee-name");
        Label marketing = new Label("Marketing Digital");
        Label diretor = new Label("Diretor de Marketing");
        Label gerente = new Label("Gerente responsável: Leticia Gabrielly Furtado");
        
        infoColaborador.getChildren().addAll(nome, marketing, diretor, gerente);
        
        // Criar outro gráfico de rosca para o colaborador
        PieChart Colaborador = new PieChart();
        Colaborador.setLabelsVisible(false);
        Colaborador.setLabelsVisible(false); // Esconder os rótulos de porcentagem na fatia
        Colaborador.setLegendSide(javafx.geometry.Side.RIGHT); // Legenda à direita
        Colaborador.getData().addAll(
            new PieChart.Data("Metas em andamento", 70),
            new PieChart.Data("Metas concluídas", 20),
            new PieChart.Data("Metas atrasadas", 10)
        );

        // Estilos inline para as fatias do PieChart do colaborador
        int i = 0;
        for (PieChart.Data data : Colaborador.getData()) {
            String color;
            switch (i) {
                case 0: color = "#A8E6CF"; break; // Verde claro
                case 1: color = "#70C1B3"; break; // Verde médio
                case 2: color = "#2E8B78"; break; // Verde escuro
                default: color = "#A9A9A9"; // Cinza padrão
            }
            data.getNode().setStyle("-fx-pie-color: " + color + ";");
            i++;
        }
        
        cardContent.getChildren().addAll(infoColaborador, Colaborador);
        card.getChildren().add(cardContent);
        
        return card;
    }
    
}