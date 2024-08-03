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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    private List<User> getUsers(){
        User user2 = new User();
        user2.setId(2L);
        user.setFirstName("Emily");

        return List.of(user, user2);
    }

    private List<UserDTO> getUserDTOs(){
        UserDTO userDTO2 = new UserDTO();
        userDTO2.setId(2L);
        userDTO2.setFirstName("Emily");

        return List.of(userDTO, userDTO2);
    }

    @AfterEach
    public void tearDown() throws Exception{
        // some impl
    }

    @Test
    public void test(){

    }

    @Test
    public void should_list_all_users(){
        // code under test is listAllUsers (userRepository, userMapper)

        //given part - preparation
        when(userRepository.findAllByIsDeletedOrderByFirstNameDesc(false)).thenReturn(getUsers());

        // expended way
        when(userMapper.convertToDto(user)).thenReturn(userDTO);
        when(userMapper.convertToDto(getUsers().get(1))).thenReturn(getUserDTOs().get(1));

        //shorter way -> same as previous two lines
        // -> follows the order, first when it runs it goes with DTO, second with getUserDTOs().get(1) as a second parameter and so on...
        //when(userMapper.convertToDto(any(User.class))).thenReturn(userDTO, getUserDTOs().get(1), third parameter...);

        List<UserDTO> expectedList = getUserDTOs();

        // when part - action
        List<UserDTO> actualList = userService.listAllUsers();

        //then part - Assertion/Verification
//        assertEquals(expectedList, actualList); // -> checking the data or the obj inside of Lists, but it cannot check the data of the obj in this lists
        // expected list obj1, obj2 - actual list obj3, obj4, without @EqualsAndHashCode it will not except as an equal

        //assertEquals(new User(), new User()); // comparing addresses of obj with ==,
        // will work if we add @EqualsAndHashCode on the top of a class whose objects we are going to test.

        //AssertJ             usingRecursiveComparison -> will except obj as equal even without @EqualsAndHashCode
        assertThat(actualList).usingRecursiveComparison().isEqualTo(expectedList);

        verify(userRepository, times(1)).findAllByIsDeletedOrderByFirstNameDesc(false);
        verify(userRepository, never()).findAllByIsDeletedOrderByFirstNameDesc(true);

    }



}
