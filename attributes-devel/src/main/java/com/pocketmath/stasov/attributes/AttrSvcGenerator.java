package com.pocketmath.stasov.attributes;

import freemarker.template.*;

import java.io.*;
import java.lang.annotation.Annotation;
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

            imports.add(AttributeHandler.class.getName());
            imports.add(AttributeHandlerConcurrentCachingProxy.class.getName());

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

            final List<AttrMeta> allAttrs = new ArrayList<AttrMeta>();
            for (Map.Entry<String,Class> entry : attrNames.entrySet()) {
                final Class cl = entry.getValue();
                boolean cached = false;
                int cacheSize = -1;
                final Annotation anno = cl.getAnnotation(AttributeType.class);
                System.out.println("entry: " + entry);
                System.out.println("anno: " + anno);
                System.out.println("entryvalue: " + entry.getValue().getAnnotations().length);
                if (anno != null) {
                    final AttributeType attrTypeAnno = (AttributeType) anno;
                    cached = attrTypeAnno.cached();
                    cacheSize = attrTypeAnno.cacheSize();
                }
                String name = entry.getKey();
                AttrMeta attr = new AttrMeta(name, name.toLowerCase(), attrTypeIds.get(name), cached, cacheSize);
                allAttrs.add(attr);
            }

            if (allAttrs.isEmpty()) {
                throw new IllegalStateException("no attrs found");
            }

            final List<AttrMeta> attrs = new ArrayList<AttrMeta>();
            final List<AttrMeta> cacheAttrs = new ArrayList<AttrMeta>();

            for (AttrMeta attr : allAttrs) {
                if (attr.isCached())
                    cacheAttrs.add(attr);
                else
                    attrs.add(attr);
            }

            data.put("imports", imports);
            data.put("allAttrs", allAttrs);
            data.put("attrs", attrs);
            data.put("cacheAttrs", cacheAttrs);

            data.put("cacheClass", AttributeHandlerConcurrentCachingProxy.class.getSimpleName());

            File f = new File("attributes-generated-glue/target/generated-sources/java/com/pocketmath/stasov/attributes");
            f.mkdirs();

            final String oppDataGenOutput = "attributes-generated-glue/target/generated-sources/java/com/pocketmath/stasov/OpportunityData.java";
            data.put("package", "com.pocketmath.stasov");
            data.put("className", "OpportunityData");
            write(data, "attributes-devel/src/main/resources/templates/OpportunityData.ftl", oppDataGenOutput, cfg);
            System.out.println("generated source written to: " + oppDataGenOutput);

            final String attributeHandlersGenOutput = "attributes-generated-glue/target/generated-sources/java/com/pocketmath/stasov/attributes/AttributeHandlers.java";
            data.put("package", "com.pocketmath.stasov.attributes");
            data.put("className", "AttributeHandlers");
            write(data, "attributes-devel/src/main/resources/templates/AttributeHandlers.ftl", attributeHandlersGenOutput, cfg);

            System.out.println("generated source written to: " + attributeHandlersGenOutput);

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
