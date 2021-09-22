package com.lifesup.gbtd.controller;

import com.lifesup.gbtd.dto.response.GenericResponse;
import com.lifesup.gbtd.model.LifesupNoteEntity;
import com.lifesup.gbtd.repository.LifesupNoteRepository;
import com.lifesup.gbtd.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@Slf4j
public class HomeController {

    private final LifesupNoteRepository repository;
    private final TestService testService;

    @Autowired
    public HomeController(LifesupNoteRepository repository, TestService testService) {
        this.repository = repository;
        this.testService = testService;
    }

    @GetMapping("/hello")
    public GenericResponse<String> hello(String name) {
        log.info("HOME");
        try {
            testService.test();
        } catch (Exception e) {
            log.error("loi vl", e);
        }
        GenericResponse<String> res = new GenericResponse<>();
        res.setData("hello from the other side");
        return res;
    }

    @PostMapping("/note")
    @Transactional
    public GenericResponse<String> saveNote(@RequestBody LifesupNoteEntity entity) {
        repository.save(entity);
        return GenericResponse.success("ok");
    }
}
