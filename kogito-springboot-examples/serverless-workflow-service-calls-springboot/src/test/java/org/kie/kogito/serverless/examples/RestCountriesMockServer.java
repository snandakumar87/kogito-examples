/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.serverless.examples;

import java.io.IOException;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class RestCountriesMockServer implements AfterAllCallback,
        BeforeAllCallback {

    private WireMockServer wireMockServer;

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8282));
        wireMockServer.start();

        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final JsonNode greecePayload = objectMapper.readTree(this.getClass().getResourceAsStream("/country_mock.json"));
            wireMockServer.stubFor(get(urlEqualTo("/rest/v2/name/Greece"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withJsonBody(greecePayload)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
