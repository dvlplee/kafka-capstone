package com.chacha.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginEvent {
    private String eventType;
    private String timeStamp;
    private String email;
    private String ipAddress;
    private String location;
    private String userAgent;
    private String os;
    private String browser;
    private String reason;
}
