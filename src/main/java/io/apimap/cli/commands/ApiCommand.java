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
import io.apimap.client.RestClientConfiguration;
import picocli.CommandLine;

import java.io.IOException;

public class ApiCommand {
    @CommandLine.Option(
            names = {"--endpoint"},
            description = "Root host URL the apimap.io instance to be used. E.g http://api.apimap.io/"
    )
    protected String endpointUrl;

    @CommandLine.Option(
            names = {"--token"},
            description = "API Authorization token"
    )
    protected String token;

    final RestClientConfiguration defaultConfiguration(String apiName){
        if (this.token == null) {
            try {
                return new RestClientConfiguration(new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME).readApiToken(apiName), getEndpointUrl());
            } catch (IOException ignored) {}
        }

        return new RestClientConfiguration(this.token, getEndpointUrl());
    }

    public String getEndpointUrl(){
        if(endpointUrl != null){
            return endpointUrl;
        }

        try {
            return new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME).readEndpoint();
        } catch (IOException ignored) {
            return null;
        }
    }
}
