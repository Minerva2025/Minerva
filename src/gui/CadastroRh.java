package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CadastroRh extends Application {
	
	@Override
	public void start(Stage stage) {
		
		Stage thirdStage = new Stage();
		
		Text titulocadastrorh = new Text("Cadastrar colaboradores RH");
		titulocadastrorh.setId("titulo-cadastro-rh");
		
		Text infosPessoais = new Text("Informações Pessoais");
		infosPessoais.getStyleClass().add("infos");
		
		TextField nome = new TextField();
		nome.setPromptText("Nome completo");
		nome.getStyleClass().add("input");
		
		DatePicker dataNasci = new DatePicker();
		dataNasci.setPromptText("Data de Nascimento");
		dataNasci.getStyleClass().add("input");
		
		TextField cpf = new TextField();
		cpf.setPromptText("CPF");
		cpf.getStyleClass().add("input");
		
		cpf.textProperty().addListener((obs, oldText, newText) -> {
            String numeric = newText.replaceAll("[^\\d]", "");
            if (numeric.length() > 11) numeric = numeric.substring(0, 11);

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < numeric.length(); i++) {
                if (i == 3 || i == 6) formatted.append('.');
                if (i == 9) formatted.append('-');
                formatted.append(numeric.charAt(i));
            }

            if (!formatted.toString().equals(newText)) {
                int pos = cpf.getCaretPosition();
                cpf.setText(formatted.toString());
                cpf.positionCaret(Math.min(pos, formatted.length()));
            }
        });
		
		Text infosprofissionais = new Text("Informações Profissionais");
		infosprofissionais.getStyleClass().add("infos");
		
		TextField experiencia = new TextField();
		experiencia.setPromptText("Experiência");
		experiencia.getStyleClass().add("input");
		
		TextField obs = new TextField();
		obs.setPromptText("Observações");
		obs.getStyleClass().add("input");
		
		Button salvarButton = new Button("Salvar");
		salvarButton.getStyleClass().add("botoes");
		
		Button cancelarButton = new Button("Cancelar");
		cancelarButton.getStyleClass().add("botoes");
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);
		
		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(40);
		grid.setAlignment(Pos.CENTER);
		grid.setId("cadastroRh-layout");
		grid.setPadding(new Insets(20, 80, 20, 80));
		grid.setMaxWidth(Double.MAX_VALUE);
		
		HBox tituloBox = new HBox(titulocadastrorh);
		tituloBox.setAlignment(Pos.CENTER);
		grid.add(tituloBox, 0, 0, 2, 1);
		
		grid.add(infosPessoais, 0, 1, 1, 1);
		
		grid.add(nome, 0, 2, 2, 1);
		
		grid.add(dataNasci, 0, 3, 1, 1);
		grid.add(cpf, 1, 3, 1, 1);
		
		grid.add(infosprofissionais, 0, 4, 1, 1);
		
		grid.add(experiencia, 0, 5, 2, 1);
		
		grid.add(obs, 0, 6, 2, 1);
		
		grid.getColumnConstraints().addAll(col1,col2);
		
		HBox botoesBox = new HBox(20);
		botoesBox.getChildren().addAll(salvarButton, cancelarButton);
		botoesBox.setAlignment(Pos.CENTER);
		grid.add(botoesBox, 0, 7, 2, 1);
		
		dataNasci.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(dataNasci, Priority.ALWAYS);
		
		BorderPane root = new BorderPane();
		root.setCenter(grid);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("CadastroRh.css").toExternalForm());
		
		
		thirdStage.setScene(scene);
		thirdStage.setFullScreen(true);
		thirdStage.setFullScreenExitHint("");
		thirdStage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}


		
}
