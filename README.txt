// Copyright (c) 2015 PocketMath, Inc.  All rights reserved.

  _________  __               .        ____   ____
 /   _____/_/  |_ _____   * ______ ____\   \ /   /                        STARRING
 \_____  \ \   __\\__  \   /  ___//  _ \\   Y   /          The Pocket "PocketTL" Targeting Language
 /        \ |  |   / __ \_ \___ \(  <_> )\     /  
/_______  / |__|  (____  //____  >\____/  \___/   
        \/+     .      \/      \/        *        .                      CO-STARRING
   .                 .                                          Arbitrary Boolean Expressions
      A secret weapon to take over galaxies.                            Matching Tree
                  .                          .   *                     IOBitset Reborn
           +            |    .     +                                 Superfast Primitives
    *                ---|---            .
          + .      /   _|_   \    *         *
       .          /  /  |  \  \         +                            A SPECIAL THNAKS TO
               -----|- AND -|-----     created .            Tors Dalid for the IOBitSet Concept
           .      \  \ _|_ /  /       March 2015     Andy Kurnia for the original IOBitSet Implementation
  *                \    |    /       .        .                   The Entire Shishito Team
             *       ---|---     +                                 ANTLR Parser Generator
                        | .              .     *           Exotic Italian Primitives Library FastUtil

      EFFICIENT SOFTWARE FOR MATCHING INCOMING
         IMPRESSION OPPORTUNITY DATA WITH                                 STORY BY
               TARGETING PARAMETERS.                                     Eric Tucker




////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// IDE + PLUGINS
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Original development used IntelliJ IDEA 14.

Some configuration is IDEA-specific.

The following plugins are recommended:
   + ANTLR      (required to re-generate/develop the grammars but otherwise not needed)
   + FreeMarker (highlighting of FreeMarker templates used for code generation)
   + .ignore    (to support UI git ignoring in the IDE)

ANTLR and FreeMarker support in the IDE require the commercial (non-free) version of IDEA.

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MAVEN
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TODO

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// MODULES / SOURCE ORGANIZATION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

Stasov is organized as a single IntelliJ IDEA project with multiple modules.

The modules are:

ATTRIBUTES
The attribute handler implementations.

ATTRIBUTES-COMMON
Attributes modules should depend on this.

ATTRIBUTES-DEVEL
For generating attribute handling code in the attributes-generated-glue module.  See AttrSvcGenerator.

ATTRIBUTES-GENERATED-GLUE
TODO

POCKETQL-GRAMMARS
ANTLR grammars.

STASOV-COMMON
All other modules should depend on this.

STASOV-UTIL
Most other modules should depend on this.

MATCHER
TODO

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// ATTTRIBUTE DEVELOPMENT
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TODO

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// TESTING
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TODO

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// JAVADOC GENERATION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TODO

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// INTEGRATION
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

TODO