package com.enterprise.backend.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * Project: hi-sweetie
 * <p>
 * Created by: khanh.hoangviet
 * Date: 2024/11/05
 * <p>
 * Description:
 */
@Data
public class ContactRequest {
    private String receiverFullName;
    @Pattern(regexp = "^[\\w.-]+@[\\w-]+(\\.[\\w-]{2,4}){1,4}$", message = "invalid email!")
    @NotEmpty(message = "email is required!")
    private String email;

    @Pattern(regexp = "^[0-9+\\-]{9,15}$", message = "invalid phone number!")
    @NotEmpty(message = "phoneNumber is required!")
    private String phoneNumber;

//    @NotEmpty(message = "note is required!")
    private String note;
}
