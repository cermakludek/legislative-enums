package cz.intelis.legislativeenums.cuzk.buildingtype;

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
@DisplayName("BuildingTypeService Unit Tests")
class BuildingTypeServiceTest {

    @Mock
    private BuildingTypeRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private BuildingTypeService buildingTypeService;

    private BuildingType testBuildingType;
    private BuildingTypeDTO testBuildingTypeDTO;

    @BeforeEach
    void setUp() {
        testBuildingType = new BuildingType();
        testBuildingType.setId(1L);
        testBuildingType.setCode("1");
        testBuildingType.setNameCs("budova s číslem popisným");
        testBuildingType.setNameEn("building with descriptive number");
        testBuildingType.setAbbreviation("ČP");
        testBuildingType.setEntryCode(true);
        testBuildingType.setValidFrom(LocalDate.of(2000, 1, 1));
        testBuildingType.setSortOrder(1);

        testBuildingTypeDTO = new BuildingTypeDTO();
        testBuildingTypeDTO.setId(1L);
        testBuildingTypeDTO.setCode("1");
        testBuildingTypeDTO.setNameCs("budova s číslem popisným");
        testBuildingTypeDTO.setNameEn("building with descriptive number");
        testBuildingTypeDTO.setAbbreviation("ČP");
        testBuildingTypeDTO.setEntryCode(true);
        testBuildingTypeDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testBuildingTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all building types")
    void shouldFindAllBuildingTypes() {
        // Given
        BuildingType bt2 = new BuildingType();
        bt2.setId(2L);
        bt2.setCode("2");
        bt2.setNameCs("budova s číslem evidenčním");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testBuildingType, bt2));

        // When
        List<BuildingTypeDTO> result = buildingTypeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find building type by ID")
    void shouldFindBuildingTypeById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingType));

        // When
        BuildingTypeDTO result = buildingTypeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("budova s číslem popisným");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building type not found by ID")
    void shouldThrowExceptionWhenBuildingTypeNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type not found with id: 999");
    }

    @Test
    @DisplayName("Should find building type by code")
    void shouldFindBuildingTypeByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testBuildingType));

        // When
        BuildingTypeDTO result = buildingTypeService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when building type not found by code")
    void shouldThrowExceptionWhenBuildingTypeNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new building type")
    void shouldCreateNewBuildingType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(BuildingType.class))).thenAnswer(inv -> {
            BuildingType bt = inv.getArgument(0);
            bt.setId(1L);
            return bt;
        });

        // When
        BuildingTypeDTO result = buildingTypeService.create(testBuildingTypeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(BuildingType.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Typy staveb"), eq("BUILDING_TYPE"), eq(1L), eq("1"), eq("budova s číslem popisným"));
    }

    @Test
    @DisplayName("Should throw exception when creating building type with existing code")
    void shouldThrowExceptionWhenCreatingBuildingTypeWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingTypeService.create(testBuildingTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing building type")
    void shouldUpdateExistingBuildingType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingType));
        when(repository.save(any(BuildingType.class))).thenReturn(testBuildingType);

        testBuildingTypeDTO.setNameCs("aktualizovaná budova");

        // When
        BuildingTypeDTO result = buildingTypeService.update(1L, testBuildingTypeDTO);

        // Then
        verify(repository, times(1)).save(any(BuildingType.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Typy staveb"), eq("BUILDING_TYPE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent building type")
    void shouldThrowExceptionWhenUpdatingNonExistentBuildingType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeService.update(999L, testBuildingTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type not found with id: 999");
    }

    @Test
    @DisplayName("Should delete building type")
    void shouldDeleteBuildingType() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingType));

        // When
        buildingTypeService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Typy staveb"), eq("BUILDING_TYPE"), eq(1L), eq("1"), eq("budova s číslem popisným"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent building type")
    void shouldThrowExceptionWhenDeletingNonExistentBuildingType() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve ČÚZK specific fields when creating building type")
    void shouldPreserveCuzkSpecificFieldsWhenCreatingBuildingType() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(BuildingType.class))).thenAnswer(inv -> {
            BuildingType bt = inv.getArgument(0);
            bt.setId(1L);
            return bt;
        });

        // When
        buildingTypeService.create(testBuildingTypeDTO);

        // Then
        ArgumentCaptor<BuildingType> captor = ArgumentCaptor.forClass(BuildingType.class);
        verify(repository).save(captor.capture());
        BuildingType saved = captor.getValue();
        assertThat(saved.getAbbreviation()).isEqualTo("ČP");
        assertThat(saved.getEntryCode()).isTrue();
        assertThat(saved.getValidFrom()).isEqualTo(LocalDate.of(2000, 1, 1));
    }
}
