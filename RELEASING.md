# To perform a release of this project


To GitHub (of the plugin itself)
```shell
export GITHUB_USER=...
export GITHUB_TOKEN=... 
./gradlew clean githubRelease
```

To Maven Central **Staging only** (of the DSL)
```shell
export MAVEN_CENTRAL_TOKEN_USERNAME=
export MAVEN_CENTRAL_TOKEN_PASSWORD=
export GOCD_GPG_KEY_ID=
export GOCD_GPG_KEYRING_FILE=
export GOCD_GPG_PASSPHRASE=
./gradlew publishToSonatype
```

To Maven Central **and release publicly**
```shell
export MAVEN_CENTRAL_TOKEN_USERNAME=
export MAVEN_CENTRAL_TOKEN_PASSWORD=
export GOCD_GPG_KEY_ID=
export GOCD_GPG_KEYRING_FILE=
export GOCD_GPG_PASSPHRASE=
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```