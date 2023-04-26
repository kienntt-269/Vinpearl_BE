package dev.kienntt.demo.BE_Vinpearl.service;

public interface DialogflowService {
    String detectIntentTexts(String projectId, String text, String sessionId, String languageCode);
}
