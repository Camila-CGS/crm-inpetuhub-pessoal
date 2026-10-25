package inpetu.service;

import inpetu.model.Recurso;
import inpetu.repository.IRecursoRepository;

import java.util.List;

public class RecursoService implements IRecursoService {
    private IRecursoRepository recursoRepository;

    public RecursoService(IRecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Override
    public void cadastrarRecurso(Recurso recurso) {
        // regras de negócio
        recursoRepository.salvar(recurso);
    }

    @Override
    public List<Recurso> listarRecursos() {
        return recursoRepository.listarTodos();
    }
}