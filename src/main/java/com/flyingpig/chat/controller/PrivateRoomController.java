package com.flyingpig.chat.controller;

import com.flyingpig.chat.service.IPrivateRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/private-room")
public class PrivateRoomController {

    @Autowired
    IPrivateRoomService privateRoomService;

}
