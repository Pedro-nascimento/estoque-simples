package com.estoque.service;

import com.estoque.dto.ProdutoDTO;
import com.estoque.model.Categoria;
import com.estoque.model.Produto;
import com.estoque.repository.CategoriaRepository;
import com.estoque.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarTodos() {
        return produtoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarAtivos() {
        return produtoRepository.findByAtivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));
        return toDTO(produto);
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorSku(String sku) {
        Produto produto = produtoRepository.findBySku(sku)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com SKU: " + sku));
        return toDTO(produto);
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarPorCategoria(Long categoriaId) {
        return produtoRepository.findByCategoriaId(categoriaId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> buscarPorTermo(String termo) {
        return produtoRepository.buscarPorTermo(termo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listarProdutosComEstoqueBaixo() {
        return produtoRepository.findProdutosComEstoqueBaixo()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProdutoDTO criar(ProdutoDTO dto) {
        if (dto.getSku() != null && produtoRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Já existe um produto com este SKU");
        }

        Produto produto = new Produto();
        atualizarProduto(produto, dto);

        produto = produtoRepository.save(produto);
        return toDTO(produto);
    }

    @Transactional
    public ProdutoDTO atualizar(Long id, ProdutoDTO dto) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));

        if (dto.getSku() != null && !dto.getSku().equals(produto.getSku()) && produtoRepository.existsBySku(dto.getSku())) {
            throw new IllegalArgumentException("Já existe um produto com este SKU");
        }

        atualizarProduto(produto, dto);

        produto = produtoRepository.save(produto);
        return toDTO(produto);
    }

    @Transactional
    public void deletar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));

        produtoRepository.delete(produto);
    }

    @Transactional
    public ProdutoDTO ativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));

        produto.setAtivo(true);
        produto = produtoRepository.save(produto);
        return toDTO(produto);
    }

    @Transactional
    public ProdutoDTO desativar(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + id));

        produto.setAtivo(false);
        produto = produtoRepository.save(produto);
        return toDTO(produto);
    }

    private void atualizarProduto(Produto produto, ProdutoDTO dto) {
        produto.setNome(dto.getNome());
        produto.setDescricao(dto.getDescricao());
        produto.setSku(dto.getSku());
        produto.setPreco(dto.getPreco());
        produto.setPrecoCusto(dto.getPrecoCusto());

        if (dto.getQuantidadeEstoque() != null) {
            produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        }

        if (dto.getQuantidadeMinima() != null) {
            produto.setQuantidadeMinima(dto.getQuantidadeMinima());
        }

        if (dto.getAtivo() != null) {
            produto.setAtivo(dto.getAtivo());
        }

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + dto.getCategoriaId()));
            produto.setCategoria(categoria);
        } else {
            produto.setCategoria(null);
        }
    }

    private ProdutoDTO toDTO(Produto produto) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(produto.getId());
        dto.setNome(produto.getNome());
        dto.setDescricao(produto.getDescricao());
        dto.setSku(produto.getSku());
        dto.setPreco(produto.getPreco());
        dto.setPrecoCusto(produto.getPrecoCusto());
        dto.setQuantidadeEstoque(produto.getQuantidadeEstoque());
        dto.setQuantidadeMinima(produto.getQuantidadeMinima());
        dto.setAtivo(produto.getAtivo());
        dto.setEstoqueBaixo(produto.isEstoqueBaixo());

        if (produto.getCategoria() != null) {
            dto.setCategoriaId(produto.getCategoria().getId());
            dto.setCategoriaNome(produto.getCategoria().getNome());
        }

        return dto;
    }
}
