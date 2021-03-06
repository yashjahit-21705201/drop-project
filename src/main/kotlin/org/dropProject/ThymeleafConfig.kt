/*-
 * ========================LICENSE_START=================================
 * DropProject
 * %%
 * Copyright (C) 2019 Pedro Alves
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package org.dropProject

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect
import org.thymeleaf.spring5.SpringTemplateEngine
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ITemplateResolver

@Configuration
class ThymeleafConfig() {

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    fun templateEngine(appCtx: ApplicationContext): SpringTemplateEngine {
        val engine = SpringTemplateEngine()
        engine.addTemplateResolver(htmlTemplateResolver(appCtx))
        engine.addTemplateResolver(xmlTemplateResolver(appCtx))

        engine.addDialect(SpringSecurityDialect())
        // engine.afterPropertiesSet() TODO: is this needed?
        return engine
    }


    @Bean
    fun xmlTemplateResolver(appCtx: ApplicationContext): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(appCtx)
        templateResolver.prefix = "classpath:/templatesXML/"
        templateResolver.suffix = ".xml"
        templateResolver.templateMode = TemplateMode.XML
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = true
        templateResolver.checkExistence = true  // if it doesn't find the template check the next resolver
        return templateResolver
    }

    @Bean
    fun htmlTemplateResolver(appCtx: ApplicationContext): SpringResourceTemplateResolver {
        val templateResolver = SpringResourceTemplateResolver()
        templateResolver.setApplicationContext(appCtx)
        templateResolver.prefix = "classpath:/templates/"
        templateResolver.suffix = ".html"
        templateResolver.templateMode = TemplateMode.HTML
        templateResolver.characterEncoding = "UTF-8"
        templateResolver.isCacheable = true
        templateResolver.checkExistence = true  // if it doesn't find the template check the next resolver
        return templateResolver
    }
}
