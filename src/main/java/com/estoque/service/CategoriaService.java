package com.estoque.service;

import com.estoque.dto.CategoriaDTO;
import com.estoque.model.Categoria;
import com.estoque.repository.CategoriaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));
        return toDTO(categoria);
    }

    @Transactional
    public CategoriaDTO criar(CategoriaDTO dto) {
        if (categoriaRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());

        categoria = categoriaRepository.save(categoria);
        return toDTO(categoria);
    }

    @Transactional
    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

        if (!categoria.getNome().equals(dto.getNome()) && categoriaRepository.existsByNome(dto.getNome())) {
            throw new IllegalArgumentException("Já existe uma categoria com este nome");
        }

        categoria.setNome(dto.getNome());
        categoria.setDescricao(dto.getDescricao());

        categoria = categoriaRepository.save(categoria);
        return toDTO(categoria);
    }

    @Transactional
    public void deletar(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com id: " + id));

        if (!categoria.getProdutos().isEmpty()) {
            throw new IllegalStateException("Não é possível excluir categoria com produtos associados");
        }

        categoriaRepository.delete(categoria);
    }

    private CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setDescricao(categoria.getDescricao());
        return dto;
    }
}
