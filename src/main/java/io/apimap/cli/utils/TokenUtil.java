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

package io.apimap.cli.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.apimap.cli.entities.TokenFile;

import java.io.File;
import java.io.IOException;

public class TokenUtil {
    public static String FILENAME = "apicatalog.conf";

    private final String filename;

    public TokenUtil(String filename) {
        this.filename = filename;
    }

    public TokenFile readFile() throws IOException {
        try {
            return defaultObjectMapper().readValue(configurationFile(), TokenFile.class);
        } catch (Exception e) {
            throw e;
        }
    }

    public String readApiToken(String apiName) throws IOException {
        try {
            TokenFile file = readFile();
            return file.getToken(apiName);
        } catch (Exception e) {
            throw e;
        }
    }

    public void writeApiToken(String apiName, String token) throws IOException {
        TokenFile file = new TokenFile();

        try {
            file = readFile();
        } catch (IOException e) {
            // Ignore exceptions, just create a new file
        }

        try {
            File outputFile = configurationFile();
            outputFile.createNewFile();
            file.setToken(apiName, token);
            defaultObjectMapper().writeValue(outputFile, file);
        } catch (Exception e) {
            throw e;
        }
    }

    private File configurationFile() {
        return new File(System.getProperty("user.home") + "/.config/" + this.filename);
    }

    private ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        mapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false);
        mapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return mapper;
    }
}
