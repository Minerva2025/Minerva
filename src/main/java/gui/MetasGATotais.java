package gui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import model.Pdi;
import model.Status;
import model.Usuario;
import dao.PdiDAO;
import dao.ColaboradorDAO;
import model.Colaborador;
import java.util.List;

public class MetasGATotais {

    private PdiDAO pdiDAO = new PdiDAO();
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
    private Usuario logado;

    public VBox criarPagina(VBox colunaPrincipal, Usuario usuarioLogado, Node... conteudoOriginal) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Label titulo = new Label("Metas Totais");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 32px;");
        titulo.setAlignment(Pos.CENTER);
        VBox.setMargin(titulo, new Insets(40, 0, 20, 0));
        
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

        // Container apenas para a tabela com fundo cinza
        VBox containerTabela = new VBox();
        containerTabela.setAlignment(Pos.CENTER);
        containerTabela.setPadding(new Insets(20));
        containerTabela.setStyle("-fx-background-color: #2A2A2A; -fx-background-radius: 15;");
        containerTabela.setMaxHeight(400); // Altura máxima do container da tabela
        containerTabela.setPrefHeight(400);

        TableView<Pdi> tabelaTodas = new TableView<>();
        tabelaTodas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Pdi, String> colResponsavel = new TableColumn<>("Responsável");
        colResponsavel.setCellValueFactory(c -> {
            Colaborador colab = colaboradorDAO.getColaboradorById(c.getValue().getColaborador_id());
            return new javafx.beans.property.SimpleStringProperty(colab != null ? colab.getNome() : "Desconhecido");
        });

        TableColumn<Pdi, String> colDivisao = new TableColumn<>("Setor");
        colDivisao.setCellValueFactory(c -> {
            Colaborador colab = colaboradorDAO.getColaboradorById(c.getValue().getColaborador_id());
            return new javafx.beans.property.SimpleStringProperty(colab != null ? colab.getSetor() : "N/D");
        });

        TableColumn<Pdi, String> colObjetivo = new TableColumn<>("Descrição");
        colObjetivo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getObjetivo()));

        TableColumn<Pdi, String> colPrazo = new TableColumn<>("Prazo");
        colPrazo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPrazo().toString()));

        TableColumn<Pdi, Status> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getStatus()));
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Status status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    switch (status) {
                        case NAO_INICIADO -> { setText("Não Iniciado"); setStyle("-fx-text-fill: gray;"); }
                        case EM_ANDAMENTO -> { setText("Em Andamento"); setStyle("-fx-text-fill: orange;"); }
                        case CONCLUIDO -> { setText("Concluído"); setStyle("-fx-text-fill: green; -fx-font-weight: bold;"); }
                        case ATRASADO -> { setText("Atrasado"); setStyle("-fx-text-fill: red; -fx-font-weight: bold;"); }
                    }
                    setAlignment(Pos.CENTER);
                }
            }
        });

        tabelaTodas.getColumns().addAll(colResponsavel, colDivisao, colObjetivo, colPrazo, colStatus);

        List<Pdi> todas = pdiDAO.listAll().stream()
                .filter(pdi -> {
                    Colaborador colab = colaboradorDAO.getColaboradorById(pdi.getColaborador_id());
                    return colab != null && usuarioLogado.getSetor().equals(colab.getSetor());
                })
                .toList();

        tabelaTodas.setItems(FXCollections.observableArrayList(todas));
        VBox.setVgrow(tabelaTodas, Priority.ALWAYS);
        
        // DIMINUIR O COMPRIMENTO DA TABELA - ADICIONAR ESTAS LINHAS:
        tabelaTodas.setMaxWidth(500);
        tabelaTodas.setPrefWidth(500);
        tabelaTodas.setMinWidth(500);
        tabelaTodas.setMaxHeight(350);
        tabelaTodas.setPrefHeight(350);
        
        // Adicionar a tabela ao container com fundo cinza
        containerTabela.getChildren().add(tabelaTodas);
        
        Button btnVoltar = new Button("Voltar");
        btnVoltar.getStyleClass().add("btnvoltar");
        btnVoltar.setOnAction(e -> {
            colunaPrincipal.getChildren().clear();
            colunaPrincipal.getChildren().addAll(conteudoOriginal);
        });

        VBox.setMargin(btnVoltar, new Insets(20, 0, 0, 0));
        
        // Adicionar apenas o container da tabela e o botão ao root
        root.getChildren().addAll(titulo, containerTabela, btnVoltar, blob1, blob2, blob3);
        
        // Adicionar classes responsivas
        titulo.getStyleClass().add("responsive-title");
        tabelaTodas.getStyleClass().add("responsive-table");
        containerTabela.getStyleClass().add("responsive-caixa");
        btnVoltar.getStyleClass().add("responsive-button");
        
        blob1.radiusXProperty().bind(Bindings.multiply(root.widthProperty(), 0.07));
        blob1.radiusYProperty().bind(blob1.radiusXProperty()); 

        blob2.radiusXProperty().bind(Bindings.multiply(root.widthProperty(), 0.05));
        blob2.radiusYProperty().bind(blob2.radiusXProperty());

        blob3.radiusXProperty().bind(Bindings.multiply(root.widthProperty(), 0.02));
        blob3.radiusYProperty().bind(blob3.radiusXProperty());

        StackPane.setAlignment(blob1, Pos.TOP_RIGHT);
        blob1.translateXProperty().bind(root.widthProperty().multiply(0.82));
        blob1.translateYProperty().bind(root.heightProperty().multiply(-0.02));
        blob1.setManaged(false);

        StackPane.setAlignment(blob2, Pos.BOTTOM_LEFT);
        blob2.translateXProperty().bind(root.widthProperty().multiply(0.2));
        blob2.translateYProperty().bind(root.heightProperty().multiply(1.3));
        blob2.setManaged(false);

        StackPane.setAlignment(blob3, Pos.BOTTOM_LEFT);
        blob3.translateXProperty().bind(root.widthProperty().multiply(0.7));
        blob3.translateYProperty().bind(root.heightProperty().multiply(0.1));
        blob3.setManaged(false);
        
        // Adicionar listener para responsividade
        Scene scene = colunaPrincipal.getScene();
        if (scene != null) {
            updateResponsiveStylesMetasTotais(scene, root);
            
            scene.widthProperty().addListener((obs, oldVal, newVal) -> {
                updateResponsiveStylesMetasTotais(scene, root);
            });
            
            scene.heightProperty().addListener((obs, oldVal, newVal) -> {
                updateResponsiveStylesMetasTotais(scene, root);
            });
        }
        
        return root;
    }
    
    private void updateResponsiveStylesMetasTotais(Scene scene, VBox root) {
        double width = scene.getWidth();
        double height = scene.getHeight();
        
        // Remover classes de tamanho anteriores
        root.getStyleClass().removeAll("small-screen", "medium-screen", "large-screen", "extra-large-screen", "mobile-landscape");
        
        // Adicionar classe baseada no tamanho da tela - MESMO PADRÃO
        if (width < 768) { // Mobile
            root.getStyleClass().add("small-screen");
            if (width > height) {
                root.getStyleClass().add("mobile-landscape");
            }
        } else if (width < 1024) { // Tablet
            root.getStyleClass().add("medium-screen");
        } else if (width < 1440) { // Desktop
            root.getStyleClass().add("large-screen");
        } else { // Telas grandes
            root.getStyleClass().add("extra-large-screen");
        }
    }
}