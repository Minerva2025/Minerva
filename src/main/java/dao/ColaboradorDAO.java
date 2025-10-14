package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

import factory.ConnectionFactory;
import model.Colaborador;
import java.sql.Date;

public class ColaboradorDAO {

    /**
     * Insere um novo colaborador no banco.
     */
    public void insert(Colaborador colaborador) {
        String sql = "INSERT INTO colaboradores (nome, cpf, data_nascimento, cargo, setor, experiencia, observacoes) VALUES (?, ?, ?, ?, ?,?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, Date.valueOf(colaborador.getData_nascimento())); // LocalDate → java.sql.Date
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getSetor());
            stmt.setString(6, colaborador.getExperiencia());
            stmt.setString(7, colaborador.getObservacoes());

            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente pelo banco
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                colaborador.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza um colaborador existente no banco.
     */
    public void update(Colaborador colaborador) {
        String sql = "UPDATE colaboradores SET nome = ?, cpf = ?, data_nascimento = ?, cargo = ?, setor = ?, experiencia = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, Date.valueOf(colaborador.getData_nascimento()));
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getSetor());
            stmt.setString(6, colaborador.getExperiencia());
            stmt.setString(7, colaborador.getObservacoes());
            stmt.setInt(8, colaborador.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deleta um colaborador do banco com base no ID.
     */
    public void delete(Colaborador colaborador) {
        String sql = "DELETE FROM colaboradores WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, colaborador.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Colaborador> listAll() {
        String sql = "SELECT * FROM colaboradores";
        List<Colaborador> colaboradores = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                colaboradores.add(new Colaborador(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getDate("data_nascimento").toLocalDate(),
                    rs.getString("cargo"),
                    rs.getString("setor"),
                    rs.getString("experiencia"),
                    rs.getString("observacoes")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return colaboradores;
    }
    
    public List<Colaborador> findBySetor(String setor) {
        String sql = "SELECT * FROM colaboradores WHERE setor = ?";
        List<Colaborador> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, setor);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Colaborador(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getDate("data_nascimento").toLocalDate(),
                    rs.getString("cargo"),
                    rs.getString("setor"),
                    rs.getString("experiencia"),
                    rs.getString("observacoes")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Colaborador> findByCargo(String cargo) {
        String sql = "SELECT * FROM colaboradores WHERE cargo = ?";
        List<Colaborador> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cargo);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Colaborador(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getDate("data_nascimento").toLocalDate(),
                    rs.getString("cargo"),
                    rs.getString("setor"),
                    rs.getString("experiencia"),
                    rs.getString("observacoes")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Colaborador> findByNome(String nome) {
        String sql = "SELECT * FROM colaboradores WHERE nome LIKE ?";
        List<Colaborador> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(new Colaborador(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getDate("data_nascimento").toLocalDate(),
                    rs.getString("cargo"),
                    rs.getString("setor"),
                    rs.getString("experiencia"),
                    rs.getString("observacoes")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
    
    public Colaborador getColaboradorById(int id) {
        String sql = "SELECT * FROM colaboradores WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Colaborador(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getDate("data_nascimento").toLocalDate(),
                    rs.getString("cargo"),
                    rs.getString("setor"),
                    rs.getString("experiencia"),
                    rs.getString("observacoes")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // retorna null se não encontrou
    }
    
}
