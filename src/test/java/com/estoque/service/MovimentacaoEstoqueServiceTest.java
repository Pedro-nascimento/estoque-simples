package com.estoque.service;

import com.estoque.dto.MovimentacaoEstoqueDTO;
import com.estoque.model.MovimentacaoEstoque;
import com.estoque.model.Produto;
import com.estoque.model.TipoMovimentacao;
import com.estoque.repository.MovimentacaoEstoqueRepository;
import com.estoque.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimentacaoEstoqueServiceTest {

    @Mock
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private MovimentacaoEstoqueService movimentacaoService;

    private Produto produto;
    private MovimentacaoEstoque movimentacao;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Smartphone");
        produto.setPreco(new BigDecimal("1500.00"));
        produto.setQuantidadeEstoque(50);
        produto.setQuantidadeMinima(10);
        produto.setAtivo(true);

        movimentacao = new MovimentacaoEstoque();
        movimentacao.setId(1L);
        movimentacao.setProduto(produto);
        movimentacao.setTipo(TipoMovimentacao.ENTRADA);
        movimentacao.setQuantidade(10);
        movimentacao.setQuantidadeAnterior(50);
        movimentacao.setQuantidadePosterior(60);
        movimentacao.setMotivo("Compra de fornecedor");
        movimentacao.setDataMovimentacao(LocalDateTime.now());
    }

    @Test
    @DisplayName("Deve listar todas as movimentações")
    void deveListarTodasMovimentacoes() {
        MovimentacaoEstoque movimentacao2 = new MovimentacaoEstoque();
        movimentacao2.setId(2L);
        movimentacao2.setProduto(produto);
        movimentacao2.setTipo(TipoMovimentacao.SAIDA);
        movimentacao2.setQuantidade(5);
        movimentacao2.setQuantidadeAnterior(60);
        movimentacao2.setQuantidadePosterior(55);
        movimentacao2.setDataMovimentacao(LocalDateTime.now());

        when(movimentacaoRepository.findAll()).thenReturn(Arrays.asList(movimentacao, movimentacao2));

        List<MovimentacaoEstoqueDTO> resultado = movimentacaoService.listarTodas();

        assertEquals(2, resultado.size());
        verify(movimentacaoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar movimentação por ID")
    void deveBuscarMovimentacaoPorId() {
        when(movimentacaoRepository.findById(1L)).thenReturn(Optional.of(movimentacao));

        MovimentacaoEstoqueDTO resultado = movimentacaoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TipoMovimentacao.ENTRADA, resultado.getTipo());
        verify(movimentacaoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando movimentação não encontrada")
    void deveLancarExcecaoQuandoMovimentacaoNaoEncontrada() {
        when(movimentacaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movimentacaoService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve listar movimentações por produto")
    void deveListarMovimentacoesPorProduto() {
        when(movimentacaoRepository.findByProdutoIdOrderByDataMovimentacaoDesc(1L))
                .thenReturn(Collections.singletonList(movimentacao));

        List<MovimentacaoEstoqueDTO> resultado = movimentacaoService.listarPorProduto(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getProdutoId());
        verify(movimentacaoRepository, times(1)).findByProdutoIdOrderByDataMovimentacaoDesc(1L);
    }

    @Test
    @DisplayName("Deve listar movimentações por período")
    void deveListarMovimentacoesPorPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(7);
        LocalDateTime fim = LocalDateTime.now();

        when(movimentacaoRepository.findByPeriodo(inicio, fim))
                .thenReturn(Collections.singletonList(movimentacao));

        List<MovimentacaoEstoqueDTO> resultado = movimentacaoService.listarPorPeriodo(inicio, fim);

        assertEquals(1, resultado.size());
        verify(movimentacaoRepository, times(1)).findByPeriodo(inicio, fim);
    }

    @Test
    @DisplayName("Deve listar movimentações por tipo")
    void deveListarMovimentacoesPorTipo() {
        when(movimentacaoRepository.findByTipo(TipoMovimentacao.ENTRADA))
                .thenReturn(Collections.singletonList(movimentacao));

        List<MovimentacaoEstoqueDTO> resultado = movimentacaoService.listarPorTipo(TipoMovimentacao.ENTRADA);

        assertEquals(1, resultado.size());
        assertEquals(TipoMovimentacao.ENTRADA, resultado.get(0).getTipo());
        verify(movimentacaoRepository, times(1)).findByTipo(TipoMovimentacao.ENTRADA);
    }

    @Test
    @DisplayName("Deve registrar entrada com sucesso")
    void deveRegistrarEntradaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarEntrada(1L, 20, "Compra");

        assertNotNull(resultado);
        assertEquals(TipoMovimentacao.ENTRADA, resultado.getTipo());
        assertEquals(20, resultado.getQuantidade());
        assertEquals(50, resultado.getQuantidadeAnterior());
        assertEquals(70, resultado.getQuantidadePosterior());

        verify(produtoRepository, times(1)).save(any(Produto.class));
        verify(movimentacaoRepository, times(1)).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve atualizar estoque do produto após entrada")
    void deveAtualizarEstoqueAposEntrada() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            assertEquals(70, p.getQuantidadeEstoque());
            return p;
        });
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenReturn(movimentacao);

        movimentacaoService.registrarEntrada(1L, 20, "Compra");

        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar entrada para produto inexistente")
    void deveLancarExcecaoAoRegistrarEntradaParaProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movimentacaoService.registrarEntrada(999L, 10, "Compra")
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve registrar saída com sucesso")
    void deveRegistrarSaidaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarSaida(1L, 10, "Venda");

        assertNotNull(resultado);
        assertEquals(TipoMovimentacao.SAIDA, resultado.getTipo());
        assertEquals(10, resultado.getQuantidade());
        assertEquals(50, resultado.getQuantidadeAnterior());
        assertEquals(40, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve atualizar estoque do produto após saída")
    void deveAtualizarEstoqueAposSaida() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            assertEquals(40, p.getQuantidadeEstoque());
            return p;
        });
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenReturn(movimentacao);

        movimentacaoService.registrarSaida(1L, 10, "Venda");

        verify(produtoRepository, times(2)).findById(1L);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar saída com estoque insuficiente")
    void deveLancarExcecaoAoRegistrarSaidaComEstoqueInsuficiente() {
        produto.setQuantidadeEstoque(5);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> movimentacaoService.registrarSaida(1L, 10, "Venda")
        );

        assertTrue(exception.getMessage().contains("insuficiente"));
        assertTrue(exception.getMessage().contains("5"));
        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar saída para produto inexistente")
    void deveLancarExcecaoAoRegistrarSaidaParaProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movimentacaoService.registrarSaida(999L, 10, "Venda")
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve registrar saída com quantidade exata do estoque")
    void deveRegistrarSaidaComQuantidadeExata() {
        produto.setQuantidadeEstoque(10);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarSaida(1L, 10, "Venda total");

        assertNotNull(resultado);
        assertEquals(10, resultado.getQuantidadeAnterior());
        assertEquals(0, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve registrar ajuste aumentando estoque")
    void deveRegistrarAjusteAumentandoEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarAjuste(1L, 70, "Correção de inventário");

        assertNotNull(resultado);
        assertEquals(TipoMovimentacao.AJUSTE, resultado.getTipo());
        assertEquals(20, resultado.getQuantidade());
        assertEquals(50, resultado.getQuantidadeAnterior());
        assertEquals(70, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve registrar ajuste diminuindo estoque")
    void deveRegistrarAjusteDiminuindoEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarAjuste(1L, 30, "Perda identificada");

        assertNotNull(resultado);
        assertEquals(TipoMovimentacao.AJUSTE, resultado.getTipo());
        assertEquals(20, resultado.getQuantidade());
        assertEquals(50, resultado.getQuantidadeAnterior());
        assertEquals(30, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve registrar ajuste para zero")
    void deveRegistrarAjusteParaZero() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            assertEquals(0, p.getQuantidadeEstoque());
            return p;
        });
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarAjuste(1L, 0, "Zerar estoque");

        assertNotNull(resultado);
        assertEquals(50, resultado.getQuantidade());
        assertEquals(0, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve registrar ajuste com mesma quantidade")
    void deveRegistrarAjusteComMesmaQuantidade() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);
        when(movimentacaoRepository.save(any(MovimentacaoEstoque.class))).thenAnswer(invocation -> {
            MovimentacaoEstoque m = invocation.getArgument(0);
            m.setId(1L);
            return m;
        });

        MovimentacaoEstoqueDTO resultado = movimentacaoService.registrarAjuste(1L, 50, "Conferência");

        assertNotNull(resultado);
        assertEquals(0, resultado.getQuantidade());
        assertEquals(50, resultado.getQuantidadeAnterior());
        assertEquals(50, resultado.getQuantidadePosterior());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar ajuste para produto inexistente")
    void deveLancarExcecaoAoRegistrarAjusteParaProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> movimentacaoService.registrarAjuste(999L, 100, "Ajuste")
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(movimentacaoRepository, never()).save(any(MovimentacaoEstoque.class));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver movimentações")
    void deveRetornarListaVaziaQuandoNaoHouverMovimentacoes() {
        when(movimentacaoRepository.findAll()).thenReturn(Collections.emptyList());

        List<MovimentacaoEstoqueDTO> resultado = movimentacaoService.listarTodas();

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve manter dados do produto na movimentação")
    void deveManterDadosDoProdutoNaMovimentacao() {
        when(movimentacaoRepository.findById(1L)).thenReturn(Optional.of(movimentacao));

        MovimentacaoEstoqueDTO resultado = movimentacaoService.buscarPorId(1L);

        assertEquals(1L, resultado.getProdutoId());
        assertEquals("Smartphone", resultado.getProdutoNome());
    }
}
