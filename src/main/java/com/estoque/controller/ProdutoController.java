package com.estoque.controller;

import com.estoque.dto.ProdutoDTO;
import com.estoque.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Operações relacionadas a produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna a lista completa de produtos cadastrados")
    public ResponseEntity<List<ProdutoDTO>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/ativos")
    @Operation(summary = "Listar produtos ativos", description = "Retorna apenas os produtos que estão ativos no sistema")
    public ResponseEntity<List<ProdutoDTO>> listarAtivos() {
        return ResponseEntity.ok(produtoService.listarAtivos());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto específico pelo seu ID")
    public ResponseEntity<ProdutoDTO> buscarPorId(@Parameter(description = "ID do produto") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Buscar produto por SKU", description = "Retorna um produto específico pelo seu código SKU")
    public ResponseEntity<ProdutoDTO> buscarPorSku(@Parameter(description = "Código SKU do produto") @PathVariable String sku) {
        return ResponseEntity.ok(produtoService.buscarPorSku(sku));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Buscar produtos por categoria", description = "Retorna todos os produtos de uma categoria específica")
    public ResponseEntity<List<ProdutoDTO>> buscarPorCategoria(@Parameter(description = "ID da categoria") @PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtoService.buscarPorCategoria(categoriaId));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar produtos por termo", description = "Pesquisa produtos pelo nome ou descrição")
    public ResponseEntity<List<ProdutoDTO>> buscarPorTermo(@Parameter(description = "Termo de busca") @RequestParam String termo) {
        return ResponseEntity.ok(produtoService.buscarPorTermo(termo));
    }

    @GetMapping("/estoque-baixo")
    @Operation(summary = "Listar produtos com estoque baixo", description = "Retorna produtos com quantidade abaixo do estoque mínimo")
    public ResponseEntity<List<ProdutoDTO>> listarProdutosComEstoqueBaixo() {
        return ResponseEntity.ok(produtoService.listarProdutosComEstoqueBaixo());
    }

    @PostMapping
    @Operation(summary = "Criar novo produto", description = "Cadastra um novo produto no sistema")
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    public ResponseEntity<ProdutoDTO> criar(@Valid @RequestBody ProdutoDTO dto) {
        ProdutoDTO produto = produtoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    public ResponseEntity<ProdutoDTO> atualizar(@Parameter(description = "ID do produto") @PathVariable Long id, @Valid @RequestBody ProdutoDTO dto) {
        return ResponseEntity.ok(produtoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar produto", description = "Remove um produto do sistema")
    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso")
    public ResponseEntity<Void> deletar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar produto", description = "Ativa um produto desativado")
    public ResponseEntity<ProdutoDTO> ativar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.ativar(id));
    }

    @PatchMapping("/{id}/desativar")
    @Operation(summary = "Desativar produto", description = "Desativa um produto ativo")
    public ResponseEntity<ProdutoDTO> desativar(@Parameter(description = "ID do produto") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.desativar(id));
    }
}
