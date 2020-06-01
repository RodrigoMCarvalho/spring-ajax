package com.rodrigo.springajax.controller;

import com.rodrigo.springajax.domain.SocialMetaTag;
import com.rodrigo.springajax.service.SocialMetaTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/meta")
public class SocialMetaTagController {

    @Autowired
    private SocialMetaTagService service;

    @PostMapping("/info")  //usado o post para o parametro não ficar na url
    public ResponseEntity<SocialMetaTag> getDadosViaUrl(@RequestParam("url") String url) {
        SocialMetaTag socialMetaTag = service.getSocialMetaTagByUrl(url);
        if(socialMetaTag!=null) {
            return ResponseEntity.ok(socialMetaTag);
        } else {
            return ResponseEntity.notFound().build();
        }
    }






















}
