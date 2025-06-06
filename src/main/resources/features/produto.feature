# language: pt

Funcionalidade: Gestão de produtos pelo administrador e estoquista

  Contexto:
    Dado que estou logado como administrador ou estoquista

  Cenário: Listar produtos como administrador
    Quando eu acessar a tela de listagem de produtos
    Então devo ver os produtos ordenados do mais recente para o mais antigo
    E cada produto deve mostrar código, nome, estoque, valor e status
    E deve haver um botão de adicionar novo produto
    E deve haver paginação limitada a 10 produtos por página

  Cenário: Buscar produtos por nome parcial
    Quando eu digitar parte do nome do produto na busca, como "smart"
    Então a lista deve conter todos os produtos que tenham "smart" no nome

  Cenário: Cadastrar um novo produto com imagens
    Quando eu preencher os dados do produto e enviar imagens
    E marcar uma imagem como padrão
    Então o produto e as imagens devem ser salvos no banco de dados

  Cenário: Alterar informações e imagens de um produto
    Dado que um produto já está cadastrado
    Quando eu atualizar as informações e as imagens
    Então as alterações devem ser refletidas no banco de dados

  Cenário: Habilitar e desabilitar produto
    Dado que um produto está ativo
    Quando eu clicar em inativar
    Então deve aparecer uma confirmação
    E ao confirmar, o status do produto deve ser alterado para inativo

  Cenário: Alterar apenas a quantidade em estoque como estoquista
    Quando eu acessar a tela de edição como estoquista
    Então apenas o campo de quantidade deve estar habilitado
    E ao salvar, a nova quantidade deve ser registrada no banco de dados

  Cenário: Visualizar o produto como o cliente vê
    Quando eu acessar a opção de visualizar produto
    Então deve aparecer uma prévia da página com imagens e avaliações
    E o botão de comprar deve estar desabilitado
