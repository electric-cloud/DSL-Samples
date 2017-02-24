# Example of passing parameters to DSL code
# This shows that the JSON properties a and b can be passed in and used in DSL
ectool evalDsl 'return args.a + args.b' --parameters '{"a":"abc", "b":"xyz"}'
