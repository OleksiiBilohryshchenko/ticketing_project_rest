package com.example.review;

import com.example.dto.ProjectDTO;
import com.example.dto.RoleDTO;
import com.example.dto.TaskDTO;
import com.example.dto.UserDTO;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.TicketingProjectException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.service.KeycloakService;
import com.example.service.ProjectService;
import com.example.service.TaskService;
import com.example.service.impl.UserServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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

    @Mock
    private PasswordEncoder passwordEncoder;

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

    private User getUserWithRole(String role){

        User user3 = new User();

        user3.setUserName("user3");
        user3.setPassWord("Abc1");
        user3.setEnabled(true);
        user3.setIsDeleted(false);
        user3.setRole(new Role(role));

        return user3;
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

    @Test
    public void should_throw_nosuchelementexception_when_user_not_found(){
        //given
        // checking if (user == null) throw new NoSuchElementException("User not found.");
//        when(userRepository.findByUserNameAndIsDeleted(anyString(),anyBoolean())).thenReturn(null); -> not really correct to do in our case
//        when(userMapper.convertToDto(any(User.class))).thenReturn(userDTO); -> also not really correct in our case

        // any() we use only if it related with Mockito library

        // when and then step will be in one step
        Throwable actualException = assertThrows(NoSuchElementException.class, () -> userService.findByUserName("Someusername"));

//        Throwable actualException = assertThrowsExactly(RuntimeException.class, () -> userService.findByUserName("Someusername")); // fail, should be exactly RunTimeException

        assertEquals("User not found.", actualException.getMessage());

        //AssertJ    for catching an exception only
//        Throwable actualException = catchThrowable(() -> userService.findByUserName("Someusername"));

    }

    // 	User Story - 1: As a user of the application, I want my password to be encoded
    //	so that my account remains secure.
    //
    //	Acceptance Criteria:
    //	1 - When a user creates a new account, their password should be encoded using
    //	a secure algorithm such as bcrypt or PBKDF2.
    //
    //	2 - Passwords should not be stored in plain text in the database or any other storage.
    //
    //	3 - Passwords encoding should be implemented consistently throughout the application,
    //	including any password reset or change functionality.

    @Test
    void should_encode_user_password_on_save_operation() {

        // given
        when(userMapper.convertToEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.convertToDto(any(User.class))).thenReturn(userDTO);
        when(passwordEncoder.encode(anyString())).thenReturn("some-password");

        String expectedPassword = "some-password";

        // when
        UserDTO savedUser = userService.save(userDTO);

        // then

        assertEquals(expectedPassword, savedUser.getPassWord());

        // verify that passwordEncoder is executed
        verify(passwordEncoder, times(1)).encode(anyString());

    }

    @Test
    void should_encode_user_password_on_update_operation() {

        // given
        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(user);
        when(userMapper.convertToEntity(any(UserDTO.class))).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.convertToDto(any(User.class))).thenReturn(userDTO);
        when(passwordEncoder.encode(anyString())).thenReturn("some-password");

        String expectedPassword = "some-password";

        // when
        UserDTO updatedUser = userService.update(userDTO);

        // then
        assertEquals(expectedPassword, updatedUser.getPassWord());

        // verify that passwordEncoder is executed
        verify(passwordEncoder, times(1)).encode(anyString());

    }

    // 	User Story 2: As an admin, I shouldn't be able to delete a manager user,
    // 	if that manager has projects linked to them to prevent data loss.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent a manager user from being deleted
    //	if they have projects linked to them.
    //	2 - An error message should be displayed to the user if they attempt
    //	to delete a manager user with linked projects.
    //
    //	User Story 3: As an admin, I shouldn't be able to delete an employee user,
    //	if that employee has tasks linked to them to prevent data loss.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent an employee user from being deleted
    //	if they have tasks linked to them.
    //	2 - An error message should be displayed to the user if they attempt
    //	to delete an employee user with linked tasks.

    @Test
    void should_delete_manager() throws TicketingProjectException {

        // given - Preparation
        User managerUser = getUserWithRole("Manager");

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(managerUser);
        when(userRepository.save(any())).thenReturn(managerUser);
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());

        // when - Action
        userService.delete(managerUser.getUserName());

        // then - Assertion/Verification
        assertTrue(managerUser.getIsDeleted());
        assertNotEquals("user3", managerUser.getUserName());

    }

    @Test
    void should_delete_employee() throws TicketingProjectException {

        // given - Preparation
        User employeeUser = getUserWithRole("Employee");

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(employeeUser);
        when(userRepository.save(any())).thenReturn(employeeUser);
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());

        // when - Action
        userService.delete(employeeUser.getUserName());

        // then - Assertion/Verification
        assertTrue(employeeUser.getIsDeleted());
        assertNotEquals("user3", employeeUser.getUserName());

    }

    // not the best practise to use
    @ParameterizedTest
    @ValueSource(strings = {"Manager", "Employee"})
    void should_delete_user(String role) throws TicketingProjectException {

        // given - Preparation
        User testUser = getUserWithRole(role);

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(testUser);
        when(userRepository.save(any())).thenReturn(testUser);

//        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
//        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());

//        if (testUser.getRole().getDescription().equals("Manager")) {
//            when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
//        } else if (testUser.getRole().getDescription().equals("Employee")) {
//            when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());
//        }

        lenient().when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(new ArrayList<>());
        lenient().when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(new ArrayList<>());

        // when - Action
        userService.delete(testUser.getUserName());

        // then - Assertion/Verification
        assertTrue(testUser.getIsDeleted());
        assertNotEquals("user3", testUser.getUserName());

    }

    @Test
    void should_throw_exception_when_deleting_manager_with_project() {

        User managerUser = getUserWithRole("Manager");

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(managerUser);
        when(projectService.listAllNonCompletedByAssignedManager(any())).thenReturn(List.of(new ProjectDTO(), new ProjectDTO()));

        Throwable actualException = assertThrows(TicketingProjectException.class, () -> userService.delete(managerUser.getUserName()));

        assertEquals("User can not be deleted", actualException.getMessage());

    }

    @Test
    void should_throw_exception_when_deleting_employee_with_task() {

        User employeeUser = getUserWithRole("Employee");

        when(userRepository.findByUserNameAndIsDeleted(anyString(), anyBoolean())).thenReturn(employeeUser);
        when(taskService.listAllNonCompletedByAssignedEmployee(any())).thenReturn(List.of(new TaskDTO(), new TaskDTO()));

        Throwable actualException = assertThrows(TicketingProjectException.class, () -> userService.delete(employeeUser.getUserName()));

        assertEquals("User can not be deleted", actualException.getMessage());

    }

    //	User Story 4: As an admin, I shouldn't be able to delete an admin user,
    //	if that admin is the last admin in the system.
    //
    //	Acceptance Criteria:
    //
    //	1 - The system should prevent an admin user from being deleted
    //	if it is the last admin.
    //	2 - An error message should be displayed to the user if there is an
    //	attempt to delete the last admin user.


}
