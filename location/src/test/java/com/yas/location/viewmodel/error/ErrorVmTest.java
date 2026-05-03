package com.yas.location.viewmodel.error;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testThreeArgConstructor_initializesEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid payload");

        assertEquals("400", vm.statusCode());
        assertEquals("Bad Request", vm.title());
        assertEquals("Invalid payload", vm.detail());
        assertNotNull(vm.fieldErrors());
        assertTrue(vm.fieldErrors().isEmpty());
    }

    @Test
    void testFourArgConstructor_preservesProvidedFieldErrors() {
        ErrorVm vm = new ErrorVm("422", "Validation Error", "Invalid fields", List.of("name is required"));

        assertEquals(1, vm.fieldErrors().size());
        assertEquals("name is required", vm.fieldErrors().getFirst());
    }
}
