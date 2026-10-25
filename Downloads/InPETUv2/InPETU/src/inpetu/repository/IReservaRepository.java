package inpetu.repository;

import inpetu.model.Reserva;
import java.util.List;

public interface IReservaRepository {
    void salvar(Reserva reserva);
    List<Reserva> listarTodas();
    Reserva buscarPorId(Long id);
    void atualizar(Reserva reserva); // Imuda o status de "Solicitada" para "Aprovada"
}