# To perform a release of this project


```shell
./gradlew clean
GITHUB_USER=... GITHUB_TOKEN=... ./gradlew githubRelease
./gradlew dsl:uploadArchives -Psigning.keyId=... -Psigning.password=... -PnexusUsername=... -PnexusPassword=...
./gradlew closeAndReleaseRepository -PnexusUsername=... -PnexusPassword=...
```
