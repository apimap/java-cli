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
import org.apache.hc.core5.http.ContentType;
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

    @Override
    public void run() {
        if (this.fromName == null) {
            System.err.println("[Error] Missing current name");
            return;
        }

        if (this.toName == null) {
            System.err.println("[Error] Missing new name");
            return;
        }

        final RestClientConfiguration configuration = defaultConfiguration(fromName);

        try {
            final Consumer<String> errorHandlerCallback = content -> {
                System.err.println("Rename failed with: " + content);
            };

            final ApiDataRestEntity oldEntity = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followResource(fromName)
                    .getResource(ApiDataRestEntity.class, ContentType.APPLICATION_JSON);

            if(oldEntity == null){
                System.err.println("[Error] Unable to get '" + fromName + "'");
                return;
            }

            oldEntity.setName(toName);

            final ApiDataRestEntity newEntity = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followResource(fromName)
                    .createOrUpdateResource(oldEntity, ContentType.APPLICATION_JSON);

            if(newEntity == null){
                System.err.println("[ERROR] Unable to get new renamed API");
                return;
            }

            final ConfigurationFileUtil util = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME);
            util.writeApiToken(toName, util.readApiToken(fromName));
            util.removeApiToken(fromName);

            System.out.println("[OK] Renamed API from '" + fromName + "' to '" + toName + "'");
            System.out.println("[NOTICE] Remember to upload a new metadata file with that contains the new name to complete the change");
        } catch (IOException | IncorrectTokenException e) {
            e.printStackTrace();
        }
    }
}
