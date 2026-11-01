package inpetu.model;

/** Pessoa ou organização que se relaciona com o InPETU hub. O ID é imutável depois de criado. */
public class Contato {
    private final int id;
    private String nome;
    private String email;

    public Contato(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "Contato{id=" + id + ", nome='" + nome + "', email='" + (email == null ? "" : email) + "'}";
    }
}
