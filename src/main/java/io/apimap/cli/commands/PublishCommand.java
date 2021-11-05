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
import io.apimap.api.rest.ApiVersionDataRestEntity;
import io.apimap.api.rest.ClassificationDataRestEntity;
import io.apimap.api.rest.ClassificationRootRestEntity;
import io.apimap.api.rest.MetadataDataRestEntity;
import io.apimap.api.rest.MetadataRootRestEntity;
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.cli.utils.MetadataUtil;
import io.apimap.cli.utils.TaxonomyUtil;
import io.apimap.cli.utils.TokenUtil;
import io.apimap.client.IRestClient;
import io.apimap.client.RestClientConfiguration;
import io.apimap.client.exception.IncorrectTokenException;
import io.apimap.file.metadata.MetadataFile;
import io.apimap.file.taxonomy.TaxonomyFile;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
publish --metadata <file> --taxonomy <file> --endpoint-url http://localhost
 */
@CommandLine.Command(
        name = "publish",
        description = "Validate and publish the required files. This will create the API and required API version if anny of them are missing.",
        parameterListHeading = "Parameters"
)
public class PublishCommand extends ApiCommand implements Runnable {
    @CommandLine.Option(
            names = {"--metadata"},
            description = "File path to the metadata file to be published. E.g my-api/metadata.apicatalog"
    )
    protected String metadataFilePath;

    @CommandLine.Option(
            names = {"--taxonomy"},
            description = "File path to the taxonomy file to be published. E.g my-api/taxonomy.apicatalog"
    )
    protected String taxonomyFilePath;

    @CommandLine.Option(
            names = {"--code-repository-url"},
            description = "URL to the source code repository of this API. If the source code is not available this argument should be skipped"
    )
    protected String codeRepositoryUrl;

    public PublishCommand(){}

    public PublishCommand(String metadataFilePath,
                          String taxonomyFilePath,
                          String endpointUrl,
                          String codeRepositoryUrl,
                          String token){
        this.metadataFilePath = metadataFilePath;
        this.taxonomyFilePath = taxonomyFilePath;
        this.endpointUrl = endpointUrl;
        this.codeRepositoryUrl = codeRepositoryUrl;
        this.token = token;
    };

    @Override
    public void run() {
        if (this.endpointUrl == null) {
            System.err.println("[ERROR] Missing endpoint url");
            return;
        }

        if (this.metadataFilePath == null) {
            System.err.println("[ERROR] Missing required metadata file");
            return;
        }

        MetadataFile metadataFile = MetadataUtil.metadataFile(metadataFilePath);

        if (metadataFile == null) {
            System.err.println("[ERROR] Empty metadata file");
            return;
        }

        RestClientConfiguration configuration = defaultConfiguration(metadataFile.getData().getName());
        uploadMetadata(metadataFile, configuration);
        uploadTaxonomy(metadataFile.getData().getName(), metadataFile.getData().getApiVersion(), configuration);
    }

    private void uploadMetadata(MetadataFile metadataFile, RestClientConfiguration configuration){
        MetadataDataRestEntity metadataDataRestEntity = new MetadataDataRestEntity(
                metadataFile.getData().getName(),
                metadataFile.getData().getDescription(),
                metadataFile.getData().getVisibility(),
                metadataFile.getData().getApiVersion(),
                metadataFile.getData().getReleaseStatus(),
                metadataFile.getData().getInterfaceSpecification(),
                metadataFile.getData().getInterfaceDescriptionLanguage(),
                metadataFile.getData().getArchitectureLayer(),
                metadataFile.getData().getBusinessUnit(),
                metadataFile.getData().getSystemIdentifier(),
                metadataFile.getData().getDocumentation()
        );

        MetadataRootRestEntity metadataRootRestEntity = new MetadataRootRestEntity(metadataDataRestEntity, metadataFile.getVersion());

        ApiDataRestEntity apiDataRestEntity = new ApiDataRestEntity(
                metadataFile.getData().getName(),
                this.codeRepositoryUrl
        );

        ApiVersionDataRestEntity apiVersionDataRestEntity = new ApiVersionDataRestEntity(metadataDataRestEntity.getApiVersion());

        Consumer<Object> apiCreatedCallback = content -> {
            TokenUtil util = new TokenUtil(TokenUtil.FILENAME);
            try {
                util.writeApiToken(((ApiDataRestEntity) content).getName(), ((ApiDataRestEntity) content).getMeta().getToken());
                configuration.setToken(((ApiDataRestEntity) content).getMeta().getToken());
            } catch (IOException e) {
                System.err.println("[WARNING] Unable to save token to file. Please copy the token and store it securely");
            }
            System.out.println("[OK] API \"" + ((ApiDataRestEntity) content).getName() + "\" created with access token: " + ((ApiDataRestEntity) content).getMeta().getToken());
        };

        Consumer<Object> apiVersionCreatedCallback = content -> {
            System.out.println("[OK] New API version created");
        };

        Consumer<String> errorHandlerCallback = content -> {
            System.err.println("[ERROR] Upload failed with: " + content);
        };

        MetadataRootRestEntity metadataReturnObject = null;
        try {
            metadataReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(metadataDataRestEntity.getName(), JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .onMissingCreate(metadataDataRestEntity.getName(), apiDataRestEntity, apiCreatedCallback)
                    .followResource(metadataDataRestEntity.getApiVersion())
                    .onMissingCreate(metadataDataRestEntity.getApiVersion(), apiVersionDataRestEntity, apiVersionCreatedCallback)
                    .followCollection(JsonApiRestResponseWrapper.METADATA_COLLECTION)
                    .createOrUpdateResource(metadataRootRestEntity);
        } catch (IOException e) {
            System.err.println(e);
        } catch (IncorrectTokenException e) {
            System.err.println("[ERROR] Token not recognized");
        }

        if (metadataReturnObject == null) {
            System.err.println("[ERROR] Unable to upload metadata");
            return;
        }

        System.out.println("[OK] Metadata published successfully");
    }

    private void uploadTaxonomy(String apiName, String apiVersion, RestClientConfiguration configuration){
        TaxonomyFile taxonomyFile = TaxonomyUtil.taxonomyFile(taxonomyFilePath);

        if (taxonomyFile == null) {
            return;
        }

        ClassificationRootRestEntity classificationRootRestEntity = new ClassificationRootRestEntity(
                taxonomyFile
                        .getData()
                        .getClassifications()
                        .stream()
                        .map(e -> new ClassificationDataRestEntity(e, taxonomyFile.getVersion()))
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        try {
            Consumer<String> errorHandlerCallback = content -> {
                System.err.println("[ERROR] Upload failed with: " + content);
            };

            ClassificationRootRestEntity classificationReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(apiName, JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .followResource(apiVersion)
                    .followCollection(JsonApiRestResponseWrapper.CLASSIFICATION_COLLECTION)
                    .createOrUpdateResource(classificationRootRestEntity);

            if (classificationReturnObject == null) {
                System.err.println("[ERROR] Unable to upload taxonomy classifications");
                return;
            }

            System.out.println("[OK] Files published successfully");
        } catch (IncorrectTokenException e) {
            System.err.println("[ERROR] Token not recognized");
        } catch (Exception e) {
            System.err.println("[ERROR] Unable to upload taxonomy classifications");
        }
    }
}
