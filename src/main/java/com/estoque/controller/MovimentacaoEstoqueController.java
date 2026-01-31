package com.estoque.controller;

import com.estoque.dto.MovimentacaoEstoqueDTO;
import com.estoque.model.TipoMovimentacao;
import com.estoque.service.MovimentacaoEstoqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
@RequiredArgsConstructor
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    @GetMapping
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoEstoqueDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }

    @GetMapping("/produto/{produtoId}")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorProduto(@PathVariable Long produtoId) {
        return ResponseEntity.ok(movimentacaoService.listarPorProduto(produtoId));
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(movimentacaoService.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorTipo(@PathVariable TipoMovimentacao tipo) {
        return ResponseEntity.ok(movimentacaoService.listarPorTipo(tipo));
    }

    @PostMapping("/entrada")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarEntrada(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarEntrada(produtoId, quantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    @PostMapping("/saida")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarSaida(
            @RequestParam Long produtoId,
            @RequestParam Integer quantidade,
            @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarSaida(produtoId, quantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    @PostMapping("/ajuste")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarAjuste(
            @RequestParam Long produtoId,
            @RequestParam Integer novaQuantidade,
            @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarAjuste(produtoId, novaQuantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
}
