package com.example.service;


import com.example.dto.UserDTO;
import com.example.exception.TicketingProjectException;

import java.util.List;

public interface UserService  {

    UserDTO findByUserName(String username);
    List<UserDTO> listAllUsers();
    UserDTO save(UserDTO user);
//    void deleteByUserName(String username);
    UserDTO update(UserDTO user);
    void delete(String username) throws TicketingProjectException;
    List<UserDTO> listAllByRole(String role);

}
