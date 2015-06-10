package com.pocketmath.stasov.attributes;

import freemarker.template.*;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

/**
 * Created by etucker on 3/28/15.
 */
public class AttrSvcGenerator {

    private final AttrConfig attrCfg;

    public AttrSvcGenerator(final AttrConfig attrCfg) {
        this.attrCfg = attrCfg;
    }

    private void write(final Map<String,Object> data, final String templatePath, final String outputPath, final Configuration cfg) throws IOException, TemplateException {
        Template template = cfg.getTemplate(templatePath);
        Writer out = new FileWriter(outputPath);
        out.write("// GENERATED FILE.  EDITING NOT RECOMMENDED.\n\r");
        template.process(data, out);
        out.flush();
    }

    public void generate() throws DuplicateAttributeException {
        final Configuration cfg = new Configuration();
        try {

            final SortedSet<String> imports = new TreeSet<String>();

            final Map<String, Object> data = new HashMap<String, Object>();

            data.put("datetime", new Date().toString() + " on host " + InetAddress.getLocalHost().getHostName() + " by user " + System.getProperty("user.name"));

            final Set<Class> attrClasses = AttrUtil.findAttrClasses(attrCfg);
            final SortedMap<String, Class> attrNames = AttrUtil.findAttrNames(attrClasses);

            long attrTypeId = 0;
            final Map<String,Long> attrTypeIds = new TreeMap<String,Long>();
            for (Map.Entry<String,Class> entry : attrNames.entrySet()) {
                attrTypeIds.put(entry.getKey(), ++attrTypeId);
                imports.add(entry.getValue().getName());
            }

            final List<AttrMeta> attrs = new ArrayList<AttrMeta>();
            for (Map.Entry<String,Class> entry : attrNames.entrySet()) {
                String name = entry.getKey();
                AttrMeta attr = new AttrMeta(name, name.toLowerCase(), attrTypeIds.get(name));
                attrs.add(attr);
            }

            if (attrs.isEmpty()) {
                throw new IllegalStateException("no attrs found");
            }

            data.put("imports", imports);
            data.put("attrs", attrs);

            data.put("package", "com.pocketmath.stasov");
            data.put("className", "OpportunityData");
            write(data, "attributes-devel/src/main/resources/templates/OpportunityData.ftl", "attributes-generated-glue/src/generated/java/com/pocketmath/stasov/OpportunityData.java", cfg);

            data.put("package", "com.pocketmath.stasov.attributes");
            data.put("className", "AttributeHandlers");
            write(data, "attributes-devel/src/main/resources/templates/AttributeHandlers.ftl", "attributes-generated-glue/src/generated/java/com/pocketmath/stasov/attributes/AttributeHandlers.java", cfg);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) throws Exception {
        AttrSvcGenerator g = new AttrSvcGenerator(new AttrConfig());
        g.generate();
    }

}
