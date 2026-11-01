package inpetu.repository;

import inpetu.model.Reserva;

import java.util.List;

public interface IReservaRepository extends IRepository<Reserva, Long> {
    List<Reserva> buscarPorRecurso(Long idRecurso);
    List<Reserva> buscarPorContato(int idContato);
}
