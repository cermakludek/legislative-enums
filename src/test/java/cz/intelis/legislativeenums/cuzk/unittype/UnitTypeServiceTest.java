package cz.intelis.legislativeenums.cuzk.unittype;

import cz.intelis.legislativeenums.notification.CodelistEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
@DisplayName("UnitTypeService Unit Tests")
class UnitTypeServiceTest {

    @Mock
    private UnitTypeRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private UnitTypeService unitTypeService;

    private UnitType testUnitType;
    private UnitTypeDTO testUnitTypeDTO;

    @BeforeEach
    void setUp() {
        testUnitType = new UnitType();
        testUnitType.setId(1L);
        testUnitType.setCode("1");
        testUnitType.setNameCs("byt");
        testUnitType.setNameEn("apartment");
        testUnitType.setAbbreviation("B");
        testUnitType.setCivilCode(true);
        testUnitType.setValidFrom(LocalDate.of(2014, 1, 1));
        testUnitType.setSortOrder(1);

        testUnitTypeDTO = new UnitTypeDTO();
        testUnitTypeDTO.setId(1L);
        testUnitTypeDTO.setCode("1");
        testUnitTypeDTO.setNameCs("byt");
        testUnitTypeDTO.setNameEn("apartment");
        testUnitTypeDTO.setAbbreviation("B");
        testUnitTypeDTO.setCivilCode(true);
        testUnitTypeDTO.setValidFrom(LocalDate.of(2014, 1, 1));
        testUnitTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all unit types")
    void shouldFindAllUnitTypes() {
        // Given
        UnitType ut2 = new UnitType();
        ut2.setId(2L);
        ut2.setCode("2");
        ut2.setNameCs("nebytový prostor");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testUnitType, ut2));

        // When
        List<UnitTypeDTO> result = unitTypeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find unit type by ID")
    void shouldFindUnitTypeById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitType));

        // When
        UnitTypeDTO result = unitTypeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("byt");
        assertThat(result.getCivilCode()).isTrue();
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when unit type not found by ID")
    void shouldThrowExceptionWhenUnitTypeNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitTypeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit type not found with id: 999");
    }

    @Test
    @DisplayName("Should find unit type by code")
    void shouldFindUnitTypeByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testUnitType));

        // When
        UnitTypeDTO result = unitTypeService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when unit type not found by code")
    void shouldThrowExceptionWhenUnitTypeNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitTypeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit type not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new unit type")
    void shouldCreateNewUnitType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(UnitType.class))).thenAnswer(inv -> {
            UnitType ut = inv.getArgument(0);
            ut.setId(1L);
            return ut;
        });

        // When
        UnitTypeDTO result = unitTypeService.create(testUnitTypeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(UnitType.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Typy jednotek"), eq("UNIT_TYPE"), eq(1L), eq("1"), eq("byt"));
    }

    @Test
    @DisplayName("Should throw exception when creating unit type with existing code")
    void shouldThrowExceptionWhenCreatingUnitTypeWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> unitTypeService.create(testUnitTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit type with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing unit type")
    void shouldUpdateExistingUnitType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitType));
        when(repository.save(any(UnitType.class))).thenReturn(testUnitType);

        testUnitTypeDTO.setNameCs("aktualizovaný byt");

        // When
        UnitTypeDTO result = unitTypeService.update(1L, testUnitTypeDTO);

        // Then
        verify(repository, times(1)).save(any(UnitType.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Typy jednotek"), eq("UNIT_TYPE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent unit type")
    void shouldThrowExceptionWhenUpdatingNonExistentUnitType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitTypeService.update(999L, testUnitTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit type not found with id: 999");
    }

    @Test
    @DisplayName("Should delete unit type")
    void shouldDeleteUnitType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testUnitType));

        // When
        unitTypeService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Typy jednotek"), eq("UNIT_TYPE"), eq(1L), eq("1"), eq("byt"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent unit type")
    void shouldThrowExceptionWhenDeletingNonExistentUnitType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> unitTypeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unit type not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve civilCode field when creating unit type")
    void shouldPreserveCivilCodeFieldWhenCreatingUnitType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(UnitType.class))).thenAnswer(inv -> {
            UnitType ut = inv.getArgument(0);
            ut.setId(1L);
            return ut;
        });

        // When
        unitTypeService.create(testUnitTypeDTO);

        // Then
        ArgumentCaptor<UnitType> captor = ArgumentCaptor.forClass(UnitType.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getCivilCode()).isTrue();
    }
}
