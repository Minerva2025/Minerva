package model;

import java.time.LocalDate;

public class Usuario {

    private int id;                 
    private String nome;            
    private String cpf;            
    private String senha;
    private LocalDate data_nascimento; 
    private Funcao funcao;          
    private String experiencia;     
    private String observacoes;     

    
    // Construtor para novo usuário (sem ID, pois ainda não foi salvo no banco)
    public Usuario(String nome, String cpf, String senha, LocalDate data_nascimento, Funcao funcao,
                   String experiencia, String observacoes) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.data_nascimento = data_nascimento;
        this.funcao = funcao;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
    }

    // Construtor para usuário vindo do banco (com ID)
    public Usuario(int id, String nome, String cpf, String senha, LocalDate data_nascimento, Funcao funcao,
                   String experiencia, String observacoes) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.data_nascimento = data_nascimento;
        this.funcao = funcao;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    
	public String getSenha() { return senha; }
	public void setSenha(String senha) { this.senha = senha;}

    public LocalDate getData_nascimento() { return data_nascimento; }
    public void setData_nascimento(LocalDate data_nascimento) { this.data_nascimento = data_nascimento; }

    public Funcao getFuncao() { return funcao; }
    public void setFuncao(Funcao funcao) { this.funcao = funcao; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

}