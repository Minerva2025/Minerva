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
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.chart.PieChart;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import model.Colaborador;
import model.Pdi;
import model.Status;
import model.Usuario;


public class RelatoriosRH extends Application {

    private TableView<Pdi> tabela;
    private javafx.collections.ObservableList<Pdi> dados;
    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private Label alerta1, alerta2, alerta3, alerta4;
    private Label tituloAlertas, tituloGrafico;
    private NumberAxis eixoY;
    private VBox conteudoAlertas;
    private BarChart<String, Number> grafico; 

    public RelatoriosRH(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage metasggStage) {

        BarraLateralRH barra = new BarraLateralRH(logado);

        VBox coluna1 = new VBox();
        coluna1.setId("coluna1");
        coluna1.setAlignment(Pos.CENTER_LEFT);
        coluna1.setSpacing(15);
        coluna1.setPadding(new Insets(15));
        
        Text titulo = new Text("Relatório de PDIs");
        titulo.setId("titulo");
        VBox.setMargin(titulo, new Insets(4, 0, 30, 0));

        CategoryAxis eixoX = new CategoryAxis();
        eixoX.setTickLabelsVisible(false);
        eixoX.setTickMarkVisible(false);

        eixoY = new NumberAxis(0, 10, 1);
        eixoY.setAutoRanging(false);
        eixoY.setLabel(null);
        eixoY.setTickLabelsVisible(true);
        eixoY.setTickMarkVisible(true);

        grafico = new BarChart<>(eixoX, eixoY); 
        grafico.setAnimated(false);
        grafico.getStyleClass().add("chart");
        grafico.setCategoryGap(10);
        grafico.setBarGap(20);
        grafico.setPrefSize(250, 180);
        
        XYChart.Series<String, Number> serieConcluido = new XYChart.Series<>();
        serieConcluido.setName("Concluído");
        XYChart.Series<String, Number> serieAndamento = new XYChart.Series<>();
        serieAndamento.setName("Em Andamento");
        XYChart.Series<String, Number> serieNaoIniciado = new XYChart.Series<>();
        serieNaoIniciado.setName("Não Iniciado");
        XYChart.Series<String, Number> serieAtrasado = new XYChart.Series<>();
        serieAtrasado.setName("Atrasado");
        
        List<String> setores = colaboradorDAO.listAll().stream()
                .map(Colaborador::getSetor)
                .distinct()
                .collect(Collectors.toList());
        for (String setor : setores) {
            serieConcluido.getData().add(new XYChart.Data<>(setor, 0));
            serieAndamento.getData().add(new XYChart.Data<>(setor, 0));
            serieNaoIniciado.getData().add(new XYChart.Data<>(setor, 0));
            serieAtrasado.getData().add(new XYChart.Data<>(setor, 0));
        }
        grafico.getData().addAll(serieConcluido, serieAndamento, serieNaoIniciado, serieAtrasado);

        ComboBox<String> filtroArea = new ComboBox<>();
        filtroArea.getItems().add("Todos");
        filtroArea.getItems().addAll(setores);
        filtroArea.setValue("Todos");
        filtroArea.getStyleClass().add("area-filtro"); 
        filtroArea.setOnAction(e -> atualizarGraficoPorArea(filtroArea.getValue(),
                serieConcluido, serieAndamento, serieNaoIniciado, serieAtrasado));

        VBox legenda = new VBox(10);
        legenda.setAlignment(Pos.CENTER_LEFT);
        legenda.setPadding(new Insets(10));
        legenda.getChildren().addAll(
                criarCaixaLegenda("#c2f0ef", "Concluído"),
                criarCaixaLegenda("#5ab2b0", "Em Andamento"),
                criarCaixaLegenda("#267a78", "Não Iniciado"),
                criarCaixaLegenda("#0b514f", "Atrasado")
        );

        HBox painelGrafico = new HBox(90, grafico, legenda);
        painelGrafico.setAlignment(Pos.CENTER);
        painelGrafico.setPadding(new Insets(0));
        tituloGrafico = new Label("PDIs por Status e Setor : ");
        tituloGrafico.getStyleClass().add("titulo-grafico"); 

        HBox tituloFiltroG = new HBox(10);
        tituloFiltroG.setAlignment(Pos.CENTER);
        tituloFiltroG.setPadding(new Insets(0, 0, 10, 0));
        tituloFiltroG.getChildren().addAll(tituloGrafico, filtroArea);

        VBox caixaGraficoCompleta = new VBox(5, tituloFiltroG, painelGrafico);
        caixaGraficoCompleta.setAlignment(Pos.TOP_CENTER);
        caixaGraficoCompleta.setPadding(new Insets(20));
        caixaGraficoCompleta.getStyleClass().add("caixa-relatorio"); 
        caixaGraficoCompleta.setPrefWidth(600);
        caixaGraficoCompleta.setMinWidth(600);
        caixaGraficoCompleta.setMaxWidth(600);


        tituloAlertas = new Label();
        tituloAlertas.getStyleClass().add("titulo-alerta");
        
        alerta1 = new Label();
        alerta2 = new Label();
        alerta3 = new Label();
        alerta4 = new Label();

        for (Label lbl : new Label[]{alerta1, alerta2, alerta3, alerta4}) {
            lbl.getStyleClass().add("label-alerta"); 
        }

        conteudoAlertas = new VBox(3, alerta1, alerta2, alerta3, alerta4);
        conteudoAlertas.setAlignment(Pos.CENTER_LEFT);
        conteudoAlertas.setPadding(new Insets(0, 5, 5, 5));

        ComboBox<String> filtroAlertas = new ComboBox<>();
        filtroAlertas.getItems().addAll(
                "Total",
                "Concluídos",
                "Em Andamento",
                "Não Iniciados",
                "Atrasados"
        );
        filtroAlertas.setValue("Total");
        filtroAlertas.getStyleClass().add("area-filtro"); 

        StackPane tituloAlertasContainer = new StackPane(tituloAlertas);
        tituloAlertasContainer.setAlignment(Pos.CENTER);
        filtroAlertas.setOnAction(e -> {
            String filtro = filtroAlertas.getValue();
            atualizarAlertasFiltro(filtro);
        });
        atualizarAlertasFiltro("Total");

        HBox tituloEFiltro = new HBox(10, tituloAlertas, filtroAlertas);
        tituloEFiltro.setAlignment(Pos.CENTER);
        tituloEFiltro.setPadding(new Insets(0, 0, 10, 0));

        VBox painelAlertas = new VBox(10, tituloEFiltro, conteudoAlertas);
        painelAlertas.setPadding(new Insets(20));
        painelAlertas.getStyleClass().add("caixa-relatorio"); 
        painelAlertas.setAlignment(Pos.TOP_LEFT);
        painelAlertas.setPrefSize(500,100);
        caixaGraficoCompleta.setMinHeight(250);
        caixaGraficoCompleta.setMaxHeight(250);

        HBox topContainer = new HBox(20, caixaGraficoCompleta, painelAlertas);
        topContainer.setAlignment(Pos.CENTER);
        topContainer.setPadding(new Insets(10));

        coluna1.getChildren().add(titulo);
        coluna1.getChildren().add(topContainer);
        
        Text tituloMeio = new Text("Relatório de colaboradores");
        tituloMeio.setId("titulo");
        tituloMeio.getStyleClass().add("titulo");
        VBox.setMargin(tituloMeio, new Insets(30, 0, 20, 0)); 

        coluna1.getChildren().add(tituloMeio);

      
        VBox relatorioColaborador = new VBox(15);
        relatorioColaborador.setPadding(new Insets(20));
        relatorioColaborador.setPrefWidth(900); 
        relatorioColaborador.setMinWidth(700);  
        relatorioColaborador.setAlignment(Pos.CENTER);
        relatorioColaborador.prefWidthProperty().bind(coluna1.widthProperty().multiply(0.85));
        relatorioColaborador.getStyleClass().add("caixa-relatorio"); 

        Label tituloRelatorio = new Label("Relatório detalhado ");
        tituloRelatorio.getStyleClass().add("titulo-relatorio-colab"); 
        relatorioColaborador.getChildren().add(tituloRelatorio);

        ComboBox<Colaborador> filtroColaborador = new ComboBox<>();
        filtroColaborador.setPromptText("Selecionar Colaborador");
        filtroColaborador.getStyleClass().add("area-filtro"); 
        
        List<Colaborador> todosColaboradores = colaboradorDAO.listAll();
        javafx.collections.ObservableList<Colaborador> obsColabs = FXCollections.observableArrayList(todosColaboradores);
        filtroColaborador.setItems(obsColabs);
        filtroColaborador.setCellFactory(lv -> new javafx.scene.control.ListCell<Colaborador>() {
            @Override
            protected void updateItem(Colaborador item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNome());
            }
        });
        filtroColaborador.setConverter(new javafx.util.StringConverter<Colaborador>() {
            @Override
            public String toString(Colaborador c) {
                return c == null ? "" : c.getNome();
            }
            @Override
            public Colaborador fromString(String string) { return null; }
        });

        relatorioColaborador.getChildren().add(filtroColaborador);

        Colaborador ultimo = todosColaboradores.stream()
                .max(Comparator.comparingInt(Colaborador::getId))
                .orElse(null);
        if (ultimo != null) {
            filtroColaborador.setValue(ultimo);
            atualizarRelatorioColaborador(relatorioColaborador, ultimo);
        }

        filtroColaborador.setOnAction(e -> {
            Colaborador escol = filtroColaborador.getValue();
            if (escol != null) atualizarRelatorioColaborador(relatorioColaborador, escol);
        });
        
        HBox relatorioContainer = new HBox(relatorioColaborador);
        relatorioContainer.setAlignment(Pos.CENTER);
        VBox.setMargin(relatorioContainer, new Insets(0, 0, 30, 0));

        relatorioContainer.prefWidthProperty().bind(coluna1.widthProperty());
        coluna1.getChildren().add(relatorioContainer);
        

        ScrollPane scrollPane = new ScrollPane(coluna1);
        scrollPane.setFitToWidth(true); 
        scrollPane.getStyleClass().add("scroll-pane-rh"); 

        HBox root = new HBox(barra, scrollPane);
        root.getStyleClass().add("root-rh"); 
        
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        scrollPane.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        coluna1.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/Metas.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosRH.css").toExternalForm());

        metasggStage.setScene(scene);
        metasggStage.setFullScreen(true);
        metasggStage.setFullScreenExitHint("");
        metasggStage.setTitle("Gerenciamento de Metas (PDIs)");
        metasggStage.show();

        Timeline atualizador = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
            carregarTabela();
            atualizarAlertas();
            atualizarGraficoPorArea(filtroArea.getValue(), serieConcluido, serieAndamento, serieNaoIniciado, serieAtrasado);
        }));
        atualizador.setCycleCount(Timeline.INDEFINITE);
        atualizador.play();
        atualizarGraficoPorArea(filtroArea.getValue(), serieConcluido, serieAndamento, serieNaoIniciado, serieAtrasado);
    }

    private void atualizarRelatorioColaborador(VBox relatorioColaborador, Colaborador selecionado) {

        javafx.scene.Node titulo = relatorioColaborador.getChildren().get(0);
        javafx.scene.Node filtro = relatorioColaborador.getChildren().get(1);

        relatorioColaborador.getChildren().clear();
        relatorioColaborador.getChildren().addAll(titulo, filtro);

       
        HBox linhaSuperior = new HBox(40); 
        linhaSuperior.setAlignment(Pos.TOP_LEFT);
        linhaSuperior.setPadding(new Insets(0, 0, 0, 20));
        linhaSuperior.spacingProperty().bind(relatorioColaborador.widthProperty().multiply(0.05));
        
        VBox dadosEsquerda = new VBox(20);
        dadosEsquerda.setAlignment(Pos.TOP_LEFT);
        dadosEsquerda.setPadding(new Insets(0, 0, 0, 20)); 

        String[][] infos = {
                {"Nome:", selecionado.getNome()},
                {"Setor:", selecionado.getSetor()},
                {"Cargo:", selecionado.getCargo()},
                {"Data de Nascimento:", selecionado.getData_nascimento() != null ? selecionado.getData_nascimento().toString() : "-"},
                {"Experiência:", selecionado.getExperiencia()},
                {"Observações:", selecionado.getObservacoes()}
        };

        for (String[] info : infos) {
            Label campo = new Label(info[0]);
            campo.getStyleClass().addAll("label-dado", "label-alerta-bold"); 
            
            Label valor = new Label(info[1] != null ? info[1] : "-");
            valor.getStyleClass().add("label-dado"); 
            
            dadosEsquerda.getChildren().add(new VBox(4, campo, valor));
        }

        List<Pdi> pdisDoColaborador = pdiDAO.listAll().stream()
                .filter(p -> p.getColaborador_id() == selecionado.getId())
                .collect(Collectors.toList());

        int totalPdis = pdisDoColaborador.size();
        int concluidos = (int) pdisDoColaborador.stream().filter(p -> p.getStatus() == Status.CONCLUIDO).count();
        double progresso = totalPdis > 0 ? (double) concluidos / totalPdis : 0.0;


        VBox ladoDireito = new VBox(15);
        ladoDireito.setAlignment(Pos.TOP_CENTER);
        ladoDireito.setPadding(new Insets(0, 0, 0, 80)); 

        long qtdNaoIniciado = pdisDoColaborador.stream().filter(p -> p.getStatus() == Status.NAO_INICIADO).count();
        long qtdEmAndamento = pdisDoColaborador.stream().filter(p -> p.getStatus() == Status.EM_ANDAMENTO).count();
        long qtdConcluido = pdisDoColaborador.stream().filter(p -> p.getStatus() == Status.CONCLUIDO).count();
        long qtdAtrasado = pdisDoColaborador.stream().filter(p -> p.getStatus() == Status.ATRASADO).count();

        ObservableList<PieChart.Data> dadosDonut = FXCollections.observableArrayList(
                new PieChart.Data("Não iniciado", qtdNaoIniciado),
                new PieChart.Data("Em andamento", qtdEmAndamento),
                new PieChart.Data("Concluído", qtdConcluido),
                new PieChart.Data("Atrasado", qtdAtrasado)
        );

        PieChart donutChart = new PieChart(dadosDonut);
        donutChart.setLabelsVisible(false);
        donutChart.setLegendVisible(false);
        donutChart.setStartAngle(90);
        donutChart.setClockwise(true);
        donutChart.setPrefSize(200, 200);

        for (PieChart.Data data : donutChart.getData()) {
            String nome = data.getName();
            String cor = switch (nome) {
                case "Concluído" -> "#c2f0ef";
                case "Em andamento" -> "#5ab2b0";
                case "Não iniciado" -> "#267a78";
                case "Atrasado" -> "#0b514f";
                default -> "#ffffff";
            };
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) newNode.setStyle("-fx-pie-color: " + cor + ";");
            });
            if (data.getNode() != null) data.getNode().setStyle("-fx-pie-color: " + cor + ";");
        }

        GridPane legendacolaborador = new GridPane();
        legendacolaborador.setHgap(20);
        legendacolaborador.setVgap(8);

        String[][] itensLegenda = {
                {"Concluído", "#c2f0ef"},
                {"Em andamento", "#5ab2b0"},
                {"Não iniciado", "#267a78"},
                {"Atrasado", "#0b514f"}
        };

        for (int i = 0; i < itensLegenda.length; i++) {
            Region bolinha = new Region();
            bolinha.setPrefSize(12, 12);
            bolinha.setMinSize(12, 12);
            bolinha.setMaxSize(12, 12);
            HBox.setHgrow(bolinha, Priority.NEVER);
            bolinha.setStyle(
                    "-fx-background-color: " + itensLegenda[i][1] + ";" +
                    "-fx-background-radius: 50%;" +
                    "-fx-border-radius: 50%;"
            );
            Label label = new Label(itensLegenda[i][0]);
            label.getStyleClass().add("label-dado"); 
            
            HBox item = new HBox(8, bolinha, label);
            item.setAlignment(Pos.CENTER_LEFT);
            legendacolaborador.add(item, i % 2, i / 2);
        }

        Label tituloProgresso = new Label("Progresso de PDIs");
        tituloProgresso.getStyleClass().addAll("label-dado", "label-alerta-bold"); 

        ProgressBar barraProgresso = new ProgressBar(progresso);
        barraProgresso.setPrefWidth(200);
        barraProgresso.getStyleClass().add("progress-bar"); 

        Label labelProgresso = new Label((int) (progresso * 100) + "%");
        labelProgresso.getStyleClass().add("label-dado"); 

        VBox progressoBox = new VBox(5, tituloProgresso, barraProgresso, labelProgresso);
        progressoBox.setAlignment(Pos.CENTER);

        Region espaco = new Region();
        espaco.setPrefHeight(10);

        ladoDireito.getChildren().addAll( progressoBox,espaco, donutChart, legendacolaborador);
        
        Region espacador = new Region();
        espacador.prefWidthProperty().bind(linhaSuperior.widthProperty().multiply(0.3)); 

        linhaSuperior.getChildren().addAll(dadosEsquerda, espacador, ladoDireito);
        
        relatorioColaborador.getChildren().add(linhaSuperior);
        relatorioColaborador.getChildren().add(new Separator());
        Label tituloPdis = new Label("PDIs");
        tituloPdis.getStyleClass().add("titulo-pdis-colab"); 
        relatorioColaborador.getChildren().add(tituloPdis);

        
        TableView<Pdi> tabelaPdis = new TableView<>();
        tabelaPdis.setMinHeight(200);
        tabelaPdis.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pdi, String> colPrazo = new TableColumn<>("Prazo");
        colPrazo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getPrazo().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));

        TableColumn<Pdi, String> colObjetivo = new TableColumn<>("Descrição");
        colObjetivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getObjetivo()));

        TableColumn<Pdi, Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().removeAll("status-nao-iniciado", "status-em-andamento", "status-concluido", "status-atrasado");
                    setStyle("");
                } else {
                    String statusText;
                    String statusClass;
                    switch (status) {
                        case NAO_INICIADO -> { statusText = "Não Iniciado"; setStyle("-fx-text-fill: gray;"); statusClass = "status-nao-iniciado"; }
                        case EM_ANDAMENTO -> { statusText = "Em Andamento"; setStyle("-fx-text-fill: orange;"); statusClass = "status-em-andamento"; }
                        case CONCLUIDO -> { statusText = "Concluído"; setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); statusClass = "status-concluido"; }
                        case ATRASADO -> { statusText = "Atrasado"; setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); statusClass = "status-atrasado"; }
                        default -> { statusText = ""; setStyle(""); statusClass = ""; }
                    }
                    setText(statusText);
                    setAlignment(Pos.CENTER);
                    getStyleClass().removeAll("status-nao-iniciado", "status-em-andamento", "status-concluido", "status-atrasado");
                    if (!statusClass.isEmpty()) getStyleClass().add(statusClass);
                }
            }
        });
        tabelaPdis.getColumns().addAll(colPrazo, colObjetivo, colStatus);
        tabelaPdis.setItems(FXCollections.observableArrayList(pdisDoColaborador));

        relatorioColaborador.getChildren().add(tabelaPdis);
    }


    private HBox criarCaixaLegenda(String cor, String texto) {
        Label corBox = new Label();
        corBox.setMinSize(20, 20);
        corBox.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 5;");
        Label nome = new Label(texto);
        nome.getStyleClass().add("label-dado");

        HBox item = new HBox(10, corBox, nome);
        item.setAlignment(Pos.CENTER_LEFT);
        return item;
    }

    private void carregarTabela() {
        List<Pdi> todos = pdiDAO.listAll();
        todos.sort(Comparator.comparing(Pdi::getPrazo));
        List<Pdi> proximosOito = todos.stream().limit(8).collect(Collectors.toList());
        dados = FXCollections.observableArrayList(proximosOito);
        if (tabela != null) tabela.setItems(dados);
    }

    private void atualizarAlertas() {
        int andamento = pdiDAO.contarPorStatus(Status.EM_ANDAMENTO);
        int concluidas = pdiDAO.contarPorStatus(Status.CONCLUIDO);
        int futuras = pdiDAO.contarPorStatus(Status.NAO_INICIADO);
        int atrasadas = pdiDAO.contarPorStatus(Status.ATRASADO);

        alerta1.setText("Em andamento: " + andamento + " " + (andamento == 1 ? "PDI" : "PDIs"));
        alerta2.setText("Concluídos: " + concluidas + " " + (concluidas == 1 ? "PDI" : "PDIs"));
        alerta3.setText("Não iniciados: " + futuras + " " + (futuras == 1 ? "PDI" : "PDIs"));
        alerta4.setText("Atrasados: " + atrasadas + " " + (atrasadas == 1 ? "PDI" : "PDIs"));
    }

    private void atualizarAlertasFiltro(String filtro) {
        Map<String, List<Pdi>> porSetor = pdiDAO.listAll().stream()
                .filter(p -> {
                    if ("Total".equals(filtro)) return true;
                    return switch (filtro) {
                        case "Concluídos" -> p.getStatus() == Status.CONCLUIDO;
                        case "Em Andamento" -> p.getStatus() == Status.EM_ANDAMENTO;
                        case "Não Iniciados" -> p.getStatus() == Status.NAO_INICIADO;
                        case "Atrasados" -> p.getStatus() == Status.ATRASADO;
                        default -> true;
                    };
                })
                .collect(Collectors.groupingBy(p -> {
                    Colaborador c = colaboradorDAO.getColaboradorById(p.getColaborador_id());
                    return (c != null) ? c.getSetor() : "Desconhecido";
                }));

        Set<String> setoresExistentes = pdiDAO.listAll().stream()
                .map(p -> {
                    Colaborador c = colaboradorDAO.getColaboradorById(p.getColaborador_id());
                    return (c != null) ? c.getSetor() : "Desconhecido";
                })
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Label> novosAlertas = new ArrayList<>();
        for (String setor : setoresExistentes) {
            List<Pdi> lista = porSetor.getOrDefault(setor, Collections.emptyList());
            int qtd = lista.size();
            String texto;
            if ("Total".equals(filtro)) {
                texto = setor + ": " + qtd + " " + (qtd == 1 ? "PDI" : "PDIs");
            } else {
                String statusTexto = switch (filtro) {
                    case "Concluídos" -> qtd == 1 ? "concluído" : "concluídos";
                    case "Em Andamento" -> qtd == 1 ? "em andamento" : "em andamento";
                    case "Não Iniciados" -> qtd == 1 ? "não iniciado" : "não iniciados";
                    case "Atrasados" -> qtd == 1 ? "atrasado" : "atrasados";
                    default -> "";
                };
                texto = setor + " : " + qtd + " " + (qtd == 1 ? "PDI" : "PDIs") ;
            }
            Label lbl = new Label(texto);
            lbl.getStyleClass().add("label-dado"); 

            novosAlertas.add(lbl);
        }
        conteudoAlertas.getChildren().setAll(novosAlertas);

        if ("Total".equals(filtro)) {
            tituloAlertas.setText("Quantidade");
        } else {
            tituloAlertas.setText("PDIs");
        }
    }

    private void atualizarGraficoPorArea(String areaFiltro,
                                         XYChart.Series<String, Number> serieConcluido,
                                         XYChart.Series<String, Number> serieAndamento,
                                         XYChart.Series<String, Number> serieNaoIniciado,
                                         XYChart.Series<String, Number> serieAtrasado) {
    	if ("Todos".equals(areaFiltro)) {
            grafico.setCategoryGap(10);  
            grafico.setBarGap(-13);       
        } else {
            grafico.setCategoryGap(10);  
            grafico.setBarGap(16);       
        }


        serieConcluido.getData().clear();
        serieAndamento.getData().clear();
        serieNaoIniciado.getData().clear();
        serieAtrasado.getData().clear();

        List<Pdi> todos = pdiDAO.listAll();
        int maxValor = 0;

        if ("Todos".equals(areaFiltro)) {
            int concluidos = (int) todos.stream().filter(p -> p.getStatus() == Status.CONCLUIDO).count();
            int andamento = (int) todos.stream().filter(p -> p.getStatus() == Status.EM_ANDAMENTO).count();
            int naoIniciado = (int) todos.stream().filter(p -> p.getStatus() == Status.NAO_INICIADO).count();
            int atrasado = (int) todos.stream().filter(p -> p.getStatus() == Status.ATRASADO).count();

            serieConcluido.getData().add(new XYChart.Data<>("Concluído", concluidos));
            serieAndamento.getData().add(new XYChart.Data<>("Em Andamento", andamento));
            serieNaoIniciado.getData().add(new XYChart.Data<>("Não Iniciado", naoIniciado));
            serieAtrasado.getData().add(new XYChart.Data<>("Atrasado", atrasado));

            maxValor = Math.max(maxValor, Math.max(Math.max(concluidos, andamento),
                    Math.max(naoIniciado, atrasado)));

        } else {
            List<Pdi> listaSetor = todos.stream()
                    .filter(p -> {
                        Colaborador c = colaboradorDAO.getColaboradorById(p.getColaborador_id());
                        return c != null && areaFiltro.equals(c.getSetor());
                    })
                    .toList();

            int concluidos = (int) listaSetor.stream().filter(p -> p.getStatus() == Status.CONCLUIDO).count();
            int andamento = (int) listaSetor.stream().filter(p -> p.getStatus() == Status.EM_ANDAMENTO).count();
            int naoIniciado = (int) listaSetor.stream().filter(p -> p.getStatus() == Status.NAO_INICIADO).count();
            int atrasado = (int) listaSetor.stream().filter(p -> p.getStatus() == Status.ATRASADO).count();

            serieConcluido.getData().add(new XYChart.Data<>(areaFiltro, concluidos));
            serieAndamento.getData().add(new XYChart.Data<>(areaFiltro, andamento));
            serieNaoIniciado.getData().add(new XYChart.Data<>(areaFiltro, naoIniciado));
            serieAtrasado.getData().add(new XYChart.Data<>(areaFiltro, atrasado));

            maxValor = Math.max(maxValor, Math.max(Math.max(concluidos, andamento),
                    Math.max(naoIniciado, atrasado)));
        }

        int limite = ((maxValor + 9) / 10) * 10;
        eixoY.setAutoRanging(false);
        eixoY.setLowerBound(0);
        eixoY.setUpperBound(limite);
        eixoY.setTickUnit(Math.max(1, limite / 5.0));
    }
}