package com.newsmanager.customer.controller;

import com.newsmanager.api.models.ThemeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import static com.newsmanager.customer.common.GlobalConfig.*;

@RestController
@RequestMapping("/news/theme")
public class ThemeController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/get")
    public String get(){
        return restTemplate.getForObject(SERVICE_PATH1+"/theme/get",String.class);
    }

    @PostMapping("/save")
    public void save(@RequestBody ThemeModel themeModel) {
        restTemplate.postForObject( SERVICE_PATH1 + "/theme/save", themeModel, String.class);
    }

    @DeleteMapping("/del/{tid}")
    public void del(@PathVariable int tid){
        restTemplate.delete(SERVICE_PATH1 + "/theme/del/" + tid);
    }

    @GetMapping("/getone")
    public String getone(@RequestParam int tid){
        return restTemplate.getForObject(SERVICE_PATH1 + "/theme/getone?tid="+tid, String.class);
    }
}
