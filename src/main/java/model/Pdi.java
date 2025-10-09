package model;

import java.time.LocalDate;

public class Pdi {
	private int id;
	private int colaborador_id;
	private String objetivo;
	private LocalDate prazo;
	private Status status;
	
	public Pdi(int id, int colaborador_id, String objetivo, LocalDate prazo, Status status) {
		this.id = id;
		this.colaborador_id = colaborador_id;
		this.objetivo = objetivo;
		this.prazo = prazo;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColaborador_id() {
		return colaborador_id;
	}

	public void setColaborador_id(int colaborador_id) {
		this.colaborador_id = colaborador_id;
	}

	public String getObjetivo() {
		return objetivo;
	}

	public void setObjetivo(String objetivo) {
		this.objetivo = objetivo;
	}

	public LocalDate getPrazo() {
		return prazo;
	}

	public void setPrazo(LocalDate prazo) {
		this.prazo = prazo;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
