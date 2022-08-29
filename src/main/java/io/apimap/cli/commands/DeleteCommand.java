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
import picocli.CommandLine;

@CommandLine.Command(
        name = "delete",
        description = "Delete a API or API version from the catalog",
        parameterListHeading = "Parameters"
)
public class DeleteCommand extends ApiCommand implements Runnable {
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

    @CommandLine.Option(
            names = {"--recursive"},
            negatable = true,
            description = "Delete single version or all versions"
    )
    protected boolean recursive;

    @CommandLine.Option(
            names = {"--confirmation"},
            description = "This will permanently REMOVE ALL information.",
            interactive = true,
            arity="0..1"
    )
    protected Boolean confirmation;

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

        if (confirmation == null) {
            String s = System.console().readLine("This will permanently REMOVE ALL information. Continue? y/n: ");
            confirmation = Boolean.valueOf(s) || "y".equalsIgnoreCase(s);
        }

        if(!confirmation){
            System.out.println("[CANCEL] Operation canceled");
            return;
        }

        final RestClientConfiguration configuration = defaultConfiguration(apiName);
        int metadataUploadStatus = 0;

        try {
            if (this.recursive) {
                metadataUploadStatus = IRestClient.withConfiguration(configuration)
                        .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                        .followResource(apiName)
                        .deleteResource();
            } else {
                metadataUploadStatus = IRestClient.withConfiguration(configuration)
                        .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                        .followResource(apiName)
                        .followCollection(JsonApiRestResponseWrapper.VERSION_COLLECTION)
                        .followResource(apiVersion)
                        .deleteResource();
            }

            if (metadataUploadStatus < 200 || metadataUploadStatus > 299) {
                System.out.println("[Error] Unable to delete API ( Status code: " + metadataUploadStatus + " )");
                return;
            }

            System.out.println("[OK] API deleted successfully");
        } catch (IncorrectTokenException e) {
            System.err.println("[ERROR] Token not recognized");
        } catch (Exception e) {
            System.out.println("[Error] Unable to delete API ( Status code: " + metadataUploadStatus + " )");
        }
    }
}
