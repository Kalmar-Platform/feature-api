package com.visma.kalmar.api.security;

import com.visma.kalmar.api.vismaconnect.VismaConnectUserGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JwtTokenInspector {
    private static final String SUBJECT = "sub";

    private final VismaConnectUserGateway vismaConnectUserGateway;

    @Autowired
    public JwtTokenInspector(VismaConnectUserGateway vismaConnectUserGateway) {
        this.vismaConnectUserGateway = vismaConnectUserGateway;
    }

    public String getConnectUserId(JwtAuthenticationToken jwtAuthenticationToken) {
        return parseSubjectClaimAsUuid(jwtAuthenticationToken);
    }

    public String getConnectUserEmail(JwtAuthenticationToken jwtAuthenticationToken) {
        String connectUserId = parseSubjectClaimAsUuid(jwtAuthenticationToken);
        if (connectUserId == null) {
            return null;
        } else {
            return vismaConnectUserGateway.findUserById(connectUserId).email();
        }
    }

    private String parseSubjectClaimAsUuid(JwtAuthenticationToken jwtAuthenticationToken) {
        String connectUserId = (String) jwtAuthenticationToken.getTokenAttributes().get(SUBJECT);
        if (connectUserId != null) {
            try {
                UUID.fromString(connectUserId);
            } catch (IllegalArgumentException e) {
                connectUserId = null;
            }
        }
        return connectUserId;
    }
}
