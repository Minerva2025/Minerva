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
        center.setId("relatoriosgg");
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
        Text cardsTitulo = new Text("Buscar relátorios");

        HBox cardsBarraDePesquisa = new HBox();
        TextField barraDePesquisa = new TextField("Pesquisar");

        Button filtrar_btn = new Button("Filtrar");
        filtrar_btn.setOnAction(event -> filtrarColaboradores(barraDePesquisa.getText().toLowerCase()));

        cardsBarraDePesquisa.getChildren().addAll(barraDePesquisa,filtrar_btn);
        navegacao.getChildren().addAll(cardsTitulo,cardsBarraDePesquisa);

        // Cards
        nameCardsContainer = new VBox();
        cards = new VBox();
        criarNameCards(colaboradores);
        cards.getChildren().add(nameCardsContainer);

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
        center.getChildren().addAll(boxTitulo,navegacao, cards);

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

        relatoriosggcolaboradoresStage.setScene(scene);
        relatoriosggcolaboradoresStage.setFullScreen(true);
        relatoriosggcolaboradoresStage.setFullScreenExitHint("");
        relatoriosggcolaboradoresStage.show();

    }

    // Métodos
    private void criarNameCards(List<Colaborador> lista) {
        for (Colaborador relatorio : lista) {
            List<Pdi> listaDePdis = pdiDAO.findByColaborador(relatorio.getId());

            int totalConcluido = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.CONCLUIDO)
                    .count();

            int totalAndamento = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.EM_ANDAMENTO)
                    .count();

            int totalAtrasado = (int) listaDePdis.stream()
                    .filter(pdi -> pdi.getStatus() == Status.ATRASADO)
                    .count();

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