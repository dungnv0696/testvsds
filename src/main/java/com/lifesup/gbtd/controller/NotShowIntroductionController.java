package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.model.NotShowIntroductionEntity;
import com.lifesup.gbtd.service.inteface.INotShowIntroductionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NotShowIntroductionController {
    @Autowired
    private INotShowIntroductionService notShowIntroductionService;

    @PostMapping("/click-popup")
    public String clickPopup(@RequestBody NotShowIntroductionEntity notShowIntroductionEntity){
        notShowIntroductionService.creat(notShowIntroductionEntity);
        return "done";
    }
}
