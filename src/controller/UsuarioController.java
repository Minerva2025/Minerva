package controller;

import dao.UsuarioDAO;
import model.Usuario;
import model.Funcao;

public class UsuarioController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public Usuario login(String cpf, String senha) {
        Usuario usuario = usuarioDAO.autenticar(cpf, senha);
        if (usuario == null) {
            System.out.println("CPF ou senha inválidos!");
        } else {
            System.out.println("Login realizado com sucesso: " + usuario.getNome());
        }
        return usuario;
    }
    
    /**
     * Criar novo usuário - Apenas RH pode criar.
     */
    public void criarUsuario(Usuario logado, Usuario novo) {
        if (logado.getFuncao() != Funcao.RH) {
            System.out.println("Acesso negado! Somente RH pode criar usuários.");
            return;
        }

        usuarioDAO.insert(novo);
        System.out.println("Usuário criado com sucesso!");
    }

    /**
     * Editar usuário - Apenas RH pode editar
     */
    public void editarUsuario(Usuario logado, Usuario usuario) {
        // Apenas RH pode editar qualquer um.
        if (logado.getFuncao() == Funcao.RH) {
            usuarioDAO.update(usuario);
            System.out.println("Usuário atualizado com sucesso!");
        } else {
            System.out.println("Acesso negado! Você não pode editar este usuário.");
        }
    }

    /**
     * Remover usuário - Apenas RH pode remover.
     */
    public void removerUsuario(Usuario logado, Usuario usuario) {
        if (logado.getFuncao() != Funcao.RH) {
            System.out.println("Acesso negado! Somente RH pode remover usuários.");
            return;
        }
        usuarioDAO.delete(usuario);
        System.out.println("Usuário removido com sucesso!");
    }

}