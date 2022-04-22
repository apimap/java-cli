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

import io.apimap.api.rest.ApiDataRestEntity;
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.cli.utils.ConfigurationFileUtil;
import io.apimap.client.IRestClient;
import io.apimap.client.RestClientConfiguration;
import io.apimap.client.exception.IncorrectTokenException;
import picocli.CommandLine;

import java.io.IOException;
import java.util.function.Consumer;

@CommandLine.Command(
        name = "rename",
        description = "Rename an existing API",
        parameterListHeading = "Parameters"
)
public class RenameCommand extends ApiCommand implements Runnable {
    @CommandLine.Option(
            names = {"--from"},
            description = "The current API name"
    )
    protected String fromName;

    @CommandLine.Option(
            names = {"--to"},
            description = "The new API name"
    )
    protected String toName;

    @CommandLine.Option(
            names = {"--confirmation"},
            description = "Renaming an API will permanently REMOVE ALL associated information.",
            interactive = true,
            arity="0..1"
    )
    protected Boolean confirmation;

    @Override
    public void run() {
        if (this.endpointUrl == null) {
            try {
                this.endpointUrl = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME).readEndpoint();
            } catch (IOException ignored) {
            }

            if (this.endpointUrl == null) {
                System.err.println("[Error] Missing endpoint url");
                return;
            }
        }

        if (this.fromName == null) {
            System.err.println("[Error] Missing current name");
            return;
        }

        if (this.toName == null) {
            System.err.println("[Error] Missing new name");
            return;
        }

        if (confirmation == null) {
            String s = System.console().readLine("Renaming an API will permanently REMOVE ALL associated information. Continue? y/n: ");
            confirmation = Boolean.valueOf(s) || "y".equalsIgnoreCase(s);
        }

        if(!confirmation){
            System.out.println("[CANCEL] Operation canceled");
            return;
        }

        RestClientConfiguration configuration = defaultConfiguration(fromName);

        try {
            Consumer<String> errorHandlerCallback = content -> {
                System.err.println("Rename failed with: " + content);
            };

            ApiDataRestEntity oldEntity = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followResource(fromName)
                    .getResource(ApiDataRestEntity.class);

            if(oldEntity == null){
                System.err.println("[Error] Unable to get '" + fromName + "'");
                return;
            }

            oldEntity.setName(toName);

            ApiDataRestEntity newEntity = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followResource(fromName)
                    .createOrUpdateResource(oldEntity);

            if(newEntity == null){
                System.err.println("[Error] Unable to get new renamed API");
                return;
            }

            int metadataUploadStatus = metadataUploadStatus = IRestClient.withConfiguration(configuration)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followResource(fromName)
                    .deleteResource();

            if (metadataUploadStatus > 200 && metadataUploadStatus < 299) {
                System.out.println("[OK] Renamed API from '" + fromName + "' to '" + toName + "'");
                return;
            }

            System.err.println("[Error] Unable to remove old API after rename");
        } catch (IOException | IncorrectTokenException e) {
            e.printStackTrace();
        }
    }
}
