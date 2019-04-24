package com.wangzai.nettywebsocket.service;

import com.wangzai.nettywebsocket.pojo.Event;

public interface EventService {
    void create(Event event);

    boolean getRole();

    boolean isRole();

    boolean isEnd();
}
