package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import factory.ConnectionFactory;
import model.Pdi;
import model.Status;

/**
 * Classe DAO responsável por realizar operações de persistência da entidade {@link Pdi}.
 * Implementa os métodos CRUD (Create, Read, Update, Delete) no banco de dados.
 */
public class PdiDAO {
    
    /**
     * Insere um novo PDI no banco de dados.
     * O ID é gerado automaticamente (AUTO_INCREMENT) e atualizado no objeto.
     *
     * @param pdi objeto {@link Pdi} a ser salvo
     */
    public void insert(Pdi pdi) {
        String sql = "INSERT INTO pdis(colaborador_id , objetivo, prazo, status) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, pdi.getColaborador_id());
            stmt.setString(2, pdi.getObjetivo());
            stmt.setDate(3, Date.valueOf(pdi.getPrazo()));
            stmt.setString(4, pdi.getStatus().name());

            stmt.executeUpdate();

            // Recupera o ID gerado e atualiza no objeto
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pdi.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Atualiza um PDI existente no banco de dados.
     * O registro é identificado pelo seu ID.
     *
     * @param pdi objeto {@link Pdi} com os novos dados
     */
    public void update(Pdi pdi) {
        String sql = "UPDATE pdis SET colaborador_id = ?, objetivo = ?, prazo = ?, status = ?  WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdi.getColaborador_id());
            stmt.setString(2, pdi.getObjetivo());
            stmt.setDate(3, Date.valueOf(pdi.getPrazo()));
            stmt.setString(4, pdi.getStatus().name());
            stmt.setInt(5, pdi.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Remove um PDI do banco de dados.
     * O registro é identificado pelo seu ID.
     *
     * @param pdi objeto {@link Pdi} a ser removido
     */
    public void delete(Pdi pdi) {
        String sql = "DELETE FROM pdis WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pdi.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Busca um PDI específico no banco de dados pelo seu ID.
     *
     * @param id identificador do PDI
     * @return objeto {@link Pdi} se encontrado, ou {@code null} caso contrário
     */
    public Pdi findById(int id) {
        String sql = "SELECT * FROM pdis WHERE id = ?";
        Pdi pdi = null;

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pdi = new Pdi(
                        rs.getInt("id"),
                        rs.getInt("colaborador_id"),
                        rs.getString("objetivo"),
                        rs.getDate("prazo").toLocalDate(),
                        Status.valueOf(rs.getString("status"))
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pdi; 
    }

    /**
     * Retorna uma lista com todos os PDIs cadastrados no banco de dados.
     *
     * @return lista de {@link Pdi}
     */
    public List<Pdi> listAll() {
        String sql = "SELECT * FROM pdis";
        List<Pdi> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Pdi pdi = new Pdi(
                    rs.getInt("id"),
                    rs.getInt("colaborador_id"),
                    rs.getString("objetivo"),
                    rs.getDate("prazo").toLocalDate(),
                    Status.valueOf(rs.getString("status"))
                );

                lista.add(pdi);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    /**
     * Listagem por ano
     */
    public List<Pdi> findByAno(int ano) {
    	String sql = "SELECT * FROM pdis WHERE YEAR(prazo) = ?";
    	List<Pdi> lista = new ArrayList<>();
    	
    	try (Connection conn = ConnectionFactory.getConnection();
    		 PreparedStatement stmt = conn.prepareStatement(sql)) {
    		
    		stmt.setInt(1, ano);
    		ResultSet rs = stmt.executeQuery();
    		
    		while (rs.next()) {
    			lista.add(new Pdi(
    				rs.getInt("id"),
    				rs.getInt("colaborador_id"),
    				rs.getString("objetivo"),
    				rs.getDate("prazo").toLocalDate(),
    				Status.valueOf(rs.getString("Status"))
    			));
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	return lista;
    }
    
    public List<Pdi> findByColaborador(int colaboradorId) {
        String sql = "SELECT * FROM pdis WHERE colaborador_id = ?";
        List<Pdi> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, colaboradorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Pdi(
                    rs.getInt("id"),
                    rs.getInt("colaborador_id"),
                    rs.getString("objetivo"),
                    rs.getDate("prazo").toLocalDate(),
                    Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Pdi> findBySetor(String setor) {
        String sql = "SELECT p.* FROM pdis p " +
                     "JOIN colaboradores c ON p.colaborador_id = c.id " +
                     "WHERE c.setor = ?";
        List<Pdi> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, setor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Pdi(
                    rs.getInt("id"),
                    rs.getInt("colaborador_id"),
                    rs.getString("objetivo"),
                    rs.getDate("prazo").toLocalDate(),
                    Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }	
}

	