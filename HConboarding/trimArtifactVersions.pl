my $artifactName="com.mycompany.heatclinic:warfile";
my $numberToKeep="8";

use ElectricCommander;
use strict;

$| = 1;

my $ec = new ElectricCommander();

my $numberFound = $ec->getArtifactVersions({artifactName => $artifactName})->find("count(//version)")->string_value;
print "$numberFound artifact versions found. Keeping $numberToKeep.\n";
if ($numberFound > $numberToKeep) {
        my $versions = $ec->getArtifactVersions({artifactName => $artifactName})->find("//version");
        my $index=$numberFound;
        foreach my $version ($versions->get_nodelist) {
                my $versionID=$version->string_value;
                if ($index > $numberToKeep) {
                        print "Deleting $artifactName:$versionID\n";
                        $ec->deleteArtifactVersion("$artifactName:$versionID");
                }
                $index--;
        }
}