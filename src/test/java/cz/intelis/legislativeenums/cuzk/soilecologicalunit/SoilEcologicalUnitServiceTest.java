package cz.intelis.legislativeenums.cuzk.soilecologicalunit;

import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SoilEcologicalUnitService Unit Tests")
class SoilEcologicalUnitServiceTest {

    @Mock
    private SoilEcologicalUnitRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private SoilEcologicalUnitService soilEcologicalUnitService;

    private SoilEcologicalUnit testSoilEcologicalUnit;
    private SoilEcologicalUnitDTO testSoilEcologicalUnitDTO;

    @BeforeEach
    void setUp() {
        testSoilEcologicalUnit = new SoilEcologicalUnit();
        testSoilEcologicalUnit.setId(1L);
        testSoilEcologicalUnit.setCode("00000");
        testSoilEcologicalUnit.setNameCs("BPEJ 00000");
        testSoilEcologicalUnit.setNameEn("BPEJ 00000");
        testSoilEcologicalUnit.setPrice(new BigDecimal("15.50"));
        testSoilEcologicalUnit.setDetailedDescription("Podrobný popis BPEJ");
        testSoilEcologicalUnit.setValidFrom(LocalDate.of(2000, 1, 1));
        testSoilEcologicalUnit.setSortOrder(1);

        testSoilEcologicalUnitDTO = new SoilEcologicalUnitDTO();
        testSoilEcologicalUnitDTO.setId(1L);
        testSoilEcologicalUnitDTO.setCode("00000");
        testSoilEcologicalUnitDTO.setNameCs("BPEJ 00000");
        testSoilEcologicalUnitDTO.setNameEn("BPEJ 00000");
        testSoilEcologicalUnitDTO.setPrice(new BigDecimal("15.50"));
        testSoilEcologicalUnitDTO.setDetailedDescription("Podrobný popis BPEJ");
        testSoilEcologicalUnitDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testSoilEcologicalUnitDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all soil ecological units")
    void shouldFindAllSoilEcologicalUnits() {
        // Given
        SoilEcologicalUnit seu2 = new SoilEcologicalUnit();
        seu2.setId(2L);
        seu2.setCode("00100");
        seu2.setNameCs("BPEJ 00100");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testSoilEcologicalUnit, seu2));

        // When
        List<SoilEcologicalUnitDTO> result = soilEcologicalUnitService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("00000");
        assertThat(result.get(1).getCode()).isEqualTo("00100");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find soil ecological unit by ID")
    void shouldFindSoilEcologicalUnitById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSoilEcologicalUnit));

        // When
        SoilEcologicalUnitDTO result = soilEcologicalUnitService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("00000");
        assertThat(result.getNameCs()).isEqualTo("BPEJ 00000");
        assertThat(result.getPrice()).isEqualTo(new BigDecimal("15.50"));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when soil ecological unit not found by ID")
    void shouldThrowExceptionWhenSoilEcologicalUnitNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> soilEcologicalUnitService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Soil ecological unit not found with id: 999");
    }

    @Test
    @DisplayName("Should find soil ecological unit by code")
    void shouldFindSoilEcologicalUnitByCode() {
        // Given
        when(repository.findByCode("00000")).thenReturn(Optional.of(testSoilEcologicalUnit));

        // When
        SoilEcologicalUnitDTO result = soilEcologicalUnitService.findByCode("00000");

        // Then
        assertThat(result.getCode()).isEqualTo("00000");
        verify(repository, times(1)).findByCode("00000");
    }

    @Test
    @DisplayName("Should throw exception when soil ecological unit not found by code")
    void shouldThrowExceptionWhenSoilEcologicalUnitNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> soilEcologicalUnitService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Soil ecological unit not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new soil ecological unit")
    void shouldCreateNewSoilEcologicalUnit() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(SoilEcologicalUnit.class))).thenAnswer(inv -> {
            SoilEcologicalUnit seu = inv.getArgument(0);
            seu.setId(1L);
            return seu;
        });

        // When
        SoilEcologicalUnitDTO result = soilEcologicalUnitService.create(testSoilEcologicalUnitDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("00000");
        verify(repository, times(1)).save(any(SoilEcologicalUnit.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("BPEJ"), eq("SOIL_ECOLOGICAL_UNIT"), eq(1L), eq("00000"), eq("BPEJ 00000"));
    }

    @Test
    @DisplayName("Should throw exception when creating soil ecological unit with existing code")
    void shouldThrowExceptionWhenCreatingSoilEcologicalUnitWithExistingCode() {
        // Given
        when(repository.existsByCode("00000")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> soilEcologicalUnitService.create(testSoilEcologicalUnitDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Soil ecological unit with code 00000 already exists");
    }

    @Test
    @DisplayName("Should update existing soil ecological unit")
    void shouldUpdateExistingSoilEcologicalUnit() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSoilEcologicalUnit));
        when(repository.save(any(SoilEcologicalUnit.class))).thenReturn(testSoilEcologicalUnit);

        testSoilEcologicalUnitDTO.setNameCs("aktualizovaná BPEJ");

        // When
        SoilEcologicalUnitDTO result = soilEcologicalUnitService.update(1L, testSoilEcologicalUnitDTO);

        // Then
        verify(repository, times(1)).save(any(SoilEcologicalUnit.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("BPEJ"), eq("SOIL_ECOLOGICAL_UNIT"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent soil ecological unit")
    void shouldThrowExceptionWhenUpdatingNonExistentSoilEcologicalUnit() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> soilEcologicalUnitService.update(999L, testSoilEcologicalUnitDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Soil ecological unit not found with id: 999");
    }

    @Test
    @DisplayName("Should delete soil ecological unit")
    void shouldDeleteSoilEcologicalUnit() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testSoilEcologicalUnit));

        // When
        soilEcologicalUnitService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("BPEJ"), eq("SOIL_ECOLOGICAL_UNIT"), eq(1L), eq("00000"), eq("BPEJ 00000"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent soil ecological unit")
    void shouldThrowExceptionWhenDeletingNonExistentSoilEcologicalUnit() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> soilEcologicalUnitService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Soil ecological unit not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve ČÚZK specific fields when creating soil ecological unit")
    void shouldPreserveCuzkSpecificFieldsWhenCreatingSoilEcologicalUnit() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(SoilEcologicalUnit.class))).thenAnswer(inv -> {
            SoilEcologicalUnit seu = inv.getArgument(0);
            seu.setId(1L);
            return seu;
        });

        // When
        soilEcologicalUnitService.create(testSoilEcologicalUnitDTO);

        // Then
        ArgumentCaptor<SoilEcologicalUnit> captor = ArgumentCaptor.forClass(SoilEcologicalUnit.class);
        verify(repository).save(captor.capture());
        SoilEcologicalUnit saved = captor.getValue();
        assertThat(saved.getPrice()).isEqualTo(new BigDecimal("15.50"));
        assertThat(saved.getDetailedDescription()).isEqualTo("Podrobný popis BPEJ");
    }
}
