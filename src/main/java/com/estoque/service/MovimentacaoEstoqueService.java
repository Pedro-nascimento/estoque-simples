package com.estoque.service;

import com.estoque.dto.MovimentacaoEstoqueDTO;
import com.estoque.model.MovimentacaoEstoque;
import com.estoque.model.Produto;
import com.estoque.model.TipoMovimentacao;
import com.estoque.repository.MovimentacaoEstoqueRepository;
import com.estoque.repository.ProdutoRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimentacaoEstoqueService {

    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueDTO> listarTodas() {
        return movimentacaoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovimentacaoEstoqueDTO buscarPorId(Long id) {
        MovimentacaoEstoque movimentacao = movimentacaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Movimentação não encontrada com id: " + id));
        return toDTO(movimentacao);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueDTO> listarPorProduto(Long produtoId) {
        return movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(produtoId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return movimentacaoRepository.findByPeriodo(inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoEstoqueDTO> listarPorTipo(TipoMovimentacao tipo) {
        return movimentacaoRepository.findByTipo(tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MovimentacaoEstoqueDTO registrarEntrada(Long produtoId, Integer quantidade, String motivo) {
        return registrarMovimentacao(produtoId, TipoMovimentacao.ENTRADA, quantidade, motivo);
    }

    @Transactional
    public MovimentacaoEstoqueDTO registrarSaida(Long produtoId, Integer quantidade, String motivo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + produtoId));

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new IllegalStateException("Quantidade em estoque insuficiente. Disponível: " + produto.getQuantidadeEstoque());
        }

        return registrarMovimentacao(produtoId, TipoMovimentacao.SAIDA, quantidade, motivo);
    }

    @Transactional
    public MovimentacaoEstoqueDTO registrarAjuste(Long produtoId, Integer novaQuantidade, String motivo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + produtoId));

        Integer quantidadeAnterior = produto.getQuantidadeEstoque();
        Integer diferenca = Math.abs(novaQuantidade - quantidadeAnterior);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setTipo(TipoMovimentacao.AJUSTE);
        movimentacao.setQuantidade(diferenca);
        movimentacao.setQuantidadeAnterior(quantidadeAnterior);
        movimentacao.setQuantidadePosterior(novaQuantidade);
        movimentacao.setMotivo(motivo);

        produto.setQuantidadeEstoque(novaQuantidade);
        produtoRepository.save(produto);

        movimentacao = movimentacaoRepository.save(movimentacao);
        return toDTO(movimentacao);
    }

    private MovimentacaoEstoqueDTO registrarMovimentacao(Long produtoId, TipoMovimentacao tipo, Integer quantidade, String motivo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com id: " + produtoId));

        Integer quantidadeAnterior = produto.getQuantidadeEstoque();
        Integer quantidadePosterior;

        if (tipo == TipoMovimentacao.ENTRADA) {
            quantidadePosterior = quantidadeAnterior + quantidade;
        } else {
            quantidadePosterior = quantidadeAnterior - quantidade;
        }

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setProduto(produto);
        movimentacao.setTipo(tipo);
        movimentacao.setQuantidade(quantidade);
        movimentacao.setQuantidadeAnterior(quantidadeAnterior);
        movimentacao.setQuantidadePosterior(quantidadePosterior);
        movimentacao.setMotivo(motivo);

        produto.setQuantidadeEstoque(quantidadePosterior);
        produtoRepository.save(produto);

        movimentacao = movimentacaoRepository.save(movimentacao);
        return toDTO(movimentacao);
    }

    private MovimentacaoEstoqueDTO toDTO(MovimentacaoEstoque movimentacao) {
        MovimentacaoEstoqueDTO dto = new MovimentacaoEstoqueDTO();
        dto.setId(movimentacao.getId());
        dto.setProdutoId(movimentacao.getProduto().getId());
        dto.setProdutoNome(movimentacao.getProduto().getNome());
        dto.setTipo(movimentacao.getTipo());
        dto.setQuantidade(movimentacao.getQuantidade());
        dto.setQuantidadeAnterior(movimentacao.getQuantidadeAnterior());
        dto.setQuantidadePosterior(movimentacao.getQuantidadePosterior());
        dto.setMotivo(movimentacao.getMotivo());
        dto.setDataMovimentacao(movimentacao.getDataMovimentacao());
        return dto;
    }
}
