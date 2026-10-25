package inpetu.repository.memory;

import inpetu.model.Reserva;
import inpetu.repository.IReservaRepository;

import java.util.ArrayList;
import java.util.List;

public class ReservaMemoryRepository implements IReservaRepository {
    private List<Reserva> listaReservas = new ArrayList<>();

    @Override
    public void salvar(Reserva reserva) {
        listaReservas.add(reserva);
    }

    @Override
    public List<Reserva> listarTodas() {
        return listaReservas;
    }

    @Override
    public Reserva buscarPorId(Long id) {
        for (Reserva r : listaReservas) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public void atualizar(Reserva reserva) {
        // Como está em memória com lista, pode atualizar substituindo ou editando os campos
        for (int i = 0; i < listaReservas.size(); i++) {
            if (listaReservas.get(i).getId().equals(reserva.getId())) {
                listaReservas.set(i, reserva);
                return;
            }
        }
    }
}