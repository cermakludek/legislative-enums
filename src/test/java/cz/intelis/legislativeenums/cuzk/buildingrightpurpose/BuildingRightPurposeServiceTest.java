package cz.intelis.legislativeenums.cuzk.buildingrightpurpose;

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
@DisplayName("BuildingRightPurposeService Unit Tests")
class BuildingRightPurposeServiceTest {

    @Mock
    private BuildingRightPurposeRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private BuildingRightPurposeService buildingRightPurposeService;

    private BuildingRightPurpose testBuildingRightPurpose;
    private BuildingRightPurposeDTO testBuildingRightPurposeDTO;

    @BeforeEach
    void setUp() {
        testBuildingRightPurpose = new BuildingRightPurpose();
        testBuildingRightPurpose.setId(1L);
        testBuildingRightPurpose.setCode("1");
        testBuildingRightPurpose.setNameCs("stavba pro bydlení");
        testBuildingRightPurpose.setNameEn("building for housing");
        testBuildingRightPurpose.setValidFrom(LocalDate.of(2014, 1, 1));
        testBuildingRightPurpose.setSortOrder(1);

        testBuildingRightPurposeDTO = new BuildingRightPurposeDTO();
        testBuildingRightPurposeDTO.setId(1L);
        testBuildingRightPurposeDTO.setCode("1");
        testBuildingRightPurposeDTO.setNameCs("stavba pro bydlení");
        testBuildingRightPurposeDTO.setNameEn("building for housing");
        testBuildingRightPurposeDTO.setValidFrom(LocalDate.of(2014, 1, 1));
        testBuildingRightPurposeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all building right purposes")
    void shouldFindAllBuildingRightPurposes() {
        // Given
        BuildingRightPurpose brp2 = new BuildingRightPurpose();
        brp2.setId(2L);
        brp2.setCode("2");
        brp2.setNameCs("stavba pro podnikání");
        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testBuildingRightPurpose, brp2));

        // When
        List<BuildingRightPurposeDTO> result = buildingRightPurposeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("1");
        assertThat(result.get(1).getCode()).isEqualTo("2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find building right purpose by ID")
    void shouldFindBuildingRightPurposeById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingRightPurpose));

        // When
        BuildingRightPurposeDTO result = buildingRightPurposeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        assertThat(result.getNameCs()).isEqualTo("stavba pro bydlení");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building right purpose not found by ID")
    void shouldThrowExceptionWhenBuildingRightPurposeNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingRightPurposeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building right purpose not found with id: 999");
    }

    @Test
    @DisplayName("Should find building right purpose by code")
    void shouldFindBuildingRightPurposeByCode() {
        // Given
        when(repository.findByCode("1")).thenReturn(Optional.of(testBuildingRightPurpose));

        // When
        BuildingRightPurposeDTO result = buildingRightPurposeService.findByCode("1");

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should throw exception when building right purpose not found by code")
    void shouldThrowExceptionWhenBuildingRightPurposeNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingRightPurposeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building right purpose not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new building right purpose")
    void shouldCreateNewBuildingRightPurpose() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.save(any(BuildingRightPurpose.class))).thenAnswer(inv -> {
            BuildingRightPurpose brp = inv.getArgument(0);
            brp.setId(1L);
            return brp;
        });

        // When
        BuildingRightPurposeDTO result = buildingRightPurposeService.create(testBuildingRightPurposeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("1");
        verify(repository, times(1)).save(any(BuildingRightPurpose.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Účely práva stavby"), eq("BUILDING_RIGHT_PURPOSE"), eq(1L), eq("1"), eq("stavba pro bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when creating building right purpose with existing code")
    void shouldThrowExceptionWhenCreatingBuildingRightPurposeWithExistingCode() {
        // Given
        when(repository.existsByCode("1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingRightPurposeService.create(testBuildingRightPurposeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building right purpose with code 1 already exists");
    }

    @Test
    @DisplayName("Should update existing building right purpose")
    void shouldUpdateExistingBuildingRightPurpose() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingRightPurpose));
        when(repository.save(any(BuildingRightPurpose.class))).thenReturn(testBuildingRightPurpose);

        testBuildingRightPurposeDTO.setNameCs("aktualizovaný účel");

        // When
        BuildingRightPurposeDTO result = buildingRightPurposeService.update(1L, testBuildingRightPurposeDTO);

        // Then
        verify(repository, times(1)).save(any(BuildingRightPurpose.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Účely práva stavby"), eq("BUILDING_RIGHT_PURPOSE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent building right purpose")
    void shouldThrowExceptionWhenUpdatingNonExistentBuildingRightPurpose() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingRightPurposeService.update(999L, testBuildingRightPurposeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building right purpose not found with id: 999");
    }

    @Test
    @DisplayName("Should delete building right purpose")
    void shouldDeleteBuildingRightPurpose() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingRightPurpose));

        // When
        buildingRightPurposeService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Účely práva stavby"), eq("BUILDING_RIGHT_PURPOSE"), eq(1L), eq("1"), eq("stavba pro bydlení"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent building right purpose")
    void shouldThrowExceptionWhenDeletingNonExistentBuildingRightPurpose() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingRightPurposeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building right purpose not found with id: 999");
    }

}
