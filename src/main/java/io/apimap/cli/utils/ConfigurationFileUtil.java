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
import io.apimap.cli.entities.ConfigurationFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ConfigurationFileUtil {
    public static String FILENAME = "apimap.conf";

    protected final String filename;

    public ConfigurationFileUtil(String filename) {
        this.filename = filename;
    }

    public ConfigurationFile readFile() throws IOException {
        try {
            File file = configurationFile();

            if(file.length() == 0){
                return new ConfigurationFile();
            }

            return defaultObjectMapper().readValue(file, ConfigurationFile.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public String readApiToken(String apiName) throws IOException {
        ConfigurationFile file = readFile();
        return file.getToken(apiName);
    }

    public String readEndpoint() throws IOException {
        ConfigurationFile file = readFile();
        return file.getEndpoint();
    }

    public void writeApiToken(String apiName, String token) throws IOException {
        ConfigurationFile existingFileContent = readFile();
        existingFileContent.setToken(apiName, token);
        defaultObjectMapper().writeValue(configurationFile(), existingFileContent);
    }

    public void writeEndpoint(String endpoint) throws IOException {
        ConfigurationFile existingFileContent = readFile();
        existingFileContent.setEndpoint(endpoint);
        defaultObjectMapper().writeValue(configurationFile(), existingFileContent);
    }

    public String filePath(){
        return System.getProperty("user.home") + "/" + configDirectory() + "/" + this.filename;
    }

    private File configurationFile() throws IOException {
        File file = new File(filePath());

        try {
            file.createNewFile();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return file;
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

    protected String configDirectory(){
        return ".config";
    }
}
