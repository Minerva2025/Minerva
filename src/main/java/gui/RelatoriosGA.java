package gui;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Usuario;


public class RelatoriosGA extends Application {
       
	private Usuario logado;

    public RelatoriosGA(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }
	
  
    @Override
    public void start(Stage relatoriosgaStage) {
      
        
        VBox center = new VBox();
        center.setId("center");
        
        // Título principal
        Text titulo = new Text("Relatórios de PDIs Marketing Digital ");
        titulo.setId("titulo");
        
      
        
        // A BARRA LATERAL 
        center.getChildren().addAll(titulo);
        BarraLateralGA barraLateral = new BarraLateralGA(logado);
    
        
        HBox root = new HBox();
        root.setStyle("-fx-background-color: #1E1E1E");
        root.getChildren().addAll(barraLateral, center);
        
        center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
        barraLateral.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
        
        
        
        // CSSs
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/RelatoriosGA.css").toExternalForm()); 

        
        relatoriosgaStage.setFullScreen(true);
        relatoriosgaStage.setFullScreenExitHint("");
        relatoriosgaStage.setScene(scene);
        relatoriosgaStage.show();
    }
    
    
    
    // Método principal que será chamado pela sua tela HomeGestorArea
    public Parent createContent() {
        // 1. O container principal do conteúdo será um VBox
        VBox content = new VBox();
      
        // Adicionar o arquivo CSS para esta tela
        content.getStylesheets().add(getClass().getResource("/gui/RelatoriosGA.css").toExternalForm());
        // Aplicar um ID para estilizar o fundo escuro
        content.setId("main-content-area");
        
        return content;
    }
   
    
}