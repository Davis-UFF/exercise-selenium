# Testes Selenium — Automation Exercise

Testes automatizados dos cenários de login com credenciais incorretas e cadastro de usuário do site [automationexercise.com](https://automationexercise.com/).

## Requisitos

- Java 17 ou superior
- Maven 3.8 ou superior
- Google Chrome instalado

## Como executar

Abra a pasta do projeto no VS Code. No terminal, na mesma pasta do `pom.xml`, execute:

```bash
mvn clean test
```

O WebDriverManager baixa o driver do Chrome na primeira execução. Os testes abrem e fecham o navegador automaticamente. Os resultados ficam em `target/surefire-reports/`.

## Arquivos de teste

```text
src/test/java/tests/
├── LoginIncorretoTest.java
└── RegisterUserTest.java
```

`LoginIncorretoTest` testa credenciais inválidas com cinco conjuntos de entradas e verifica também um e-mail em formato inválido. `RegisterUserTest` cadastra um usuário com dados de teste, confirma a criação e exclui a conta ao final.
