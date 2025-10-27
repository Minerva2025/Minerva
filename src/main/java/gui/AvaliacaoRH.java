package gui;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.StringConverter;
import model.Colaborador;
import model.Pdi;
import model.Usuario;
import model.Status;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import dao.ColaboradorDAO;
import dao.PdiDAO;
import java.util.List;


public class AvaliacaoRH extends Application {

	private Usuario logado;
	private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
	private PdiDAO pdiDAO = new PdiDAO();
	private File arquivoSelecionado = null;

	public AvaliacaoRH(Usuario usuarioLogado) {
		this.logado = usuarioLogado;
	}

	@Override
	public void start(Stage avaliacaorhStage) {

		GridPane grid = new GridPane();
		grid.setHgap(20);
		grid.setVgap(30);
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setId("avaliacao-rh");
		grid.setPadding(new Insets(5, 80, 5, 80));
		grid.setMaxWidth(Double.MAX_VALUE);

		BarraLateralRH barra = new BarraLateralRH(logado);

		Text titulo = new Text("Avaliações");
		titulo.setId("titulo");

		GridPane grid2 = new GridPane();
		grid2.setPadding(new Insets(20, 20, 20, 20));
		grid2.setHgap(20);
		grid2.setVgap(40);
		grid2.setAlignment(Pos.CENTER);
		grid2.setMaxWidth(Double.MAX_VALUE);
		GridPane.setMargin(grid, new Insets(0, 0, 0, 20));
		grid.prefWidthProperty().bind(grid2.widthProperty());

		ComboBox<Colaborador> colaborador = new ComboBox<>();
		colaborador.setEditable(true);
		colaborador.getItems().addAll(colaboradorDAO.listAll());
		colaborador.setPromptText("Selecione um colaborador");
		colaborador.setPrefWidth(500);
		colaborador.getEditor().getStyleClass().add("input");
		colaborador.setMaxWidth(Double.MAX_VALUE);
		colaborador.setPromptText("Colaborador");
		colaborador.getStyleClass().add("combo-box");
		GridPane.setHgrow(colaborador, Priority.ALWAYS);

		ObservableList<Colaborador> todosColaboradores = FXCollections.observableArrayList(colaboradorDAO.listAll());
		colaborador.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty()) {
				// apenas limpa a seleção e não altera os itens
				colaborador.getSelectionModel().clearSelection();
				colaborador.hide(); // evita mostrar dropdown indesejado
			} else {
				ObservableList<Colaborador> filtrados = todosColaboradores
						.filtered(c -> c.getNome().toLowerCase().contains(newValue.toLowerCase()));
				colaborador.setItems(filtrados);
				colaborador.show();
			}
		});

		colaborador.setCellFactory(lv -> new javafx.scene.control.ListCell<Colaborador>() {
			@Override
			protected void updateItem(Colaborador item, boolean empty) {
				super.updateItem(item, empty);
				setText((empty || item == null) ? null : item.getNome());
			}
		});

		colaborador.setButtonCell(new javafx.scene.control.ListCell<Colaborador>() {
			@Override
			protected void updateItem(Colaborador item, boolean empty) {
				super.updateItem(item, empty);
				setText((empty || item == null) ? null : item.getNome());
			}
		});

		colaborador.setConverter(new StringConverter<Colaborador>() {
			@Override
			public String toString(Colaborador colaborador) {
				return (colaborador == null) ? "" : colaborador.getNome();
			}

			@Override
			public Colaborador fromString(String string) {
				return colaborador.getItems().stream().filter(c -> c.getNome().equalsIgnoreCase(string)).findFirst()
						.orElse(null);
			}
		});

		ComboBox<Pdi> metas = new ComboBox<>();
		metas.setEditable(true);
		metas.setPrefWidth(500);
		metas.getEditor().getStyleClass().add("input");
		metas.setMaxWidth(Double.MAX_VALUE);
		metas.setPromptText("Metas");
		metas.getStyleClass().add("combo-box");
		GridPane.setHgrow(metas, Priority.ALWAYS);

		metas.setCellFactory(lv -> new javafx.scene.control.ListCell<Pdi>() {
			@Override
			protected void updateItem(Pdi item, boolean empty) {
				super.updateItem(item, empty);
				setText((empty || item == null) ? null : item.getObjetivo());
			}
		});

		metas.setButtonCell(new javafx.scene.control.ListCell<Pdi>() {
			@Override
			protected void updateItem(Pdi item, boolean empty) {
				super.updateItem(item, empty);
				setText((empty || item == null) ? null : item.getObjetivo());
			}
		});

		metas.setConverter(new StringConverter<Pdi>() {
			@Override
			public String toString(Pdi pdi) {
				return (pdi == null) ? "" : pdi.getObjetivo();
			}

			@Override
			public Pdi fromString(String string) {
				return metas.getItems().stream().filter(p -> p.getObjetivo().equalsIgnoreCase(string)).findFirst()
						.orElse(null);
			}
		});

		Text colab = new Text("Colaborador:");
		colab.setId("colab");

		colaborador.setOnAction(event -> {
			Colaborador selecionado = colaborador.getSelectionModel().getSelectedItem();
			metas.getItems().clear(); // limpa metas antigas

			if (selecionado != null) {
				List<Pdi> listaMetas = pdiDAO.findByColaborador(selecionado.getId());
				metas.getItems().addAll(listaMetas);
				colab.setText(selecionado.getNome());
			} else {
				colab.setText("Colaborador: ");
			}
		});

		Text meta = new Text("Meta Escolhida: ");
		meta.setId("meta");

		

		HBox combobox = new HBox(10);
		combobox.getChildren().addAll(colaborador, metas);

		Text statusText = new Text("Status:");
		statusText.setId("status");

		ComboBox<String> status = new ComboBox<>();
		status.getItems().addAll("Não Iniciado", "Em Andamento", "Concluído", "Atrasado");

		metas.setOnAction(event -> {
		    Pdi metaSelecionada = metas.getSelectionModel().getSelectedItem();

		    if (metaSelecionada != null) {
		        meta.setText(metaSelecionada.getObjetivo());

		        String statusAtual = traduzirStatus(metaSelecionada.getStatus());
		        status.getSelectionModel().select(statusAtual);
		    } else {
		        meta.setText("Meta Escolhida:");
		        status.getSelectionModel().clearSelection();
		    }
		});
		
		Text upload = new Text("Upload de arquivo:");
		upload.setId("upload");

		Button botao = new Button("Selecionar arquivo");
		botao.setId("botao");
		Text arquivoS = new Text("Nenhum arquivo selecionado");
		arquivoS.setId("arquivoS");
		
		botao.setOnAction(event -> {
			FileChooser arquiv = new FileChooser();
			arquiv.setTitle("Escolha um arquivo");

			arquiv.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"),
					new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

			File arquivo = arquiv.showOpenDialog(avaliacaorhStage);
			if (arquivo != null) {
				arquivoSelecionado = arquivo;
				arquivoS.setText(arquivo.getName());

			}
		});

		HBox arquivoBox = new HBox(15);
		arquivoBox.setAlignment(Pos.CENTER_LEFT);
		arquivoBox.getChildren().addAll(botao, arquivoS);

		Button salvar = new Button("Salvar");
		salvar.setId("salvar"); 
		
		salvar.setOnAction(event -> {
		    Pdi metaSelecionada = metas.getSelectionModel().getSelectedItem(); 
		    String statusSelecionado = status.getSelectionModel().getSelectedItem(); 

		    if (metaSelecionada != null && statusSelecionado != null) {
		        Status novoStatus = null;
		        switch (statusSelecionado) {
		            case "Não Iniciado":
		                novoStatus = Status.NAO_INICIADO;
		                break;
		            case "Em Andamento":
		                novoStatus = Status.EM_ANDAMENTO;
		                break;
		            case "Concluído":
		                novoStatus = Status.CONCLUIDO;
		                break;
		            case "Atrasado":
		                novoStatus = Status.ATRASADO;
		                break;
		        }
		        
		        if (novoStatus != null) {
		            metaSelecionada.setStatus(novoStatus);
		            pdiDAO.update(metaSelecionada);


		            if (arquivoSelecionado != null) { 
		                File pastaUploads = new File(System.getProperty("user.dir") + "/uploads");
		                if (!pastaUploads.exists()) pastaUploads.mkdirs();

		                File destino = new File(pastaUploads, arquivoSelecionado.getName());

		                try (FileChannel source = new FileInputStream(arquivoSelecionado).getChannel();
		                     FileChannel dest = new FileOutputStream(destino).getChannel()) {
		                    dest.transferFrom(source, 0, source.size());
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		                
		                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
		                alerta.setTitle("Sucesso");
		                alerta.setHeaderText(null);
		                alerta.setContentText("Dados salvos com sucesso!");
		                alerta.initOwner(avaliacaorhStage);
		                alerta.initModality(Modality.WINDOW_MODAL); 
		                DialogPane dialogPane = alerta.getDialogPane();
		                dialogPane.setStyle("-fx-background-color: white");
		                alerta.showAndWait();
		                
		               
		                colaborador.getSelectionModel().clearSelection();
		                metas.getItems().clear();
		                metas.getSelectionModel().clearSelection();
		                status.getSelectionModel().clearSelection();
		                arquivoSelecionado = null;
		                arquivoS.setText("Nenhum arquivo selecionado");
		                meta.setText("Meta Escolhida:");
		                colab.setText("Colaborador:");
		            }
		        }
		    }
		});

		HBox button = new HBox(20);
		button.setAlignment(Pos.CENTER);
		button.getChildren().add(salvar);

		grid2.add(colaborador, 0, 1);
		grid2.add(metas, 1, 1);

		grid.add(meta, 0, 1);

		grid.add(statusText, 0, 2);

		grid.add(status, 0, 3);

		grid.add(upload, 0, 4);

		grid.add(arquivoBox, 0, 5, 2, 1);

		VBox center = new VBox();
		center.setId("center");
		center.setPadding(new Insets(20, 20, 20, 20));
		center.setSpacing(5);
		center.setAlignment(Pos.TOP_LEFT);
		center.getChildren().addAll(titulo, grid2, colab, grid);
		
		VBox tela = new VBox();
		tela.setAlignment(Pos.TOP_LEFT);
		tela.setPadding(new Insets(0,0,20,0));
		tela.getChildren().add(center);
		tela.setId("tela");
		
		HBox salvarBotao = new HBox();
		salvarBotao.setAlignment(Pos.CENTER);
		salvarBotao.setPadding(new Insets(20));
		salvarBotao.getChildren().add(salvar);
		salvarBotao.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		tela.getChildren().add(salvarBotao);

		HBox root = new HBox();
		root.setId("root");
		root.getChildren().addAll(barra, tela);

		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/gui/HomeRH.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/AvaliacaoRH.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());

		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(50);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(50);

		grid2.getColumnConstraints().addAll(col1, col2);
		grid.getColumnConstraints().addAll(col1, col2);

		avaliacaorhStage.setScene(scene);
		avaliacaorhStage.setFullScreen(true);
		avaliacaorhStage.setFullScreenExitHint("");
		avaliacaorhStage.show();
	}

	private String traduzirStatus(Status status) {
	    if (status == null) return null;
	    switch (status) {
	        case NAO_INICIADO: return "Não Iniciado";
	        case EM_ANDAMENTO: return "Em Andamento";
	        case CONCLUIDO: return "Concluído";
	        case ATRASADO: return "Atrasado";
	        default: return status.name();
	    }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}