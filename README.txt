// Copyright (c) 2015 PocketMath, Inc.  All rights reserved.

  _________  __               .        ____   ____
 /   _____/_/  |_ _____   * ______ ____\   \ /   /
 \_____  \ \   __\\__  \   /  ___//  _ \\   Y   / 
 /        \ |  |   / __ \_ \___ \(  <_> )\     /  
/_______  / |__|  (____  //____  >\____/  \___/   
        \/+     .      \/      \/        *        .
   .                 .
      A secret weapon to take over galaxies.
                  .                          .   *
           +            |    .     +
    *                ---|---            .
          + .      /   _|_   \    *         *
       .          /  /  |  \  \         +
               -----|- AND -|-----     created .
           .      \  \ _|_ /  /       March 2015
  *                \    |    /       .        .
             *       ---|---     +
                        | .              .     *

      EFFICIENT SOFTWARE FOR MATCHING INCOMING
         IMPRESSION OPPORTUNITY DATA WITH
               TARGETING PARAMETERS.


                    STARRING

      The Pocket "PocketTL" Targeting Langauge


                   CO-STARRING

          Arbitrary Boolean Expressions
                  Matching Tree
                 IOBitset Reborn
              Superfast Primitives


               A SPECIAL THANKS TO

           Tors for the IOBitSet Concept
    Andy for the original IOBitSet Implementation
                   Shishito Team

               ANTLR Parser Generator
     Exotic Italian Primitives Library FastUtil


               ORIGINAL DEVELOPMENT
                   Eric Tucker



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