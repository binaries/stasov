
      _________  __               .        ____   ____
     /   _____/_/  |_ _____   * ______ ____\   \ /   /     STARRING
     \_____  \ \   __\\__  \   /  ___//  _ \\   Y   /      "PocketTL" Targeting Language
     /        \ |  |   / __ \_ \___ \(  <_> )\     /
    /_______  / |__|  (____  //____  >\____/  \___/
            \/+     .      \/      \/        *        .    CO-STARRING
       .                 .                                 Arbitrary Boolean Expressions
          A secret weapon to take over galaxies.           Matching Tree
                      .                          .   *     IOBitset Reborn
               +            |    .     +                   Superfast Primitives
        *                ---|---            .
              + .      /   _|_   \    *         *
           .          /  /  |  \  \         +              A SPECIAL THANKS TO
                   -----|- AND -|-----           .         Tors Dalid, IOBitSet Concept
               .      \  \ _|_ /  /                 .      Andy Kurnia, 1st IOBitSet Impl
      *                \    |    /       .        .        The Entire Shishito Team
                 *       ---|---     +                     ANTLR Parser Generator
                            | .              .     *       Exotic Italian FastUtil

          EFFICIENT SOFTWARE FOR MATCHING INCOMING
             IMPRESSION OPPORTUNITY DATA WITH              STORY BY                UNLEASHED
                   TARGETING PARAMETERS.                   Eric Tucker            March 2015


    --------- Copyright (c) 2015 PocketMath, Inc.  All rights reserved. --------------------



# RIDICULOUSLY SIMPLE TO USE

### Start the Engine
```Java
// Instatiate the engine
Engine engine = new Engine();
```

### Indexing Insertion Orders
```Java
// Add some IOs
engine.index("City = Austin AND DeviceType = iPhone OR City = Houston", 1);
engine.index("City = Singapore AND DeviceType = iPhone", 2);
engine.index("City = \"San Francisco\"", 3);
engine.index("DeviceType = iPhone AND NOT City = Houston", 4);
engine.index("DeviceType IN (iPhone, Android) AND City IN (Singapore, \"San Francisco\")", 5);
```
### Impression Opportunity Matching
```Java
// Create a dummy impression opportunity
MapOpportunityData opp = engine.mapOpportunityData();
opp.put("city", "singapore");
opp.put("devicetype", "iphone");

// Find matching IOs
LongSortedSet results = engine.query(opp);

// Show results
System.out.println("Results=" + results);
```

# IDE + PLUGINS

Original development used IntelliJ IDEA 14.

Some configuration is IDEA-specific.

The following plugins are recommended:
   + ANTLR      (required to re-generate/develop the grammars but otherwise not needed)
   + FreeMarker (highlighting of FreeMarker templates used for code generation)
   + .ignore    (to support UI git ignoring in the IDE)

ANTLR and FreeMarker support in the IDE require the commercial (non-free) version of IDEA.

# MAVEN
TODO

# MODULES + SOURCE ORGANIZATION

Stasov is organized as a single IntelliJ IDEA project with multiple modules.

The modules are:

#### ATTRIBUTES
The attribute handler implementations.

#### ATTRIBUTES-COMMON
Attributes modules should depend on this.

#### ATTRIBUTES-DEVEL
For generating attribute handling code in the attributes-generated-glue module.  See AttrSvcGenerator.

#### ATTRIBUTES-GENERATED-GLUE
TODO

POCKETQL-GRAMMARS
ANTLR grammars.

When regenerating grammars, rebuild the entire project (or at least all modules dependent on generated grammars).
Otherwise you may not see effects of changes to grammars.

STASOV-COMMON
All other modules should depend on this.

STASOV-UTIL
Most other modules should depend on this.

MATCHER
TODO

# ATTRIBUTE DEVELOPMENT

TODO

# TESTING

TODO

# JAVADOC GENERATION

TODO

# INTEGRATION

TODO