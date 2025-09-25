package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorTest {
	public static void main(String[] args) {
		List<Colaborador> colaboradores = new ArrayList<>();
        colaboradores.add(new Colaborador(
        		1,
                "Isaura de Lourdes",
                "11111",
                LocalDate.of(2006, 9, 23),
                "Diretor Criativo",
                "Marketing Digital",
                "4 anos",
                null
        ));

        colaboradores.add(new Colaborador(
                2,
                "Gustavo Garcia",
                "22222",
                LocalDate.of(2006, 10, 11),
                "Assistente de Marketing Digital",
                "Marketing Digital",
                "3 anos",
                "Experiente"
        ));

        colaboradores.add(new Colaborador(
                3,
                "Fernanda Cassiano",
                "33333",
                LocalDate.of(2005, 4, 14),
                "Assistente de Logística",
                "Logística",
                "1 ano",
                "Estagiária"
        ));

        colaboradores.add(new Colaborador(
                4,
                "Wendy Mininel",
                "44444",
                LocalDate.of(2005, 10, 15),
                "Diretora Jurídico",
                "Jurídico",
                "5 anos",
                null
        ));
        
        for (Colaborador c : colaboradores) {
            System.out.println("ID: " + c.getId());
            System.out.println("Nome: " + c.getNome());
            System.out.println("CPF: " + c.getCpf());
            System.out.println("Data de Nascimento: " + c.getData_nascimento());
            System.out.println("Cargo: " + c.getCargo());
            System.out.println("Setor: " + c.getSetor());
            System.out.println("Experiência: " + c.getExperiencia());
            System.out.println("Observações: " + c.getObservacoes());
            System.out.println("//");
        }
    }
}
