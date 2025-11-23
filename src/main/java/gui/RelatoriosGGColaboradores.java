package gui;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
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

import java.util.List;

public class RelatoriosGGColaboradores extends Application{
    PdiDAO pdiDAO = new PdiDAO();
    ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    List<Colaborador> colaboradores = colaboradorDAO.listAll();
    private VBox cards;
    private VBox nameCardsContainer;

    private Usuario logado;
    public RelatoriosGGColaboradores(Usuario usuariologado) {this.logado = usuariologado;}


    @Override
    public void start(Stage relatoriosggcolaboradoresStage){
        // Criação da barra lateral
        BarraLateralGG barra = new BarraLateralGG(logado);

        // VBox Center
        VBox center = new VBox();
        center.setId("center");
        center.setAlignment(Pos.TOP_CENTER);
        center.setSpacing(15);
        center.setPadding(new Insets(15));
        center.setStyle("-fx-background-color: #1E1E1E;");

        //VBox Titulo
        VBox boxTitulo = new VBox();
        boxTitulo.setId("boxTitulo");
        boxTitulo.setAlignment(Pos.TOP_LEFT);
        boxTitulo.setSpacing(15);
        boxTitulo.setPadding(new Insets(30));

        Text titulo = new Text("Todos os colaboradores");
        titulo.setId("titulo");
        VBox.setMargin(titulo, new Insets(4, 0, 30, 0));
        boxTitulo.getChildren().add(titulo);

        // Pesquisa e navegação
        VBox navegacao = new VBox();
        navegacao.setId("navegacao");

        Text cardsTitulo = new Text("Buscar relátorios");
        cardsTitulo.getStyleClass().add("cardsContainerTitle");
        cardsTitulo.setId("cardsTitulo");

        HBox cardsBarraDePesquisa = new HBox();
        cardsBarraDePesquisa.setId("cardsBarraDePesquisa");
        cardsBarraDePesquisa.getStyleClass().add("cardsFilter");

        TextField barraDePesquisa = new TextField();
        barraDePesquisa.setPromptText("Pesquisar");
        barraDePesquisa.getStyleClass().add("filtro");
        barraDePesquisa.setId("barraDePesquisa");

        Button filtrar_btn = new Button("Filtrar");
        filtrar_btn.getStyleClass().add("filtro_btn");
        filtrar_btn.setId("filtrar_btn");
        filtrar_btn.setOnAction(event -> filtrarColaboradores(barraDePesquisa.getText().toLowerCase()));

        cardsBarraDePesquisa.getChildren().addAll(barraDePesquisa,filtrar_btn);
        navegacao.getChildren().addAll(cardsTitulo,cardsBarraDePesquisa);

        // Cards
        nameCardsContainer = new VBox();
        nameCardsContainer.setId("nameCardsContainer");
        nameCardsContainer.setSpacing(30);
        cards = new VBox();
        cards.setId("cards");
        criarNameCards(colaboradores);
        cards.getChildren().add(nameCardsContainer);

        // VBox contentContainer - conteúdo principal
        VBox contentContainer = new VBox();
        contentContainer.setId("contentContainer");
        contentContainer.getChildren().addAll(boxTitulo, navegacao, cards);
        contentContainer.setSpacing(30);
        contentContainer.setPadding(new Insets(20));

        // StackPane para envolver o conteúdo e os blobs
        StackPane contentWithBlobs = new StackPane();
        contentWithBlobs.setId("contentWithBlobs");

        // Blobs
        Ellipse blob1 = new Ellipse();
        blob1.setId("blob1");
        blob1.getStyleClass().add("blob");
        Ellipse blob2 = new Ellipse();
        blob2.setId("blob2");
        blob2.getStyleClass().add("blob");
        Ellipse blob3 = new Ellipse();
        blob3.setId("blob3");
        blob3.getStyleClass().add("blob");

        GaussianBlur blur = new GaussianBlur(40);
        blob1.setEffect(blur);
        blob2.setEffect(blur);
        blob3.setEffect(blur);

        // Adiciona os elementos na ordem correta: blobs primeiro, depois conteúdo
        contentWithBlobs.getChildren().addAll(blob1, blob2, blob3, contentContainer);

        // Configura os blobs para ficarem atrás do conteúdo
        blob1.toBack();
        blob2.toBack();
        blob3.toBack();

        // Posicionamento absoluto dos blobs dentro do StackPane
        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
        StackPane.setAlignment(blob2, Pos.TOP_RIGHT);
        StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);

        // Margens para posicionar melhor os blobs
        StackPane.setMargin(blob1, new Insets(-50, 500, 0, 0));
        StackPane.setMargin(blob2, new Insets(-50, 250, 0, 0));
        StackPane.setMargin(blob3, new Insets(0, 0, -50, -50));

        // ScrollPane para os cards
        ScrollPane scrollPane = new ScrollPane(contentWithBlobs);
        scrollPane.setId("scrollPane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Definir altura máxima do ScrollPane para evitar problemas de layout
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        // AddAll do Center
        center.getChildren().add(scrollPane);

        // HBox root
        HBox root = new HBox();
        root.getStyleClass().add("root");
        root.setId("root");
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
        blob3.setRadiusX(60);
        blob3.setRadiusY(60);

        // Aplicar classes responsivas baseadas no tamanho da tela
        applyResponsiveClasses(scene, root);

        relatoriosggcolaboradoresStage.setScene(scene);
        relatoriosggcolaboradoresStage.setFullScreen(true);
        relatoriosggcolaboradoresStage.setFullScreenExitHint("");
        relatoriosggcolaboradoresStage.show();

    }

    // Método para aplicar classes responsivas
    private void applyResponsiveClasses(Scene scene, HBox root) {
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double width = newWidth.doubleValue();

            // Remove todas as classes de tamanho de tela
            root.getStyleClass().removeAll(
                    "small-screen", "medium-screen", "large-screen", "extra-large-screen",
                    "mobile-landscape"
            );

            // Aplica classes baseadas na largura da tela
            if (width < 768) {
                root.getStyleClass().add("small-screen");
            } else if (width < 1024) {
                root.getStyleClass().add("medium-screen");
            } else if (width < 1440) {
                root.getStyleClass().add("large-screen");
            } else {
                root.getStyleClass().add("extra-large-screen");
            }

            // Verifica se é landscape em dispositivos móveis
            double height = scene.getHeight();
            if (width > height && width < 1024) {
                root.getStyleClass().add("mobile-landscape");
            }
        });

        // Também verifica a altura para ajustes específicos
        scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            double width = scene.getWidth();
            double height = newHeight.doubleValue();

            root.getStyleClass().removeAll("mobile-landscape");

            if (width > height && width < 1024) {
                root.getStyleClass().add("mobile-landscape");
            }
        });
    }

    // Métodos
    private void criarNameCards(List<Colaborador> lista) {
        for (Colaborador relatorio : lista) {
            List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorio.getId());

            // CORREÇÃO: Calcular totais INDIVIDUAIS para cada colaborador
            int totalConcluidoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                    .count();

            int totalAndamentoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                    .count();

            int totalAtrasadoPorColaborador = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                    .count();

            // HBox NameCard
            HBox nameCard = new HBox();
            nameCard.getStyleClass().add("namecardsContainer");
            nameCard.setId("nameCard_" + relatorio.getId());

            // VBox NameCard com as informações escritas.
            VBox nameCardInfos = new VBox();
            nameCardInfos.getStyleClass().add("nameCardInfosContainer");
            nameCardInfos.setId("nameCardInfos_" + relatorio.getId());

            VBox nameCardTitleContainer = new VBox();
            nameCardTitleContainer.getStyleClass().add("nameCardTitleContainer");
            nameCardTitleContainer.setId("nameCardTitleContainer_" + relatorio.getId());

            Text nameCardTitle = new Text(relatorio.getNome());
            nameCardTitle.getStyleClass().add("CardsTitle");
            nameCardTitle.setId("nameCardTitle_" + relatorio.getId());

            nameCardTitleContainer.getChildren().add(nameCardTitle);

            VBox nameInfos = new VBox();
            nameInfos.getStyleClass().add("nameinfos");
            nameInfos.setId("nameInfos_" + relatorio.getId());

            Text nameCardSetor = new Text(relatorio.getSetor());
            nameCardSetor.getStyleClass().add("nameCardSetor");
            nameCardSetor.setId("nameCardSetor_" + relatorio.getId());

            Text nameCardCargo = new Text(relatorio.getCargo());
            nameCardCargo.getStyleClass().add("nameCardCargo");
            nameCardCargo.setId("nameCardCargo_" + relatorio.getId());

            nameInfos.getChildren().addAll(nameCardSetor, nameCardCargo);

            nameCardInfos.getChildren().addAll(nameCardTitleContainer, nameInfos);

            // CORREÇÃO: Usar os totais INDIVIDUAIS do colaborador atual
            PieChart nameCardPieChart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamentoPorColaborador),
                    new PieChart.Data("Metas concluídas", totalConcluidoPorColaborador),
                    new PieChart.Data("Metas atrasadas", totalAtrasadoPorColaborador)
            ));

            nameCardPieChart.getStyleClass().add("cardspiechart");
            nameCardPieChart.setId("nameCardPieChart_" + relatorio.getId());

            nameCard.getChildren().addAll(nameCardInfos, nameCardPieChart);

            nameCardsContainer.getChildren().add(nameCard);
        }
    }
    private void filtrarColaboradores(String pesquisa) {
        nameCardsContainer.getChildren().clear();

        List<Colaborador> filtrados = colaboradores.stream()
                .filter(relatorio ->
                        relatorio.getNome().toLowerCase().contains(pesquisa)
                                || relatorio.getSetor().toLowerCase().contains(pesquisa)
                                || relatorio.getCargo().toLowerCase().contains(pesquisa)
                )
                .toList();

        criarNameCards(filtrados);
    }

}