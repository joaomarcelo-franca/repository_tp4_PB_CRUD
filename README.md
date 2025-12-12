# Projeto CI/CD com Ambientes e Segurança — Documentação Final
## ■ Arquitetura do Sistema
A aplicação segue uma arquitetura modular em Java, utilizando:
- Javalin como framework web
- Serviços desacoplados seguindo SRP
- Estrutura organizada em camadas (controller, service, model)
- Testes unitários com JUnit 5
## ■ Workflows Implementados (CI/CD)
### 1. CI Multiambiente
Pipeline automático para:
- Desenvolvimento
- Teste
- Produção
### 2. Análise de Segurança
- SpotBugs (via plugin Gradle)
- Verificação automática a cada push/pull request
### 3. Testes Automatizados
- Build e execução de testes via Gradle
- Relatórios de cobertura (Jacoco)
## ■ Pipeline de Deploy
- Build → Testes → Análises → Deploy
- Diferenciação automática de ambiente via GitHub Actions
- Deploy condicional baseado na branch
## ■■ Guia de Execução
### Rodar o sistema
```
./gradlew run
```
### Rodar testes
```
./gradlew test
```
### Gerar relatório de cobertura
```
./gradlew jacocoTestReport
```
### Rodar SpotBugs
```
./gradlew spotbugsMain
```
## ■ Estratégias de Segurança
- Análise estática de código
- Linters automáticos
- Gate de aprovação no PR
- Execução obrigatória do CI para merges
## ■ Validações Pós■Deploy
- Testes automáticos de verificação de saúde
- Logs e monitoramento básicos
- Scripts de checagem executados no pipeline
[![CI - Build, Test & Coverage](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/github-hosted.yml/badge.svg)](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/github-hosted.yml)
[![CI - Build, Test, Coverage & Package](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/ci-cd.yml)
[![CI/CD - Multi Environment Deploy](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/ci-cd-multienv.yml/badge.svg)](https://github.com/joaomarcelo-franca/repository_tp4_PB_CRUD/actions/workflows/ci-cd-multienv.yml)