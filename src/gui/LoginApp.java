package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import controller.UsuarioController;
import model.Funcao;
import model.Usuario;

public class LoginApp extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		
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
		
		Text titulo = new Text("LOGIN");
		titulo.setId("titulo-text");
		
		TextField cpfField = new TextField();
		cpfField.setPromptText("CPF");
		cpfField.getStyleClass().add("input");
		
		cpfField.textProperty().addListener((obs, oldText, newText) -> {
            String numeric = newText.replaceAll("[^\\d]", "");
            if (numeric.length() > 11) numeric = numeric.substring(0, 11);

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < numeric.length(); i++) {
                if (i == 3 || i == 6) formatted.append('.');
                if (i == 9) formatted.append('-');
                formatted.append(numeric.charAt(i));
            }
            
            if (!formatted.toString().equals(newText)) {
                int pos = cpfField.getCaretPosition();
                cpfField.setText(formatted.toString());
                cpfField.positionCaret(Math.min(pos, formatted.length()));
            }
        });
		
		PasswordField senhaField = new PasswordField();
		senhaField.setPromptText("Senha");
		senhaField.getStyleClass().add("input");
		
		Button loginButton = new Button("Entrar");
		loginButton.setId("login-botao");
		
		VBox layout = new VBox(20, titulo, cpfField, senhaField, loginButton);
		VBox.setMargin(loginButton, new Insets(40,0,0,0));
		layout.setAlignment(Pos.CENTER);
		layout.setPadding(new Insets(40));
		layout.setId("login-layout");
		
		cpfField.setMaxWidth(705);
		senhaField.setMaxWidth(705);
        
		StackPane root = new StackPane();
		root.getChildren().addAll(layout, blob1, blob2, blob3);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("LoginApp.css").toExternalForm());
		
		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.10));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.10));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.03));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());

		StackPane.setAlignment(blob1, Pos.TOP_LEFT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(-0.05));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));

		StackPane.setAlignment(blob2, Pos.BOTTOM_RIGHT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.05));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.09));
		
		StackPane.setAlignment(blob3, Pos.CENTER_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.2));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(-0.35));
		
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitHint("");
		primaryStage.show();
		
		UsuarioController usuarioController = new UsuarioController();

		loginButton.setOnAction(e -> {
			String cpf = cpfField.getText().replaceAll("[^\\d]", "");
            String senha = senhaField.getText();

		    Usuario usuarioLogado = usuarioController.login(cpf, senha);

		    if (usuarioLogado != null) {
		        Stage homeStage = new Stage();
		        if (usuarioLogado.getFuncao() == Funcao.RH) {
		        	new HomeRH(usuarioLogado).start(homeStage);		   
		        } else if (usuarioLogado.getFuncao() == Funcao.GESTOR_AREA) {
		            new HomeGestorArea(usuarioLogado).start(homeStage);
		        } else if (usuarioLogado.getFuncao() == Funcao.GESTOR_GERAL) {
		            new HomeGestorGeral(usuarioLogado).start(homeStage);
		        }
		        primaryStage.close();
		    }
		    	else {
		        // Login falhou
		        Alert alert = new Alert(Alert.AlertType.ERROR);
		        alert.setTitle("Erro de Login");
		        alert.setHeaderText(null);
		        alert.setContentText("CPF ou senha inválidos!");
		        alert.showAndWait();
		    }
		});
		
	}
}
	

