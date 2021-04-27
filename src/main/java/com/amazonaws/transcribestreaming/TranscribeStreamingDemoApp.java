/*
 * Copyright 2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazonaws.transcribestreaming;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.comprehend.model.LanguageCode;

public class TranscribeStreamingDemoApp extends Application {

  private static final Logger log = LoggerFactory.getLogger(TranscribeStreamingDemoApp.class);

  private static final ComprehendClient comprehend = ComprehendClient.create();

    @Override
    public void start(Stage primaryStage)  {

        WindowController windowController = new WindowController(primaryStage);

        primaryStage.setOnCloseRequest(__ -> {
            windowController.close();
            System.exit(0);
        });
        primaryStage.show();

        Thread transcribing = new Thread(new Runnable() {
          @Override
          public void run() {
            while (true) {
              log.info("Waiting for finished sentences...");
              try {
                String phrase = windowController.pendingAnalysis.take();
                log.debug("Analyzing {}", phrase);
                final DetectSentimentResponse detectSentimentResponse = comprehend
                    .detectSentiment(DetectSentimentRequest.builder()
                        .languageCode(LanguageCode.EN)
                        .text(phrase)
                        .build());
                log.debug("Response: {}", detectSentimentResponse);
                log.debug("Response: {} / {}", detectSentimentResponse.sentiment(), detectSentimentResponse.sentimentScore());
              } catch (InterruptedException e) {

              }
            }
          }
        });
        transcribing.setDaemon(true);
      transcribing.setName("Transcriber");
      transcribing.start();
    }

    public static void main(String args[]) {
        launch(args);
    }

}