# TaskManager CIVIL

Sistema de gerenciamento de projetos de construção civil desenvolvido com Kotlin Multiplatform (KMP) e Compose Multiplatform.

## 📋 Sobre o Projeto

O TaskManager CIVIL é uma aplicação multiplataforma para gestão completa de projetos de construção civil, oferecendo funcionalidades para gerenciamento de:

- **Projetos**: Controle completo de obras com cronogramas, progresso e status
- **Tarefas**: Organização de atividades com responsáveis, prazos e prioridades
- **Colaboradores**: Gestão da equipe com informações detalhadas, projetos atribuídos e histórico
- **Times**: Organização por setores/departamentos (Arquitetura, Estruturas, Hidráulica, Elétrica, etc.)
- **Documentos**: Sistema completo de gestão documental com:
  - Controle de versão (revisões R00, R01, R02...)
  - Fluxo de aprovação em 3 níveis (Projetista → Coordenador → Gerente)
  - Categorização por tipo, disciplina e fase do projeto
  - Identificação única (padrão OBRA-DISC-SEQ-REV)
  - Suporte a múltiplos tipos: Plantas, Documentos Técnicos, Legais, Financeiros, Qualidade e Obra

## 👥 Equipe de Desenvolvimento

**Projeto Interdisciplinar - 6º Semestre**

**Integrantes:**

- 1º: Breno Ribeiro Souza
- 2º: Daniele Capristano Pereira
- 3º: Gustavo dos Anjos Campos
- 4º: Lucas Trindade de Andrade
- 5º: Reryson Santos de Andrade
- 6º: Ulisses da Silva Antonelli

## 🛠️ Tecnologias Utilizadas

- **Kotlin Multiplatform (KMP)**: Framework para desenvolvimento multiplataforma
- **Compose Multiplatform**: UI moderna e declarativa
- **Material Design 3**: Sistema de design moderno
- **Kotlin Coroutines & Flow**: Programação assíncrona e reativa
- **kotlinx.datetime**: Manipulação de datas multiplataforma
- **Navigation Compose**: Navegação entre telas

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **MVVM (Model-View-ViewModel)**:

```
composeApp/
├── domain/
│   ├── models/          # Entidades e modelos de dados
│   └── repository/      # Interfaces de repositórios
├── data/
│   ├── repository/      # Implementações de repositórios
│   └── MockData.kt      # Dados mockados para desenvolvimento
├── presentation/
│   ├── screens/         # Telas organizadas por feature
│   │   ├── projects/
│   │   ├── tasks/
│   │   ├── employees/
│   │   ├── teams/
│   │   └── documents/
│   ├── navigation/      # Sistema de navegação
│   └── ViewModelFactory.kt
└── utils/               # Utilitários e helpers
```

## 🚀 Como Executar

### Pré-requisitos

- **JDK 17** ou superior
- **Android Studio** (para Android)
- **Xcode** (para iOS - apenas em macOS)
- **Gradle 8.0+**

### Plataformas Suportadas

#### 🌐 Web (wasmJs)

```bash
# Desenvolvimento com hot-reload
./gradlew wasmJsBrowserDevelopmentRun

# Build de produção
./gradlew wasmJsBrowserDistribution
```

Os arquivos compilados estarão em: `composeApp/build/dist/wasmJs/productionExecutable/`

#### 🤖 Android

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

Ou abra o projeto no Android Studio e execute diretamente.

#### 🖥️ Desktop (JVM)

```bash
# Executar aplicação desktop
./gradlew run

# Criar executável
./gradlew packageDistributionForCurrentOS
```

## 📦 Build de Produção

### Web (wasmJs)

```bash
./gradlew wasmJsBrowserDistribution
```

Saída: `composeApp/build/dist/wasmJs/productionExecutable/`

### Android APK

```bash
./gradlew assembleRelease
```

Saída: `composeApp/build/outputs/apk/release/`

### Desktop

```bash
./gradlew packageDistributionForCurrentOS
```

Saída: `composeApp/build/compose/binaries/main/`

## 🎯 Funcionalidades Principais

### Gestão de Projetos
- Criação e edição de projetos
- Acompanhamento de progresso
- Controle de cronograma e orçamento
- Status e fases do projeto

### Gestão de Tarefas
- Criação de tarefas por projeto
- Atribuição de responsáveis
- Controle de prioridades e status
- Filtros e ordenação

### Gestão de Colaboradores
- Cadastro completo de colaboradores
- Visualização de projetos atribuídos
- Filtros por data de admissão, demissão e ordem alfabética
- Histórico e status ativo/inativo

### Gestão de Times
- Organização por departamentos (12 setores)
- Visualização de membros e líderes
- Projetos atribuídos por time
- Badges coloridos por setor

### Gestão de Documentos
- Sistema de código único (OBRA-DISC-SEQ-REV)
- Controle de versão com histórico completo
- Fluxo de aprovação em 3 estágios
- Filtros avançados (categoria, tipo, status, fase, disciplina)
- Suporte a múltiplos tipos de documentos
- Tags e descrições personalizadas

## 🔧 Estrutura de Dados

### Modelos Principais

- **Project**: Projetos de construção
- **Task**: Tarefas e atividades
- **Employee**: Colaboradores
- **Team**: Times e departamentos
- **Document**: Documentos com versionamento
- **DocumentVersion**: Histórico de revisões
- **DocumentApproval**: Fluxo de aprovações

## 📝 Convenções de Commit

O projeto utiliza commits semânticos em português:

- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `refactor:` Refatoração de código
- `docs:` Documentação
- `style:` Formatação
- `test:` Testes
- `chore:` Manutenção

## 📄 Licença

Este projeto foi desenvolvido como parte do Projeto Interdisciplinar do 6º semestre.

## 🤝 Contribuindo

Este é um projeto acadêmico. Para sugestões ou melhorias, entre em contato com a equipe de desenvolvimento.

---

**Desenvolvido pela equipe do 6º Semestre**
