package com.example.service.impl;

import com.example.annotation.DefaultExceptionMessage;
import com.example.dto.ProjectDTO;
import com.example.dto.TaskDTO;
import com.example.dto.UserDTO;
import com.example.entity.User;
import com.example.exception.TicketingProjectException;
import com.example.mapper.UserMapper;
import com.example.repository.UserRepository;
import com.example.service.KeycloakService;
import com.example.service.ProjectService;
import com.example.service.TaskService;
import com.example.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final KeycloakService keycloakService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           @Lazy ProjectService projectService,
                           @Lazy TaskService taskService,
                           KeycloakService keycloakService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.projectService = projectService;
        this.taskService = taskService;
        this.keycloakService = keycloakService;
        this.passwordEncoder = passwordEncoder;
    }

    // to be able to test we need Mock userRepository and userMapper
    @Override
    public UserDTO findByUserName(String username) {
        User user = userRepository.findByUserNameAndIsDeleted(username, false);
        if (user == null) throw new NoSuchElementException("User not found.");
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> userList = userRepository.findAllByIsDeletedOrderByFirstNameDesc(false);
        return userList.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public UserDTO save(UserDTO user) {

        user.setEnabled(true);
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));

        User obj = userMapper.convertToEntity(user);

        User savedUser = userRepository.save(obj);

        keycloakService.userCreate(user);

        return userMapper.convertToDto(savedUser);

    }

//    @Override
//    public void deleteByUserName(String username) {
//
//        userRepository.deleteByUserName(username);
//    }

    @Override
    public UserDTO update(UserDTO user) {
        user.setPassWord(passwordEncoder.encode(user.getPassWord()));
        //Find current user
        User user1 = userRepository.findByUserNameAndIsDeleted(user.getUserName(), false);  //has id
        //Map update user dto to entity object
        User convertedUser = userMapper.convertToEntity(user);   // has id?
        //set id to the converted object
        convertedUser.setId(user1.getId());
        //save the updated user in the db
        User updatedUser = userRepository.save(convertedUser);

        return userMapper.convertToDto(updatedUser);

    }

    @Override
    @DefaultExceptionMessage(defaultMessage = "Delete user is failed")
    public void delete(String username) throws TicketingProjectException {

        User user = userRepository.findByUserNameAndIsDeleted(username, false);

        if (checkIfUserCanBeDeleted(user)) {
            user.setIsDeleted(true);
            user.setUserName(user.getUserName() + "-" + user.getId());  // harold@manager.com-2
            userRepository.save(user);
        }else{
            throw new TicketingProjectException("User can not be deleted");
        }

    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findByRoleDescriptionIgnoreCaseAndIsDeleted(role, false);
        return users.stream().map(userMapper::convertToDto).collect(Collectors.toList());
    }

    private boolean checkIfUserCanBeDeleted(User user) {

        switch (user.getRole().getDescription()) {
            case "Manager":
                List<ProjectDTO> projectDTOList = projectService.listAllNonCompletedByAssignedManager(userMapper.convertToDto(user));
                return projectDTOList.size() == 0;
            case "Employee":
                List<TaskDTO> taskDTOList = taskService.listAllNonCompletedByAssignedEmployee(userMapper.convertToDto(user));
                return taskDTOList.size() == 0;
            default:
                return true;
        }

    }

}
