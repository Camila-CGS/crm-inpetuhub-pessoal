package inpetu.service;

import inpetu.model.Recurso;
import inpetu.repository.IRecursoRepository;
import inpetu.repository.IReservaRepository;

import java.util.List;

public class RecursoService implements IRecursoService {

    private final IRecursoRepository recursoRepository;
    private final IReservaRepository reservaRepository;

    public RecursoService(IRecursoRepository recursoRepository, IReservaRepository reservaRepository) {
        this.recursoRepository = recursoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void cadastrar(Recurso recurso) {
        validar(recurso);
        if (recurso.getId() == null || recurso.getId() <= 0) {
            throw new RuntimeException("O ID do recurso deve ser um número positivo.");
        }
        if (recursoRepository.existe(recurso.getId())) {
            throw new RuntimeException("Já existe um recurso com o ID " + recurso.getId() + ".");
        }
        recursoRepository.gravar(recurso);
    }

    @Override
    public Recurso consultar(Long id) {
        return recursoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Recurso " + id + " não encontrado."));
    }

    @Override
    public void editar(Long id, Recurso dados) {
        validar(dados);
        if (!id.equals(dados.getId())) {
            throw new RuntimeException("Não é permitido alterar o ID do recurso (" + id + " -> " + dados.getId() + ").");
        }
        Recurso existente = consultar(id);
        existente.setNome(dados.getNome().trim());
        existente.setTipo(dados.getTipo().trim());
        existente.setCapacidade(dados.getCapacidade());
        recursoRepository.gravar(existente);
    }

    @Override
    public List<Recurso> listar() {
        return recursoRepository.listarTodos();
    }

    @Override
    public void remover(Long id) {
        Recurso existente = consultar(id);
        if (!reservaRepository.buscarPorRecurso(id).isEmpty()) {
            throw new RuntimeException("O recurso " + id + " está vinculado a uma reserva e não pode ser excluído.");
        }
        recursoRepository.remover(existente.getId());
    }

    private void validar(Recurso recurso) {
        if (recurso == null) {
            throw new RuntimeException("O recurso não pode ser nulo.");
        }
        if (recurso.getNome() == null || recurso.getNome().isBlank()) {
            throw new RuntimeException("O nome do recurso é obrigatório.");
        }
        if (recurso.getTipo() == null || recurso.getTipo().isBlank()) {
            throw new RuntimeException("O tipo do recurso é obrigatório (ex.: Sala, Auditório, Equipamento).");
        }
        if (recurso.getCapacidade() < 1) {
            throw new RuntimeException("A capacidade do recurso deve ser de pelo menos 1.");
        }
    }
}
