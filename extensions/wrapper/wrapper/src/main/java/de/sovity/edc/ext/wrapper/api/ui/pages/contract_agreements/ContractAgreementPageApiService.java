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

package de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementData;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementDataFetcher;
import de.sovity.edc.ext.wrapper.api.ui.pages.contract_agreements.services.ContractAgreementPageCardBuilder;
import lombok.RequiredArgsConstructor;
import lombok.val;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class ContractAgreementPageApiService {
    private final ContractAgreementDataFetcher contractAgreementDataFetcher;
    private final ContractAgreementPageCardBuilder contractAgreementPageCardBuilder;
    private final String managementApiKey;
    private final ConcurrentMap<String, Asset> remoteAssetCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Date> remoteAssetCacheExpiry = new ConcurrentHashMap<>();

    private Map<String,Object> fetchAssetInfo(String agreementId) {
        var client = new OkHttpClient.Builder().connectTimeout(2, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).build();
        var req = new Request.Builder()
            .url("http://edc:11002/api/management/dynamicassetmetadata/forContractId")
            .post(RequestBody.create(
                MediaType.parse("text/plain"),
                agreementId
            ))
            .header("X-Api-Key", managementApiKey)
            .build();
        try(var response = client.newCall(req).execute()) {
            if(response.body() == null) return Collections.emptyMap();
            TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};
            return new ObjectMapper().readValue(response.body().string(), typeRef);
        } catch (IOException | NullPointerException e) {
            System.out.println("failed to get asset metadata for agreement " + agreementId);
            return Collections.emptyMap();
        }
    }

    private Asset getAssetWithProps(ContractAgreementData agreementData) {
        if(agreementData.asset().getProperties().size() == 1) {
            // remote asset, we consume this agreement, so fetch information about the asset, asynchronously, reloaded by the frontend all the time anyway
            final Date cacheEntryExpiry = remoteAssetCacheExpiry.getOrDefault(agreementData.agreement().getId(), null);
            if(cacheEntryExpiry == null || cacheEntryExpiry.before(new Date())) {
                remoteAssetCacheExpiry.put(agreementData.agreement().getId(), new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1))); // temporarily delay expiry to prevent multiple requests
                new Thread(() -> {
                    Map<String, Object> props = fetchAssetInfo(agreementData.agreement().getId());
                    if(!props.isEmpty()) {
                        Asset asset = agreementData.asset();
                        asset.getProperties().clear();
                        asset.getProperties().putAll(props);
                        remoteAssetCache.put(agreementData.agreement().getId(), asset);
                        remoteAssetCacheExpiry.put(agreementData.agreement().getId(), new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)));
                    }
                }).start();
            }
        }
        return remoteAssetCache.getOrDefault(agreementData.agreement().getId(), agreementData.asset()); // might return old data once if expired
    }

    @NotNull
    public ContractAgreementPage contractAgreementPage(DSLContext dsl, @Nullable ContractAgreementPageQuery contractAgreementPageQuery) {
        var agreements = contractAgreementDataFetcher.getContractAgreements(dsl);
        var cards = agreements.stream()
            .map(agreement -> contractAgreementPageCardBuilder.buildContractAgreementCard(
                agreement.agreement(),
                agreement.negotiation(),
                getAssetWithProps(agreement),
                agreement.transfers(),
                agreement.termination()))
            .sorted(Comparator.comparing(ContractAgreementCard::getContractSigningDate).reversed());

        if (contractAgreementPageQuery == null || contractAgreementPageQuery.getTerminationStatus() == null) {
            return new ContractAgreementPage(cards.toList());
        } else {
            var filtered = cards.filter(card ->
                    card.getTerminationStatus().equals(contractAgreementPageQuery.getTerminationStatus()))
                .toList();
            return new ContractAgreementPage(filtered);
        }
    }

    public ContractAgreementCard contractAgreement(DSLContext dsl, String contractAgreementId) {
        val agreementData = contractAgreementDataFetcher.getContractAgreement(dsl, contractAgreementId);
        return contractAgreementPageCardBuilder.buildContractAgreementCard(
            agreementData.agreement(),
            agreementData.negotiation(),
            getAssetWithProps(agreementData),
            agreementData.transfers(),
            agreementData.termination()
        );
    }
}
