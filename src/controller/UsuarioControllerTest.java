package controller;

import model.Usuario;
import model.Funcao;

import java.time.LocalDate;

public class UsuarioControllerTest {

    public static void main(String[] args) {
        UsuarioController controller = new UsuarioController();

        Usuario rh = new Usuario(
                "Guilherme Rosa", "011111", "54321", LocalDate.of(2006, 1, 1),
                Funcao.RH, "1 ano", null);
        rh.setId(1);

        Usuario gestorGeral = new Usuario(
                "Leticia Furtonato", "022222", "12345", LocalDate.of(2006, 5, 20),
                Funcao.GESTOR_GERAL, "3 anos", null);
        gestorGeral.setId(2);

        Usuario gestorArea = new Usuario(
                "Paulo Sodré", "033333",  "11111", LocalDate.of(1988, 3, 15),
                Funcao.GESTOR_AREA, "7 anos", null);
        gestorArea.setId(3);

 
        System.out.println("Teste de criação");
        controller.criarUsuario(rh, gestorArea);          // RH pode criar
        controller.criarUsuario(gestorGeral, rh);         // Gestor Geral não pode criar


        System.out.println("\n Teste de edição");
        controller.editarUsuario(rh, gestorArea);         // RH pode editar
        controller.editarUsuario(gestorArea, gestorGeral);// Gestor de Área não pode editar Gestor Geral
        controller.editarUsuario(gestorGeral, rh);        // Gestor Geral não pode editar RH

        System.out.println("\n Teste de remoção");
        controller.removerUsuario(rh, gestorArea);        // RH pode remover
        controller.removerUsuario(gestorGeral, rh);       // Gestor Geral não pode remove
    }
}
