package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.CheckinDTO;
import com.example.demo.dto.CheckinResponseDTO;
import com.example.demo.dto.CheckoutDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.dto.RegistroDTO;
import com.example.demo.dto.RelatorioDTO;
import com.example.demo.entity.Arquivo;
import com.example.demo.entity.Checkin;
import com.example.demo.entity.Checkout;
import com.example.demo.entity.Posto;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.CheckinRepository;
import com.example.demo.repository.CheckoutRepository;
import com.example.demo.repository.PostoRepository;
import com.example.demo.repository.UsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class CheckService {

    @Autowired
    private PostoRepository postoRepository;

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private CheckinRepository checkinRepository;

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ==================== CHECK-IN ====================

    @Transactional
    public CheckinResponseDTO checkin(CheckinDTO dto) {
        Posto posto = postoRepository.findById(dto.getPostoId()).orElseThrow();
        Usuario usuario = getUsuarioLogado();

        Checkin checkin = new Checkin();
        checkin.setPosto(posto);
        checkin.setUsuario(usuario);

        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
            Arquivo arquivo = arquivoService.upload(dto.getFoto());
            checkin.setFoto(arquivo);
        }

        Checkin checkinSalvo = checkinRepository.save(checkin);

        CheckinResponseDTO crd = new CheckinResponseDTO();
        crd.setPosto(posto.getNome());
        crd.setHorario(checkinSalvo.getCreatedAt());

        return crd;
    }

    // ==================== CHECK-OUT ====================

    @Transactional
    public CheckoutResponseDTO checkout(CheckoutDTO dto) {
        Posto posto = postoRepository.findById(dto.getPostoId()).orElseThrow();
        Usuario usuario = getUsuarioLogado();

        Checkout checkout = new Checkout();
        checkout.setPosto(posto);
        checkout.setUsuario(usuario);

        // Relatório
        checkout.setMatutinoPrevencoes(dto.getMatutinoPrevencoes());
        checkout.setMatutinoIncidentes(dto.getMatutinoIncidentes());
        checkout.setVespertinoPrevencoes(dto.getVespertinoPrevencoes());
        checkout.setVespertinoIncidentes(dto.getVespertinoIncidentes());
        checkout.setLesoesAguaViva(dto.getLesoesAguaViva());

        int totalMatutino = dto.getMatutinoPrevencoes() + dto.getMatutinoIncidentes();
        int totalVespertino = dto.getVespertinoPrevencoes() + dto.getVespertinoIncidentes();
        checkout.setTotalGeral(totalMatutino + totalVespertino + dto.getLesoesAguaViva());

        if (dto.getFoto() != null && !dto.getFoto().isEmpty()) {
            Arquivo arquivo = arquivoService.upload(dto.getFoto());
            checkout.setFoto(arquivo);
        }

        Checkout checkoutSalvo = checkoutRepository.save(checkout);

        CheckoutResponseDTO crd = new CheckoutResponseDTO();
        crd.setPosto(posto.getNome());
        crd.setHorario(checkoutSalvo.getCreatedAt());

        return crd;
    }

    // ==================== REGISTROS (HISTÓRICO) ====================

    public List<RegistroDTO> getRegistros(String email) {
        List<Checkin> checkins = checkinRepository.findByUsuarioEmail(email);
        List<Checkout> checkouts = checkoutRepository.findByUsuarioEmail(email);

        return unificarRegistros(checkins, checkouts);
    }

    public List<RegistroDTO> getRegistrosHoje(String email) {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicio = hoje.atStartOfDay();
        LocalDateTime fim = hoje.atTime(LocalTime.MAX);

        List<Checkin> checkins = checkinRepository.findByUsuarioEmailAndCreatedAtBetween(email, inicio, fim);
        List<Checkout> checkouts = checkoutRepository.findByUsuarioEmailAndCreatedAtBetween(email, inicio, fim);

        return unificarRegistros(checkins, checkouts);
    }

    // ==================== ADMIN ====================

    public List<RegistroDTO> getAllRegistros(Long postoId, String data) {
        List<Checkin> checkins;
        List<Checkout> checkouts;

        if (postoId != null && data != null) {
            LocalDate date = LocalDate.parse(data);
            LocalDateTime inicio = date.atStartOfDay();
            LocalDateTime fim = date.atTime(LocalTime.MAX);
            checkins = checkinRepository.findByPostoIdAndCreatedAtBetween(postoId, inicio, fim);
            checkouts = checkoutRepository.findByPostoIdAndCreatedAtBetween(postoId, inicio, fim);
        } else if (postoId != null) {
            checkins = checkinRepository.findByPostoId(postoId);
            checkouts = checkoutRepository.findByPostoId(postoId);
        } else if (data != null) {
            LocalDate date = LocalDate.parse(data);
            LocalDateTime inicio = date.atStartOfDay();
            LocalDateTime fim = date.atTime(LocalTime.MAX);
            checkins = checkinRepository.findByCreatedAtBetween(inicio, fim);
            checkouts = checkoutRepository.findByCreatedAtBetween(inicio, fim);
        } else {
            checkins = checkinRepository.findAllOrderByCreatedAtDesc();
            checkouts = checkoutRepository.findAllOrderByCreatedAtDesc();
        }

        return unificarRegistros(checkins, checkouts);
    }

    @Transactional
    public void deleteAllRegistros() {
        checkinRepository.findAllOrderByCreatedAtDesc()
                .forEach(c -> checkinRepository.softDeleteById(c.getId()));
        checkoutRepository.findAllOrderByCreatedAtDesc()
                .forEach(c -> checkoutRepository.softDeleteById(c.getId()));
    }

    // ==================== HELPERS ====================

    private Usuario getUsuarioLogado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email).orElseThrow();
    }

    private List<RegistroDTO> unificarRegistros(List<Checkin> checkins, List<Checkout> checkouts) {
        List<RegistroDTO> registros = new ArrayList<>();

        for (Checkin c : checkins) {
            RegistroDTO dto = new RegistroDTO();
            dto.setId(c.getId());
            dto.setTipo("checkin");
            dto.setUsuario(c.getNome() != null ? c.getNome() : c.getUsuario().getEmail());
            dto.setPosto(c.getPosto().getNome());
            dto.setPostoId(c.getPosto().getId());
            dto.setFotoUrl(c.getFoto() != null ? "/arquivos/" + c.getFoto().getId() : null);
            dto.setTimestamp(c.getCreatedAt());
            registros.add(dto);
        }

        for (Checkout c : checkouts) {
            RegistroDTO dto = new RegistroDTO();
            dto.setId(c.getId());
            dto.setTipo("checkout");
            dto.setUsuario(c.getUsuario().getEmail());
            dto.setPosto(c.getPosto().getNome());
            dto.setPostoId(c.getPosto().getId());
            dto.setFotoUrl(c.getFoto() != null ? "/arquivos/" + c.getFoto().getId() : null);
            dto.setTimestamp(c.getCreatedAt());

            // Relatório
            RelatorioDTO relatorio = new RelatorioDTO();
            int totalMat = c.getMatutinoPrevencoes() + c.getMatutinoIncidentes();
            int totalVesp = c.getVespertinoPrevencoes() + c.getVespertinoIncidentes();
            relatorio.setMatutino(new RelatorioDTO.TurnoDTO(c.getMatutinoPrevencoes(), c.getMatutinoIncidentes(), totalMat));
            relatorio.setVespertino(new RelatorioDTO.TurnoDTO(c.getVespertinoPrevencoes(), c.getVespertinoIncidentes(), totalVesp));
            relatorio.setLesoesAguaViva(c.getLesoesAguaViva());
            relatorio.setTotalGeral(c.getTotalGeral());
            dto.setRelatorio(relatorio);

            registros.add(dto);
        }

        // Ordena por timestamp (mais recente primeiro)
        registros.sort(Comparator.comparing(RegistroDTO::getTimestamp).reversed());

        return registros;
    }
}
