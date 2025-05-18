package cool.houge.mahu.codegen;

import io.helidon.common.types.TypeName;

///
/// @author ZY (kzou227@qq.com)
final class TypeNames {

    private TypeNames() {}

    static final TypeName CONFIGURED = TypeName.create("io.helidon.builder.api.Prototype.Configured");
    static final TypeName REFRESHABLE = TypeName.create("cool.houge.mahu.config.annotation.Refreshable");
}
