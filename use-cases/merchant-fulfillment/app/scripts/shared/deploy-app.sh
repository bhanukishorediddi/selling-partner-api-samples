#!/bin/bash

# Get the programming language from the input arguments
language=""
while getopts 'l:' flag; do
  case "${flag}" in
    l) language="${OPTARG}";;
  esac
done

# Set the Lambda runtime based on the programming language
runtime=""
case "${language}" in
  java) runtime="java11";;
  javascript) runtime="nodejs18.x";;
  *) echo "Undefined language"; exit;;
esac

# Verify pre-requisites
bash ../shared/pre-requisites.sh "$@"
if [ $? -ne 0 ]
then
  echo "Verifying pre-requisites failed"
  echo "Aborting"
  exit -1
fi

# Generate a random string of 6 characters
random_string=$(LC_ALL=C tr -dc 'a-z' < /dev/urandom | fold -w 6 | head -n 1)

# Create an IAM user with required permissions to generate the CloudFormation stack
echo "Creating IAM user"
user_name="sp-api-app-user-${random_string}"
aws iam create-user --user-name ${user_name}
if [ $? -ne 0 ]
then
  echo "IAM user creation failed"
  echo "Aborting"
  exit -1
fi

# Sleeping to propagate changes
echo "Processing ..."
sleep 30

# Create and attach the IAM policy
echo "Creating IAM policy"
policy_arn=$(aws iam create-policy --policy-name "sp-api-app-policy-${random_string}" --policy-document file://../iam-policy.json --query "Policy.Arn" --output text)
aws iam attach-user-policy --user-name "${user_name}" --policy-arn ${policy_arn}
if [ $? -ne 0 ]
then
  echo "IAM policy creation failed"
  echo "Aborting"
  exit -1
fi

# Sleeping to propagate changes
echo "Processing ..."
sleep 30

# Create access key and secret key for the IAM user
echo "Creating IAM keys"
secret=$(aws iam create-access-key --user-name ${user_name} --query 'AccessKey.[AccessKeyId,SecretAccessKey]' --output text)
access_key=$(echo ${secret} | awk '{print $1}')
secret_key=$(echo ${secret} | awk '{print $2}')

# Sleeping to propagate changes
echo "Processing ..."
sleep 30

# Create the S3 bucket to host Java code
echo "Creating S3 resources"
bucket_name="sp-api-app-bucket-${random_string}"
AWS_ACCESS_KEY_ID=${access_key} AWS_SECRET_ACCESS_KEY=${secret_key} \
  aws s3 mb "s3://${bucket_name}"
if [ $? -ne 0 ]
then
  echo "S3 bucket creation failed"
  echo "Aborting"
  exit -1
fi

# Sleeping to propagate changes
echo "Processing ..."
sleep 30

# If it's a Java app, package the code and upload it to S3
if [ "$language" == "java" ]; then
  echo "Packaging and uploading Java code"
  java_code_folder="../../../code/java/"
  java_code_jar="target/sp-api-java-app-1.0.jar"
  code_s3_key="src/sp-api-java-app.jar"
  retrieve_order_handler="lambda.RetrieveOrderHandler"
  inventory_check_handler="lambda.InventoryCheckHandler"
  eligible_shipment_handler="lambda.EligibleShipmentHandler"
  select_shipment_handler="lambda.SelectShipmentHandler"
  create_shipment_handler="lambda.CreateShipmentHandler"
  presign_s3_label_handler="lambda.PresignS3LabelHandler"
  process_notification_handler="lambda.ProcessNotificationHandler"
  subscribe_notifications_handler="lambda.SubscribeNotificationsHandler"
  mvn validate -f "${java_code_folder}pom.xml"
  mvn package -f "${java_code_folder}pom.xml"
  AWS_ACCESS_KEY_ID=${access_key} AWS_SECRET_ACCESS_KEY=${secret_key} \
    aws s3 cp "${java_code_folder}${java_code_jar}" "s3://${bucket_name}/${code_s3_key}"
elif [ "$language" == "javascript" ]; then
  echo "Packaging and uploading JavaScript code"
  js_code_folder="../../../code/javascript/"
  js_code_zip="target/sp-api-javascript-app-1.0-upload.zip"
  code_s3_key="src/sp-api-javascript-app.zip"
  lambda_func_handler="index.handler"
  (
    cd "${js_code_folder}src" || exit
    npm install
    zip -rq "${js_code_folder}${js_code_zip}" .
  )
  AWS_ACCESS_KEY_ID=${access_key} AWS_SECRET_ACCESS_KEY=${secret_key} \
    aws s3 cp "${js_code_folder}${js_code_zip}" "s3://${bucket_name}/${code_s3_key}"
fi

# Upload the StepFunctions state machine definition to S3
state_machine_s3_key="step-functions/state-machine-definition.json"
AWS_ACCESS_KEY_ID=${access_key} AWS_SECRET_ACCESS_KEY=${secret_key} \
  aws s3 cp ../../step-functions/step-functions-workflow-definition.json "s3://${bucket_name}/${state_machine_s3_key}"
if [ $? -ne 0 ]
then
  echo "State machine definition upload to S3 failed"
  echo "Aborting"
  exit -1
fi

# Retrieve config values
config_file="../../app.config"
sp_api_access_key="$(grep "^AccessKey=" "${config_file}" | cut -d"=" -f2)"
sp_api_secret_key="$(grep "^SecretKey=" "${config_file}" | cut -d"=" -f2)"
sp_api_role_arn="$(grep "^RoleArn=" "${config_file}" | cut -d"=" -f2)"
sp_api_client_id="$(grep "^ClientId=" "${config_file}" | cut -d"=" -f2)"
sp_api_client_secret="$(grep "^ClientSecret=" "${config_file}" | cut -d"=" -f2)"
sp_api_refresh_token="$(grep "^RefreshToken=" "${config_file}" | cut -d"=" -f2)"
sp_api_region_code="$(grep "^RegionCode=" "${config_file}" | cut -d"=" -f2)"
sp_api_email="$(grep "^Email=" "${config_file}" | cut -d"=" -f2)"

# Create the CloudFormation stack
echo "Creating CloudFormation stack"
stack_name="sp-api-app-java-${random_string}"
AWS_ACCESS_KEY_ID=${access_key} AWS_SECRET_ACCESS_KEY=${secret_key} \
  aws cloudformation create-stack \
    --stack-name "${stack_name}" \
    --template-body file://../../app-template.yaml \
    --capabilities CAPABILITY_NAMED_IAM \
    --parameters \
      ParameterKey="AccessKey",ParameterValue="${sp_api_access_key}" \
      ParameterKey="SecretKey",ParameterValue="${sp_api_secret_key}" \
      ParameterKey="RoleArn",ParameterValue="${sp_api_role_arn}" \
      ParameterKey="ClientId",ParameterValue="${sp_api_client_id}" \
      ParameterKey="ClientSecret",ParameterValue="${sp_api_client_secret}" \
      ParameterKey="RefreshToken",ParameterValue="${sp_api_refresh_token}" \
      ParameterKey="RegionCode",ParameterValue="${sp_api_region_code}" \
      ParameterKey="ProgrammingLanguage",ParameterValue="${runtime}" \
      ParameterKey="RandomSuffix",ParameterValue="${random_string}" \
      ParameterKey="ArtifactsS3BucketName",ParameterValue="${bucket_name}" \
      ParameterKey="LambdaFunctionsCodeS3Key",ParameterValue="${code_s3_key}" \
      ParameterKey="SPAPIRetrieveOrderLambdaFunctionHandler",ParameterValue="${retrieve_order_handler}" \
      ParameterKey="SPAPIInventoryCheckLambdaFunctionHandler",ParameterValue="${inventory_check_handler}" \
      ParameterKey="SPAPIEligibleShipmentLambdaFunctionHandler",ParameterValue="${eligible_shipment_handler}" \
      ParameterKey="SPAPISelectShipmentLambdaFunctionHandler",ParameterValue="${select_shipment_handler}" \
      ParameterKey="SPAPICreateShipmentLambdaFunctionHandler",ParameterValue="${create_shipment_handler}" \
      ParameterKey="SPAPIPresignS3LabelLambdaFunctionHandler",ParameterValue="${presign_s3_label_handler}" \
      ParameterKey="SPAPIProcessNotificationLambdaFunctionHandler",ParameterValue="${process_notification_handler}" \
      ParameterKey="SPAPISubscribeNotificationsLambdaFunctionHandler",ParameterValue="${subscribe_notifications_handler}" \
      ParameterKey="StepFunctionsStateMachineDefinitionS3Key",ParameterValue="${state_machine_s3_key}" \
      ParameterKey="NotificationEmail",ParameterValue="${sp_api_email}"
if [ $? -ne 0 ]
then
  echo "CloudFormation stack creation failed"
  echo "Aborting"
  exit -1
fi
echo "CloudFormation stack name = ${stack_name}"

mfn_bucket_name="sp-api-labels-s3-bucket-${random_string}"

# Store resources' IDs in a tmp file for future clean-up
echo "Storing resources' IDs in a tmp file"
tempdir="tmp"
if [ ! -d "$tempdir" ]; then
  mkdir "$tempdir"
fi
filename="tmp/resources.txt"
exec 3<> "$filename"
echo "user_name=$user_name" >&3
echo "policy_arn=$policy_arn" >&3
echo "access_key=$access_key" >&3
echo "secret_key=$secret_key" >&3
echo "bucket_name=$bucket_name" >&3
echo "mfn_bucket_name=$mfn_bucket_name" >&3
echo "stack_name=$stack_name" >&3
exec 3>&-

echo "Successfully created an MFN SP-API app"