/*
 * Copyright 2019 Mahmoud Romeh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.resilience4j.bulkhead.autoconfigure;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.bulkhead.configure.*;
import io.github.resilience4j.bulkhead.configure.threadpool.ThreadPoolBulkheadConfiguration;
import io.github.resilience4j.bulkhead.event.BulkheadEvent;
import io.github.resilience4j.common.CompositeCustomizer;
import io.github.resilience4j.common.bulkhead.configuration.BulkheadConfigCustomizer;
import io.github.resilience4j.common.bulkhead.configuration.ThreadPoolBulkheadConfigCustomizer;
import io.github.resilience4j.common.bulkhead.configuration.ThreadPoolBulkheadConfigurationProperties;
import io.github.resilience4j.consumer.EventConsumerRegistry;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import io.github.resilience4j.fallback.FallbackExecutor;
import io.github.resilience4j.fallback.autoconfigure.FallbackConfigurationOnMissingBean;
import io.github.resilience4j.spelresolver.SpelResolver;
import io.github.resilience4j.spelresolver.autoconfigure.SpelResolverConfigurationOnMissingBean;
import io.github.resilience4j.utils.AspectJOnClasspathCondition;
import io.github.resilience4j.utils.ReactorOnClasspathCondition;
import io.github.resilience4j.utils.RxJava2OnClasspathCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * {@link Configuration Configuration} for resilience4j-bulkhead.
 */
@Configuration
@Import({FallbackConfigurationOnMissingBean.class, SpelResolverConfigurationOnMissingBean.class})
public abstract class AbstractBulkheadConfigurationOnMissingBean {

    protected final BulkheadConfiguration bulkheadConfiguration;
    protected final ThreadPoolBulkheadConfiguration threadPoolBulkheadConfiguration;

    public AbstractBulkheadConfigurationOnMissingBean() {
        this.threadPoolBulkheadConfiguration = new ThreadPoolBulkheadConfiguration();
        this.bulkheadConfiguration = new BulkheadConfiguration();
    }


    @Bean
    @ConditionalOnMissingBean(name = "compositeBulkheadCustomizer")
    @Qualifier("compositeBulkheadCustomizer")
    public CompositeCustomizer<BulkheadConfigCustomizer> compositeBulkheadCustomizer(
        @Autowired(required = false) List<BulkheadConfigCustomizer> customizers) {
        return new CompositeCustomizer<>(customizers);
    }

    @Bean
    @ConditionalOnMissingBean
    public BulkheadRegistry bulkheadRegistry(
        BulkheadConfigurationProperties bulkheadConfigurationProperties,
        EventConsumerRegistry<BulkheadEvent> bulkheadEventConsumerRegistry,
        RegistryEventConsumer<Bulkhead> bulkheadRegistryEventConsumer,
        @Qualifier("compositeBulkheadCustomizer") CompositeCustomizer<BulkheadConfigCustomizer> compositeBulkheadCustomizer) {
        return bulkheadConfiguration
            .bulkheadRegistry(bulkheadConfigurationProperties, bulkheadEventConsumerRegistry,
                bulkheadRegistryEventConsumer, compositeBulkheadCustomizer);
    }

    @Bean
    @Primary
    public RegistryEventConsumer<Bulkhead> bulkheadRegistryEventConsumer(
        Optional<List<RegistryEventConsumer<Bulkhead>>> optionalRegistryEventConsumers) {
        return bulkheadConfiguration.bulkheadRegistryEventConsumer(optionalRegistryEventConsumers);
    }

    @Bean
    @Conditional(value = {AspectJOnClasspathCondition.class})
    @ConditionalOnMissingBean
    public BulkheadAspect bulkheadAspect(
        BulkheadConfigurationProperties bulkheadConfigurationProperties,
        ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
        BulkheadRegistry bulkheadRegistry,
        @Autowired(required = false) List<BulkheadAspectExt> bulkHeadAspectExtList,
        FallbackExecutor fallbackExecutor,
        SpelResolver spelResolver) {
        return bulkheadConfiguration
            .bulkheadAspect(bulkheadConfigurationProperties, threadPoolBulkheadRegistry,
                bulkheadRegistry, bulkHeadAspectExtList, fallbackExecutor, spelResolver);
    }

    @Bean
    @Conditional(value = {RxJava2OnClasspathCondition.class, AspectJOnClasspathCondition.class})
    @ConditionalOnMissingBean
    public RxJava2BulkheadAspectExt rxJava2BulkHeadAspectExt() {
        return bulkheadConfiguration.rxJava2BulkHeadAspectExt();
    }

    @Bean
    @Conditional(value = {ReactorOnClasspathCondition.class, AspectJOnClasspathCondition.class})
    @ConditionalOnMissingBean
    public ReactorBulkheadAspectExt reactorBulkHeadAspectExt() {
        return bulkheadConfiguration.reactorBulkHeadAspectExt();
    }


    @Bean
    @ConditionalOnMissingBean(name = "compositeThreadPoolBulkheadCustomizer")
    @Qualifier("compositeThreadPoolBulkheadCustomizer")
    public CompositeCustomizer<ThreadPoolBulkheadConfigCustomizer> compositeThreadPoolBulkheadCustomizer(
        @Autowired(required = false) List<ThreadPoolBulkheadConfigCustomizer> customizers) {
        return new CompositeCustomizer<>(customizers);
    }


    @Bean
    @ConditionalOnMissingBean
    public ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry(
        ThreadPoolBulkheadConfigurationProperties threadPoolBulkheadConfigurationProperties,
        EventConsumerRegistry<BulkheadEvent> bulkheadEventConsumerRegistry,
        RegistryEventConsumer<ThreadPoolBulkhead> threadPoolBulkheadRegistryEventConsumer,
        @Qualifier("compositeThreadPoolBulkheadCustomizer") CompositeCustomizer<ThreadPoolBulkheadConfigCustomizer> compositeThreadPoolBulkheadCustomizer) {
        return threadPoolBulkheadConfiguration.threadPoolBulkheadRegistry(
            threadPoolBulkheadConfigurationProperties, bulkheadEventConsumerRegistry,
            threadPoolBulkheadRegistryEventConsumer, compositeThreadPoolBulkheadCustomizer);
    }

    @Bean
    @Primary
    public RegistryEventConsumer<ThreadPoolBulkhead> threadPoolBulkheadRegistryEventConsumer(
        Optional<List<RegistryEventConsumer<ThreadPoolBulkhead>>> optionalRegistryEventConsumers) {
        return threadPoolBulkheadConfiguration
            .threadPoolBulkheadRegistryEventConsumer(optionalRegistryEventConsumers);
    }

}
