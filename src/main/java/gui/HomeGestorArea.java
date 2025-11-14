package gui;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Colaborador;
import model.Pdi;
import model.Status;
import model.Usuario;
import javafx.scene.layout.Priority;


public class HomeGestorArea extends Application {

    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private VBox alertBox;             
    private Text alertMessageText;     

    public HomeGestorArea(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage homeggStage) {

        String primeiroNome = logado.getNome().split(" ")[0];
        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

        int totalColaboradoresSetor = colaboradorDAO.findBySetor(logado.getSetor()).size();

        List<Pdi> pdisDoSetor = pdiDAO.findBySetor(logado.getSetor());
        List<Colaborador> colaboradoresSetor = colaboradorDAO.findBySetor(logado.getSetor());


        int pdiAtivoCount = 0;
        int pdiInativoCount = 0;
        int pdiConcluidoCount = 0; 

        for (Pdi pdi : pdisDoSetor) {
            switch (pdi.getStatus()) {
                case EM_ANDAMENTO, ATRASADO -> pdiAtivoCount++;
                case NAO_INICIADO -> pdiInativoCount++;
                case CONCLUIDO -> pdiConcluidoCount++;
            }
        }

        Text titulo = new Text("Bem-vindo " + primeiroNome + "!");
        titulo.setId("titulo");

//        Ellipse blob1 = new Ellipse();
//        blob1.setId("blob1");
//
//        Ellipse blob2 = new Ellipse();
//        blob2.setId("blob2");
//
//        Ellipse blob3 = new Ellipse();
//        blob3.setId("blob3");
//
//        GaussianBlur blur = new GaussianBlur(40);
//        blob1.setEffect(blur);
//        blob2.setEffect(blur);
//        blob3.setEffect(blur);

        Text colaboradores = new Text("Colaboradores: " + totalColaboradoresSetor);
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

        VBox.setMargin(chartsContainer, new Insets(20, 45, 0, 0));

        chartsContainer.setPrefWidth(Double.MAX_VALUE);
        HBox.setHgrow(chartsContainer, Priority.ALWAYS);

        StackPane boxChart1 = new StackPane();
        boxChart1.setId("boxChart1");
        boxChart1.getStyleClass().add("card");
        HBox.setHgrow(boxChart1, Priority.ALWAYS);
        boxChart1.setMaxWidth(Double.MAX_VALUE);
        boxChart1.setMinWidth(0);

        //Gráfico
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Status dos PDIs");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Quantidade de PDIs");


        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Evolução do Setor");
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setCategoryGap(25);
        barChart.setBarGap(10);

        // Busca todos os PDIs do setor do gestor logado
        List<Pdi> pdisSetor = pdiDAO.findBySetor(logado.getSetor());

        // Agrupa os PDIs por status e conta quantos há em cada
        Map<Status, Long> contagemPorStatus = pdisSetor.stream()
            .collect(Collectors.groupingBy(Pdi::getStatus, Collectors.counting()));

        // Cria a série de dados para o gráfico
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.getData().add(new XYChart.Data<>("Não iniciado", contagemPorStatus.getOrDefault(Status.NAO_INICIADO, 0L)));
        serie.getData().add(new XYChart.Data<>("Em andamento", contagemPorStatus.getOrDefault(Status.EM_ANDAMENTO, 0L)));
        serie.getData().add(new XYChart.Data<>("Atrasado", contagemPorStatus.getOrDefault(Status.ATRASADO, 0L)));
        serie.getData().add(new XYChart.Data<>("Concluído", contagemPorStatus.getOrDefault(Status.CONCLUIDO, 0L)));

        barChart.getData().add(serie);
        
        Platform.runLater(() -> {
            serie.getData().get(0).getNode().getStyleClass().add("barra-nao-iniciado");
            serie.getData().get(1).getNode().getStyleClass().add("barra-em-andamento");
            serie.getData().get(2).getNode().getStyleClass().add("barra-atrasado");
            serie.getData().get(3).getNode().getStyleClass().add("barra-concluido");
        });

        // Estilo visual
        barChart.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        xAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);
        yAxis.setTickLabelFill(javafx.scene.paint.Color.WHITE);
        barChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        barChart.setLegendVisible(false);

        // Adiciona o gráfico no boxChart1
        boxChart1.getChildren().add(barChart);

        StackPane boxChart2 = new StackPane();
        boxChart2.setId("boxChart2");
        boxChart2.getStyleClass().add("card");
        HBox.setHgrow(boxChart2, Priority.ALWAYS);
        boxChart2.setMaxWidth(Double.MAX_VALUE);
        boxChart2.setMinWidth(0);
        
        VBox alertContainer = new VBox(10);
        alertContainer.setPadding(new Insets(25, 25, 25, 25));
        alertContainer.setAlignment(Pos.TOP_LEFT);

        Label alertTitle = new Label("Alertas");
        alertTitle.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        VBox alertList = new VBox(30);
        alertList.setAlignment(Pos.TOP_LEFT);
        // --- Lógica de contagem de alertas ---
        long pdisAtrasados = pdisDoSetor.stream()
                .filter(p -> p.getStatus() == Status.ATRASADO)
                .count();

        long pdisProximosVencimento = pdisDoSetor.stream()
                .filter(p -> p.getPrazo() != null &&
                        !p.getStatus().equals(Status.CONCLUIDO) &&
                        p.getPrazo().isAfter(LocalDate.now()) &&
                        p.getPrazo().isBefore(LocalDate.now().plusDays(7)))
                .count();


        long funcionariosComPdiAtrasado = pdisDoSetor.stream()
                .filter(p -> p.getStatus() == Status.ATRASADO)
                .map(Pdi::getColaborador_id)
                .distinct()
                .count();

        // --- Montagem dos alertas ---
        if (funcionariosComPdiAtrasado > 0) {
            alertList.getChildren().add(criarAlerta("• " + funcionariosComPdiAtrasado + " funcionário(s) com PDI(s) atrasado"));
        }

        if (pdisProximosVencimento > 0) {
            alertList.getChildren().add(criarAlerta("• " + pdisProximosVencimento + " PDI(s) próximos ao vencimento"));
        }

        if (pdisAtrasados > 0) {
            alertList.getChildren().add(criarAlerta("• " + pdisAtrasados + " PDI(s) atrasados"));
        }

        // Se não houver alertas, mostra mensagem positiva
        if (alertList.getChildren().isEmpty()) {
            Label allGood = new Label("Tudo certo com este setor! ✅");
            allGood.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 16px;");
            alertList.getChildren().add(allGood);
        }

        // Adiciona ao container e ao layout do boxChart2
        alertContainer.getChildren().addAll(alertTitle, alertList);
        boxChart2.getChildren().add(alertContainer);
        VBox.setMargin(alertContainer, new Insets(10, 0, 0, 15));
        
        alertContainer.setId("alertContainer");
        alertTitle.setId("alertTitle");
        alertList.setId("alertList");

        // ======= FIM DO BLOCO DE ALERTAS =======

        chartsContainer.getChildren().addAll(boxChart1, boxChart2);

        double spacing = chartsContainer.getSpacing();

        boxChart1.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.25).subtract(spacing * 0.25)
        );

        boxChart2.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.75).subtract(spacing * 0.75)
        );
        
        VBox center = new VBox();
        center.setId("center");
       
        center.getChildren().addAll(titulo, container, chartsContainer);
//        center.getChildren().addAll(titulo, container, chartsContainer, blob1, blob2, blob3);

        BarraLateralGA barra = new BarraLateralGA(logado);

        HBox root = new HBox();
        root.setStyle("-fx-background-color: #1E1E1E");
        root.getChildren().addAll(barra, center);

        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/HomeGestorArea.css").toExternalForm());

//        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
//        blob1.radiusYProperty().bind(blob1.radiusXProperty());
//
//        blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
//        blob2.radiusYProperty().bind(blob2.radiusXProperty());
//
//        blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
//        blob3.radiusYProperty().bind(blob3.radiusXProperty());
//
//        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
//        blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
//        blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));
//
//        StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
//        blob2.translateXProperty().bind(scene.widthProperty().multiply(0.4));
//        blob2.translateYProperty().bind(scene.heightProperty().multiply(0.3));
//
//        StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
//        blob3.translateXProperty().bind(scene.widthProperty().multiply(0.52));
//        blob3.translateYProperty().bind(scene.heightProperty().multiply(0.07));

        homeggStage.setScene(scene);
        homeggStage.setFullScreen(true);
        homeggStage.setFullScreenExitHint("");
        homeggStage.show();
    }
    
    private Label criarAlerta(String texto) {
        Label label = new Label(texto);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        return label;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
