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

package io.apimap.cli.entities;

import java.util.HashMap;

public class ConfigurationFile {
    protected HashMap<String, String> tokens = new HashMap<>();
    protected String endpoint;

    public ConfigurationFile() {
    }

    public ConfigurationFile(HashMap<String, String> tokens) {
        this.tokens = tokens;
    }

    public ConfigurationFile(HashMap<String, String> tokens, String endpoint) {
        this.tokens = tokens;
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public HashMap<String, String> getTokens() {
        if (tokens == null) {
            return new HashMap<>();
        }

        return tokens;
    }

    public void setTokens(HashMap<String, String> tokens) {
        this.tokens = tokens;
    }

    public String getToken(String apiName) {
        if (this.tokens == null) {
            return null;
        }

        return this.tokens.get(apiName);
    }

    public void setToken(String apiName, String token) {
        if (this.tokens == null) {
            this.tokens = new HashMap<>();
        }

        this.tokens.put(apiName, token);
    }

    @Override
    public String toString() {
        return "ConfigurationFile{" +
                "tokens=" + tokens +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
