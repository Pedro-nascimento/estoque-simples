package com.estoque.service;

import com.estoque.dto.CategoriaDTO;
import com.estoque.model.Categoria;
import com.estoque.model.Produto;
import com.estoque.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria;
    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNome("Eletrônicos");
        categoria.setDescricao("Produtos eletrônicos");

        categoriaDTO = new CategoriaDTO();
        categoriaDTO.setNome("Eletrônicos");
        categoriaDTO.setDescricao("Produtos eletrônicos");
    }

    @Test
    @DisplayName("Deve listar todas as categorias")
    void deveListarTodasCategorias() {
        Categoria categoria2 = new Categoria();
        categoria2.setId(2L);
        categoria2.setNome("Roupas");

        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria, categoria2));

        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        assertEquals(2, resultado.size());
        assertEquals("Eletrônicos", resultado.get(0).getNome());
        assertEquals("Roupas", resultado.get(1).getNome());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver categorias")
    void deveRetornarListaVaziaQuandoNaoHouverCategorias() {
        when(categoriaRepository.findAll()).thenReturn(Collections.emptyList());

        List<CategoriaDTO> resultado = categoriaService.listarTodas();

        assertTrue(resultado.isEmpty());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve buscar categoria por ID")
    void deveBuscarCategoriaPorId() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        CategoriaDTO resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Eletrônicos", resultado.getNome());
        verify(categoriaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando categoria não encontrada por ID")
    void deveLancarExcecaoQuandoCategoriaNaoEncontradaPorId() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.buscarPorId(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(categoriaRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() {
        when(categoriaRepository.existsByNome(anyString())).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO resultado = categoriaService.criar(categoriaDTO);

        assertNotNull(resultado);
        assertEquals("Eletrônicos", resultado.getNome());
        verify(categoriaRepository, times(1)).existsByNome("Eletrônicos");
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar categoria com nome duplicado")
    void deveLancarExcecaoAoCriarCategoriaComNomeDuplicado() {
        when(categoriaRepository.existsByNome("Eletrônicos")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.criar(categoriaDTO)
        );

        assertTrue(exception.getMessage().contains("Já existe uma categoria com este nome"));
        verify(categoriaRepository, times(1)).existsByNome("Eletrônicos");
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve atualizar categoria com sucesso")
    void deveAtualizarCategoriaComSucesso() {
        CategoriaDTO novoDTO = new CategoriaDTO();
        novoDTO.setNome("Eletrônicos Atualizados");
        novoDTO.setDescricao("Nova descrição");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNome("Eletrônicos Atualizados")).thenReturn(false);
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO resultado = categoriaService.atualizar(1L, novoDTO);

        assertNotNull(resultado);
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve atualizar categoria mantendo o mesmo nome")
    void deveAtualizarCategoriaComMesmoNome() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        CategoriaDTO resultado = categoriaService.atualizar(1L, categoriaDTO);

        assertNotNull(resultado);
        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, never()).existsByNome(anyString());
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria com nome duplicado")
    void deveLancarExcecaoAoAtualizarCategoriaComNomeDuplicado() {
        CategoriaDTO novoDTO = new CategoriaDTO();
        novoDTO.setNome("Roupas");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.existsByNome("Roupas")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.atualizar(1L, novoDTO)
        );

        assertTrue(exception.getMessage().contains("Já existe uma categoria com este nome"));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar categoria inexistente")
    void deveLancarExcecaoAoAtualizarCategoriaInexistente() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.atualizar(999L, categoriaDTO)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(categoriaRepository, never()).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve deletar categoria com sucesso")
    void deveDeletarCategoriaComSucesso() {
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).delete(categoria);

        assertDoesNotThrow(() -> categoriaService.deletar(1L));

        verify(categoriaRepository, times(1)).findById(1L);
        verify(categoriaRepository, times(1)).delete(categoria);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria com produtos associados")
    void deveLancarExcecaoAoDeletarCategoriaComProdutos() {
        Produto produto = new Produto();
        produto.setId(1L);
        categoria.getProdutos().add(produto);

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> categoriaService.deletar(1L)
        );

        assertTrue(exception.getMessage().contains("produtos associados"));
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar categoria inexistente")
    void deveLancarExcecaoAoDeletarCategoriaInexistente() {
        when(categoriaRepository.findById(anyLong())).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoriaService.deletar(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
        verify(categoriaRepository, never()).delete(any(Categoria.class));
    }
}
