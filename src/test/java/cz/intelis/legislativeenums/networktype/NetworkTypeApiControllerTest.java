package cz.intelis.legislativeenums.networktype;

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
@DisplayName("NetworkTypeApiController Unit Tests")
class NetworkTypeApiControllerTest {

    @Mock
    private NetworkTypeService networkTypeService;

    @InjectMocks
    private NetworkTypeApiController controller;

    private NetworkTypeDTO testNetworkTypeDTO;

    @BeforeEach
    void setUp() {
        testNetworkTypeDTO = new NetworkTypeDTO();
        testNetworkTypeDTO.setId(1L);
        testNetworkTypeDTO.setCode("ELE");
        testNetworkTypeDTO.setNameCs("Elektrická síť");
        testNetworkTypeDTO.setNameEn("Electric network");
        testNetworkTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all network types")
    void shouldReturnAllNetworkTypes() {
        // Given
        NetworkTypeDTO dto2 = new NetworkTypeDTO();
        dto2.setId(2L);
        dto2.setCode("GAS");
        dto2.setNameCs("Plynová síť");
        List<NetworkTypeDTO> networkTypes = Arrays.asList(testNetworkTypeDTO, dto2);
        when(networkTypeService.findAll()).thenReturn(networkTypes);

        // When
        ResponseEntity<List<NetworkTypeDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("ELE");
        verify(networkTypeService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid network types when validOnly is true")
    void shouldReturnOnlyValidNetworkTypesWhenValidOnlyIsTrue() {
        // Given
        List<NetworkTypeDTO> validNetworkTypes = Arrays.asList(testNetworkTypeDTO);
        when(networkTypeService.findAllCurrentlyValid()).thenReturn(validNetworkTypes);

        // When
        ResponseEntity<List<NetworkTypeDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(networkTypeService, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return network type by ID")
    void shouldReturnNetworkTypeById() {
        // Given
        when(networkTypeService.findById(1L)).thenReturn(testNetworkTypeDTO);

        // When
        ResponseEntity<NetworkTypeDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("ELE");
        verify(networkTypeService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return network type by code")
    void shouldReturnNetworkTypeByCode() {
        // Given
        when(networkTypeService.findByCode("ELE")).thenReturn(testNetworkTypeDTO);

        // When
        ResponseEntity<NetworkTypeDTO> response = controller.findByCode("ELE");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("ELE");
        verify(networkTypeService, times(1)).findByCode("ELE");
    }

    @Test
    @DisplayName("Should create new network type")
    void shouldCreateNewNetworkType() {
        // Given
        when(networkTypeService.create(any(NetworkTypeDTO.class))).thenReturn(testNetworkTypeDTO);

        // When
        ResponseEntity<NetworkTypeDTO> response = controller.create(testNetworkTypeDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("ELE");
        verify(networkTypeService, times(1)).create(any(NetworkTypeDTO.class));
    }

    @Test
    @DisplayName("Should update existing network type")
    void shouldUpdateExistingNetworkType() {
        // Given
        testNetworkTypeDTO.setNameCs("Aktualizovaná elektrická síť");
        when(networkTypeService.update(eq(1L), any(NetworkTypeDTO.class))).thenReturn(testNetworkTypeDTO);

        // When
        ResponseEntity<NetworkTypeDTO> response = controller.update(1L, testNetworkTypeDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("Aktualizovaná elektrická síť");
        verify(networkTypeService, times(1)).update(eq(1L), any(NetworkTypeDTO.class));
    }

    @Test
    @DisplayName("Should delete network type")
    void shouldDeleteNetworkType() {
        // Given
        doNothing().when(networkTypeService).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(networkTypeService, times(1)).delete(1L);
    }
}
