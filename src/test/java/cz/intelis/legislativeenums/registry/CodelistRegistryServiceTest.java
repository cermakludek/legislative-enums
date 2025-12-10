package cz.intelis.legislativeenums.registry;

import cz.intelis.legislativeenums.flag.Flag;
import cz.intelis.legislativeenums.flag.FlagRepository;
import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CodelistRegistryService Unit Tests")
class CodelistRegistryServiceTest {

    @Mock
    private CodelistRegistryRepository codelistRegistryRepository;

    @Mock
    private FlagRepository flagRepository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private CodelistRegistryService codelistRegistryService;

    private CodelistRegistry testCodelistRegistry;
    private CodelistRegistryDTO testCodelistRegistryDTO;
    private Flag testFlag;

    @BeforeEach
    void setUp() {
        testFlag = new Flag();
        testFlag.setId(1L);
        testFlag.setCode("CUZK");
        testFlag.setNameCs("ČÚZK číselník");
        testCodelistRegistry = new CodelistRegistry();
        testCodelistRegistry.setId(1L);
        testCodelistRegistry.setCode("LAND_TYPE");
        testCodelistRegistry.setNameCs("Druh pozemku");
        testCodelistRegistry.setNameEn("Land Type");
        testCodelistRegistry.setDescriptionCs("Popis česky");
        testCodelistRegistry.setDescriptionEn("Description English");
        testCodelistRegistry.setWebUrl("/land-types");
        testCodelistRegistry.setApiUrl("/api/v1/land-types");
        testCodelistRegistry.setIconClass("bi-tree");
        testCodelistRegistry.setSortOrder(1);
        testCodelistRegistry.setFlags(new HashSet<>(Set.of(testFlag)));

        testCodelistRegistryDTO = new CodelistRegistryDTO();
        testCodelistRegistryDTO.setId(1L);
        testCodelistRegistryDTO.setCode("LAND_TYPE");
        testCodelistRegistryDTO.setNameCs("Druh pozemku");
        testCodelistRegistryDTO.setNameEn("Land Type");
        testCodelistRegistryDTO.setDescriptionCs("Popis česky");
        testCodelistRegistryDTO.setDescriptionEn("Description English");
        testCodelistRegistryDTO.setWebUrl("/land-types");
        testCodelistRegistryDTO.setApiUrl("/api/v1/land-types");
        testCodelistRegistryDTO.setIconClass("bi-tree");
        testCodelistRegistryDTO.setSortOrder(1);
        testCodelistRegistryDTO.setFlagIds(Set.of(1L));
    }

    @Test
    @DisplayName("Should find all codelist registry entries")
    void shouldFindAllCodelistRegistryEntries() {
        // Given
        CodelistRegistry cr2 = new CodelistRegistry();
        cr2.setId(2L);
        cr2.setCode("BUILDING_TYPE");
        cr2.setNameCs("Typ budovy");
        cr2.setFlags(new HashSet<>());

        when(codelistRegistryRepository.findAllOrdered()).thenReturn(Arrays.asList(testCodelistRegistry, cr2));

        // When
        List<CodelistRegistryDTO> result = codelistRegistryService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("LAND_TYPE");
        assertThat(result.get(1).getCode()).isEqualTo("BUILDING_TYPE");
        verify(codelistRegistryRepository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find codelist registry entry by ID")
    void shouldFindCodelistRegistryEntryById() {
        // Given
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));

        // When
        CodelistRegistryDTO result = codelistRegistryService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("LAND_TYPE");
        assertThat(result.getNameCs()).isEqualTo("Druh pozemku");
        verify(codelistRegistryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when codelist registry entry not found by ID")
    void shouldThrowExceptionWhenCodelistRegistryEntryNotFoundById() {
        // Given
        when(codelistRegistryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry not found with id: 999");
    }

    @Test
    @DisplayName("Should find codelist registry entry by code")
    void shouldFindCodelistRegistryEntryByCode() {
        // Given
        when(codelistRegistryRepository.findByCode("LAND_TYPE")).thenReturn(Optional.of(testCodelistRegistry));

        // When
        CodelistRegistryDTO result = codelistRegistryService.findByCode("LAND_TYPE");

        // Then
        assertThat(result.getCode()).isEqualTo("LAND_TYPE");
        verify(codelistRegistryRepository, times(1)).findByCode("LAND_TYPE");
    }

    @Test
    @DisplayName("Should throw exception when codelist registry entry not found by code")
    void shouldThrowExceptionWhenCodelistRegistryEntryNotFoundByCode() {
        // Given
        when(codelistRegistryRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new codelist registry entry")
    void shouldCreateNewCodelistRegistryEntry() {
        // Given
        when(codelistRegistryRepository.existsByCode(anyString())).thenReturn(false);
        when(flagRepository.findAllById(any())).thenReturn(Arrays.asList(testFlag));
        when(codelistRegistryRepository.save(any(CodelistRegistry.class))).thenAnswer(inv -> {
            CodelistRegistry cr = inv.getArgument(0);
            cr.setId(1L);
            return cr;
        });

        // When
        CodelistRegistryDTO result = codelistRegistryService.create(testCodelistRegistryDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("LAND_TYPE");
        verify(codelistRegistryRepository, times(1)).save(any(CodelistRegistry.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Registr číselníků"), eq("CODELIST_REGISTRY"), eq(1L), eq("LAND_TYPE"), eq("Druh pozemku"));
    }

    @Test
    @DisplayName("Should throw exception when creating codelist registry entry with existing code")
    void shouldThrowExceptionWhenCreatingCodelistRegistryEntryWithExistingCode() {
        // Given
        when(codelistRegistryRepository.existsByCode("LAND_TYPE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.create(testCodelistRegistryDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry with code LAND_TYPE already exists");
    }

    @Test
    @DisplayName("Should update existing codelist registry entry")
    void shouldUpdateExistingCodelistRegistryEntry() {
        // Given
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));
        when(flagRepository.findAllById(any())).thenReturn(Arrays.asList(testFlag));
        when(codelistRegistryRepository.save(any(CodelistRegistry.class))).thenReturn(testCodelistRegistry);

        testCodelistRegistryDTO.setNameCs("Aktualizovaný název");

        // When
        CodelistRegistryDTO result = codelistRegistryService.update(1L, testCodelistRegistryDTO);

        // Then
        verify(codelistRegistryRepository, times(1)).save(any(CodelistRegistry.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Registr číselníků"), eq("CODELIST_REGISTRY"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent codelist registry entry")
    void shouldThrowExceptionWhenUpdatingNonExistentCodelistRegistryEntry() {
        // Given
        when(codelistRegistryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.update(999L, testCodelistRegistryDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when updating codelist registry entry with existing code")
    void shouldThrowExceptionWhenUpdatingCodelistRegistryEntryWithExistingCode() {
        // Given
        testCodelistRegistryDTO.setCode("NEWCODE");
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));
        when(codelistRegistryRepository.existsByCode("NEWCODE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.update(1L, testCodelistRegistryDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry with code NEWCODE already exists");
    }

    @Test
    @DisplayName("Should delete codelist registry entry")
    void shouldDeleteCodelistRegistryEntry() {
        // Given
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));

        // When
        codelistRegistryService.delete(1L);

        // Then
        verify(codelistRegistryRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Registr číselníků"), eq("CODELIST_REGISTRY"), eq(1L), eq("LAND_TYPE"), eq("Druh pozemku"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent codelist registry entry")
    void shouldThrowExceptionWhenDeletingNonExistentCodelistRegistryEntry() {
        // Given
        when(codelistRegistryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist registry entry not found with id: 999");
    }

    @Test
    @DisplayName("Should add flag to codelist")
    void shouldAddFlagToCodelist() {
        // Given
        Flag newFlag = new Flag();
        newFlag.setId(2L);
        newFlag.setCode("NEW");
        newFlag.setNameCs("Nový příznak");

        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));
        when(flagRepository.findById(2L)).thenReturn(Optional.of(newFlag));
        when(codelistRegistryRepository.save(any(CodelistRegistry.class))).thenReturn(testCodelistRegistry);

        // When
        codelistRegistryService.addFlag(1L, 2L);

        // Then
        verify(codelistRegistryRepository, times(1)).save(any(CodelistRegistry.class));
        assertThat(testCodelistRegistry.getFlags()).contains(newFlag);
    }

    @Test
    @DisplayName("Should throw exception when adding flag to non-existent codelist")
    void shouldThrowExceptionWhenAddingFlagToNonExistentCodelist() {
        // Given
        when(codelistRegistryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.addFlag(999L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when adding non-existent flag to codelist")
    void shouldThrowExceptionWhenAddingNonExistentFlagToCodelist() {
        // Given
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));
        when(flagRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.addFlag(1L, 999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Flag not found with id: 999");
    }

    @Test
    @DisplayName("Should remove flag from codelist")
    void shouldRemoveFlagFromCodelist() {
        // Given
        when(codelistRegistryRepository.findById(1L)).thenReturn(Optional.of(testCodelistRegistry));
        when(codelistRegistryRepository.save(any(CodelistRegistry.class))).thenReturn(testCodelistRegistry);

        // When
        codelistRegistryService.removeFlag(1L, 1L);

        // Then
        verify(codelistRegistryRepository, times(1)).save(any(CodelistRegistry.class));
    }

    @Test
    @DisplayName("Should throw exception when removing flag from non-existent codelist")
    void shouldThrowExceptionWhenRemovingFlagFromNonExistentCodelist() {
        // Given
        when(codelistRegistryRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> codelistRegistryService.removeFlag(999L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Codelist not found with id: 999");
    }

    @Test
    @DisplayName("Should create codelist registry entry with empty flags when no flags provided")
    void shouldCreateCodelistRegistryEntryWithEmptyFlagsWhenNoFlagsProvided() {
        // Given
        testCodelistRegistryDTO.setFlagIds(null);
        when(codelistRegistryRepository.existsByCode(anyString())).thenReturn(false);
        when(codelistRegistryRepository.save(any(CodelistRegistry.class))).thenAnswer(inv -> {
            CodelistRegistry cr = inv.getArgument(0);
            cr.setId(1L);
            return cr;
        });

        // When
        codelistRegistryService.create(testCodelistRegistryDTO);

        // Then
        verify(flagRepository, never()).findAllById(any());
    }
}
