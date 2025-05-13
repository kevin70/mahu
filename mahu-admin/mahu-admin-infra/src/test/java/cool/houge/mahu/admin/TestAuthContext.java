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

import cool.houge.mahu.admin.security.AuthContext;

/// 集成测试默认认证上下文
///
/// @author ZY (kzou227@qq.com)
public class TestAuthContext implements AuthContext {

    /// 默认集成测试默认认证上下文
    public static final AuthContext DEFAULT = new TestAuthContext(-1, "INTEGRATION-TEST");

    private final int uid;
    private final String name;

    public TestAuthContext(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    @Override
    public long uid() {
        return uid;
    }

    @Override
    public String name() {
        return name;
    }
}
