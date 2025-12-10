package cz.intelis.legislativeenums.kso;

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
@DisplayName("BuildingClassificationService Unit Tests")
class BuildingClassificationServiceTest {

    @Mock
    private BuildingClassificationRepository repository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private BuildingClassificationService buildingClassificationService;

    private BuildingClassification testBuildingClassification;
    private BuildingClassificationDTO testBuildingClassificationDTO;
    private BuildingClassification parentClassification;

    @BeforeEach
    void setUp() {
        parentClassification = new BuildingClassification();
        parentClassification.setId(10L);
        parentClassification.setCode("801");
        parentClassification.setNameCs("Budovy občanské výstavby");
        parentClassification.setNameEn("Civic buildings");
        parentClassification.setLevel(1);

        testBuildingClassification = new BuildingClassification();
        testBuildingClassification.setId(1L);
        testBuildingClassification.setCode("801.1");
        testBuildingClassification.setNameCs("Budovy pro zdravotní péči");
        testBuildingClassification.setNameEn("Healthcare buildings");
        testBuildingClassification.setDescriptionCs("Popis česky");
        testBuildingClassification.setDescriptionEn("Description English");
        testBuildingClassification.setLevel(2);
        testBuildingClassification.setParent(parentClassification);
        testBuildingClassification.setSortOrder(1);

        testBuildingClassificationDTO = new BuildingClassificationDTO();
        testBuildingClassificationDTO.setId(1L);
        testBuildingClassificationDTO.setCode("801.1");
        testBuildingClassificationDTO.setNameCs("Budovy pro zdravotní péči");
        testBuildingClassificationDTO.setNameEn("Healthcare buildings");
        testBuildingClassificationDTO.setDescriptionCs("Popis česky");
        testBuildingClassificationDTO.setDescriptionEn("Description English");
        testBuildingClassificationDTO.setLevel(2);
        testBuildingClassificationDTO.setParentId(10L);
        testBuildingClassificationDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all building classifications")
    void shouldFindAllBuildingClassifications() {
        // Given
        BuildingClassification bc2 = new BuildingClassification();
        bc2.setId(2L);
        bc2.setCode("801.2");
        bc2.setNameCs("Budovy pro vzdělávání");
        bc2.setLevel(2);

        when(repository.findAllOrdered()).thenReturn(Arrays.asList(testBuildingClassification, bc2));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("801.1");
        assertThat(result.get(1).getCode()).isEqualTo("801.2");
        verify(repository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find tree structure")
    void shouldFindTree() {
        // Given
        parentClassification.getChildren().add(testBuildingClassification);
        when(repository.findRootItems()).thenReturn(Arrays.asList(parentClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.findTree();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("801");
        verify(repository, times(1)).findRootItems();
    }

    @Test
    @DisplayName("Should find root items")
    void shouldFindRootItems() {
        // Given
        when(repository.findRootItems()).thenReturn(Arrays.asList(parentClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.findRootItems();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLevel()).isEqualTo(1);
        verify(repository, times(1)).findRootItems();
    }

    @Test
    @DisplayName("Should find children by parent ID")
    void shouldFindChildrenByParentId() {
        // Given
        when(repository.findByParentId(10L)).thenReturn(Arrays.asList(testBuildingClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.findChildren(10L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCode()).isEqualTo("801.1");
        verify(repository, times(1)).findByParentId(10L);
    }

    @Test
    @DisplayName("Should find by level")
    void shouldFindByLevel() {
        // Given
        when(repository.findByLevel(2)).thenReturn(Arrays.asList(testBuildingClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.findByLevel(2);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLevel()).isEqualTo(2);
        verify(repository, times(1)).findByLevel(2);
    }

    @Test
    @DisplayName("Should search by query")
    void shouldSearchByQuery() {
        // Given
        when(repository.search("zdravotní")).thenReturn(Arrays.asList(testBuildingClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.search("zdravotní");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNameCs()).contains("zdravotní");
        verify(repository, times(1)).search("zdravotní");
    }

    @Test
    @DisplayName("Should find building classification by ID")
    void shouldFindBuildingClassificationById() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingClassification));

        // When
        BuildingClassificationDTO result = buildingClassificationService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("801.1");
        assertThat(result.getNameCs()).isEqualTo("Budovy pro zdravotní péči");
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when building classification not found by ID")
    void shouldThrowExceptionWhenBuildingClassificationNotFoundById() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification not found with id: 999");
    }

    @Test
    @DisplayName("Should find building classification by code")
    void shouldFindBuildingClassificationByCode() {
        // Given
        when(repository.findByCode("801.1")).thenReturn(Optional.of(testBuildingClassification));

        // When
        BuildingClassificationDTO result = buildingClassificationService.findByCode("801.1");

        // Then
        assertThat(result.getCode()).isEqualTo("801.1");
        verify(repository, times(1)).findByCode("801.1");
    }

    @Test
    @DisplayName("Should throw exception when building classification not found by code")
    void shouldThrowExceptionWhenBuildingClassificationNotFoundByCode() {
        // Given
        when(repository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new building classification")
    void shouldCreateNewBuildingClassification() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.findById(10L)).thenReturn(Optional.of(parentClassification));
        when(repository.save(any(BuildingClassification.class))).thenAnswer(inv -> {
            BuildingClassification bc = inv.getArgument(0);
            bc.setId(1L);
            return bc;
        });

        // When
        BuildingClassificationDTO result = buildingClassificationService.create(testBuildingClassificationDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("801.1");
        verify(repository, times(1)).save(any(BuildingClassification.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Klasifikace staveb (KSO)"), eq("BUILDING_CLASSIFICATION"), eq(1L), eq("801.1"), eq("Budovy pro zdravotní péči"));
    }

    @Test
    @DisplayName("Should throw exception when creating building classification with existing code")
    void shouldThrowExceptionWhenCreatingBuildingClassificationWithExistingCode() {
        // Given
        when(repository.existsByCode("801.1")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.create(testBuildingClassificationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification with code 801.1 already exists");
    }

    @Test
    @DisplayName("Should update existing building classification")
    void shouldUpdateExistingBuildingClassification() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingClassification));
        when(repository.findById(10L)).thenReturn(Optional.of(parentClassification));
        when(repository.save(any(BuildingClassification.class))).thenReturn(testBuildingClassification);

        testBuildingClassificationDTO.setNameCs("Aktualizovaný název");

        // When
        BuildingClassificationDTO result = buildingClassificationService.update(1L, testBuildingClassificationDTO);

        // Then
        verify(repository, times(1)).save(any(BuildingClassification.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Klasifikace staveb (KSO)"), eq("BUILDING_CLASSIFICATION"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent building classification")
    void shouldThrowExceptionWhenUpdatingNonExistentBuildingClassification() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.update(999L, testBuildingClassificationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when updating building classification with existing code")
    void shouldThrowExceptionWhenUpdatingBuildingClassificationWithExistingCode() {
        // Given
        testBuildingClassificationDTO.setCode("NEWCODE");
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingClassification));
        when(repository.existsByCode("NEWCODE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.update(1L, testBuildingClassificationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification with code NEWCODE already exists");
    }

    @Test
    @DisplayName("Should delete building classification")
    void shouldDeleteBuildingClassification() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(testBuildingClassification));
        when(repository.hasChildren(1L)).thenReturn(false);

        // When
        buildingClassificationService.delete(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Klasifikace staveb (KSO)"), eq("BUILDING_CLASSIFICATION"), eq(1L), eq("801.1"), eq("Budovy pro zdravotní péči"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent building classification")
    void shouldThrowExceptionWhenDeletingNonExistentBuildingClassification() {
        // Given
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Building classification not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when deleting building classification with children")
    void shouldThrowExceptionWhenDeletingBuildingClassificationWithChildren() {
        // Given
        when(repository.findById(10L)).thenReturn(Optional.of(parentClassification));
        when(repository.hasChildren(10L)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.delete(10L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot delete classification with children");
    }

    @Test
    @DisplayName("Should get possible parents for level")
    void shouldGetPossibleParentsForLevel() {
        // Given
        when(repository.findByLevel(1)).thenReturn(Arrays.asList(parentClassification));

        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.getPossibleParents(2);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLevel()).isEqualTo(1);
        verify(repository, times(1)).findByLevel(1);
    }

    @Test
    @DisplayName("Should return empty list for level 1 possible parents")
    void shouldReturnEmptyListForLevel1PossibleParents() {
        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.getPossibleParents(1);

        // Then
        assertThat(result).isEmpty();
        verify(repository, never()).findByLevel(any());
    }

    @Test
    @DisplayName("Should return empty list for null level possible parents")
    void shouldReturnEmptyListForNullLevelPossibleParents() {
        // When
        List<BuildingClassificationDTO> result = buildingClassificationService.getPossibleParents(null);

        // Then
        assertThat(result).isEmpty();
        verify(repository, never()).findByLevel(any());
    }

    @Test
    @DisplayName("Should throw exception when parent not found during create")
    void shouldThrowExceptionWhenParentNotFoundDuringCreate() {
        // Given
        when(repository.existsByCode(anyString())).thenReturn(false);
        when(repository.findById(10L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> buildingClassificationService.create(testBuildingClassificationDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Parent classification not found with id: 10");
    }
}
