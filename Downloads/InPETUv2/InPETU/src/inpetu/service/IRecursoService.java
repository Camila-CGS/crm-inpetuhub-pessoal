package inpetu.service;

import inpetu.model.Recurso;

import java.util.List;

public interface IRecursoService {
    void cadastrar(Recurso recurso);
    Recurso consultar(Long id);
    /** Edita o recurso {@code id} com os dados informados. O ID não pode ser alterado. */
    void editar(Long id, Recurso dados);
    List<Recurso> listar();
    void remover(Long id);
}
