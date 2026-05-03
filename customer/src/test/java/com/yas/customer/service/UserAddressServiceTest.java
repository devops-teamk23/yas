package com.yas.customer.service;

import static com.yas.customer.util.SecurityContextUtils.setUpSecurityContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.AccessDeniedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.customer.model.UserAddress;
import com.yas.customer.repository.UserAddressRepository;
import com.yas.customer.viewmodel.address.ActiveAddressVm;
import com.yas.customer.viewmodel.address.AddressDetailVm;
import com.yas.customer.viewmodel.address.AddressPostVm;
import com.yas.customer.viewmodel.address.AddressVm;
import com.yas.customer.viewmodel.useraddress.UserAddressVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserAddressServiceTest {

    @Mock
    private UserAddressRepository userAddressRepository;

    @Mock
    private LocationService locationService;

    private UserAddressService userAddressService;

    @BeforeEach
    void setUp() {
        userAddressService = new UserAddressService(userAddressRepository, locationService);
    }

    @Test
    void testGetUserAddressList_whenAnonymous_thenThrowAccessDenied() {
        setUpSecurityContext("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getUserAddressList());
    }

    @Test
    void testGetAddressDefault_whenAnonymous_thenThrowAccessDenied() {
        setUpSecurityContext("anonymousUser");

        assertThrows(AccessDeniedException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_whenNotFound_thenThrowNotFound() {
        setUpSecurityContext("user-1");
        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userAddressService.getAddressDefault());
    }

    @Test
    void testGetAddressDefault_whenFound_thenReturnAddressDetail() {
        setUpSecurityContext("user-1");
        UserAddress active = UserAddress.builder().id(11L).userId("user-1").addressId(200L).isActive(true).build();
        AddressDetailVm detail = new AddressDetailVm(200L, "John Doe", "0123", "Street", "City", "70000",
            1L, "District", 2L, "State", 3L, "Country");

        when(userAddressRepository.findByUserIdAndIsActiveTrue("user-1")).thenReturn(Optional.of(active));
        when(locationService.getAddressById(200L)).thenReturn(detail);

        AddressDetailVm result = userAddressService.getAddressDefault();

        assertThat(result).isEqualTo(detail);
    }

    @Test
    void testCreateAddress_whenFirstAddress_thenSetActiveTrue() {
        setUpSecurityContext("user-1");
        AddressPostVm postVm = new AddressPostVm("Jane", "0123", "Street", "City", "70000", 1L, 2L, 3L);
        AddressVm createdAddress = AddressVm.builder().id(300L).contactName("Jane").phone("0123")
            .addressLine1("Street").city("City").zipCode("70000").districtId(1L).stateOrProvinceId(2L)
            .countryId(3L).build();

        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of());
        when(locationService.createAddress(postVm)).thenReturn(createdAddress);
        when(userAddressRepository.save(org.mockito.ArgumentMatchers.any(UserAddress.class))).thenAnswer(invocation -> {
            UserAddress ua = invocation.getArgument(0);
            ua.setId(99L);
            return ua;
        });

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.isActive()).isTrue();
        assertThat(result.addressGetVm().id()).isEqualTo(300L);
    }

    @Test
    void testCreateAddress_whenUserHasAddresses_thenSetActiveFalse() {
        setUpSecurityContext("user-1");
        AddressPostVm postVm = new AddressPostVm("Jane", "0123", "Street", "City", "70000", 1L, 2L, 3L);
        AddressVm createdAddress = AddressVm.builder().id(301L).contactName("Jane").phone("0123")
            .addressLine1("Street").city("City").zipCode("70000").districtId(1L).stateOrProvinceId(2L)
            .countryId(3L).build();
        UserAddress existing = UserAddress.builder().id(1L).userId("user-1").addressId(100L).isActive(true).build();

        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(existing));
        when(locationService.createAddress(postVm)).thenReturn(createdAddress);
        when(userAddressRepository.save(org.mockito.ArgumentMatchers.any(UserAddress.class))).thenAnswer(invocation -> {
            UserAddress ua = invocation.getArgument(0);
            ua.setId(100L);
            return ua;
        });

        UserAddressVm result = userAddressService.createAddress(postVm);

        assertThat(result.userId()).isEqualTo("user-1");
        assertThat(result.isActive()).isFalse();
        assertThat(result.addressGetVm().id()).isEqualTo(301L);
    }

    @Test
    void testDeleteAddress_whenAddressNotFound_thenThrowNotFound() {
        setUpSecurityContext("user-1");
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 20L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> userAddressService.deleteAddress(20L));
    }

    @Test
    void testDeleteAddress_whenAddressExists_thenDelete() {
        setUpSecurityContext("user-1");
        UserAddress existing = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(false).build();
        when(userAddressRepository.findOneByUserIdAndAddressId("user-1", 20L)).thenReturn(existing);

        userAddressService.deleteAddress(20L);

        verify(userAddressRepository).delete(existing);
    }

    @Test
    void testChooseDefaultAddress_updatesActiveFlagsAndSavesAll() {
        setUpSecurityContext("user-1");
        UserAddress first = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(true).build();
        UserAddress second = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(false).build();
        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(first, second));

        userAddressService.chooseDefaultAddress(20L);

        ArgumentCaptor<List<UserAddress>> captor = ArgumentCaptor.forClass(List.class);
        verify(userAddressRepository).saveAll(captor.capture());
        List<UserAddress> savedList = captor.getValue();

        assertThat(savedList).hasSize(2);
        assertThat(savedList.get(0).getIsActive()).isFalse();
        assertThat(savedList.get(1).getIsActive()).isTrue();
    }

    @Test
    void testGetUserAddressList_mapsAndSortsActiveFirst() {
        setUpSecurityContext("user-1");
        UserAddress inactiveAddress = UserAddress.builder().id(1L).userId("user-1").addressId(10L).isActive(false).build();
        UserAddress activeAddress = UserAddress.builder().id(2L).userId("user-1").addressId(20L).isActive(true).build();

        AddressDetailVm detail10 = new AddressDetailVm(10L, "A", "1", "L1", "City", "70000", 1L,
            "D1", 11L, "S1", 111L, "C1");
        AddressDetailVm detail20 = new AddressDetailVm(20L, "B", "2", "L2", "City", "70001", 2L,
            "D2", 22L, "S2", 222L, "C2");

        when(userAddressRepository.findAllByUserId("user-1")).thenReturn(List.of(inactiveAddress, activeAddress));
        when(locationService.getAddressesByIdList(List.of(10L, 20L))).thenReturn(List.of(detail10, detail20));

        List<ActiveAddressVm> result = userAddressService.getUserAddressList();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(20L);
        assertThat(result.get(0).isActive()).isTrue();
        assertThat(result.get(1).id()).isEqualTo(10L);
        assertThat(result.get(1).isActive()).isFalse();
    }
}
