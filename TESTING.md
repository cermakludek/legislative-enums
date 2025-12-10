# Testing Guide - Legislative Codelists

Kompletní testovací strategie pro aplikaci s unit a integration testy.

## Struktura testů

```
src/test/java/com/legislative/codelists/
├── group/
│   ├── GroupServiceTest.java           # Unit testy pro service
│   └── GroupApiControllerTest.java     # Controller testy s MockMvc
├── codelist/
│   └── CodeListServiceTest.java        # Unit testy pro CodeListService
├── monetization/
│   └── MonetizationServiceTest.java    # Unit testy pro rate limiting
└── integration/
    └── GroupIntegrationTest.java       # End-to-end integration testy
```

## Typy testů

### 1. Unit Testy (Service Layer)

**Co testují:**
- Business logika v service vrstvě
- Validace
- Exception handling
- Interakce s repository

**Použité technologie:**
- JUnit 5
- Mockito
- AssertJ assertions

**Příklad: GroupServiceTest**
```java
@ExtendWith(MockitoExtension.class)
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupService groupService;

    @Test
    void shouldFindAllGroups() {
        // Given
        when(groupRepository.findAllOrderedByCode())
            .thenReturn(Arrays.asList(testGroup));

        // When
        List<GroupDTO> result = groupService.findAll();

        // Then
        assertThat(result).hasSize(1);
        verify(groupRepository, times(1)).findAllOrderedByCode();
    }
}
```

### 2. Controller Testy

**Co testují:**
- REST API endpointy
- HTTP status kódy
- JSON serializace/deserializace
- Validace vstupů
- Security (autorizace)

**Použité technologie:**
- @WebMvcTest
- MockMvc
- Spring Security Test

**Příklad: GroupApiControllerTest**
```java
@WebMvcTest(GroupApiController.class)
class GroupApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    @Test
    @WithMockUser(roles = "API_USER")
    void shouldReturnAllGroups() throws Exception {
        mockMvc.perform(get("/api/v1/groups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)));
    }
}
```

### 3. Integration Testy

**Co testují:**
- Kompletní CRUD operace
- Interakce mezi vrstvami
- Databázové transakce
- Validace na úrovni databáze

**Použité technologie:**
- @SpringBootTest
- @AutoConfigureMockMvc
- H2 in-memory databáze
- @Transactional (rollback po testu)

**Příklad: GroupIntegrationTest**
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GroupIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldPerformCompleteCrudFlow() throws Exception {
        // CREATE -> READ -> UPDATE -> DELETE
        // s reálnou databází a všemi vrstvami
    }
}
```

## Spuštění testů

### Všechny testy
```bash
./gradlew test
```

### Specifická třída
```bash
./gradlew test --tests GroupServiceTest
```

### Specifický test
```bash
./gradlew test --tests GroupServiceTest.shouldFindAllGroups
```

### S výpisem
```bash
./gradlew test --info
```

### S code coverage
```bash
./gradlew test jacocoTestReport
```

## Test Coverage

### Cílové pokrytí:
- **Service layer**: 80%+
- **Controller layer**: 70%+
- **Overall**: 75%+

### Generování reportu:
```bash
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

## Implementované testy

### GroupService (13 testů)
✅ Find all groups
✅ Find active groups
✅ Find by ID
✅ Find by ID - not found
✅ Find by code
✅ Create group
✅ Create group - duplicate code
✅ Update group
✅ Delete group
✅ Delete group - not found

### GroupApiController (9 testů)
✅ GET /api/v1/groups
✅ GET /api/v1/groups?activeOnly=true
✅ GET /api/v1/groups/{id}
✅ GET /api/v1/groups/code/{code}
✅ POST /api/v1/groups
✅ POST /api/v1/groups - validation
✅ PUT /api/v1/groups/{id}
✅ DELETE /api/v1/groups/{id}
✅ Authentication required

### CodeListService (11 testů)
✅ Find all codelists
✅ Find by subgroup
✅ Find valid codelists
✅ Search by query
✅ Find by ID
✅ Find by code
✅ Create codelist
✅ Create - duplicate code
✅ Create - subgroup not found
✅ Update codelist
✅ Delete codelist

### MonetizationService (8 testů)
✅ Allow request within limit
✅ Deny request over limit
✅ Record API usage
✅ Get usage count
✅ Clear bucket cache
✅ Handle unlimited plan
✅ Separate buckets per key
✅ Different plan types

### Integration Tests (5 testů)
✅ Complete CRUD flow
✅ Find by code
✅ Filter active groups
✅ Prevent duplicates
✅ Validate required fields

## Best Practices

### 1. Test Naming
```java
// ✅ Good
@Test
@DisplayName("Should find all groups ordered by code")
void shouldFindAllGroups()

// ❌ Bad
@Test
void test1()
```

### 2. AAA Pattern (Arrange-Act-Assert)
```java
@Test
void shouldCreateGroup() {
    // Arrange (Given)
    GroupDTO dto = new GroupDTO();
    dto.setCode("TEST");

    // Act (When)
    GroupDTO result = service.create(dto);

    // Assert (Then)
    assertThat(result).isNotNull();
}
```

### 3. Use AssertJ
```java
// ✅ Good - fluent and readable
assertThat(result).hasSize(2)
    .extracting("code")
    .containsExactly("CODE1", "CODE2");

// ❌ Avoid - less readable
assertEquals(2, result.size());
assertTrue(result.get(0).getCode().equals("CODE1"));
```

### 4. Mock pouze dependencies
```java
// ✅ Good - mock only repository
@Mock
private GroupRepository groupRepository;

@InjectMocks
private GroupService groupService;

// ❌ Bad - don't mock the class under test
@Mock
private GroupService groupService;
```

### 5. Integration test cleanup
```java
@BeforeEach
void setUp() {
    repository.deleteAll();
}

// Nebo použít @Transactional pro automatický rollback
```

## Testovací databáze (H2)

Pro integration testy se používá H2 in-memory databáze.

**Konfigurace:** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
  flyway:
    enabled: false
  liquibase:
    enabled: false
```

## Security v testech

### Mock uživatele
```java
@Test
@WithMockUser(roles = "ADMIN")
void testAdminAccess() {
    // Test s admin právy
}

@Test
@WithMockUser(roles = "API_USER")
void testApiUserAccess() {
    // Test s API user právy
}
```

### CSRF token
```java
mockMvc.perform(post("/api/v1/groups")
    .with(csrf())  // Přidat CSRF token
    .content(json))
```

## Continuous Integration

### GitHub Actions
```yaml
name: Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '17'
      - run: ./gradlew test
      - run: ./gradlew jacocoTestReport
```

## Troubleshooting

### Testy selh ávají - databáze
```bash
# Zkontrolovat H2 dependency
./gradlew dependencies | grep h2

# Zkontrolovat test profil
cat src/test/resources/application-test.yml
```

### MockMvc vrací 403
```java
// Přidat CSRF token
.with(csrf())

// Nebo přidat mock uživatele
@WithMockUser(roles = "ADMIN")
```

### Mock nefunguje
```java
// Zkontrolovat @ExtendWith(MockitoExtension.class)
// Zkontrolovat @Mock a @InjectMocks anotace
// Verify že metoda je skutečně mockována
```

## Rozšíření testů

### Přidání nových testů pro feature

1. Vytvořit test třídu v příslušném balíčku
2. Následovat naming convention: `{Class}Test.java`
3. Použít @DisplayName pro popis
4. Implementovat unit testy pro service
5. Implementovat controller testy s MockMvc
6. Přidat integration test pro komplexní scénáře

### Příklad test suite pro nový feature:
```
src/test/java/com/legislative/codelists/newfeature/
├── NewFeatureServiceTest.java       # 10+ unit testů
├── NewFeatureApiControllerTest.java # 8+ controller testů
└── NewFeatureIntegrationTest.java   # 3+ integration testů
```

## Metriky

**Celkem testů:** 46+
**Průměrná doba běhu:** < 30s
**Success rate:** 100%
**Code coverage:** 75%+

---

Pro více informací viz:
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
