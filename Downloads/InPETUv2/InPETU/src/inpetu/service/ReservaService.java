package inpetu.service;

import inpetu.model.Reserva;
import inpetu.repository.IReservaRepository;

import java.util.List;

public class ReservaService {
    private IReservaRepository reservaRepository;

    public ReservaService(IReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public void solicitarReserva(Reserva novaReserva) {
        // Validação de conflito de horário para o mesmo recurso
        for (Reserva r : reservaRepository.listarTodas()) {
            // Verifica se é o mesmo recurso e se o status está aprovado/solicitado
            if (r.getRecurso().getId().equals(novaReserva.getRecurso().getId()) &&
                    !r.getStatus().equals("REPROVADA")) {

                //  sobreposição de datas/horários
                boolean conflito = novaReserva.getDataInicio().isBefore(r.getDataFim()) &&
                        novaReserva.getDataFim().isAfter(r.getDataInicio());

                if (conflito) {
                    throw new RuntimeException("Erro: Este recurso já possui uma reserva neste horário!");
                }
            }
        }

        novaReserva.setStatus("SOLICITADA");
        reservaRepository.salvar(novaReserva);
    }

    public void alterarStatusReserva(Long idReserva, String novoStatus) {
        Reserva reserva = reservaRepository.buscarPorId(idReserva);
        if (reserva != null) {
            reserva.setStatus(novoStatus); // Ex: "APROVADA" ou "REPROVADA"
            reservaRepository.atualizar(reserva);
        }
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.listarTodas();
    }
}