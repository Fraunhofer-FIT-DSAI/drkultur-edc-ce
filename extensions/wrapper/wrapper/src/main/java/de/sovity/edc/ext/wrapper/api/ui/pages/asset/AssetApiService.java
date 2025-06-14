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

package de.sovity.edc.ext.wrapper.api.ui.pages.asset;

import de.sovity.edc.ext.db.jooq.Tables;
import de.sovity.edc.ext.db.jooq.tables.EdcAsset;
import de.sovity.edc.ext.wrapper.api.ServiceException;
import de.sovity.edc.ext.wrapper.api.common.mappers.AssetMapper;
import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.pages.dashboard.services.SelfDescriptionService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.edc.connector.spi.asset.AssetService;
import org.eclipse.edc.spi.query.QuerySpec;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class AssetApiService {
    private final AssetService assetService;
    private final AssetMapper assetMapper;
    private final AssetIdValidator assetIdValidator;
    private final SelfDescriptionService selfDescriptionService;

    public List<UiAsset> getAssets() {
        var assets = getAllAssets();
        var connectorEndpoint = selfDescriptionService.getConnectorEndpoint();
        var participantId = selfDescriptionService.getParticipantId();
        return assets.stream().sorted(Comparator.comparing(Asset::getCreatedAt).reversed())
            .map(asset -> assetMapper.buildUiAsset(asset, connectorEndpoint, participantId))
            .toList();
    }

    @NotNull
    public IdResponseDto createAsset(UiAssetCreateRequest request) {
        assetIdValidator.assertValid(request.getId());
        var organizationName = selfDescriptionService.getCuratorName();
        val asset = assetMapper.buildAsset(request, organizationName);
        val createdAsset = assetService.create(asset).orElseThrow(ServiceException::new);
        return new IdResponseDto(createdAsset.getId());
    }

    public IdResponseDto editAsset(String assetId, UiAssetEditRequest request) {
        val foundAsset = assetService.findById(assetId);
        Objects.requireNonNull(foundAsset, "Asset with ID %s not found".formatted(assetId));
        val editedAsset = assetMapper.editAsset(foundAsset, request);
        val updatedAsset = assetService.update(editedAsset).orElseThrow(ServiceException::new);
        assetService.update(editedAsset.getId(), editedAsset.getDataAddress());
        return new IdResponseDto(updatedAsset.getId());
    }

    @NotNull
    public IdResponseDto deleteAsset(String assetId) {
        var response = assetService.delete(assetId).orElseThrow(ServiceException::new);
        return new IdResponseDto(response.getId());
    }

    private List<Asset> getAllAssets() {
        return assetService.query(QuerySpec.max()).orElseThrow(ServiceException::new).toList();
    }

    public boolean assetExists(DSLContext dsl, String assetId) {
        val a = Tables.EDC_ASSET;
        return dsl.selectCount()
            .from(a)
            .where(a.ASSET_ID.eq(assetId))
            .fetchSingleInto(Integer.class) > 0;
    }
}
