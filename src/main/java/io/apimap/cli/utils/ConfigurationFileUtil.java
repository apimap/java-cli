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

public class ConfigurationFileUtil {
    public final static String FILENAME = "apimap.conf";

    protected final String filename;

    public ConfigurationFileUtil(final String filename) {
        this.filename = filename;
    }

    public ConfigurationFile readFile() throws IOException {
        try {
            final File file = configurationFile();

            if(file.length() == 0){
                return new ConfigurationFile();
            }

            return defaultObjectMapper().readValue(file, ConfigurationFile.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public String readApiToken(final String apiName) throws IOException {
        final ConfigurationFile file = readFile();
        return file.getToken(apiName).getToken();
    }

    public String readEndpoint() throws IOException {
        final ConfigurationFile file = readFile();
        return (file.getEndpoint() != null) ? file.getEndpoint() : "Not defined";
    }

    public void removeApiToken(final String apiName) throws IOException {
        final ConfigurationFile existingFileContent = readFile();
        existingFileContent.removeToken(apiName);
        defaultObjectMapper().writeValue(configurationFile(), existingFileContent);
    }

    public void writeApiToken(final String apiName,
                              final String token) throws IOException {
        final ConfigurationFile existingFileContent = readFile();
        existingFileContent.setToken(apiName, token);
        defaultObjectMapper().writeValue(configurationFile(), existingFileContent);
    }

    public void writeEndpoint(final String endpoint) throws IOException {
        final ConfigurationFile existingFileContent = readFile();
        existingFileContent.setEndpoint(endpoint);
        defaultObjectMapper().writeValue(configurationFile(), existingFileContent);
    }

    public String filePath(){
        return folderPath() + "/" + this.filename;
    }

    public String folderPath(){
        return System.getProperty("user.home") + "/" + configDirectory();
    }

    private File configurationFile() throws IOException {
        final File file = new File(filePath());

        if(!file.exists()){
            try {
                final File folder = new File(folderPath());

                if(!folder.mkdirs()){
                    System.err.println("[ERROR] Unable to created necessary config folders");
                }

                if(!file.createNewFile()){
                    System.err.println("[ERROR] Unable to created config file");
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        return file;
    }

    private ObjectMapper defaultObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
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
