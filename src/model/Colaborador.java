package model;

import java.time.LocalDate;

public class Colaborador {
	
	private int id;
	private String nome;
	private String cpf;
	private LocalDate data_nascimento;
	private String cargo;
	private String setor;
	private String experiencia;
	private String observacoes;
			
			
	public Colaborador(int id, String nome, String cpf, LocalDate data_nascimento, String cargo, String setor,
			String experiencia, String observacoes) {
		this.id = id;
		this.nome = nome;
		this.cpf = cpf;
		this.data_nascimento = data_nascimento;
		this.cargo = cargo;
		this.setor = setor;
		this.experiencia = experiencia;
		this.observacoes = observacoes;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getCpf() {
		return cpf;
	}


	public void setCpf(String cpf) {
		this.cpf = cpf;
	}


	public LocalDate getData_nascimento() {
		return data_nascimento;
	}


	public void setData_nascimento(LocalDate data_nascimento) {
		this.data_nascimento = data_nascimento;
	}


	public String getCargo() {
		return cargo;
	}


	public void setCargo(String cargo) {
		this.cargo = cargo;
	}


	public String getSetor() {
		return setor;
	}


	public void setSetor(String setor) {
		this.setor = setor;
	}


	public String getExperiencia() {
		return experiencia;
	}


	public void setExperiencia(String experiencia) {
		this.experiencia = experiencia;
	}


	public String getObservacoes() {
		return observacoes;
	}


	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	

	

}
