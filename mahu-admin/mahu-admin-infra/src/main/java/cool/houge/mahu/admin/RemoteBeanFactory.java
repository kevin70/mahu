/*
 * (c) Copyright 2025 Palantir Technologies Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cool.houge.mahu.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import cool.houge.mahu.remote.aliyun.AliyunClient;
import io.avaje.inject.Bean;
import io.avaje.inject.Factory;
import io.helidon.webclient.api.WebClient;

/// 第三方对象声明工厂
///
/// @author ZY (kzou227@qq.com)
@Factory
public class RemoteBeanFactory {

    @Bean
    public AliyunClient aliyunClient(WebClient webClient, ObjectMapper objectMapper) {
        return new AliyunClient(webClient, objectMapper);
    }
}
