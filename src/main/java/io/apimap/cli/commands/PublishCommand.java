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
import io.apimap.api.rest.jsonapi.JsonApiRestResponseWrapper;
import io.apimap.cli.utils.MetadataUtil;
import io.apimap.cli.utils.TaxonomyUtil;
import io.apimap.cli.utils.ConfigurationFileUtil;
import io.apimap.client.IRestClient;
import io.apimap.client.RestClientConfiguration;
import io.apimap.client.exception.IncorrectTokenException;
import io.apimap.file.metadata.MetadataFile;
import io.apimap.file.taxonomy.TaxonomyFile;
import org.apache.hc.core5.http.ContentType;
import picocli.CommandLine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/*
publish --metadata <file> --taxonomy <file> --endpoint http://localhost
 */
@CommandLine.Command(
        name = "publish",
        description = "Validate and publish the required files. This will create the API and required API version if anny of them are missing.",
        parameterListHeading = "Parameters"
)
public class PublishCommand extends ApiCommand implements Runnable {
    @CommandLine.Option(
            names = {"--metadata"},
            description = "File path to the metadata file to be published. E.g my-api/metadata.apimap"
    )
    protected String metadataFilePath;

    @CommandLine.Option(
            names = {"--taxonomy"},
            description = "File path to the taxonomy file to be published. E.g my-api/taxonomy.apimap"
    )
    protected String taxonomyFilePath;

    @CommandLine.Option(
            names = {"--code-repository-url"},
            description = "URL to the source code repository of this API. If the source code is not available this argument should be skipped"
    )
    protected String codeRepositoryUrl;

    @Override
    public void run() {
        if (this.metadataFilePath == null) {
            System.err.println("[ERROR] Missing required metadata file");
            return;
        }

        final MetadataFile metadataFile = MetadataUtil.metadataFile(metadataFilePath);

        if (metadataFile == null) {
            System.err.println("[ERROR] Empty metadata file");
            return;
        }

        final RestClientConfiguration configuration = defaultConfiguration(metadataFile.getData().getName());
        uploadMetadata(metadataFile, configuration);
        uploadTaxonomy(metadataFile.getData().getName(), metadataFile.getData().getApiVersion(), configuration);
    }

    private void uploadMetadata(final MetadataFile metadataFile,
                                final RestClientConfiguration configuration){
        final MetadataDataRestEntity metadataDataApiEntity = new MetadataDataRestEntity(
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

        final ApiDataRestEntity apiDataApiEntity = new ApiDataRestEntity(
                metadataDataApiEntity.getName(),
                this.codeRepositoryUrl
        );

        final ApiVersionDataRestEntity apiVersionDataApiEntity = new ApiVersionDataRestEntity(
                metadataDataApiEntity.getApiVersion()
        );

        final Consumer<Object> apiCreatedCallback = content -> {
            ConfigurationFileUtil util = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME);
            try {
                util.writeApiToken(((ApiDataRestEntity) content).getName(), ((ApiDataRestEntity) content).getMeta().getToken());
                configuration.setToken(((ApiDataRestEntity) content).getMeta().getToken());
            } catch (IOException e) {
                System.err.println("[WARNING] Unable to save token to file. Please copy the token and store it securely");
            }
            System.out.println("[OK] API \"" + ((ApiDataRestEntity) content).getName() + "\" created with access token: " + ((ApiDataRestEntity) content).getMeta().getToken());
        };

        final Consumer<Object> apiVersionCreatedCallback = content -> {
            System.out.println("[OK] New API version created");
        };

        final Consumer<String> errorHandlerCallback = content -> {
            System.err.println("[ERROR] Upload failed with: " + content);
        };

        System.out.println(apiDataApiEntity.getAttributes().toString());

        MetadataDataRestEntity metadataReturnObject = null;
        try {
            metadataReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(metadataDataApiEntity.getName(), JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .onMissingCreate(metadataDataApiEntity.getName(), apiDataApiEntity, apiCreatedCallback)
                    .followResource(metadataDataApiEntity.getApiVersion())
                    .onMissingCreate(metadataDataApiEntity.getApiVersion(), apiVersionDataApiEntity, apiVersionCreatedCallback)
                    .followCollection(JsonApiRestResponseWrapper.METADATA_COLLECTION)
                    .createOrUpdateResource(metadataDataApiEntity, ContentType.APPLICATION_JSON);
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

    private void uploadTaxonomy(final String apiName,
                                final String apiVersion,
                                final RestClientConfiguration configuration){
        final TaxonomyFile taxonomyFile = TaxonomyUtil.taxonomyFile(taxonomyFilePath);

        if (taxonomyFile == null) {
            return;
        }

        final ClassificationRootRestEntity classificationRootRestEntity = new ClassificationRootRestEntity(
                taxonomyFile
                        .getData()
                        .getClassifications()
                        .stream()
                        .map(e -> new ClassificationDataRestEntity(e, taxonomyFile.getVersion()))
                        .collect(Collectors.toCollection(ArrayList::new))
        );

        try {
            final Consumer<String> errorHandlerCallback = content -> {
                System.err.println("[ERROR] Upload failed with: " + content);
            };

            final ClassificationRootRestEntity classificationReturnObject = IRestClient.withConfiguration(configuration)
                    .withErrorHandler(errorHandlerCallback)
                    .followCollection(JsonApiRestResponseWrapper.API_COLLECTION)
                    .followCollection(apiName, JsonApiRestResponseWrapper.VERSION_COLLECTION)
                    .followResource(apiVersion)
                    .followCollection(JsonApiRestResponseWrapper.CLASSIFICATION_COLLECTION)
                    .createOrUpdateResource(classificationRootRestEntity, ContentType.APPLICATION_JSON);

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
