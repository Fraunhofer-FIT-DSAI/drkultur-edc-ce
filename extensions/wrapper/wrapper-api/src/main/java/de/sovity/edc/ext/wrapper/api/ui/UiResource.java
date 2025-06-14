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

package de.sovity.edc.ext.wrapper.api.ui;

import de.sovity.edc.ext.wrapper.api.common.model.UiAsset;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetCreateRequest;
import de.sovity.edc.ext.wrapper.api.common.model.UiAssetEditRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.AssetPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementCard;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractAgreementPageQuery;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionPage;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractDefinitionRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractNegotiationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.ContractTerminationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.DashboardPage;
import de.sovity.edc.ext.wrapper.api.ui.model.DataOfferCreationRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.IdAvailabilityResponse;
import de.sovity.edc.ext.wrapper.api.ui.model.IdResponseDto;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateCustomTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.InitiateTransferRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDefinitionCreateDto;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDefinitionCreateRequest;
import de.sovity.edc.ext.wrapper.api.ui.model.PolicyDefinitionPage;
import de.sovity.edc.ext.wrapper.api.ui.model.QuerySpecDto;
import de.sovity.edc.ext.wrapper.api.ui.model.TransferHistoryPage;
import de.sovity.edc.ext.wrapper.api.ui.model.UiContractNegotiation;
import de.sovity.edc.ext.wrapper.api.ui.model.UiDataOffer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Path("wrapper/ui")
@Tag(name = "UI", description = "EDC UI API Endpoints")
interface UiResource {

    @GET
    @Path("pages/dashboard-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for the Dashboard Page")
    DashboardPage getDashboardPage();

    @GET
    @Path("pages/asset-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Asset Page")
    AssetPage getAssetPage();

    @POST
    @Path("pages/asset-page/assets")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create a new Asset")
    IdResponseDto createAsset(UiAssetCreateRequest uiAssetCreateRequest);

    @PUT
    @Path("pages/asset-page/assets/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Updates an Asset's metadata and optionally also the data source.")
    IdResponseDto editAsset(@PathParam("assetId") String assetId, UiAssetEditRequest uiAssetEditRequest);

    @DELETE
    @Path("pages/asset-page/assets/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete an Asset")
    IdResponseDto deleteAsset(@PathParam("assetId") String assetId);

    @GET
    @Path("pages/asset-page/bscw/{oid}")
    @Produces(MediaType.APPLICATION_JSON)
    @HeaderParam("Authorization")
    @Operation(description = "Fetches metadata for a BSCW OID")
    String getBscwMetadataForOID(@PathParam("oid") int oid, @HeaderParam("Authorization") String authorization);

    @GET
    @Path("pages/policy-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Policy Definition Page")
    PolicyDefinitionPage getPolicyDefinitionPage();

    @POST
    @Path("pages/policy-page/policy-definitions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    @Operation(description = "[Deprecated] Create a new Policy Definition from a list of constraints. " +
        "Use createPolicyDefinitionV2 instead.", deprecated = true)
    IdResponseDto createPolicyDefinition(PolicyDefinitionCreateRequest policyDefinitionDtoDto);

    @POST
    @Path("v2/pages/policy-page/policy-definitions")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Create a new Policy Definition")
    IdResponseDto createPolicyDefinitionV2(PolicyDefinitionCreateDto policyDefinitionCreateDto);

    @DELETE
    @Path("pages/policy-page/policy-definitions/{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete a Policy Definition")
    IdResponseDto deletePolicyDefinition(@PathParam("policyId") String policyId);

    @GET
    @Path("pages/contract-definition-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for Contract Definition Page")
    ContractDefinitionPage getContractDefinitionPage();

    @POST
    @Path("pages/contract-definition-page/contract-definitions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create a new Contract Definition")
    IdResponseDto createContractDefinition(ContractDefinitionRequest contractDefinitionRequest);

    @DELETE
    @Path("pages/contract-definition-page/contract-definitions/{contractDefinitionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Delete a Contract Definition")
    IdResponseDto deleteContractDefinition(@PathParam("contractDefinitionId") String contractDefinitionId);

    @POST
    @Path("pages/create-data-offer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Create a new asset, contract definition and optional policies. Uses the same id for the asset, the contract policy, the access policy and the contract definition")
    IdResponseDto createDataOffer(DataOfferCreationRequest dataOfferCreationRequest);

    @POST
    @Path("pages/catalog-page/data-offers")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch a connector's data offers")
    List<UiDataOffer> getCatalogPageDataOffers(@QueryParam("connectorEndpoint") String connectorEndpoint, @Nullable QuerySpecDto querySpecDto);

    @GET
    @Path("pages/catalog-page/contract-asset-ids")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Fetch Asset IDs for Consuming Contracts")
    List<String> getCatalogPageContractedAssetIds();

    @POST
    @Path("pages/catalog-page/contract-negotiations")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Initiate a new Contract Negotiation")
    UiContractNegotiation initiateContractNegotiation(ContractNegotiationRequest contractNegotiationRequest);

    @GET
    @Path("pages/catalog-page/contract-negotiations/{contractNegotiationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get Contract Negotiation Information")
    UiContractNegotiation getContractNegotiation(@PathParam("contractNegotiationId") String contractNegotiationId);

    @POST
    @Path("pages/contract-agreement-page")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect filtered data for the Contract Agreement Page")
    ContractAgreementPage getContractAgreementPage(
        @RequestBody(description = "If null, returns all the contract agreements.")
        @Nullable ContractAgreementPageQuery contractAgreementPageQuery
    );

    @GET
    @Path("pages/contract-agreement-page/{contractAgreementId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Get a single contract agreement card by its identifier")
    ContractAgreementCard getContractAgreementCard(@PathParam("contractAgreementId") String contractAgreementId);

    @POST
    @Path("pages/contract-agreement-page/transfers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Initiate a Transfer Process")
    IdResponseDto initiateTransfer(InitiateTransferRequest initiateTransferRequest);

    @POST
    @Path("pages/contract-agreement-page/transfers/custom")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Initiate a Transfer Process via a custom Transfer Process JSON-LD. Fields such as connectorId, assetId, providerConnectorId, providerConnectorAddress will be set automatically.")
    IdResponseDto initiateCustomTransfer(InitiateCustomTransferRequest initiateCustomTransferRequest);

    @POST
    @Path("pages/content-agreement-page/{contractAgreementId}/terminate")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Terminates a contract agreement designated by its contract agreement id.")
    IdResponseDto terminateContractAgreement(
        @PathParam("contractAgreementId") String contractAgreementId,
        ContractTerminationRequest contractTerminationRequest);

    @GET
    @Path("pages/transfer-history-page")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Collect all data for the Transfer History Page")
    TransferHistoryPage getTransferHistoryPage();

    @GET
    @Path("pages/transfer-history-page/transfer-processes/{transferProcessId}/asset")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Queries a transfer process' asset")
    UiAsset getTransferProcessAsset(@PathParam("transferProcessId") String transferProcessId);

    @GET
    @Path("pages/data-offer-page/validate-policy-id/{policyId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Validates if the provided policyId is already taken")
    IdAvailabilityResponse isPolicyIdAvailable(@PathParam("policyId") String policyId);

    @GET
    @Path("pages/data-offer-page/validate-asset-id/{assetId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Validates if the provided assetId is already taken")
    IdAvailabilityResponse isAssetIdAvailable(@PathParam("assetId") String assetId);

    @GET
    @Path("pages/data-offer-page/validate-contract-definition-id/{contractDefinitionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(description = "Validates if the provided contractDefinitionId is already taken")
    IdAvailabilityResponse isContractDefinitionIdAvailable(@PathParam("contractDefinitionId") String contractDefinitionId);
}
