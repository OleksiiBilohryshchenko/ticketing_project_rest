package com.example.controller;

import com.example.dto.ProjectDTO;
import com.example.dto.RoleDTO;
import com.example.dto.UserDTO;
import com.example.enums.Gender;
import com.example.enums.Status;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    static String token;

    // static because usage before all static method,
    // and if we want to use fields in a static method they must be static
    static UserDTO manager;
    static ProjectDTO project;

    @BeforeAll
    static void setUp(){

        token = "Bearer " + "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmMTAyMzg4UnJKTnBoSkdWY1BSQlg0dUtiRnBNdUltTTRUS1NjOFpvNkdrIn0.eyJleHAiOjE3MjE1ODc2MDgsImlhdCI6MTcyMTU2OTYwOCwianRpIjoiZWU3Nzg5NjQtZTkxNC00ZGFhLWE3YTctZTIyYWE1YTI4NmE2IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL2F1dGgvcmVhbG1zL2V4YW1wbGUtZGV2IiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjZhYWQxMDZiLTQ0NWYtNDRkZC1iN2M5LTY3ZTcxM2MwNjY3MiIsInR5cCI6IkJlYXJlciIsImF6cCI6InRpY2tldGluZy1hcHAiLCJzZXNzaW9uX3N0YXRlIjoiMjNjMjY4MmEtZDAxMC00NjdhLThjNzYtNTUxYmViMzhmYjk1IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vbG9jYWxob3N0OjgwODEiXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1leGFtcGxlLWRldiIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsidGlja2V0aW5nLWFwcCI6eyJyb2xlcyI6WyJNYW5hZ2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6Im9wZW5pZCBwcm9maWxlIGVtYWlsIiwic2lkIjoiMjNjMjY4MmEtZDAxMC00NjdhLThjNzYtNTUxYmViMzhmYjk1IiwiZW1haWxfdmVyaWZpZWQiOnRydWUsInByZWZlcnJlZF91c2VybmFtZSI6ImFsZXgifQ.wfoeY3xLmqg7dq0Y6KCbrMNnFQlaHRCN4Zr36UVxItvmfNhf3ElxzZfw-tzThA9myl7hoJqv7A8fREmWE1xj1W4iFzLcNYDi2RXdACIedTk8A4jM_WW4sInwqemSFYYCvMz7ITEEer4aoT6xsU4M6c3_QLowBKWTjulBCnGGSiy766AjOZe31Z8rd1-UD44_XgNUXfT2o6BHBtmXb-tDLkGOAqRwV-0hlSCFA4hvvk--0SQExGaWJXhgUL1Dt0PYkI2Gay3a6shc_zmDtWeE4IaHwUIp326PtvDBtuzc4NX33AlkbOc1a-1Xdcvgx5YlGFYiXld1RIuftRIayejeYg";  // hardcoded way

        manager = new UserDTO(2L,
                "",
                "",
                "alex",
                "abc1",
                "",
                true,
                "",
                new RoleDTO(2L, "Manager"),
                Gender.MALE);

        project = new ProjectDTO(
                "API Project",
                "PR001",
                manager,
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Some details",
                Status.OPEN
        );

    }

    // testing flow from getProjects endpoint

    @Test
    void givenNoToken_getProjects() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project"))
                .andExpect(status().is4xxClientError()); // check 4xx status

    }

    @Test
    void givenToken_getProjects() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get("/api/v1/project")
                .header("Authorization", token)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].projectCode").exists()) // [0] first element inside data (from list)  // https://github.com/json-path/JsonPath
                .andExpect(jsonPath("$.data[0].assignedManager.userName").exists()) // checks if we have this user or not
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isNotEmpty()) // if userName is empty or not
                .andExpect(jsonPath("$.data[0].assignedManager.userName").isString()) // if it is String format
                .andExpect(jsonPath("$.data[0].assignedManager.userName").value("alex")); // checks if it is alex or not

    }

    @Test
    void givenToken_createProject() throws Exception {

        mvc.perform(MockMvcRequestBuilders.post("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Project is successfully created"));

    }

    @Test
    void givenToken_updateProject() throws Exception {

        project.setProjectName("API Project - 2");

        mvc.perform(MockMvcRequestBuilders.put("/api/v1/project")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(toJsonString(project)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Project is successfully updated"));
    }

    // converting Json to String
    private String toJsonString(final Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(obj);
    }


}