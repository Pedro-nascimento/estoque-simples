package com.estoque.dto;

import com.estoque.model.TipoMovimentacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoEstoqueDTO {

    private Long id;

    @NotNull(message = "Produto é obrigatório")
    private Long produtoId;

    private String produtoNome;

    @NotNull(message = "Tipo de movimentação é obrigatório")
    private TipoMovimentacao tipo;

    @NotNull(message = "Quantidade é obrigatória")
    @Positive(message = "Quantidade deve ser positiva")
    private Integer quantidade;

    private Integer quantidadeAnterior;

    private Integer quantidadePosterior;

    private String motivo;

    private LocalDateTime dataMovimentacao;
}
