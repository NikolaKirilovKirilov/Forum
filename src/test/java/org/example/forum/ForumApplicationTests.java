package org.example.forum;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ForumApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void fullForumFlow() throws Exception {
        String userToken = registerAndLogin("john", "john@example.com", "password123");
        String adminToken = login("admin", "admin123");

        mockMvc.perform(post("/api/topics")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Spring Boot Discussion"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Spring Boot Discussion"))
                .andExpect(jsonPath("$.author.username").value("john"))
                .andExpect(jsonPath("$.viewCount").value(0));

        MvcResult topicsResult = mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode topics = objectMapper().readTree(topicsResult.getResponse().getContentAsString());
        long topicId = topics.get(0).get("id").asLong();

        for (int i = 1; i <= 11; i++) {
            mockMvc.perform(post("/api/topics/" + topicId + "/replies")
                            .header("Authorization", "Bearer " + userToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {"content":"Reply number %d"}
                                    """.formatted(i)))
                    .andExpect(status().isCreated());
        }

        mockMvc.perform(get("/api/topics/" + topicId + "?page=0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topic.viewCount").value(1))
                .andExpect(jsonPath("$.replies.length()").value(10))
                .andExpect(jsonPath("$.totalReplies").value(11))
                .andExpect(jsonPath("$.totalPages").value(2));

        mockMvc.perform(get("/api/topics/" + topicId + "?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.replies.length()").value(1))
                .andExpect(jsonPath("$.topic.viewCount").value(2));

        mockMvc.perform(put("/api/users/2/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"role":"MODERATOR"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("MODERATOR"));
    }

    private String registerAndLogin(String username, String email, String password) throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"%s",
                                  "email":"%s",
                                  "password":"%s"
                                }
                                """.formatted(username, email, password)))
                .andExpect(status().isCreated());
        return login(username, password);
    }

    private String login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"%s",
                                  "password":"%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode body = objectMapper().readTree(result.getResponse().getContentAsString());
        assertThat(body.get("token").asText()).isNotBlank();
        return body.get("token").asText();
    }

    private com.fasterxml.jackson.databind.ObjectMapper objectMapper() {
        return new com.fasterxml.jackson.databind.ObjectMapper();
    }
}
