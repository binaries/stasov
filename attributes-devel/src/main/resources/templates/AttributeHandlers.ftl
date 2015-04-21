// Generated at ${datetime}

package ${package};

import java.util.HashSet;
import java.util.Set;

import it.unimi.dsi.fastutil.longs.*;

// Generated imports:
<#list imports as import>
import ${import};
</#list>

public class ${className} extends AttrSvcBase {

    <#list attrs as attr>
    private final ${attr.className} ${attr.typeName};
    </#list>

    public ${className}() {
        final Set<AttrMeta> attrMetas = new HashSet<AttrMeta>();
        <#list attrs as attr>
        ${attr.typeName} = new ${attr.className}();
        attrMetas.add(new AttrMeta("${attr.className}", "${attr.typeName}", ${attr.typeId}));
        </#list>

        register(attrMetas);
    }

    protected final AttributeHandler lookupHandler(final long attrTypeId) {
        assert(attrTypeId < Integer.MAX_VALUE);
        switch ((int)attrTypeId) {
            <#list attrs as attr>
            case ${attr.typeId} : { return ${attr.typeName}; }
            </#list>
            default : throw new UnsupportedOperationException();
        }
    }

}