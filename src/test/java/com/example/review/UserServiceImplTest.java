package com.example.review;

import com.example.dto.RoleDTO;
import com.example.dto.UserDTO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.service.KeycloakService;
import com.example.service.ProjectService;
import com.example.service.TaskService;
import com.example.service.impl.UserServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
// three components of JUnit5 -> Platform, Jupiter, Vintage (adopt previous JUnit versions @)
@ExtendWith(MockitoExtension.class) //Junit is a framework -> we can use it in a combination with other libraries.
// To be able to use Mockito library, we need extend it with @ExtendWith.
public class UserServiceImplTest {

    // first steps -> add @Test on the top of the testing method

    // Mocking -> simulate the behavior of real objects, systems, or components in a controlled way. Proxy obj, with no impl.
    // Stub -> provide impl for the methods which you want to use (when, thenReturn ...).
    // @InjectMocks -> inject dependencies for mocking
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private TaskService taskService;

    @Mock
    private KeycloakService keycloakService;

    @InjectMocks // inject all above mocks
    private UserServiceImpl userService; // since there is no bean without running an app we need use UserServiceImpl directly

    User user;
    UserDTO userDTO;

//    @BeforeAll // runs before all only once
//    public static void setUpBeforeClass() throws Exception{
//        // some impl
//    }
//
//    @AfterAll
//    public static void tearDownAfterClass() throws Exception{
//        // some impl
//    }

    @BeforeEach // runs as many times as many tests you are running
    public void setUp() throws Exception{

       user = new User();
       user.setId(1L);
       user.setFirstName("John");
       user.setLastName("Doe");
       user.setUserName("user");
       user.setPassWord("Abc1");
       user.setEnabled(true);
       user.setRole(new Role("Manager"));

        userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUserName("user");
        userDTO.setPassWord("Abc1");
        userDTO.setEnabled(true);

        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setDescription("Manager");

        userDTO.setRole(roleDTO);
    }

    @AfterEach
    public void tearDown() throws Exception{
        // some impl
    }

    @Test
    public void test(){

    }



}
