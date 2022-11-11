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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurationFile {
    protected HashMap<String, String> tokens = new HashMap<>();
    protected String endpoint;

    public ConfigurationFile() {
    }

    public ConfigurationFile(final String endpoint) {
        this.endpoint = endpoint;
    }

    public ConfigurationFile(final String endpoint, final HashMap<String, String> tokens) {
        this.endpoint = endpoint;
        this.tokens = tokens;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public void removeToken(final String apiName){
        if(tokens != null){
            tokens.remove(apiName);
        }
    }

    public HashMap<String, String> getTokens() {
        return this.tokens;
    }

    @JsonIgnore
    public List<Token> getTokenArray() {
        if (tokens == null) { return new ArrayList<>();}
        return tokens
                .keySet()
                .stream()
                .map(key -> new Token(key, tokens.get(key)))
                .toList();
    }

    public Token getToken(final String apiName) {
        if (this.tokens == null) { return null;}
        return new Token(apiName, this.tokens.get(apiName));
    }

    public void setToken(final String apiName,
                         final String token) {
        if (this.tokens == null) { this.tokens = new HashMap<>(); }
        this.tokens.put(apiName, token);
    }

    public static class Token {
        private String apiName;
        private String token;

        public Token(){
        }

        public Token(String apiName, String token) {
            this.apiName = apiName;
            this.token = token;
        }

        public String getApiName() {
            return apiName;
        }

        public String getToken() {
            return token;
        }

        public void setApiName(String apiName) {
            this.apiName = apiName;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @Override
            public String toString() {
                return "Token{" +
                        "apiName='" + apiName + '\'' +
                        ", token='" + token + '\'' +
                        '}';
            }
        }

    @Override
    public String toString() {
        return "ConfigurationFile{" +
                "tokens=" + tokens +
                ", endpoint='" + endpoint + '\'' +
                '}';
    }
}
