package me.didk.common.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
        @Parameter(
                in = ParameterIn.QUERY,
                name = "page",
                description = "One-based page index (1..N)",
                schema = @Schema(type = "integer", defaultValue = "1", minimum = "1")
        ),
        @Parameter(
                in = ParameterIn.QUERY,
                name = "pageSize",
                description = "The size of the page to be returned",
                schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
        ),
        @Parameter(
                in = ParameterIn.QUERY,
                name = "sort",
                description = "Sorting criteria in the format: property,(asc|desc). Multiple sort criteria are supported.",
                array = @ArraySchema(schema = @Schema(type = "string")),
                example = "createdAt,desc"
        )
})
public @interface OneBasedPageableAsQueryParam {
}
