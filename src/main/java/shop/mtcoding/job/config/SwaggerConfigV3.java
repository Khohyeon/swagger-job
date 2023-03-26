package shop.mtcoding.job.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfigV3 {

    private final String nullSortValue = StringUtils.stripAccents("100000000000000000000");
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("My App").version("1.0.0"))
                // Components section defines Security Scheme "mySecretHeader"
                .components(new Components()
                        .addSecuritySchemes("Authorization", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .name("Authentication")))
                // AddSecurityItem section applies created scheme globally
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }


    @Bean
    public OpenApiCustomiser sortTagsAlphabetically() {
        return openApi -> {
            List<Tag> tags = openApi.getTags()
                    .stream()
                    .sorted(Comparator.comparing(tag -> StringUtils.stripAccents(tag.getName())))
                    .collect(Collectors.toList());

            Paths sortedPaths = new Paths();

            Map<String, PathItem> paths = openApi.getPaths();
            List<Map.Entry<String, PathItem>> entryList = new LinkedList<>(paths.entrySet());
            List<Map.Entry<String, PathItem>> sortList = entryList
                    .stream()
                    .sorted(
                        Comparator.comparing(
                                map -> {
                                    PathItem pathItem = map.getValue();
                                    if (pathItem.getGet() != null) {
                                        if (pathItem.getGet().getSummary() == null) {
                                            return nullSortValue;
                                        }
                                        return StringUtils.stripAccents(pathItem.getGet().getSummary());
                                    } else if (pathItem.getPost() != null){
                                        if (pathItem.getPost().getSummary() == null) {
                                            return nullSortValue;
                                        }
                                        return StringUtils.stripAccents(pathItem.getPost().getSummary());

                                    } else if (pathItem.getPut() != null){
                                        if (pathItem.getPut().getSummary() == null) {
                                            return nullSortValue;
                                        }
                                        return StringUtils.stripAccents(pathItem.getPut().getSummary());
                                    } else if (pathItem.getDelete() != null){
                                        if (pathItem.getDelete().getSummary() == null) {
                                            return nullSortValue;
                                        }
                                        return StringUtils.stripAccents(pathItem.getDelete().getSummary());
                                    } else {
                                        return nullSortValue;
                                    }
                                }
                        )
                    )
                    .collect(Collectors.toList());

            LinkedHashMap<String, PathItem> sortPathMap = new LinkedHashMap<>();
            for (Map.Entry<String, PathItem> entry : sortList) {
                sortPathMap.put(entry.getKey(), entry.getValue());
            }

            sortedPaths.putAll(sortPathMap);
            openApi.setPaths(sortedPaths);
            openApi.setTags(tags);
        };
    }
}
