# Liquibase - Správa databázového schématu a dat

Projekt používá **Liquibase** pro správu databázového schématu i dat číselníků.

## Struktura

```
src/main/resources/
└── db/
    └── changelog/               # Liquibase - schéma i data
        ├── db.changelog-master.yaml
        └── changesets/
            ├── 001-users.yaml
            ├── 002-voltage-levels.yaml
            ├── 003-network-types.yaml
            ├── 004-codelist-registry.yaml
            ├── 005-flags.yaml
            ├── 006-api-keys.yaml
            ├── 007-cuzk-schema.yaml
            ├── 008-cuzk-data.yaml
            ├── 009-cuzk-registry.yaml
            ├── 010-kso-schema.yaml
            ├── 011-kso-data.yaml
            ├── 012-kso-fix.yaml
            └── 013-response-format.yaml
```

## Implementované číselníky

### 1. Provozní stavy elektrických zařízení
**Právní základ:** Vyhláška č. 79/2010 Sb., § 3

**Struktura:**
- **Skupina:** Elektroenergetika (ELEKTRO)
- **Podskupina:** Údržba (UDRZBA_ELEKTRO)
- **Číselníky:**

| Kód | Název CS | Název EN | Popis |
|-----|----------|----------|-------|
| PROVOZ_STAVY_01 | Provoz | Operation | Zařízení připojeno k napájecí síti a připraveno k použití (§ 3 odst. 2) |
| PROVOZ_STAVY_02 | Odstavení | Shutdown | Trvale odpojeno, může být znovu připojeno (§ 3 odst. 3) |
| PROVOZ_STAVY_03 | Odpojení | Disconnection | Trvale odpojeno, vyžaduje revizi před připojením (§ 3 odst. 4) |

**Platnost od:** 1. 1. 2011 (účinnost vyhlášky)

## Přidání nového číselníku

### 1. Vytvoření changesets souboru

```bash
touch src/main/resources/db/changelog/changesets/002-nazev-ciselnik.yaml
```

### 2. Příklad changesets

```yaml
databaseChangeLog:
  - changeSet:
      id: 002-insert-group-example
      author: vas-jmeno
      comment: Popis změny
      changes:
        - insert:
            tableName: groups
            columns:
              - column:
                  name: code
                  value: EXAMPLE_CODE
              - column:
                  name: name_cs
                  value: Příklad
              - column:
                  name: name_en
                  value: Example
              - column:
                  name: active
                  valueBoolean: true
              - column:
                  name: created_at
                  valueComputed: CURRENT_TIMESTAMP
      rollback:
        - delete:
            tableName: groups
            where: code = 'EXAMPLE_CODE'
```

### 3. Registrace v master changelogs

Přidat do `db.changelog-master.yaml`:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/changesets/001-provozni-stavy-elektrickych-zarizeni.yaml
  - include:
      file: db/changelog/changesets/002-nazev-ciselnik.yaml
```

## API přístup k číselníkům

### Získání skupiny Elektroenergetika

```bash
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/v1/groups/code/ELEKTRO
```

### Získání podskupiny Údržba

```bash
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/v1/subgroups/by-group/{group-id}
```

### Získání provozních stavů

```bash
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/v1/codelists/by-subgroup/{subgroup-id}
```

### Vyhledání konkrétního stavu

```bash
curl -H "X-API-Key: your-api-key" \
  http://localhost:8080/api/v1/codelists/code/PROVOZ_STAVY_01
```

## Příkazy Liquibase

### Zobrazení changesets

```bash
./gradlew liquibaseStatus
```

### Rollback poslední změny

```bash
./gradlew liquibaseRollbackCount -PliquibaseCommandValue=1
```

### Vygenerování SQL

```bash
./gradlew liquibaseUpdateSQL
```

## Best Practices

1. **Každý changeset má unikátní ID**: `{číslo}-{akce}-{název}`
2. **Vždy definovat rollback**: Pro možnost vrácení změn
3. **Použít valueComputed pro foreign keys**: `(SELECT id FROM groups WHERE code = 'X')`
4. **Datum platnosti**: Uvádět `valid_from` pro legislativní číselníky
5. **Bilingvální data**: Vždy vyplnit `name_cs` i `name_en`

## Právní reference

Při vytváření číselníků z legislativy uvádět:
- Číslo předpisu (např. "vyhláška č. 79/2010 Sb.")
- Paragraf a odstavec (např. "§ 3 odst. 2")
- Datum účinnosti v `valid_from`

## Troubleshooting

### Liquibase neběží

Zkontrolovat `application.yml`:
```yaml
spring:
  liquibase:
    enabled: true
    change-log: classpath:db/changelog/db.changelog-master.yaml
```

### Duplicitní data

Použít precondition:
```yaml
preconditions:
  - onFail: MARK_RAN
  - not:
      - sqlCheck:
          expectedResult: 0
          sql: SELECT COUNT(*) FROM groups WHERE code = 'ELEKTRO'
```

### Rollback selhal

Zkontrolovat rollback definici v changesets a manuálně opravit databázi.
