package gui;

import javafx.application.Application;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Colaborador;
import model.Funcao;
import model.Pdi;
import model.Status;
import model.Usuario;
import util.PDFExporter;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import gui.BarraLateralRH;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class MetasGG extends Application {

    private TableView<Pdi> tabela;
    private ObservableList<Pdi> dados;
    private Usuario logado;
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private PdiDAO pdiDAO = new PdiDAO();


    public MetasGG(Usuario usuarioLogado) {
        this.logado = usuarioLogado;
    }
	
	public void start(Stage metasggStage) {
		setupBotaoExportar();

		// cria barra lateral
	    BarraLateralGG barra = new BarraLateralGG(logado);
	    
	    VBox coluna1 = new VBox();
	    coluna1.setId("coluna1");

	    Text titulo = new Text("Gerenciar Metas");
	    titulo.setId("titulo");
	    VBox.setMargin(titulo, new Insets(0, 0, 25, 0));
	    titulo.setTextAlignment(TextAlignment.CENTER);
	    titulo.setStyle("-fx-font-size: 32px; -fx-fill: white;");
	   
	    coluna1.setAlignment(Pos.TOP_CENTER);
	    VBox.setMargin(titulo, new Insets(0, 0, 25, 0));
	    
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

	    tabela.setMaxHeight(500);
	    
	    tabela.getColumns().addAll(colNome, colSetor, colObjetivo, colPrazo, colStatus, colAcoes);
	    
	    //Botão exportar pdf
	    HBox headerBox = new HBox(titulo, btnExportar);
	    headerBox.setAlignment(Pos.CENTER);
        
        coluna1.getChildren().addAll(titulo, tabelaContainer, headerBox);
        
        
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
	    scene.getStylesheets().add(getClass().getResource("Metas.css").toExternalForm());
	    scene.getStylesheets().add(getClass().getResource("BarraLateral.css").toExternalForm());
	    
	    metasggStage.setScene(scene);
	    metasggStage.setFullScreen(true);
	    metasggStage.setFullScreenExitHint("");
	    metasggStage.setTitle("Gerenciamento de Metas (PDIs)");
	    metasggStage.show();

	}
	
    private void carregarTabela() {
        dados = FXCollections.observableArrayList(pdiDAO.listAll());
        tabela.setItems(dados);
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
    		
    		File file = fileChooser.showSaveDialog(tabela.getScene().getWindow());
    		
    		boolean sucesso = PDFExporter.exportarPDIsParaPDF(dados, file.getAbsolutePath());
    	});
    }
}	
