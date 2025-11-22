package gui;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import model.Colaborador;
import model.Pdi;
import model.Status;
import model.Usuario;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelatoriosGG extends Application {

    PdiDAO pdiDAO = new PdiDAO();
    ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    List<Colaborador> colaboradores = colaboradorDAO.listAll();
    private VBox cards;
    private VBox cardsBoxTop;
    private VBox nameCardsContainer;
    private VBox setorCardsContainer;
    private int limit = 1;
    private List<String> setores = Arrays.asList(
            "Desenvolvimento",
            "Marketing",
            "Suporte",
            "Financeiro",
            "Pesquisa e Inovação"
    );


    private Usuario logado;
    public RelatoriosGG(Usuario usuariologado) {this.logado = usuariologado;}

    @Override
    public void start(Stage relatoriosggStage) {
        // Criação da Barra Lateral
        BarraLateralGG barra = new BarraLateralGG(logado);

        // VBox center
        VBox center = new VBox();
        center.setId("relatoriosgg");
        center.setAlignment(Pos.TOP_CENTER);
        center.setSpacing(15);
        center.setPadding(new Insets(15));
        center.setStyle("-fx-background-color: #1E1E1E;");

        // VBox titulo
        VBox boxTitulo = new VBox();
        boxTitulo.setId("boxTitulo");
        boxTitulo.setAlignment(Pos.TOP_LEFT);
        boxTitulo.setSpacing(15);
        boxTitulo.setPadding(new Insets(30));

        Text titulo = new Text("Relatórios de PDIs");
        titulo.setId("titulo");
        VBox.setMargin(titulo, new Insets(4, 0, 30, 0));

        boxTitulo.getChildren().add(titulo);

        // BarChart
        VBox barChartContainer = new VBox();
        barChartContainer.getStyleClass().add("chartContainer");


        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Setor");


        NumberAxis yAxis = new NumberAxis(0,20,1);
        yAxis.setLabel("Evolução");
        yAxis.setTickLabelsVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Evolução");
        barChart.setHorizontalGridLinesVisible(false);
        barChart.setVerticalGridLinesVisible(false);
        barChart.setMaxSize(600,350);

        XYChart.Series<String, Number> serieConcluido = new XYChart.Series<>();
        serieConcluido.setName("Concluido");
        XYChart.Series<String, Number> serieAndamento = new XYChart.Series<>();
        serieAndamento.setName("Em andamento");
        XYChart.Series<String, Number> serieNaoIniciado = new XYChart.Series<>();
        serieNaoIniciado.setName("Não iniciado");
        XYChart.Series<String, Number> serieAtrasado = new XYChart.Series<>();
        serieAtrasado.setName("Atrasado");

        Map<String, int[]> totalPorSetor = new HashMap<>();

        for (Colaborador colaborador : colaboradores) {
            List<Pdi> listaDePdis = pdiDAO.findByColaborador(colaborador.getId());

            int[] totais = totalPorSetor.getOrDefault(colaborador.getSetor(), new int[4]);

            totais[0] += listaDePdis.stream().filter(pdi -> pdi.getStatus() == Status.CONCLUIDO).count();
            totais[1] += listaDePdis.stream().filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO).count();
            totais[2] += listaDePdis.stream().filter(pdi -> pdi.getStatus() == Status.NAO_INICIADO).count();
            totais[3] += listaDePdis.stream().filter(pdi -> pdi.getStatus() == Status.ATRASADO).count();

            totalPorSetor.put(colaborador.getSetor(), totais);
        }

        for (String setor : totalPorSetor.keySet()) {
            int[] totais = totalPorSetor.get(setor);
            serieConcluido.getData().add(new XYChart.Data<>(setor, totais[0]));
            serieAndamento.getData().add(new XYChart.Data<>(setor, totais[1]));
            serieNaoIniciado.getData().add(new XYChart.Data<>(setor, totais[2]));
            serieAtrasado.getData().add(new XYChart.Data<>(setor, totais[3]));
        }

        barChart.getData().addAll(serieConcluido,serieAndamento,serieNaoIniciado,serieAtrasado);
        barChartContainer.getChildren().add(barChart);


        // PieChart
        VBox pieChartContainer = new VBox();
        pieChartContainer.getStyleClass().add("chartContainer");

        int totalAndamento = totalPorSetor.values().stream()
                .mapToInt(totais -> totais[1])
                .sum();

        int totalAtrasado = totalPorSetor.values().stream()
                .mapToInt(totais -> totais[3])
                .sum();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Metas em andamento", totalAndamento),
                new PieChart.Data("Metas atrasadas", totalAtrasado)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChartContainer.getChildren().add(pieChart);
        pieChart.setMaxSize(600,350);

        // Gráficos HBox
        HBox graficos = new HBox();
        graficos.setSpacing(30);
        graficos.setPadding(new Insets(15));
        graficos.setAlignment(Pos.CENTER);
        graficos.getChildren().addAll(barChartContainer,pieChartContainer);

        // VBox Seção de Cards
        cards = new VBox();
        cards.setSpacing(15);
        cards.setPadding(new Insets(15));

        // Barra de pesquisa
        cardsBoxTop = new VBox(15);
        Text cardsTitulo = new Text("Buscar relátorios");
        cardsTitulo.setId("cardsTitle");

        HBox cardsBarraDePesquisa = new HBox(15);
        cardsBarraDePesquisa.setId("barraDePesquisa");
        TextField barraDePesquisa = new TextField("Pesquisar");
        barraDePesquisa.setId("textfield_barraDePesquisa");
        barraDePesquisa.setPrefSize(800,275);

        Button filtrar_btn = new Button("Filtrar");
        filtrar_btn.getStyleClass().add("filtrar_btn");
        filtrar_btn.setOnAction(event -> filtrarColaboradores(barraDePesquisa.getText().toLowerCase()));

        cardsBarraDePesquisa.getChildren().addAll(barraDePesquisa,filtrar_btn);
        cardsBoxTop.getChildren().addAll(cardsTitulo,cardsBarraDePesquisa);

        // Cards Relatorios por nome
        nameCardsContainer = new VBox();
        nameCardsContainer.getStyleClass().add("nameCardsContainer");
        criarNameCards(colaboradores);


        VBox maisNameCardsContainer = new VBox(15);
        maisNameCardsContainer.setAlignment(Pos.CENTER);

        Button maisNameCards_btn = new Button("Ver mais relatórios de colaboradores");
        maisNameCards_btn.setId("maisNameCards_btn");
        maisNameCards_btn.getStyleClass().add("btn");
        maisNameCards_btn.setAlignment(Pos.CENTER);
        maisNameCards_btn.setOnAction(event -> {
            Stage relatoriosggcolaboradoresStage = new Stage();
            new RelatoriosGGColaboradores(logado).start(relatoriosggcolaboradoresStage);

            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        maisNameCardsContainer.getChildren().add(maisNameCards_btn);

        // Cards Relatorios por setor
        setorCardsContainer = new VBox();
        setorCardsContainer.getStyleClass().add("setorCardsContainer");
        criarSetorCards(setores);

        VBox maisSetorCardsContainer = new VBox(15);
        maisSetorCardsContainer.setAlignment(Pos.CENTER);

        Button maisSetorCards_btn = new Button("Ver mais relatórios de divisões do setor");
        maisSetorCards_btn.getStyleClass().add("btn");
        maisSetorCards_btn.setOnAction(event -> {
            Stage relatoriosggsetoresStage = new Stage();
            new RelatoriosGGSetores(logado).start(relatoriosggsetoresStage);

            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });
        maisSetorCardsContainer.getChildren().addAll(maisSetorCards_btn);


        // Blobs
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

        // AddAll do Cards
        cards.getChildren().addAll(cardsBoxTop,nameCardsContainer,maisNameCardsContainer, setorCardsContainer, maisSetorCardsContainer);


        // AddAll do Center
        center.getChildren().addAll(boxTitulo,graficos,cards);


        // HBox root
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #1E1E1E");
        root.getChildren().addAll(barra, center, blob1, blob2, blob3);

        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));


        // Scene
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGG.css").toExternalForm());

        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob1.radiusYProperty().bind(blob1.radiusXProperty());

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.04));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());

		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.96));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(0.3));
		blob1.setManaged(false);

		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.4));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(1.02));
		blob2.setManaged(false);

		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.8));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.1));
		blob3.setManaged(false);

        relatoriosggStage.setScene(scene);
        relatoriosggStage.setFullScreen(true);
        relatoriosggStage.setFullScreenExitHint("");
        relatoriosggStage.show();
    }

    // Métodos
    private void filtrarColaboradores(String pesquisa) {
        int contador = 0;

        for (Colaborador relatorios : colaboradores) {
            if (contador >= limit) break;

            boolean incluiCard = relatorios.getNome().toLowerCase().contains(pesquisa)
                    || relatorios.getSetor().toLowerCase().contains(pesquisa)
                    || relatorios.getCargo().toLowerCase().contains(pesquisa);

            if (incluiCard) {
                nameCardsContainer.getChildren().clear();
                List<Colaborador> filtrados = colaboradores.stream()
                        .filter(relatorio -> relatorio.getNome().toLowerCase().contains(pesquisa)
                                || relatorio.getSetor().toLowerCase().contains(pesquisa)
                                || relatorio.getCargo().toLowerCase().contains(pesquisa))
                        .toList();
                criarNameCards(filtrados);
            }
        }
    }

    private void criarNameCards(List<Colaborador> lista) {
        int totalConcluido = 0;
        int totalAndamento = 0;
        int totalAtrasado = 0;
        int contador = 0;

        for (Colaborador relatorio : lista) {
            if (contador >= limit) break;
            contador++;

            List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorio.getId());

            int totalConcluidoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                    .count();
            totalConcluido += totalConcluidoPorColaborador;

            int totalAndamentoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                    .count();
            totalAndamento += totalAndamentoPorColaborador;

            int totalAtrasadoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                    .count();
            totalAtrasado += totalAtrasadoPorColaborador;

            HBox nameCard = new HBox();
            VBox nameCardInfos = new VBox();

            Text nameCardTitulo = new Text(relatorio.getNome());
            Text nameCardSetor = new Text(relatorio.getSetor());
            Text nameCardCargo = new Text(relatorio.getCargo());

            PieChart nameCardPieChart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluídas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));

            nameCardInfos.getChildren().addAll(nameCardTitulo, nameCardSetor, nameCardCargo);
            nameCard.getChildren().addAll(nameCardInfos, nameCardPieChart);

            nameCardsContainer.getChildren().add(nameCard);
        }
    }

    private void criarSetorCards(List<String> setores) {
        int contador = 0;

        for (String setor : setores) {
            if (contador >= limit) break;
            contador++;

            List<Colaborador> colaboradoresDoSetor = colaboradorDAO.findBySetor(setor);

            int totalConcluido = 0;
            int totalAndamento = 0;
            int totalAtrasado = 0;

            for (Colaborador relatorio : colaboradoresDoSetor) {
                List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorio.getId());

                totalConcluido += (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                        .count();

                totalAndamento += (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                        .count();

                totalAtrasado += (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                        .count();
            }

            HBox setorCard = new HBox();
            VBox setorCardInfos = new VBox();

            Text setorTitulo = new Text(setor);
            Text totalColaboradores = new Text("Colaboradores: " + colaboradoresDoSetor.size());

            PieChart chart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));

            setorCardInfos.getChildren().addAll(setorTitulo, totalColaboradores);
            setorCard.getChildren().addAll(setorCardInfos, chart);

            setorCardsContainer.getChildren().add(setorCard);
        }
    }




}
