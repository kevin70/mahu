package cool.houge.mahu.codegen;

import io.helidon.codegen.CodegenContext;
import io.helidon.codegen.spi.CodegenExtension;
import io.helidon.codegen.spi.CodegenExtensionProvider;
import io.helidon.common.types.TypeName;
import java.util.Set;

///
/// @author ZY (kzou227@qq.com)
public class RefreshableCodegenProvider implements CodegenExtensionProvider {

    @Override
    public CodegenExtension create(CodegenContext ctx, TypeName generatorType) {
        return new RefreshableCodegen(ctx);
    }

    @Override
    public Set<TypeName> supportedAnnotations() {
        return Set.of(TypeNames.REFRESHABLE);
    }
}
