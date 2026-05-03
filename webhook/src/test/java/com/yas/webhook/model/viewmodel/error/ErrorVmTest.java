package com.yas.webhook.model.viewmodel.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void shouldCreateErrorWithDefaultFieldErrors() {
        ErrorVm error = new ErrorVm("404", "Not found", "Missing");

        assertThat(error.statusCode()).isEqualTo("404");
        assertThat(error.title()).isEqualTo("Not found");
        assertThat(error.detail()).isEqualTo("Missing");
        assertThat(error.fieldErrors()).isEmpty();
    }

    @Test
    void shouldCreateErrorWithFieldErrors() {
        ErrorVm error = new ErrorVm("400", "Bad request", "Invalid", List.of("name"));

        assertThat(error.fieldErrors()).containsExactly("name");
    }
}
