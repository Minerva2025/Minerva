CREATE DATABASE IF NOT EXISTS sistema_pdi;
USE sistema_pdi;

CREATE TABLE usuarios (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    data_nascimento DATE NOT NULL,
    funcao ENUM('RH', 'GESTOR_GERAL', 'GESTOR_AREA') NOT NULL,
    experiencia TEXT,
    observacoes TEXT,
    setor VARCHAR(50)
);

CREATE TABLE colaboradores (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    data_nascimento DATE NOT NULL,
	cargo VARCHAR(50) NOT NULL,
	setor Varchar(50) NOT NULL,
    experiencia TEXT,
    observacoes TEXT
);

CREATE TABLE pdis (
    id INT PRIMARY KEY AUTO_INCREMENT,
    colaborador_id INT NOT NULL,
    objetivo TEXT NOT NULL,
    prazo DATE NOT NULL,
    status ENUM('NAO_INICIADO','EM_ANDAMENTO','CONCLUIDO','ATRASADO') DEFAULT 'EM_ANDAMENTO',
    FOREIGN KEY (colaborador_id) REFERENCES colaboradores(id)
);

INSERT INTO usuarios
(nome, cpf, senha, data_nascimento, funcao, experiencia, observacoes)
VALUES
('Guilherme Rosa','12345678910','123456','2006-11-16','GESTOR_GERAL','Testando','Teste');
