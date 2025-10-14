package com.project.taskmanagercivil.data

import com.project.taskmanagercivil.domain.models.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class MockData {
    val users = listOf(
    User(
        "1",
        "João Silva",
        "Engenheiro Civil",
        "joao.silva@construtora.com"),
    User(
        "2",
        "Maria Santos",
        "Arquiteta",
        "maria.santos@construtora.com"),
    User(
        "3",
        "Pedro Costa",
        "Mestre de Obras",
        "pedro.costa@construtora.com"),
    User(
        "4",
        "Ana Oliveira",
        "Técnica em Segurança",
        "ana.oliveira@construtora.com"),
    User(
        "5",
        "Carlos Ferreira",
        "Encarregado",
        "carlos.ferreira@construtora.com")
    )

    val employees = listOf(
        Employee(
            id = "1",
            fullName = "João Silva",
            role = "Engenheiro Civil",
            email = "joao.silva@construtora.com",
            phone = "(11) 98765-4321",
            cpf = "123.456.789-00",
            hireDate = LocalDate(2020, 3, 15),
            terminationDate = null,
            projectIds = listOf("1", "2"), // Vista Verde, Plaza
            isActive = true
        ),
        Employee(
            id = "2",
            fullName = "Maria Santos",
            role = "Arquiteta",
            email = "maria.santos@construtora.com",
            phone = "(11) 98765-4322",
            cpf = "234.567.890-11",
            hireDate = LocalDate(2019, 6, 10),
            terminationDate = null,
            projectIds = listOf("1"), // Vista Verde
            isActive = true
        ),
        Employee(
            id = "3",
            fullName = "Pedro Costa",
            role = "Mestre de Obras",
            email = "pedro.costa@construtora.com",
            phone = "(11) 98765-4323",
            cpf = "345.678.901-22",
            hireDate = LocalDate(2018, 1, 20),
            terminationDate = null,
            projectIds = listOf("1", "2", "5"), // Vista Verde, Plaza, Viaduto
            isActive = true
        ),
        Employee(
            id = "4",
            fullName = "Ana Oliveira",
            role = "Técnica em Segurança do Trabalho",
            email = "ana.oliveira@construtora.com",
            phone = "(11) 98765-4324",
            cpf = "456.789.012-33",
            hireDate = LocalDate(2021, 8, 5),
            terminationDate = null,
            projectIds = listOf("1", "3", "4"), // Vista Verde, Ponte, Hospital
            isActive = true
        ),
        Employee(
            id = "5",
            fullName = "Carlos Ferreira",
            role = "Encarregado de Instalações",
            email = "carlos.ferreira@construtora.com",
            phone = "(11) 98765-4325",
            cpf = "567.890.123-44",
            hireDate = LocalDate(2019, 11, 12),
            terminationDate = null,
            projectIds = listOf("1", "4"), // Vista Verde, Hospital
            isActive = true
        ),
        Employee(
            id = "6",
            fullName = "Juliana Almeida",
            role = "Engenheira de Estruturas",
            email = "juliana.almeida@construtora.com",
            phone = "(11) 98765-4326",
            cpf = "678.901.234-55",
            hireDate = LocalDate(2020, 5, 18),
            terminationDate = null,
            projectIds = listOf("2", "3"), // Plaza, Ponte
            isActive = true
        ),
        Employee(
            id = "7",
            fullName = "Roberto Mendes",
            role = "Topógrafo",
            email = "roberto.mendes@construtora.com",
            phone = "(11) 98765-4327",
            cpf = "789.012.345-66",
            hireDate = LocalDate(2017, 9, 22),
            terminationDate = null,
            projectIds = listOf("3", "6"), // Ponte, Viaduto
            isActive = true
        ),
        Employee(
            id = "8",
            fullName = "Fernanda Lima",
            role = "Administradora de Obras",
            email = "fernanda.lima@construtora.com",
            phone = "(11) 98765-4328",
            cpf = "890.123.456-77",
            hireDate = LocalDate(2021, 2, 14),
            terminationDate = null,
            projectIds = listOf("4", "5"), // Hospital, Condomínio
            isActive = true
        ),
        Employee(
            id = "9",
            fullName = "Ricardo Santos",
            role = "Eletricista",
            email = "ricardo.santos@construtora.com",
            phone = "(11) 98765-4329",
            cpf = "901.234.567-88",
            hireDate = LocalDate(2019, 4, 8),
            terminationDate = null,
            projectIds = listOf("1", "5"), // Vista Verde, Condomínio
            isActive = true
        ),
        Employee(
            id = "10",
            fullName = "Paula Rodrigues",
            role = "Compradora",
            email = "paula.rodrigues@construtora.com",
            phone = "(11) 98765-4330",
            cpf = "012.345.678-99",
            hireDate = LocalDate(2020, 10, 25),
            terminationDate = null,
            projectIds = listOf("1", "2", "3", "4"), // Múltiplos projetos
            isActive = true
        ),
        Employee(
            id = "11",
            fullName = "Marcos Pereira",
            role = "Pedreiro",
            email = "marcos.pereira@construtora.com",
            phone = "(11) 98765-4331",
            cpf = "111.222.333-44",
            hireDate = LocalDate(2016, 7, 30),
            terminationDate = LocalDate(2024, 1, 15), // Demitido
            projectIds = listOf("1"),
            isActive = false
        ),
        Employee(
            id = "12",
            fullName = "Luciana Souza",
            role = "Engenheira Ambiental",
            email = "luciana.souza@construtora.com",
            phone = "(11) 98765-4332",
            cpf = "222.333.444-55",
            hireDate = LocalDate(2021, 12, 1),
            terminationDate = null,
            projectIds = listOf("3", "6"), // Ponte, Viaduto
            isActive = true
        )
    )

    val projects = listOf(
        Project(
            "1",
            "Edifício Residencial Vista Verde",
            "Construção de edifício residencial com 20 andares",
            "Incorporadora ABC",
            LocalDate(2024, 1, 15),
            LocalDate(2025, 6, 30),
            15000000.0,
            "São Paulo - SP"
        ),
        Project(
            "2",
            "Centro Comercial Plaza",
            "Reforma e ampliação de centro comercial",
            "Grupo Shopping XYZ",
            LocalDate(2024, 3, 1),
            LocalDate(2024, 12, 15),
            8500000.0,
            "Campinas - SP"
        ),
        Project(
            "3",
            "Ponte Metropolitana Norte",
            "Construção de ponte estaiada com 850m de extensão",
            "Consórcio Infraestrutura BR",
            LocalDate(2024, 2, 1),
            LocalDate(2026, 8, 30),
            45000000.0,
            "Guarulhos - SP"
        ),
        Project(
            "4",
            "Hospital Regional Sul",
            "Construção de hospital com 200 leitos",
            "Governo do Estado",
            LocalDate(2024, 4, 1),
            LocalDate(2025, 12, 31),
            28000000.0,
            "Santos - SP"
        ),
        Project(
            "5",
            "Condomínio Parque das Flores",
            "Construção de condomínio residencial com 8 torres",
            "Construtora Horizonte",
            LocalDate(2024, 1, 10),
            LocalDate(2025, 10, 15),
            22000000.0,
            "Sorocaba - SP"
        ),
        Project(
            "6",
            "Viaduto Interligação Leste",
            "Construção de viaduto com 3 níveis",
            "Prefeitura Municipal",
            LocalDate(2024, 5, 1),
            LocalDate(2025, 3, 30),
            12000000.0,
            "São Bernardo - SP"
        )
    )

    val tasks = listOf(
        Task(
            "1",
            "Fundação - Bloco A",
            "Execução da fundação do Bloco A com estacas de 20m",
            TaskStatus.COMPLETED,
            TaskPriority.CRITICAL,
            users[2],
            projects[0],
            LocalDate(2024, 1, 20),
            LocalDate(2024, 2, 28),
            1.0f,
            listOf("fundação", "estrutura", "bloco-a")
        ),
        Task(
            "2",
            "Estrutura - 1º ao 5º Andar",
            "Concretagem da estrutura dos primeiros 5 andares",
            TaskStatus.IN_PROGRESS,
            TaskPriority.HIGH,
            users[0],
            projects[0],
            LocalDate(2024, 3, 1),
            LocalDate(2024, 5, 15),
            0.65f,
            listOf("estrutura", "concreto"),
            listOf("1")
        ),
        Task(
            "3",
            "Instalações Elétricas - Torre Sul",
            "Instalação da infraestrutura elétrica da torre sul",
            TaskStatus.IN_PROGRESS,
            TaskPriority.MEDIUM,
            users[4],
            projects[0],
            LocalDate(2024, 4, 1),
            LocalDate(2024, 6, 30),
            0.3f,
            listOf("elétrica", "instalações")
        ),
        Task(
            "4",
            "Aprovação do Projeto de PPCI",
            "Submissão e acompanhamento da aprovação do PPCI",
            TaskStatus.IN_REVIEW,
            TaskPriority.HIGH,
            users[3],
            projects[0],
            LocalDate(2024, 2, 1),
            LocalDate(2024, 3, 15),
            0.8f,
            listOf("documentação", "segurança", "ppci")
        ),
        Task(
            "5",
            "Demolição - Área Antiga",
            "Demolição controlada da estrutura antiga",
            TaskStatus.COMPLETED,
            TaskPriority.HIGH,
            users[2],
            projects[1],
            LocalDate(2024, 3, 1),
            LocalDate(2024, 3, 20),
            1.0f,
            listOf("demolição", "preparação")
        ),
        Task(
            "6",
            "Reforço Estrutural - Pilares",
            "Reforço dos pilares principais do shopping",
            TaskStatus.IN_PROGRESS,
            TaskPriority.CRITICAL,
            users[0],
            projects[1],
            LocalDate(2024, 4, 1),
            LocalDate(2024, 5, 30),
            0.45f,
            listOf("estrutura", "reforço"),
            listOf("5")
        ),
        Task(
            "7",
            "Nova Fachada - Design",
            "Projeto e detalhamento da nova fachada",
            TaskStatus.TODO,
            TaskPriority.MEDIUM,
            users[1],
            projects[1],
            LocalDate(2024, 5, 1),
            LocalDate(2024, 6, 15),
            0.0f,
            listOf("arquitetura", "fachada", "design")
        ),
        Task(
            "8",
            "Sistema de Ar Condicionado",
            "Instalação do novo sistema de climatização",
            TaskStatus.BLOCKED,
            TaskPriority.MEDIUM,
            users[4],
            projects[1],
            LocalDate(2024, 6, 1),
            LocalDate(2024, 8, 30),
            0.15f,
            listOf("hvac", "instalações"),
            listOf("6")
        ),
        Task(
            "9",
            "Fundação - Pilares Centrais",
            "Execução de fundação profunda para pilares da ponte",
            TaskStatus.IN_PROGRESS,
            TaskPriority.CRITICAL,
            users[2],
            projects[2],
            LocalDate(2024, 2, 15),
            LocalDate(2024, 8, 30),
            0.55f,
            listOf("fundação", "ponte", "estrutura")
        ),
        Task(
            "10",
            "Instalações Hidráulicas - Ala Norte",
            "Sistema hidráulico completo da ala norte do hospital",
            TaskStatus.TODO,
            TaskPriority.HIGH,
            users[4],
            projects[3],
            LocalDate(2024, 6, 1),
            LocalDate(2024, 9, 30),
            0.0f,
            listOf("hidráulica", "hospital")
        ),
        Task(
            "11",
            "Estrutura - Torres 1 e 2",
            "Concretagem das estruturas das primeiras duas torres",
            TaskStatus.IN_PROGRESS,
            TaskPriority.HIGH,
            users[0],
            projects[4],
            LocalDate(2024, 2, 1),
            LocalDate(2024, 7, 15),
            0.72f,
            listOf("estrutura", "concreto", "torres")
        ),
        Task(
            "12",
            "Terraplenagem - Área do Viaduto",
            "Preparação do terreno para construção do viaduto",
            TaskStatus.COMPLETED,
            TaskPriority.CRITICAL,
            users[2],
            projects[5],
            LocalDate(2024, 5, 1),
            LocalDate(2024, 6, 15),
            1.0f,
            listOf("terraplenagem", "preparação")
        )
    )

    val teams = listOf(
        Team(
            id = "1",
            name = "Arquitetura",
            department = TeamDepartment.ARCHITECTURE,
            description = "Equipe responsável por projetos arquitetônicos e design",
            leaderId = "2", // Maria Santos
            memberIds = listOf("2"), // Maria
            projectIds = listOf("1", "2"), // Vista Verde, Plaza
            createdDate = LocalDate(2019, 6, 1),
            isActive = true
        ),
        Team(
            id = "2",
            name = "Estruturas e Fundações",
            department = TeamDepartment.STRUCTURE,
            description = "Equipe especializada em estruturas, fundações e cálculos estruturais",
            leaderId = "1", // João Silva
            memberIds = listOf("1", "6"), // João, Juliana
            projectIds = listOf("1", "2", "3", "4"), // Vista Verde, Plaza, Ponte, Hospital
            createdDate = LocalDate(2020, 3, 1),
            isActive = true
        ),
        Team(
            id = "3",
            name = "Hidráulica - Obras",
            department = TeamDepartment.HYDRAULIC,
            description = "Equipe de instalações hidráulicas em obras",
            leaderId = "5", // Carlos Ferreira
            memberIds = listOf("5"), // Carlos
            projectIds = listOf("1", "4"), // Vista Verde, Hospital
            createdDate = LocalDate(2019, 11, 1),
            isActive = true
        ),
        Team(
            id = "4",
            name = "Elétrica - Obras",
            department = TeamDepartment.ELECTRICAL,
            description = "Equipe de instalações elétricas em obras",
            leaderId = "9", // Ricardo Santos
            memberIds = listOf("9"), // Ricardo
            projectIds = listOf("1", "5"), // Vista Verde, Condomínio
            createdDate = LocalDate(2019, 4, 1),
            isActive = true
        ),
        Team(
            id = "5",
            name = "Execução de Obras",
            department = TeamDepartment.MASONRY,
            description = "Equipe de execução, alvenaria e serviços gerais de obra",
            leaderId = "3", // Pedro Costa
            memberIds = listOf("3"), // Pedro
            projectIds = listOf("1", "2", "5", "6"), // Vista Verde, Plaza, Condomínio, Viaduto
            createdDate = LocalDate(2018, 1, 1),
            isActive = true
        ),
        Team(
            id = "6",
            name = "Segurança do Trabalho",
            department = TeamDepartment.SAFETY,
            description = "Equipe responsável pela segurança e saúde ocupacional",
            leaderId = "4", // Ana Oliveira
            memberIds = listOf("4"), // Ana
            projectIds = listOf("1", "3", "4"), // Vista Verde, Ponte, Hospital
            createdDate = LocalDate(2021, 8, 1),
            isActive = true
        ),
        Team(
            id = "7",
            name = "Administração de Obras",
            department = TeamDepartment.ADMINISTRATION,
            description = "Equipe administrativa e de suporte às obras",
            leaderId = "8", // Fernanda Lima
            memberIds = listOf("8"), // Fernanda
            projectIds = listOf("4", "5"), // Hospital, Condomínio
            createdDate = LocalDate(2021, 2, 1),
            isActive = true
        ),
        Team(
            id = "8",
            name = "Compras e Suprimentos",
            department = TeamDepartment.PURCHASING,
            description = "Equipe de compras, cotações e suprimentos",
            leaderId = "10", // Paula Rodrigues
            memberIds = listOf("10"), // Paula
            projectIds = listOf("1", "2", "3", "4"), // Múltiplos projetos
            createdDate = LocalDate(2020, 10, 1),
            isActive = true
        ),
        Team(
            id = "9",
            name = "Topografia e Planejamento",
            department = TeamDepartment.PLANNING,
            description = "Equipe de topografia, medições e planejamento de obras",
            leaderId = "7", // Roberto Mendes
            memberIds = listOf("7"), // Roberto
            projectIds = listOf("3", "6"), // Ponte, Viaduto
            createdDate = LocalDate(2017, 9, 1),
            isActive = true
        ),
        Team(
            id = "10",
            name = "Meio Ambiente e Qualidade",
            department = TeamDepartment.QUALITY,
            description = "Equipe de gestão ambiental e controle de qualidade",
            leaderId = "12", // Luciana Souza
            memberIds = listOf("12"), // Luciana
            projectIds = listOf("3", "6"), // Ponte, Viaduto
            createdDate = LocalDate(2021, 12, 1),
            isActive = true
        )
    )

    val documents = listOf(
        // === PLANTAS (5) ===
        Document(
            id = "1",
            code = "VV-ARQ-001-R02",
            title = "Planta Arquitetônica - Pavimento Tipo",
            type = DocumentType.ARCHITECTURAL_PLAN,
            category = DocumentCategory.PLANS_PROJECTS,
            discipline = DocumentDiscipline.ARCHITECTURE,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.APPROVED,
            currentRevision = "R02",
            createdDate = LocalDate(2024, 1, 20),
            createdBy = "2", // Maria Santos
            fileUrl = "https://storage.example.com/docs/vv-arq-001-r02.pdf",
            fileSize = 2450000,
            tags = listOf("planta", "arquitetura", "pavimento-tipo"),
            description = "Planta arquitetônica do pavimento tipo com layout de apartamentos"
        ),
        Document(
            id = "2",
            code = "VV-EST-002-R01",
            title = "Planta Estrutural - Estrutura de Concreto",
            type = DocumentType.STRUCTURAL_PLAN,
            category = DocumentCategory.PLANS_PROJECTS,
            discipline = DocumentDiscipline.STRUCTURE,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.APPROVED,
            currentRevision = "R01",
            createdDate = LocalDate(2024, 2, 5),
            createdBy = "1", // João Silva
            fileUrl = "https://storage.example.com/docs/vv-est-002-r01.dwg",
            fileSize = 3200000,
            tags = listOf("planta", "estrutura", "concreto"),
            description = "Detalhamento estrutural completo com dimensionamento de vigas e pilares"
        ),
        Document(
            id = "3",
            code = "PL-HID-003-R00",
            title = "Planta Hidráulica - Sistema de Água Fria",
            type = DocumentType.HYDRAULIC_PLAN,
            category = DocumentCategory.PLANS_PROJECTS,
            discipline = DocumentDiscipline.HYDRAULIC,
            projectId = "2", // Plaza
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.IN_PROGRESS,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 4, 10),
            createdBy = "5", // Carlos Ferreira
            fileUrl = null,
            fileSize = null,
            tags = listOf("planta", "hidráulica", "água-fria"),
            description = "Projeto do sistema de distribuição de água fria do shopping"
        ),
        Document(
            id = "4",
            code = "HS-ELE-004-R02",
            title = "Planta Elétrica - Quadros de Distribuição",
            type = DocumentType.ELECTRICAL_PLAN,
            category = DocumentCategory.PLANS_PROJECTS,
            discipline = DocumentDiscipline.ELECTRICAL,
            projectId = "4", // Hospital
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.APPROVED,
            currentRevision = "R02",
            createdDate = LocalDate(2024, 5, 15),
            createdBy = "9", // Ricardo Santos
            fileUrl = "https://storage.example.com/docs/hs-ele-004-r02.pdf",
            fileSize = 1850000,
            tags = listOf("planta", "elétrica", "quadros"),
            description = "Diagrama unifilar e disposição dos quadros elétricos principais"
        ),
        Document(
            id = "5",
            code = "PN-FUN-005-R01",
            title = "Planta de Fundações - Estacas e Blocos",
            type = DocumentType.FOUNDATION_PLAN,
            category = DocumentCategory.PLANS_PROJECTS,
            discipline = DocumentDiscipline.FOUNDATION,
            projectId = "3", // Ponte
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.FOR_REVIEW,
            currentRevision = "R01",
            createdDate = LocalDate(2024, 3, 8),
            createdBy = "6", // Juliana Almeida
            fileUrl = "https://storage.example.com/docs/pn-fun-005-r01.dwg",
            fileSize = 4100000,
            tags = listOf("planta", "fundações", "estacas", "blocos"),
            description = "Projeto de fundação profunda com detalhamento de estacas e blocos de coroamento"
        ),

        // === DOCUMENTOS TÉCNICOS (3) ===
        Document(
            id = "6",
            code = "VV-EST-MC-001-R01",
            title = "Memória de Cálculo Estrutural",
            type = DocumentType.CALCULATION_MEMORY,
            category = DocumentCategory.TECHNICAL,
            discipline = DocumentDiscipline.STRUCTURE,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.EXECUTIVE_PROJECT,
            status = DocumentStatus.APPROVED,
            currentRevision = "R01",
            createdDate = LocalDate(2024, 2, 1),
            createdBy = "1", // João Silva
            fileUrl = "https://storage.example.com/docs/vv-est-mc-001-r01.pdf",
            fileSize = 5200000,
            tags = listOf("memória-cálculo", "estrutura", "dimensionamento"),
            description = "Memória de cálculo estrutural completa com verificações normativas"
        ),
        Document(
            id = "7",
            code = "PL-ET-001-R00",
            title = "Especificação Técnica - Revestimentos",
            type = DocumentType.TECHNICAL_SPECIFICATION,
            category = DocumentCategory.TECHNICAL,
            discipline = null,
            projectId = "2", // Plaza
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.IN_APPROVAL,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 5, 20),
            createdBy = "2", // Maria Santos
            fileUrl = "https://storage.example.com/docs/pl-et-001-r00.pdf",
            fileSize = 980000,
            tags = listOf("especificação", "revestimentos", "acabamento"),
            description = "Especificação técnica de materiais e procedimentos para revestimentos"
        ),
        Document(
            id = "8",
            code = "PN-LT-001-R00",
            title = "Laudo Técnico - Sondagem do Solo",
            type = DocumentType.TECHNICAL_REPORT,
            category = DocumentCategory.TECHNICAL,
            discipline = DocumentDiscipline.FOUNDATION,
            projectId = "3", // Ponte
            phase = ProjectPhase.PRELIMINARY_STUDY,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 1, 10),
            createdBy = "7", // Roberto Mendes
            fileUrl = "https://storage.example.com/docs/pn-lt-001-r00.pdf",
            fileSize = 1450000,
            tags = listOf("laudo", "sondagem", "solo", "fundações"),
            description = "Laudo de sondagem SPT com caracterização do solo"
        ),

        // === DOCUMENTOS LEGAIS (2) ===
        Document(
            id = "9",
            code = "VV-CTR-001-R00",
            title = "Contrato de Empreitada Global",
            type = DocumentType.CONTRACT,
            category = DocumentCategory.LEGAL_CONTRACTUAL,
            discipline = null,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.PRELIMINARY_STUDY,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 1, 5),
            createdBy = "8", // Fernanda Lima
            fileUrl = "https://storage.example.com/docs/vv-ctr-001-r00.pdf",
            fileSize = 650000,
            tags = listOf("contrato", "empreitada", "legal"),
            description = "Contrato principal de construção do empreendimento"
        ),
        Document(
            id = "10",
            code = "HS-ALV-001-R00",
            title = "Alvará de Construção",
            type = DocumentType.LICENSE,
            category = DocumentCategory.LEGAL_CONTRACTUAL,
            discipline = null,
            projectId = "4", // Hospital
            phase = ProjectPhase.BASIC_PROJECT,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 3, 25),
            createdBy = "8", // Fernanda Lima
            fileUrl = "https://storage.example.com/docs/hs-alv-001-r00.pdf",
            fileSize = 520000,
            tags = listOf("alvará", "licença", "prefeitura"),
            description = "Alvará de construção emitido pela Prefeitura"
        ),

        // === DOCUMENTOS FINANCEIROS (2) ===
        Document(
            id = "11",
            code = "VV-MED-202405-R00",
            title = "Medição Mensal - Maio/2024",
            type = DocumentType.MEASUREMENT,
            category = DocumentCategory.FINANCIAL,
            discipline = null,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.IN_APPROVAL,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 5, 30),
            createdBy = "8", // Fernanda Lima
            fileUrl = "https://storage.example.com/docs/vv-med-202405-r00.pdf",
            fileSize = 1120000,
            tags = listOf("medição", "financeiro", "maio"),
            description = "Medição física e financeira dos serviços executados em maio de 2024"
        ),
        Document(
            id = "12",
            code = "PL-NF-12345-R00",
            title = "Nota Fiscal - Aquisição de Materiais",
            type = DocumentType.INVOICE,
            category = DocumentCategory.FINANCIAL,
            discipline = null,
            projectId = "2", // Plaza
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 4, 18),
            createdBy = "10", // Paula Rodrigues
            fileUrl = "https://storage.example.com/docs/pl-nf-12345-r00.pdf",
            fileSize = 580000,
            tags = listOf("nota-fiscal", "materiais", "compras"),
            description = "NF de aquisição de materiais cerâmicos para revestimento"
        ),

        // === DOCUMENTOS DE QUALIDADE (2) ===
        Document(
            id = "13",
            code = "VV-FVS-EST-001-R00",
            title = "FVS - Concretagem de Laje",
            type = DocumentType.SERVICE_VERIFICATION,
            category = DocumentCategory.QUALITY,
            discipline = DocumentDiscipline.STRUCTURE,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 3, 15),
            createdBy = "12", // Luciana Souza
            fileUrl = "https://storage.example.com/docs/vv-fvs-est-001-r00.pdf",
            fileSize = 720000,
            tags = listOf("fvs", "qualidade", "concretagem", "laje"),
            description = "Ficha de Verificação de Serviço para concretagem da laje do 5º andar"
        ),
        Document(
            id = "14",
            code = "HS-CHK-SEG-001-R00",
            title = "Checklist - Segurança do Trabalho",
            type = DocumentType.CHECKLIST,
            category = DocumentCategory.QUALITY,
            discipline = null,
            projectId = "4", // Hospital
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.FOR_REVIEW,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 5, 22),
            createdBy = "4", // Ana Oliveira
            fileUrl = "https://storage.example.com/docs/hs-chk-seg-001-r00.pdf",
            fileSize = 450000,
            tags = listOf("checklist", "segurança", "vistoria"),
            description = "Checklist de verificação de conformidade de segurança do trabalho"
        ),

        // === DOCUMENTOS DA OBRA (1) ===
        Document(
            id = "15",
            code = "VV-RDO-20240520-R00",
            title = "Diário de Obra - 20/05/2024",
            type = DocumentType.DAILY_REPORT,
            category = DocumentCategory.CONSTRUCTION_SITE,
            discipline = null,
            projectId = "1", // Vista Verde
            phase = ProjectPhase.CONSTRUCTION,
            status = DocumentStatus.APPROVED,
            currentRevision = "R00",
            createdDate = LocalDate(2024, 5, 20),
            createdBy = "3", // Pedro Costa
            fileUrl = "https://storage.example.com/docs/vv-rdo-20240520-r00.pdf",
            fileSize = 890000,
            tags = listOf("rdo", "diário", "obra"),
            description = "Registro Diário de Obra com atividades, efetivo e condições climáticas"
        )
    )

    val documentVersions = listOf(
        // Versões da Planta Arquitetônica VV-ARQ-001
        DocumentVersion(
            id = "1",
            documentId = "1",
            revision = "R00",
            versionDate = LocalDate(2024, 1, 20),
            createdBy = "2", // Maria Santos
            changes = "Emissão inicial para aprovação",
            fileUrl = "https://storage.example.com/docs/vv-arq-001-r00.pdf",
            status = DocumentStatus.SUPERSEDED,
            isSuperseded = true
        ),
        DocumentVersion(
            id = "2",
            documentId = "1",
            revision = "R01",
            versionDate = LocalDate(2024, 2, 5),
            createdBy = "2", // Maria Santos
            changes = "Ajustes no layout da cozinha dos apartamentos tipo 1",
            fileUrl = "https://storage.example.com/docs/vv-arq-001-r01.pdf",
            status = DocumentStatus.SUPERSEDED,
            isSuperseded = true
        ),
        DocumentVersion(
            id = "3",
            documentId = "1",
            revision = "R02",
            versionDate = LocalDate(2024, 2, 18),
            createdBy = "2", // Maria Santos
            changes = "Revisão das áreas técnicas conforme solicitação da construtora",
            fileUrl = "https://storage.example.com/docs/vv-arq-001-r02.pdf",
            status = DocumentStatus.APPROVED,
            isSuperseded = false
        ),

        // Versões da Planta Estrutural VV-EST-002
        DocumentVersion(
            id = "4",
            documentId = "2",
            revision = "R00",
            versionDate = LocalDate(2024, 2, 5),
            createdBy = "1", // João Silva
            changes = "Emissão inicial do projeto estrutural",
            fileUrl = "https://storage.example.com/docs/vv-est-002-r00.dwg",
            status = DocumentStatus.SUPERSEDED,
            isSuperseded = true
        ),
        DocumentVersion(
            id = "5",
            documentId = "2",
            revision = "R01",
            versionDate = LocalDate(2024, 2, 22),
            createdBy = "1", // João Silva
            changes = "Reforço de pilares P3 e P5 conforme análise de cargas",
            fileUrl = "https://storage.example.com/docs/vv-est-002-r01.dwg",
            status = DocumentStatus.APPROVED,
            isSuperseded = false
        ),

        // Versões da Planta de Fundações PN-FUN-005
        DocumentVersion(
            id = "6",
            documentId = "5",
            revision = "R00",
            versionDate = LocalDate(2024, 3, 8),
            createdBy = "6", // Juliana Almeida
            changes = "Emissão inicial do projeto de fundações",
            fileUrl = "https://storage.example.com/docs/pn-fun-005-r00.dwg",
            status = DocumentStatus.SUPERSEDED,
            isSuperseded = true
        ),
        DocumentVersion(
            id = "7",
            documentId = "5",
            revision = "R01",
            versionDate = LocalDate(2024, 3, 25),
            createdBy = "6", // Juliana Almeida
            changes = "Alteração do tipo de estaca para estaca hélice contínua e ajuste de profundidade",
            fileUrl = "https://storage.example.com/docs/pn-fun-005-r01.dwg",
            status = DocumentStatus.FOR_REVIEW,
            isSuperseded = false
        ),

        // Versão da Memória de Cálculo VV-EST-MC-001
        DocumentVersion(
            id = "8",
            documentId = "6",
            revision = "R00",
            versionDate = LocalDate(2024, 2, 1),
            createdBy = "1", // João Silva
            changes = "Emissão inicial da memória de cálculo",
            fileUrl = "https://storage.example.com/docs/vv-est-mc-001-r00.pdf",
            status = DocumentStatus.SUPERSEDED,
            isSuperseded = true
        )
    )

    val documentApprovals = listOf(
        // Aprovações para Planta Arquitetônica VV-ARQ-001-R02 (APROVADO)
        DocumentApproval(
            id = "1",
            documentId = "1",
            revision = "R02",
            stage = ApprovalStage.DESIGNER,
            approver = "2",
            approverName = "Maria Santos",
            approverRole = "Arquiteta",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 2, 18, 14, 30),
            comments = "Projeto conforme normas vigentes e especificações do cliente"
        ),
        DocumentApproval(
            id = "2",
            documentId = "1",
            revision = "R02",
            stage = ApprovalStage.COORDINATOR,
            approver = "1",
            approverName = "João Silva",
            approverRole = "Engenheiro Civil",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 2, 19, 10, 15),
            comments = "Coordenação entre disciplinas verificada. Aprovado para execução."
        ),
        DocumentApproval(
            id = "3",
            documentId = "1",
            revision = "R02",
            stage = ApprovalStage.MANAGER,
            approver = "8",
            approverName = "Fernanda Lima",
            approverRole = "Administradora de Obras",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 2, 20, 9, 0),
            comments = "Aprovado. Liberar para execução."
        ),

        // Aprovações para Especificação Técnica PL-ET-001-R00 (EM APROVAÇÃO - mix de status)
        DocumentApproval(
            id = "4",
            documentId = "7",
            revision = "R00",
            stage = ApprovalStage.DESIGNER,
            approver = "2",
            approverName = "Maria Santos",
            approverRole = "Arquiteta",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 5, 21, 16, 45),
            comments = "Especificações técnicas adequadas ao projeto"
        ),
        DocumentApproval(
            id = "5",
            documentId = "7",
            revision = "R00",
            stage = ApprovalStage.COORDINATOR,
            approver = "1",
            approverName = "João Silva",
            approverRole = "Engenheiro Civil",
            status = ApprovalStatus.PENDING,
            date = null,
            comments = null
        ),
        DocumentApproval(
            id = "6",
            documentId = "7",
            revision = "R00",
            stage = ApprovalStage.MANAGER,
            approver = "8",
            approverName = "Fernanda Lima",
            approverRole = "Administradora de Obras",
            status = ApprovalStatus.PENDING,
            date = null,
            comments = null
        ),

        // Aprovações para Planta de Fundações PN-FUN-005-R01 (PARA REVISÃO - reprovado)
        DocumentApproval(
            id = "7",
            documentId = "5",
            revision = "R01",
            stage = ApprovalStage.DESIGNER,
            approver = "6",
            approverName = "Juliana Almeida",
            approverRole = "Engenheira de Estruturas",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 3, 26, 11, 20),
            comments = "Dimensionamento das estacas adequado ao laudo de sondagem"
        ),
        DocumentApproval(
            id = "8",
            documentId = "5",
            revision = "R01",
            stage = ApprovalStage.COORDINATOR,
            approver = "1",
            approverName = "João Silva",
            approverRole = "Engenheiro Civil",
            status = ApprovalStatus.REJECTED,
            date = LocalDateTime(2024, 3, 27, 15, 10),
            comments = "Necessário revisar o detalhamento dos blocos de coroamento - divergência com memorial descritivo"
        ),
        DocumentApproval(
            id = "9",
            documentId = "5",
            revision = "R01",
            stage = ApprovalStage.MANAGER,
            approver = "8",
            approverName = "Fernanda Lima",
            approverRole = "Administradora de Obras",
            status = ApprovalStatus.PENDING,
            date = null,
            comments = null
        ),

        // Aprovações para Medição VV-MED-202405-R00 (EM APROVAÇÃO)
        DocumentApproval(
            id = "10",
            documentId = "11",
            revision = "R00",
            stage = ApprovalStage.DESIGNER,
            approver = "3",
            approverName = "Pedro Costa",
            approverRole = "Mestre de Obras",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 5, 31, 8, 30),
            comments = "Serviços medidos conferidos em campo. Quantitativos corretos."
        ),
        DocumentApproval(
            id = "11",
            documentId = "11",
            revision = "R00",
            stage = ApprovalStage.COORDINATOR,
            approver = "1",
            approverName = "João Silva",
            approverRole = "Engenheiro Civil",
            status = ApprovalStatus.APPROVED,
            date = LocalDateTime(2024, 5, 31, 14, 0),
            comments = "Medição conferida e aprovada tecnicamente"
        ),
        DocumentApproval(
            id = "12",
            documentId = "11",
            revision = "R00",
            stage = ApprovalStage.MANAGER,
            approver = "8",
            approverName = "Fernanda Lima",
            approverRole = "Administradora de Obras",
            status = ApprovalStatus.PENDING,
            date = null,
            comments = null
        )
    )
}