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

    // Funcionalidade nos métodos
    private int limit = 1;
    private List<String> setores = Arrays.asList(
            "Desenvolvimento",
            "Marketing",
            "Suporte",
            "Financeiro",
            "Pesquisa e Inovação"
    );

    // Cards
    private VBox cards;
    private VBox nameCardsContainer;
    private VBox setorCardsContainer;

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

        Text titulo = new Text("Relatórios de PDIs");
        titulo.setId("titulo");

        tituloContainer.getChildren().add(titulo);

        // BarChart
        // VBox Container do Grafico de Barras
        StackPane barChartContainer = new StackPane();
        barChartContainer.getStyleClass().add("chart_container");

        // Eixo X
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.getStyleClass().add("barchart_xAxis");
        xAxis.setLabel("Setor");
        xAxis.setCategories(FXCollections.observableArrayList("DD", "DM", "DS", "DF", "DPI"));

        // EixoY
        NumberAxis yAxis = new NumberAxis(0,10,1);
        yAxis.setLabel("Evolução");
        yAxis.getStyleClass().add("barchart_yAxis");

        // Gráfico de Barras
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.getStyleClass().add("barchart");
        barChart.getStyleClass().add("chart");
        barChart.getStyleClass().add("bar-chart-responsive");
        barChart.setAnimated(false);

        // Sistema de Score
        Map<String, Map<Status, Integer>> totalPorSetor = calcularPDITotalPorSetor(colaboradores);
        Map<String, Integer> scores = calcularScorePorSetor(totalPorSetor);

        // Séries de dados dos Gráficos (Nomes)
        XYChart.Series<String, Number> serieDesenvolvimento = new XYChart.Series<>();
        serieDesenvolvimento.setName("DD");

        XYChart.Series<String, Number> serieMarketing = new XYChart.Series<>();
        serieMarketing.setName("DM");

        XYChart.Series<String, Number> serieSuporte = new XYChart.Series<>();
        serieSuporte.setName("DS");

        XYChart.Series<String, Number> serieFinanceiro = new XYChart.Series<>();
        serieFinanceiro.setName("DF");

        XYChart.Series<String, Number> seriePesquisaInovacao = new XYChart.Series<>();
        seriePesquisaInovacao.setName("DPI");

        for (String setor : scores.keySet()) {
            Integer score = scores.get(setor);
            int valorFinal = Math.max(0, score);

            switch (setor) {
                case "Desenvolvimento":
                    serieDesenvolvimento.getData().add(new XYChart.Data<>("DD", valorFinal));
                    break;
                case "Marketing":
                    serieMarketing.getData().add(new XYChart.Data<>("DM", valorFinal));
                    break;
                case "Suporte":
                    serieSuporte.getData().add(new XYChart.Data<>("DS", valorFinal));
                    break;
                case "Financeiro":
                    serieFinanceiro.getData().add(new XYChart.Data<>("DF", valorFinal));
                    break;
                case "Pesquisa e Inovação":
                    seriePesquisaInovacao.getData().add(new XYChart.Data<>("DPI", valorFinal));
                    break;
            }
        }
        barChart.getData().addAll(serieDesenvolvimento,serieMarketing,serieSuporte,serieFinanceiro,seriePesquisaInovacao);
        barChartContainer.getChildren().add(barChart);

        // PieChart
        // VBox Container do Gráfico de pizza
        StackPane pieChartContainer = new StackPane();
        pieChartContainer.getStyleClass().add("chart_container");

        // Maps para calcular o total de tarefas em andamento e em atraso.
        Map<Status, Integer> totalGeral = calcularPDITotalGeral(colaboradores);
       int totalAndamento = totalGeral.get(Status.EM_ANDAMENTO);
       int totalAtrasado = totalGeral.get(Status.ATRASADO);

        // Adicionando os dados para o grafico de pizza.
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Metas em andamento", totalAndamento),
                new PieChart.Data("Metas atrasadas", totalAtrasado)
        );

        // Criação do grafico de pizza com seus dados
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.getStyleClass().add("chart");
        pieChart.getStyleClass().add("piechart");
        pieChart.getStyleClass().add("pie-chart-responsive");
        // Adicionado o grafico de pizza ao seu container
        pieChartContainer.getChildren().add(pieChart);

        // HBox dos gráficos contendo ambos os containers de graficos.
        HBox graficos = new HBox();
        graficos.getStyleClass().add("graficos");
        graficos.getChildren().addAll(barChartContainer,pieChartContainer);

        // VBox Seção de Cards
        cards = new VBox();
        cards.getStyleClass().add("cardsContainer");

        // Barra de pesquisa
        // CardsBoxTop contem toda a barra de pesquisa.
        Text cardsTitle = new Text("Buscar relátorios");
        cardsTitle.getStyleClass().add("cardsContainerTitle");

        // HBox da barra de pesquisa com o TextField e o botão
        HBox cardsFilter = new HBox();
        cardsFilter.getStyleClass().add("cardsFilter");

        TextField filtro = new TextField();
        filtro.setPromptText("Pesquisar");
        filtro.getStyleClass().add("filtro");

        Button filtro_btn = new Button("Filtrar");
        filtro_btn.getStyleClass().add("filtro_btn");
        filtro_btn.setOnAction(event -> filtrarColaboradores(filtro.getText().toLowerCase()));

        // Adicionando o textfield e o botão a HBox Cards de pesquisa e depois adicionando ao BoxTop
        cardsFilter.getChildren().addAll(filtro,filtro_btn);

        cards.getChildren().addAll(cardsTitle, cardsFilter);

        // Cards Relatorios por nome
        // VBox container dos names Cards
        nameCardsContainer = new VBox();
        nameCardsContainer.getStyleClass().add("namecardsContainer");
        criarNameCards(colaboradores);

        // O botão de ver mais namecards
        Button maisNameCards_btn = new Button("Ver mais relatórios de colaboradores");
        maisNameCards_btn.getStyleClass().add("cards_btn");

        maisNameCards_btn.setOnAction(event -> {
            Stage relatoriosggcolaboradoresStage = new Stage();
            new RelatoriosGGColaboradores(logado).start(relatoriosggcolaboradoresStage);

            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        cards.getChildren().addAll(nameCardsContainer, maisNameCards_btn);

        // Cards Relatorios por setor
        // VBox Container contendo o setorCards
        setorCardsContainer = new VBox();
        setorCardsContainer.getStyleClass().add("setorcardsContainer");

        criarSetorCards(setores);

        // VBox contendo o botão de ver mais cards Container
        Button maisSetorCards_btn = new Button("Ver mais relatórios de divisões do setor");
        maisSetorCards_btn.getStyleClass().add("cards_btn");

        maisSetorCards_btn.setOnAction(event -> {
            Stage relatoriosggsetoresStage = new Stage();
            new RelatoriosGGSetores(logado).start(relatoriosggsetoresStage);

            Stage stageAtual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stageAtual.close();
        });

        // AddAll do Cards
        cards.getChildren().addAll(setorCardsContainer, maisSetorCards_btn);

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

        // AddAll do Center
        center.getChildren().addAll(tituloContainer,graficos,cards);

        // HBox root
        HBox root = new HBox();
        root.setId("root-relatorios-gg");
        root.getChildren().addAll(barra, center, blob1, blob2, blob3);

        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));

        // Scene
        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGG.css").toExternalForm());

        // Bindings responsivos para os blobs
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

        // Listener para redimensionamento responsivo
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });
        
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            updateResponsiveStyles(scene);
        });

        relatoriosggStage.setScene(scene);
        relatoriosggStage.setFullScreen(true);
        relatoriosggStage.setFullScreenExitHint("");
        relatoriosggStage.show();
        
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

    // ... (resto dos métodos permanece igual - calcularPDITotalPorSetor, calcularPDITotalGeral, calcularScorePorSetor, etc.)
    // Métodos (mantidos da versão original)
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
            nameCard.getStyleClass().add("name-card");

            // VBox NameCard com as informações escritas.
            VBox nameCardInfos = new VBox();
            nameCardInfos.getStyleClass().add("nameCardInfosContainer");

            Text nameCardTitle = new Text(relatorio.getNome());
            nameCardTitle.getStyleClass().add("nameCardTitle");
            Text nameCardSetor = new Text(relatorio.getSetor());
            nameCardSetor.getStyleClass().add("nameCardSetor");
            Text nameCardCargo = new Text(relatorio.getCargo());
            nameCardCargo.getStyleClass().add("nameCardCargo");

            PieChart nameCardPieChart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluídas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));
            
            nameCardPieChart.getStyleClass().add("namecardpiechart");
            nameCardPieChart.getStyleClass().add("pie-chart-responsive");

            nameCardInfos.getChildren().addAll(nameCardTitle, nameCardSetor, nameCardCargo);
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

            // HBox SetorCard
            HBox setorCard = new HBox();
            setorCard.getStyleClass().add("setor-card");

            // VBox SetorCardInfos com as informações dos cards de setores
            VBox setorCardInfos = new VBox();
            setorCardInfos.getStyleClass().add("setor-card-infos");

            Text setorTitulo = new Text(setor);
            setorTitulo.getStyleClass().add("setor-titulo");
            Text totalColaboradores = new Text("Colaboradores: " + colaboradoresDoSetor.size());
            totalColaboradores.getStyleClass().add("totalColaboradores");

            PieChart chart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));
            chart.getStyleClass().add("pie-chart-responsive");

            setorCardInfos.getChildren().addAll(setorTitulo, totalColaboradores);
            setorCard.getChildren().addAll(setorCardInfos, chart);

            setorCardsContainer.getChildren().add(setorCard);
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