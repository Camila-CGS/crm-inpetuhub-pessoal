package inpetu.service;

import inpetu.model.Contato;
import inpetu.repository.IContatoRepository;
import inpetu.repository.IReservaRepository;

import java.util.List;

public class ContatoService implements IContatoService {

    private final IContatoRepository contatoRepository;
    private final IReservaRepository reservaRepository;

    public ContatoService(IContatoRepository contatoRepository, IReservaRepository reservaRepository) {
        this.contatoRepository = contatoRepository;
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void cadastrar(Contato contato) {
        validar(contato);
        if (contato.getId() <= 0) {
            throw new RuntimeException("O ID do contato deve ser um número positivo.");
        }
        if (contatoRepository.existe(contato.getId())) {
            throw new RuntimeException("Já existe um contato com o ID " + contato.getId() + ".");
        }
        contatoRepository.gravar(contato);
    }

    @Override
    public Contato consultar(int id) {
        return contatoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contato " + id + " não encontrado."));
    }

    @Override
    public void editar(int id, Contato dados) {
        validar(dados);
        if (dados.getId() != id) {
            throw new RuntimeException("Não é permitido alterar o ID do contato (" + id + " -> " + dados.getId() + ").");
        }
        Contato existente = consultar(id);
        existente.setNome(dados.getNome().trim());
        existente.setEmail(dados.getEmail());
        contatoRepository.gravar(existente);
    }

    @Override
    public List<Contato> listar() {
        return contatoRepository.listarTodos();
    }

    @Override
    public void remover(int id) {
        Contato existente = consultar(id);
        if (!reservaRepository.buscarPorContato(id).isEmpty()) {
            throw new RuntimeException("O contato " + id + " está vinculado a uma reserva e não pode ser excluído.");
        }
        contatoRepository.remover(existente.getId());
    }

    private void validar(Contato contato) {
        if (contato == null) {
            throw new RuntimeException("O contato não pode ser nulo.");
        }
        if (contato.getNome() == null || contato.getNome().isBlank()) {
            throw new RuntimeException("O nome do contato é obrigatório.");
        }
    }
}
