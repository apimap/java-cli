/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
 */

package io.apimap.cli.commands;

import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.client.IRestClient;
import io.apimap.client.RestClientConfiguration;
import io.apimap.client.exception.IncorrectTokenException;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

@CommandLine.Command(
        name = "document",
        description = "Manage README.md and CHANGELOG.md documents",
        parameterListHeading = "Parameters"
)
public class DocumentCommand extends ApiCommand implements Runnable {
    @CommandLine.Option(
            names = {"--readme"},
            description = "File path to the README.md file to be published. E.g my-api/README.md"
    )
    protected String readmeFilePath;

    @CommandLine.Option(
            names = {"--changelog"},
            description = "File path to the CHANGELOG.md file to be published. E.g my-api/CHANGELOG.md"
    )
    protected String changelogFilePath;

    @CommandLine.Option(
            names = {"--api"},
            description = "Name of the API to be updated"
    )
    protected String apiName;

    @CommandLine.Option(
            names = {"--version"},
            description = "Name of the API version to be updated"
    )
    protected String apiVersion;

    @Override
    public void run() {
        if (this.apiName == null) {
            System.err.println("[Error] Missing API name");
            return;
        }

        if (this.apiVersion == null) {
            System.err.println("[Error] Missing API version");
            return;
        }

        if (this.readmeFilePath != null) {
            uploadReadmeDocument();
        }

        if (this.changelogFilePath != null) {
            uploadChangelogDocument();
        }
    }

    void uploadReadmeDocument(){
        final Consumer<String> errorHandlerCallback = content -> {
            System.err.println("[ERROR] Upload failed with: " + content);
        };

        final RestClientConfiguration configuration = defaultConfiguration(apiName);
        Object metadataReturnObject = null;

        try {
            final Path filePath = Path.of(this.readmeFilePath);
            final String content = Files.readString(filePath);

            metadataReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(apiName, JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .followResource(apiVersion)
                    .followCollection(JsonApiRestResponseWrapper.README_ELEMENT)
                    .createOrUpdateResource(content, ContentType.create("text/markdown"));

            if(metadataReturnObject != null){
                System.out.println("[OK] README uploaded");
            }
        } catch (IncorrectTokenException e) {
            System.err.println("[ERROR] Token not recognized");
        } catch (Exception e) {
            System.out.println("[ERROR] Unable to upload document");
        }
    }

    void uploadChangelogDocument(){
        final Consumer<String> errorHandlerCallback = content -> {
            System.err.println("[ERROR] Upload failed with: " + content);
        };

        final RestClientConfiguration configuration = defaultConfiguration(apiName);
        Object metadataReturnObject = null;

        try {
            final Path filePath = Path.of(this.changelogFilePath);
            String content = Files.readString(filePath);

            metadataReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(apiName, JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .followResource(apiVersion)
                    .followCollection(JsonApiRestResponseWrapper.CHANGELOG_ELEMENT)
                    .createOrUpdateResource(content, ContentType.create("text/markdown"));

            if(metadataReturnObject != null){
                System.out.println("[OK] CHANGELOG uploaded");
            }
        } catch (IncorrectTokenException e) {
            System.err.println("[ERROR] Token not recognized");
        } catch (Exception e) {
            System.out.println("[Error] Unable to upload document");
        }
    }
}
