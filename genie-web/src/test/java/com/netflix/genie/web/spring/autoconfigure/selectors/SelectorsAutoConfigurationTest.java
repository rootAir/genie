/*
 *
 *  Copyright 2020 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.web.spring.autoconfigure.selectors;

import com.netflix.genie.web.scripts.ClusterSelectorScript;
import com.netflix.genie.web.scripts.CommandSelectorManagedScript;
import com.netflix.genie.web.selectors.ClusterSelector;
import com.netflix.genie.web.selectors.CommandSelector;
import com.netflix.genie.web.selectors.impl.RandomClusterSelectorImpl;
import com.netflix.genie.web.selectors.impl.RandomCommandSelectorImpl;
import com.netflix.genie.web.selectors.impl.ScriptClusterSelectorImpl;
import com.netflix.genie.web.selectors.impl.ScriptCommandSelectorImpl;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * Tests for {@link SelectorsAutoConfiguration}.
 *
 * @author tgianos
 * @since 4.0.0
 */
class SelectorsAutoConfigurationTest {

    private ApplicationContextRunner contextRunner =
        new ApplicationContextRunner()
            .withConfiguration(
                AutoConfigurations.of(
                    SelectorsAutoConfiguration.class
                )
            )
            .withUserConfiguration(RegistryConfig.class);

    @Test
    void canCreateDefaultBeans() {
        Assertions
            .assertThat(SelectorsAutoConfiguration.SCRIPT_CLUSTER_SELECTOR_PRECEDENCE)
            .isEqualTo(Ordered.LOWEST_PRECEDENCE - 50);

        this.contextRunner.run(
            context -> {
                Assertions.assertThat(context).hasSingleBean(ClusterSelector.class);
                Assertions.assertThat(context).hasSingleBean(RandomClusterSelectorImpl.class);
                Assertions.assertThat(context).doesNotHaveBean(ScriptClusterSelectorImpl.class);
                Assertions.assertThat(context).hasSingleBean(CommandSelector.class);
                Assertions.assertThat(context).hasSingleBean(RandomCommandSelectorImpl.class);
                Assertions.assertThat(context).doesNotHaveBean(ScriptCommandSelectorImpl.class);
            }
        );
    }

    @Test
    void canCreateConditionalBeans() {
        this.contextRunner
            .withUserConfiguration(ScriptsConfig.class)
            .run(
                context -> {
                    Assertions
                        .assertThat(context)
                        .hasSingleBean(RandomClusterSelectorImpl.class)
                        .hasSingleBean(ScriptClusterSelectorImpl.class)
                        .getBeans(ClusterSelector.class)
                        .hasSize(2); // No real easy way to test the order

                    Assertions.assertThat(context).hasSingleBean(CommandSelector.class);
                    Assertions.assertThat(context).doesNotHaveBean(RandomCommandSelectorImpl.class);
                    Assertions.assertThat(context).hasSingleBean(ScriptCommandSelectorImpl.class);
                }
            );
    }

    @Test
    void commandSelectorOverrideProvided() {
        this.contextRunner
            .withUserConfiguration(ScriptsConfig.class, UserCommandSelectorConfig.class)
            .run(
                context -> {
                    Assertions
                        .assertThat(context)
                        .hasSingleBean(RandomClusterSelectorImpl.class)
                        .hasSingleBean(ScriptClusterSelectorImpl.class)
                        .getBeans(ClusterSelector.class)
                        .hasSize(2); // No real easy way to test the order

                    Assertions.assertThat(context).hasSingleBean(CommandSelector.class);
                    Assertions.assertThat(context).doesNotHaveBean(RandomCommandSelectorImpl.class);
                    Assertions.assertThat(context).doesNotHaveBean(ScriptCommandSelectorImpl.class);
                    Assertions.assertThat(context).hasBean("customCommandSelector");
                }
            );
    }

    /**
     * Dummy config to create a {@link MeterRegistry} instance.
     */
    private static class RegistryConfig {

        /**
         * Dummy meter registry.
         *
         * @return {@link SimpleMeterRegistry} instance
         */
        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    /**
     * Dummy scripts configuration for tests.
     */
    private static class ScriptsConfig {

        /**
         * Dummy script based cluster selector.
         */
        @Bean
        public ClusterSelectorScript clusterSelectorScript() {
            return Mockito.mock(ClusterSelectorScript.class);
        }

        /**
         * Dummy script based command selector.
         */
        @Bean
        public CommandSelectorManagedScript commandSelectorManagedScript() {
            return Mockito.mock(CommandSelectorManagedScript.class);
        }
    }

    /**
     * User provided command selector configuration.
     */
    private static class UserCommandSelectorConfig {

        @Bean
        public CommandSelector customCommandSelector() {
            return Mockito.mock(CommandSelector.class);
        }
    }
}
