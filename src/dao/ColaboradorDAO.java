package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import factory.ConnectionFactory;
import model.Colaborador;
import java.sql.Date; // Para converter LocalDate para SQL DATE

/**
 * DAO para manipulação da tabela 'colaboradores'.
 * Contém métodos de insert, update, delete e list.
 */
public class ColaboradorDAO {

    /**
     * Insere um novo colaborador no banco.
     */
    public void insert(Colaborador colaborador) {
        String sql = "INSERT INTO colaboradores (nome, cpf, data_nascimento, cargo, experiencia, observacoes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, Date.valueOf(colaborador.getData_nascimento())); // LocalDate → java.sql.Date
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getExperiencia());
            stmt.setString(6, colaborador.getObservacoes());

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
        String sql = "UPDATE colaboradores SET nome = ?, cpf = ?, data_nascimento = ?, cargo = ?, experiencia = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, colaborador.getNome());
            stmt.setString(2, colaborador.getCpf());
            stmt.setDate(3, Date.valueOf(colaborador.getData_nascimento()));
            stmt.setString(4, colaborador.getCargo());
            stmt.setString(5, colaborador.getExperiencia());
            stmt.setString(6, colaborador.getObservacoes());
            stmt.setInt(7, colaborador.getId());

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

    /**
     * Lista todos os colaboradores do banco.
     * Retorna um array de colaboradores.
     */
    public Colaborador[] list() {
        String sql = "SELECT * FROM colaboradores";
        Colaborador[] colaboradores = new Colaborador[0];

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Colaborador c = new Colaborador(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getDate("data_nascimento").toLocalDate(), // java.sql.Date → LocalDate
                        rs.getString("cargo"),
                        rs.getString("experiencia"),
                        rs.getString("observacoes")
                );

                // Redimensiona o array para adicionar o novo colaborador
                Colaborador[] temp = new Colaborador[colaboradores.length + 1];
                for (int i = 0; i < colaboradores.length; i++) {
                    temp[i] = colaboradores[i];
                }
                temp[temp.length - 1] = c;
                colaboradores = temp;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return colaboradores;
    }
}
