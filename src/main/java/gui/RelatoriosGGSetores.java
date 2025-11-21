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

        // Navegação
        VBox navegacao = new VBox();

        // Cards
        setorCardsContainer = new VBox();
        cards = new VBox();
        criarSetorCards(setores);
        cards.getChildren().add(setorCardsContainer);

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

        relatoriosggsetoresStage.setScene(scene);
        relatoriosggsetoresStage.setFullScreen(true);
        relatoriosggsetoresStage.setFullScreenExitHint("");
        relatoriosggsetoresStage.show();

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