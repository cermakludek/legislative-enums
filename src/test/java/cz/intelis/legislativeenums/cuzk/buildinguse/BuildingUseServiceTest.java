package cz.intelis.legislativeenums.cuzk.buildinguse;

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
@DisplayName("BuildingUseService Unit Tests")
class BuildingUseServiceTest {

    @Mock
    private BuildingUseRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private BuildingUseService buildingUseService;

    private BuildingUse testBuildingUse;
    private BuildingUseDTO testBuildingUseDTO;

    @BeforeEach
    void setUp() {
        testBuildingUse = new BuildingUse();
        testBuildingUse.setId(1L);
        testBuildingUse.setCode("1");
        testBuildingUse.setNameCs("bydlení");
        testBuildingUse.setNameEn("housing");
        testBuildingUse.setAbbreviation("BYD");
        testBuildingUse.setValidFrom(LocalDate.of(2000, 1, 1));
        testBuildingUse.setSortOrder(1);

        testBuildingUseDTO = new BuildingUseDTO();
        testBuildingUseDTO.setId(1L);
        testBuildingUseDTO.setCode("1");
        testBuildingUseDTO.setNameCs("bydlení");
        testBuildingUseDTO.setNameEn("housing");
        testBuildingUseDTO.setAbbreviation("BYD");
        testBuildingUseDTO.setValidFrom(LocalDate.of(2000, 1, 1));
        testBuildingUseDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all building uses")
    void shouldFindAllBuildingUses() {
        // Given
        BuildingUse bu2 = new BuildingUse();
        bu2.setId(2L);
        bu2.setCode("2");
        bu2.setNameCs("rodinná rekreace");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testBuildingUse, bu2));

        // When
        List<BuildingUseDTO> result = buildingUseService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find building use by ID")
    void shouldFindBuildingUseById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingUse));

        // When
        BuildingUseDTO result = buildingUseService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("bydlení");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building use not found by ID")
    void shouldThrowExceptionWhenBuildingUseNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingUseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building use not found with id: 999");
    }

    @Test
    @DisplayName("Should find building use by code")
    void shouldFindBuildingUseByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testBuildingUse));

        // When
        BuildingUseDTO result = buildingUseService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when building use not found by code")
    void shouldThrowExceptionWhenBuildingUseNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingUseService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building use not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new building use")
    void shouldCreateNewBuildingUse() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(BuildingUse.class))).thenAnswer(inv -> {
            BuildingUse bu = inv.getArgument(0);
            bu.setId(1L);
            return bu;
        });

        // When
        BuildingUseDTO result = buildingUseService.create(testBuildingUseDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(BuildingUse.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Způsoby využití stavby"), eq("BUILDING_USE"), eq(1L), eq("1"), eq("bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when creating building use with existing code")
    void shouldThrowExceptionWhenCreatingBuildingUseWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingUseService.create(testBuildingUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building use with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing building use")
    void shouldUpdateExistingBuildingUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingUse));
        when(repository.save(any(BuildingUse.class))).thenReturn(testBuildingUse);

        testBuildingUseDTO.setNameCs("aktualizované bydlení");

        // When
        BuildingUseDTO result = buildingUseService.update(1L, testBuildingUseDTO);

        // Then
        verify(repository, times(1)).save(any(BuildingUse.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Způsoby využití stavby"), eq("BUILDING_USE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent building use")
    void shouldThrowExceptionWhenUpdatingNonExistentBuildingUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingUseService.update(999L, testBuildingUseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building use not found with id: 999");
    }

    @Test
    @DisplayName("Should delete building use")
    void shouldDeleteBuildingUse() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingUse));

        // When
        buildingUseService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Způsoby využití stavby"), eq("BUILDING_USE"), eq(1L), eq("1"), eq("bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent building use")
    void shouldThrowExceptionWhenDeletingNonExistentBuildingUse() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingUseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building use not found with id: 999");
    }

}
