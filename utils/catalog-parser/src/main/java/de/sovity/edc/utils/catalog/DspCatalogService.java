/*
 * Copyright (c) 2023 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - init
 *
 */

package de.sovity.edc.utils.catalog;

import de.sovity.edc.utils.JsonUtils;
import de.sovity.edc.utils.catalog.mapper.DspDataOfferBuilder;
import de.sovity.edc.utils.catalog.model.DspCatalog;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.connector.spi.catalog.CatalogService;
import org.eclipse.edc.spi.query.QuerySpec;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class DspCatalogService {
    private final CatalogService catalogService;
    private final DspDataOfferBuilder dspDataOfferBuilder;

    public List<DspCatalog> fetchDataOffers(String endpoint) throws DspCatalogServiceException {
        return fetchDataOffersWithFilters(endpoint, QuerySpec.max());
    }

    /**
     * Contrary to the upstream implementation, this method returns a list of {@link DspCatalog} objects to support the XFSC Federated Catalogue. It can handle an array of catalogs or a single catalog as input.
     */
    public List<DspCatalog> fetchDataOffersWithFilters(String endpoint, QuerySpec querySpec) {
        var catalogsJson = fetchDcatResponse(endpoint, querySpec);
        if(catalogsJson.getValueType() == JsonValue.ValueType.OBJECT) {
            final var endpointUrl = extractEndpoint(catalogsJson.asJsonObject()); // need to get the catalog providers endpoint as the endpoint is the XFSC FC-wrapping EDC's DSP endpoint
            return Collections.singletonList(dspDataOfferBuilder.buildDataOffers(endpointUrl, catalogsJson.asJsonObject()));
        } else if (catalogsJson.getValueType() == JsonValue.ValueType.ARRAY) {
            return catalogsJson.asJsonArray().stream()
                .map(JsonValue::asJsonObject)
                .map(catalogJson -> {
                    var endpointUrl = extractEndpoint(catalogJson);  // need to get the catalog providers endpoint
                    return dspDataOfferBuilder.buildDataOffers(endpointUrl, catalogJson);
                })
                .toList();
        } else {
            throw new DspCatalogServiceException("Unexpected response from catalog service");
        }
    }

    private JsonValue fetchDcatResponse(String connectorEndpoint, QuerySpec querySpec) {
        var raw = fetchDcatRaw(connectorEndpoint, querySpec);
        var string = new String(raw, StandardCharsets.UTF_8);
        return JsonUtils.parseJsonValue(string);
    }

    @SneakyThrows
    private byte[] fetchDcatRaw(String connectorEndpoint, QuerySpec querySpec) {
        return catalogService
                .requestCatalog(connectorEndpoint, "dataspace-protocol-http", querySpec)
                .get(10, TimeUnit.SECONDS)
                .orElseThrow(DspCatalogServiceException::ofFailure);
    }

    private String extractEndpoint(JsonObject catalogJson) {
        return catalogJson.get("dcat:service").asJsonObject().getString("dct:endpointUrl");
    }
}
