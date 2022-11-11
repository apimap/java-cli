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
        name = "token",
        description = "Manage stored tokens"
)
public class TokenCommand implements Runnable {

    @Override
    public void run() {
        final ConfigurationFileUtil util = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME);

        try {
            System.out.printf("%-30.30s  %-40.40s%n", "API", "Token");
            util.readFile().getTokenArray().forEach((token) -> {
                System.out.printf("%-30.30s  %-40.40s%n", token.getApiName(), token.getToken());
            });
        } catch (IOException e) {
            System.err.println("Unable to read config file.");
        }
    }
}
