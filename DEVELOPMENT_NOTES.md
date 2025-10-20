# Notas de Desenvolvimento - TaskManagerCivil KMP

## Sobre o Projeto
Este é um projeto Kotlin Multiplatform (KMP) com suporte para:
- **WasmJS** (Web)
- **Android**
- **Desktop (JVM)**

Utiliza Jetpack Compose Multiplatform com Material 3 e arquitetura MVVM.

---

## Erros Comuns e Soluções KMP

### 1. API de Coleções - `getOrDefault` não existe
**Erro:**
```
Unresolved reference: getOrDefault
```

**Causa:**
A API de Map do Kotlin stdlib não possui o método `getOrDefault` como no Java.

**Solução:**
Usar o operador Elvis com acesso seguro:
```kotlin
// ❌ ERRADO
val value = map.getOrDefault(key, 0)

// ✅ CORRETO
val value = (map[key] ?: 0)
```

---

### 2. String.format não disponível em commonMain
**Erro:**
```
Unresolved reference: format
```

**Causa:**
`String.format()` é específico da JVM e não está disponível em código comum (commonMain).

**Solução:**
Usar interpolação de strings e conversões manuais:
```kotlin
// ❌ ERRADO
val text = String.format("%.1f%%", value)

// ✅ CORRETO
val text = "${value.toInt()}%"
// ou para mais precisão:
val text = "${(value * 100).roundToInt() / 100.0}%"
```

---

### 3. Caminhos de Importação
**Erro:**
```
Unresolved reference: formatDate
Unresolved reference: formatCurrency
```

**Causa:**
Importações incorretas de funções utilitárias.

**Solução:**
Sempre usar o caminho completo do pacote `utils`:
```kotlin
// ❌ ERRADO
import com.project.taskmanagercivil.presentation.utils.formatDate

// ✅ CORRETO
import com.project.taskmanagercivil.utils.formatDate
import com.project.taskmanagercivil.utils.formatCurrency
```

**Importante:** Certifique-se de que as funções de extensão estejam declaradas no objeto utilitário:
```kotlin
// Em FormatUtils.kt
object FormatUtils {
    fun formatCurrency(value: Double): String { ... }
    fun formatDate(date: LocalDate): String { ... }
}

// Funções de extensão para compatibilidade
fun formatCurrency(value: Double): String = FormatUtils.formatCurrency(value)
fun formatDate(date: LocalDate): String = FormatUtils.formatDate(date)
```

---

### 4. kotlinx.datetime - Operações com Datas
**Erro:**
```
Unresolved reference: plus
Unresolved reference: minus
```

**Causa:**
Faltam imports de operadores do kotlinx.datetime.

**Solução:**
Usar import com wildcard para incluir todos os operadores:
```kotlin
// ❌ ERRADO
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock

// ✅ CORRETO
import kotlinx.datetime.*
```

**Nota:** Há avisos de deprecação para `kotlinx.datetime.Clock` (superseded by `kotlin.time.Clock`), mas isso é apenas um warning e não impede a compilação.

---

### 5. Material 3 Extended Colors
**Erro:**
```
Unresolved reference: getColors
```

**Causa:**
Uso incorreto da API de ExtendedColors.

**Solução:**
Acessar diretamente através do MaterialTheme:
```kotlin
// ❌ ERRADO
val colors = ExtendedColors.getColors(MaterialTheme.colorScheme)

// ✅ CORRETO
val colors = MaterialTheme.extendedColors
```

---

### 6. Imports Faltantes em Compose
**Erro:**
```
Unresolved reference: Dp
```

**Causa:**
Falta de import de tipos básicos do Compose.

**Solução:**
Sempre verificar imports necessários para tipos básicos:
```kotlin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
```

---

## Boas Práticas KMP

### 1. Formatação de Valores
Para formatar valores monetários ou numéricos que funcionem em todas as plataformas:
```kotlin
// Para moeda
fun formatCurrency(value: Double): String {
    val millions = value / 1_000_000
    return when {
        millions >= 1 -> {
            val formatted = (millions * 100).roundToInt() / 100.0
            "R$ $formatted M"
        }
        else -> {
            val thousands = (value / 1000).roundToInt()
            "R$ $thousands mil"
        }
    }
}

// Para datas
fun formatDate(date: LocalDate): String {
    val day = date.dayOfMonth.toString().padStart(2, '0')
    val month = date.monthNumber.toString().padStart(2, '0')
    val year = date.year
    return "$day/$month/$year"
}
```

### 2. Acesso a Maps
Sempre use o operador Elvis para valores padrão:
```kotlin
val count = statusMap[TaskStatus.TODO] ?: 0
```

### 3. Cálculos com Datas
Use kotlinx.datetime para operações cross-platform:
```kotlin
import kotlinx.datetime.*

val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
val futureDate = now.plus(DatePeriod(days = 7))
val pastDate = now.minus(DatePeriod(months = 6))
```

### 4. StateFlow e ViewModel
Sempre inicialize StateFlow com valores padrão seguros:
```kotlin
private val _uiState = MutableStateFlow(DashboardUiState())
val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
```

---

## Avisos Comuns (Warnings)

### Deprecation: kotlinx.datetime.Clock
**Warning:**
```
'Clock' is superseded by 'kotlin.time.Clock'
```

**Status:** Apenas aviso, não impede compilação.

**Ação:** Pode ser ignorado por enquanto. Atualizar quando a migração for necessária.

---

## Checklist Antes de Commit

- [ ] Executar `./gradlew composeApp:build` para verificar compilação
- [ ] Verificar que não há erros de compilação (warnings são aceitáveis)
- [ ] Confirmar que imports estão corretos (especialmente `utils`)
- [ ] Garantir que código funciona em commonMain (sem APIs específicas de plataforma)
- [ ] Testar navegação e fluxo de dados se aplicável
- [ ] Commit semântico com autor "Ulisses Antonelli"

---

## Estrutura do Projeto

```
composeApp/src/commonMain/kotlin/com/project/taskmanagercivil/
├── data/
│   ├── models/          # Data classes para API/DB
│   └── repository/      # Implementações de repositórios
├── domain/
│   ├── models/          # Domain models (Task, Project, etc.)
│   └── repository/      # Interfaces de repositórios
├── presentation/
│   ├── components/      # Componentes reutilizáveis
│   ├── navigation/      # Sistema de navegação
│   ├── screens/         # Telas organizadas por feature
│   ├── theme/           # Tema Material 3
│   └── ViewModelFactory.kt
└── utils/               # Utilitários (formatação, etc.)
```

---

## Histórico de Implementações

### Dashboard (2024-10-18)
- Criação completa da tela de Dashboard
- Componentes: StatusSummaryCard, ProgressChart, CriticalDeadlinesCard, FinancialIndicatorsCard, MonthlyProjectChart
- Integração com dados mock existentes
- Navegação contextual implementada
- **Erros corrigidos:** getOrDefault, String.format, imports, ExtendedColors API
