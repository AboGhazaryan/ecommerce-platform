package com.ecommerce.service;

import com.ecommerce.dto.SellerResponse;
import com.ecommerce.dto.UserRequest;
import com.ecommerce.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse creatUser(UserRequest dto);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);

    SellerResponse getSellerInfoById(Integer id);

    UserResponse changeUserById(Integer id, UserRequest dto);

    void deleteUserById(Integer id);

    UserResponse blockUser(Integer id);

    UserResponse unblockUser(Integer id);
}
