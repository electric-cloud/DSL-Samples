/*
Format: Electric Flow DSL
File: propertyPathSearch.groovy
Description: Illustrates how a property value can be set by searching
a set of properties, using the first non blank value (undefined
okay, as well)

ectool evalDsl --dslFile "propertyPathSearch.groovy"

*/

project "Property Search Path", {
  property "prop", value: "project level value"
  procedure "Test", {
    property "prop", value: "procedure level value"
    step "set prop",
      command : "ectool setProperty /myJob/out " +
        '''\
		"$[/javascript
			var PropName = "prop";
			var Out = "default value";
			var PropCandidates = [myStep[PropName],myProcedure[PropName],myProject[PropName]];
			for (var PropCandidate in PropCandidates) {
				if (!PropCandidates[PropCandidate]=="") {
					Out = PropCandidates[PropCandidate];
					break;
				}
			}
			Out;
		]"
		'''.stripIndent(), {
        property "prop", value: "step level value"
      }
    step "echo prop", command: "echo \"\$[/myJob/out]\""
  }
}
