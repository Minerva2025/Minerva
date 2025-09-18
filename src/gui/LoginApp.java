package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginApp extends Application{
	
	@Override
	public void start(Stage primaryStage) {
		
		Text titulo = new Text("LOGIN");
		titulo.setId("titulo-text");
		
		TextField cpfField = new TextField();
		cpfField.setPromptText("CPF");
		cpfField.getStyleClass().add("input");
		
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
        
		
		Scene scene = new Scene(layout);
		scene.getStylesheets().add(getClass().getResource("LoginApp.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.setFullScreen(true);
		primaryStage.setFullScreenExitHint("");
		primaryStage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}
	

