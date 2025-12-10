# Legislative Codelists - Aplikace pro evidenci legislativních číselníků

Plně funkční aplikace pro správu legislativních číselníků s třístupňovou hierarchií, internacionalizací a monetizovatelným API.

## Funkce

### Základní funkce
- **Třístupňová hierarchie**: Skupina (Oblast) → Podskupina (Typ) → Číselník
- **Internacionalizace**: Plná podpora češtiny a angličtiny
- **Webové rozhraní**: Moderní UI postavené na Thymeleaf + Bootstrap 5
- **REST API**: Plně dokumentované API s OpenAPI/Swagger
- **Databáze**: PostgreSQL s Flyway migracemi

### Bezpečnost
- **Basic Authentication** pro webové rozhraní
- **API Key Authentication** pro REST API
- **Role-based Access Control**:
  - **Admin**: Plný přístup ke všem funkcím
  - **Contributor**: Vytváření a editace číselníků
  - **Reader**: Pouze čtení

### Monetizace API
- **Free Plan**: 100 požadavků/den zdarma
- **Basic Plan**: 10,000 požadavků/měsíc za 500 CZK
- **Premium Plan**: 100,000 požadavků/měsíc za 3,000 CZK
- **Enterprise Plan**: Pay-per-request (0.05 CZK/požadavek)
- **Rate Limiting**: Automatické omezování pomocí bucket4j
- **Usage Tracking**: Sledování využití API pro každý klíč

## Technologie

- **Java 17**
- **Spring Boot 3.2.1**
- **PostgreSQL 15**
- **Gradle 8.5**
- **Thymeleaf**
- **Docker & Docker Compose**
- **Flyway** (databázové migrace)
- **Swagger/OpenAPI** (dokumentace API)
- **Bucket4j** (rate limiting)

## Rychlý start

### Pomocí Docker Compose (doporučeno)

```bash
# Spuštění aplikace
docker-compose up -d

# Aplikace běží na http://localhost:8080
```

### Lokální vývoj

#### Požadavky
- Java 17+
- PostgreSQL 15+
- Gradle 8.5+

#### Konfigurace databáze

```bash
# Vytvořit databázi
createdb -U postgres legislative_codelists

# Nastavit prostředí (volitelné)
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=legislative_codelists
export DB_USER=dbuser
export DB_PASSWORD=dbpass
```

#### Spuštění aplikace

```bash
# Build
./gradlew build

# Spuštění
./gradlew bootRun

# Nebo s vlastním profilem
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## Přístup k aplikaci

### Webové rozhraní
- **URL**: http://localhost:8080
- **Přihlášení**:
  - Username: `admin`
  - Password: `admin123`

### API Dokumentace
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

### Databáze
- **Host**: localhost:5432
- **Database**: legislative_codelists
- **Username**: dbuser
- **Password**: dbpass

## API Endpoints

### Veřejné endpointy (vyžadují API Key)

```bash
# Získat všechny skupiny
GET /api/v1/groups
Header: X-API-Key: {your-api-key}

# Získat skupinu podle ID
GET /api/v1/groups/{id}

# Získat skupinu podle kódu
GET /api/v1/groups/code/{code}

# Získat podskupiny podle skupiny
GET /api/v1/subgroups/by-group/{groupId}

# Získat číselníky podle podskupiny
GET /api/v1/codelists/by-subgroup/{subgroupId}

# Vyhledat číselníky
GET /api/v1/codelists/search?query={search}

# Získat platné číselníky
GET /api/v1/codelists/valid
```

### Administrátorské endpointy (Basic Auth)

```bash
# Vytvořit skupinu
POST /api/v1/groups
Authorization: Basic {base64(username:password)}
Content-Type: application/json

{
  "code": "NEW_GROUP",
  "nameCs": "Nová skupina",
  "nameEn": "New Group",
  "descriptionCs": "Popis",
  "descriptionEn": "Description",
  "active": true
}

# Aktualizovat skupinu
PUT /api/v1/groups/{id}

# Smazat skupinu
DELETE /api/v1/groups/{id}
```

## Získání API klíče

1. Přihlaste se do webového rozhraní jako admin
2. Přejděte do sekce "API Keys"
3. Vytvořte nový API klíč a zvolte Usage Plan
4. Zkopírujte vygenerovaný klíč (UUID formát)
5. Použijte klíč v hlavičce `X-API-Key` pro API požadavky

## Příklad použití API

```bash
# Pomocí curl
curl -H "X-API-Key: your-api-key-here" \
  http://localhost:8080/api/v1/groups

# Pomocí httpie
http GET http://localhost:8080/api/v1/groups \
  X-API-Key:your-api-key-here

# Pomocí JavaScript fetch
fetch('http://localhost:8080/api/v1/groups', {
  headers: {
    'X-API-Key': 'your-api-key-here'
  }
})
.then(res => res.json())
.then(data => console.log(data));
```

## Datový model

```
Group (Oblast)
├── id
├── code
├── name_cs / name_en
├── description_cs / description_en
├── active
└── Subgroup[] (Typy)
    ├── id
    ├── code
    ├── name_cs / name_en
    ├── description_cs / description_en
    ├── active
    └── CodeList[] (Číselníky)
        ├── id
        ├── code
        ├── value_cs / value_en
        ├── description_cs / description_en
        ├── valid_from / valid_to
        └── active
```

## Bezpečnost

### Web Interface (Basic Auth)
- **URL**: `/web/**`, `/admin/**`
- **Autentizace**: HTTP Basic Authentication
- **Role**: ADMIN, CONTRIBUTOR, READER

### REST API (API Key)
- **URL**: `/api/v1/**`
- **Autentizace**: API Key v hlavičce `X-API-Key`
- **Rate Limiting**: Podle Usage Plan
- **Tracking**: Všechny požadavky jsou logovány

## Rate Limiting

Aplikace automaticky omezuje počet požadavků podle zvoleného Usage Plan:

| Plan       | Limit              | Období |
|------------|--------------------|--------|
| FREE       | 100 requests       | 24h    |
| BASIC      | 10,000 requests    | 30 dní |
| PREMIUM    | 100,000 requests   | 30 dní |
| ENTERPRISE | Neomezeno          | -      |

Při překročení limitu API vrací HTTP 429 (Too Many Requests).

## Profily prostředí

### Development (`dev`)
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```
- SQL logování zapnuto
- Thymeleaf cache vypnut
- Debug logging

### Production (`prod`)
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```
- SQL logování vypnuto
- Thymeleaf cache zapnut
- Error details skryty
- Optimalizováno pro výkon

## Databázové migrace

Migrace jsou spravovány pomocí Flyway:

```
src/main/resources/db/migration/
├── V1__init_schema.sql       # Vytvoření tabulek
└── V2__seed_data.sql         # Seed data (admin user, usage plans, příklady)
```

## Build a Deployment

### Local Build
```bash
./gradlew clean build
java -jar build/libs/legislative-codelists-1.0.0.jar
```

### Docker Build
```bash
docker build -t legislative-codelists .
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_USER=dbuser \
  -e DB_PASSWORD=dbpass \
  legislative-codelists
```

### Docker Compose
```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# View logs
docker-compose logs -f app

# Rebuild
docker-compose up -d --build
```

## Monitorování

### Actuator Endpoints (pro produkci)
Můžete přidat Spring Boot Actuator pro monitoring:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
```

Endpointy:
- `/actuator/health` - Status aplikace
- `/actuator/metrics` - Metriky
- `/actuator/info` - Informace o aplikaci

## Rozšíření

### Přidání nové entity
1. Vytvořit entitu v `domain/`
2. Vytvořit repository v `repository/`
3. Vytvořit DTO v `dto/`
4. Vytvořit service v `service/`
5. Vytvořit controller v `controller/api/` a `controller/web/`
6. Přidat Flyway migraci v `db/migration/`

### Přidání nového Usage Plan
```sql
INSERT INTO usage_plans (name, type, requests_limit, period_hours, price, currency, description)
VALUES ('CUSTOM', 'PER_PERIOD', 50000, 720, 1500.00, 'CZK', 'Custom plan');
```

## Troubleshooting

### Databáze se nepřipojí
```bash
# Zkontrolovat běžící PostgreSQL
docker-compose ps

# Zkontrolovat logy
docker-compose logs postgres

# Restartovat služby
docker-compose restart
```

### Port 8080 je obsazený
```yaml
# V docker-compose.yml změnit port
ports:
  - "8081:8080"
```

### Flyway migrace selhávají
```bash
# Vyčistit databázi a začít znovu
docker-compose down -v
docker-compose up -d
```

## Licence

Apache 2.0

## Autor

Legislative Codelists Team
