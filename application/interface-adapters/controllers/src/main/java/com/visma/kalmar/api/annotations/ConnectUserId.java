package com.visma.kalmar.api.annotations;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@AuthenticationPrincipal(expression = "@jwtTokenInspector.getConnectUserId(#this)")
public @interface ConnectUserId {
}
