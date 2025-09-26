package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import factory.ConnectionFactory;
import model.Usuario;
import model.Funcao;
import java.sql.Date; 

/**
 * DAO para manipulação da tabela 'usuarios'.
 * Contém métodos de insert, update, delete e list.
 */
public class UsuarioDAO {

    /**
     * Insere um novo usuário no banco.
     */
    public void insert(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nome, cpf, senha, data_nascimento, funcao, experiencia, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getSenha());
            stmt.setDate(4, Date.valueOf(usuario.getData_nascimento())); // Converte LocalDate para java.sql.Date
            stmt.setString(5, usuario.getFuncao().name());
            stmt.setString(6, usuario.getExperiencia());
            stmt.setString(7, usuario.getObservacoes());
            
            stmt.executeUpdate();

            // Recupera o ID gerado automaticamente pelo banco
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Atualiza um usuário existente no banco.
     */
    public void update(Usuario usuario) {
        String sql = "UPDATE usuarios SET nome = ?, cpf = ?, senha = ?,  data_nascimento = ?, funcao = ?, experiencia = ?, observacoes = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getSenha());
            stmt.setDate(4, Date.valueOf(usuario.getData_nascimento()));
            stmt.setString(5, usuario.getFuncao().name());
            stmt.setString(6, usuario.getExperiencia());
            stmt.setString(7, usuario.getObservacoes());
            stmt.setInt(8, usuario.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deleta um usuário do banco com base no ID.
     */
    public void delete(Usuario usuario) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Autentica um usuário pelo CPF e senha.
     */
    public Usuario autenticar(String cpf, String senha) {
        String sql = "SELECT * FROM usuarios WHERE cpf = ? AND senha = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cpf);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario u = new Usuario(
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("senha"),
                        rs.getDate("data_nascimento").toLocalDate(),
                        Funcao.valueOf(rs.getString("funcao")),
                        rs.getString("experiencia"),
                        rs.getString("observacoes")
                );
                u.setId(rs.getInt("id"));
                return u;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lista todos os usuários do banco. Retorna um array de usuários.
     */
    public Usuario[] list() {
        String sql = "SELECT * FROM usuarios";
        Usuario[] usuarios = new Usuario[0];

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
            	Usuario u = new Usuario(
            		    rs.getString("nome"),
            		    rs.getString("cpf"),
            		    rs.getString("senha"),
            		    rs.getDate("data_nascimento").toLocalDate(),
            		    Funcao.valueOf(rs.getString("funcao")),
            		    rs.getString("experiencia"),
            		    rs.getString("observacoes")
            		);
            		u.setId(rs.getInt("id"));

                // Redimensiona o array para adicionar o novo usuário
                Usuario[] temp = new Usuario[usuarios.length + 1];
                for (int i = 0; i < usuarios.length; i++) {
                    temp[i] = usuarios[i];
                }
                temp[temp.length - 1] = u;
                usuarios = temp;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return usuarios;
    }
}
