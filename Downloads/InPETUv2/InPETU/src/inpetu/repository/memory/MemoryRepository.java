package inpetu.repository.memory;

import inpetu.repository.IRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Implementação em memória reutilizada pelos três repositórios (mantém a ordem de inserção). */
public abstract class MemoryRepository<T, ID> implements IRepository<T, ID> {
    protected final Map<ID, T> armazenamento = new LinkedHashMap<>();
    private final Function<T, ID> extratorDeId;

    protected MemoryRepository(Function<T, ID> extratorDeId) {
        this.extratorDeId = extratorDeId;
    }

    @Override
    public void gravar(T entidade) {
        armazenamento.put(extratorDeId.apply(entidade), entidade);
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(armazenamento.get(id));
    }

    @Override
    public boolean existe(ID id) {
        return armazenamento.containsKey(id);
    }

    /** Devolve uma cópia da lista, para que quem chama não altere o armazenamento por fora. */
    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public void remover(ID id) {
        armazenamento.remove(id);
    }
}
