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
        name = "config",
        description = "Manage configuration",
        subcommands = {
                EndpointConfigCommand.class
        }
)
public class ConfigCommand  implements Runnable {
    @Override
    public void run() {
        final ConfigurationFileUtil util = new ConfigurationFileUtil(ConfigurationFileUtil.FILENAME);

        try {
            System.out.printf("%-20.20s  %-40.40s%n", "Parameter", "Value");
            System.out.printf("%-20.20s  %-40.40s%n", "File location", util.filePath());
            System.out.printf("%-20.20s  %-40.40s%n", "Endpoint", util.readEndpoint());
        } catch (IOException e) {
            System.err.println("[ERROR] Unable to read config file.");
        }
    }
}
