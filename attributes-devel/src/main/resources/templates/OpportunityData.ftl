// Generated at ${datetime}

package ${package};

import java.util.Set;

import com.pocketmath.stasov.engine.OpportunityDataBase;

// Generated imports:
<#list imports as import>
import ${import};
</#list>

public abstract class ${className} extends OpportunityDataBase {

    @Override
    public final Set<String> getData(final long attrTypeId) {
        assert(attrTypeId < Integer.MAX_VALUE);
        assert(attrTypeId > 0);
        switch ((int)attrTypeId) {
<#list attrs as attr>
            case ${attr.typeId} : { return get${attr.className}(); }
</#list>
            default : throw new UnsupportedOperationException("The attribute type was not found for attrTypeId=" + attrTypeId);
        }
    }

<#list attrs as attr>
    protected abstract Set<String> get${attr.className}();
</#list>
}