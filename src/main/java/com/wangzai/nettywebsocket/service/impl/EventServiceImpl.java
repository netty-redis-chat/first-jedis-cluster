package com.wangzai.nettywebsocket.service.impl;

import com.wangzai.nettywebsocket.pojo.Event;
import com.wangzai.nettywebsocket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class EventServiceImpl implements EventService {
    private static String rolesKey="roles";
    private static String eventKey="event";
    private static String userRoleKey="user:roles";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    HttpSession session;

    @Override
    public void create(Event event) {
        String[] roles = event.getRoles().split(",");
//        redisTemplate.opsForSet().add(rolesKey, roles[0]);
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                for (String role : roles) {
                    redisOperations.opsForSet().add(rolesKey, "izaya");
                }
                redisOperations.opsForValue().set(eventKey, event.getName());
                return null;
            }
        });
//        for (String role : roles) {
//            redisTemplate.opsForSet().add(rolesKey, role);
//        }
//        redisTemplate.opsForValue().set(eventKey, event.getName());
    }

    @Override
    public boolean getRole() {
        if (redisTemplate.opsForSet().size(rolesKey) == 0) {
            return false;
        }
        final String id = session.getId();
//        String roleName = (String) redisTemplate.execute(new SessionCallback() {
//            @Override
//            public Object execute(RedisOperations redisOperations) throws DataAccessException {
//                String role = (String) redisOperations.opsForSet().pop(rolesKey);
//                redisOperations.opsForHash().put(userRoleKey, id, role);
//                return role;
//            }
//        });
        String role = (String) redisTemplate.opsForSet().pop(rolesKey);
        redisTemplate.opsForHash().put(userRoleKey, id, role);
        return true;
    }

    @Override
    public boolean isRole() {
        return redisTemplate.opsForHash().hasKey(userRoleKey, session.getId());
    }

    @Override
    public boolean isEnd() {
        return true;
    }


}
