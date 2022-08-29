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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.apimap.file.FileFactory;
import io.apimap.file.metadata.MetadataFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MetadataUtil {
    public static MetadataFile metadataFile(final String filePath) {
        try {
            final MetadataFile returnValue = validateMetadataFile(filePath);

            if (returnValue != null) {
                System.err.println("[OK] Metadata validated successfully");
            }

            return returnValue;
        } catch (JsonMappingException e) {
            System.err.println("[ERROR] Unable to map metadata file values");
        } catch (JsonParseException e) {
            System.err.println("[ERROR] Unable to parse metadata file structure");
        } catch (IOException e) {
            System.err.println("[ERROR] Unable to read metadata file");
        }

        return null;
    }

    private static MetadataFile validateMetadataFile(final String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FileNotFoundException("[ERROR] Empty metadata file path");
        }

        try(final FileInputStream fileReader = new FileInputStream(filePath)) {
            return FileFactory.metadataFromInputStream(fileReader);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }

        return null;
    }
}
