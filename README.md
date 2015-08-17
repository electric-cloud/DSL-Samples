# DSL-Samples

Examples of DSL code that produce Commander and Deploy models

## Tips and tricks

<code>
// Storing and retrieving Groovy structures through EF properties
import groovy.json.JsonOutput
def groovyObject = [a: "a val", b: "b val"]
propValue = JsonOutput.toJson(groovyObject)
// save propValue to an EF property
$[EFprop].each { k, v ->
	keyName = k
	value = v
}
</code>


