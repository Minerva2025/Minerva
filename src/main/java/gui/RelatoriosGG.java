package gui;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import java.util.List;


public class RelatoriosGG extends Application {

    PdiDAO pdiDAO = new PdiDAO();
    ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    List<Colaborador> colaboradores = colaboradorDAO.listAll();
    private VBox cards;
    private VBox cardsBoxTop;
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

        // BarChart

        VBox boxBarchart = new VBox();

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Setor");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Evolução");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Evolução das metas por setor.");
        barra.setId("barchart");

        XYChart.Series<String, Number> serieConcluido = new XYChart.Series<>();
        serieConcluido.setName("Concluido");
        XYChart.Series<String, Number> serieAndamento = new XYChart.Series<>();
        serieAndamento.setName("Em andamento");
        XYChart.Series<String, Number> serieNaoIniciado = new XYChart.Series<>();
        serieNaoIniciado.setName("Não iniciado");
        XYChart.Series<String, Number> serieAtrasado = new XYChart.Series<>();
        serieAtrasado.setName("Atrasado");

        int totalConcluido = 0;
        int totalAndamento = 0;
        int totalNaoIniciado = 0;
        int totalAtrasado = 0;

        for(Colaborador colaborador : colaboradores){
            List<Pdi> listaDePdis = pdiDAO.findByColaborador(colaborador.getId());

            int totalConcluidoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                    .count();
            totalConcluido = totalConcluido + totalConcluidoPorColaborador;

            int totalAndamentoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                    .count();
            totalAndamento = totalAndamento + totalAndamentoPorColaborador;

            int totalNaoIniciadoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.NAO_INICIADO)
                    .count();
            totalNaoIniciado = totalNaoIniciado + totalNaoIniciadoPorColaborador;

            int totalAtrasadoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                    .count();
            totalAtrasado = totalAtrasado + totalAtrasadoPorColaborador;
        }

        for(Colaborador colaborador : colaboradores){
            serieConcluido.getData().add(new XYChart.Data<>(colaborador.getSetor(), totalConcluido));
            serieAndamento.getData().add(new XYChart.Data<>(colaborador.getSetor(), totalAndamento));
            serieNaoIniciado.getData().add(new XYChart.Data<>(colaborador.getSetor(), totalNaoIniciado));
            serieAtrasado.getData().add(new XYChart.Data<>(colaborador.getSetor(), totalAtrasado));
        }

        barChart.getData().addAll(serieConcluido,serieAndamento,serieNaoIniciado,serieAtrasado);
        boxBarchart.getChildren().add(barChart);

        // PieChart

        VBox boxPieChart = new VBox();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Metas em andamento", totalAndamento),
                new PieChart.Data("Metas atrasadas", totalAtrasado)
        );

        PieChart pieChart = new PieChart(pieChartData);

        boxPieChart.getChildren().add(pieChart);

        // Gráficos HBox

        HBox graficos = new HBox();

        graficos.getChildren().addAll(boxBarchart,boxPieChart);

        // VBox Seção de Cards

        cards = new VBox();

        // Barra de pesquisa

        cardsBoxTop = new VBox();
        Text cardsTitulo = new Text("Buscar relátorios");

        HBox cardsBarraDePesquisa = new HBox();
        TextField barraDePesquisa = new TextField("Pesquisar");

        Button filtrar_btn = new Button("Filtrar");
        filtrar_btn.setOnAction(event -> filtrarColaboradores(barraDePesquisa.getText().toLowerCase()));

        cardsBarraDePesquisa.getChildren().addAll(barraDePesquisa,filtrar_btn);

        cardsBoxTop.getChildren().addAll(cardsTitulo,cardsBarraDePesquisa);

        // Cards Relatorios por nome

        totalConcluido = 0;
        totalAtrasado = 0;
        totalAndamento = 0;

        cards.getChildren().add(cardsBoxTop);

        limit = 1;
        int contador = 0;

        for(Colaborador relatorio: colaboradores){
            if(contador >= limit) break;
            contador++;

            List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorio.getId());

            int totalConcluidoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                    .count();
            totalConcluido = totalConcluido + totalConcluidoPorColaborador;

            int totalAndamentoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                    .count();
            totalAndamento = totalAndamento + totalAndamentoPorColaborador;

            int totalAtrasadoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                    .count();
            totalAtrasado = totalAtrasado + totalAtrasadoPorColaborador;

            HBox nameCard = new HBox();
            VBox nameCardInfos = new VBox();

            Text nameCardTitulo = new Text(relatorio.getNome());
            Text nameCardSetor = new Text(relatorio.getSetor());
            Text nameCardCargo = new Text(relatorio.getCargo());

            ObservableList<PieChart.Data> nameCardPieChartData = FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            );
            PieChart nameCardPieChart = new PieChart(nameCardPieChartData);

            nameCardInfos.getChildren().addAll(nameCardTitulo, nameCardSetor, nameCardCargo);
            nameCard.getChildren().addAll(nameCardInfos, nameCardPieChart);

            cards.getChildren().add(nameCard);

            Button maisNameCards = new Button("Ver mais relatórios de colaboradores");
            cards.getChildren().add(maisNameCards);
        }

        // Cards Relatorios por setor

        contador = 0;

        for (String setor : setores) {
            if (contador >= limit) break;
            contador++;

            List<Colaborador> colaboradoresDoSetor = colaboradorDAO.findBySetor(setor);

            totalConcluido = 0;
            totalAndamento = 0;
            totalAtrasado = 0;

            for (Colaborador c : colaboradoresDoSetor) {
                List<Pdi> listaDePdis = pdiDAO.findByColaborador(c.getId());

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
            VBox setorInfos = new VBox();

            Text setorTitulo = new Text(setor);
            Text totalColaboradores = new Text("Colaboradores: " + colaboradoresDoSetor.size());

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            );
            PieChart chart = new PieChart(pieData);

            setorInfos.getChildren().addAll(setorTitulo, totalColaboradores);
            setorCard.getChildren().addAll(setorInfos, chart);

            cards.getChildren().add(setorCard);
        }

        Button maisSetorCards = new Button("Ver mais relatórios de divisões do setor");
        cards.getChildren().add(maisSetorCards);


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

        cards.getChildren().clear();
        cards.getChildren().add(cardsBoxTop);

        int contador = 0;

        for (Colaborador relatorios : colaboradores) {
            if (contador >= limit) break;

            boolean incluiCard = relatorios.getNome().toLowerCase().contains(pesquisa)
                    || relatorios.getSetor().toLowerCase().contains(pesquisa)
                    || relatorios.getCargo().toLowerCase().contains(pesquisa);

            if (incluiCard) {
                contador++;

                HBox nameCard = new HBox();
                VBox nameCardInfos = new VBox();

                Text nameCardTitulo = new Text(relatorios.getNome());
                Text nameCardSetor = new Text(relatorios.getSetor());
                Text nameCardCargo = new Text(relatorios.getCargo());

                List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorios.getId());
                int totalConcluido = (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                        .count();
                int totalAndamento = (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                        .count();
                int totalAtrasado = (int) listaDePdis.stream()
                        .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                        .count();

                PieChart nameCardChart = new PieChart(FXCollections.observableArrayList(
                        new PieChart.Data("Metas em andamento", totalAndamento),
                        new PieChart.Data("Metas concluidas", totalConcluido),
                        new PieChart.Data("Metas atrasadas", totalAtrasado)
                ));

                nameCardInfos.getChildren().addAll(nameCardTitulo, nameCardSetor, nameCardCargo);
                nameCard.getChildren().addAll(nameCardInfos, nameCardChart);

                cards.getChildren().add(nameCard);
            }
        }
    }


}
