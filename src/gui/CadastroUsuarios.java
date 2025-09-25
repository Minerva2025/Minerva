package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Funcao;
import model.Usuario;
import controller.UsuarioController;
import java.time.LocalDate;


public class CadastroUsuarios extends Application {
	
    private Usuario logado;
    private UsuarioController usuarioController = new UsuarioController();

    public CadastroUsuarios(Usuario logado) { 
    	this.logado = logado;
    }
	
	@Override
	public void start(Stage cadastroUStage) {
				
		Text titulocadastrorh = new Text("Cadastrar Usuários");
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
		
		PasswordField senha = new PasswordField();
		senha.setPromptText("Senha");
		senha.getStyleClass().add("input");
		
		Text infosprofissionais = new Text("Informações Profissionais");
		infosprofissionais.getStyleClass().add("infos");
		
		
		TextField experiencia = new TextField();
		experiencia.setPromptText("Experiência");
		experiencia.getStyleClass().add("input");
		
		
		ComboBox<Funcao> funcao = new ComboBox<>();
		funcao.getItems().addAll(Funcao.values());
		funcao.setPromptText("Função");
		funcao.getStyleClass().add("input");
		
		funcao.setCellFactory(cb -> new ListCell<>() {
		    @Override
		    protected void updateItem(Funcao item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) {
		            setText(null);
		        } else {
		            switch(item) {
		                case RH -> setText("RH");
		                case GESTOR_AREA -> setText("Gestor de Área");
		                case GESTOR_GERAL -> setText("Gestor Geral");
		            }
		        }
		    }
		});
		funcao.setButtonCell(funcao.getCellFactory().call(null));

		
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
		grid.add(tituloBox, 0, 0,2, 1);
		
		grid.add(infosPessoais, 0, 1, 1, 1);
		
		grid.add(nome, 0, 2, 2, 1);
		
		grid.add(dataNasci, 0, 3, 1, 1);
		grid.add(cpf, 1, 3, 1, 1);
		
		grid.add(senha, 0, 4, 2, 1);
		
		grid.add(infosprofissionais, 0, 5, 1, 1);
		
		grid.add(experiencia, 0, 6, 1, 1);
		
		grid.add(funcao, 1, 6, 1, 1);
		
		grid.add(obs, 0, 7, 2, 1);
		
		grid.getColumnConstraints().addAll(col1,col2);
		
		HBox botoesBox = new HBox(20);
		botoesBox.getChildren().addAll(salvarButton, cancelarButton);
		botoesBox.setAlignment(Pos.CENTER);
		grid.add(botoesBox, 0, 8, 2, 1);
		
		dataNasci.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(dataNasci, Priority.ALWAYS);
		
		GridPane.setHgrow(funcao, Priority.ALWAYS);
		funcao.setMaxWidth(Double.MAX_VALUE);
		
		BorderPane root = new BorderPane();
		root.setCenter(grid);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("CadastroUsuario.css").toExternalForm());
		
		
		cadastroUStage.setScene(scene);
		cadastroUStage.setFullScreen(true);
		cadastroUStage.setFullScreenExitHint("");
		cadastroUStage.show();
		
		salvarButton.setOnAction(e -> {
		    try {
		        String nomeStr = nome.getText();
		        String cpfStr = cpf.getText().replaceAll("[^\\d]", "");
		        String senhaStr = senha.getText();
		        LocalDate dataNascimento = dataNasci.getValue();
		        Funcao funcaoSelecionada = funcao.getValue();
		        String experienciaStr = experiencia.getText();
		        String obsStr = obs.getText();

		        if (nomeStr.isEmpty() || cpfStr.isEmpty() || senhaStr.isEmpty() || dataNascimento == null || funcaoSelecionada == null) {
		            Alert alert = new Alert(Alert.AlertType.ERROR, "Preencha todos os campos obrigatórios!");
		            alert.showAndWait();
		            return;
		        }
				
		        Usuario novoUsuario = new Usuario(
		                nomeStr,
		                cpfStr,
		                senhaStr,
		                dataNascimento,
		                funcaoSelecionada,
		                experienciaStr,
		                obsStr
		        );
		        
		        usuarioController.criarUsuario(logado, novoUsuario);
		        
		        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Usuário cadastrado com sucesso!");
		        alert.showAndWait();
		        cadastroUStage.close();
		        
		    } catch (Exception ex) {
		        ex.printStackTrace();
		        Alert alert = new Alert(Alert.AlertType.ERROR, "Erro ao cadastrar usuário!");
		        alert.showAndWait();
		    }
		});
		
		cancelarButton.setOnAction(e -> cadastroUStage.close());

	}
	
	public static void main (String[]args) {
		launch(args);
	}
}


