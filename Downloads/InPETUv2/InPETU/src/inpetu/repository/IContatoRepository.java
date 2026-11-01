package inpetu.repository;

import inpetu.model.Contato;

import java.util.Optional;

public interface IContatoRepository extends IRepository<Contato, Integer> {
    Optional<Contato> buscarPorNome(String nome);
}
