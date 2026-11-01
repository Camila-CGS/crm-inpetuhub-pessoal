package inpetu.model;

import java.time.LocalDateTime;

/** Reserva de um {@link Recurso} por um {@link Contato} em um período. O ID é imutável. */
public class Reserva {
    private final Long id;
    private Contato contato;
    private Recurso recurso;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private StatusReserva status;

    public Reserva(Long id, Contato contato, Recurso recurso,
                   LocalDateTime dataInicio, LocalDateTime dataFim, StatusReserva status) {
        this.id = id;
        this.contato = contato;
        this.recurso = recurso;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
    }

    public Long getId() { return id; }

    public Contato getContato() { return contato; }
    public void setContato(Contato contato) { this.contato = contato; }

    public Recurso getRecurso() { return recurso; }
    public void setRecurso(Recurso recurso) { this.recurso = recurso; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public StatusReserva getStatus() { return status; }
    public void setStatus(StatusReserva status) { this.status = status; }

    @Override
    public String toString() {
        return "Reserva{id=" + id
                + ", contato=" + (contato == null ? "-" : contato.getNome())
                + ", recurso=" + (recurso == null ? "-" : recurso.getNome())
                + ", inicio=" + dataInicio + ", fim=" + dataFim + ", status=" + status + "}";
    }
}
