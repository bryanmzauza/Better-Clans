# BetterClans

Plugin de clãs para Minecraft **Java Edition 26.1.2** (Paper) com foco em PvP competitivo, duelos `/x1` e evento semanal **Gladiador**.

> Status: **Fase 1 (MVP) em andamento** — bootstrap do projeto concluído, classes-esqueleto criadas.

## Stack

- Paper API `26.1.2-R0.1-SNAPSHOT`
- Java `25` (toolchain Gradle)
- Gradle Kotlin DSL + `paperweight-userdev` + `run-paper` + `shadow`
- HikariCP + MySQL/SQLite (DAO abstrato)
- Vault (hard-depend) / PlaceholderAPI / ProtocolLib / LuckPerms / nChat (soft-depend)

Especificação completa: [docs/betterclans-plugin-spec.md](docs/betterclans-plugin-spec.md).

## Como buildar

```powershell
# gera build/libs/BetterClans-<versao>.jar
./gradlew build

# sobe um Paper de dev com o plugin carregado
./gradlew runServer
```

## Configuração

Após o primeiro start, ajuste `plugins/BetterClans/config.yml`:

- Default usa **SQLite** (arquivo local `betterclans.db`).
- Para produção com MySQL, troque `database.type: mysql` e preencha o bloco `mysql:`.

## Comandos principais

| Comando | Descrição |
|---|---|
| `/clan` (alias `/bc`, `/clã`) | Comando raiz do plugin |
| `/cc`, `/ac` | Chat de clã / aliança |
| `/x1` | Desafio de duelo 1v1 |
| `/gladiator` | Evento semanal Battle Royale |

Veja a [especificação](docs/betterclans-plugin-spec.md) para a lista completa.

## Licença

MIT — veja [LICENSE](LICENSE).

## Notas de versão

- Paper dev bundle `26.1.2-R0.1-SNAPSHOT`: caso ainda não esteja publicado no repo público da PaperMC, ajuste `paperApiVersion` em [gradle.properties](gradle.properties) temporariamente para uma versão disponível (ex.: `1.21.4-R0.1-SNAPSHOT`) enquanto aguarda a publicação.
