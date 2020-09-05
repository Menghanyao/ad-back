package com.ad.menghanyao.ad.service;

import com.ad.menghanyao.ad.dto.ListDTO;
import com.ad.menghanyao.ad.dto.ResultDTO;
import com.ad.menghanyao.ad.enumeration.NoticeEnum;
import com.ad.menghanyao.ad.enumeration.ResultEnum;
import com.ad.menghanyao.ad.enumeration.UserEnum;
import com.ad.menghanyao.ad.mapper.UserMapper;
import com.ad.menghanyao.ad.model.Notice;
import com.ad.menghanyao.ad.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private TimeService timeService;

    @Autowired
    private PageService pageService;

    private static Boolean canSetRoot = true;
    private static Long rootGMtCreate = 0L;

    public ResultDTO addUser(User user) {
        System.out.println("Service：调用了addUser接口，需要插入一条用户信息");
        System.out.println("user = " + user);
        if (user.getUserPassword() == null) {
            user.setUserPassword("123456");
        }
        user.setUserToken(UUID.randomUUID().toString());
        user.setUserStatus(UserEnum.USER_STATUS_NORMAL.getCode());
        user.setUserCash(0L);
        user.setGmtCreate(System.currentTimeMillis());
        user.setGmtModified(user.getGmtCreate());
        try {
            userMapper.addUser(user);
            return new ResultDTO(ResultEnum.USER_ADDED.getCode(), ResultEnum.USER_ADDED.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultDTO(ResultEnum.USER_ADD_FAILED.getCode(), ResultEnum.USER_ADD_FAILED.getMessage());
        }
    }

    public ResultDTO addRoot(User root) {
        if (!canSetRoot) {
            return new ResultDTO(ResultEnum.ROOT_ALREADY_EXIST.getCode(), ResultEnum.ROOT_ALREADY_EXIST.getMessage());
        } else  {
            String name = "src/data/"+ rootGMtCreate + ".txt";
            File file = new File(name);
            if (!file.exists() && rootGMtCreate == 0L) {
                addUser(root);
                canSetRoot = false;
                rootGMtCreate = System.currentTimeMillis();
                name = "src/data/"+ rootGMtCreate + ".txt";
                file = new File(name);
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new ResultDTO(ResultEnum.ROOT_ADDED.getCode(), ResultEnum.ROOT_ADDED.getMessage());
            }
            else return new ResultDTO(ResultEnum.ROOT_ADD_FAILED.getCode(), ResultEnum.ROOT_ADD_FAILED.getMessage());
        }
    }

    public ListDTO<User> userList(ListDTO<User> requestListDTO) {
        System.out.println("Service：调用了userlist接口，需要返回用户列表");
        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Integer userType = getUserType(requestListDTO.getUserId());

        Long total = serviceGetUserCount(userType);
        List<User> userList = serviceGetUserList(requestListDTO.getUserId(), userType, offset, limit);

        ListDTO<User> userListDTO = new ListDTO<User>();
        userListDTO.setCurrent(requestListDTO.getCurrent());
        userListDTO.setSize(limit);
        userListDTO.setTotal(total);
        userListDTO.setData(userList);
        return userListDTO;
    }

    private List<User> serviceGetUserList(Long userId, Integer userType, int offset, Integer limit) {
        switch (userType){
            case 0:
            case 3:
                List<User> userList = userMapper.userList(offset, limit);
                return userList;
            case 1:
            case 2:
            default:
                break;
        }
        return null;
    }

    private Long serviceGetUserCount(Integer userType) {
        switch (userType){
            case 0:
            case 3:
                return userMapper.userCount();
            case 1:
            case 2:
            default:
                break;
        }
        return null;
    }

    public Integer getUserType(Long userId) {
        return userMapper.getUserType(userId);
    }

    public Long userCountToday() {
        System.out.println("Service：userCountToday，需要返回今日用户总数");
        Long todayZeroTimestamp = timeService.getTodayTimestamp();
        Long userCountToday = userMapper.userCountToday(todayZeroTimestamp);
        return userCountToday;
    }

    public ListDTO<User> adminList(ListDTO<User> requestListDTO) {
        System.out.println("Service：调用了adminList接口，需要返回管理员列表");
        Integer offset = pageService.getOffset(requestListDTO.getCurrent(), requestListDTO.getSize());
        Integer limit =  pageService.getSize(requestListDTO.getSize());

        Integer userType = getUserType(requestListDTO.getUserId());
        if (userType.equals(UserEnum.USER_TYPE_ROOT.getCode()) || userType.equals(UserEnum.USER_TYPE_ADMIN.getCode())) {
            Long total = userMapper.userCountRootAdminWait();
            List<User> userList = userMapper.userListRootAdminWait(offset, limit);
            ListDTO<User> userListDTO = new ListDTO<User>();
            userListDTO.setCurrent(offset + 1);
            userListDTO.setSize(limit);
            userListDTO.setTotal(total);
            userListDTO.setData(userList);
            return userListDTO;
        }
        return null;
    }

    public Boolean haveAuthority(Long userId, Integer targetType) {
        Integer userType = userMapper.getUserType(userId);
        if (targetType.equals(UserEnum.USER_TYPE_ADMIN.getCode())) {
            return userType.equals(UserEnum.USER_TYPE_ADMIN.getCode()) || userType.equals(UserEnum.USER_TYPE_ROOT.getCode());
        } else if (targetType.equals(UserEnum.USER_TYPE_ROOT.getCode())) {
            return userType.equals(UserEnum.USER_TYPE_ROOT.getCode());
        } else
            return false;
    }

    public ResultDTO adminPass(Long target, Long operator) {
        System.out.println("Service：adminPass，需要通过管理员申请");
        if (haveAuthority(operator, UserEnum.USER_TYPE_ROOT.getCode())) {
            try {
                userMapper.adminPass(target, UserEnum.USER_TYPE_ADMIN.getCode(), System.currentTimeMillis());
                Notice notice = new Notice();
                notice.setFromId(operator);
                notice.setToId(target);
                notice.setNoticeReason(NoticeEnum.NOTICE_STATUS_AUTHORIZED.getMessage());
                notice.setNoticeStatus(NoticeEnum.NOTICE_STATUS_AUTHORIZED.getCode());
                notice.setNoticeType(NoticeEnum.NOTICE_TYPE_SYSTEM.getCode());
                ResultDTO resultDTO = noticeService.addNotice(notice);
                if (resultDTO.getCode().equals(ResultEnum.NOTICE_ADDED.getCode())) {
                    return new ResultDTO(UserEnum.ADMIN_PASSED.getCode(), UserEnum.ADMIN_PASSED.getMessage());
                }
            } catch (Exception e) {
                return new ResultDTO(UserEnum.ADMIN_PASS_FAILED.getCode(), UserEnum.ADMIN_PASS_FAILED.getMessage());
            }

        }
        return new ResultDTO(ResultEnum.NO_AUTHORITY.getCode(), ResultEnum.NO_AUTHORITY.getMessage());
    }

    public Object login(User oldUser) {
        if (oldUser.getUserId() == null) {
            //注册
            if (userMapper.getUserCountByUserPhone(oldUser.getUserPhone()) == 0L) {
                ResultDTO resultDTO = addUser(oldUser);
                if (resultDTO.getCode() == ResultEnum.USER_ADDED.getCode())
                    login(oldUser);// 注册完登录
            }

            //账号密码登录
            User dbUser = userMapper.getUserByUserPhone(oldUser.getUserPhone());
            if (oldUser.getUserPassword().equals(dbUser.getUserPassword())) {
                return updateUserToken(dbUser);
            }
            return new ResultDTO(ResultEnum.LOGININ_FAILED.getCode(), ResultEnum.LOGININ_FAILED.getMessage());
        } else {
            //自动登录，检验token
            User dbUser = userMapper.getUserByUserId(oldUser.getUserId());
            if (oldUser.getUserToken().equals(dbUser.getUserToken())) {//校验token
                return updateUserToken(dbUser);
            }
            return new ResultDTO(ResultEnum.NO_LONGER_LOGIN.getCode(), ResultEnum.NO_LONGER_LOGIN.getMessage());
        }
    }

    private User updateUserToken(User dbUser) {
        dbUser.setUserToken(UUID.randomUUID().toString());
        dbUser.setGmtModified(System.currentTimeMillis());
        userMapper.updateUser(dbUser.getUserId(), dbUser.getUserToken(), dbUser.getGmtModified());
        return dbUser;
    }


    public void updateUserCash(Long userId) {
        User user = userMapper.getUserByUserId(userId);
        userMapper.updateUserCash(userId, user.getUserCash() + 1);
    }

    public Object updateUser(User user) {
        user.setGmtModified(System.currentTimeMillis());
        user.setUserToken(UUID.randomUUID().toString());
        userMapper.changeUser(user);
        return user;
    }

    //协同过滤算法工具：返回观众列表
//    public List<User> audienceList() {
//        return userMapper.audienceList();
//    }
}


