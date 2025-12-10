package cz.intelis.legislativeenums.cuzk.buildingtypeuse;

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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingTypeUseService Unit Tests")
class BuildingTypeUseServiceTest {

    @Mock
    private BuildingTypeUseRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private BuildingTypeUseService buildingTypeUseService;

    private BuildingTypeUse testBuildingTypeUse;
    private BuildingTypeUseDTO testBuildingTypeUseDTO;

    @BeforeEach
    void setUp() {
        testBuildingTypeUse = new BuildingTypeUse();
        testBuildingTypeUse.setId(1L);
        testBuildingTypeUse.setBuildingTypeCode("1");
        testBuildingTypeUse.setBuildingUseCode("1");
        testBuildingTypeUseDTO = new BuildingTypeUseDTO();
        testBuildingTypeUseDTO.setId(1L);
        testBuildingTypeUseDTO.setBuildingTypeCode("1");
        testBuildingTypeUseDTO.setBuildingUseCode("1");
    }

    @Test
    @DisplayName("Should find all building type uses")
    void shouldFindAllBuildingTypeUses() {
        // Given
        BuildingTypeUse btu2 = new BuildingTypeUse();
        btu2.setId(2L);
        btu2.setBuildingTypeCode("2");
        btu2.setBuildingUseCode("2");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testBuildingTypeUse, btu2));

        // When
        List<BuildingTypeUseDTO> result = buildingTypeUseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getBuildingTypeCode()).isEqualTo("1");
        assertThat(result.get(1).getBuildingTypeCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find building type use by ID")
    void shouldFindBuildingTypeUseById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingTypeUse));

        // When
        BuildingTypeUseDTO result = buildingTypeUseService.findById(1L);

        // Then
        assertThat(result.getBuildingTypeCode()).isEqualTo("1");
        assertThat(result.getBuildingUseCode()).isEqualTo("1");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building type use not found by ID")
    void shouldThrowExceptionWhenBuildingTypeUseNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeUseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type use relation not found with id: 999");
    }

    @Test
    @DisplayName("Should find building type uses by building type code")
    void shouldFindBuildingTypeUsesByBuildingTypeCode() {
        // Given
        when(repository.findByBuildingTypeCode("1")).thenReturn(Arrays.asList(testBuildingTypeUse));

        // When
        List<BuildingTypeUseDTO> result = buildingTypeUseService.findByBuildingTypeCode("1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBuildingTypeCode()).isEqualTo("1");
        verify(repository, times(1)).findByBuildingTypeCode("1");
    }

    @Test
    @DisplayName("Should find building type uses by building use code")
    void shouldFindBuildingTypeUsesByBuildingUseCode() {
        // Given
        when(repository.findByBuildingUseCode("1")).thenReturn(Arrays.asList(testBuildingTypeUse));

        // When
        List<BuildingTypeUseDTO> result = buildingTypeUseService.findByBuildingUseCode("1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBuildingUseCode()).isEqualTo("1");
        verify(repository, times(1)).findByBuildingUseCode("1");
    }

    @Test
    @DisplayName("Should create new building type use")
    void shouldCreateNewBuildingTypeUse() {
        // Given
        when(repository.existsByBuildingTypeCodeAndBuildingUseCode(anyString(), anyString())).thenReturn(false);
        when(repository.save(any(BuildingTypeUse.class))).thenAnswer(inv -> {
            BuildingTypeUse btu = inv.getArgument(0);
            btu.setId(1L);
            return btu;
        });

        // When
        BuildingTypeUseDTO result = buildingTypeUseService.create(testBuildingTypeUseDTO);

        // Then
        assertThat(result.getBuildingTypeCode()).isEqualTo("1");
        assertThat(result.getBuildingUseCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(BuildingTypeUse.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Vazby typ stavby - využití"), eq("BUILDING_TYPE_USE"), eq(1L), eq("1-1"), eq("1-1"));
    }

    @Test
    @DisplayName("Should throw exception when creating building type use with existing relation")
    void shouldThrowExceptionWhenCreatingBuildingTypeUseWithExistingRelation() {
        // Given
        when(repository.existsByBuildingTypeCodeAndBuildingUseCode("1", "1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingTypeUseService.create(testBuildingTypeUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type use relation already exists for building type 1 and building use 1");
    }

    @Test
    @DisplayName("Should update existing building type use")
    void shouldUpdateExistingBuildingTypeUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingTypeUse));
        when(repository.save(any(BuildingTypeUse.class))).thenReturn(testBuildingTypeUse);
        // When
        BuildingTypeUseDTO result = buildingTypeUseService.update(1L, testBuildingTypeUseDTO);

        // Then
        verify(repository, times(1)).save(any(BuildingTypeUse.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Vazby typ stavby - využití"), eq("BUILDING_TYPE_USE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent building type use")
    void shouldThrowExceptionWhenUpdatingNonExistentBuildingTypeUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeUseService.update(999L, testBuildingTypeUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type use relation not found with id: 999");
    }

    @Test
    @DisplayName("Should delete building type use")
    void shouldDeleteBuildingTypeUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingTypeUse));

        // When
        buildingTypeUseService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Vazby typ stavby - využití"), eq("BUILDING_TYPE_USE"), eq(1L), eq("1-1"), eq("1-1"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent building type use")
    void shouldThrowExceptionWhenDeletingNonExistentBuildingTypeUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingTypeUseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building type use relation not found with id: 999");
    }

}
