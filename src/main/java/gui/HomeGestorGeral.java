package gui;

import java.time.LocalDate;
import java.util.List;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Pdi;
import model.Status;
import model.Usuario;
import javafx.scene.layout.Priority;


public class HomeGestorGeral extends Application {

    private Usuario logado;
    private PdiDAO pdiDAO = new PdiDAO();
    private VBox alertBox;             
    private Text alertMessageText;     

    public HomeGestorGeral(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }

    @Override
    public void start(Stage homeggStage) {

        String primeiroNome = logado.getNome().split(" ")[0];
        ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

        int totalColaboradores = colaboradorDAO.listAll().size();

        List<Pdi> todosPdis = pdiDAO.listAll();

        int pdiAtivoCount = 0;
        int pdiInativoCount = 0;
        int pdiConcluidoCount = 0; 

        for (Pdi pdi : todosPdis) {
            switch (pdi.getStatus()) {
                case EM_ANDAMENTO, ATRASADO -> pdiAtivoCount++;
                case NAO_INICIADO -> pdiInativoCount++;
                case CONCLUIDO -> pdiConcluidoCount++;
            }
        }

        Text titulo = new Text("Bem-vindo " + primeiroNome + "!");
        titulo.setId("titulo");

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

        Text colaboradores = new Text("Colaboradores: " + totalColaboradores);
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
        
   
        chartsContainer.setPrefWidth(Double.MAX_VALUE);
        
        VBox.setMargin(chartsContainer, new Insets(20, 45, 0, 0));

  
        StackPane boxChart1 = new StackPane();
        boxChart1.setId("boxChart1");
        boxChart1.getStyleClass().add("card");
     
        HBox.setHgrow(boxChart1, Priority.ALWAYS);
        boxChart1.setMaxWidth(Double.MAX_VALUE);
        boxChart1.setMinWidth(0);

      
        StackPane boxChart2 = new StackPane();
        boxChart2.setId("boxChart2");
        boxChart2.getStyleClass().add("card");
    
        HBox.setHgrow(boxChart1, Priority.ALWAYS);
        boxChart2.setMaxWidth(Double.MAX_VALUE);
        boxChart2.setMinWidth(0); 

        chartsContainer.getChildren().addAll(boxChart1, boxChart2);
        

        double spacing = chartsContainer.getSpacing();
        boxChart1.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.5).subtract(spacing / 2.0)
        );
        boxChart2.prefWidthProperty().bind(
            chartsContainer.widthProperty().multiply(0.5).subtract(spacing / 2.0)
        );

        
        alertBox = new VBox(6);
        alertBox.setId("alertBox");
        Text alertTitle = new Text("Atenção!");
        alertTitle.setId("alertTitle");
        alertMessageText = new Text("");
        alertMessageText.setId("alertMessage");

        alertBox.getChildren().addAll(alertTitle, alertMessageText);
       
        alertBox.managedProperty().bind(alertBox.visibleProperty());

        VBox center = new VBox();
        center.setId("center");
       
        center.getChildren().addAll(titulo, container, chartsContainer, alertBox, blob1, blob2, blob3);

        BarraLateralGG barra = new BarraLateralGG(logado);

        HBox root = new HBox();
        root.setStyle("-fx-background-color: #1E1E1E");
        root.getChildren().addAll(barra, center);

        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/HomeGestorGeral.css").toExternalForm());

        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
        blob1.radiusYProperty().bind(blob1.radiusXProperty());

        blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
        blob2.radiusYProperty().bind(blob2.radiusXProperty());

        blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
        blob3.radiusYProperty().bind(blob3.radiusXProperty());

        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
        blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
        blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));

        StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
        blob2.translateXProperty().bind(scene.widthProperty().multiply(0.4));
        blob2.translateYProperty().bind(scene.heightProperty().multiply(0.3));

        StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
        blob3.translateXProperty().bind(scene.widthProperty().multiply(0.52));
        blob3.translateYProperty().bind(scene.heightProperty().multiply(0.07));

        homeggStage.setScene(scene);
        homeggStage.setFullScreen(true);
        homeggStage.setFullScreenExitHint("");
        homeggStage.show();

        
        updateAlertVisibility();

       
        Timeline refresher = new Timeline(new KeyFrame(Duration.seconds(15), ev -> {
            Platform.runLater(this::updateAlertVisibility);
        }));
        refresher.setCycleCount(Timeline.INDEFINITE);
        refresher.play();
    }

    private void updateAlertVisibility() {
        List<Pdi> todos = pdiDAO.listAll();
        int expiradas = 0;
        LocalDate hoje = LocalDate.now();

        for (Pdi p : todos) {
            LocalDate prazo = p.getPrazo();
            if (prazo != null && prazo.isBefore(hoje) && p.getStatus() != Status.CONCLUIDO) {
                expiradas++;
            }
        }

        if (expiradas > 0) {
            alertMessageText.setText(expiradas + " PDI(s) expirado(s) — verifique e atribua ações.");
            alertBox.setVisible(true);
        } else {
            alertBox.setVisible(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
