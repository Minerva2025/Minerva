

package dao;

import dao.PdiDAO;
import model.Pdi;
import model.Status;
import org.junit.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class PdiDAOTest {

    private static PdiDAO pdiDAO;
    private Pdi pdiTeste;

    @BeforeClass
    public static void setupClass() {
        PdiDAO = new PdiDAO();
    }

    @Before
    public void setup() {
        // Criar um PDI de teste antes de cada teste
        // Use um colaborador existente no banco
        pdiTeste = new Pdi(0, 1, "Objetivo de teste", LocalDate.now().plusDays(30), Status.EM_ANDAMENTO);
        pdiDAO.insert(pdiTeste); // insere no banco e gera o ID
        // NÃO fazemos assert aqui para evitar falha no setup
    }

    @After
    public void tearDown() {
        // Limpar o PDI de teste do banco após cada teste, se tiver sido inserido
        if (pdiTeste != null && pdiTeste.getId() > 0) {
            pdiDAO.delete(pdiTeste);
        }
    }

    @Test
    public void testInsertAndFindById() {
        assertTrue("O ID do PDI deve ser maior que zero após insert", pdiTeste.getId() > 0);

        Pdi encontrado = pdiDAO.findById(pdiTeste.getId());
        assertNotNull("Deve encontrar o PDI pelo ID", encontrado);
        assertEquals(pdiTeste.getObjetivo(), encontrado.getObjetivo());
        assertEquals(pdiTeste.getStatus(), encontrado.getStatus());
        assertEquals(pdiTeste.getColaborador_id(), encontrado.getColaborador_id());
    }

    @Test
    public void testUpdate() {
        pdiTeste.setObjetivo("Objetivo atualizado");
        pdiTeste.setStatus(Status.CONCLUIDO);
        pdiDAO.update(pdiTeste);

        Pdi atualizado = pdiDAO.findById(pdiTeste.getId());
        assertEquals("Objetivo atualizado", atualizado.getObjetivo());
        assertEquals(Status.CONCLUIDO, atualizado.getStatus());
    }

    @Test
    public void testListAll() {
        List<Pdi> lista = pdiDAO.listAll();
        assertNotNull(lista);
        assertTrue(lista.size() > 0);
        assertTrue(lista.stream().anyMatch(p -> p.getId() == pdiTeste.getId()));
    }

    @Test
    public void testFindByColaborador() {
        List<Pdi> lista = pdiDAO.findByColaborador(pdiTeste.getColaborador_id());
        assertNotNull(lista);
        assertTrue(lista.stream().anyMatch(p -> p.getId() == pdiTeste.getId()));
    }

    @Test
    public void testDelete() {
        Pdi temp = new Pdi(0, 1, "Para deletar", LocalDate.now().plusDays(10), Status.EM_ANDAMENTO);
        pdiDAO.insert(temp);
        int id = temp.getId();
        assertTrue("O ID do PDI temporário deve ser maior que zero", id > 0);

        pdiDAO.delete(temp);
        Pdi deletado = pdiDAO.findById(id);
        assertNull("O PDI deve ser nulo após delete", deletado);
    }

    @Test
    public void testFindByAno() {
        int ano = LocalDate.now().getYear();
        List<Pdi> lista = pdiDAO.findByAno(ano);
        assertNotNull(lista);
        assertTrue(lista.stream().anyMatch(p -> p.getId() == pdiTeste.getId()));
    }
}