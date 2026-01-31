package com.estoque.service;

import com.estoque.dto.ProdutoDTO;
import com.estoque.model.Categoria;
import com.estoque.model.Produto;
import com.estoque.repository.CategoriaRepository;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;
    private ProdutoDTO produtoDTO;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");

        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Smartphone");
        produto.setDescricao("Smartphone Android");
        produto.setSku("SMART-001");
        produto.setPreco(new BigDecimal("1500.00"));
        produto.setPrecoCusto(new BigDecimal("1000.00"));
        produto.setQuantidadeEstoque(50);
        produto.setQuantidadeMinima(10);
        produto.setAtivo(true);
        produto.setCategoria(categoria);

        produtoDTO = new ProdutoDTO();
        produtoDTO.setNome("Smartphone");
        produtoDTO.setDescricao("Smartphone Android");
        produtoDTO.setSku("SMART-001");
        produtoDTO.setPreco(new BigDecimal("1500.00"));
        produtoDTO.setPrecoCusto(new BigDecimal("1000.00"));
        produtoDTO.setQuantidadeEstoque(50);
        produtoDTO.setQuantidadeMinima(10);
        produtoDTO.setAtivo(true);
        produtoDTO.setCategoriaId(1L);
    }

    @Test
    @DisplayName("Deve listar todos os produtos")
    void deveListarTodosProdutos() {
        Produto produto2 = new Produto();
        produto2.setId(2L);
        produto2.setNome("Tablet");
        produto2.setPreco(new BigDecimal("800.00"));
        produto2.setQuantidadeEstoque(20);
        produto2.setQuantidadeMinima(5);
        produto2.setAtivo(true);

        when(produtoRepository.findAll()).thenReturn(Arrays.asList(produto, produto2));

        List<ProdutoDTO> resultado = produtoService.listarTodos();

        assertEquals(2, resultado.size());
        assertEquals("Smartphone", resultado.get(0).getNome());
        assertEquals("Tablet", resultado.get(1).getNome());
        verify(produtoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar apenas produtos ativos")
    void deveListarProdutosAtivos() {
        when(produtoRepository.findByAtivoTrue()).thenReturn(Collections.singletonList(produto));

        List<ProdutoDTO> resultado = produtoService.listarAtivos();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getAtivo());
        verify(produtoRepository, times(1)).findByAtivoTrue();
    }

    @Test
    @DisplayName("Deve buscar produto por ID")
    void deveBuscarProdutoPorId() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDTO resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Smartphone", resultado.getNome());
        verify(produtoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado por ID")
    void deveLancarExcecaoQuandoProdutoNaoEncontradoPorId() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve buscar produto por SKU")
    void deveBuscarProdutoPorSku() {
        when(produtoRepository.findBySku("SMART-001")).thenReturn(Optional.of(produto));

        ProdutoDTO resultado = produtoService.buscarPorSku("SMART-001");

        assertNotNull(resultado);
        assertEquals("SMART-001", resultado.getSku());
        verify(produtoRepository, times(1)).findBySku("SMART-001");
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado por SKU")
    void deveLancarExcecaoQuandoProdutoNaoEncontradoPorSku() {
        when(produtoRepository.findBySku(anyString())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.buscarPorSku("SKU-INVALIDO")
        );

        assertTrue(exception.getMessage().contains("SKU-INVALIDO"));
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria")
    void deveBuscarProdutosPorCategoria() {
        when(produtoRepository.findByCategoriaId(1L)).thenReturn(Collections.singletonList(produto));

        List<ProdutoDTO> resultado = produtoService.buscarPorCategoria(1L);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getCategoriaId());
        verify(produtoRepository, times(1)).findByCategoriaId(1L);
    }

    @Test
    @DisplayName("Deve buscar produtos por termo")
    void deveBuscarProdutosPorTermo() {
        when(produtoRepository.buscarPorTermo("Smart")).thenReturn(Collections.singletonList(produto));

        List<ProdutoDTO> resultado = produtoService.buscarPorTermo("Smart");

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getNome().contains("Smartphone"));
        verify(produtoRepository, times(1)).buscarPorTermo("Smart");
    }

    @Test
    @DisplayName("Deve listar produtos com estoque baixo")
    void deveListarProdutosComEstoqueBaixo() {
        produto.setQuantidadeEstoque(5);
        produto.setQuantidadeMinima(10);

        when(produtoRepository.findProdutosComEstoqueBaixo()).thenReturn(Collections.singletonList(produto));

        List<ProdutoDTO> resultado = produtoService.listarProdutosComEstoqueBaixo();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getEstoqueBaixo());
        verify(produtoRepository, times(1)).findProdutosComEstoqueBaixo();
    }

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.existsBySku("SMART-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO resultado = produtoService.criar(produtoDTO);

        assertNotNull(resultado);
        assertEquals("Smartphone", resultado.getNome());
        verify(produtoRepository, times(1)).existsBySku("SMART-001");
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve criar produto sem SKU")
    void deveCriarProdutoSemSku() {
        produtoDTO.setSku(null);
        produto.setSku(null);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO resultado = produtoService.criar(produtoDTO);

        assertNotNull(resultado);
        verify(produtoRepository, never()).existsBySku(anyString());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve criar produto sem categoria")
    void deveCriarProdutoSemCategoria() {
        produtoDTO.setCategoriaId(null);
        produto.setCategoria(null);

        when(produtoRepository.existsBySku("SMART-001")).thenReturn(false);
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO resultado = produtoService.criar(produtoDTO);

        assertNotNull(resultado);
        assertNull(resultado.getCategoriaId());
        verify(categoriaRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com SKU duplicado")
    void deveLancarExcecaoAoCriarProdutoComSkuDuplicado() {
        when(produtoRepository.existsBySku("SMART-001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.criar(produtoDTO)
        );

        assertTrue(exception.getMessage().contains("SKU"));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com categoria inexistente")
    void deveLancarExcecaoAoCriarProdutoComCategoriaInexistente() {
        when(produtoRepository.existsBySku("SMART-001")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.criar(produtoDTO)
        );

        assertTrue(exception.getMessage().contains("Categoria"));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto com sucesso")
    void deveAtualizarProdutoComSucesso() {
        ProdutoDTO novoDTO = new ProdutoDTO();
        novoDTO.setNome("Smartphone Atualizado");
        novoDTO.setSku("SMART-002");
        novoDTO.setPreco(new BigDecimal("1600.00"));
        novoDTO.setCategoriaId(1L);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.existsBySku("SMART-002")).thenReturn(false);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO resultado = produtoService.atualizar(1L, novoDTO);

        assertNotNull(resultado);
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve atualizar produto mantendo o mesmo SKU")
    void deveAtualizarProdutoComMesmoSku() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(produtoRepository.save(any(Produto.class))).thenReturn(produto);

        ProdutoDTO resultado = produtoService.atualizar(1L, produtoDTO);

        assertNotNull(resultado);
        verify(produtoRepository, never()).existsBySku(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto com SKU duplicado")
    void deveLancarExcecaoAoAtualizarProdutoComSkuDuplicado() {
        ProdutoDTO novoDTO = new ProdutoDTO();
        novoDTO.setSku("OUTRO-SKU");

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.existsBySku("OUTRO-SKU")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.atualizar(1L, novoDTO)
        );

        assertTrue(exception.getMessage().contains("SKU"));
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto inexistente")
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.atualizar(999L, produtoDTO)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso")
    void deveDeletarProdutoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        doNothing().when(produtoRepository).delete(produto);

        assertDoesNotThrow(() -> produtoService.deletar(1L));

        verify(produtoRepository, times(1)).findById(1L);
        verify(produtoRepository, times(1)).delete(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto inexistente")
    void deveLancarExcecaoAoDeletarProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.deletar(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(produtoRepository, never()).delete(any(Produto.class));
    }

    @Test
    @DisplayName("Deve ativar produto com sucesso")
    void deveAtivarProdutoComSucesso() {
        produto.setAtivo(false);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setAtivo(true);
            return p;
        });

        ProdutoDTO resultado = produtoService.ativar(1L);

        assertNotNull(resultado);
        assertTrue(resultado.getAtivo());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao ativar produto inexistente")
    void deveLancarExcecaoAoAtivarProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.ativar(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve desativar produto com sucesso")
    void deveDesativarProdutoComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> {
            Produto p = invocation.getArgument(0);
            p.setAtivo(false);
            return p;
        });

        ProdutoDTO resultado = produtoService.desativar(1L);

        assertNotNull(resultado);
        assertFalse(resultado.getAtivo());
        verify(produtoRepository, times(1)).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao desativar produto inexistente")
    void deveLancarExcecaoAoDesativarProdutoInexistente() {
        when(produtoRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> produtoService.desativar(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Deve identificar corretamente estoque baixo")
    void deveIdentificarEstoqueBaixo() {
        produto.setQuantidadeEstoque(5);
        produto.setQuantidadeMinima(10);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDTO resultado = produtoService.buscarPorId(1L);

        assertTrue(resultado.getEstoqueBaixo());
    }

    @Test
    @DisplayName("Deve identificar estoque normal")
    void deveIdentificarEstoqueNormal() {
        produto.setQuantidadeEstoque(50);
        produto.setQuantidadeMinima(10);

        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        ProdutoDTO resultado = produtoService.buscarPorId(1L);

        assertFalse(resultado.getEstoqueBaixo());
    }
}
