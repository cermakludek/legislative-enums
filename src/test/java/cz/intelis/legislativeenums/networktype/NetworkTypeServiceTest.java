package cz.intelis.legislativeenums.networktype;

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
@DisplayName("NetworkTypeService Unit Tests")
class NetworkTypeServiceTest {

    @Mock
    private NetworkTypeRepository networkTypeRepository;

    @Mock
    private CodelistEventPublisher eventPublisher;

    @InjectMocks
    private NetworkTypeService networkTypeService;

    private NetworkType testNetworkType;
    private NetworkTypeDTO testNetworkTypeDTO;

    @BeforeEach
    void setUp() {
        testNetworkType = new NetworkType();
        testNetworkType.setId(1L);
        testNetworkType.setCode("NAD");
        testNetworkType.setNameCs("nadzemní vedení");
        testNetworkType.setNameEn("overhead line");
        testNetworkType.setDescriptionCs("Popis česky");
        testNetworkType.setDescriptionEn("Description English");
        testNetworkType.setSortOrder(1);

        testNetworkTypeDTO = new NetworkTypeDTO();
        testNetworkTypeDTO.setId(1L);
        testNetworkTypeDTO.setCode("NAD");
        testNetworkTypeDTO.setNameCs("nadzemní vedení");
        testNetworkTypeDTO.setNameEn("overhead line");
        testNetworkTypeDTO.setDescriptionCs("Popis česky");
        testNetworkTypeDTO.setDescriptionEn("Description English");
        testNetworkTypeDTO.setSortOrder(1);
    }

    @Test
    @DisplayName("Should find all network types")
    void shouldFindAllNetworkTypes() {
        // Given
        NetworkType nt2 = new NetworkType();
        nt2.setId(2L);
        nt2.setCode("POD");
        nt2.setNameCs("podzemní vedení");

        when(networkTypeRepository.findAllOrdered()).thenReturn(Arrays.asList(testNetworkType, nt2));

        // When
        List<NetworkTypeDTO> result = networkTypeService.findAll();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCode()).isEqualTo("NAD");
        assertThat(result.get(1).getCode()).isEqualTo("POD");
        verify(networkTypeRepository, times(1)).findAllOrdered();
    }

    @Test
    @DisplayName("Should find network type by ID")
    void shouldFindNetworkTypeById() {
        // Given
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));

        // When
        NetworkTypeDTO result = networkTypeService.findById(1L);

        // Then
        assertThat(result.getCode()).isEqualTo("NAD");
        assertThat(result.getNameCs()).isEqualTo("nadzemní vedení");
        verify(networkTypeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when network type not found by ID")
    void shouldThrowExceptionWhenNetworkTypeNotFoundById() {
        // Given
        when(networkTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> networkTypeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type not found with id: 999");
    }

    @Test
    @DisplayName("Should find network type by code")
    void shouldFindNetworkTypeByCode() {
        // Given
        when(networkTypeRepository.findByCode("NAD")).thenReturn(Optional.of(testNetworkType));

        // When
        NetworkTypeDTO result = networkTypeService.findByCode("NAD");

        // Then
        assertThat(result.getCode()).isEqualTo("NAD");
        verify(networkTypeRepository, times(1)).findByCode("NAD");
    }

    @Test
    @DisplayName("Should throw exception when network type not found by code")
    void shouldThrowExceptionWhenNetworkTypeNotFoundByCode() {
        // Given
        when(networkTypeRepository.findByCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> networkTypeService.findByCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type not found with code: INVALID");
    }

    @Test
    @DisplayName("Should create new network type")
    void shouldCreateNewNetworkType() {
        // Given
        when(networkTypeRepository.existsByCode(anyString())).thenReturn(false);
        when(networkTypeRepository.save(any(NetworkType.class))).thenAnswer(inv -> {
            NetworkType nt = inv.getArgument(0);
            nt.setId(1L);
            return nt;
        });

        // When
        NetworkTypeDTO result = networkTypeService.create(testNetworkTypeDTO);

        // Then
        assertThat(result.getCode()).isEqualTo("NAD");
        verify(networkTypeRepository, times(1)).save(any(NetworkType.class));
        verify(eventPublisher, times(1)).publishInsert(
                eq("Typy sítí"), eq("NETWORK_TYPE"), eq(1L), eq("NAD"), eq("nadzemní vedení"));
    }

    @Test
    @DisplayName("Should throw exception when creating network type with existing code")
    void shouldThrowExceptionWhenCreatingNetworkTypeWithExistingCode() {
        // Given
        when(networkTypeRepository.existsByCode("NAD")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> networkTypeService.create(testNetworkTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type with code NAD already exists");
    }

    @Test
    @DisplayName("Should update existing network type")
    void shouldUpdateExistingNetworkType() {
        // Given
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));
        when(networkTypeRepository.save(any(NetworkType.class))).thenReturn(testNetworkType);

        testNetworkTypeDTO.setNameCs("aktualizované vedení");

        // When
        NetworkTypeDTO result = networkTypeService.update(1L, testNetworkTypeDTO);

        // Then
        verify(networkTypeRepository, times(1)).save(any(NetworkType.class));
        verify(eventPublisher, times(1)).publishUpdate(
                eq("Typy sítí"), eq("NETWORK_TYPE"), eq(1L), anyString(), anyString());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent network type")
    void shouldThrowExceptionWhenUpdatingNonExistentNetworkType() {
        // Given
        when(networkTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> networkTypeService.update(999L, testNetworkTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type not found with id: 999");
    }

    @Test
    @DisplayName("Should throw exception when updating network type with existing code")
    void shouldThrowExceptionWhenUpdatingNetworkTypeWithExistingCode() {
        // Given
        testNetworkTypeDTO.setCode("NEWCODE");
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));
        when(networkTypeRepository.existsByCode("NEWCODE")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> networkTypeService.update(1L, testNetworkTypeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type with code NEWCODE already exists");
    }

    @Test
    @DisplayName("Should allow updating network type keeping same code")
    void shouldAllowUpdatingNetworkTypeKeepingSameCode() {
        // Given
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));
        when(networkTypeRepository.save(any(NetworkType.class))).thenReturn(testNetworkType);

        // Code remains "NAD" which is the same as existing
        testNetworkTypeDTO.setNameCs("Aktualizovaný název");

        // When
        NetworkTypeDTO result = networkTypeService.update(1L, testNetworkTypeDTO);

        // Then - should not throw exception
        verify(networkTypeRepository, times(1)).save(any(NetworkType.class));
    }

    @Test
    @DisplayName("Should delete network type")
    void shouldDeleteNetworkType() {
        // Given
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));

        // When
        networkTypeService.delete(1L);

        // Then
        verify(networkTypeRepository, times(1)).deleteById(1L);
        verify(eventPublisher, times(1)).publishDelete(
                eq("Typy sítí"), eq("NETWORK_TYPE"), eq(1L), eq("NAD"), eq("nadzemní vedení"));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent network type")
    void shouldThrowExceptionWhenDeletingNonExistentNetworkType() {
        // Given
        when(networkTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> networkTypeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Network type not found with id: 999");
    }

    @Test
    @DisplayName("Should preserve all fields when updating network type")
    void shouldPreserveAllFieldsWhenUpdatingNetworkType() {
        // Given
        when(networkTypeRepository.findById(1L)).thenReturn(Optional.of(testNetworkType));
        when(networkTypeRepository.save(any(NetworkType.class))).thenAnswer(inv -> inv.getArgument(0));

        testNetworkTypeDTO.setDescriptionCs("Nový popis");
        testNetworkTypeDTO.setDescriptionEn("New description");
        testNetworkTypeDTO.setSortOrder(5);

        // When
        networkTypeService.update(1L, testNetworkTypeDTO);

        // Then
        ArgumentCaptor<NetworkType> captor = ArgumentCaptor.forClass(NetworkType.class);
        verify(networkTypeRepository).save(captor.capture());
        NetworkType saved = captor.getValue();
        assertThat(saved.getDescriptionCs()).isEqualTo("Nový popis");
        assertThat(saved.getDescriptionEn()).isEqualTo("New description");
        assertThat(saved.getSortOrder()).isEqualTo(5);
    }
}
