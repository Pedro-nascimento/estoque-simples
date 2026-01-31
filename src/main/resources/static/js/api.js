const API_BASE_URL = '/api';

const api = {
    // Categorias
    categorias: {
        listar: () => fetch(`${API_BASE_URL}/categorias`).then(res => res.json()),
        buscarPorId: (id) => fetch(`${API_BASE_URL}/categorias/${id}`).then(res => res.json()),
        criar: (data) => fetch(`${API_BASE_URL}/categorias`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        atualizar: (id, data) => fetch(`${API_BASE_URL}/categorias/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        deletar: (id) => fetch(`${API_BASE_URL}/categorias/${id}`, {
            method: 'DELETE'
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return true;
        })
    },

    // Produtos
    produtos: {
        listar: () => fetch(`${API_BASE_URL}/produtos`).then(res => res.json()),
        listarAtivos: () => fetch(`${API_BASE_URL}/produtos/ativos`).then(res => res.json()),
        buscarPorId: (id) => fetch(`${API_BASE_URL}/produtos/${id}`).then(res => res.json()),
        buscarPorSku: (sku) => fetch(`${API_BASE_URL}/produtos/sku/${sku}`).then(res => res.json()),
        buscarPorCategoria: (categoriaId) => fetch(`${API_BASE_URL}/produtos/categoria/${categoriaId}`).then(res => res.json()),
        buscarPorTermo: (termo) => fetch(`${API_BASE_URL}/produtos/buscar?termo=${encodeURIComponent(termo)}`).then(res => res.json()),
        listarEstoqueBaixo: () => fetch(`${API_BASE_URL}/produtos/estoque-baixo`).then(res => res.json()),
        criar: (data) => fetch(`${API_BASE_URL}/produtos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        atualizar: (id, data) => fetch(`${API_BASE_URL}/produtos/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        deletar: (id) => fetch(`${API_BASE_URL}/produtos/${id}`, {
            method: 'DELETE'
        }).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return true;
        }),
        ativar: (id) => fetch(`${API_BASE_URL}/produtos/${id}/ativar`, {
            method: 'PATCH'
        }).then(res => res.json()),
        desativar: (id) => fetch(`${API_BASE_URL}/produtos/${id}/desativar`, {
            method: 'PATCH'
        }).then(res => res.json())
    },

    // Movimentações
    movimentacoes: {
        listar: () => fetch(`${API_BASE_URL}/movimentacoes`).then(res => res.json()),
        buscarPorId: (id) => fetch(`${API_BASE_URL}/movimentacoes/${id}`).then(res => res.json()),
        listarPorProduto: (produtoId) => fetch(`${API_BASE_URL}/movimentacoes/produto/${produtoId}`).then(res => res.json()),
        listarPorTipo: (tipo) => fetch(`${API_BASE_URL}/movimentacoes/tipo/${tipo}`).then(res => res.json()),
        registrarEntrada: (produtoId, quantidade, motivo) => fetch(
            `${API_BASE_URL}/movimentacoes/entrada?produtoId=${produtoId}&quantidade=${quantidade}&motivo=${encodeURIComponent(motivo || '')}`,
            { method: 'POST' }
        ).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        registrarSaida: (produtoId, quantidade, motivo) => fetch(
            `${API_BASE_URL}/movimentacoes/saida?produtoId=${produtoId}&quantidade=${quantidade}&motivo=${encodeURIComponent(motivo || '')}`,
            { method: 'POST' }
        ).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        }),
        registrarAjuste: (produtoId, novaQuantidade, motivo) => fetch(
            `${API_BASE_URL}/movimentacoes/ajuste?produtoId=${produtoId}&novaQuantidade=${novaQuantidade}&motivo=${encodeURIComponent(motivo || '')}`,
            { method: 'POST' }
        ).then(res => {
            if (!res.ok) return res.json().then(err => Promise.reject(err));
            return res.json();
        })
    }
};
