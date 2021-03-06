/*
 * Copyright 2019-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.vividus.proxy.mitm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.browserup.bup.mitm.manager.ImpersonatingMitmManager;

import org.junit.jupiter.api.Test;
import org.littleshoot.proxy.MitmManager;
import org.vividus.http.keystore.KeyStoreOptions;

class MitmManagerFactoryTests
{
    private IMitmManagerFactory factory = new MitmManagerFactory();

    @Test
    void testCreateMitmManager()
    {
        MitmManagerOptions options = new MitmManagerOptions("alias", true,
                new KeyStoreOptions("bundle.p12", "password", "PKCS12"));
        MitmManager mitmManager = factory.createMitmManager(options);
        assertThat(mitmManager, instanceOf(ImpersonatingMitmManager.class));
    }

    @Test
    void testCreateMitmManagerWithInvalidParameters()
    {
        MitmManagerOptions options = new MitmManagerOptions(null, true,
                new KeyStoreOptions(null, null, null));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> factory.createMitmManager(options));
        assertEquals("key store path parameter must be set", exception.getMessage());
    }
}
