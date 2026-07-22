package com.hrm.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Test: ChatbotRoleDataRepository")
public class ChatbotRoleDataRepositoryTest {
    @Test
    void testInterface() {
        assertTrue(ChatbotRoleDataRepository.class.isInterface() || java.lang.reflect.Modifier.isAbstract(ChatbotRoleDataRepository.class.getModifiers()));
    }
}
