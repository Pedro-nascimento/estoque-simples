package com.estoque.controller;

import com.estoque.dto.MovimentacaoEstoqueDTO;
import com.estoque.model.TipoMovimentacao;
import com.estoque.service.MovimentacaoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Movimentações", description = "Operações de entrada, saída e ajuste de estoque")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService movimentacaoService;

    @GetMapping
    @Operation(summary = "Listar todas as movimentações", description = "Retorna o histórico completo de movimentações de estoque")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarTodas() {
        return ResponseEntity.ok(movimentacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar movimentação por ID", description = "Retorna uma movimentação específica pelo seu ID")
    public ResponseEntity<MovimentacaoEstoqueDTO> buscarPorId(@Parameter(description = "ID da movimentação") @PathVariable Long id) {
        return ResponseEntity.ok(movimentacaoService.buscarPorId(id));
    }

    @GetMapping("/produto/{produtoId}")
    @Operation(summary = "Listar movimentações por produto", description = "Retorna todas as movimentações de um produto específico")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorProduto(@Parameter(description = "ID do produto") @PathVariable Long produtoId) {
        return ResponseEntity.ok(movimentacaoService.listarPorProduto(produtoId));
    }

    @GetMapping("/periodo")
    @Operation(summary = "Listar movimentações por período", description = "Retorna movimentações dentro de um intervalo de datas")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorPeriodo(
            @Parameter(description = "Data/hora inicial (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @Parameter(description = "Data/hora final (ISO 8601)") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(movimentacaoService.listarPorPeriodo(inicio, fim));
    }

    @GetMapping("/tipo/{tipo}")
    @Operation(summary = "Listar movimentações por tipo", description = "Retorna movimentações filtradas por tipo (ENTRADA, SAIDA, AJUSTE)")
    public ResponseEntity<List<MovimentacaoEstoqueDTO>> listarPorTipo(@Parameter(description = "Tipo da movimentação") @PathVariable TipoMovimentacao tipo) {
        return ResponseEntity.ok(movimentacaoService.listarPorTipo(tipo));
    }

    @PostMapping("/entrada")
    @Operation(summary = "Registrar entrada de estoque", description = "Adiciona quantidade ao estoque de um produto")
    @ApiResponse(responseCode = "201", description = "Entrada registrada com sucesso")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarEntrada(
            @Parameter(description = "ID do produto") @RequestParam Long produtoId,
            @Parameter(description = "Quantidade a adicionar") @RequestParam Integer quantidade,
            @Parameter(description = "Motivo da entrada") @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarEntrada(produtoId, quantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    @PostMapping("/saida")
    @Operation(summary = "Registrar saída de estoque", description = "Remove quantidade do estoque de um produto")
    @ApiResponse(responseCode = "201", description = "Saída registrada com sucesso")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarSaida(
            @Parameter(description = "ID do produto") @RequestParam Long produtoId,
            @Parameter(description = "Quantidade a remover") @RequestParam Integer quantidade,
            @Parameter(description = "Motivo da saída") @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarSaida(produtoId, quantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }

    @PostMapping("/ajuste")
    @Operation(summary = "Registrar ajuste de estoque", description = "Define uma nova quantidade absoluta para o estoque de um produto")
    @ApiResponse(responseCode = "201", description = "Ajuste registrado com sucesso")
    public ResponseEntity<MovimentacaoEstoqueDTO> registrarAjuste(
            @Parameter(description = "ID do produto") @RequestParam Long produtoId,
            @Parameter(description = "Nova quantidade do estoque") @RequestParam Integer novaQuantidade,
            @Parameter(description = "Motivo do ajuste") @RequestParam(required = false) String motivo) {
        MovimentacaoEstoqueDTO movimentacao = movimentacaoService.registrarAjuste(produtoId, novaQuantidade, motivo);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacao);
    }
}
