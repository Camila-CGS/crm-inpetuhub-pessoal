package inpetu.service;

import inpetu.model.Contato;
import inpetu.model.Recurso;
import inpetu.model.Reserva;
import inpetu.model.StatusReserva;
import inpetu.repository.IContatoRepository;
import inpetu.repository.IRecursoRepository;
import inpetu.repository.IReservaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class ReservaService implements IReservaService {

    private final IReservaRepository reservaRepository;
    private final IContatoRepository contatoRepository;
    private final IRecursoRepository recursoRepository;

    public ReservaService(IReservaRepository reservaRepository,
                          IContatoRepository contatoRepository,
                          IRecursoRepository recursoRepository) {
        this.reservaRepository = reservaRepository;
        this.contatoRepository = contatoRepository;
        this.recursoRepository = recursoRepository;
    }

    @Override
    public void cadastrar(Reserva reserva) {
        if (reserva == null) {
            throw new RuntimeException("A reserva não pode ser nula.");
        }
        if (reserva.getId() == null || reserva.getId() <= 0) {
            throw new RuntimeException("O ID da reserva deve ser um número positivo.");
        }
        if (reservaRepository.existe(reserva.getId())) {
            throw new RuntimeException("Já existe uma reserva com o ID " + reserva.getId() + ".");
        }
        // Tudo é validado antes de gravar: se algo falhar, nada é alterado.
        Contato contato = resolverContato(reserva);
        Recurso recurso = resolverRecurso(reserva);
        validarPeriodo(reserva.getDataInicio(), reserva.getDataFim());
        garantirDisponibilidade(null, recurso, reserva.getDataInicio(), reserva.getDataFim(), StatusReserva.SOLICITADA);

        reserva.setContato(contato);   // passa a apontar para os objetos cadastrados
        reserva.setRecurso(recurso);
        reserva.setStatus(StatusReserva.SOLICITADA);
        reservaRepository.gravar(reserva);
    }

    @Override
    public Reserva consultar(Long id) {
        return reservaRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Reserva " + id + " não encontrada."));
    }

    @Override
    public void editar(Long id, Reserva dados) {
        if (dados == null) {
            throw new RuntimeException("Os dados da reserva não podem ser nulos.");
        }
        if (!id.equals(dados.getId())) {
            throw new RuntimeException("Não é permitido alterar o ID da reserva (" + id + " -> " + dados.getId() + ").");
        }
        Reserva existente = consultar(id);
        Contato contato = resolverContato(dados);
        Recurso recurso = resolverRecurso(dados);
        validarPeriodo(dados.getDataInicio(), dados.getDataFim());
        StatusReserva status = dados.getStatus() != null ? dados.getStatus() : existente.getStatus();
        garantirDisponibilidade(id, recurso, dados.getDataInicio(), dados.getDataFim(), status);

        existente.setContato(contato);
        existente.setRecurso(recurso);
        existente.setDataInicio(dados.getDataInicio());
        existente.setDataFim(dados.getDataFim());
        existente.setStatus(status);
        reservaRepository.gravar(existente);
    }

    @Override
    public void alterarStatus(Long id, StatusReserva novoStatus) {
        if (novoStatus == null) {
            throw new RuntimeException("O status da reserva é obrigatório.");
        }
        Reserva reserva = consultar(id);
        // Reativar uma reserva reprovada também exige que o horário ainda esteja livre.
        garantirDisponibilidade(id, reserva.getRecurso(), reserva.getDataInicio(), reserva.getDataFim(), novoStatus);
        reserva.setStatus(novoStatus);
        reservaRepository.gravar(reserva);
    }

    @Override
    public List<Reserva> listar() {
        return reservaRepository.listarTodos();
    }

    @Override
    public void remover(Long id) {
        Reserva reserva = consultar(id);
        reservaRepository.remover(reserva.getId());
    }

    // ---------- validações ----------

    private Contato resolverContato(Reserva reserva) {
        if (reserva.getContato() == null) {
            throw new RuntimeException("A reserva precisa de um contato.");
        }
        int idContato = reserva.getContato().getId();
        return contatoRepository.buscarPorId(idContato)
                .orElseThrow(() -> new RuntimeException("Contato " + idContato + " não está cadastrado."));
    }

    private Recurso resolverRecurso(Reserva reserva) {
        if (reserva.getRecurso() == null) {
            throw new RuntimeException("A reserva precisa de um recurso.");
        }
        Long idRecurso = reserva.getRecurso().getId();
        return recursoRepository.buscarPorId(idRecurso)
                .orElseThrow(() -> new RuntimeException("Recurso " + idRecurso + " não está cadastrado."));
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) {
            throw new RuntimeException("As datas de início e fim da reserva são obrigatórias.");
        }
        if (!inicio.isBefore(fim)) {
            throw new RuntimeException("A data de início deve ser anterior à data de fim.");
        }
    }

    /**
     * Regra de negócio: o recurso só pode ser reservado se estiver livre no período.
     * Reservas REPROVADAS não ocupam o recurso; {@code idIgnorar} exclui a própria reserva na edição.
     */
    private void garantirDisponibilidade(Long idIgnorar, Recurso recurso,
                                         LocalDateTime inicio, LocalDateTime fim, StatusReserva status) {
        if (status == StatusReserva.REPROVADA) {
            return;
        }
        for (Reserva outra : reservaRepository.buscarPorRecurso(recurso.getId())) {
            if (outra.getId().equals(idIgnorar) || outra.getStatus() == StatusReserva.REPROVADA) {
                continue;
            }
            boolean sobrepoe = inicio.isBefore(outra.getDataFim()) && fim.isAfter(outra.getDataInicio());
            if (sobrepoe) {
                throw new RuntimeException("O recurso '" + recurso.getNome()
                        + "' não está disponível no período: conflita com a reserva " + outra.getId() + ".");
            }
        }
    }
}
