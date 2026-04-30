package com.google.gemini.controllers;

import com.google.gemini.dto.PromptDto;
import com.google.gemini.service.text.GenerativeTextService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class TestController {
    GenerativeTextService generativeTextService;
    public TestController(GenerativeTextService service){
        this.generativeTextService = service;
    }


    @GetMapping("/welcome")
    public String welcome(){
        //generativeTextService.generateContentStream();
        generativeTextService.generateContentByUsingBothPromptAndImage();
        return "Welcome to Gemini!";
    }

    @PostMapping("/generate/text")
    public String getGenerativeText(@RequestBody PromptDto promptDto) {
        System.out.println("Prompt from frontend: " + promptDto.getPrompt());
        return generativeTextService.generateText(promptDto).getBody();
    }

    @GetMapping(value= "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public ResponseBodyEmitter streamGenerativeText(@RequestParam String prompt) {
        System.out.println("Prompt from frontend: " + prompt);
        return generativeTextService.generateContentStream(prompt);
    }



}
