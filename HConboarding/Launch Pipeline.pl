use ElectricCommander;
use JSON; 
use strict;

$| = 1;

#Create a reference to ElectricCommander
my $ec = new ElectricCommander({'format'=>'json'});

my $pipeline = $ec->runPipeline(
	"Default", "$[app]",
	{startingStage => "$[startingStage]"}
	)->{responses}[0]->{flowRuntime};

print encode_json($pipeline),"\n";
$ec->setProperty("/myJob/report-urls/Release pipeline",
	"/flow/?s=Flow+Tools&ss=Flow#pipeline-run/$pipeline->{flowId}/$pipeline->{flowRuntimeId}");
	
$ec->setProperty("/myJob/ec_job_description",
	'<html><table bgcolor="#f1f1f4" border="1"><tr><td style="padding:5px"><a target="_blank" href="/flow/?s=Flow+Tools&ss=Flow#pipeline-run/' . $pipeline->{flowId} . "/" . $pipeline->{flowRuntimeId} . '">Release pipeline details</a><br /></td></tr></table></html>');