package inpetu.model;

public class Recurso {
    private Long id;
    private String nome;
    private String tipo; // Ex: Salas e Equipamentos
    private int capacidade;

    // Meu construtor vazio
    public Recurso() {}

    //Construtor com todos os dados
    public Recurso(Long id, String nome, String tipo, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
    }
        // Getters e Setters (para pegar e alterar os valores)
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public int getCapacidade() { return capacidade; }
        public void setCapacidade(int capacidade) { this.capacidade = capacidade; }
    }

