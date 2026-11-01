package inpetu.repository;

import java.util.List;
import java.util.Optional;

/** Contrato genérico de persistência (T = entidade, ID = tipo da chave primária). */
public interface IRepository<T, ID> {
    /** Insere ou substitui a entidade, usando o ID como chave. */
    void gravar(T entidade);
    Optional<T> buscarPorId(ID id);
    boolean existe(ID id);
    List<T> listarTodos();
    void remover(ID id);
}
