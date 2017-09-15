#!/bin/bash

ectool runProcedure --projectName "On line bank Release" \
--procedureName "Create Release" \
--actualParameter "applications=OB - Account Statements,OB - Credit Card Accounts,OB - Fund Transfer" \
"pipeName=Quarterly Online Banking Release" \
"plannedEndDate=2017-10-1" \
"plannedStartDate=2017-9-2" \
"projName=On line bank Release" \
"release=September Release" \
"stages=UAT,PreProd,PROD" \
"versions=2.4,5.1,1.7"

ectool runProcedure --projectName "On line bank Release" \
--procedureName "Create Release" \
--actualParameter "applications=OB - Account Statements,OB - Credit Card Accounts,OB - Fund Transfer" \
"pipeName=Quarterly Online Banking Release" \
"plannedEndDate=2017-11-1" \
"plannedStartDate=2017-10-2" \
"projName=On line bank Release" \
"release=October Release" \
"stages=UAT,PreProd,PROD" \
"versions=2.4,5.1,1.7"

ectool runProcedure --projectName "On line bank Release" \
--procedureName "Create Release" \
--actualParameter "applications=OB - Account Statements,OB - Credit Card Accounts,OB - Fund Transfer" \
"pipeName=Quarterly Online Banking Release" \
"plannedEndDate=2017-11-1" \
"plannedStartDate=2017-10-2" \
"projName=On line bank Release" \
"release=November Release" \
"stages=UAT,PreProd,PROD" \
"versions=2.4,5.1,1.7"


