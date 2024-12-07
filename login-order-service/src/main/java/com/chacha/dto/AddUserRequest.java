package com.chacha.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest { // 사용자 정보 객체
    private String email;
    private String password;
}
