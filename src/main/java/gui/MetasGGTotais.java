package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import model.Pdi;
import model.Status;
import dao.PdiDAO;
import dao.ColaboradorDAO;
import model.Colaborador;

import java.util.List;

public class MetasGGTotais {

    private PdiDAO pdiDAO = new PdiDAO();
    private ColaboradorDAO colaboradorDAO = new ColaboradorDAO();

    public VBox criarPagina(VBox colunaPrincipal, Node... conteudoOriginal) {
        VBox root = new VBox(20);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(20));

        Label titulo = new Label("Metas Totais");
        titulo.setStyle("-fx-text-fill: white; -fx-font-size: 32px;");
        titulo.setAlignment(Pos.CENTER);
        VBox.setMargin(titulo, new Insets(40, 0, 20, 0));

        VBox caixa = new VBox(20);
        caixa.setAlignment(Pos.CENTER);
        caixa.setPadding(new Insets(20));
        caixa.setStyle("-fx-background-radius: 15;");

        caixa.setPrefHeight(800); 

        TableView<Pdi> tabelaTodas = new TableView<>();
        tabelaTodas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Pdi, String> colResponsavel = new TableColumn<>("Responsável");
        colResponsavel.setCellValueFactory(c -> {
            Colaborador colab = colaboradorDAO.getColaboradorById(c.getValue().getColaborador_id());
            return new javafx.beans.property.SimpleStringProperty(colab != null ? colab.getNome() : "Desconhecido");
        });
        
        TableColumn<Pdi, String> colDivisao = new TableColumn<>("Divisão");
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

        tabelaTodas.getColumns().addAll( colResponsavel,colDivisao, colObjetivo, colPrazo, colStatus);

        List<Pdi> todas = pdiDAO.listAll();
        ObservableList<Pdi> todasMetas = FXCollections.observableArrayList(todas);
        tabelaTodas.setItems(todasMetas);

        VBox.setVgrow(tabelaTodas, Priority.ALWAYS);
        tabelaTodas.setPrefHeight(650);

        Button btnvoltar = new Button("Voltar");
        btnvoltar.getStyleClass().add("btnvoltar");
        btnvoltar.setOnAction(e -> {
            colunaPrincipal.getChildren().clear();
            colunaPrincipal.getChildren().addAll(conteudoOriginal);
        });

        VBox.setMargin(btnvoltar, new Insets(20, 0, 0, 0));
        caixa.getChildren().addAll(tabelaTodas, btnvoltar);
        root.getChildren().addAll(titulo, caixa);
        return root;
    }
}