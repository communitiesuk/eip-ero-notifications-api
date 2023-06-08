package uk.gov.dluhc.notificationsapi.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.format.FormatterRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import uk.gov.dluhc.logging.rest.CorrelationIdMdcInterceptor
import uk.gov.dluhc.notificationsapi.models.SourceType as SourceTypeApi

@Configuration
class WebMvcConfig(private val correlationIdMdcInterceptor: CorrelationIdMdcInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(correlationIdMdcInterceptor)
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(StringToSourceTypeEnumConverter())
    }
}

class StringToSourceTypeEnumConverter : Converter<String?, SourceTypeApi?> {
    override fun convert(source: String): SourceTypeApi {
        return SourceTypeApi.values().find { sourceType -> sourceType.value == source }
            ?: throw IllegalArgumentException("Unknown SourceType value $source")
    }
}
