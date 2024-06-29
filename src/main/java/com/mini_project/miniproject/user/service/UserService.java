package com.mini_project.miniproject.user.service;


import com.mini_project.miniproject.user.dto.RegisterRequestDto;
import com.mini_project.miniproject.user.entity.Users;

import java.util.List;

public interface UserService {
    Users register(RegisterRequestDto registerRequestDto);

}
