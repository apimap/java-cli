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
import io.apimap.cli.utils.MetadataUtil;
import io.apimap.client.IRestClient;
import io.apimap.client.RestClientConfiguration;
import io.apimap.client.exception.IncorrectTokenException;
import io.apimap.file.metadata.MetadataFile;
import picocli.CommandLine;

@CommandLine.Command(
        name = "delete",
        description = "Delete a API or API version from the catalog",
        parameterListHeading = "Parameters"
)
public class DeleteCommand extends ApiCommand implements Runnable {
    @CommandLine.Option(
            names = {"--metadata"},
            description = "File path to the metadata file to be published. E.g my-api/metadata.apicatalog"
    )
    protected String metadataFilePath;

    @CommandLine.Option(
            names = {"--recursive"},
            negatable = true,
            description = "Delete single version or all versions"
    )
    protected boolean recursive;

    @Override
    public void run() {
        if (this.endpointUrl == null) {
            System.err.println("[Error] Missing endpoint url");
            return;
        }

        if (this.metadataFilePath == null) {
            System.err.println("[Error] Missing metadata file");
            return;
        }

        MetadataFile metadataFile = MetadataUtil.metadataFile(metadataFilePath);
        RestClientConfiguration configuration = defaultConfiguration(metadataFile.getData().getName());

        if (metadataFile == null) {
            return;
        }

        int metadataUploadStatus = 0;

        try {
            if (this.recursive) {
                metadataUploadStatus = IRestClient.withConfiguration(configuration)
                        .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                        .followResource(metadataFile.getData().getName())
                        .deleteResource();
            } else {
                metadataUploadStatus = IRestClient.withConfiguration(configuration)
                        .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                        .followResource(metadataFile.getData().getName())
                        .followCollection(JsonApiRestResponseWrapper.VERSION_COLLECTION)
                        .followResource(metadataFile.getData().getApiVersion())
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
