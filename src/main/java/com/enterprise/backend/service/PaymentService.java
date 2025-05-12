package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.TbOrder;
import com.enterprise.backend.model.entity.Transaction;
import com.enterprise.backend.model.request.SepayWebhookRequest;
import com.enterprise.backend.service.repository.TbOrderRepository;
import com.enterprise.backend.service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TransactionRepository transactionRepository;
    private final TbOrderRepository tbOrderRepository;
    
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("DH(\\d+)");
    
    @Transactional
    public boolean processPaymentWebhook(SepayWebhookRequest webhookData) {
        // 1. Save transaction record
        Transaction transaction = createTransactionFromWebhook(webhookData);
        transactionRepository.save(transaction);
        
        // 2. Extract order ID from content
        Long orderId = extractOrderId(webhookData.getContent());
        if (orderId == null) {
            log.warn("Could not extract order ID from content: {}", webhookData.getContent());
            return false;
        }
        
        // 3. Validate and update order
        return updateOrderIfValid(orderId, webhookData);
    }
    
    private Transaction createTransactionFromWebhook(SepayWebhookRequest webhookData) {
        BigDecimal amountIn = BigDecimal.ZERO;
        BigDecimal amountOut = BigDecimal.ZERO;
        
        if ("in".equals(webhookData.getTransferType())) {
            amountIn = webhookData.getTransferAmount();
        } else if ("out".equals(webhookData.getTransferType())) {
            amountOut = webhookData.getTransferAmount();
        }
        
        return Transaction.builder()
                .gateway(webhookData.getGateway())
                .transactionDate(webhookData.getTransactionDate())
                .accountNumber(webhookData.getAccountNumber())
                .subAccount(webhookData.getSubAccount())
                .amountIn(amountIn)
                .amountOut(amountOut)
                .accumulated(webhookData.getAccumulated())
                .code(webhookData.getCode())
                .transactionContent(webhookData.getContent())
                .referenceNumber(webhookData.getReferenceNumber())
                .body(webhookData.getDescription())
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    private Long extractOrderId(String content) {
        if (content == null) {
            return null;
        }
        
        Matcher matcher = ORDER_ID_PATTERN.matcher(content);
        if (matcher.find()) {
            try {
                return Long.parseLong(matcher.group(1));
            } catch (NumberFormatException e) {
                log.error("Failed to parse order ID: {}", matcher.group(1), e);
            }
        }
        
        return null;
    }
    
    private boolean updateOrderIfValid(Long orderId, SepayWebhookRequest webhookData) {
        return tbOrderRepository.findById(orderId)
                .filter(order -> 
                    order.getPaymentStatus() == TbOrder.PaymentStatus.Unpaid &&
                    order.getTotal().compareTo(webhookData.getTransferAmount()) == 0)
                .map(order -> {
                    order.setPaymentStatus(TbOrder.PaymentStatus.Paid);
                    tbOrderRepository.save(order);
                    log.info("Order ID {} has been marked as paid", orderId);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Order ID {} not found or payment validation failed", orderId);
                    return false;
                });
    }
    
    public TbOrder.PaymentStatus getOrderPaymentStatus(Long orderId) {
        return tbOrderRepository.findById(orderId)
                .map(TbOrder::getPaymentStatus)
                .orElse(null);
    }
}