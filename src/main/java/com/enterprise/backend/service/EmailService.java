package com.enterprise.backend.service;

import com.enterprise.backend.model.entity.Authority;
import com.enterprise.backend.model.entity.ProductOrder;
import com.enterprise.backend.model.entity.User;
import com.enterprise.backend.model.enums.OrderStatus;
import com.enterprise.backend.model.request.ContactRequest;
import com.enterprise.backend.util.Constants;
import com.enterprise.backend.util.HTMLTemplateReader;
import com.enterprise.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Set;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${app.admin.mail}")
    private String adminEmail;

    @Value("${app.admin.phone}")
    private String adminPhone;

    @Value("${app.front-end.domain}")
    private String domainFrontEnd;

    @Value("${app.front-end.cms}")
    private String cmsUrl;

    private String buildForgotPasswordMessage(String codeResetPassword) {
        StringBuilder builder = new StringBuilder();
        builder.append("Reset password\n")
                .append("Code will expire within 5 minutes\n")
                .append("Code: ");
        if (StringUtils.isNotEmpty(codeResetPassword)) {
            builder.append(codeResetPassword);
        }
        return builder.toString();
    }

    private void sendMail(String content, String subject, String to, boolean isHtml) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, Constants.UTF_8);
            helper.setText(content, isHtml);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom(adminEmail);
            javaMailSender.send(mimeMessage);
            log.info("Mail sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error while sending mail to {}: {}", to, e.getMessage(), e);
        }
    }

    public void sendNewOrder(ProductOrder productOrder, String htmlContent, Set<User> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }

        String filePathCustomer = "./config/email/mailOrder.html";
        String filePathAdmin = "./config/email/mailOrderAdmin.html";

        try {
            String htmlTemplateCustomer = HTMLTemplateReader.readTemplate(filePathCustomer);
            String htmlTemplateAdmin = HTMLTemplateReader.readTemplate(filePathAdmin);
            receivers.forEach(receiver -> {
                if (receiver.getAuthorities().stream()
                        .anyMatch(authority -> Authority.Role.ROLE_SUPER_ADMIN.equals(authority.getRole())
                                || Authority.Role.ROLE_ADMIN.equals(authority.getRole()))) {
                    String adminSubject = "Bạn có một đơn hàng mới";
                    String htmlBody = htmlTemplateAdmin
                            .replace("{{MA_DON_HANG}}", String.valueOf(productOrder.getId()))
                            .replace("{{NGAY_DAT_HANG}}", Utils.formatDateTime(productOrder.getCreatedDate()))
                            .replace("{{TEN_KHACH_HANG}}", productOrder.getReceiverFullName())
                            .replace("{{EMAIL_KHACH_HANG}}", productOrder.getEmail())
                            .replace("{{SDT_KHACH_HANG}}", productOrder.getPhoneNumber())
                            .replace("{{TEN_CUA_HANG}}", domainFrontEnd)
                            .replace("{{CHI_TIET_DON_HANG_HTML}}", htmlContent)
                            .replace("{{LINK_XU_LY_DON_HANG}}", cmsUrl)
                            ;
                    new Thread(() -> sendMail(htmlBody, adminSubject, receiver.getEmail(), true)).start();
                } else {
                    String receiverSubject = "Bạn vừa đặt một đơn hàng mới tại " + domainFrontEnd;
                    String htmlBody = htmlTemplateCustomer
                            .replace("{{TEN_KHACH_HANG}}", receiver.getFullName())
                            .replace("{{TEN_CUA_HANG}}", domainFrontEnd)
                            .replace("{{MA_VAN_DON}}", String.valueOf(productOrder.getId()))
                            .replace("{{CHI_TIET_DON_HANG_HTML}}", htmlContent)
                            .replace("{{EMAIL_HO_TRO}}", adminEmail)
                            .replace("{{SDT_HO_TRO}}", adminPhone)
                            ;
                    new Thread(() -> sendMail(htmlBody, receiverSubject, receiver.getEmail(), true)).start();
                }
            });
        } catch (Exception e) {
            log.error("Failed to send mail sendNewOrder: {}", e.getMessage(), e);
        }
    }

    public void sendNewContact(String htmlContent, Set<User> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }
        receivers.forEach(receiver -> {
            if (receiver.getAuthorities().stream()
                    .anyMatch(authority -> Authority.Role.ROLE_SUPER_ADMIN.equals(authority.getRole())
                            || Authority.Role.ROLE_ADMIN.equals(authority.getRole()))) {
                String adminSubject = "Bạn có một thông tin liên hệ mới";
                new Thread(() -> sendMail(htmlContent, adminSubject, receiver.getEmail(), true)).start();
            } else {
                String receiverSubject = "Bạn vừa để lại một thông tin liên hệ tại " + domainFrontEnd;
                new Thread(() -> sendMail(htmlContent, receiverSubject, receiver.getEmail(), true)).start();
            }
        });
    }

    public void sendUpdateOrder(String htmlContent, OrderStatus status, String receiver) {
        StringBuilder receiverSubject = new StringBuilder();
        switch (status) {
            case PENDING:
                receiverSubject.append("Đơn hàng của bạn đã được vận chuyển");
                break;
            case COMPLETED:
                receiverSubject.append("Đơn hàng của bạn đã được hoàn thành");
                break;
            case CANCELLED:
                receiverSubject.append("Đơn hàng của bạn đã bị hủy");
                break;
            default:
                return;
        }
        new Thread(() -> sendMail(htmlContent, String.valueOf(receiverSubject), receiver, true)).start();
    }

    @Async
    public void sendMailForgotPassword(String codeResetPass, String email, String fullName) {
        String filePath = "./config/email/mailTemplate.html";
        try {
            String htmlTemplate = HTMLTemplateReader.readTemplate(filePath);
            String htmlBody = htmlTemplate.replace("{{ fullName }}", fullName)
                    .replace("{{ code }}", codeResetPass);
            String subject = "Forgot password!!!";
            sendMail(htmlBody, subject, email, true);
        } catch (Exception e) {
            log.error("Failed to send mail reset password: {}", e.getMessage(), e);
        }
    }

    public void sendNewContactV2(ContactRequest request, Set<User> receivers) {
        if (receivers == null || receivers.isEmpty()) {
            return;
        }

        String filePathCustomer = "./config/email/mailWelcome.html";
        String filePathAdmin = "./config/email/mailWelcomeAdmin.html";

        try {
            String htmlTemplateCustomer = HTMLTemplateReader.readTemplate(filePathCustomer);
            String htmlTemplateAdmin = HTMLTemplateReader.readTemplate(filePathAdmin);
            receivers.forEach(receiver -> {
                if (receiver.getAuthorities().stream()
                        .anyMatch(authority -> Authority.Role.ROLE_SUPER_ADMIN.equals(authority.getRole())
                                || Authority.Role.ROLE_ADMIN.equals(authority.getRole()))) {
                    String adminSubject = "Bạn có một thông tin liên hệ mới";
                    String htmlBody = htmlTemplateAdmin
                            .replace("{{TEN_KHACH_HANG}}", request.getReceiverFullName())
                            .replace("{{EMAIL_KHACH_HANG}}", request.getEmail())
                            .replace("{{SDT_KHACH_HANG}}", request.getPhoneNumber())
                            .replace("{{TEN_CONG_TY}}", domainFrontEnd)
                            .replace("{{GHI_CHU_KHACH_HANG}}", request.getNote())
                            .replace("{{LINK_HE_THONG_QUAN_LY}}", cmsUrl)
                            ;
                    new Thread(() -> sendMail(htmlBody, adminSubject, receiver.getEmail(), true)).start();
                } else {
                    String receiverSubject = "Bạn vừa để lại một thông tin liên hệ tại " + domainFrontEnd;
                    String htmlBody = htmlTemplateCustomer
                            .replace("{{TEN_KHACH_HANG}}", receiver.getFullName())
                            .replace("{{EMAIL_KHACH_HANG}}", receiver.getEmail())
                            .replace("{{SDT_KHACH_HANG}}", receiver.getPhone())
                            .replace("{{TEN_CONG_TY}}", domainFrontEnd)
                            .replace("{{GHI_CHU_KHACH_HANG}}", request.getNote())
                            ;
                    new Thread(() -> sendMail(htmlBody, receiverSubject, receiver.getEmail(), true)).start();
                }
            });
        } catch (Exception e) {
            log.error("Failed to send mail sendNewContactV2: {}", e.getMessage(), e);
        }
    }
}