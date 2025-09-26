/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import factory.ConnectionFactoryTest;
import model.Pdi;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import static org.junit.Assert.assertEquals;

public class PdiDAOTest {

    @BeforeClass
    public static void setupDatabase() throws Exception {
        try (Connection conn = ConnectionFactoryTest.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS pdis");
            stmt.execute("DROP TABLE IF EXISTS colaboradores");
            stmt.execute("CREATE TABLE colaboradores (id INT PRIMARY KEY, nome VARCHAR(50), setor VARCHAR(50))");
            stmt.execute("CREATE TABLE pdis (id INT AUTO_INCREMENT PRIMARY KEY, colaborador_id INT, objetivo VARCHAR(100), prazo DATE, status VARCHAR(20))");
        }
    }

    @Before
    public void insertData() throws Exception {
        try (Connection conn = ConnectionFactoryTest.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM pdis");
            stmt.execute("DELETE FROM colaboradores");
            stmt.execute("INSERT INTO colaboradores VALUES (1,'Alice','TI'), (2,'Bob','RH')");
            stmt.execute("INSERT INTO pdis (colaborador_id, objetivo, prazo, status) VALUES (1,'Objetivo 2025','2025-12-31','EM_ANDAMENTO'),(1,'Objetivo 2024','2024-06-15','CONCLUIDO'),(2,'Objetivo RH','2025-05-10','ATRASADO')");
        }
    }

    @Test
    public void testFindByAno() throws Exception {
        try (Connection conn = ConnectionFactoryTest.getConnection()) {
            PdiDAO dao = new PdiDAO(conn);
            List<Pdi> lista = dao.findByAno(2025);
            assertEquals(2, lista.size());
        }
    }

    @Test
    public void testFindByColaborador() throws Exception {
        try (Connection conn = ConnectionFactoryTest.getConnection()) {
            PdiDAO dao = new PdiDAO(conn);
            List<Pdi> lista = dao.findByColaborador(1);
            assertEquals(2, lista.size());
        }
    }

    @Test
    public void testFindBySetor() throws Exception {
        try (Connection conn = ConnectionFactoryTest.getConnection()) {
            PdiDAO dao = new PdiDAO(conn);
            List<Pdi> lista = dao.findBySetor("RH");
            assertEquals(1, lista.size());
            assertEquals("Objetivo RH", lista.get(0).getObjetivo());
        }
    }
}
