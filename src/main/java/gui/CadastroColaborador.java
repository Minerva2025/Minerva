package gui;

import controller.UsuarioController;
import dao.ColaboradorDAO;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Colaborador;
import model.Usuario;

public class CadastroColaborador extends Application {
	
    private Usuario logado;
    private UsuarioController usuarioController = new UsuarioController();
	
	public CadastroColaborador(Usuario logado) {
    	this.logado = logado;
	}

	@Override
	public void start(Stage cadastroCStage) {
				
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
			"Marketing",
			"Suporte",
			"Financeiro", 
			"Pesquisa e Inovação"
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
		
		StackPane stack = new StackPane();
		stack.getChildren().addAll(grid, blob1, blob2, blob3);
		
		BorderPane root = new BorderPane();
		root.setId("root-cadastro-colaborador");
		root.setCenter(stack);
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/Cadastro.css").toExternalForm());
		
		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob1.radiusYProperty().bind(blob1.radiusXProperty());
		
		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());
		
		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());
		
		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(-0.2));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.09));
		
		StackPane.setAlignment(blob2, Pos.TOP_RIGHT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(-0.1));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.1));
		
		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(-0.05));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.1));
		
		scene.widthProperty().addListener((obss, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});
		
		scene.heightProperty().addListener((obss, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});
	
		cadastroCStage.setScene(scene);
		cadastroCStage.setFullScreen(true);
		cadastroCStage.setFullScreenExitHint("");
		cadastroCStage.show();
		
		updateResponsiveStyles(scene);
		
		salvarButton.setOnAction(e -> {
		    try {
		        Colaborador colaborador = new Colaborador(
		            0, // id será gerado no banco
		            nome.getText(),
		            cpf.getText().replaceAll("[^\\d]", ""),
		            dataNasci.getValue(),
		            cargo.getText(),
		            setor.getValue(),
		            experiencia.getText(),
		            obs.getText()
		        );

		        ColaboradorDAO dao = new ColaboradorDAO();
		        dao.insert(colaborador);

		        cadastroCStage.close();

		    } catch (Exception ex) {
		        ex.printStackTrace();
		        System.out.println(" Erro ao salvar colaborador: " + ex.getMessage());
		    }
		});

		cancelarButton.setOnAction(e -> cadastroCStage.close());

	}
	
	private void updateResponsiveStyles(Scene scene) {
		double width = scene.getWidth();
		double height = scene.getHeight();
		
		scene.getRoot().getStyleClass().removeAll("small-screen", "medium-screen", "large-screen", "extra-large-screen", "mobile-landscape");
		
		if (width < 768) {
			scene.getRoot().getStyleClass().add("small-screen");
			if (width > height) {
				scene.getRoot().getStyleClass().add("mobile-landscape");
			}
		} else if (width < 1024) {
			scene.getRoot().getStyleClass().add("medium-screen");
		} else if (width < 1440) {
			scene.getRoot().getStyleClass().add("large-screen");
		} else {
			scene.getRoot().getStyleClass().add("extra-large-screen");
		}
	}
	
	public static void main (String[]args) {
		launch(args);
	}
}