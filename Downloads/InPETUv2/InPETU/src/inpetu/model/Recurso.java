package inpetu.model;

/** Sala, auditório ou equipamento que pode ser reservado. O ID é imutável depois de criado. */
public class Recurso {
    private final Long id;
    private String nome;
    private String tipo; // Ex: Sala, Auditório, Equipamento
    private int capacidade;

    public Recurso(Long id, String nome, String tipo, int capacidade) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.capacidade = capacidade;
    }

    public Long getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getCapacidade() { return capacidade; }
    public void setCapacidade(int capacidade) { this.capacidade = capacidade; }

    @Override
    public String toString() {
        return "Recurso{id=" + id + ", nome='" + nome + "', tipo='" + tipo + "', capacidade=" + capacidade + "}";
    }
}
