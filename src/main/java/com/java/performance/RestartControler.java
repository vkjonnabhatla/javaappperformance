package com.java.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestartControler {

    @Autowired
    private RestartService restartService;

    @PostMapping("/restart")
    public void restart(){
        //restartService.restartApp();
        try{
            MyPerformanceApplication.restart();
        }catch(Exception ex){
            System.out.println(ex);
        }

    }
}
