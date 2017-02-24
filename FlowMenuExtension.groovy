/*
	ElectricFlow DSL Example
	
	Add menu items to the ElectricFlow UI
	
	The ElectricFlow menu can be extended by creating a property, /server/ec_ui/flowMenuExtension.  The content of this property should be XML with 'menu', 'tab', 'label' and 'url' tags arranged in the following manner:
	
		<?xml version="1.0" encoding="UTF-8"?>
			<menu>
				<!-- A top level menu item  -->
				<tab>
					<label>some label text</label>
					<url>a link</url>
				</tab>
				<!-- A top level menu item with a sub menu -->
				<tab>
					<label>a root menu...</label>
					<tab>
						<label>a sub menu item</label>
						<url>a link</url>
					</tab>
				</tab>
				<!-- etcetera  -->
			</menu>

	Note that the URLs are referenced from the 'commander' node, so referencing Flow UI items requires ../flow
	
	Instructions
	- Copy and paste this document into the DSL IDE and 'Submit DSL', or
	- ectool evalDsl --dslFile FlowMenuExtension.groovy
			
*/


property "/server/ec_ui/flowMenuExtension", value: '''\
	<?xml version="1.0" encoding="UTF-8"?>
	<menu>
		<tab>
			<label>Master Components</label>
			<url>../flow/#applications/components</url>
		</tab>
		<tab>
			<label>CI</label>
			<url>pages/EC-CIManager/configure</url>
		</tab>
	</menu>
'''.stripIndent()