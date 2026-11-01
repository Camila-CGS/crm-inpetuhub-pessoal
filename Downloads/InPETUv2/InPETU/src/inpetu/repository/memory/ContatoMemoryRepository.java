package inpetu.repository.memory;

import inpetu.model.Contato;
import inpetu.repository.IContatoRepository;

import java.util.Optional;

public class ContatoMemoryRepository extends MemoryRepository<Contato, Integer> implements IContatoRepository {

    public ContatoMemoryRepository() {
        super(Contato::getId);
    }

    @Override
    public Optional<Contato> buscarPorNome(String nome) {
        return armazenamento.values().stream()
                .filter(c -> c.getNome() != null && c.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }
}
