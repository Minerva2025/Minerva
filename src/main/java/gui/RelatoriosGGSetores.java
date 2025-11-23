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

import java.util.Arrays;
import java.util.List;

public class RelatoriosGGSetores extends Application{
    PdiDAO pdiDAO = new PdiDAO();
    ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    List<Colaborador> colaboradores = colaboradorDAO.listAll();
    private VBox cards;
    private VBox setorCardsContainer;
    private List<String> setores = Arrays.asList(
            "Desenvolvimento",
            "Marketing",
            "Suporte",
            "Financeiro",
            "Pesquisa e Inovação"
    );

    private Usuario logado;
    public RelatoriosGGSetores(Usuario usuariologado) {this.logado = usuariologado;}


    @Override
    public void start(Stage relatoriosggsetoresStage){
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

        Text titulo = new Text("Todos os setores");
        titulo.setId("titulo");
        VBox.setMargin(titulo, new Insets(4, 0, 30, 0));
        boxTitulo.getChildren().add(titulo);

        // Navegação
        VBox navegacao = new VBox();
        navegacao.setId("navegacao");

        // Cards
        setorCardsContainer = new VBox();
        setorCardsContainer.setId("setorCardsContainer");
        setorCardsContainer.setSpacing(30);
        cards = new VBox();
        cards.setId("cards");
        criarSetorCards(setores);
        cards.getChildren().add(setorCardsContainer);

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
        StackPane.setMargin(blob1, new Insets(40, -50, 0, 0));
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
        blob1.setRadiusX(70);
        blob1.setRadiusY(70);
        blob2.setRadiusX(70);
        blob2.setRadiusY(70);
        blob3.setRadiusX(60);
        blob3.setRadiusY(60);

        // Aplicar classes responsivas baseadas no tamanho da tela
        applyResponsiveClasses(scene, root);

        relatoriosggsetoresStage.setScene(scene);
        relatoriosggsetoresStage.setFullScreen(true);
        relatoriosggsetoresStage.setFullScreenExitHint("");
        relatoriosggsetoresStage.show();

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
    private void criarSetorCards(List<String> setores) {
        for (String setor : setores) {
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
            setorCard.getStyleClass().add("setorcardsContainer");
            setorCard.setId("setorCard_" + setor.replace(" ", ""));

            // VBox SetorCardInfos com as informações dos cards de setores
            VBox setorCardInfos = new VBox();
            setorCardInfos.setId("setorCardInfos_" + setor.replace(" ", ""));

            Text setorTitulo = new Text(setor);
            setorTitulo.getStyleClass().add("CardsTitle");
            setorTitulo.setId("setorTitulo_" + setor.replace(" ", ""));

            Text totalColaboradores = new Text("Colaboradores: " + colaboradoresDoSetor.size());
            totalColaboradores.getStyleClass().add("totalColaboradores");
            totalColaboradores.setId("totalColaboradores_" + setor.replace(" ", ""));

            PieChart chart = new PieChart(FXCollections.observableArrayList(
                    new PieChart.Data("Metas em andamento", totalAndamento),
                    new PieChart.Data("Metas concluidas", totalConcluido),
                    new PieChart.Data("Metas atrasadas", totalAtrasado)
            ));

            chart.getStyleClass().add("cardspiechart");
            chart.setId("setorPieChart_" + setor.replace(" ", ""));

            setorCardInfos.getChildren().addAll(setorTitulo, totalColaboradores);

            setorCard.getChildren().addAll(setorCardInfos, chart);

            setorCardsContainer.getChildren().add(setorCard);
        }
    }
}