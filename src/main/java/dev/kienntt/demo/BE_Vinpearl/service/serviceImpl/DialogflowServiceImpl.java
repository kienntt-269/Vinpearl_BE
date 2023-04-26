package dev.kienntt.demo.BE_Vinpearl.service.serviceImpl;

import com.google.cloud.dialogflow.v2beta1.*;
import com.google.protobuf.*;
import dev.kienntt.demo.BE_Vinpearl.service.DialogflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DialogflowServiceImpl implements DialogflowService {

//    @Autowired
//    private SessionsClient sessionsClient;
//
//    public DialogflowServiceImpl() throws IOException {
//        sessionsClient = SessionsClient.create();
//    }

    public String detectIntentTexts(String projectId, String text, String sessionId, String languageCode) {
        // Set the session ID and language code
//        SessionName session = SessionName.of(projectId, sessionId);
////        LanguageCode languageCodeEnum = LanguageCode.forValue(languageCode);
//
//        // Set the text (input query) and query parameters
//        TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);
//
//        QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
//
//        // Perform the text query request
//        DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
//
//        // Return the response text
//        return response.getQueryResult().getFulfillmentText();
        return null;
    }
}
