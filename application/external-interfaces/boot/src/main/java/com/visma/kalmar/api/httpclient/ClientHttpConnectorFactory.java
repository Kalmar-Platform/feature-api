package com.visma.kalmar.api.httpclient;

import io.netty.handler.logging.LogLevel;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class ClientHttpConnectorFactory {
    private ClientHttpConnectorFactory() {}

    public static ClientHttpConnector createDebuggableClientHttpConnector() {
        return new ReactorClientHttpConnector(
                HttpClient.create()
                        .wiretap(
                                "reactor.netty.http.client.HttpClient",
                                LogLevel.DEBUG,
                                AdvancedByteBufFormat.TEXTUAL));
    }
}
