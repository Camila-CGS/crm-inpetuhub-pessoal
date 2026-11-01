package inpetu;

import inpetu.model.Contato;
import inpetu.model.Recurso;
import inpetu.model.Reserva;
import inpetu.model.StatusReserva;
import inpetu.repository.IContatoRepository;
import inpetu.repository.IRecursoRepository;
import inpetu.repository.IReservaRepository;
import inpetu.repository.memory.ContatoMemoryRepository;
import inpetu.repository.memory.RecursoMemoryRepository;
import inpetu.repository.memory.ReservaMemoryRepository;
import inpetu.service.ContatoService;
import inpetu.service.IContatoService;
import inpetu.service.IRecursoService;
import inpetu.service.IReservaService;
import inpetu.service.RecursoService;
import inpetu.service.ReservaService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Ponto de entrada do CRM InPETU hub.
 * <p>
 * Sem argumentos: abre o menu interativo (usado na apresentação).<br>
 * Com {@code --roteiro}: executa só o roteiro de verificação e encerra
 * (código de saída 0 se tudo passou, 1 se houve falha).
 * <p>
 * As classes internas ficam neste arquivo para manter a estrutura do projeto simples:
 * {@link Sistema} (monta as dependências), {@link MenuConsole} (entrada e saída)
 * e {@link RoteiroVerificacao} (testes do roteiro do professor).
 */
public class Application {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--roteiro")) {
            boolean tudoOk = new RoteiroVerificacao().executar();
            System.exit(tudoOk ? 0 : 1);
        }

        try (Scanner scanner = new Scanner(System.in)) {
            new MenuConsole(new Sistema(), scanner).iniciar();
        }
    }

    /**
     * Raiz de composição: é o único lugar que conhece as implementações concretas
     * e injeta as dependências (Inversão de Dependência).
     * Para trocar a memória por banco de dados basta mudar os repositórios aqui.
     */
    static class Sistema {
        public final IContatoService contatos;
        public final IRecursoService recursos;
        public final IReservaService reservas;

        public Sistema() {
            IContatoRepository contatoRepository = new ContatoMemoryRepository();
            IRecursoRepository recursoRepository = new RecursoMemoryRepository();
            IReservaRepository reservaRepository = new ReservaMemoryRepository();

            this.contatos = new ContatoService(contatoRepository, reservaRepository);
            this.recursos = new RecursoService(recursoRepository, reservaRepository);
            this.reservas = new ReservaService(reservaRepository, contatoRepository, recursoRepository);
        }
    }

    /** Interface de console. Só conversa com as interfaces de serviço (não conhece repositórios). */
    static class MenuConsole {
        private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        private final Sistema sistema;
        private final Scanner entrada;

        public MenuConsole(Sistema sistema, Scanner entrada) {
            this.sistema = sistema;
            this.entrada = entrada;
        }

        /** Lançada quando a entrada acaba (ex.: Ctrl+D), para não ficar pedindo dados para sempre. */
        private static class EntradaEncerradaException extends RuntimeException {
            private static final long serialVersionUID = 1L;
        }

        public void iniciar() {
            try {
                executarMenuPrincipal();
            } catch (EntradaEncerradaException e) {
                System.out.println("\nEntrada encerrada. Saindo...");
            }
        }

        private void executarMenuPrincipal() {
            int opcao;
            do {
                System.out.println("\n========================================");
                System.out.println("          CRM InPETU hub - MVP          ");
                System.out.println("========================================");
                System.out.println("1. Contatos");
                System.out.println("2. Recursos");
                System.out.println("3. Reservas");
                System.out.println("4. Executar roteiro de verificação");
                System.out.println("5. Carregar dados de exemplo");
                System.out.println("0. Sair");
                opcao = lerInt("Escolha uma opção: ");
                switch (opcao) {
                    case 1 -> menuContatos();
                    case 2 -> menuRecursos();
                    case 3 -> menuReservas();
                    case 4 -> new RoteiroVerificacao().executar();
                    case 5 -> executar(this::carregarExemplos);
                    case 0 -> System.out.println("Encerrando a aplicação...");
                    default -> System.out.println("Opção inválida.");
                }
            } while (opcao != 0);
        }

        // ===================== CONTATOS =====================
        private void menuContatos() {
            int op;
            do {
                op = submenu("CONTATOS", "Cadastrar", "Consultar", "Editar", "Listar", "Excluir");
                switch (op) {
                    case 1 -> executar(() -> {
                        sistema.contatos.cadastrar(new Contato(lerInt("ID: "), lerTexto("Nome: "), lerTexto("E-mail (opcional): ")));
                        System.out.println("Contato cadastrado.");
                    });
                    case 2 -> executar(() -> System.out.println(sistema.contatos.consultar(lerInt("ID: "))));
                    case 3 -> executar(() -> {
                        int id = lerInt("ID do contato a editar: ");
                        Contato atual = sistema.contatos.consultar(id);
                        int novoId = lerIntOuPadrao("Novo ID (Enter mantém " + id + "): ", id);
                        String nome = lerTextoOuPadrao("Nome (Enter mantém '" + atual.getNome() + "'): ", atual.getNome());
                        String email = lerTextoOuPadrao("E-mail (Enter mantém): ", atual.getEmail());
                        sistema.contatos.editar(id, new Contato(novoId, nome, email));
                        System.out.println("Contato atualizado.");
                    });
                    case 4 -> listar(sistema.contatos.listar(), "Nenhum contato cadastrado.");
                    case 5 -> executar(() -> {
                        sistema.contatos.remover(lerInt("ID do contato a excluir: "));
                        System.out.println("Contato excluído.");
                    });
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        // ===================== RECURSOS =====================
        private void menuRecursos() {
            int op;
            do {
                op = submenu("RECURSOS", "Cadastrar", "Consultar", "Editar", "Listar", "Excluir");
                switch (op) {
                    case 1 -> executar(() -> {
                        sistema.recursos.cadastrar(new Recurso(lerLong("ID: "), lerTexto("Nome: "),
                                lerTexto("Tipo (Sala, Auditório, Equipamento): "), lerInt("Capacidade: ")));
                        System.out.println("Recurso cadastrado.");
                    });
                    case 2 -> executar(() -> System.out.println(sistema.recursos.consultar(lerLong("ID: "))));
                    case 3 -> executar(() -> {
                        long id = lerLong("ID do recurso a editar: ");
                        Recurso atual = sistema.recursos.consultar(id);
                        long novoId = lerLongOuPadrao("Novo ID (Enter mantém " + id + "): ", id);
                        String nome = lerTextoOuPadrao("Nome (Enter mantém '" + atual.getNome() + "'): ", atual.getNome());
                        String tipo = lerTextoOuPadrao("Tipo (Enter mantém '" + atual.getTipo() + "'): ", atual.getTipo());
                        int cap = lerIntOuPadrao("Capacidade (Enter mantém " + atual.getCapacidade() + "): ", atual.getCapacidade());
                        sistema.recursos.editar(id, new Recurso(novoId, nome, tipo, cap));
                        System.out.println("Recurso atualizado.");
                    });
                    case 4 -> listar(sistema.recursos.listar(), "Nenhum recurso cadastrado.");
                    case 5 -> executar(() -> {
                        sistema.recursos.remover(lerLong("ID do recurso a excluir: "));
                        System.out.println("Recurso excluído.");
                    });
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        // ===================== RESERVAS =====================
        private void menuReservas() {
            int op;
            do {
                op = submenu("RESERVAS", "Cadastrar", "Consultar", "Editar", "Listar", "Alterar status", "Excluir");
                switch (op) {
                    case 1 -> executar(() -> {
                        long id = lerLong("ID da reserva: ");
                        Reserva nova = new Reserva(id, lerContatoReferencia(), lerRecursoReferencia(),
                                lerDataHora("Início (dd/MM/yyyy HH:mm): "), lerDataHora("Fim (dd/MM/yyyy HH:mm): "), null);
                        sistema.reservas.cadastrar(nova);
                        System.out.println("Reserva cadastrada com status " + nova.getStatus() + ".");
                    });
                    case 2 -> executar(() -> System.out.println(sistema.reservas.consultar(lerLong("ID: "))));
                    case 3 -> executar(() -> {
                        long id = lerLong("ID da reserva a editar: ");
                        sistema.reservas.consultar(id);
                        long novoId = lerLongOuPadrao("Novo ID (Enter mantém " + id + "): ", id);
                        Reserva dados = new Reserva(novoId, lerContatoReferencia(), lerRecursoReferencia(),
                                lerDataHora("Início (dd/MM/yyyy HH:mm): "), lerDataHora("Fim (dd/MM/yyyy HH:mm): "), null);
                        sistema.reservas.editar(id, dados);
                        System.out.println("Reserva atualizada.");
                    });
                    case 4 -> listar(sistema.reservas.listar(), "Nenhuma reserva cadastrada.");
                    case 5 -> executar(() -> {
                        long id = lerLong("ID da reserva: ");
                        StatusReserva status = StatusReserva.valueOf(
                                lerTexto("Novo status (SOLICITADA, APROVADA, REPROVADA): ").toUpperCase());
                        sistema.reservas.alterarStatus(id, status);
                        System.out.println("Status atualizado.");
                    });
                    case 6 -> executar(() -> {
                        sistema.reservas.remover(lerLong("ID da reserva a excluir: "));
                        System.out.println("Reserva excluída.");
                    });
                    case 0 -> { }
                    default -> System.out.println("Opção inválida.");
                }
            } while (op != 0);
        }

        /** Pergunta só o ID e monta um Contato "de referência"; o serviço localiza o contato cadastrado. */
        private Contato lerContatoReferencia() {
            String texto = lerTextoOuPadrao("ID do contato (Enter para deixar sem contato): ", "");
            return texto.isBlank() ? null : new Contato(converterInt(texto), "", null);
        }

        private Recurso lerRecursoReferencia() {
            String texto = lerTextoOuPadrao("ID do recurso (Enter para deixar sem recurso): ", "");
            return texto.isBlank() ? null : new Recurso(converterLong(texto), "", "", 0);
        }

        private void carregarExemplos() {
            sistema.contatos.cadastrar(new Contato(1, "Ana Souza", "ana@email.com"));
            sistema.contatos.cadastrar(new Contato(2, "Bruno Lima", "bruno@email.com"));
            sistema.recursos.cadastrar(new Recurso(1L, "Sala de Reunião A", "Sala", 10));
            sistema.recursos.cadastrar(new Recurso(2L, "Auditório", "Auditório", 120));
            sistema.recursos.cadastrar(new Recurso(3L, "Projetor", "Equipamento", 1));
            LocalDateTime t = LocalDateTime.now().plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
            sistema.reservas.cadastrar(new Reserva(1L, sistema.contatos.consultar(1), sistema.recursos.consultar(1L), t, t.plusHours(2), null));
            System.out.println("Dados de exemplo carregados (2 contatos, 3 recursos, 1 reserva).");
        }

        // ===================== apoio de entrada/saída =====================

        private int submenu(String titulo, String... opcoes) {
            System.out.println("\n--- " + titulo + " ---");
            for (int i = 0; i < opcoes.length; i++) {
                System.out.println((i + 1) + ". " + opcoes[i]);
            }
            System.out.println("0. Voltar");
            return lerInt("Escolha uma opção: ");
        }

        private <T> void listar(List<T> itens, String mensagemVazia) {
            if (itens.isEmpty()) {
                System.out.println(mensagemVazia);
            } else {
                itens.forEach(System.out::println);
                System.out.println("Total: " + itens.size());
            }
        }

        /** Executa uma ação mostrando os erros na tela sem derrubar o programa. */
        private void executar(Runnable acao) {
            try {
                acao.run();
            } catch (EntradaEncerradaException e) {
                throw e;
            } catch (RuntimeException e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }

        private String lerTexto(String pergunta) {
            System.out.print(pergunta);
            if (!entrada.hasNextLine()) {
                throw new EntradaEncerradaException();
            }
            return entrada.nextLine().trim();
        }

        private String lerTextoOuPadrao(String pergunta, String padrao) {
            String texto = lerTexto(pergunta);
            return texto.isBlank() ? (padrao == null ? "" : padrao) : texto;
        }

        private int lerInt(String pergunta) {
            while (true) {
                String texto = lerTexto(pergunta);
                try {
                    return Integer.parseInt(texto);
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número inteiro válido.");
                }
            }
        }

        private long lerLong(String pergunta) {
            while (true) {
                String texto = lerTexto(pergunta);
                try {
                    return Long.parseLong(texto);
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número inteiro válido.");
                }
            }
        }

        private int lerIntOuPadrao(String pergunta, int padrao) {
            while (true) {
                String texto = lerTexto(pergunta);
                if (texto.isBlank()) return padrao;
                try {
                    return Integer.parseInt(texto);
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número inteiro válido.");
                }
            }
        }

        private long lerLongOuPadrao(String pergunta, long padrao) {
            while (true) {
                String texto = lerTexto(pergunta);
                if (texto.isBlank()) return padrao;
                try {
                    return Long.parseLong(texto);
                } catch (NumberFormatException e) {
                    System.out.println("Digite um número inteiro válido.");
                }
            }
        }

        private int converterInt(String texto) {
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + texto + "' não é um número");
            }
        }

        private long converterLong(String texto) {
            try {
                return Long.parseLong(texto);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("'" + texto + "' não é um número");
            }
        }

        private LocalDateTime lerDataHora(String pergunta) {
            while (true) {
                String texto = lerTexto(pergunta);
                try {
                    return LocalDateTime.parse(texto, DATA_HORA);
                } catch (DateTimeParseException e) {
                    System.out.println("Data inválida. Use o formato dd/MM/yyyy HH:mm (ex.: 10/11/2026 09:00).");
                }
            }
        }
    }

    /**
     * Roteiro de verificação do professor, implementado como testes automatizados.
     * Cada item do PDF "roteiro.verificacao" aparece com o mesmo texto na saída.
     * Cada cenário usa um {@link Sistema} novo, então os testes não dependem uns dos outros.
     */
    static class RoteiroVerificacao {

        private interface Acao { void executar(); }

        private int aprovados = 0;
        private int reprovados = 0;

        /** Executa o roteiro completo e devolve true se tudo passou. */
        public boolean executar() {
            System.out.println("\n================ ROTEIRO DE VERIFICAÇÃO ================");
            roteiroContato();
            roteiroRecurso();
            roteiroReserva();
            System.out.println("\n========================================================");
            System.out.println("Resultado: " + aprovados + " verificações OK, " + reprovados + " falhas.");
            return reprovados == 0;
        }

        // ===================== CONTATO =====================
        private void roteiroContato() {
            secao("CONTATO");

            item("Cadastro");
            Sistema s = new Sistema();
            s.contatos.cadastrar(new Contato(1, "Ana Souza", "ana@email.com"));
            confere("cadastro de contato válido", s.contatos.listar().size() == 1);
            esperaErro("Inclusão de contato já existente (mesma PK)",
                    () -> s.contatos.cadastrar(new Contato(1, "Outra Pessoa", null)));
            confere("  o contato original não foi sobrescrito", s.contatos.consultar(1).getNome().equals("Ana Souza"));
            esperaErro("Inclusão de contato sem nome",
                    () -> s.contatos.cadastrar(new Contato(2, "  ", null)));
            esperaErro("Inclusão de contato com ID inválido (0)",
                    () -> s.contatos.cadastrar(new Contato(0, "Fulano", null)));

            item("Consultar");
            confere("consulta de contato existente", s.contatos.consultar(1).getId() == 1);
            esperaErro("Contato inexistente", () -> s.contatos.consultar(99));

            item("Editar");
            s.contatos.editar(1, new Contato(1, "Ana Souza Lima", "ana.lima@email.com"));
            confere("edição de nome e e-mail", s.contatos.consultar(1).getNome().equals("Ana Souza Lima")
                    && s.contatos.consultar(1).getEmail().equals("ana.lima@email.com"));
            esperaErro("Alteração de ID (não permitir)",
                    () -> s.contatos.editar(1, new Contato(5, "Ana", null)));
            confere("  o ID 1 continua existindo e o 5 não foi criado",
                    s.contatos.listar().size() == 1 && s.contatos.consultar(1) != null);
            esperaErro("Edição de contato inexistente",
                    () -> s.contatos.editar(99, new Contato(99, "Ninguém", null)));
            esperaErro("Edição com nome vazio",
                    () -> s.contatos.editar(1, new Contato(1, "", null)));
            confere("  edição inválida não alterou o contato", s.contatos.consultar(1).getNome().equals("Ana Souza Lima"));

            item("Listar");
            s.contatos.cadastrar(new Contato(2, "Bruno", null));
            s.contatos.cadastrar(new Contato(3, "Carla", null));
            confere("Listou todos? (3 cadastrados, " + s.contatos.listar().size() + " listados)", s.contatos.listar().size() == 3);
            confere("  lista vazia no início", new Sistema().contatos.listar().isEmpty());

            item("Excluir (regra de negócio)");
            s.contatos.remover(3);
            confere("exclusão de contato sem reserva", s.contatos.listar().size() == 2);
            esperaErro("Exclusão de contato inexistente", () -> s.contatos.remover(3));
            Sistema v = sistemaComReserva();
            esperaErro("Exclusão de contato vinculado a reserva (não permitir)",
                    () -> v.contatos.remover(1));
            confere("  contato vinculado continua cadastrado", v.contatos.consultar(1) != null);
        }

        // ===================== RECURSO =====================
        private void roteiroRecurso() {
            secao("RECURSO");

            item("Cadastro");
            Sistema s = new Sistema();
            s.recursos.cadastrar(new Recurso(1L, "Sala de Reunião A", "Sala", 10));
            confere("cadastro de recurso válido", s.recursos.listar().size() == 1);
            esperaErro("Inclusão de recurso já existente (mesma PK)",
                    () -> s.recursos.cadastrar(new Recurso(1L, "Auditório", "Auditório", 100)));
            confere("  o recurso original não foi sobrescrito", s.recursos.consultar(1L).getNome().equals("Sala de Reunião A"));
            esperaErro("Inclusão de recurso sem nome",
                    () -> s.recursos.cadastrar(new Recurso(2L, "", "Sala", 5)));
            esperaErro("Inclusão de recurso sem tipo",
                    () -> s.recursos.cadastrar(new Recurso(2L, "Sala B", null, 5)));
            esperaErro("Inclusão de recurso com capacidade inválida (0)",
                    () -> s.recursos.cadastrar(new Recurso(2L, "Sala B", "Sala", 0)));
            esperaErro("Inclusão de recurso sem ID",
                    () -> s.recursos.cadastrar(new Recurso(null, "Sala B", "Sala", 5)));

            item("Consultar");
            confere("consulta de recurso existente", s.recursos.consultar(1L).getCapacidade() == 10);
            esperaErro("Recurso inexistente", () -> s.recursos.consultar(99L));

            item("Editar");
            s.recursos.editar(1L, new Recurso(1L, "Sala A (reformada)", "Sala", 20));
            confere("edição de nome e capacidade", s.recursos.consultar(1L).getNome().equals("Sala A (reformada)")
                    && s.recursos.consultar(1L).getCapacidade() == 20);
            esperaErro("Alteração de ID (não permitir)",
                    () -> s.recursos.editar(1L, new Recurso(7L, "Sala A", "Sala", 10)));
            confere("  o ID 1 continua existindo e o 7 não foi criado",
                    s.recursos.listar().size() == 1 && s.recursos.consultar(1L) != null);
            esperaErro("Edição de recurso inexistente",
                    () -> s.recursos.editar(99L, new Recurso(99L, "X", "Sala", 1)));
            esperaErro("Edição com capacidade inválida",
                    () -> s.recursos.editar(1L, new Recurso(1L, "Sala A", "Sala", -3)));
            confere("  edição inválida não alterou o recurso", s.recursos.consultar(1L).getCapacidade() == 20);

            item("Listar");
            s.recursos.cadastrar(new Recurso(2L, "Projetor", "Equipamento", 1));
            s.recursos.cadastrar(new Recurso(3L, "Auditório", "Auditório", 120));
            confere("Listou todos? (3 cadastrados, " + s.recursos.listar().size() + " listados)", s.recursos.listar().size() == 3);
            confere("  lista vazia no início", new Sistema().recursos.listar().isEmpty());

            item("Excluir (regra de negócio)");
            s.recursos.remover(3L);
            confere("exclusão de recurso sem reserva", s.recursos.listar().size() == 2);
            esperaErro("Exclusão de recurso inexistente", () -> s.recursos.remover(3L));
            Sistema v = sistemaComReserva();
            esperaErro("Exclusão de recurso vinculado a reserva (não permitir)",
                    () -> v.recursos.remover(1L));
            confere("  recurso vinculado continua cadastrado", v.recursos.consultar(1L) != null);
        }

        // ===================== RESERVA =====================
        private void roteiroReserva() {
            secao("RESERVAS");
            LocalDateTime base = LocalDateTime.of(2026, 11, 10, 9, 0);

            item("Cadastro");
            Sistema s = sistemaBase();
            Contato ana = s.contatos.consultar(1);
            Recurso salaA = s.recursos.consultar(1L);
            s.reservas.cadastrar(new Reserva(1L, ana, salaA, base, base.plusHours(2), null));
            confere("cadastro de reserva válida (status SOLICITADA)",
                    s.reservas.consultar(1L).getStatus() == StatusReserva.SOLICITADA);
            esperaErro("Inclusão de reserva sem contato",
                    () -> s.reservas.cadastrar(new Reserva(2L, null, salaA, base.plusDays(1), base.plusDays(1).plusHours(1), null)));
            esperaErro("Inclusão de reserva sem recurso",
                    () -> s.reservas.cadastrar(new Reserva(2L, ana, null, base.plusDays(1), base.plusDays(1).plusHours(1), null)));
            esperaErro("Inclusão de reserva sem contato e sem recurso",
                    () -> s.reservas.cadastrar(new Reserva(2L, null, null, base.plusDays(1), base.plusDays(1).plusHours(1), null)));
            esperaErro("Inclusão de reserva com contato não cadastrado",
                    () -> s.reservas.cadastrar(new Reserva(2L, new Contato(77, "Fantasma", null), salaA, base.plusDays(1), base.plusDays(1).plusHours(1), null)));
            esperaErro("Inclusão de reserva com recurso não cadastrado",
                    () -> s.reservas.cadastrar(new Reserva(2L, ana, new Recurso(77L, "Fantasma", "Sala", 1), base.plusDays(1), base.plusDays(1).plusHours(1), null)));
            esperaErro("Inclusão de reserva com ID repetido (mesma PK)",
                    () -> s.reservas.cadastrar(new Reserva(1L, ana, s.recursos.consultar(2L), base.plusDays(2), base.plusDays(2).plusHours(1), null)));
            esperaErro("Inclusão de reserva com período inválido (início depois do fim)",
                    () -> s.reservas.cadastrar(new Reserva(2L, ana, salaA, base.plusHours(5), base.plusHours(4), null)));
            confere("  nenhuma tentativa inválida gravou reserva", s.reservas.listar().size() == 1);

            item("Cadastro com contato ou recurso repetidos");
            esperaErro("Mesmo recurso, período sobreposto (recurso indisponível)",
                    () -> s.reservas.cadastrar(new Reserva(2L, s.contatos.consultar(2), salaA, base.plusHours(1), base.plusHours(3), null)));
            s.reservas.cadastrar(new Reserva(2L, ana, s.recursos.consultar(2L), base, base.plusHours(2), null));
            confere("Mesmo contato em outro recurso no mesmo horário: permitido", s.reservas.listar().size() == 2);
            s.reservas.cadastrar(new Reserva(3L, s.contatos.consultar(2), salaA, base.plusHours(2), base.plusHours(4), null));
            confere("Mesmo recurso em período seguinte (encostando no fim da anterior): permitido", s.reservas.listar().size() == 3);
            s.reservas.cadastrar(new Reserva(4L, ana, salaA, base.plusDays(1), base.plusDays(1).plusHours(1), null));
            confere("Mesmo contato e mesmo recurso em outro dia: permitido", s.reservas.listar().size() == 4);

            item("Consultar");
            confere("consulta de reserva existente", s.reservas.consultar(1L).getContato().getId() == 1);
            esperaErro("Reserva inexistente", () -> s.reservas.consultar(99L));

            item("Editar");
            s.reservas.editar(4L, new Reserva(4L, s.contatos.consultar(2), salaA, base.plusDays(1).plusHours(3), base.plusDays(1).plusHours(5), null));
            Reserva r4 = s.reservas.consultar(4L);
            confere("edição de contato e período", r4.getContato().getId() == 2 && r4.getDataInicio().equals(base.plusDays(1).plusHours(3)));
            s.reservas.editar(4L, new Reserva(4L, r4.getContato(), salaA, r4.getDataInicio().plusMinutes(30), r4.getDataFim(), null));
            confere("edição que se sobrepõe só à própria reserva: permitido", s.reservas.consultar(4L).getDataInicio().getMinute() == 30);
            esperaErro("Alteração de ID (não permitir)",
                    () -> s.reservas.editar(4L, new Reserva(40L, ana, salaA, base.plusDays(3), base.plusDays(3).plusHours(1), null)));
            confere("  o ID 4 continua existindo e o 40 não foi criado", s.reservas.listar().size() == 4);
            esperaErro("Edição para período já ocupado (recurso indisponível)",
                    () -> s.reservas.editar(4L, new Reserva(4L, ana, salaA, base.plusHours(1), base.plusHours(3), null)));
            confere("  reserva ficou com o período anterior (estado estável)",
                    s.reservas.consultar(4L).getDataInicio().equals(base.plusDays(1).plusHours(3).plusMinutes(30)));
            esperaErro("Edição sem contato",
                    () -> s.reservas.editar(4L, new Reserva(4L, null, salaA, base.plusDays(5), base.plusDays(5).plusHours(1), null)));
            esperaErro("Edição de reserva inexistente",
                    () -> s.reservas.editar(99L, new Reserva(99L, ana, salaA, base.plusDays(5), base.plusDays(5).plusHours(1), null)));

            item("Status da reserva");
            s.reservas.alterarStatus(1L, StatusReserva.APROVADA);
            confere("aprovação de reserva", s.reservas.consultar(1L).getStatus() == StatusReserva.APROVADA);
            s.reservas.alterarStatus(1L, StatusReserva.REPROVADA);
            s.reservas.cadastrar(new Reserva(5L, s.contatos.consultar(2), salaA, base, base.plusHours(2), null));
            confere("reserva REPROVADA libera o horário para outra reserva", s.reservas.listar().size() == 5);
            esperaErro("Reativar reserva reprovada em horário já ocupado",
                    () -> s.reservas.alterarStatus(1L, StatusReserva.APROVADA));
            esperaErro("Status da reserva inexistente",
                    () -> s.reservas.alterarStatus(99L, StatusReserva.APROVADA));

            item("Listar");
            confere("Listou todas? (5 cadastradas, " + s.reservas.listar().size() + " listadas)", s.reservas.listar().size() == 5);
            confere("  lista vazia no início", new Sistema().reservas.listar().isEmpty());

            item("Excluir");
            s.reservas.remover(5L);
            confere("exclusão de reserva", s.reservas.listar().size() == 4);
            esperaErro("Exclusão de reserva inexistente", () -> s.reservas.remover(5L));
            // Depois de excluir a reserva, o vínculo some e contato/recurso podem ser excluídos.
            Sistema v = sistemaComReserva();
            v.reservas.remover(1L);
            v.contatos.remover(1);
            v.recursos.remover(1L);
            confere("sem reserva vinculada, contato e recurso podem ser excluídos",
                    v.contatos.listar().isEmpty() && v.recursos.listar().isEmpty());
        }

        // ===================== apoio =====================

        private Sistema sistemaBase() {
            Sistema s = new Sistema();
            s.contatos.cadastrar(new Contato(1, "Ana Souza", "ana@email.com"));
            s.contatos.cadastrar(new Contato(2, "Bruno Lima", "bruno@email.com"));
            s.recursos.cadastrar(new Recurso(1L, "Sala de Reunião A", "Sala", 10));
            s.recursos.cadastrar(new Recurso(2L, "Auditório", "Auditório", 120));
            return s;
        }

        private Sistema sistemaComReserva() {
            Sistema s = new Sistema();
            s.contatos.cadastrar(new Contato(1, "Ana Souza", null));
            s.recursos.cadastrar(new Recurso(1L, "Sala A", "Sala", 10));
            LocalDateTime t = LocalDateTime.of(2026, 11, 10, 9, 0);
            s.reservas.cadastrar(new Reserva(1L, s.contatos.consultar(1), s.recursos.consultar(1L), t, t.plusHours(1), null));
            return s;
        }

        private void secao(String titulo) {
            System.out.println("\n##### " + titulo + " #####");
        }

        private void item(String titulo) {
            System.out.println("\n  >> " + titulo);
        }

        private void confere(String descricao, boolean condicao) {
            registrar(descricao, condicao, "");
        }

        /** O cenário passa quando a ação é recusada com um erro (RuntimeException) em vez de ser aceita. */
        private void esperaErro(String descricao, Acao acao) {
            try {
                acao.executar();
                registrar(descricao, false, " (deveria ser recusado, mas foi aceito)");
            } catch (RuntimeException e) {
                registrar(descricao, true, " -> recusado: " + e.getMessage());
            }
        }

        private void registrar(String descricao, boolean passou, String detalhe) {
            if (passou) aprovados++; else reprovados++;
            System.out.println("    [" + (passou ? "OK" : "FALHOU") + "] " + descricao + detalhe);
        }
    }
}
