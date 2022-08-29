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

import io.apimap.cli.utils.ConfigurationFileUtil;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "endpoint",
        description = "Update endpoint configuration"
)
public class EndpointConfigCommand implements Runnable {

    @CommandLine.Option(
            names = {"--url"},
            description = "Endpoint URL to the Apimap instance used. E.g http://localhost:8080"
    )
    protected String url;

    @Override
    public void run() {
        if (this.url == null) {
            System.err.println("[ERROR] Missing endpoint url");
            return;
        }

        final ConfigurationFileUtil util = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME);
        try {
            util.writeEndpoint(url);
            System.out.println("[OK] New endpoint: " + url);
        } catch (IOException e) {
            System.out.println("[ERROR] Unable to update configuration");
        }
    }
}
