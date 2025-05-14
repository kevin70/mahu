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

package cool.houge.mahu.admin.shared;

import static java.util.Objects.requireNonNull;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cool.houge.mahu.admin.repository.LogRepository;
import cool.houge.mahu.entity.log.BaseBizLog;
import io.ebean.annotation.Transactional;
import io.hypersistence.tsid.TSID;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/// 共享的服务
///
/// @author ZY (kzou227@qq.com)
@Singleton
public class SharedService {

    private static final Logger log = LogManager.getLogger(SharedService.class);

    private static final Cache<String, TSID.Factory> TSID_FACTORY_CACHE =
            Caffeine.newBuilder().maximumSize(256).build();

    private final LogRepository logRepository;

    @Inject
    public SharedService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    /// 保存业务日志
    @Transactional
    public void save(BaseBizLog bizLog) {
        requireNonNull(bizLog);
        if (bizLog.getId() == null) {
            bizLog.setId(takeBizLogId(bizLog));
        }

        logRepository.save(bizLog);
        log.debug("保存业务日志 {}", bizLog);
    }

    Long takeBizLogId(BaseBizLog bizLog) {
        return TSID_FACTORY_CACHE
                .get(bizLog.getClass().getName(), _ -> TSID.Factory.builder()
                        .build())
                .generate()
                .toLong();
    }
}
