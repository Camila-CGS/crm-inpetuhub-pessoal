package inpetu.model;

import java.time.LocalDateTime;

public class Reserva {
    private Long id;
    private Contato contato;
    private Recurso recurso;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String status; // Ex: "SOLICITADA", "APROVADA", "REPROVADA"

    public Reserva() {}

    public Reserva(Long id, Contato contato, Recurso recurso, LocalDateTime dataInicio, LocalDateTime dataFim, String status) {
        this.id = id;
        this.contato = contato;
        this.recurso = recurso;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Contato getContato() { return contato; }
    public void setContato(Contato contato) { this.contato = contato; }

    public Recurso getRecurso() { return recurso; }
    public void setRecurso(Recurso recurso) { this.recurso = recurso; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}