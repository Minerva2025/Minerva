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
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}
	

