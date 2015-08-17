# DSL-Samples

Examples of DSL code that produce Commander and Deploy models

## Tips and tricks

<pre><code>
// Storing and retrieving Groovy array through EF properties
import groovy.json.JsonOutput
def groovyArray = ["a", "b"]
propValue = JsonOutput.toJson(groovyArray)
// save propValue to an EF property
$[EFprop].each { val ->
	// use each element here as 'val'
}
</pre></code>


