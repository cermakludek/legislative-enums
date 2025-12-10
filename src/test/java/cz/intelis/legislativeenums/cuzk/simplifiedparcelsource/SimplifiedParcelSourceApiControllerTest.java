package cz.intelis.legislativeenums.cuzk.simplifiedparcelsource;

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
@DisplayName("SimplifiedParcelSourceApiController Unit Tests")
class SimplifiedParcelSourceApiControllerTest {

    @Mock
    private SimplifiedParcelSourceService service;

    @InjectMocks
    private SimplifiedParcelSourceApiController controller;

    private SimplifiedParcelSourceDTO testDTO;

    @BeforeEach
    void setUp() {
        testDTO = new SimplifiedParcelSourceDTO();
        testDTO.setId(1L);
        testDTO.setCode("1");
        testDTO.setNameCs("pozemkový katastr");
        testDTO.setNameEn("land cadastre");
        testDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should return all simplified parcel sources")
    void shouldReturnAllSimplifiedParcelSources() {
        // Given
        SimplifiedParcelSourceDTO dto2 = new SimplifiedParcelSourceDTO();
        dto2.setId(2L);
        dto2.setCode("2");
        dto2.setNameCs("evidence nemovitostí");
        List<SimplifiedParcelSourceDTO> simplifiedParcelSources = Arrays.asList(testDTO, dto2);
        when(service.findAll()).thenReturn(simplifiedParcelSources);

        // When
        ResponseEntity<List<SimplifiedParcelSourceDTO>> response = controller.findAll(false);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getCode()).isEqualTo("1");
        assertThat(response.getBody().get(1).getCode()).isEqualTo("2");
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return only currently valid simplified parcel sources when validOnly is true")
    void shouldReturnOnlyValidSimplifiedParcelSourcesWhenValidOnlyIsTrue() {
        // Given
        List<SimplifiedParcelSourceDTO> validSimplifiedParcelSources = Arrays.asList(testDTO);
        when(service.findAllCurrentlyValid()).thenReturn(validSimplifiedParcelSources);

        // When
        ResponseEntity<List<SimplifiedParcelSourceDTO>> response = controller.findAll(true);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        verify(service, times(1)).findAllCurrentlyValid();
    }

    @Test
    @DisplayName("Should return simplified parcel source by ID")
    void shouldReturnSimplifiedParcelSourceById() {
        // Given
        when(service.findById(1L)).thenReturn(testDTO);

        // When
        ResponseEntity<SimplifiedParcelSourceDTO> response = controller.findById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return simplified parcel source by code")
    void shouldReturnSimplifiedParcelSourceByCode() {
        // Given
        when(service.findByCode("1")).thenReturn(testDTO);

        // When
        ResponseEntity<SimplifiedParcelSourceDTO> response = controller.findByCode("1");

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).findByCode("1");
    }

    @Test
    @DisplayName("Should create new simplified parcel source")
    void shouldCreateNewSimplifiedParcelSource() {
        // Given
        when(service.create(any(SimplifiedParcelSourceDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<SimplifiedParcelSourceDTO> response = controller.create(testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getCode()).isEqualTo("1");
        verify(service, times(1)).create(any(SimplifiedParcelSourceDTO.class));
    }

    @Test
    @DisplayName("Should update existing simplified parcel source")
    void shouldUpdateExistingSimplifiedParcelSource() {
        // Given
        testDTO.setNameCs("aktualizovaný pozemkový katastr");
        when(service.update(eq(1L), any(SimplifiedParcelSourceDTO.class))).thenReturn(testDTO);

        // When
        ResponseEntity<SimplifiedParcelSourceDTO> response = controller.update(1L, testDTO);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getNameCs()).isEqualTo("aktualizovaný pozemkový katastr");
        verify(service, times(1)).update(eq(1L), any(SimplifiedParcelSourceDTO.class));
    }

    @Test
    @DisplayName("Should delete simplified parcel source")
    void shouldDeleteSimplifiedParcelSource() {
        // Given
        doNothing().when(service).delete(1L);

        // When
        ResponseEntity<Void> response = controller.delete(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(service, times(1)).delete(1L);
    }
}
