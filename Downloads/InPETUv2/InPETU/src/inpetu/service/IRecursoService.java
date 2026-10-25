package inpetu.service;

import inpetu.model.Recurso;
import java.util.List;

public interface IRecursoService {
    void cadastrarRecurso(Recurso recurso);
    List<Recurso> listarRecursos();
}