package gui;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

    private int limit = 1;
    private List<String> setores = Arrays.asList(
            "Desenvolvimento",
            "Marketing",
            "Suporte",
            "Financeiro",
            "Pesquisa e Inovação"
    );

    private VBox cards;
    private VBox nameCardsContainer;
    private VBox setorCardsContainer;
    private ScrollPane mainScrollPane;

    private Usuario logado;
    public RelatoriosGG(Usuario usuariologado) {this.logado = usuariologado;}

    @Override
    public void start(Stage relatoriosggStage) {
        // Criação da Barra Lateral
        BarraLateralGG barra = new BarraLateralGG(logado);

        // VBox center
        VBox center = new VBox();
        center.setId("center");

        // VBox titulo
        VBox tituloContainer = new VBox();
        tituloContainer.setPadding(new javafx.geometry.Insets(20, 0, 20, 0));

        Text titulo = new Text("Relatórios de PDIs");
        titulo.setId("titulo");

        tituloContainer.getChildren().add(titulo);

        // BarChart
        StackPane barChartContainer = new StackPane();
        barChartContainer.getStyleClass().add("chart_container");

        // Eixo X
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.getStyleClass().add("barchart_xAxis");
        xAxis.setLabel("Setor");

        // EixoY
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Evolução");
        yAxis.getStyleClass().add("barchart_yAxis");

        // Gráfico de Barras
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getStyleClass().add("barchart");
        barChart.getStyleClass().add("chart");
        barChart.setAnimated(false);

        // Sistema de Score
        Map<String, Map<Status, Integer>> totalPorSetor = calcularPDITotalPorSetor(colaboradores);
        Map<String, Integer> scores = calcularScorePorSetor(totalPorSetor);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Score por Setor");

        for (String setor : scores.keySet()) {
            Integer score = scores.get(setor);
            int valorFinal = Math.max(0, score);

            String abreviacao = "";
            switch (setor) {
                case "Desenvolvimento":
                    abreviacao = "DD";
                    break;
                case "Marketing":
                    abreviacao = "DM";
                    break;
                case "Suporte":
                    abreviacao = "DS";
                    break;
                case "Financeiro":
                    abreviacao = "DF";
                    break;
                case "Pesquisa e Inovação":
                    abreviacao = "DPI";
                    break;
                default:
                    abreviacao = setor;
            }

            series.getData().add(new XYChart.Data<>(abreviacao, valorFinal));
        }

        barChart.getData().add(series);
        barChartContainer.getChildren().add(barChart);

        // PieChart
        StackPane pieChartContainer = new StackPane();
        pieChartContainer.getStyleClass().add("chart_container");

        Map<Status, Integer> totalGeral = calcularPDITotalGeral(colaboradores);
        int totalAndamento = totalGeral.get(Status.EM_ANDAMENTO);
        int totalAtrasado = totalGeral.get(Status.ATRASADO);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Metas em andamento", totalAndamento),
                new PieChart.Data("Metas atrasadas", totalAtrasado)
        );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.getStyleClass().add("chart");
        pieChart.getStyleClass().add("piechart");
        pieChartContainer.getChildren().add(pieChart);

        // HBox dos gráficos - Responsivo
        HBox graficos = new HBox();
        graficos.getStyleClass().add("graficos");
        graficos.getChildren().addAll(barChartContainer, pieChartContainer);

        // Tornar gráficos responsivos
        barChartContainer.prefWidthProperty().bind(graficos.widthProperty().divide(2).subtract(15));
        pieChartContainer.prefWidthProperty().bind(graficos.widthProperty().divide(2).subtract(15));

        // VBox Seção de Cards
        cards = new VBox();
        cards.getStyleClass().add("cardsContainer");

        // Barra de pesquisa
        Text cardsTitle = new Text("Buscar relátorios");
        cardsTitle.getStyleClass().add("cardsContainerTitle");

        HBox cardsFilter = new HBox();
        cardsFilter.getStyleClass().add("cardsFilter");

        TextField filtro = new TextField();
        filtro.setPromptText("Pesquisar");
        filtro.getStyleClass().add("filtro");

        Button filtro_btn = new Button("Filtrar");
        filtro_btn.getStyleClass().add("filtro_btn");
        filtro_btn.setOnAction(event -> filtrarColaboradores(filtro.getText().toLowerCase()));

        cardsFilter.getChildren().addAll(filtro, filtro_btn);
        cards.getChildren().addAll(cardsTitle, cardsFilter);

        // Cards Relatorios por nome
        nameCardsContainer = new VBox();
        nameCardsContainer.getStyleClass().add("namecardsContainer");
        criarNameCards(colaboradores);

        Button maisNameCards_btn = new Button("Ver mais relatórios de colaboradores");
        maisNameCards_btn.getStyleClass().add("cards_btn");
        maisNameCards_btn.setOnAction(event -> {
            Stage relatoriosggcolaboradoresStage = new Stage();
            new RelatoriosGGColaboradores(logado).start(relatoriosggcolaboradoresStage);
            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        // Cards Relatorios por setor
        setorCardsContainer = new VBox();
        setorCardsContainer.getStyleClass().add("setorcardsContainer");
        criarSetorCards(setores);

        Button maisSetorCards_btn = new Button("Ver mais relatórios de divisões do setor");
        maisSetorCards_btn.getStyleClass().add("cards_btn");
        maisSetorCards_btn.setOnAction(event -> {
            Stage relatoriosggsetoresStage = new Stage();
            new RelatoriosGGSetores(logado).start(relatoriosggsetoresStage);
            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        // AddAll do Cards
        cards.getChildren().addAll(nameCardsContainer, maisNameCards_btn, setorCardsContainer, maisSetorCards_btn);

        // VBox contentContainer - conteúdo principal
        VBox contentContainer = new VBox();
        contentContainer.getChildren().addAll(tituloContainer, graficos, cards);
        contentContainer.setSpacing(30);
        contentContainer.setPadding(new javafx.geometry.Insets(20));

        // StackPane para envolver o conteúdo e os blobs
        StackPane contentWithBlobs = new StackPane();
        
        // Blobs
        Ellipse blob1 = new Ellipse();
        blob1.setId("blob1");
        blob1.getStyleClass().add("blob");
        Ellipse blob2 = new Ellipse();
        blob2.setId("blob2");
        blob2.getStyleClass().add("blob");

        GaussianBlur blur = new GaussianBlur(40);
        blob1.setEffect(blur);
        blob2.setEffect(blur);

        // Adiciona os elementos na ordem correta: blobs primeiro, depois conteúdo
        contentWithBlobs.getChildren().addAll(blob1, blob2, contentContainer);

        // Configura os blobs para ficarem atrás do conteúdo
        blob1.toBack();
        blob2.toBack();

        // Posicionamento absoluto dos blobs dentro do StackPane
        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
        StackPane.setAlignment(blob2, Pos.TOP_RIGHT);
        
        // Margens para posicionar melhor os blobs
        StackPane.setMargin(blob1, new javafx.geometry.Insets(100, -50, 0, 0));
        StackPane.setMargin(blob2, new javafx.geometry.Insets(-50, 250, 0, 0));

        // ScrollPane principal
        mainScrollPane = new ScrollPane(contentWithBlobs);
        mainScrollPane.setFitToWidth(true);
        mainScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // AddAll do Center
        center.getChildren().add(mainScrollPane);

        // HBox root
        HBox root = new HBox();
        root.getStyleClass().add("root");
        root.getChildren().addAll(barra, center);
       
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));

        // Scene
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGG.css").toExternalForm());

        // Configurações de tamanho dos blobs
        blob1.setRadiusX(90);
        blob1.setRadiusY(90);
        blob2.setRadiusX(70);
        blob2.setRadiusY(70);

        // Configuração responsiva dos gráficos
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.doubleValue() < 1200) {
                graficos.setSpacing(15);
            } else {
                graficos.setSpacing(30);
            }
        });

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

            // HBox NameCard
            HBox nameCard = new HBox();
            nameCard.getStyleClass().add("namecard");

            // VBox NameCard com as informações escritas.
            VBox nameCardInfos = new VBox();
            nameCardInfos.getStyleClass().add("nameCardInfosContainer");

            VBox nameCardTitleContainer = new VBox();
            nameCardTitleContainer.getStyleClass().add("nameCardTitleContainer");

            Text nameCardTitle = new Text(relatorio.getNome());
            nameCardTitle.getStyleClass().add("CardsTitle");

            nameCardTitleContainer.getChildren().add(nameCardTitle);

            VBox nameInfos = new VBox();
            nameInfos.getStyleClass().add("nameinfos");

            Text nameCardSetor = new Text(relatorio.getSetor());
            nameCardSetor.getStyleClass().add("nameCardSetor");

            Text nameCardCargo = new Text(relatorio.getCargo());
            nameCardCargo.getStyleClass().add("nameCardCargo");

            nameInfos.getChildren().addAll(nameCardSetor, nameCardCargo);

            nameCardInfos.getChildren().addAll(nameCardTitleContainer, nameInfos);

            PieChart nameCardPieChart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluídas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));

            nameCardPieChart.getStyleClass().add("cardspiechart");



            nameCard.getChildren().addAll(nameCardInfos, nameCardPieChart);

            nameCardsContainer.getChildren().add(nameCard);
            nameCardsContainer.getStyleClass().add("namecardsContainer");
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

            // HBox SetorCard
            HBox setorCard = new HBox();

            // VBox SetorCardInfos com as informações dos cards de setores
            VBox setorCardInfos = new VBox();

            Text setorTitulo = new Text(setor);
            setorTitulo.getStyleClass().add("CardsTitle");

            Text totalColaboradores = new Text("Colaboradores: " + colaboradoresDoSetor.size());
            totalColaboradores.getStyleClass().add("totalColaboradores");

            PieChart chart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));

            chart.getStyleClass().add("cardspiechart");

            setorCardInfos.getChildren().addAll(setorTitulo, totalColaboradores);

            setorCard.getChildren().addAll(setorCardInfos, chart);

            setorCardsContainer.getChildren().add(setorCard);
            setorCardsContainer.getStyleClass().add("setorcardsContainer");
        }
    }

    public Map<String, Map<Status, Integer>> calcularPDITotalPorSetor(List<Colaborador> colaboradores) {
        List<String> setores = List.of(
                "Desenvolvimento",
                "Marketing",
                "Suporte",
                "Financeiro",
                "Pesquisa e Inovação"
        );

        Map<String, Map<Status, Integer>> totalPorSetor = new HashMap<>();

        for (String setor : setores) {
            Map<Status, Integer> mapaStatus = new HashMap<>();
            for (Status st : Status.values()) {
                mapaStatus.put(st, 0);
            }
            totalPorSetor.put(setor, mapaStatus);
        }

        for (Colaborador col : colaboradores) {

            Map<Status, Integer> mapaStatus = totalPorSetor.get(col.getSetor());

            List<Pdi> pdis = pdiDAO.findByColaborador(col.getId());

            for (Pdi pdi : pdis) {
                Status status = pdi.getStatus();

                Integer valorAtual = mapaStatus.get(status);

                mapaStatus.put(status, valorAtual + 1);
            }
        }

        return totalPorSetor;
    }

    public Map<Status, Integer> calcularPDITotalGeral(List<Colaborador> colaboradores) {

        Map<Status, Integer> totalGeral = new HashMap<>();

        for (Status st : Status.values()) {
            totalGeral.put(st, 0);
        }

        for (Colaborador col : colaboradores) {

            List<Pdi> pdis = pdiDAO.findByColaborador(col.getId());

            for (Pdi pdi : pdis) {
                Status status = pdi.getStatus();
                totalGeral.put(status, totalGeral.get(status) + 1);
            }
        }

        return totalGeral;
    }

    public Map<String, Integer> calcularScorePorSetor(Map<String, Map<Status, Integer>> totalPorSetor){
        Map<String, Integer> scores = new HashMap<>();

        int pesoConcluido = 2;
        int pesoEmAndamento = 1;
        int pesoNaoIniciado = 0;
        int pesoAtrasado = -2;

        for(Map.Entry<String, Map<Status, Integer>> entry : totalPorSetor.entrySet()){
            String setor = entry.getKey();
            Map<Status, Integer> mapa = entry.getValue();
            if (mapa == null){
                scores.put(setor, 0);
                continue;
            }

            int concluidos = mapa.getOrDefault(Status.CONCLUIDO, 0);
            int andamento  = mapa.getOrDefault(Status.EM_ANDAMENTO, 0);
            int naoInicio  = mapa.getOrDefault(Status.NAO_INICIADO, 0);
            int atrasado   = mapa.getOrDefault(Status.ATRASADO, 0);

            int score = (concluidos * pesoConcluido)
                    + (andamento * pesoEmAndamento)
                    + (naoInicio * pesoNaoIniciado)
                    + (atrasado * pesoAtrasado);

            scores.put(setor, score);

        }
        return scores;
    }

}