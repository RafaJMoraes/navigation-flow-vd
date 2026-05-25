# Flow Navigation Toolkit

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

> Biblioteca de navegação baseada em stack para aplicações Vaadin Flow, oferecendo controle programático completo sobre o fluxo de navegação com gerenciamento de estado integrado.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Instalação](#instalação)
- [Operações de Navegação](#operações-de-navegação)
  - [push()](#push)
  - [pop()](#pop)
  - [replace()](#replace)
  - [current()](#current)
  - [previous()](#previous)
  - [clear()](#clear)
- [Casos de Uso Práticos](#casos-de-uso-práticos)
- [Diagramas de Stack](#diagramas-de-stack)
- [Features Planejadas](#features-planejadas)
- [Status de Implementação](#status-de-implementação)
- [Roadmap](#roadmap)
- [Contribuindo](#contribuindo)

---

## Visão Geral

O **Flow Navigation Toolkit** é uma biblioteca que implementa um modelo de navegação baseado em stack (pilha) para aplicações [Vaadin Flow](https://vaadin.com/flow). Diferente da navegação padrão do browser, a biblioteca oferece:

- **Controle programático** sobre a pilha de navegação
- **Gerenciamento de estado** entre transições de tela
- **Navegação com parâmetros** tipados e seguros
- **Histórico navegável** com suporte a operações push/pop/replace

---

## Instalação

```xml
<dependency>
    <groupId>com.ex-rafah-co</groupId>
    <artifactId>navigation-flow-vd</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

---

## Operações de Navegação

### `push()`

Adiciona uma nova rota à stack de navegação, mantendo todo o histórico anterior. É a operação mais comum para avançar em um fluxo de navegação.

#### Sintaxe Básica

```java
// Navegação simples para uma view
NavigationFlow.push(ProductListView.class);
```

#### Navegação com Parâmetros

```java
// Navegação com parâmetro de rota
NavigationFlow.push(ProductDetailView.class, 
    RouteParam.of("productId", 42L));

// Navegação com múltiplos parâmetros
NavigationFlow.push(OrderView.class, 
    RouteParam.of("orderId", 1001L),
    RouteParam.of("tab", "items"));
```

#### Navegação com Estado

```java
// Navegação passando estado complexo entre views
NavigationState state = NavigationState.builder()
    .put("selectedItems", selectedItems)
    .put("filterCriteria", currentFilter)
    .put("returnAction", "refresh")
    .build();

NavigationFlow.push(CheckoutView.class, state);
```

#### Diagrama — Estado da Stack

```
Antes do push(C):          Depois do push(C):
┌─────────────┐            ┌─────────────┐
│             │            │      C      │  ← topo (atual)
│             │            ├─────────────┤
│      B      │  ← topo   │      B      │
├─────────────┤            ├─────────────┤
│      A      │            │      A      │
└─────────────┘            └─────────────┘
```

---

### `pop()`

Remove a rota atual do topo da stack e retorna à rota anterior. Equivale ao "voltar" do browser, mas com controle programático e preservação de estado.

#### Sintaxe Básica

```java
// Voltar para a view anterior
NavigationFlow.pop();
```

#### Navegação com Parâmetros

```java
// Pop com resultado para a view anterior
NavigationFlow.pop(NavigationResult.of("status", "confirmed"));
```

#### Navegação com Estado

```java
// Pop com estado de retorno (útil para fluxos de seleção)
NavigationState result = NavigationState.builder()
    .put("selectedProduct", product)
    .put("quantity", 3)
    .build();

NavigationFlow.pop(result);

// Na view anterior, receber o resultado:
@Override
public void onNavigationResult(NavigationState resultState) {
    Product selected = resultState.get("selectedProduct", Product.class);
    int qty = resultState.get("quantity", Integer.class);
    // processar resultado...
}
```

#### Diagrama — Estado da Stack

```
Antes do pop():            Depois do pop():
┌─────────────┐            ┌─────────────┐
│      C      │  ← topo    │             │
├─────────────┤            │      B      │  ← topo (atual)
│      B      │            ├─────────────┤
├─────────────┤            │      A      │
│      A      │            └─────────────┘
└─────────────┘
```

---

### `replace()`

Substitui a rota atual sem adicionar uma nova entrada ao histórico. A rota substituída é removida permanentemente da stack.

#### Sintaxe Básica

```java
// Substituir a view atual (ex: após login, substituir a tela de login)
NavigationFlow.replace(DashboardView.class);
```

#### Navegação com Parâmetros

```java
// Replace com parâmetros (ex: redirecionar para versão correta)
NavigationFlow.replace(ProductDetailView.class, 
    RouteParam.of("productId", correctProductId));
```

#### Navegação com Estado

```java
// Replace com estado (ex: troca de etapa em wizard sem histórico)
NavigationState wizardState = NavigationState.builder()
    .put("currentStep", 2)
    .put("formData", formData)
    .put("skipBackNavigation", true)
    .build();

NavigationFlow.replace(WizardStep2View.class, wizardState);
```

#### Diagrama — Estado da Stack

```
Antes do replace(D):      Depois do replace(D):
┌─────────────┐            ┌─────────────┐
│      C      │  ← topo    │      D      │  ← topo (atual)
├─────────────┤            ├─────────────┤
│      B      │            │      B      │
├─────────────┤            ├─────────────┤
│      A      │            │      A      │
└─────────────┘            └─────────────┘
         C foi removido e substituído por D
```

---

### `current()`

Retorna a rota atual (topo da stack) sem modificar o estado da navegação.

#### Sintaxe Básica

```java
// Obter a rota atual
NavigationEntry current = NavigationFlow.current();
String viewName = current.getViewClass().getSimpleName();
```

#### Navegação com Parâmetros

```java
// Acessar parâmetros da rota atual
NavigationEntry current = NavigationFlow.current();
Long productId = current.getParam("productId", Long.class);
String tab = current.getParam("tab", String.class);
```

#### Navegação com Estado

```java
// Acessar estado da rota atual
NavigationEntry current = NavigationFlow.current();
NavigationState state = current.getState();

List<Item> items = state.get("selectedItems", List.class);
FilterCriteria filter = state.get("filterCriteria", FilterCriteria.class);
```

#### Diagrama — Estado da Stack

```
current() retorna C:
┌─────────────┐
│      C      │  ← current() retorna esta entrada
├─────────────┤
│      B      │
├─────────────┤
│      A      │
└─────────────┘
        Stack não é modificada
```

---

### `previous()`

Retorna a rota anterior (segunda posição da stack) sem modificar o estado da navegação. Útil para exibir breadcrumbs ou informações de contexto.

#### Sintaxe Básica

```java
// Obter a rota anterior
NavigationEntry prev = NavigationFlow.previous();
if (prev != null) {
    String previousView = prev.getViewClass().getSimpleName();
}
```

#### Navegação com Parâmetros

```java
// Verificar de onde o usuário veio
NavigationEntry prev = NavigationFlow.previous();
if (prev != null && prev.getViewClass() == ProductListView.class) {
    // Mostrar botão "Voltar para lista"
    String category = prev.getParam("category", String.class);
    showBackButton("Voltar para " + category);
}
```

#### Navegação com Estado

```java
// Usar estado da view anterior para contexto
NavigationEntry prev = NavigationFlow.previous();
if (prev != null) {
    NavigationState prevState = prev.getState();
    String searchTerm = prevState.get("searchTerm", String.class);
    // Exibir: "Resultado para: {searchTerm}"
}
```

#### Diagrama — Estado da Stack

```
previous() retorna B:
┌─────────────┐
│      C      │  ← current (topo)
├─────────────┤
│      B      │  ← previous() retorna esta entrada
├─────────────┤
│      A      │
└─────────────┘
        Stack não é modificada
```

---

### `clear()`

Limpa toda a stack de navegação, removendo todo o histórico. Geralmente usado ao fazer logout ou ao reiniciar um fluxo.

#### Sintaxe Básica

```java
// Limpar toda a stack (ex: logout)
NavigationFlow.clear();
```

#### Navegação com Parâmetros

```java
// Limpar e navegar para uma nova raiz
NavigationFlow.clear();
NavigationFlow.push(LoginView.class, 
    RouteParam.of("reason", "session_expired"));
```

#### Navegação com Estado

```java
// Limpar com estado de reset
NavigationFlow.clear();

NavigationState freshState = NavigationState.builder()
    .put("freshStart", true)
    .put("previousUser", currentUser.getName())
    .build();

NavigationFlow.push(HomeView.class, freshState);
```

#### Diagrama — Estado da Stack

```
Antes do clear():          Depois do clear():
┌─────────────┐            ┌─────────────┐
│      C      │  ← topo    │             │
├─────────────┤            │             │
│      B      │            │   (vazia)   │
├─────────────┤            │             │
│      A      │            │             │
└─────────────┘            └─────────────┘
```

---

## Casos de Uso Práticos

### Quando usar `push()` vs `replace()`

| Cenário | Operação | Justificativa |
|---------|----------|---------------|
| Navegar de lista para detalhe | `push()` | Usuário deve poder voltar à lista |
| Após login bem-sucedido | `replace()` | Não faz sentido "voltar" ao login |
| Abrir subpágina/aba | `push()` | Preserva contexto de navegação |
| Redirect após validação | `replace()` | URL corrigida sem histórico |
| Etapas de wizard | `replace()` | Evita voltar para etapas intermediárias |
| Seleção de item em modal | `push()` | Modal deve ser "dismissável" com pop |

### Quando usar `pop()`

| Cenário | Exemplo | Resultado |
|---------|---------|-----------|
| Botão "Voltar" | Detalhe → Lista | Retorna à lista com filtros preservados |
| Confirmação de ação | Checkout → Carrinho | Retorna com resultado da operação |
| Cancelar edição | Form → Detalhe | Descarta alterações e retorna |
| Fechar overlay | Seletor → Tela anterior | Envia item selecionado via resultado |

### Fluxos Típicos de Navegação

#### Fluxo E-commerce

```java
// 1. Usuário navega pela loja
NavigationFlow.push(HomeView.class);
NavigationFlow.push(CategoryView.class, RouteParam.of("cat", "electronics"));
NavigationFlow.push(ProductDetailView.class, RouteParam.of("id", 42L));

// 2. Adiciona ao carrinho e vai para checkout
NavigationFlow.push(CartView.class);
NavigationFlow.push(CheckoutView.class);

// 3. Após pagamento, substitui por confirmação (sem voltar ao checkout)
NavigationFlow.replace(OrderConfirmationView.class, 
    RouteParam.of("orderId", 5001L));

// Stack final: Home → Category → Product → Cart → Confirmation
```

#### Fluxo de Autenticação

```java
// 1. Usuário acessa área protegida
NavigationFlow.push(LoginView.class);

// 2. Login bem-sucedido — substitui login por dashboard
NavigationFlow.replace(DashboardView.class);

// 3. Logout — limpa tudo e volta ao login
NavigationFlow.clear();
NavigationFlow.push(LoginView.class);
```

#### Fluxo de Wizard (Cadastro)

```java
// 1. Inicia wizard
NavigationFlow.push(WizardStep1View.class);

// 2. Avança etapas com replace (não permite "voltar" entre etapas via pop)
NavigationFlow.replace(WizardStep2View.class, state);
NavigationFlow.replace(WizardStep3View.class, state);

// 3. Conclusão — substitui por tela de sucesso
NavigationFlow.replace(WizardCompleteView.class);

// Stack: apenas WizardComplete no topo (etapas intermediárias não ficaram)
```

---

## Diagramas de Stack

### Visão Geral das Operações

```
╔══════════════════════════════════════════════════════════════════════╗
║                    OPERAÇÕES DE NAVEGAÇÃO                           ║
╠══════════════════════════════════════════════════════════════════════╣
║                                                                      ║
║  push(X)          pop()            replace(X)       clear()          ║
║  ┌───┐           ┌───┐            ┌───┐            ┌───┐           ║
║  │ X │ ← novo    │   │ removido   │ X │ ← novo     │   │           ║
║  ├───┤           ├───┤            ├───┤            │   │           ║
║  │ C │           │ B │ ← topo     │ B │            │   │ (vazia)   ║
║  ├───┤           ├───┤            ├───┤            │   │           ║
║  │ B │           │ A │            │ A │            │   │           ║
║  ├───┤           └───┘            └───┘            └───┘           ║
║  │ A │                                                               ║
║  └───┘                                                               ║
║                                                                      ║
║  Adiciona ao     Remove do        Substitui o      Remove todas     ║
║  topo            topo             topo             as entradas       ║
║                                                                      ║
╚══════════════════════════════════════════════════════════════════════╝
```

### Fluxo Completo — Exemplo Visual

```
Estado Inicial     push(A)          push(B)          push(C)
┌─────┐           ┌─────┐          ┌─────┐          ┌─────┐
│vazia│    →      │  A  │   →      │  B  │   →      │  C  │
└─────┘           └─────┘          ├─────┤          ├─────┤
                                   │  A  │          │  B  │
                                   └─────┘          ├─────┤
                                                    │  A  │
                                                    └─────┘

pop()              replace(D)       clear()          push(E)
┌─────┐           ┌─────┐          ┌─────┐          ┌─────┐
│  B  │    →      │  D  │   →      │vazia│   →      │  E  │
├─────┤           ├─────┤          └─────┘          └─────┘
│  A  │           │  A  │
└─────┘           └─────┘
```

---

## Features Planejadas

### Navegação Reativa

Suporte a observação reativa da stack de navegação, permitindo que componentes da UI reajam automaticamente a mudanças de rota.

```java
// Observar mudanças na stack
NavigationFlow.observe(event -> {
    switch (event.getType()) {
        case PUSH -> updateBreadcrumb(event.getEntry());
        case POP -> animateTransitionBack();
        case REPLACE -> updateCurrentIndicator();
        case CLEAR -> resetNavigation();
    }
});

// Binding reativo com componentes Vaadin
NavigationFlow.currentProperty()
    .addValueChangeListener(e -> {
        header.setText(e.getValue().getTitle());
    });
```

### Wizard Flows

Suporte nativo para fluxos de wizard multi-etapas com validação entre etapas e persistência de estado.

```java
// Definir wizard
WizardFlow wizard = WizardFlow.builder()
    .step(PersonalInfoView.class)
    .step(AddressView.class)
    .step(PaymentView.class)
    .step(ConfirmationView.class)
    .onComplete(state -> processRegistration(state))
    .onCancel(() -> NavigationFlow.pop())
    .build();

// Iniciar wizard
NavigationFlow.pushWizard(wizard);
```

### Dialog Navigation

Navegação integrada com diálogos e overlays, tratando-os como entradas na stack.

```java
// Abrir dialog como parte da navegação
NavigationFlow.pushDialog(SelectProductDialog.class, params);

// O dialog pode retornar resultado via pop
// Dentro do dialog:
NavigationFlow.pop(NavigationResult.of("product", selectedProduct));
```

### Breadcrumbs

Geração automática de breadcrumbs baseada na stack de navegação atual.

```java
// Componente de breadcrumb automático
BreadcrumbNav breadcrumb = new BreadcrumbNav();
breadcrumb.setMaxDepth(4);
breadcrumb.setClickable(true);

// Ou manual
List<NavigationEntry> trail = NavigationFlow.getStack();
trail.forEach(entry -> {
    breadcrumb.addItem(entry.getTitle(), () -> {
        NavigationFlow.popTo(entry);
    });
});
```

### Guards / Interceptors

Sistema de guardas e interceptors para controle de acesso e validação antes/depois de cada navegação.

```java
// Guard de autenticação
NavigationFlow.addGuard(new NavigationGuard() {
    @Override
    public GuardResult beforeNavigation(NavigationContext context) {
        if (requiresAuth(context.getTarget()) && !isAuthenticated()) {
            return GuardResult.redirect(LoginView.class);
        }
        return GuardResult.allow();
    }
});

// Interceptor de analytics
NavigationFlow.addInterceptor(new NavigationInterceptor() {
    @Override
    public void afterNavigation(NavigationContext context) {
        analytics.trackPageView(context.getTarget().getRoute());
    }
});

// Guard de formulário não salvo
NavigationFlow.addGuard(new UnsavedChangesGuard() {
    @Override
    public GuardResult beforeLeave(NavigationContext context) {
        if (hasUnsavedChanges()) {
            return GuardResult.confirm("Deseja sair sem salvar?");
        }
        return GuardResult.allow();
    }
});
```

### Gerenciamento de Estado

Sistema completo de gerenciamento de estado integrado à navegação, com escopo por view e compartilhamento entre views.

```java
// Estado com escopo de view (limpo automaticamente ao sair)
NavigationFlow.setState("formData", formData, StateScope.VIEW);

// Estado compartilhado entre views (persiste na sessão)
NavigationFlow.setState("cart", cartItems, StateScope.SESSION);

// Estado com escopo de fluxo (persiste enquanto estiver no fluxo)
NavigationFlow.setState("wizardData", data, StateScope.FLOW);

// Recuperar estado
CartData cart = NavigationFlow.getState("cart", CartData.class);
```

---

## Status de Implementação

| Feature | Status | Versão Alvo |
|---------|--------|-------------|
| `push()` | :yellow_circle: Planejado | 0.2.0 |
| `pop()` | :yellow_circle: Planejado | 0.2.0 |
| `replace()` | :yellow_circle: Planejado | 0.2.0 |
| `current()` | :yellow_circle: Planejado | 0.2.0 |
| `previous()` | :yellow_circle: Planejado | 0.2.0 |
| `clear()` | :yellow_circle: Planejado | 0.2.0 |
| Navegação com Parâmetros | :yellow_circle: Planejado | 0.2.0 |
| Navegação com Estado | :yellow_circle: Planejado | 0.3.0 |
| Navegação Reativa | :red_circle: Futuro | 0.4.0 |
| Wizard Flows | :red_circle: Futuro | 0.5.0 |
| Dialog Navigation | :red_circle: Futuro | 0.5.0 |
| Breadcrumbs | :red_circle: Futuro | 0.6.0 |
| Guards / Interceptors | :red_circle: Futuro | 0.6.0 |
| Gerenciamento de Estado | :red_circle: Futuro | 0.7.0 |

**Legenda:**
- :green_circle: Implementado — disponível e testado
- :yellow_circle: Planejado — design definido, implementação em andamento
- :red_circle: Futuro — conceito definido, implementação não iniciada

---

## Roadmap

```
v0.1.x (atual)    Estrutura base do projeto
       │
       ▼
v0.2.0            Core Navigation (push, pop, replace, current, previous, clear)
       │           + Navegação com parâmetros tipados
       ▼
v0.3.0            State Management
       │           + Navegação com estado entre views
       │           + Escopos de estado (VIEW, SESSION, FLOW)
       ▼
v0.4.0            Reactive Navigation
       │           + Observadores de mudança de rota
       │           + Property binding com componentes Vaadin
       ▼
v0.5.0            Advanced Flows
       │           + Wizard Flows multi-etapas
       │           + Dialog Navigation
       ▼
v0.6.0            Navigation UX
       │           + Breadcrumbs automáticos
       │           + Guards e Interceptors
       │           + Proteção de formulários não salvos
       ▼
v0.7.0            Full State Management
                   + Estado compartilhado entre views
                   + Persistência de estado
                   + Serialização/restauração de stack
```

---

## Contribuindo

Contribuições são bem-vindas! Este projeto está em fase inicial de desenvolvimento.

### Pré-requisitos

- Java 21+
- Maven 3.9+

### Build

```bash
./mvnw clean install
```

### Testes

```bash
./mvnw test
```

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.
