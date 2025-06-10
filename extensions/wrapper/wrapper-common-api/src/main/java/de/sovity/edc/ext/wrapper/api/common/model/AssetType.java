package de.sovity.edc.ext.wrapper.api.common.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
    description = "Differentiate different types of assets for DRK and in general.",
    enumAsRef = true
)
public enum AssetType {
    GENERAL,
    THEATRE_PLAN,
    MUSEUM,
    MUSICSCHOOL
}
