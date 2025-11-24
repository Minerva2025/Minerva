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

		BarraLateralRH barra = new BarraLateralRH(logado);

		Text titulo = new Text("Avaliações");
		titulo.setId("titulo");
		
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
		
		//===========================================//
		
		Text subtit = new Text("Buscar colaborador");
		subtit.setId("subtit");
		
		ComboBox<Colaborador> colaborador = new ComboBox<>();
		colaborador.setEditable(true);
		colaborador.getItems().addAll(colaboradorDAO.listAll());
		colaborador.setPromptText("Selecione um colaborador");
		colaborador.setPrefWidth(300);
		colaborador.getEditor().getStyleClass().add("input");
		colaborador.setMaxWidth(Double.MAX_VALUE);
		colaborador.setPromptText("Colaborador");
		colaborador.getStyleClass().add("combo-box");
		GridPane.setHgrow(colaborador, Priority.ALWAYS);

		ObservableList<Colaborador> todosColaboradores = FXCollections.observableArrayList(colaboradorDAO.listAll());
		colaborador.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
			Colaborador selecionado = colaborador.getSelectionModel().getSelectedItem();
			if (selecionado != null) {
				return;
			}
			
			if (newValue == null || newValue.isEmpty()) {
				colaborador.getSelectionModel().clearSelection();
				colaborador.hide();
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
		metas.setPrefWidth(300);
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
		
		GridPane pesquisa = new GridPane();
		pesquisa.setHgap(20);
		pesquisa.setVgap(30);
		pesquisa.setAlignment(Pos.TOP_LEFT);
		pesquisa.setId("pesquisa");
		pesquisa.setMaxWidth(Double.MAX_VALUE);
		pesquisa.setPadding(new Insets(0,40,0,0));
		
		pesquisa.getStyleClass().add("responsive-grid");
		colaborador.getStyleClass().add("responsive-combo");
		metas.getStyleClass().add("responsive-combo");
		subtit.getStyleClass().add("responsive-subtitle");
		
		pesquisa.add(subtit, 0, 1);
		pesquisa.add(colaborador, 0, 2);
		pesquisa.add(metas, 1, 2);
		
		//=======================================//
		
		Text colab = new Text("Colaborador");
		colab.setId("colab");

		Text cargo = new Text("cargo");
		cargo.setId("cargo");
		
		Text setor = new Text("setor");
		setor.setId("setor");
		
		colaborador.setOnAction(event -> {
			Colaborador selecionado = colaborador.getSelectionModel().getSelectedItem();
			metas.getItems().clear(); // limpa metas antigas

			if (selecionado != null) {
				List<Pdi> listaMetas = pdiDAO.findByColaborador(selecionado.getId());
				metas.getItems().addAll(listaMetas);
				colab.setText(selecionado.getNome());
				cargo.setText(selecionado.getCargo());
				setor.setText(selecionado.getSetor());
			} else {
				colab.setText("Colaborador ");
				cargo.setText("cargo");
				setor.setText("setor");
			}
		});
		
		GridPane infoscolab = new GridPane();
		infoscolab.setHgap(30);
		infoscolab.setVgap(10);
		infoscolab.setAlignment(Pos.TOP_LEFT);
		infoscolab.getStyleClass().add("responsive-grid");
		
		infoscolab.add(colab, 0, 1, 2, 1);
		infoscolab.add(cargo, 0, 2, 1, 1);
		infoscolab.add(setor, 1, 2, 1, 1);
		
		//===========================================//
		
		Text meta = new Text("Meta Escolhida");
		meta.setId("meta");

		Text data = new Text("Prazo");
		data.setId("data");

		ComboBox<String> status = new ComboBox<>();
		status.getItems().addAll("Não Iniciado", "Em Andamento", "Concluído", "Atrasado");
		status.getStyleClass().add("responsive-combo");

		metas.setOnAction(event -> {
		    Pdi metaSelecionada = metas.getSelectionModel().getSelectedItem();

		    if (metaSelecionada != null) {
		        meta.setText(metaSelecionada.getObjetivo());
		        data.setText(metaSelecionada.getPrazo().toString());
		        String statusAtual = traduzirStatus(metaSelecionada.getStatus());
		        status.getSelectionModel().select(statusAtual);
		    } else {
		        meta.setText("Meta Escolhida");
		        status.getSelectionModel().clearSelection();
		    }
		});
		
		GridPane infosmeta = new GridPane();
		infosmeta.setHgap(5);
		infosmeta.setVgap(10);
		infosmeta.setAlignment(Pos.TOP_LEFT);
		infosmeta.setMaxWidth(Double.MAX_VALUE);
		infosmeta.getStyleClass().add("responsive-grid");
		
		infosmeta.add(meta, 0, 1);
		infosmeta.add(data, 1, 1);
		infosmeta.add(status, 0, 2);
		
		//============================================================//
		
		Text upload = new Text("Deseja fazer o upload de um arquivo?");
		upload.setId("upload");

		Text arquivoEscolher = new Text("Importar arquivo");
		arquivoEscolher.setId("arquivo");
		
		arquivoEscolher.setOnMouseClicked(event -> {
			FileChooser arquiv = new FileChooser();
			arquiv.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF", "*.pdf"),
					new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg"));

			File arquivoEscolhido = arquiv.showOpenDialog(avaliacaorhStage);
			if (arquivoEscolhido != null) {
				arquivoSelecionado = arquivoEscolhido;
				arquivoEscolher.setText(arquivoEscolhido.getName());

			}
		});

		HBox uploadArquivo = new HBox(15);
		uploadArquivo.setAlignment(Pos.CENTER_LEFT);
		uploadArquivo.getStyleClass().add("responsive-upload");
		uploadArquivo.getChildren().addAll(upload, arquivoEscolher);

		//===========================================================//
		
		Button salvar = new Button("Salvar");
		salvar.setId("salvar"); 
		salvar.getStyleClass().add("responsive-button");
		
		salvar.setOnAction(event -> {
		    Pdi metaSelecionada = metas.getSelectionModel().getSelectedItem(); 
		    String statusSelecionado = status.getSelectionModel().getSelectedItem(); 

		    if (metaSelecionada == null || statusSelecionado == null) {
		    	Alert erro = new Alert(Alert.AlertType.ERROR);
		        erro.setTitle("Erro");
		        erro.setHeaderText(null);
		        erro.setContentText("Selecione uma meta e um status antes de salvar.");
		        erro.showAndWait();
		        return; 
		    }
		    
		    Status novoStatus = converterParaEnum(statusSelecionado);
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
		   metas.getSelectionModel().clearSelection();		   
		   status.getSelectionModel().clearSelection();   
		                
		   arquivoSelecionado = null;		           
		   arquivoEscolher.setText("Nenhum arquivo selecionado");		                
		            
		   meta.setText("Meta Escolhida");		   
		   colab.setText("Colaborador");
		  
		        
		});

		HBox button = new HBox(20);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("responsive-button-container");
		button.getChildren().add(salvar);
		
		//========================================================//


		VBox center = new VBox();
		center.setId("center");
		center.setPadding(new Insets(20, 20, 20, 20));
		center.setSpacing(50);
		center.setAlignment(Pos.TOP_LEFT);
		center.getChildren().addAll(titulo, pesquisa, infoscolab, infosmeta, uploadArquivo, button, blob1, blob2, blob3);
		
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
        scene.getStylesheets().add(getClass().getResource("/gui/Global.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/gui/BarraLateral.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/HomeRH.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/gui/AvaliacaoRH.css").toExternalForm());

		scene.widthProperty().addListener((obss, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});
		
		scene.heightProperty().addListener((obss, oldVal, newVal) -> {
			updateResponsiveStyles(scene);
		});

		center.prefWidthProperty().bind(root.widthProperty().multiply(0.85));
		barra.prefWidthProperty().bind(root.widthProperty().multiply(0.15));

		blob1.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
		blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

		blob2.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.08));
		blob2.radiusYProperty().bind(blob2.radiusXProperty());

		blob3.radiusXProperty().bind(Bindings.multiply(scene.widthProperty(), 0.035));
		blob3.radiusYProperty().bind(blob3.radiusXProperty());

		StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
		blob1.translateXProperty().bind(scene.widthProperty().multiply(0.35));
		blob1.translateYProperty().bind(scene.heightProperty().multiply(-0.01));
		blob1.setManaged(false);

		StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
		blob2.translateXProperty().bind(scene.widthProperty().multiply(0.82));
		blob2.translateYProperty().bind(scene.heightProperty().multiply(0.98));
		blob2.setManaged(false);

		StackPane.setAlignment(blob3, Pos.TOP_RIGHT);
		blob3.translateXProperty().bind(scene.widthProperty().multiply(0.5));
		blob3.translateYProperty().bind(scene.heightProperty().multiply(0.1));
		blob3.setManaged(false);

		avaliacaorhStage.setScene(scene);
		avaliacaorhStage.setFullScreen(true);
		avaliacaorhStage.setFullScreenExitHint("");
		avaliacaorhStage.show();
		
		updateResponsiveStyles(scene);
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
	
	private String traduzirStatus(Status status) {
		    if (status == null) return null;

		    switch (status) {
		        case NAO_INICIADO:
		            return "Não Iniciado";
		        case EM_ANDAMENTO:
		            return "Em Andamento";
		        case CONCLUIDO:
		            return "Concluído";
		        case ATRASADO:
		            return "Atrasado";
		        default:
		            return status.name();
		    }
		}
	
	private Status converterParaEnum(String texto) {
	    switch (texto) {
	        case "Não Iniciado":
	            return Status.NAO_INICIADO;
	        case "Em Andamento":
	            return Status.EM_ANDAMENTO;
	        case "Concluído":
	            return Status.CONCLUIDO;
	        case "Atrasado":
	            return Status.ATRASADO;
	        default:
	            return null;
	    }
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}