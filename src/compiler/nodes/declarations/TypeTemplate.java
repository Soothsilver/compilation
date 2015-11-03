package compiler.nodes.declarations;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class TypeTemplate extends TypeOrTypeTemplate {
    public ArrayList<Type> typeParameters = new ArrayList<>();
    protected TypeTemplate(String name, int line, int column) {
        super(name, line, column);
    }
    public static TypeTemplate createTemplate(String name, int line, int column) {
        TypeTemplate template = new TypeTemplate(name, line, column);
        return template;
    }

    @Override
    public String getFullString() {
        return "type " + name + " = class[[" + typeParameters.stream().map(tp->tp.toString()).collect(Collectors.joining(",")) + "]] {\n"
                + declarations.stream().map(decl->decl.getFullString()).collect(Collectors.joining("\n"))
                + (declarations.size() > 0 ? "\n" : "")
                + subroutines
                + "};";
    }
}
