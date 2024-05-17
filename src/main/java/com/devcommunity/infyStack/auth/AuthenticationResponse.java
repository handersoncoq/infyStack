package com.devcommunity.infyStack.auth;

import com.devcommunity.infyStack.enums.UserRole;
import com.devcommunity.infyStack.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token;
    private UserRole role;
    private UserStatus status;
    private Date tokenExpiryDate;
}
