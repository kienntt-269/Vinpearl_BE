package dev.kienntt.demo.BE_Vinpearl.controller;

import dev.kienntt.demo.BE_Vinpearl.service.DialogflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/dialogflow")
public class DialogflowController {

    @Autowired
    private DialogflowService dialogflowService;

    @PostMapping("/dialogflow")
    public String dialogflow(@RequestBody String query) throws Exception {
        String projectId = "your-project-id";
        String sessionId = "your-session-id";
        String languageCode = "en-US";

        return dialogflowService.detectIntentTexts(projectId, query, sessionId, languageCode);
    }
}
