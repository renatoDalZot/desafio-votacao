# Votação

## Índice

- [Enunciado do desafio](#enunciado-do-desafio)
  - [Objetivo](#objetivo)
  - [Como proceder](#como-proceder)
    - [Tarefas bônus](#tarefas-bônus)
    - [Tarefa Bônus 1 - Integração com sistemas externos](#tarefa-bônus-1---integração-com-sistemas-externos)
    - [Tarefa Bônus 2 - Performance](#tarefa-bônus-2---performance)
    - [Tarefa Bônus 3 - Versionamento da API](#tarefa-bônus-3---versionamento-da-api)
    - [O que será analisado](#o-que-será-analisado)
    - [Dicas](#dicas)
    - [Anexo 1](#anexo-1)
      - [Introdução](#introdução)
      - [Tipo de tela – FORMULARIO](#tipo-de-tela--formulario)
      - [Tipo de tela – SELECAO](#tipo-de-tela--selecao)
- [desafio-votação](#desafio-votacao)
  - [Requisitos de funcionamento da API](#requisitos)
  - [Endpoints da API](#endpoints-da-api)
    - [POST /v1/pautas](#post-v1pautas)
    - [GET /v1/pautas/buscar/{id}](#get-v1pautasbuscarid)
    - [GET /v1/listarpaginado](#get-v1listarpaginado)
    - [DELETE /v1/pautas/deletar/{id}](#delete-v1pautasdeletarid)
    - [POST /v1/sessao/abrir](#post-v1sessaoabrir)
    - [POST /v1/sessao/reabrir](#post-v1sessaoreabrir)
    - [POST /v1/sessao/votar](#post-v1sessaovotar)
    - [GET /v1/sessao/voto/{idVoto}](#get-v1sessaovotoidvoto)
    - [GET /v1/sessao/voto/{idPauta}/{cpfAssociado}](#get-v1sessaovotoidpautacpfassociado)
    - [POST /v1/sessao/encerrar/{idPauta}](#post-v1sessaoencerraridpauta)
  - [Observações](#observações)


## Objetivo

No cooperativismo, cada associado possui um voto e as decisões são tomadas em assembleias, por votação. Imagine que você deve criar uma solução para dispositivos móveis para gerenciar e participar dessas sessões de votação.
Essa solução deve ser executada na nuvem e promover as seguintes funcionalidades através de uma API REST:

- Cadastrar uma nova pauta
- Abrir uma sessão de votação em uma pauta (a sessão de votação deve ficar aberta por
  um tempo determinado na chamada de abertura ou 1 minuto por default)
- Receber votos dos associados em pautas (os votos são apenas 'Sim'/'Não'. Cada associado
  é identificado por um id único e pode votar apenas uma vez por pauta)
- Contabilizar os votos e dar o resultado da votação na pauta

Para fins de exercício, a segurança das interfaces pode ser abstraída e qualquer chamada para as interfaces pode ser considerada como autorizada. A solução deve ser construída em java, usando Spring-boot, mas os frameworks e bibliotecas são de livre escolha (desde que não infrinja direitos de uso).

É importante que as pautas e os votos sejam persistidos e que não sejam perdidos com o restart da aplicação.

O foco dessa avaliação é a comunicação entre o backend e o aplicativo mobile. Essa comunicação é feita através de mensagens no formato JSON, onde essas mensagens serão interpretadas pelo cliente para montar as telas onde o usuário vai interagir com o sistema. A aplicação cliente não faz parte da avaliação, apenas os componentes do servidor. O formato padrão dessas mensagens será detalhado no anexo 1.

## Como proceder

Por favor, realize o FORK desse repositório e implemente sua solução no FORK em seu repositório GItHub, ao final, notifique da conclusão para que possamos analisar o código implementado.

Lembre de deixar todas as orientações necessárias para executar o seu código.

### Tarefas bônus

- Tarefa Bônus 1 - Integração com sistemas externos
  - Criar uma Facade/Client Fake que retorna aleátoriamente se um CPF recebido é válido ou não.
  - Caso o CPF seja inválido, a API retornará o HTTP Status 404 (Not found). Você pode usar geradores de CPF para gerar CPFs válidos
  - Caso o CPF seja válido, a API retornará se o usuário pode (ABLE_TO_VOTE) ou não pode (UNABLE_TO_VOTE) executar a operação. Essa operação retorna resultados aleatórios, portanto um mesmo CPF pode funcionar em um teste e não funcionar no outro.

```
// CPF Ok para votar
{
    "status": "ABLE_TO_VOTE
}
// CPF Nao Ok para votar - retornar 404 no client tb
{
    "status": "UNABLE_TO_VOTE
}
```

Exemplos de retorno do serviço

### Tarefa Bônus 2 - Performance

- Imagine que sua aplicação possa ser usada em cenários que existam centenas de
  milhares de votos. Ela deve se comportar de maneira performática nesses
  cenários
- Testes de performance são uma boa maneira de garantir e observar como sua
  aplicação se comporta

### Tarefa Bônus 3 - Versionamento da API

○ Como você versionaria a API da sua aplicação? Que estratégia usar?

## O que será analisado

- Simplicidade no design da solução (evitar over engineering)
- Organização do código
- Arquitetura do projeto
- Boas práticas de programação (manutenibilidade, legibilidade etc)
- Possíveis bugs
- Tratamento de erros e exceções
- Explicação breve do porquê das escolhas tomadas durante o desenvolvimento da solução
- Uso de testes automatizados e ferramentas de qualidade
- Limpeza do código
- Documentação do código e da API
- Logs da aplicação
- Mensagens e organização dos commits

## Dicas

- Teste bem sua solução, evite bugs
- Deixe o domínio das URLs de callback passiveis de alteração via configuração, para facilitar
  o teste tanto no emulador, quanto em dispositivos fisicos.
  Observações importantes
- Não inicie o teste sem sanar todas as dúvidas
- Iremos executar a aplicação para testá-la, cuide com qualquer dependência externa e
  deixe claro caso haja instruções especiais para execução do mesmo
  Classificação da informação: Uso Interno

## Anexo 1

### Introdução

A seguir serão detalhados os tipos de tela que o cliente mobile suporta, assim como os tipos de campos disponíveis para a interação do usuário.

### Tipo de tela – FORMULARIO

A tela do tipo FORMULARIO exibe uma coleção de campos (itens) e possui um ou dois botões de ação na parte inferior.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada botão quando o mesmo é acionado. Nos casos onde temos campos de entrada
de dados na tela, os valores informados pelo usuário são adicionados ao corpo da requisição. Abaixo o exemplo da requisição que o aplicativo vai fazer quando o botão “Ação 1” for acionado:

```
POST http://seudominio.com/ACAO1
{
    “campo1”: “valor1”,
    “campo2”: 123,
    “idCampoTexto”: “Texto”,
    “idCampoNumerico: 999
    “idCampoData”: “01/01/2000”
}
```

Obs: o formato da url acima é meramente ilustrativo e não define qualquer padrão de formato.

### Tipo de tela – SELECAO

A tela do tipo SELECAO exibe uma lista de opções para que o usuário.

O aplicativo envia uma requisição POST para a url informada e com o body definido pelo objeto dentro de cada item da lista de seleção, quando o mesmo é acionado, semelhando ao funcionamento dos botões da tela FORMULARIO.

# desafio-votacao

## Requisitos

### Variáveis de ambiente

- MONGODB_URI: URI de conexão com o banco de dados MongoDB

## Endpoints da API

### POST /v1/pautas
- Cria uma nova pauta
- Body:
  ```json
  {
    "titulo": "string",
    "descricao": "string"
  }
  ```
- Response:
  ```json
  {
    "id": "string",
    "titulo": "string",
    "descricao": "string",
    "sessao": {
      "id": "string",
      "inicio": "string",
      "fim": "string"
    },
  "aprovada" : "boolean",
  "votosSim": "number",
  "votosNao": "number",
  "dataApuracao": "string"
  }
  ```

### GET /v1/pautas/buscar/{id}
- Busca uma pauta pelo id
- Response:
  ```json
  {
    "id": "string",
    "titulo": "string",
    "descricao": "string",
    "sessao": {
      "id": "string",
      "inicio": "string",
      "fim": "string"
    },
  "aprovada" : "boolean",
  "votosSim": "number",
  "votosNao": "number",
  "dataApuracao": "string"
  }
  ```

### GET /v1/listarpaginado
- Lista todas as pautas cadastradas
  - Query Params:
    - page: número da página (opcional)
    - size: quantidade de itens por página (opcional)
- Response:
  ```json
  [
    {
      "id": "string",
      "titulo": "string",
      "descricao": "string",
      "sessao": {
        "id": "string",
        "inicio": "string",
        "fim": "string"
      },
    "aprovada" : "boolean",
    "votosSim": "number",
    "votosNao": "number",
    "dataApuracao": "string"
    }
  ]
  ```

### DELETE /v1/pautas/deletar/{id}
  - Deleta uma pauta pelo id
  - Response:
    ```
    204 No Content
    ```

### POST /v1/sessao/abrir
- Abre uma sessão de votação em uma pauta
  - Body:
    ```json
    {
      "idPauta": "string",
      "duracao": 0, 
      "unidade": "string (MINUTES/HOURS/DAYS)"
    }
    ```
    _'duracao' e 'unidade' opcionais. Default: 1 e 'MINUTES', respectivamente._ 

- Response:
  ```json
  {
      "id": "string",
      "titulo": "string",
      "descricao": "string",
      "sessao": {
        "id": "string",
        "inicio": "string",
        "fim": "string"
      },
    "aprovada" : "boolean",
    "votosSim": "number",
    "votosNao": "number",
    "dataApuracao": "string"
    }
  ```

### POST /v1/sessao/reabrir
- Reabre uma sessão de votação em uma pauta (apenas se já tiver sido aberta)
  - Body:
    ```json
    {
      "idPauta": "string",
      "duracao": 0, 
      "unidade": "string (MINUTES/HOURS/DAYS)"
    }
    ```
    _'duracao' e 'unidade' opcionais. Default: 1 e 'MINUTES', respectivamente._
- Response:
  ```json
  {
      "id": "string",
      "titulo": "string",
      "descricao": "string",
      "sessao": {
        "id": "string",
        "inicio": "string",
        "fim": "string"
      },
    "aprovada" : "boolean",
    "votosSim": "number",
    "votosNao": "number",
    "dataApuracao": "string"
    }
  ```
  
### POST /v1/sessao/votar 
- Submete um voto
- Body:
  ```json
  {
    "idPauta": "string",
    "cpfAssociado": "string",
    "opcao": "string (Sim/Não)"
  }
  ```
  
- Response:
  ```json
  {
    	"timestamp": "string",
	    "message": "string",
	     "details": "string"
    }
  ```
  
### GET /v1/sessao/voto/{idVoto}
- Busca um voto pelo id
- Response:
  ```json
  {
    "id": "string",
    "associadoCPF": "string",
    "pautaId": "string",
    "dataHora": "string",
    "opcao": "string (Sim/Não)"
  }
  ```
  
### GET /v1/sessao/voto/{idPauta}/{cpfAssociado}
- Busca um voto pelo id da pauta e cpf do associado 
- Response:
  ```json
  {
    "id": "string",
    "associadoCPF": "string (incluir os pontos e traços)",
    "pautaId": "string",
    "dataHora": "string",
    "opcao": "string (Sim/Não)"
  }
  ```
  
### POST /v1/sessao/encerrar/{idPauta}
- Encerra uma sessão de votação em uma pauta
- Response:
```
200 OK
```



## Observações

HORÁRIO: O sistema utilizará o horário local do servidor em que está hospedado. Como 
não recebe horário do cliente, apenas o tempo de duração da votação, o sistema
considera que o horário de início da votação é o horário atual do servidor. A boa prática 
recomenda que o servidor utilize o fuso UTC, mas para simplificar o desafio, foi
utilizado o horário local do servidor.

COMMITS: Os commits não foram feitos paulatinamente, devido ao fato de este projeto
ter sido escrito em pouco tempo.

TESTES: A API foi testada utilizando testes de integração via postman. [Relatório](https://desafio-votacaoteste.netlify.app/desafio-votacao-2025-01-30-03-08-18-337-0) 



