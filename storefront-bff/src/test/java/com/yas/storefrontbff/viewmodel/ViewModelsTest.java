package com.yas.storefrontbff.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelsTest {

    @Test
    void shouldCreateAuthenticationViewModels() {
        AuthenticatedUserVm user = new AuthenticatedUserVm("customer");
        AuthenticationInfoVm authenticationInfo = new AuthenticationInfoVm(true, user);

        assertThat(authenticationInfo.isAuthenticated()).isTrue();
        assertThat(authenticationInfo.authenticatedUser().username()).isEqualTo("customer");
    }

    @Test
    void shouldCreateCartViewModels() {
        CartDetailVm detail = new CartDetailVm(1L, 2L, 3);
        CartGetDetailVm cart = new CartGetDetailVm(4L, "customer", List.of(detail));
        CartItemVm item = CartItemVm.fromCartDetailVm(detail);

        assertThat(cart.cartDetails()).containsExactly(detail);
        assertThat(item.productId()).isEqualTo(2L);
        assertThat(item.quantity()).isEqualTo(3);
    }

    @Test
    void shouldCreateGuestAndTokenViewModels() {
        GuestUserVm guest = new GuestUserVm("guest-id", "guest@example.com", "secret");
        TokenResponseVm token = new TokenResponseVm("access", "refresh");

        assertThat(guest.email()).isEqualTo("guest@example.com");
        assertThat(token.accessToken()).isEqualTo("access");
        assertThat(token.refreshToken()).isEqualTo("refresh");
    }
}
