package inpetu.repository.memory;

import inpetu.model.Recurso;
import inpetu.repository.IRecursoRepository;

import java.util.ArrayList;
import java.util.List;

public class RecursoMemoryRepository implements IRecursoRepository {
    private List<Recurso> listaRecursos = new ArrayList<>();

    @Override
    public void salvar(Recurso recurso) {
        listaRecursos.add(recurso);
    }

    @Override
    public List<Recurso> listarTodos() {
        return listaRecursos;
    }

    @Override
    public Recurso buscarPorId(Long id) {
        for (Recurso r : listaRecursos) {
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }
}