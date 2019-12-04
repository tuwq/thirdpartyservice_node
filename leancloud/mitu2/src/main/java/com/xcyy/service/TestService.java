package com.xcyy.service;

import com.xcyy.common.JsonResult;
import com.xcyy.mapper.UsersMapper;
import com.xcyy.model.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class TestService extends BaseService {

    @Autowired
    private UsersMapper usersMapper;

    public JsonResult<List<Users>> allUser() {
        List<Users> all = usersMapper.findAll();
        return this.success(all);
    }
}
