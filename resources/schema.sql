CREATE DATABASE IF NOT EXISTS sistema_pdi;
USE sistema_pdi;

CREATE TABLE usuarios (
	id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    funcao ENUM('RH', 'GESTOR_GERAL', 'GESTOR_AREA') NOT NULL
);

CREATE TABLE colaboradores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100), 
    cargo VARCHAR(100),
    setor VARCHAR(100),
    data_admissao DATE
);

CREATE TABLE pdis (
    id INT PRIMARY KEY AUTO_INCREMENT,
    colaborador_id INT NOT NULL,
    objetivo TEXT NOT NULL,
    prazo DATE NOT NULL,
    status ENUM('EM_ANDAMENTO', 'CONCLUIDO', 'ATRASADO') DEFAULT 'EM_ANDAMENTO',
    FOREIGN KEY (colaborador_id) REFERENCES colaboradores(id)
);