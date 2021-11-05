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
import io.apimap.file.taxonomy.TaxonomyFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TaxonomyUtil {
    public static TaxonomyFile taxonomyFile(String filePath) {
        try {
            TaxonomyFile returnValue = validateTaxonomyFile(filePath);

            if (returnValue != null) {
                System.out.println("[OK] Taxonomy validated successfully");
            }

            return returnValue;
        } catch (JsonMappingException e) {
            System.err.println("[ERROR] Unable to map taxonomy file values");
        } catch (JsonParseException e) {
            System.err.println("[ERROR] Unable to parse taxonomy file structure");
        } catch (IOException e) {
            System.err.println("[ERROR] Unable to read taxonomy file");
        }

        return null;
    }

    private static TaxonomyFile validateTaxonomyFile(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new FileNotFoundException("[ERROR] Empty taxonomy file path");
        }

        try {
            FileInputStream fileReader = new FileInputStream(filePath);
            return FileFactory.taxonomyFromInputStream(fileReader);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }

        return null;
    }
}
