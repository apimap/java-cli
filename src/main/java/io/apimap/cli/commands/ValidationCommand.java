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

import io.apimap.cli.utils.MetadataUtil;
import io.apimap.cli.utils.TaxonomyUtil;
import picocli.CommandLine;

@CommandLine.Command(
        name = "validate",
        description = "Validate metadata and taxonomy file content",
        parameterListHeading = "Parameters"
)
public class ValidationCommand implements Runnable {
    @CommandLine.Option(
            names = {"--metadata"},
            description = "File path to the metadata file to be validated. E.g my-api/metadata.apicatalog"
    )
    protected String metadataFilePath;

    @CommandLine.Option(
            names = {"--taxonomy"},
            description = "File path to the taxonomy file to be validated. E.g my-api/taxonomy.apicatalog"
    )
    protected String taxonomyFilePath;

    @Override
    public void run() {
        if (metadataFilePath != null) {
            MetadataUtil.metadataFile(metadataFilePath);
        }

        if (taxonomyFilePath != null) {
            TaxonomyUtil.taxonomyFile(taxonomyFilePath);
        }
    }
}
