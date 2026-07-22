package com.hrm.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test: GeminiGenerateContentProvider - 100% Branch Coverage")
public class GeminiGenerateContentProviderTest {

    @Test
    @SuppressWarnings("unchecked")
    void testAnswerBranches() throws Exception {
        AiProviderConfig.GeminiSettings enabledSettings = new AiProviderConfig.GeminiSettings("key123", "model", "http://fake-endpoint/");
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse<String> mockResponse = mock(HttpResponse.class);

        GeminiGenerateContentProvider provider = new GeminiGenerateContentProvider(enabledSettings, mockClient);

        // Case 1: Status code 400/500 -> empty
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockResponse);
        assertFalse(provider.answer("Hello", "Admin").isPresent());

        // Case 2: Status code 200 with valid JSON response
        when(mockResponse.statusCode()).thenReturn(200);
        String jsonSuccess = "{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Xin chào!\"}]}}]}";
        when(mockResponse.body()).thenReturn(jsonSuccess);
        Optional<String> answer = provider.answer("Hello", "Admin");
        assertTrue(answer.isPresent());
        assertEquals("Xin chào!", answer.get());

        // Case 3: Empty candidates
        when(mockResponse.body()).thenReturn("{\"candidates\":[]}");
        assertFalse(provider.answer("Hello", null).isPresent());

        // Case 4: Missing parts
        when(mockResponse.body()).thenReturn("{\"candidates\":[{\"content\":{}}]}");
        assertFalse(provider.answer("Hello", "  ").isPresent());

        // Case 5: Null / empty text in part
        when(mockResponse.body()).thenReturn("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"\"}]}}]}");
        assertFalse(provider.answer("Hello", "Guest").isPresent());
    }

    @Test
    void testDisabledSetting() throws Exception {
        AiProviderConfig.GeminiSettings disabledSettings = new AiProviderConfig.GeminiSettings("", "model", "endpoint/");
        GeminiGenerateContentProvider provider = new GeminiGenerateContentProvider(disabledSettings, HttpClient.newHttpClient());
        assertFalse(provider.answer("Hello", "User").isPresent());
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(new GeminiGenerateContentProvider());
    }
}
