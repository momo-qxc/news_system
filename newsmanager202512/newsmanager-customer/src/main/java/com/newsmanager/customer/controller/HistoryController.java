package com.newsmanager.customer.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import static com.newsmanager.customer.common.GlobalConfig.SERVICE_PATH1;

@RestController
@RequestMapping("/news/history")
public class HistoryController {
    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getdetail")
    @Operation(description = "获取用户浏览历史详情")
    public String getDetail(@RequestParam String phone) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/history/getdetail?phone=" + phone, String.class);
    }

    @GetMapping("/getdetail_pager")
    @Operation(description = "分页获取用户浏览历史详情")
    public String getDetailPager(
            @RequestParam String phone,
            @RequestParam(defaultValue = "1") int pageno,
            @RequestParam(defaultValue = "10") int pagesize) {
        return restTemplate.getForObject(SERVICE_PATH1 + "/history/getdetail_pager?phone=" + phone + "&pageno=" + pageno
                + "&pagesize=" + pagesize, String.class);
    }

    @PostMapping("/save")
    @Operation(description = "记录浏览历史")
    public void save(@RequestParam String phone, @RequestParam String nid, @RequestParam String createdate) {
        org.springframework.util.MultiValueMap<String, String> params = new org.springframework.util.LinkedMultiValueMap<>();
        params.add("phone", phone);
        params.add("nid", nid);
        params.add("createdate", createdate);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
        org.springframework.http.HttpEntity<org.springframework.util.MultiValueMap<String, String>> request = new org.springframework.http.HttpEntity<>(
                params, headers);

        restTemplate.postForObject(SERVICE_PATH1 + "/history/save", request, String.class);
    }

    @DeleteMapping("/del/{hid}")
    @Operation(description = "删除单条浏览记录")
    public void del(@PathVariable int hid) {
        restTemplate.delete(SERVICE_PATH1 + "/history/del/" + hid);
    }

    @DeleteMapping("/clear")
    @Operation(description = "清空当前用户的浏览记录")
    public void clearAll(@RequestParam String phone) {
        restTemplate.delete(SERVICE_PATH1 + "/history/clear?phone=" + phone);
    }
}
