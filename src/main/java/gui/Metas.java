package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Colaborador;
import model.Pdi;
import model.Status;
import model.Usuario;
import util.PDFExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import util.POIExcelExporter;

public class Metas extends Application {

    private TableView<Pdi> tabela;
    private ObservableList<Pdi> dados;
    private Usuario logado;
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private PdiDAO pdiDAO = new PdiDAO();


    public Metas(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }
	
	public void start(Stage metasStage) {
		setupBotaoExportar();
        setupBotaoExportarExcel();
		
		// cria barra lateral
	    BarraLateralRH barra = new BarraLateralRH(logado);
	    
	    VBox coluna1 = new VBox();
	    coluna1.setId("coluna1");

	    Text titulo = new Text("Gerenciar Metas");
	    titulo.setId("titulo");
	    VBox.setMargin(titulo, new Insets(0, 0, 25, 0));
	    
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
	   
	    tabela = new TableView<>();
	    carregarTabela();
	    VBox.setVgrow(tabela, Priority.ALWAYS);

        
        TableColumn<Pdi, String> colNome = new TableColumn<>("Colaborador");
        colNome.setCellValueFactory(c -> {
            int colaboradorId = c.getValue().getColaborador_id();
            Colaborador colaborador = colaboradorDAO.getColaboradorById(colaboradorId); // método que você cria
            String nome = (colaborador != null) ? colaborador.getNome() : "Desconhecido";
            return new javafx.beans.property.SimpleStringProperty(nome);
        });
        
        TableColumn<Pdi, String> colSetor = new TableColumn<>("Setor");
        colSetor.setCellValueFactory(c -> {
            int colaboradorId = c.getValue().getColaborador_id();
            Colaborador colaborador = colaboradorDAO.getColaboradorById(colaboradorId);
            String setor = (colaborador != null) ? colaborador.getSetor() : "Desconhecido";
            return new javafx.beans.property.SimpleStringProperty(setor);
        });

        TableColumn<Pdi, String> colObjetivo = new TableColumn<>("Objetivo");
        colObjetivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getObjetivo()));

        TableColumn<Pdi, String> colPrazo = new TableColumn<>("Prazo");
        colPrazo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrazo().toString()));

        TableColumn<Pdi, Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        colStatus.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (status) {
                        case NAO_INICIADO -> {
                            setText("Não Iniciado");
                            setStyle("-fx-text-fill: gray;");
                        }
                        case EM_ANDAMENTO -> {
                            setText("Em Andamento");
                            setStyle("-fx-text-fill: orange;");
                        }
                        case CONCLUIDO -> {
                            setText("Concluído");
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        }
                        case ATRASADO -> {
                            setText("Atrasado");
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        }
                    }
                    setAlignment(Pos.CENTER); // centraliza texto
                }
            }
        });
        
        
        
        TableColumn<Pdi, Void> colAcoes = new TableColumn<>("Ações");
        
        colAcoes.setCellFactory(tc -> new javafx.scene.control.TableCell<>() {
            private final Button btnDelete = new Button("❌");

            {
                btnDelete.setOnAction(e -> {
                    Pdi pdi = getTableView().getItems().get(getIndex());

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Deseja excluir a meta \"" + pdi.getObjetivo() + "\"?",
                        ButtonType.YES, ButtonType.NO);
                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            pdiDAO.delete(pdi); 
                            carregarTabela();
                        }
                    });
                });
                btnDelete.setId("btnDelete");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });
        
	    HBox tabelaContainer = new HBox(tabela);
	    tabelaContainer.setAlignment(Pos.CENTER);
	    VBox.setVgrow(tabelaContainer, Priority.ALWAYS);

        
	    tabela.getColumns().addAll(colNome, colSetor, colObjetivo, colPrazo, colStatus, colAcoes);
	    
	    //Botão exportar pdf
        HBox headerBox = new HBox(titulo, btnExportar, btnExportarExcel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setSpacing(20);
	    
        Text tituloCadastrar = new Text("Cadastrar Nova Meta");
        tituloCadastrar.setId("tituloCadastrar");
        
        HBox boxTitulo = new HBox(tituloCadastrar);
        boxTitulo.setAlignment(Pos.CENTER_LEFT);
        boxTitulo.setPadding(new Insets(10, 0, 5, 0));
        
        GridPane cadastrar = new GridPane();
		cadastrar.setHgap(40); 
		cadastrar.setVgap(30); 
		cadastrar.setAlignment(Pos.CENTER);
		cadastrar.setId("cadastro-layout");
		cadastrar.setPadding(new Insets(20, 80, 20, 80));
        
        ComboBox<Colaborador> cbColaborador = new ComboBox<>();
        cbColaborador.setEditable(true);
        cbColaborador.getItems().addAll(colaboradorDAO.listAll());
        cbColaborador.setPromptText("Selecione um colaborador");
        cbColaborador.setPrefWidth(250);
        cbColaborador.getEditor().getStyleClass().add("input");
        cbColaborador.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(cbColaborador, Priority.ALWAYS);
		
		ObservableList<Colaborador> todosColaboradores = FXCollections.observableArrayList(colaboradorDAO.listAll());
		cbColaborador.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
		    if (newValue == null || newValue.isEmpty()) {
		        // apenas limpa a seleção e não altera os itens
		        cbColaborador.getSelectionModel().clearSelection();
		        cbColaborador.hide(); // evita mostrar dropdown indesejado
		    } else {
		        ObservableList<Colaborador> filtrados = todosColaboradores.filtered(c -> 
		            c.getNome().toLowerCase().contains(newValue.toLowerCase())
		        );
		        cbColaborador.setItems(filtrados);
		        cbColaborador.show();
		    }
		});
		
		cbColaborador.setCellFactory(lv -> new javafx.scene.control.ListCell<Colaborador>() {
		    @Override
		    protected void updateItem(Colaborador item, boolean empty) {
		        super.updateItem(item, empty);
		        setText((empty || item == null) ? null : item.getNome());
		    }
		});

		cbColaborador.setButtonCell(new javafx.scene.control.ListCell<Colaborador>() {
		    @Override
		    protected void updateItem(Colaborador item, boolean empty) {
		        super.updateItem(item, empty);
		        setText((empty || item == null) ? null : item.getNome());
		    }
		});
		
		cbColaborador.setConverter(new StringConverter<Colaborador>() {
		    @Override
		    public String toString(Colaborador colaborador) {
		        return (colaborador == null) ? "" : colaborador.getNome();
		    }

		    @Override
		    public Colaborador fromString(String string) {
		        // Procura pelo colaborador correspondente ao nome digitado
		        return cbColaborador.getItems()
		                .stream()
		                .filter(c -> c.getNome().equalsIgnoreCase(string))
		                .findFirst()
		                .orElse(null);
		    }
		});

        
        TextField tfObjetivo = new TextField();
        tfObjetivo.setPromptText("Objetivo");
        tfObjetivo.setPrefWidth(250);
        tfObjetivo.getStyleClass().add("input");
        tfObjetivo.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(tfObjetivo, Priority.ALWAYS);


        
        DatePicker dpPrazo = new DatePicker();
        dpPrazo.getStyleClass().add("input");
        dpPrazo.setPromptText("Prazo");
        dpPrazo.setPrefWidth(250);
        dpPrazo.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(dpPrazo, Priority.ALWAYS);



        
        ComboBox<Status> cbStatus = new ComboBox<>();
        cbStatus.getItems().setAll(Status.values());
        cbStatus.getStyleClass().add("input");
        cbStatus.setPromptText("Status");
        cbStatus.setPrefWidth(250);
        cbStatus.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(cbStatus, Priority.ALWAYS);
		
		cbStatus.setCellFactory(cb -> new ListCell<>() {
		    @Override
		    protected void updateItem(Status item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) {
		            setText(null);
		        } else {
		            switch(item) {
		                case NAO_INICIADO -> setText("Não Iniciado");
		                case EM_ANDAMENTO -> setText("Em Andamento");
		                case CONCLUIDO -> setText("Concluído");
		                case ATRASADO -> setText("Atrasado");
		            }
		        }
		    }
		});
		cbStatus.setButtonCell(cbStatus.getCellFactory().call(null));

        Button btnSalvar = new Button("Cadastrar");
        btnSalvar.getStyleClass().add("botao-cadastro");
        
        btnSalvar.setOnAction(e -> {
            Colaborador colab = cbColaborador.getValue();
            if (colab == null || tfObjetivo.getText().isEmpty() || dpPrazo.getValue() == null || cbStatus.getValue() == null) {
                mostrarAlerta("Preencha todos os campos!");
                return;
            }

            Pdi novo = new Pdi(0, colab.getId(), tfObjetivo.getText(), dpPrazo.getValue(), cbStatus.getValue());
            pdiDAO.insert(novo);
            carregarTabela();
            limparCampos(tfObjetivo, dpPrazo, cbColaborador, cbStatus);
        });
        
        
        HBox boxBotao = new HBox(btnSalvar);
        boxBotao.setAlignment(Pos.CENTER);


        cadastrar.add(cbColaborador, 0, 0);
        cadastrar.add(tfObjetivo, 1, 0);
        cadastrar.add(dpPrazo, 0, 1);
        cadastrar.add(cbStatus, 1, 1);
        cadastrar.add(boxBotao, 0, 2, 2, 1); 
        
        coluna1.getChildren().addAll(titulo, tabelaContainer, headerBox, boxTitulo, cadastrar, blob1, blob2, blob3);
        
        
	    // layout raiz
	    HBox root = new HBox();
	    root.getChildren().addAll(barra, coluna1);
	    root.setStyle("-fx-background-color: #1E1E1E");

	    // proporção
	    coluna1.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
	    barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
	    
	    colNome.prefWidthProperty().bind(tabela.widthProperty().multiply(0.2));  
	    colSetor.prefWidthProperty().bind(tabela.widthProperty().multiply(0.15));  
	    colObjetivo.prefWidthProperty().bind(tabela.widthProperty().multiply(0.3));  
	    colPrazo.prefWidthProperty().bind(tabela.widthProperty().multiply(0.1));  
	    colStatus.prefWidthProperty().bind(tabela.widthProperty().multiply(0.1));
	    colAcoes.prefWidthProperty().bind(tabela.widthProperty().multiply(0.1));

	    // cena e estilo
	    Scene scene = new Scene(root, 1000, 600);
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
	    scene.getStylesheets().add(getClass().getResource("/gui/Metas.css").toExternalForm());
	    
	    blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.07));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.05));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.02));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());

		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.72));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.015));
		blob1.setManaged(false);

		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.2));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(1.025));
		blob2.setManaged(false);

		StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.6));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.012));
		blob3.setManaged(false);
	    
	    metasStage.setScene(scene);
	    metasStage.setFullScreen(true);
	    metasStage.setFullScreenExitHint("");
	    metasStage.setTitle("Gerenciamento de Metas (PDIs)");
	    metasStage.show();

	}
	
    private void carregarTabela() {
        dados = FXCollections.observableArrayList(pdiDAO.listAll());
        tabela.setItems(dados);
    }
    
    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.showAndWait();
    }
    
    private void limparCampos(TextField tfObjetivo, DatePicker dpPrazo, ComboBox<Colaborador> cbColaborador, ComboBox<Status> cbStatus) {
    	tfObjetivo.clear();
        dpPrazo.setValue(null);
        cbColaborador.setValue(null);
        cbColaborador.getEditor().clear();
        cbStatus.setValue(null);
    }
    
    //Botão exportar pdf
    Button btnExportar = new Button("Exportar PDF");
    private void setupBotaoExportar() {
    	btnExportar.getStyleClass().add("botao-exportar");
    	
    	btnExportar.setOnAction(e -> {
    		javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
    		fileChooser.setTitle("Salvar relatório PDF");
    		
    		
    		String fileName = "relatorio_metas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".pdf";
    		fileChooser.setInitialFileName(fileName);
    		
    		File arquivoSelecionado = fileChooser.showSaveDialog(tabela.getScene().getWindow());
            if (arquivoSelecionado == null) {
                System.out.println("Operação cancelada pelo usuário.");
                return;
            }
            
            boolean sucesso = PDFExporter.exportarPDIsParaPDF(dados, arquivoSelecionado.getAbsolutePath());
    	});
    }

    Button btnExportarExcel = new Button("Exportar Excel");

    private void setupBotaoExportarExcel() {
        btnExportarExcel.getStyleClass().add("botao-exportar");

        btnExportarExcel.setOnAction(e -> {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Escolha onde salvar o arquivo Excel");

            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("Arquivos Excel (*.xlsx)", "*.xlsx")
            );

            String fileName = "metas_" + LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + ".xlsx";
            fileChooser.setInitialFileName(fileName);

            File pastaDownloads = new File(System.getProperty("user.home"), "Downloads");
            if (pastaDownloads.exists()) {
                fileChooser.setInitialDirectory(pastaDownloads);
            }

            File arquivoSelecionado = fileChooser.showSaveDialog(tabela.getScene().getWindow());
            if (arquivoSelecionado == null) {
                System.out.println("Operação cancelada pelo usuário.");
                return;
            }

            if (!arquivoSelecionado.getName().toLowerCase().endsWith(".xlsx")) {
                arquivoSelecionado = new File(arquivoSelecionado.getAbsolutePath() + ".xlsx");
            }

            POIExcelExporter.exportarParaExcel(arquivoSelecionado, dados);
        });
    }
    
}	
