package gui;

import java.time.LocalDate;
import java.util.List;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Ellipse;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Pdi;
import model.Status;
import model.Usuario;
import javafx.scene.layout.Priority;
import javafx.scene.control.ScrollPane;

public class HomeGestorGeral extends Application {

    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private VBox alertBox;             
    private Text alertMessageText;     
    private StackPane boxChart1;
    private StackPane boxChart2;
    private StackPane trendCard;

    public HomeGestorGeral(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage homeggStage) {

        String primeiroNome = logado.getNome().split(" ")[0];
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
                default -> {} 
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
        HBox.setHgrow(boxChart2, Priority.ALWAYS);
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

        
        alertBox = new VBox(6);
        alertBox.setId("alertBox");
        Text alertTitle = new Text("Atenção!");
        alertTitle.setId("alertTitle");
        alertMessageText = new Text("");
        alertMessageText.setId("alertMessage");

        alertBox.getChildren().addAll(alertTitle, alertMessageText);
        alertBox.managedProperty().bind(alertBox.visibleProperty());
        
        VBox center = new VBox();
        center.setId("center");
        center.getChildren().addAll(titulo, container, chartsContainer, alertBox, blob1, blob2, blob3);
        
        BarraLateralGG barra = new BarraLateralGG(logado);

        HBox root = new HBox();
        root.setId("root-home");
        root.getChildren().addAll(barra, center);
        
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/HomeGestorGeral.css").toExternalForm());

        // Bindings responsivos para os blobs
        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.065));
        blob1.radiusYProperty().bind(blob1.radiusXProperty());

        blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.045));
        blob2.radiusYProperty().bind(blob2.radiusXProperty());

        blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.03));
        blob3.radiusYProperty().bind(blob3.radiusXProperty());

        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
        blob1.translateXProperty().bind(scene.widthProperty().multiply(0.60));
        blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.85));

        StackPane.setAlignment(blob2, Pos.TOP_RIGHT);
        blob2.translateXProperty().bind(scene.widthProperty().multiply(0.46));
        blob2.translateYProperty().bind(scene.heightProperty().multiply(-1.05));

        StackPane.setAlignment(blob3, Pos.BOTTOM_RIGHT);
        blob3.translateXProperty().bind(scene.widthProperty().multiply(0.4));
        blob3.translateYProperty().bind(scene.heightProperty().multiply(-0.2));

        // Listener para redimensionamento responsivo
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });

        homeggStage.setScene(scene);
        homeggStage.setFullScreen(true);
        homeggStage.setFullScreenExitHint("");
        homeggStage.show();

        // Adiciona os gráficos aos boxChart1 e boxChart2
        createCharts(todosPdis);
        
        updateAlertVisibility();

        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(15), ev -> {
            Platform.runLater(this::updateAlertVisibility);
        }));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
        
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
                default -> {}
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
        
        // Tamanho responsivo será controlado via CSS
        pieChart.getStyleClass().add("pie-chart");
        
        boxChart1.getChildren().add(pieChart);
        
        // Gráfico de Barras
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        
        xAxis.setLabel("Divisão");
        yAxis.setLabel("Evolução");

        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
        
        barChart.setStyle("-fx-category-gap: 70px; -fx-bar-gap: 5px;");
        
        barChart.getStyleClass().add("bar-chart");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("PDI's"); 

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
        
        series.getData().addAll(dataDm, dataCm, dataSm);
        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        boxChart2.getChildren().add(barChart);
    }

    private void updateAlertVisibility() {
        List<Pdi> todos = pdiDAO.listAll();
        int expiradas = 0;
        LocalDate hoje = LocalDate.now();

        for (Pdi p : todos) {
            LocalDate prazo = p.getPrazo();
            if (prazo != null && prazo.isBefore(hoje) && p.getStatus() != Status.CONCLUIDO) {
                expiradas++;
            }
        }

        if (expiradas > 0) {
            alertMessageText.setText(expiradas + " PDI(s) expirado(s) — verifique e atribua ações.");
            alertBox.setVisible(true);
        } else {
            alertBox.setVisible(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}