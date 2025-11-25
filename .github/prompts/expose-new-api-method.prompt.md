---
mode: 'agent'
model: 'Claude Sonnet 4'
description: 'Generate a new API method for Feature API'
---


 Your goal is to implement a new API method in the Feature API project following the guidelines provided in the instructions files.
 
 Following the instructions presented in #copilot-instructions.md file, plus all the other instruction files we need to implement a new api method ${input:methodName}, part of the ${input:apiName}.
 
 If the API doesn't exist yet, you need to create it following the guidelines in the #api-guidelines.instructions.md file.

 Be careful : when handling countries and languages the input will be the `country code` and `language code`, not the `country id` or `language id`, so you will need to convert the codes to the corresponding `country id` and `language id` using the repositories provided in the project.
  
 The project uses two MySQL relational databases to store the data, make sure you follow the instructions in the #repositories-guidelines.instructions.md and #gataway-guidelines files to understand how to access the databases and where the data is stored.

  
