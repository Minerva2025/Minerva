package controller;

import dao.UsuarioDAO;
import model.Usuario;
import model.Funcao;

public class UsuarioController {

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

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
     * Editar usuário - RH pode editar qualquer um, Gestor de Área só sua equipe.
     */
    public void editarUsuario(Usuario logado, Usuario usuario) {
        // Apenas RH pode editar qualquer um ou Gestor de Área dentro da sua equipe.
        if (logado.getFuncao() == Funcao.RH || pertenceEquipe(logado, usuario)) {
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

    /**
     * Listar usuários - RH e Gestor Geral veem todos, Gestor de Área só sua equipe.
     */
    public void listarUsuarios(Usuario logado) {
        Usuario[] usuarios = usuarioDAO.list();
        for (Usuario u : usuarios) {
            if (logado.getFuncao() == Funcao.RH || logado.getFuncao() == Funcao.GESTOR_GERAL || pertenceEquipe(logado, u)) {
                System.out.println("ID: " + u.getId() + " | Nome: " + u.getNome() +
                        " | Função: " + u.getFuncao() + " | Gestor: " + u.getGestorId());
            }
        }
    }

    /**
     * Verifica se um colaborador pertence à equipe do gestor logado.
     */
    private boolean pertenceEquipe(Usuario gestor, Usuario colaborador) {
        if (gestor.getFuncao() != Funcao.GESTOR_AREA) {
            return false;
        }
        // Assumindo que gestorId na classe Usuario foi alterado para Integer
        return colaborador.getGestorId() != null && colaborador.getGestorId().equals(gestor.getId());
    }
}

