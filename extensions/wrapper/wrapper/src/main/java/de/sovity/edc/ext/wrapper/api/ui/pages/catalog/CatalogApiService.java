/*
 *  Copyright (c) 2022 sovity GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       sovity GmbH - initial API and implementation
 *
 */

package de.sovity.edc.ext.wrapper.api.ui.pages.catalog;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.QuerySpecDto;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterion;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteral;
import de.sovity.edc.ext.wrapper.api.ui.model.UiCriterionLiteralType;
import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import de.sovity.edc.utils.catalog.DspCatalogService;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.iam.TokenDecorator;
import org.eclipse.edc.spi.iam.TokenParameters;
import org.eclipse.edc.spi.query.Criterion;
import org.eclipse.edc.spi.query.QuerySpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.ConfigPropertyUtils.configKey;

@RequiredArgsConstructor
public class CatalogApiService {
    private final UiDataOfferBuilder uiDataOfferBuilder;
    private final DspCatalogService dspCatalogService;


    private final IdentityService identityService;
    private final TokenDecorator tokenDecorator;
    private final ObjectMapper objectMapper;
    private final String connectorsHost;
    private static final String EDC_OAUTH_PROVIDER_AUDIENCE = configKey("EDC_OAUTH_PROVIDER_AUDIENCE");


    public List<UiDataOffer> fetchDataOffersBatched() {
        var client = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS).build();
        var tokenParameters = tokenDecorator.decorate(TokenParameters.Builder.newInstance().audience(EDC_OAUTH_PROVIDER_AUDIENCE)).build();
        var token = identityService.obtainClientCredentials(tokenParameters);
        var req = new Request.Builder()
                .url(connectorsHost)
                .header("Authorization", "Bearer " + token.getContent().getToken())
                .build();
        List<String> urls;
        try(var response = client.newCall(req).execute()) {
            if(response.body() == null) return Collections.emptyList();
            urls = Arrays.asList(objectMapper.readValue(response.body().string(), String[].class));
        } catch (IOException | NullPointerException e) {
            System.out.println("failed to get client list from DAPS");
            return Collections.emptyList();
        }
        return urls.parallelStream().map(url -> {
            try {
                return fetchDataOffers(url, null);
            } catch (Exception e) {
                return Collections.<UiDataOffer>emptyList();
            }
        }).flatMap(List::stream).toList();
    }

    public List<UiDataOffer> fetchDataOffers(String connectorEndpoint, QuerySpecDto querySpecDto) {
        if(connectorEndpoint.equals("batched")) {
            return fetchDataOffersBatched();  // query specs not supported here
        } else {
            var querySpec = querySpecDto != null ? querySpecDtoToQuerySpec(querySpecDto) : QuerySpec.max();
            var dspCatalog = dspCatalogService.fetchDataOffersWithFilters(connectorEndpoint, querySpec);
            return uiDataOfferBuilder.buildUiDataOffers(dspCatalog);
        }
    }

    private QuerySpec querySpecDtoToQuerySpec(QuerySpecDto querySpecDto) {
        var mappedCriteria = querySpecDto.getCriteria().stream()
            .map(crit -> {
                var operandRight = crit.getOperandRight().getType() == UiCriterionLiteralType.VALUE ? crit.getOperandRight().getValue() : crit.getOperandRight().getValueList();
                return new Criterion(crit.getOperandLeft(), crit.getOperator().name(), operandRight);
            }).toList();
        return QuerySpec.Builder.newInstance()
            .filter(mappedCriteria)
            .build();
    }
}
