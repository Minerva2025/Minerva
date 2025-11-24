package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
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
        setor.setVisible(false);
        setor.setManaged(false);
        setor.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(setor, Priority.ALWAYS);

        TextField obs = new TextField();
        obs.setPromptText("Observações");
        obs.getStyleClass().add("input");

        Button salvarButton = new Button("Salvar");
        salvarButton.getStyleClass().add("botao-cadastro");

        Button cancelarButton = new Button("Cancelar");
        cancelarButton.getStyleClass().add("botao-cadastro");

        HBox botoesBox = new HBox(20);
        botoesBox.getChildren().addAll(salvarButton, cancelarButton);
        botoesBox.setAlignment(Pos.CENTER);
        botoesBox.setPadding(new Insets(20, 0, 0, 0));

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
        grid.add(senha, 0, 4, 2, 1);

        grid.add(infosprofissionais, 0, 5, 1, 1);
        grid.add(experiencia, 0, 6, 1, 1);
        grid.add(funcao, 1, 6, 1, 1);
        grid.add(obs, 0, 7, 2, 1);

        grid.getColumnConstraints().addAll(col1, col2);

        funcao.valueProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == Funcao.GESTOR_AREA) {
                setor.setVisible(true);
                setor.setManaged(true);
                grid.getChildren().remove(obs);
                grid.add(obs, 0, 7, 1, 1);
                grid.add(setor, 1, 7, 1, 1);
                GridPane.setHgrow(setor, Priority.ALWAYS);
                setor.setMaxWidth(Double.MAX_VALUE);
            } else {
                setor.setVisible(false);
                setor.setManaged(false);
                grid.getChildren().remove(setor);
                grid.getChildren().remove(obs);
                grid.add(obs, 0, 7, 2, 1);
            }
        });

        dataNasci.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(dataNasci, Priority.ALWAYS);
        GridPane.setHgrow(funcao, Priority.ALWAYS);
        funcao.setMaxWidth(Double.MAX_VALUE);

        VBox mainContainer = new VBox();
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(20));
        mainContainer.getChildren().addAll(grid, botoesBox);

        ScrollPane scroll = new ScrollPane(mainContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        StackPane stack = new StackPane();
        stack.getChildren().addAll(scroll, blob1, blob2, blob3);

        BorderPane root = new BorderPane();
        root.setId("root-cadastro-usuarios");
        root.setCenter(stack);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/gui/CadastroUsuario.css").toExternalForm());

        blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
        blob1.radiusYProperty().bind(blob1.radiusXProperty());

        blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
        blob2.radiusYProperty().bind(blob2.radiusXProperty());

        blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
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

        scene.widthProperty().addListener((obss, oldVal, newVal) -> updateResponsiveStyles(scene));
        scene.heightProperty().addListener((obss, oldVal, newVal) -> updateResponsiveStyles(scene));

        cadastroUStage.setScene(scene);
        cadastroUStage.setFullScreen(true);
        cadastroUStage.setFullScreenExitHint("");
        cadastroUStage.show();

        updateResponsiveStyles(scene);

        salvarButton.setOnAction(e -> {
            try {
                String nomeStr = nome.getText();
                String cpfStr = cpf.getText().replaceAll("[^\\d]", "");
                String senhaStr = senha.getText();
                LocalDate dataNascimento = dataNasci.getValue();
                Funcao funcaoSelecionada = funcao.getValue();
                String experienciaStr = experiencia.getText();
                String obsStr = obs.getText();
                String setorStr = setor.isVisible() ? setor.getValue() : null;

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
                        obsStr,
                        setorStr
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

    public static void main(String[] args) {
        launch(args);
    }
}
