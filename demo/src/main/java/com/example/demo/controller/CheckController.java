package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.annotations.Admin;
import com.example.demo.dto.CheckinDTO;
import com.example.demo.dto.CheckinResponseDTO;
import com.example.demo.dto.CheckoutDTO;
import com.example.demo.dto.CheckoutResponseDTO;
import com.example.demo.dto.RegistroDTO;
import com.example.demo.service.CheckService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/check")
public class CheckController {

    @Autowired
    private CheckService checkService;

    // ==================== CHECK-IN ====================

    @PostMapping("/in")
    public CheckinResponseDTO checkin(@ModelAttribute @Valid CheckinDTO dto) {
        return checkService.checkin(dto);
    }

    // ==================== CHECK-OUT ====================

    @PostMapping("/out")
    public CheckoutResponseDTO checkout(@ModelAttribute @Valid CheckoutDTO dto) {
        return checkService.checkout(dto);
    }

    // ==================== REGISTROS DO USUÁRIO LOGADO ====================

    @GetMapping("/registros")
    public List<RegistroDTO> getRegistros() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return checkService.getRegistros(email);
    }

    @GetMapping("/registros/hoje")
    public List<RegistroDTO> getRegistrosHoje() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return checkService.getRegistrosHoje(email);
    }

    // ==================== ADMIN ====================

    @GetMapping("/admin/registros")
    @Admin
    public List<RegistroDTO> getAllRegistros(
            @RequestParam(required = false) Long postoId,
            @RequestParam(required = false) String data) {
        return checkService.getAllRegistros(postoId, data);
    }

    @DeleteMapping("/admin/registros")
    @Admin
    public ResponseEntity<?> deleteAllRegistros() {
        checkService.deleteAllRegistros();
        return ResponseEntity.ok().build();
    }
}
