package com.hrm.service;

import java.util.Optional;

@FunctionalInterface
public interface AiChatProvider {
    Optional<String> answer(String message, String roleName) throws Exception;
}
