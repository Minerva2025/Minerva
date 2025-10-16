package model;

import java.time.LocalDate;

public class Usuario {

    private int id;                 
    private String nome;            
    private String cpf;            
    private String senha;
    private LocalDate data_nascimento; 
    private Funcao funcao;
    private String setor;
    private String experiencia;     
    private String observacoes;     

    
 // Construtor para novo usuário
    public Usuario(String nome, String cpf, String senha, LocalDate data_nascimento, Funcao funcao,
                   String experiencia, String observacoes, String setor) {
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.data_nascimento = data_nascimento;
        this.funcao = funcao;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
        this.setor = setor;
    }

    // Construtor vindo do banco
    public Usuario(int id, String nome, String cpf, String senha, LocalDate data_nascimento, Funcao funcao,
                   String experiencia, String observacoes, String setor) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.senha = senha;
        this.data_nascimento = data_nascimento;
        this.funcao = funcao;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
        this.setor = setor;
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
    
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

}