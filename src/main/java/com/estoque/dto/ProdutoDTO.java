package com.estoque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    private String descricao;

    private String sku;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    private BigDecimal preco;

    @PositiveOrZero(message = "Preço de custo deve ser zero ou positivo")
    private BigDecimal precoCusto;

    @PositiveOrZero(message = "Quantidade em estoque deve ser zero ou positiva")
    private Integer quantidadeEstoque;

    @PositiveOrZero(message = "Quantidade mínima deve ser zero ou positiva")
    private Integer quantidadeMinima;

    private Boolean ativo;

    private Long categoriaId;

    private String categoriaNome;

    private Boolean estoqueBaixo;
}
