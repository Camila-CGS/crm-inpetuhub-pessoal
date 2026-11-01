package inpetu.repository.memory;

import inpetu.model.Recurso;
import inpetu.repository.IRecursoRepository;

public class RecursoMemoryRepository extends MemoryRepository<Recurso, Long> implements IRecursoRepository {

    public RecursoMemoryRepository() {
        super(Recurso::getId);
    }
}
