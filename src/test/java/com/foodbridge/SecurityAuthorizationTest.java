package com.foodbridge;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPublicPagesAccessibleToAnonymousUsers() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/food"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/materials"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/cash"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/auth/register"))
                .andExpect(status().isOk());
    }

    @Test
    void testAdminEndpointsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "donor@example.com", authorities = {"ROLE_INDIVIDUAL_DONOR"})
    void testNonAdminUsersForbiddenFromAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@foodbridge.org", authorities = {"ROLE_ADMIN"})
    void testAdminUsersCanAccessAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk());
    }
}
