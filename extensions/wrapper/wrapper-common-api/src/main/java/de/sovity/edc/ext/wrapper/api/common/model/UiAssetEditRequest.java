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

package de.sovity.edc.ext.wrapper.api.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Type-safe data offer metadata for editing an asset as supported by the sovity product landscape. Contains extension points.")
public class UiAssetEditRequest {
    @Schema(description = "Data Source", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private UiDataSource dataSourceOverrideOrNull;

    @Schema(description = "Asset Title", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String title;

    @Schema(description = "Asset Language", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String language;

    @Schema(description = "Asset Description", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "Asset Homepage", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String publisherHomepage;

    @Schema(description = "License URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String licenseUrl;

    @Schema(description = "Version", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String version;

    @Schema(description = "Asset Keywords", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> keywords;

    @Schema(description = "Asset MediaType", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String mediaType;

    @Schema(description = "Landing Page URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String landingPageUrl;

    @Schema(description = "Data Category", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataCategory;

    @Schema(description = "Data Subcategory", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataSubcategory;

    @Schema(description = "Data Model", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataModel;

    @Schema(description = "Geo-Reference Method", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String geoReferenceMethod;

    @Schema(description = "Transport Mode", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String transportMode;

    @Schema(description = "The sovereign is distinct from the publisher by being the legal owner of the data.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String sovereignLegalName;

    @Schema(description = "Geo location", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String geoLocation;

    @Schema(description = "Locations by NUTS standard which divides countries into administrative divisions", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> nutsLocations;

    @Schema(description = "Data sample URLs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> dataSampleUrls;

    @Schema(description = "Reference file/schema URLs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private List<String> referenceFileUrls;

    @Schema(description = "Additional information on reference files/schemas", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String referenceFilesDescription;

    @Schema(description = "Instructions for use that are not legally relevant e.g. information on how to cite the dataset in papers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String conditionsForUse;

    @Schema(description = "Data update frequency", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String dataUpdateFrequency;

    @Schema(description = "Temporal coverage start date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate temporalCoverageFrom;

    @Schema(description = "Temporal coverage end date (inclusive)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate temporalCoverageToInclusive;

    // DRK specific
    @Schema(description = "Asset Type", requiredMode = Schema.RequiredMode.REQUIRED)
    private AssetType drkAssetType;

    @Schema(description = "Update Check Frequency (min)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkUpdateCheckFrequency;

    // DRK specific - UC3
    @Schema(description = "Theatre Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkTheatreName;

    @Schema(description = "Theatre Streetaddress", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkTheatreStreetaddress;

    @Schema(description = "Theatre Postal Code", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkTheatrePostalCode;

    @Schema(description = "Theatre Locality", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkTheatreLocality;

    @Schema(description = "Theatre Country", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkTheatreCountry;

    // DRK specific - UC2
    @Schema(description = "Museum Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkMuseumName;

    // DRK specific - UC4
    @Schema(description = "Music School Name", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String drkMusicSchoolName;


    @Schema(description = "Contains serialized custom properties in the JSON format.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customJsonAsString;

    @Schema(description = "Contains serialized custom properties in the JSON LD format. " +
        "Contrary to the customJsonAsString field, this string must represent a JSON LD object " +
        "and will be affected by JSON LD compaction and expansion. " +
        "Due to a technical limitation, the properties can't be booleans.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String customJsonLdAsString;

    @Schema(description = "Same as customJsonAsString but the data will be stored in the private properties.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String privateCustomJsonAsString;

    @Schema(description = "Same as customJsonLdAsString but the data will be stored in the private properties. " +
        "The same limitations apply.",
        requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String privateCustomJsonLdAsString;
}
