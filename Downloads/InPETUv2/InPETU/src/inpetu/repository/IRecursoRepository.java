package inpetu.repository;

import inpetu.model.Recurso;
import java.util.List;

public interface IRecursoRepository {
    void salvar(Recurso recurso);
    List<Recurso> listarTodos();
    Recurso buscarPorId(Long id);
}