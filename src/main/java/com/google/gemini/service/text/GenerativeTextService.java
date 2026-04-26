package com.google.gemini.service.text;

import com.google.gemini.dto.PromptDto;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.Optional;

@Component
public class GenerativeTextService {

    @Value("${GOOGLE_API_KEY}")
    private String apiKey;

        public ResponseEntity<String> generateText(PromptDto promptDto) {
            // The client gets the API key from the environment variable `GEMINI_API_KEY`.

            //first way to create client
            Client client = null;

            try{
                client = new Client();
            }catch(Exception ex) {
                System.out.println("Exception Occured" + ex.getMessage());
            }

            //second way to create client
            Client client2 = Client.builder().apiKey(apiKey).build();



            GenerateContentResponse response =
                    client2.models.generateContent(
                            "gemini-2.5-flash",
                            promptDto.getPrompt(),
                            null);

            System.out.println(response.text());

            return new ResponseEntity<String>(response.text(), HttpStatus.CREATED);

        }

        //generate content stream
    public ResponseBodyEmitter generateContentStream(String prompt) {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        new Thread(() -> {
            try {
                Client client = new Client();
                ResponseStream<GenerateContentResponse> responseStream =
                        client.models.generateContentStream("gemini-2.5-flash", prompt, null);

                for (GenerateContentResponse res : responseStream) {
                    emitter.send("data: " + res.text() + "\n\n", MediaType.TEXT_EVENT_STREAM);
                }

                responseStream.close();
                emitter.complete(); // Signal that streaming is complete
            } catch (Exception e) {
                emitter.completeWithError(e); // Handle errors
            }
        }).start();

        return emitter;
    }


        public void generateContentByUsingBothPromptAndImage(){
            Client client = new Client();

            // Construct a multimodal content with quick constructors
            Content content =
                    Content.fromParts(
                            Part.fromText("describe the image"),
                            Part.fromUri("https://i.ibb.co/ynd9Ssc9/Screenshot-2024-06-28-21-18-13.png", "image/png"));

            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", content, null);


            System.out.println("Response: " + response.text());
        }
    }

