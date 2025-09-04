# Convenção de Commits

Use o seguinte padrão para mensagens de commit, para manter o histórico claro e organizado.

---

<tipo>(escopo opcional): mensagem resumida no imperativo

---

## Tipos comuns:

| Tipo     | Quando usar                                                                                         | Exemplo                                      |
| -------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------|
| **feat** | Adicionou algo novo (nova funcionalidade, novo endpoint, nova regra)                              | `feat(api): adiciona endpoint de cadastro de usuário` |
| **fix**  | Corrigiu um bug (algo funcionava errado e agora funciona certo)                                   | `fix(login): corrige erro ao validar token expirado` |
| **refactor** | Melhorou o código interno, sem alterar o comportamento externo (só organização, clareza, limpeza) | `refactor(service): extrai método para validação de email` |
| **docs** | Alterou só a documentação (README, Swagger, comentários)                                         | `docs(readme): adiciona instruções de instalação` |
| **style**| Alterou só formatação (indentação, espaços, imports, aspas), sem alterar lógica                  | `style(lint): ajusta indentação e ordena imports` |
| **test** | Adicionou ou alterou testes automatizados                                                        | `test(user): adiciona testes unitários para validação de CPF` |
| **chore**| Alterações de manutenção do projeto (dependências, configs, CI/CD, build)                        | `chore(deps): atualiza versões de bibliotecas` |
| **perf** | Melhorou performance (algoritmos, tempo de resposta, consumo de recursos)                        | `perf(query): otimiza busca de usuários por índice` |

---

## Regras rápidas para escolher o tipo:

- Adicionei algo novo? Use `feat`
- Corrigi algo que estava errado? Use `fix`
- Melhorei o código sem mudar o que ele faz? Use `refactor`
- Só mexi em documentação? Use `docs`
- Só mexi em formatação (estética)? Use `style`
- Adicionei/ajustei testes? Use `test`
- Mexi em configs, scripts, dependências? Use `chore`
- Melhorei performance? Use `perf`

---

## Exemplo de uso:

```bash
feat(usuario): adiciona endpoint de criação de usuário
fix(login): corrige validação de token expirado
refactor(service): extrai validação de email para método separado
docs(readme): atualiza instruções de deploy
style(lint): remove imports não utilizados
test(api): adiciona testes para recuperação de senha
chore(ci): ajusta configuração de pipeline
perf(query): melhora performance da consulta de pedidos
```

---

## Corpo (opcional):

Se precisar, acrescente um texto explicativo mais detalhado após a linha de título, para descrever melhor o que foi feito.

---

Feito para manter seu histórico de commits sempre claro e profissional!
