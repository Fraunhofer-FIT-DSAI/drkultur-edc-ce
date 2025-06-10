package de.sovity.edc.ext.wrapper.api.ui.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Query Specification coming from the UI")
public class QuerySpecDto {
    @Schema(description = "Criteria Specification", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<UiCriterion> criteria;  // use UiCriterion from policies for now

}
