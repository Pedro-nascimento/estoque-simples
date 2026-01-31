// Estado da aplicação
let produtos = [];
let categorias = [];
let movimentacoes = [];

// Inicialização
document.addEventListener('DOMContentLoaded', () => {
    configurarNavegacao();
    carregarDados();
});

// Navegação entre páginas
function configurarNavegacao() {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const page = e.target.dataset.page;

            document.querySelectorAll('.nav-link').forEach(l => l.classList.remove('active'));
            e.target.classList.add('active');

            document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
            document.getElementById(`${page}-page`).classList.add('active');

            if (page === 'movimentacoes') {
                carregarMovimentacoes();
            }
        });
    });
}

// Carregar dados iniciais
async function carregarDados() {
    try {
        await Promise.all([
            carregarCategorias(),
            carregarProdutos()
        ]);
    } catch (error) {
        showToast('Erro ao carregar dados', 'error');
        console.error(error);
    }
}

// === CATEGORIAS ===

async function carregarCategorias() {
    try {
        categorias = await api.categorias.listar();
        renderizarCategorias();
        atualizarSelectCategorias();
    } catch (error) {
        showToast('Erro ao carregar categorias', 'error');
    }
}

function renderizarCategorias() {
    const tbody = document.querySelector('#tabela-categorias tbody');
    tbody.innerHTML = categorias.map(cat => `
        <tr>
            <td>${cat.nome}</td>
            <td>${cat.descricao || '-'}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-primary" onclick="editarCategoria(${cat.id})">Editar</button>
                    <button class="btn btn-sm btn-danger" onclick="deletarCategoria(${cat.id})">Excluir</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function atualizarSelectCategorias() {
    const selects = ['filtro-categoria', 'produto-categoria'];
    selects.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;

        const currentValue = select.value;
        const isFilter = id === 'filtro-categoria';

        select.innerHTML = isFilter ? '<option value="">Todas as categorias</option>' : '<option value="">Sem categoria</option>';
        select.innerHTML += categorias.map(cat =>
            `<option value="${cat.id}">${cat.nome}</option>`
        ).join('');

        select.value = currentValue;
    });
}

function abrirModalCategoria(id = null) {
    const modal = document.getElementById('modal-categoria');
    const titulo = document.getElementById('modal-categoria-titulo');
    const form = document.getElementById('form-categoria');

    form.reset();
    document.getElementById('categoria-id').value = '';

    if (id) {
        const categoria = categorias.find(c => c.id === id);
        if (categoria) {
            titulo.textContent = 'Editar Categoria';
            document.getElementById('categoria-id').value = categoria.id;
            document.getElementById('categoria-nome').value = categoria.nome;
            document.getElementById('categoria-descricao').value = categoria.descricao || '';
        }
    } else {
        titulo.textContent = 'Nova Categoria';
    }

    modal.classList.add('active');
}

function editarCategoria(id) {
    abrirModalCategoria(id);
}

async function salvarCategoria(event) {
    event.preventDefault();

    const id = document.getElementById('categoria-id').value;
    const data = {
        nome: document.getElementById('categoria-nome').value,
        descricao: document.getElementById('categoria-descricao').value
    };

    try {
        if (id) {
            await api.categorias.atualizar(id, data);
            showToast('Categoria atualizada com sucesso', 'success');
        } else {
            await api.categorias.criar(data);
            showToast('Categoria criada com sucesso', 'success');
        }

        fecharModal('modal-categoria');
        await carregarCategorias();
    } catch (error) {
        showToast(error.message || 'Erro ao salvar categoria', 'error');
    }
}

async function deletarCategoria(id) {
    if (!confirm('Deseja realmente excluir esta categoria?')) return;

    try {
        await api.categorias.deletar(id);
        showToast('Categoria excluída com sucesso', 'success');
        await carregarCategorias();
    } catch (error) {
        showToast(error.message || 'Erro ao excluir categoria', 'error');
    }
}

// === PRODUTOS ===

async function carregarProdutos() {
    try {
        produtos = await api.produtos.listar();
        renderizarProdutos();
        atualizarSelectProdutos();
    } catch (error) {
        showToast('Erro ao carregar produtos', 'error');
    }
}

function renderizarProdutos(lista = produtos) {
    const tbody = document.querySelector('#tabela-produtos tbody');
    tbody.innerHTML = lista.map(prod => `
        <tr>
            <td>${prod.nome}</td>
            <td>${prod.sku || '-'}</td>
            <td>${prod.categoriaNome || '-'}</td>
            <td>R$ ${prod.preco?.toFixed(2) || '0.00'}</td>
            <td class="${prod.estoqueBaixo ? 'estoque-baixo' : ''}">${prod.quantidadeEstoque || 0}</td>
            <td>
                <span class="badge ${prod.ativo ? 'badge-success' : 'badge-danger'}">
                    ${prod.ativo ? 'Ativo' : 'Inativo'}
                </span>
            </td>
            <td>
                <div class="action-buttons">
                    <button class="btn btn-sm btn-primary" onclick="editarProduto(${prod.id})">Editar</button>
                    <button class="btn btn-sm ${prod.ativo ? 'btn-warning' : 'btn-success'}" onclick="toggleAtivoProduto(${prod.id}, ${prod.ativo})">
                        ${prod.ativo ? 'Desativar' : 'Ativar'}
                    </button>
                    <button class="btn btn-sm btn-danger" onclick="deletarProduto(${prod.id})">Excluir</button>
                </div>
            </td>
        </tr>
    `).join('');
}

function atualizarSelectProdutos() {
    const selects = ['filtro-produto-mov', 'movimentacao-produto'];
    selects.forEach(id => {
        const select = document.getElementById(id);
        if (!select) return;

        const currentValue = select.value;
        const isFilter = id === 'filtro-produto-mov';

        select.innerHTML = isFilter ? '<option value="">Todos os produtos</option>' : '';
        select.innerHTML += produtos.filter(p => p.ativo).map(prod =>
            `<option value="${prod.id}">${prod.nome} (Estoque: ${prod.quantidadeEstoque})</option>`
        ).join('');

        select.value = currentValue;
    });
}

function abrirModalProduto(id = null) {
    const modal = document.getElementById('modal-produto');
    const titulo = document.getElementById('modal-produto-titulo');
    const form = document.getElementById('form-produto');

    form.reset();
    document.getElementById('produto-id').value = '';
    document.getElementById('produto-estoque').value = '0';
    document.getElementById('produto-estoque-minimo').value = '0';

    if (id) {
        const produto = produtos.find(p => p.id === id);
        if (produto) {
            titulo.textContent = 'Editar Produto';
            document.getElementById('produto-id').value = produto.id;
            document.getElementById('produto-nome').value = produto.nome;
            document.getElementById('produto-descricao').value = produto.descricao || '';
            document.getElementById('produto-sku').value = produto.sku || '';
            document.getElementById('produto-categoria').value = produto.categoriaId || '';
            document.getElementById('produto-preco').value = produto.preco || '';
            document.getElementById('produto-preco-custo').value = produto.precoCusto || '';
            document.getElementById('produto-estoque').value = produto.quantidadeEstoque || 0;
            document.getElementById('produto-estoque-minimo').value = produto.quantidadeMinima || 0;
        }
    } else {
        titulo.textContent = 'Novo Produto';
    }

    modal.classList.add('active');
}

function editarProduto(id) {
    abrirModalProduto(id);
}

async function salvarProduto(event) {
    event.preventDefault();

    const id = document.getElementById('produto-id').value;
    const categoriaId = document.getElementById('produto-categoria').value;

    const data = {
        nome: document.getElementById('produto-nome').value,
        descricao: document.getElementById('produto-descricao').value,
        sku: document.getElementById('produto-sku').value || null,
        categoriaId: categoriaId ? parseInt(categoriaId) : null,
        preco: parseFloat(document.getElementById('produto-preco').value),
        precoCusto: parseFloat(document.getElementById('produto-preco-custo').value) || null,
        quantidadeEstoque: parseInt(document.getElementById('produto-estoque').value) || 0,
        quantidadeMinima: parseInt(document.getElementById('produto-estoque-minimo').value) || 0
    };

    try {
        if (id) {
            await api.produtos.atualizar(id, data);
            showToast('Produto atualizado com sucesso', 'success');
        } else {
            await api.produtos.criar(data);
            showToast('Produto criado com sucesso', 'success');
        }

        fecharModal('modal-produto');
        await carregarProdutos();
    } catch (error) {
        showToast(error.message || 'Erro ao salvar produto', 'error');
    }
}

async function deletarProduto(id) {
    if (!confirm('Deseja realmente excluir este produto?')) return;

    try {
        await api.produtos.deletar(id);
        showToast('Produto excluído com sucesso', 'success');
        await carregarProdutos();
    } catch (error) {
        showToast(error.message || 'Erro ao excluir produto', 'error');
    }
}

async function toggleAtivoProduto(id, ativo) {
    try {
        if (ativo) {
            await api.produtos.desativar(id);
            showToast('Produto desativado', 'success');
        } else {
            await api.produtos.ativar(id);
            showToast('Produto ativado', 'success');
        }
        await carregarProdutos();
    } catch (error) {
        showToast(error.message || 'Erro ao alterar status', 'error');
    }
}

// Filtros de produtos
async function buscarProdutos() {
    const termo = document.getElementById('busca-produto').value;

    if (termo.length >= 2) {
        try {
            const resultado = await api.produtos.buscarPorTermo(termo);
            renderizarProdutos(resultado);
        } catch (error) {
            console.error(error);
        }
    } else if (termo.length === 0) {
        renderizarProdutos();
    }
}

async function filtrarPorCategoria() {
    const categoriaId = document.getElementById('filtro-categoria').value;

    if (categoriaId) {
        try {
            const resultado = await api.produtos.buscarPorCategoria(categoriaId);
            renderizarProdutos(resultado);
        } catch (error) {
            console.error(error);
        }
    } else {
        renderizarProdutos();
    }
}

async function filtrarEstoqueBaixo() {
    const checked = document.getElementById('filtro-estoque-baixo').checked;

    if (checked) {
        try {
            const resultado = await api.produtos.listarEstoqueBaixo();
            renderizarProdutos(resultado);
        } catch (error) {
            console.error(error);
        }
    } else {
        renderizarProdutos();
    }
}

// === MOVIMENTAÇÕES ===

async function carregarMovimentacoes() {
    try {
        movimentacoes = await api.movimentacoes.listar();
        renderizarMovimentacoes();
    } catch (error) {
        showToast('Erro ao carregar movimentações', 'error');
    }
}

function renderizarMovimentacoes(lista = movimentacoes) {
    const tbody = document.querySelector('#tabela-movimentacoes tbody');
    tbody.innerHTML = lista.map(mov => {
        const tipoClass = {
            'ENTRADA': 'badge-success',
            'SAIDA': 'badge-danger',
            'AJUSTE': 'badge-warning'
        }[mov.tipo] || 'badge-info';

        const data = mov.dataMovimentacao ? new Date(mov.dataMovimentacao).toLocaleString('pt-BR') : '-';

        return `
            <tr>
                <td>${data}</td>
                <td>${mov.produtoNome}</td>
                <td><span class="badge ${tipoClass}">${mov.tipo}</span></td>
                <td>${mov.quantidade}</td>
                <td>${mov.quantidadeAnterior}</td>
                <td>${mov.quantidadePosterior}</td>
                <td>${mov.motivo || '-'}</td>
            </tr>
        `;
    }).join('');
}

function abrirModalMovimentacao(tipo) {
    const modal = document.getElementById('modal-movimentacao');
    const titulo = document.getElementById('modal-movimentacao-titulo');
    const labelQtd = document.getElementById('label-quantidade');
    const inputQtd = document.getElementById('movimentacao-quantidade');

    document.getElementById('form-movimentacao').reset();
    document.getElementById('movimentacao-tipo').value = tipo;

    const titulos = {
        'ENTRADA': 'Registrar Entrada',
        'SAIDA': 'Registrar Saída',
        'AJUSTE': 'Registrar Ajuste'
    };

    titulo.textContent = titulos[tipo];

    if (tipo === 'AJUSTE') {
        labelQtd.textContent = 'Nova Quantidade *';
        inputQtd.min = '0';
    } else {
        labelQtd.textContent = 'Quantidade *';
        inputQtd.min = '1';
    }

    modal.classList.add('active');
}

async function salvarMovimentacao(event) {
    event.preventDefault();

    const tipo = document.getElementById('movimentacao-tipo').value;
    const produtoId = document.getElementById('movimentacao-produto').value;
    const quantidade = parseInt(document.getElementById('movimentacao-quantidade').value);
    const motivo = document.getElementById('movimentacao-motivo').value;

    try {
        if (tipo === 'ENTRADA') {
            await api.movimentacoes.registrarEntrada(produtoId, quantidade, motivo);
            showToast('Entrada registrada com sucesso', 'success');
        } else if (tipo === 'SAIDA') {
            await api.movimentacoes.registrarSaida(produtoId, quantidade, motivo);
            showToast('Saída registrada com sucesso', 'success');
        } else if (tipo === 'AJUSTE') {
            await api.movimentacoes.registrarAjuste(produtoId, quantidade, motivo);
            showToast('Ajuste registrado com sucesso', 'success');
        }

        fecharModal('modal-movimentacao');
        await carregarMovimentacoes();
        await carregarProdutos();
    } catch (error) {
        showToast(error.message || 'Erro ao registrar movimentação', 'error');
    }
}

async function filtrarMovimentacoes() {
    const produtoId = document.getElementById('filtro-produto-mov').value;
    const tipo = document.getElementById('filtro-tipo-mov').value;

    let resultado = movimentacoes;

    if (produtoId) {
        try {
            resultado = await api.movimentacoes.listarPorProduto(produtoId);
        } catch (error) {
            console.error(error);
            return;
        }
    }

    if (tipo) {
        if (!produtoId) {
            try {
                resultado = await api.movimentacoes.listarPorTipo(tipo);
            } catch (error) {
                console.error(error);
                return;
            }
        } else {
            resultado = resultado.filter(m => m.tipo === tipo);
        }
    }

    renderizarMovimentacoes(resultado);
}

// === UTILITÁRIOS ===

function fecharModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;

    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// Fechar modal ao clicar fora
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.classList.remove('active');
        }
    });
});

// Fechar modal com ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal.active').forEach(modal => {
            modal.classList.remove('active');
        });
    }
});
