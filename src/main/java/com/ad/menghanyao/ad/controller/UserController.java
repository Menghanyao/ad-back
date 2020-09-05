package com.ad.menghanyao.ad.controller;

import com.ad.menghanyao.ad.dto.*;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.enumeration.UserEnum;
import com.ad.menghanyao.ad.mapper.UserMapper;
import com.ad.menghanyao.ad.model.User;
import com.ad.menghanyao.ad.service.TimeService;
import com.ad.menghanyao.ad.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@ResponseBody
@CrossOrigin
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TimeService timeService;

    @GetMapping("/")
    public String hello(){
        ArrayList<User> user = userMapper.UserList();
        System.out.println(user);
//        User user = new User();
//        user.setUserPassword("123456");
//        userService.addUser(user, UserEnum.USER_TYPE_USER.getCode());
        return "hello world";
    }


    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Object post(@RequestBody TestDTO testDTO ) {
        TestDTO testDTO1 = new TestDTO();
        testDTO1.setUserId(testDTO.getUserId());
        System.out.println("testDTO1 = " + testDTO1.getUserId());
        return testDTO;
    }




    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public Object addUser(@RequestBody UserDTO userDTO ) {
        System.out.println("Controller：调用了addUser接口，需要插入一条用户信息");
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setUserType(UserEnum.USER_TYPE_USER.getCode());
        userService.addUser(user);
        return null;
    }

    @RequestMapping(value = "/addAder", method = RequestMethod.POST)
    public Object addAder(@RequestBody UserDTO userDTO ) {
        System.out.println("Controller：调用了addAder接口，需要插入一条商户信息");
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setUserType(UserEnum.USER_TYPE_ADER.getCode());
        userService.addUser(user);
        ResultDTO resultDTO = new ResultDTO(ResultEnum.USER_ADDED.getCode(), ResultEnum.USER_ADDED.getMessage());
        return resultDTO;
    }

    @RequestMapping(value = "/addAdmin", method = RequestMethod.POST)
    public Object addAdmin(@RequestBody UserDTO userDTO ) {
        System.out.println("Controller：addAdmin，需要插入一条admin信息");
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        user.setUserType(UserEnum.USER_TYPE_WAIT.getCode());
        userService.addUser(user);
        ResultDTO resultDTO = new ResultDTO(ResultEnum.WAIT_ADDED.getCode(), ResultEnum.WAIT_ADDED.getMessage());
        return resultDTO;
    }

    @RequestMapping(value = "/addRoot/{value}", method = RequestMethod.POST)
    public Object addRoot(@RequestBody UserDTO userDTO, @PathVariable String value) {
        System.out.println("Controller：addRoot，需要插入一条root信息");
        Long apply = Long.valueOf(value);
        Long now = System.currentTimeMillis();
        if (now > apply + 5 * 60 * 1000)
            return null;
        User root = new User();
        BeanUtils.copyProperties(userDTO, root);
        root.setUserType(UserEnum.USER_TYPE_ROOT.getCode());
        userService.addRoot(root);
        ResultDTO resultDTO = new ResultDTO(ResultEnum.ROOT_ADDED.getCode(), ResultEnum.ROOT_ADDED.getMessage());
        return resultDTO;
    }


    @RequestMapping(value = "/userList", method = RequestMethod.POST)
    public Object userList(@RequestBody ListDTO<User> requestListDTO ) {
        System.out.println("Controller：userList，需要返回用户列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<User> responseListDTO = userService.userList(requestListDTO);
        return responseListDTO;
    }

    @RequestMapping(value = "/adminList", method = RequestMethod.POST)
    public Object adminList(@RequestBody ListDTO<User> requestListDTO ) {
        System.out.println("Controller：adminList，需要返回管理员列表");
        System.out.println("listDTO = " + requestListDTO);
        ListDTO<User> responseListDTO = userService.adminList(requestListDTO);
        return responseListDTO;
    }

    @GetMapping("/userCount")
    public Long userCount(){
        System.out.println("Controller：userCount，需要返回用户总数");
        Long userCount = userMapper.userCount();
        System.out.println(userCount);
        return userCount;
    }

    @GetMapping("/userCountToday")
    public Long userCountToday(){
        System.out.println("Controller：userCountToday，需要返回今日用户总数");
        Long userCountToday = userService.userCountToday();
        System.out.println(userCountToday);
        return userCountToday;
    }


    @RequestMapping(value = "/adminPass", method = RequestMethod.POST)
    public Object adminPass(@RequestBody OperationDTO operationDTO ) {
        System.out.println("Controller：adminPass，需要通过申请");
        System.out.println("operationDTO = " + operationDTO);
        ResultDTO resultDTO = userService.adminPass(operationDTO.getTarget(), operationDTO.getOperator());
        return resultDTO;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody UserDTO userDTO ) {
        System.out.println("Controller：login，需要login");
        System.out.println("userDTO = " + userDTO);
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        Object resp = userService.login(user);
        return resp;
    }

    @GetMapping("/getSevenDayCount")
    public Object getSevenDayCount(){
        System.out.println("Controller：getSevenDayCount，需要返回七日数据");
        return timeService.getSevenDayCount();
    }

    @RequestMapping(value = "/updateUser", method = RequestMethod.POST)
    public Object updateUser(@RequestBody UserDTO userDTO ) {
        System.out.println("Controller：updateUser，需要返updateUser");
        System.out.println("userDTO = " + userDTO);
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        Object resp = userService.updateUser(user);
        return resp;
    }
}
