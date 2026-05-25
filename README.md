<p align="center">
  <h1 align="center">Flow Navigation Toolkit</h1>
  <p align="center"><strong>Modern Navigation Library for Vaadin Flow</strong></p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0-green?logo=springboot" alt="Spring Boot 4" />
  <img src="https://img.shields.io/badge/Vaadin-24%2F25-blue?logo=vaadin" alt="Vaadin 24/25" />
  <img src="https://img.shields.io/badge/Maven%20Central-soon-lightgrey?logo=apachemaven" alt="Maven Central" />
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="MIT License" />
</p>

---

## Visão Geral

O **Flow Navigation Toolkit** é uma biblioteca de navegação moderna e programática para [Vaadin Flow](https://vaadin.com/flow), inspirada nos padrões de frameworks como **Flutter Navigator 2.0**, **React Router** e **Jetpack Navigation (Android)**.

A biblioteca oferece uma camada de abstração sobre o sistema de navegação nativo do Vaadin, trazendo recursos avançados que não existem out-of-the-box:

- **Navigation Stack** — push, pop e replace com histórico gerenciado
- **Navegação Reativa** — Flux de eventos de navegação via Project Reactor
- **Gerenciamento de Estado** — estado compartilhado observável entre views
- **Wizard Flows** — engine para fluxos multi-step com validação por etapa
- **Dialog Navigation** — stack de diálogos com encadeamento e callbacks tipados
- **Breadcrumbs** — trilha de navegação automática baseada no histórico
- **Guards & Interceptors** — proteção de rotas e interceptação de navegação
- **Restore State** — persistência do estado de navegação via VaadinSession
- **Eventos em Tempo Real** — listeners e streams reativos para cada ação de navegação

---

## O Problema do Vaadin Flow

O Vaadin Flow oferece `UI.getCurrent().navigate(...)` como mecanismo principal de navegação. Embora funcional, ele apresenta limitações significativas para aplicações complexas:

### O que o Vaadin **não** possui nativamente

| Recurso | Vaadin Nativo | Flow Navigation Toolkit |
|---|---|---|
| Navigation Stack (push/pop) | ❌ | ✅ |
| Estado compartilhado entre views | ❌ | ✅ |
| Navegação reativa (Flux/Streams) | ❌ | ✅ |
| Wizard Engine | ❌ | ✅ |
| Dialog Stack & Chaining | ❌ | ✅ |
| Breadcrumbs automáticos | ❌ | ✅ |
| Guards programáticos | Parcial (`BeforeEnterObserver`) | ✅ Completo |
| Restore de navegação | ❌ | ✅ |
| Eventos de navegação tipados | ❌ | ✅ |

### Problemas comuns sem a biblioteca

- **Código acoplado** — lógica de navegação espalhada nas views
- **Refresh manual** — sem reatividade ao mudar de rota
- **Navegação inconsistente** — sem stack, sem conceito de "voltar"
- **Sem estado entre views** — obriga o uso de query parameters ou session attributes manuais
- **Wizard complexo** — implementação ad-hoc para cada fluxo multi-step

---

## Instalação

### Módulos disponíveis

```xml
<!-- Core — obrigatório -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Spring Boot Auto-Configuration -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-spring</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Navegação Reativa (Project Reactor) -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-reactive</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Wizard Engine -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-wizard</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Dialog Navigation -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-dialog</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>

<!-- Breadcrumbs -->
<dependency>
    <groupId>io.flownavigation</groupId>
    <artifactId>flow-navigation-breadcrumb</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

### Exemplo completo no `pom.xml`

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.flownavigation</groupId>
            <artifactId>flow-navigation-parent</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>io.flownavigation</groupId>
        <artifactId>flow-navigation-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.flownavigation</groupId>
        <artifactId>flow-navigation-spring</artifactId>
    </dependency>
    <!-- Adicione os módulos adicionais conforme necessário -->
</dependencies>
```

---

## Configuração

### `application.yml` (módulo Spring)

```yaml
flow-navigation:
  enabled: true
  max-stack-size: 50
  restore-navigation: true
```

### Propriedades configuráveis

| Propriedade | Tipo | Padrão | Descrição |
|---|---|---|---|
| `flow-navigation.enabled` | `boolean` | `true` | Habilita/desabilita a auto-configuration |
| `flow-navigation.max-stack-size` | `int` | `50` | Tamanho máximo da navigation stack |
| `flow-navigation.restore-navigation` | `boolean` | `true` | Restaura o estado de navegação da VaadinSession |

---

## Quick Start

### Uso básico do `NavigationManager`

```java
// Injeção via Spring (escopo VaadinSession)
@Autowired
private NavigationManager navigationManager;

// Push — navega para uma nova rota
navigationManager.push("users");

// Push com parâmetros
navigationManager.push("users/detail", Map.of("userId", 42));

// Push com parâmetros e estado
navigationManager.push("users/detail",
    Map.of("userId", 42),
    Map.of("editMode", true));

// Pop — volta para a rota anterior
Optional<NavigationEntry> popped = navigationManager.pop();

// Replace — substitui a rota atual sem adicionar ao histórico
navigationManager.replace("dashboard");

// Consultar rota atual
Optional<NavigationEntry> current = navigationManager.current();

// Consultar rota anterior
Optional<NavigationEntry> previous = navigationManager.previous();
```

### Navegação Reativa

```java
ReactiveNavigationManager reactiveNav = new ReactiveNavigationManager(navigationManager);

// Observar todas as mudanças de rota
reactiveNav.routeChanges()
    .subscribe(route -> System.out.println("Navegou para: " + route));

// Filtrar apenas eventos de push
reactiveNav.pushEvents()
    .subscribe(event -> log.info("Push: {}", event.route()));

// Observar mudanças de estado
reactiveNav.stateChanges()
    .subscribe(event -> log.info("Estado alterado: {}", event.route()));
```

---

## Módulos

### flow-navigation-core

Módulo principal contendo o engine de navegação e gerenciamento de estado.

**Classes principais:**

| Classe | Descrição |
|---|---|
| `NavigationManager` | Gerenciador central de navegação — push, pop, replace, guards, interceptors, listeners |
| `NavigationStack` | Stack de navegação com tamanho máximo configurável (padrão: 50) |
| `NavigationEntry` | Representa uma entrada na stack — rota, parâmetros, estado e timestamp |
| `NavigationContext` | Contexto da navegação atual com rota, rota anterior e estado compartilhado |
| `NavigationEvent` | Evento de navegação tipado (PUSHED, POPPED, REPLACED, CLEARED) |
| `NavigationGuard` | Interface funcional para bloqueio/autorização de navegação |
| `NavigationInterceptor` | Interface para interceptar navegação antes e depois de ocorrer |
| `NavigationStateStore` | Store de estado compartilhado com suporte a listeners de mudança |

**Features:**
- Push/pop/replace com parâmetros e estado
- Guards com resultado ALLOW/DENY/REDIRECT
- Interceptors before/after navigation
- Event listeners para cada ação de navegação
- Estado compartilhado observável via `NavigationStateStore`
- Stack com overflow automático (remove a entrada mais antiga ao exceder o limite)

---

### flow-navigation-spring

Auto-configuration para integração transparente com Spring Boot.

**Classes principais:**

| Classe | Descrição |
|---|---|
| `FlowNavigationAutoConfiguration` | `@Configuration` com beans auto-configurados |
| `FlowNavigationProperties` | `@ConfigurationProperties` para `flow-navigation.*` |
| `VaadinNavigationIntegration` | Integração com VaadinSession para persistência e restauração |

**Features:**
- `NavigationManager` com escopo `vaadin-session` via `ScopedProxyMode.TARGET_CLASS`
- Ativação condicional via `flow-navigation.enabled=true`
- Persistência automática na VaadinSession
- Restauração do estado ao recarregar a página
- Configuração via `application.yml` ou `application.properties`

---

### flow-navigation-reactive

Camada reativa sobre o `NavigationManager` usando Project Reactor.

**Classes principais:**

| Classe | Descrição |
|---|---|
| `ReactiveNavigationManager` | Wrapper reativo com Flux de eventos |
| `ReactiveNavigationEvent` | Interface base para eventos reativos |
| `NavigationPushedEvent` | Evento emitido ao fazer push |
| `NavigationPoppedEvent` | Evento emitido ao fazer pop |
| `NavigationReplacedEvent` | Evento emitido ao fazer replace |
| `NavigationStateChangedEvent` | Evento emitido ao alterar estado compartilhado |

**Features:**
- `Flux<ReactiveNavigationEvent>` para todos os eventos
- Streams filtrados: `pushEvents()`, `popEvents()`, `replaceEvents()`, `stateChanges()`
- `routeChanges()` — Flux de Strings com a rota atual a cada navegação
- Bridge automático entre `NavigationManager` e o `Sinks.Many` do Reactor
- Suporte a backpressure via `onBackpressureBuffer()`
- Método `dispose()` para cleanup do stream

---

### flow-navigation-wizard

Engine completo para fluxos multi-step (wizards).

**Classes principais:**

| Classe | Descrição |
|---|---|
| `WizardFlow` | Define um wizard com steps ordenados (Builder pattern) |
| `WizardStep` | Representa um step individual com rota, validação e flag opcional |
| `WizardNavigator` | Controla a progressão — start, next, previous, finish, cancel |
| `WizardContext` | Contexto compartilhado entre steps com dados globais e por step |
| `WizardEvent` | Eventos do wizard (STARTED, STEP_CHANGED, VALIDATION_FAILED, COMPLETED, CANCELLED) |

**Exemplo de uso:**

```java
// Definir o fluxo
WizardFlow flow = WizardFlow.builder("enrollment")
    .title("Matrícula do Aluno")
    .step(WizardStep.builder("personal-data")
        .title("Dados Pessoais")
        .route("wizard/personal-data")
        .validator(ctx -> ctx.get("name", String.class).isPresent())
        .build())
    .step(WizardStep.builder("address")
        .title("Endereço")
        .route("wizard/address")
        .build())
    .step(WizardStep.builder("payment")
        .title("Pagamento")
        .route("wizard/payment")
        .optional(true)
        .build())
    .step(WizardStep.builder("confirmation")
        .title("Confirmação")
        .route("wizard/confirmation")
        .build())
    .build();

// Navegar pelo wizard
WizardNavigator wizard = new WizardNavigator(flow);
WizardStep firstStep = wizard.start();       // Inicia no primeiro step

wizard.getContext().put("name", "João");      // Salvar dados no contexto
Optional<WizardStep> next = wizard.next();    // Avança para o próximo step

wizard.previous();                            // Volta para o step anterior
boolean finished = wizard.finish();           // Finaliza o wizard

// Monitorar progresso
double progress = wizard.getProgress();       // 0.0 a 1.0
boolean isLast = wizard.isLastStep();
boolean hasNext = wizard.hasNext();

// Ouvir eventos
wizard.addEventListener(event -> {
    switch (event.getType()) {
        case COMPLETED -> log.info("Wizard finalizado!");
        case CANCELLED -> log.info("Wizard cancelado!");
        case VALIDATION_FAILED -> log.warn("Validação falhou no step: {}", event.getStep());
        default -> {}
    }
});
```

---

### flow-navigation-dialog

Gerenciamento de navegação de diálogos com stack, encadeamento e callbacks tipados.

**Classes principais:**

| Classe | Descrição |
|---|---|
| `DialogNavigator` | Gerenciador de dialog stack com fluent API |
| `DialogNavigationEntry<T>` | Entrada na stack de diálogos com parâmetros e handlers |
| `DialogResult<T>` | Resultado tipado do diálogo (CONFIRMED, CANCELLED, DISMISSED) |
| `DialogBuilder<T>` | Builder fluente para configurar e abrir diálogos |

**Exemplo de uso:**

```java
DialogNavigator dialogNav = new DialogNavigator();

// Abrir um diálogo com callback de resultado
dialogNav.<String>dialog(ConfirmDialog.class)
    .withParams(Map.of("message", "Deseja excluir?"))
    .modal(true)
    .onResult(result -> {
        if (result.isConfirmed()) {
            String value = result.getValue().orElse("ok");
            log.info("Confirmado: {}", value);
        } else if (result.isCancelled()) {
            log.info("Cancelado");
        }
    })
    .open();

// Encadear diálogos (chaining)
dialogNav.<UserData>dialog(UserFormDialog.class)
    .onResult(result -> {
        if (result.isConfirmed()) {
            result.getValue().ifPresent(user -> {
                // Abrir próximo diálogo após confirmação
                dialogNav.<Void>dialog(SuccessDialog.class).open();
            });
        }
    })
    .open();

// Fechar o diálogo ativo com resultado
dialogNav.closeWithResult(DialogResult.confirmed("dados-salvos"));

// Cancelar ou dispensar
dialogNav.cancel();
dialogNav.dismiss();

// Verificar estado
boolean hasOpen = dialogNav.hasOpenDialogs();
int count = dialogNav.getDialogCount();
dialogNav.closeAll();
```

---

### flow-navigation-breadcrumb

Geração automática de breadcrumbs baseada no histórico de navegação.

**Classes principais:**

| Classe | Descrição |
|---|---|
| `BreadcrumbTrail` | Gerador e gerenciador da trilha de breadcrumbs |
| `BreadcrumbItem` | Record representando um item (rota, label, parâmetros, ativo) |
| `BreadcrumbConfig` | Configuração de labels estáticos, dinâmicos e rota home |

**Features:**
- Geração automática de trail baseada no histórico do `NavigationManager`
- Labels estáticos e dinâmicos (função que recebe parâmetros e retorna label)
- Label padrão por fallback (capitaliza o último segmento da rota)
- Trail com Home como primeiro item
- Navegação direta para qualquer item do breadcrumb (pop back até a rota)
- Deduplicação automática de rotas na trail

**Exemplo de configuração:**

```java
BreadcrumbConfig config = new BreadcrumbConfig()
    .home("/", "Início")
    .route("/users", "Usuários")
    .route("/users/new", "Novo Usuário")
    .dynamicRoute("/users/detail", params ->
        "Usuário #" + params.getOrDefault("userId", "?"));

BreadcrumbTrail trail = new BreadcrumbTrail(navigationManager, config);

// Obter trail
List<BreadcrumbItem> items = trail.getTrailWithHome();
// [Início > Usuários > Usuário #42]

// Navegar para um item
trail.navigateTo(items.get(1)); // Volta para "Usuários"
```

---

## Gerenciamento de Estado

O `NavigationStateStore` permite compartilhar estado entre views de forma observável.

### Uso básico

```java
NavigationManager nav = new NavigationManager();

// Armazenar estado
nav.state().put("currentUser", user);
nav.state().put("theme", "dark");

// Recuperar estado tipado
Optional<User> user = nav.state().get("currentUser", User.class);
Optional<String> theme = nav.state().get("theme", String.class);

// Verificar e remover
boolean exists = nav.state().contains("currentUser");
nav.state().remove("currentUser");

// Obter todo o estado
Map<String, Object> allState = nav.state().getAll();
```

### Estado observável com listeners

```java
// Registrar listener de mudanças
nav.state().addListener(change -> {
    log.info("Estado alterado: key={}, old={}, new={}",
        change.key(), change.oldValue(), change.newValue());
});

// Qualquer put/remove dispara o listener
nav.state().put("counter", 1);   // listener chamado: key=counter, old=null, new=1
nav.state().put("counter", 2);   // listener chamado: key=counter, old=1, new=2
nav.state().remove("counter");   // listener chamado: key=counter, old=2, new=null
```

### Estado por NavigationEntry

Cada `NavigationEntry` também carrega seu próprio estado:

```java
nav.push("checkout", Map.of("step", 1), Map.of("cartTotal", 99.90));

NavigationEntry current = nav.current().orElseThrow();
Map<String, Object> entryState = current.getState();  // {cartTotal=99.90}
Map<String, Object> entryParams = current.getParams(); // {step=1}
```

---

## Segurança

### Navigation Guards

Guards permitem bloquear ou autorizar navegação antes que ela ocorra:

```java
NavigationManager nav = new NavigationManager();

// Guard de autenticação
nav.beforeEach((context, target) -> {
    if (target.startsWith("admin/") && !isAuthenticated()) {
        return NavigationGuard.GuardResult.DENY;
    }
    return NavigationGuard.GuardResult.ALLOW;
});

// Guard de autorização por role
nav.beforeEach((context, target) -> {
    if (target.equals("settings") && !hasRole("ADMIN")) {
        return NavigationGuard.GuardResult.REDIRECT;
    }
    return NavigationGuard.GuardResult.ALLOW;
});

// Tentativa de navegação — bloqueada se o guard retornar DENY
boolean success = nav.push("admin/dashboard"); // false se não autenticado
```

### Interceptors

Interceptors permitem executar lógica antes e depois da navegação:

```java
nav.addInterceptor(new NavigationInterceptor() {
    @Override
    public void beforeNavigation(String from, String to) {
        log.info("Saindo de '{}' para '{}'", from, to);
        // Analytics, logging, salvar estado, etc.
    }

    @Override
    public void afterNavigation(String from, String to) {
        log.info("Chegou em '{}' vindo de '{}'", to, from);
        // Atualizar breadcrumbs, counters, etc.
    }
});
```

### Event Listeners

```java
nav.addEventListener(event -> {
    switch (event.getType()) {
        case PUSHED -> log.info("Push para: {}", event.getEntry().getRoute());
        case POPPED -> log.info("Pop de: {}", event.getEntry().getRoute());
        case REPLACED -> log.info("Replace com: {}", event.getEntry().getRoute());
        case CLEARED -> log.info("Stack limpa");
    }
});
```

---

## Integração com Vaadin

### Hooks utilizados

O Flow Navigation Toolkit integra com o ciclo de vida do Vaadin através dos seguintes mecanismos:

| Mecanismo Vaadin | Uso na Biblioteca |
|---|---|
| `VaadinSession` | Persistência do `NavigationManager` e estado de navegação |
| `@Scope("vaadin-session")` | Escopo do bean `NavigationManager` no Spring |
| `BeforeEnterObserver` | Base para guards de navegação (verificação de acesso) |
| `AfterNavigationObserver` | Base para interceptors pós-navegação |

### Integração com VaadinSession

A `VaadinNavigationIntegration` gerencia automaticamente:

```java
// Obter o NavigationManager da sessão atual
VaadinNavigationIntegration integration = new VaadinNavigationIntegration(properties);
NavigationManager nav = integration.getNavigationManager();

// Persistir estado (chamado automaticamente pelo módulo Spring)
integration.persistState(VaadinSession.getCurrent(), nav);

// Restaurar estado após page refresh
NavigationManager restored = integration.restoreState(VaadinSession.getCurrent());
```

### Compatibilidade com `@Push`

A biblioteca é compatível com `@Push` do Vaadin. Os eventos reativos do `ReactiveNavigationManager` podem ser consumidos em conjunto com push updates para atualizar a UI em tempo real.

---

## Arquitetura

### Diagrama de componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                         Views (Vaadin)                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌─────────────┐  ┌──────────────┐  ┌───────────────────┐    │
│   │ Navigation  │  │   Dialog     │  │    Wizard         │    │
│   │    API      │  │  Navigator   │  │   Navigator       │    │
│   └──────┬──────┘  └──────┬───────┘  └────────┬──────────┘    │
│          │                │                    │               │
│   ┌──────▼──────────────────────────────────────────────┐      │
│   │              NavigationManager                       │      │
│   │  ┌────────────┐ ┌──────────┐ ┌────────────────┐    │      │
│   │  │   Guards   │ │ Intercep.│ │  Event Listeners│    │      │
│   │  └────────────┘ └──────────┘ └────────────────┘    │      │
│   └──────┬──────────────────┬───────────────────────────┘      │
│          │                  │                                   │
│   ┌──────▼──────┐   ┌──────▼──────────┐                       │
│   │ Navigation  │   │   Navigation    │                       │
│   │   Stack     │   │  State Store    │                       │
│   └──────┬──────┘   └──────┬──────────┘                       │
│          │                  │                                   │
│   ┌──────▼──────────────────▼──────────┐                       │
│   │       Reactive Events (Flux)       │                       │
│   │    ReactiveNavigationManager       │                       │
│   └──────┬─────────────────────────────┘                       │
│          │                                                      │
│   ┌──────▼──────────────────────────────┐                      │
│   │        VaadinSession (Persist)      │                      │
│   └─────────────────────────────────────┘                      │
│                                                                 │
├─────────────────────────────────────────────────────────────────┤
│                   Spring Boot Auto-Configuration                │
└─────────────────────────────────────────────────────────────────┘
```

### Estratégia de navegação

A biblioteca mantém uma **stack própria** (`NavigationStack`) separada do browser history. Isso permite:
- Push/pop/replace sem afetar o histórico do navegador
- Controle total sobre a ordem de navegação
- Stack com limite máximo configurável (overflow automático)

### Estratégia de persistência

O estado de navegação é persistido na **VaadinSession**, garantindo:
- Sobrevivência a page refreshes (F5)
- Isolamento por sessão de usuário
- Cleanup automático quando a sessão expira

---

## Exemplos de Uso

### Navegação básica completa

```java
@Route(value = "users", layout = MainLayout.class)
public class UsersView extends VerticalLayout implements BeforeEnterObserver {

    private final NavigationManager nav;

    public UsersView(NavigationManager nav) {
        this.nav = nav;

        Button detailBtn = new Button("Ver Detalhes", e -> {
            nav.push("users/detail", Map.of("userId", 42));
        });

        Button backBtn = new Button("Voltar", e -> {
            nav.pop();
        });

        add(detailBtn, backBtn);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Sincronizar a stack ao entrar na view
        nav.push("users");
    }
}
```

### Wizard Flow completo

```java
@Route(value = "enrollment", layout = MainLayout.class)
public class EnrollmentView extends VerticalLayout {

    private final WizardNavigator wizard;

    public EnrollmentView() {
        WizardFlow flow = WizardFlow.builder("enrollment")
            .title("Matrícula")
            .step(WizardStep.builder("step1").title("Dados").route("enrollment/data").build())
            .step(WizardStep.builder("step2").title("Documentos").route("enrollment/docs").build())
            .step(WizardStep.builder("step3").title("Pagamento").route("enrollment/payment").build())
            .build();

        this.wizard = new WizardNavigator(flow);

        ProgressBar progress = new ProgressBar();
        Button nextBtn = new Button("Próximo", e -> {
            wizard.next();
            progress.setValue(wizard.getProgress());
        });
        Button prevBtn = new Button("Anterior", e -> {
            wizard.previous();
            progress.setValue(wizard.getProgress());
        });
        Button finishBtn = new Button("Finalizar", e -> {
            if (wizard.finish()) {
                Notification.show("Matrícula concluída!");
            }
        });

        wizard.start();
        progress.setValue(wizard.getProgress());

        add(progress, new HorizontalLayout(prevBtn, nextBtn, finishBtn));
    }
}
```

### Dialog Navigation

```java
DialogNavigator dialogNav = new DialogNavigator();

// Abrir diálogo de confirmação
dialogNav.<Boolean>dialog(ConfirmDeleteDialog.class)
    .withParams(Map.of("entityName", "Aluno", "entityId", 123))
    .modal(true)
    .onResult(result -> {
        if (result.isConfirmed()) {
            deleteEntity(123);
            Notification.show("Removido com sucesso");
        }
    })
    .open();
```

### Navegação Reativa

```java
ReactiveNavigationManager reactiveNav = new ReactiveNavigationManager(nav);

// Atualizar badge com contagem de navegações
reactiveNav.pushEvents()
    .map(event -> event.route())
    .subscribe(route -> {
        analytics.track("page_view", route);
    });

// Reagir a mudanças de estado
reactiveNav.stateChanges()
    .filter(event -> "theme".equals(event.route()))
    .subscribe(event -> updateTheme());
```

### Guards completo

```java
NavigationManager nav = new NavigationManager();

// Guard: requer autenticação
nav.beforeEach((context, target) -> {
    List<String> publicRoutes = List.of("login", "register", "forgot-password");
    if (publicRoutes.contains(target)) {
        return NavigationGuard.GuardResult.ALLOW;
    }
    return isAuthenticated()
        ? NavigationGuard.GuardResult.ALLOW
        : NavigationGuard.GuardResult.DENY;
});

// Guard: role-based access
nav.beforeEach((context, target) -> {
    if (target.startsWith("admin/") && !currentUser.hasRole("ADMIN")) {
        return NavigationGuard.GuardResult.DENY;
    }
    return NavigationGuard.GuardResult.ALLOW;
});

// Interceptor: logging
nav.addInterceptor(new NavigationInterceptor() {
    @Override
    public void beforeNavigation(String from, String to) {
        auditLog.record("NAV", from, to, currentUser.getId());
    }
});
```

---

## Roadmap

| Versão | Features |
|---|---|
| **v1.0** | Push/pop, replace, navigation stack, route state, shared state store, guards, interceptors |
| **v2.0** | Reactive navigation (Flux), dialog navigation, navigation events |
| **v3.0** | Wizard engine, deep linking, restore state, breadcrumbs |
| **v4.0** | Animated transitions, mobile-optimized navigation, collaborative navigation |

---

## Stack Tecnológica

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 21 | Linguagem base (records, sealed classes, pattern matching) |
| Spring Boot | 4.0.6 | Auto-configuration e dependency injection |
| Vaadin | 24.7.3 (compat 24/25) | Framework web — integração com VaadinSession |
| Project Reactor | 3.7.6 | Streams reativos para eventos de navegação |
| Maven | 3.9+ | Build e gerenciamento de dependências |
| SLF4J | 2.0.16 | Logging |
| Jakarta EE | 10 | Especificações Jakarta (Servlet, CDI) |
| JUnit 5 | 5.11.4 | Testes unitários |
| Mockito | 5.15.2 | Mocking para testes |

---

## Contribuição

Contribuições são bem-vindas! Siga os passos abaixo:

### Setup de desenvolvimento

```bash
# Clone o repositório
git clone https://github.com/RafaJMoraes/navigation-flow-vd.git
cd navigation-flow-vd

# Checkout na branch de desenvolvimento
git checkout develop

# Build completo
./mvnw clean install

# Executar testes
./mvnw test

# Executar a aplicação demo
cd flow-navigation-demo
../mvnw spring-boot:run
```

### Processo de Pull Request

1. Faça fork do repositório
2. Crie sua feature branch: `git checkout -b feature/minha-feature`
3. Faça commit das alterações: `git commit -m "feat: descrição da feature"`
4. Push para a branch: `git push origin feature/minha-feature`
5. Abra um Pull Request para a branch `develop`

### Guidelines

- Siga o padrão de código existente
- Adicione testes para novas funcionalidades
- Use [Conventional Commits](https://www.conventionalcommits.org/)
- Documente APIs públicas com Javadoc
- Mantenha compatibilidade com Java 21+

---

## Licença

Este projeto está licenciado sob a **MIT License** — veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## Links Úteis

- [Repositório](https://github.com/RafaJMoraes/navigation-flow-vd)
- [Issues](https://github.com/RafaJMoraes/navigation-flow-vd/issues)
- [Releases](https://github.com/RafaJMoraes/navigation-flow-vd/releases)
- [Vaadin Flow Documentation](https://vaadin.com/docs/latest/flow)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/reference/)
- [Project Reactor](https://projectreactor.io/)

---

<p align="center">
  Desenvolvido por <a href="https://github.com/RafaJMoraes">Rafael Junior de Moraes</a>
</p>
