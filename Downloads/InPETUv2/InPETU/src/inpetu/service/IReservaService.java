package inpetu.service;

import inpetu.model.Reserva;
import inpetu.model.StatusReserva;

import java.util.List;

public interface IReservaService {
    void cadastrar(Reserva reserva);
    Reserva consultar(Long id);
    /** Edita a reserva {@code id} com os dados informados. O ID não pode ser alterado. */
    void editar(Long id, Reserva dados);
    void alterarStatus(Long id, StatusReserva novoStatus);
    List<Reserva> listar();
    void remover(Long id);
}
