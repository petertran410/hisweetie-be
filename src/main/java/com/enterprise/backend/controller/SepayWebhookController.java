// SepayWebhookController.java in com.enterprise.backend.controller
package com.enterprise.backend.controller;

import com.enterprise.backend.model.request.SepayWebhookRequest;
import com.enterprise.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/sepay-webhook")
@RequiredArgsConstructor
public class SepayWebhookController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> handleWebhook(@RequestBody SepayWebhookRequest webhookData) {
        log.info("Received webhook from SePay: {}", webhookData);
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean processed = paymentService.processPaymentWebhook(webhookData);
            if (processed) {
                response.put("success", true);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Transaction not processed");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}