package model;

import java.time.LocalDate;

/**
 * Classe que representa um Usuário do sistema.
 * Contém informações pessoais, função, experiência e observações.
 */
public class Usuario {

    private int id;                 // ID único do usuário (gerado pelo banco)
    private String nome;            // Nome completo
    private String cpf;             // CPF (como String para preservar zeros)
    private LocalDate data_nascimento; // Data de nascimento
    private Funcao funcao;          // Função/cargo (enum)
    private String experiencia;     // Experiência profissional
    private String observacoes;     // Observações adicionais

    /**
     * Construtor da classe Usuario.
     * @param nome Nome do usuário
     * @param cpf CPF do usuário
     * @param data_nascimento Data de nascimento (LocalDate)
     * @param funcao Função do usuário (RH, GESTOR_GERAL, GESTOR_AREA)
     * @param experiencia Experiência profissional
     * @param observacoes Observações adicionais
     */
    public Usuario(String nome, String cpf, LocalDate data_nascimento, Funcao funcao, String experiencia,
                   String observacoes) {
        this.nome = nome;
        this.cpf = cpf;
        this.data_nascimento = data_nascimento;
        this.funcao = funcao;
        this.experiencia = experiencia;
        this.observacoes = observacoes;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getData_nascimento() { return data_nascimento; }
    public void setData_nascimento(LocalDate data_nascimento) { this.data_nascimento = data_nascimento; }

    public Funcao getFuncao() { return funcao; }
    public void setFuncao(Funcao funcao) { this.funcao = funcao; }

    public String getExperiencia() { return experiencia; }
    public void setExperiencia(String experiencia) { this.experiencia = experiencia; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
