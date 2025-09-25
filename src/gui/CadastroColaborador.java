package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CadastroColaborador extends Application {
	
	@Override
	public void start(Stage stage) {
		
		Stage secondStage = new Stage();		
		
		Text titulocadastro = new Text("Cadastrar colaboradores");
		titulocadastro.setId("titulo-cadastro");
		
		Text infosPessoais = new Text("Informações Pessoais");
		infosPessoais.getStyleClass().add("infos");
		
		TextField nome = new TextField();
		nome.setPromptText("Nome completo");
		nome.getStyleClass().add("input");
		
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
		
		DatePicker dataNasci = new DatePicker();
		dataNasci.setPromptText("Data de Nascimento");
		dataNasci.getStyleClass().add("input");
		
		Text infosProfissionais = new Text("Informações Profissionais");
		infosProfissionais.getStyleClass().add("infos");
		
		TextField cargo = new TextField();
		cargo.setPromptText("Cargo");
		cargo.getStyleClass().add("input");
		
		ComboBox<String> setor = new ComboBox<>();
		setor.getItems().addAll(
		    "Desenvolvimento",
		    "Produto",
		    "Vendas e Marketing",
		    "Suporte",
		    "DevOps",
		    "Financeiro",
		    "Pesquisa e Inovação",
		    "Gestão de Projetos"
		);
		setor.setPromptText("Setor");
		setor.getStyleClass().add("input");
		
		TextField experiencia = new TextField();
		experiencia.setPromptText("Experiência");
		experiencia.getStyleClass().add("input");
		
		TextField obs = new TextField();
		obs.setPromptText("Observações");
		obs.getStyleClass().add("input");
		
		Button salvarButton = new Button("Salvar");
		salvarButton.getStyleClass().add("botao-cadastro");
		
		Button cancelarButton = new Button("Cancelar");
		cancelarButton.getStyleClass().add("botao-cadastro");
		
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);
		
		GridPane grid = new GridPane();
		grid.setHgap(20); 
		grid.setVgap(40); 
		grid.setAlignment(Pos.CENTER);
		grid.setId("cadastro-layout");
		grid.setPadding(new Insets(20, 80, 20, 80));

		grid.setMaxWidth(Double.MAX_VALUE);
		
		HBox tituloBox = new HBox(titulocadastro);
		tituloBox.setAlignment(Pos.CENTER);
		grid.add(tituloBox, 0, 0, 2, 1);
		
		grid.add(infosPessoais, 0, 1, 1, 1);
		
		grid.add(nome, 0, 2, 2, 1);
		
		grid.add(dataNasci, 0, 3, 1, 1);
		grid.add(cpf, 1, 3, 1, 1);
		
		grid.add(infosProfissionais, 0, 4, 2, 1);
		
		grid.add(cargo, 0, 5, 1, 1);
		grid.add(setor, 1, 5, 1, 1);
		
		grid.add(experiencia, 0, 6, 1, 1);
		
		grid.add(obs, 1, 6, 1, 1);
		
		grid.getColumnConstraints().addAll(col1, col2);
		
		HBox botoesBox = new HBox(20);
		botoesBox.getChildren().addAll(salvarButton, cancelarButton);
		botoesBox.setAlignment(Pos.CENTER);
		grid.add(botoesBox, 0, 7, 2, 1);
		
		dataNasci.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(dataNasci, Priority.ALWAYS);
		
		GridPane.setHgrow(setor, Priority.ALWAYS);
		setor.setMaxWidth(Double.MAX_VALUE);
		
		BorderPane root = new BorderPane();
		root.setCenter(grid);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("Cadastro.css").toExternalForm());
		
		
		secondStage.setScene(scene);
		secondStage.setFullScreen(true);
		secondStage.setFullScreenExitHint("");
		secondStage.show();
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}

