package inpetu.repository.memory;

import inpetu.model.Reserva;
import inpetu.repository.IReservaRepository;

import java.util.List;

public class ReservaMemoryRepository extends MemoryRepository<Reserva, Long> implements IReservaRepository {

    public ReservaMemoryRepository() {
        super(Reserva::getId);
    }

    @Override
    public List<Reserva> buscarPorRecurso(Long idRecurso) {
        return armazenamento.values().stream()
                .filter(r -> r.getRecurso() != null && r.getRecurso().getId().equals(idRecurso))
                .toList();
    }

    @Override
    public List<Reserva> buscarPorContato(int idContato) {
        return armazenamento.values().stream()
                .filter(r -> r.getContato() != null && r.getContato().getId() == idContato)
                .toList();
    }
}
