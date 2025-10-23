package controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

import dao.ColaboradorDAO;
import dao.PdiDAO;
import model.Pdi;
import model.Colaborador;
import model.Status;

public class PdiController {
	//Cadastro de pdis
	@FXML
	private TextField txtNome;
	@FXML
	private DatePicker dpPrazo; 
	@FXML
	private ComboBox<Colaborador> cbColaborador;
	@FXML
	private Button btnCadastrar;
	
	private PdiDAO pdiDAO;
	private ObservableList<Colaborador> colaboradoresList;
	
	@FXML
	public void initialize() {
		pdiDAO = new PdiDAO();
		
		carregarColaboradores();
		
		btnCadastrar.setOnAction(event -> handleCadastrar());
	}
	
	//Configuração do combo box de colaboradores
	private void carregarColaboradores() {
		ColaboradorDAO colaboradorDAO = new ColaboradorDAO();
		List<Colaborador> colaboradores = colaboradorDAO.listAll();
		colaboradoresList.addAll(colaboradores);
		
		cbColaborador.setItems(colaboradoresList);
	}
	
	//Realizando o cadastro de um novo pdi
	@FXML
	private void handleCadastrar() {
		if (cadastroValido()) {
			try {
				Colaborador colaboradorSelecionado = cbColaborador.getValue();
				
				Pdi pdi = new Pdi (
					0,
					colaboradorSelecionado.getId(),
					txtNome.getText().trim(),
					dpPrazo.getValue(),
					Status.NAO_INICIADO
				);
				
				pdiDAO.insert(pdi);
				limparCampos();
				alertar("Sucesso", "O pdi foi criado com sucesso", AlertType.INFORMATION);
				
			} catch (Exception e) {
				alertar("Erro", "Erro na criação do pdi: "+e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	//Verificando se todos os campos do cadastro foram preenchidos
	private boolean cadastroValido() {
		if (txtNome.getText().trim().isEmpty()) {
			alertar("ERRO", "O nome da tarefa não pode estar vazio.", AlertType.ERROR);
			txtNome.requestFocus();
			return false;
		}
		if (cbColaborador.getValue() == null) {
			alertar("ERRO", "O campo colaborador não pode estar vazio.", AlertType.ERROR);
			cbColaborador.requestFocus();
			return false;
		}
		if (dpPrazo.getValue() == null) {
			alertar("ERRO", "O prazo não pode estar vazio.", AlertType.ERROR);
			dpPrazo.requestFocus();
			return false;
		}
		return true;
	}
	
	//Mensagem de alerta
	private void alertar(String titulo, String mensagem, AlertType tipo) {
		Alert alert = new Alert(tipo);
		alert.setTitle(titulo);
		alert.setContentText(mensagem);
		alert.showAndWait();
	}
	
	
	//Limpar campos do cadastro
	private void limparCampos() {
        txtNome.clear();
        dpPrazo.setValue(null);
        cbColaborador.setValue(null);
    }
	
}
