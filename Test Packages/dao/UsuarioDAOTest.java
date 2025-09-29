package dao;

import dao.UsuarioDAO;
import model.Usuario;
import model.Funcao;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

public class UsuarioDAOTest {

    private UsuarioDAO usuarioDAO;

    @Before
    public void setUp() {
        usuarioDAO = new UsuarioDAO();
    }

    @Test
    public void testInsertAndFindByCpfSenha() {
        String cpf = "12345678901"; // CPF único para este teste
        String senha = "senha123";

        Usuario usuario = new Usuario(
                "João Teste",
                cpf,
                senha,
                LocalDate.of(1995, 5, 15),
                Funcao.RH,
                "5 anos de experiência",
                "Observação de teste"
        );

        // Insere no banco
        usuarioDAO.insert(usuario);

        // Verifica se o ID foi gerado
        assertTrue("ID deveria ser gerado após insert", usuario.getId() > 0);

        // Recupera via autenticação
        Usuario autenticado = usuarioDAO.autenticar(cpf, senha);
        assertNotNull("Usuário deveria ser autenticado", autenticado);
        assertEquals("João Teste", autenticado.getNome());
        assertEquals(usuario.getId(), autenticado.getId());
    }

    @Test
    public void testUpdate() {
        String cpf = "98765432101";
        String senha = "senha321";

        Usuario usuario = new Usuario(
                "Maria Teste",
                cpf,
                senha,
                LocalDate.of(1990, 1, 20),
                Funcao.GESTOR_GERAL,
                "Experiência inicial",
                "Observações iniciais"
        );

        // Insere
        usuarioDAO.insert(usuario);
        assertTrue(usuario.getId() > 0);

        // Atualiza informações
        usuario.setNome("Maria Atualizada");
        usuario.setObservacoes("Observações atualizadas");
        usuarioDAO.update(usuario);

        Usuario atualizado = usuarioDAO.autenticar(cpf, senha);
        assertNotNull(atualizado);
        assertEquals("Maria Atualizada", atualizado.getNome());
        assertEquals("Observações atualizadas", atualizado.getObservacoes());
        assertEquals(usuario.getId(), atualizado.getId());
    }

    @Test
    public void testDelete() {
        String cpf = "55566677701";
        String senha = "senha555";

        Usuario usuario = new Usuario(
                "Carlos Teste",
                cpf,
                senha,
                LocalDate.of(1988, 12, 1),
                Funcao.GESTOR_AREA,
                "Pouca experiência",
                "Para deletar"
        );

        usuarioDAO.insert(usuario);
        assertTrue(usuario.getId() > 0);

        usuarioDAO.delete(usuario);

        Usuario autenticado = usuarioDAO.autenticar(cpf, senha);
        assertNull("Usuário não deveria mais existir após delete", autenticado);
    }

    @Test
    public void testList() {
        Usuario[] usuarios = usuarioDAO.list();
        assertNotNull("Lista não deve ser null", usuarios);
        assertTrue("Lista deve ter tamanho >= 0", usuarios.length >= 0);
    }
}
