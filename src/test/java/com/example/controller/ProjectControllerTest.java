package com.example.controller;

import com.example.dto.ProjectDTO;
import com.example.dto.RoleDTO;
import com.example.dto.UserDTO;
import com.example.enums.Gender;
import com.example.enums.Status;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    // static because usage before all static method,
    // and if we want to use fields in a static method they must be static
    static UserDTO manager;
    static ProjectDTO project;

    @BeforeAll
    static void setUp(){

        manager = new UserDTO(2L,
                "",
                "",
                "alex",
                "abc1",
                "",
                true,
                "",
                new RoleDTO(2L,"Manager"),
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

}