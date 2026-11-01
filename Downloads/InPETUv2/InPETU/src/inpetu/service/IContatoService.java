package inpetu.service;

import inpetu.model.Contato;

import java.util.List;

public interface IContatoService {
    void cadastrar(Contato contato);
    Contato consultar(int id);
    /** Edita o contato {@code id} com os dados informados. O ID não pode ser alterado. */
    void editar(int id, Contato dados);
    List<Contato> listar();
    void remover(int id);
}
