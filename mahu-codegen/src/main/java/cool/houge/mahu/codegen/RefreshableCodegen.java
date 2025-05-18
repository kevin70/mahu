package cool.houge.mahu.codegen;

import io.helidon.codegen.CodegenContext;
import io.helidon.codegen.CodegenUtil;
import io.helidon.codegen.RoundContext;
import io.helidon.codegen.classmodel.*;
import io.helidon.codegen.spi.CodegenExtension;
import io.helidon.common.types.AccessModifier;
import io.helidon.common.types.ElementKind;
import io.helidon.common.types.TypeInfo;
import io.helidon.common.types.TypeName;

import java.time.LocalDateTime;

///
/// @author ZY (kzou227@qq.com)
class RefreshableCodegen implements CodegenExtension {

    private static final TypeName GENERATOR = TypeName.create(RefreshableCodegen.class);

    private final CodegenContext ctx;

    RefreshableCodegen(CodegenContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void process(RoundContext roundContext) {
        var blueprintInterfaces = roundContext.annotatedTypes(TypeNames.REFRESHABLE).stream()
                .filter(it -> it.kind() == ElementKind.INTERFACE)
                .toList();
        for (TypeInfo blueprintInterface : blueprintInterfaces) {
            process(roundContext, blueprintInterface);
        }
    }

    @Override
    public void processingOver(RoundContext roundContext) {
        process(roundContext);
    }

    private void process(RoundContext roundContext, TypeInfo blueprint) {
        var refreshClassName = className(blueprint);
        var refreshTypeName = TypeName.create(blueprint.typeName().packageName() + "." + refreshClassName);
        var configTypeName = configTypeName(blueprint);

        var constructor = Constructor.builder()
                .accessModifier(AccessModifier.PUBLIC)
                .name(refreshClassName)
                .addParameter(Parameter.builder().name("config").type(TypeName.create("io.helidon.config.Config")))
                .addContentLine("config.onChange(cc -> delegate = " + configTypeName.fqName() + ".create(cc));")
                .addContentLine("delegate = " + configTypeName.fqName() + ".create(config);");
        var refreshClassModel = ClassModel.builder()
                .type(refreshTypeName)
                .addInterface(configTypeName)
                .addConstructor(constructor)
                .addField(Field.builder()
                        .accessModifier(AccessModifier.PRIVATE)
                        .name("delegate")
                        .type(configTypeName)
                        .addAnnotation(Annotation.builder().type("lombok.experimental.Delegate")));

        refreshClassModel.addAnnotation(CodegenUtil.generatedAnnotation(
                GENERATOR,
                blueprint.typeName(),
                refreshTypeName,
                "1",
                LocalDateTime.now().toString()));
        ctx.filer().writeSourceFile(refreshClassModel.build());
    }

    private String className(TypeInfo blueprint) {
        var name = blueprint.typeName().className();
        return "Refresh" + name.substring(0, name.length() - "blueprint".length());
    }

    private TypeName configTypeName(TypeInfo blueprint) {
        var className = blueprint.typeName().className();
        var name = className.substring(0, className.length() - "blueprint".length());
        return TypeName.create(blueprint.typeName().packageName() + "." + name);
    }
}
