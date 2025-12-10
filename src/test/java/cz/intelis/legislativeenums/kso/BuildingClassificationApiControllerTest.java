package cz.intelis.legislativeenums.kso;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuildingClassificationApiController Unit Tests")
class BuildingClassificationApiControllerTest {

    @Mock
    private BuildingClassificationService service;

    @InjectMocks
    private BuildingClassificationApiController controller;

    private BuildingClassificationDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new BuildingClassificationDTO();
        testDTO.setId(1L);
        testDTO.setCode("801");
        testDTO.setNameCs("Budovy občanské výstavby");
        testDTO.setNameEn("Civic buildings");
        testDTO.setLevel(1);
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all building classifications")
    void shouldReturnAllBuildingClassifications() {
        // Given
        BuildingClassificationDTO dto2 = new BuildingClassificationDTO();
        dto2.setId(2L);
        dto2.setCode("802");
        dto2.setNameCs("Budovy pro průmysl");
        dto2.setLevel(1);
        List<BuildingClassificationDTO> classifications = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(classifications);

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.findAll();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("801");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return building classifications tree")
    void shouldReturnBuildingClassificationsTree() {
        // Given
        BuildingClassificationDTO childDTO = new BuildingClassificationDTO();
        childDTO.setId(2L);
        childDTO.setCode("801.1");
        childDTO.setNameCs("Budovy pro zdravotní péči");
        childDTO.setLevel(2);
        childDTO.setParentId(1L);

        testDTO.setChildren(Arrays.asList(childDTO));
        when(service.findTree()).thenReturn(Arrays.asList(testDTO));

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.findTree();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getChildren()).hasSize(1);
        verify(service, times(1)).findTree();
    }

    @Test
    @DisplayName("Should return root level items")
    void shouldReturnRootLevelItems() {
        // Given
        when(service.findRootItems()).thenReturn(Arrays.asList(testDTO));

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.findRoots();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getLevel()).isEqualTo(1);
        verify(service, times(1)).findRootItems();
    }

    @Test
    @DisplayName("Should return children by parent ID")
    void shouldReturnChildrenByParentId() {
        // Given
        BuildingClassificationDTO childDTO = new BuildingClassificationDTO();
        childDTO.setId(2L);
        childDTO.setCode("801.1");
        childDTO.setNameCs("Budovy pro zdravotní péči");
        childDTO.setLevel(2);

        when(service.findChildren(1L)).thenReturn(Arrays.asList(childDTO));

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.findChildren(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("801.1");
        verify(service, times(1)).findChildren(1L);
    }

    @Test
    @DisplayName("Should return items by level")
    void shouldReturnItemsByLevel() {
        // Given
        when(service.findByLevel(1)).thenReturn(Arrays.asList(testDTO));

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.findByLevel(1);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getLevel()).isEqualTo(1);
        verify(service, times(1)).findByLevel(1);
    }

    @Test
    @DisplayName("Should search classifications")
    void shouldSearchClassifications() {
        // Given
        when(service.search("občan")).thenReturn(Arrays.asList(testDTO));

        // When
        ResponseEntity<List<BuildingClassificationDTO>> response = controller.search("občan");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).search("občan");
    }

    @Test
    @DisplayName("Should return building classification by ID")
    void shouldReturnBuildingClassificationById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingClassificationDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("801");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return building classification by code")
    void shouldReturnBuildingClassificationByCode() {
        // Given
        when(service.findByCode("801")).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingClassificationDTO> response = controller.findByCode("801");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("801");
        verify(service, times(1)).findByCode("801");
    }

    @Test
    @DisplayName("Should create new building classification")
    void shouldCreateNewBuildingClassification() {
        // Given
        when(service.create(any(BuildingClassificationDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingClassificationDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("801");
        verify(service, times(1)).create(any(BuildingClassificationDTO.class));
    }

    @Test
    @DisplayName("Should update existing building classification")
    void shouldUpdateExistingBuildingClassification() {
        // Given
        testDTO.setNameCs("Aktualizovaný název");
        when(service.update(eq(1L), any(BuildingClassificationDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<BuildingClassificationDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("Aktualizovaný název");
        verify(service, times(1)).update(eq(1L), any(BuildingClassificationDTO.class));
    }

    @Test
    @DisplayName("Should delete building classification")
    void shouldDeleteBuildingClassification() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
