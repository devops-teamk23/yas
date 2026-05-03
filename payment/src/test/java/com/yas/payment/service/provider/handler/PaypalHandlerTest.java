package com.yas.payment.service.provider.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaypalHandlerTest {

    @Mock
    private PaymentProviderService paymentProviderService;

    @Mock
    private PaypalService paypalService;

    @InjectMocks
    private PaypalHandler paypalHandler;

    @Test
    void getProviderId_shouldReturnPAYPAL() {
        assertEquals("PAYPAL", paypalHandler.getProviderId());
    }

    @Test
    void initPayment_shouldReturnInitiatedPayment() {
        InitPaymentRequestVm initRequest = new InitPaymentRequestVm("PAYPAL", BigDecimal.valueOf(100.0), "checkout123");
        PaypalCreatePaymentResponse paypalResponse = new PaypalCreatePaymentResponse(
                "COMPLETED", "pay123", "http://redirect.url");

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(anyString()))
                .thenReturn("settings");
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class)))
                .thenReturn(paypalResponse);

        InitiatedPayment result = paypalHandler.initPayment(initRequest);

        assertEquals("COMPLETED", result.getStatus());
        assertEquals("pay123", result.getPaymentId());
        assertEquals("http://redirect.url", result.getRedirectUrl());
    }

    @Test
    void capturePayment_shouldReturnCapturedPayment() {
        CapturePaymentRequestVm captureRequest = new CapturePaymentRequestVm("PAYPAL", "token123");
        PaypalCapturePaymentResponse paypalResponse = new PaypalCapturePaymentResponse(
                "checkout123", BigDecimal.valueOf(100.0), BigDecimal.valueOf(2.0),
                "txn123", "PAYPAL", "COMPLETED", null);

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId(anyString()))
                .thenReturn("settings");
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class)))
                .thenReturn(paypalResponse);

        CapturedPayment result = paypalHandler.capturePayment(captureRequest);

        assertEquals("checkout123", result.getCheckoutId());
        assertEquals(BigDecimal.valueOf(100.0), result.getAmount());
        assertEquals(BigDecimal.valueOf(2.0), result.getPaymentFee());
        assertEquals("txn123", result.getGatewayTransactionId());
        assertEquals(PaymentMethod.PAYPAL, result.getPaymentMethod());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentStatus());
        assertNull(result.getFailureMessage());
    }
}
