## Cloud Functions
Have a read at https://docs.microsoft.com/en-us/azure/azure-functions/functions-create-first-java-gradle

```bash
./gradlew :functions:build
```

Run the function locally
```bash
./gradlew jar --info
./gradlew azureFunctionsRun
```

Deploy manually to Azure
```bash
./gradlew azureFunctionsDeploy
```