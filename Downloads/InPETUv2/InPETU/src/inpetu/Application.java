package inpetu;

import inpetu.model.Contato;
import inpetu.model.Recurso;
import inpetu.model.Reserva;
import inpetu.repository.memory.ContatoMemoryRepository;
import inpetu.repository.memory.RecursoMemoryRepository;
import inpetu.repository.memory.ReservaMemoryRepository;
import inpetu.service.ContatoService;
import inpetu.service.RecursoService;
import inpetu.service.ReservaService;

import java.time.LocalDateTime;

public class Application {
    public static void main(String[] args) {
        System.out.println("--- TESTES DO INPETU HUB VERSÃO 2---");

        // 1. Testando Recursos
        RecursoService recursoService = new RecursoService(new RecursoMemoryRepository());
        Recurso sala = new Recurso(1L, "Sala de Reunião A", "Sala", 10);
        recursoService.cadastrarRecurso(sala);

        System.out.println("Recursos cadastrados: " + recursoService.listarRecursos().size());

        // 2. Testando Contatos
        ContatoService contatoService = new ContatoService(new ContatoMemoryRepository());
        Contato contato = new Contato();
        contato.setId(1);
        contato.setNome("Camila Santos");
        contatoService.gravar(contato);

        // 3. Testando Reservas
        ReservaService reservaService = new ReservaService(new ReservaMemoryRepository());
        Reserva reserva = new Reserva(
                1L,
                contato,
                sala,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                "SOLICITADA"
        );

        reservaService.solicitarReserva(reserva);
        System.out.println("Reserva solicitada com sucesso! Total de reservas: " + reservaService.listarReservas().size());

        System.out.println("--- TESTES FINALIZADOS COM SUCESSO ---");
    }
}