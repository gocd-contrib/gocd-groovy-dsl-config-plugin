# 0.7.6

## Improved

 - Added support for `allowOnlyOnSuccess` attribute at approval in stage

# 0.7.4

## Fixed

- Fixed an incorrect validation with fetch artifact tasks. Blank value for `destination` should be considered valid.

## Improved

- Upgraded a bunch of dependent libraries.

# v0.7.3

## Fixed

- Fixed a NPE when plugin settings are not defined.

# v0.7.2

## Fixed

- Fixed handling of variables with empty values (`FOO=`) while exporting pipeline configs.
- Add support for serializing job properties.
- Improve exporting of SCM blacklist/whitelist filters.

# v0.7.1

## Fixed

- Fixed issue around serialization/deserialization and export of pipeline parameters

# v0.7.0

## Improved

- Added support for exporting pipeline config repository. Requires GoCD server version 19.1 or later to export pipeline.
- Added support for performing preflight checks. Requires GoCD server version 19.1 or later to export pipeline.
- Added support for plugin icon. Requires GoCD server version 19.1 or later to show icon.

## Changed

- The command line syntax has been changed to be consistent with the syntax used by the JSON and YAML config repo plugins.

## Chores

- Upgraded a bunch of libraries

# v0.6.0

## Fixed

- [#3](https://github.com/ketan/gocd-groovy-dsl-config-plugin/issues/3) - Error while parsing Config repository

## Chores

- Upgraded gradle
- Upgraded a bunch of libraries

# v0.5.0

## Improved

- Added support for artifact origin in fetch artifact tasks

# v0.4.0

## Improved

- Added validation of generated DSL.

# v0.3.0

## Changed

- renamed the `secureVariables` attribute to `secureEnvironmentVariables`

# v0.2.0

## Changed

- replaced the `Factory` interface with built-in `java.util.Supplier` interface.
- `dsl` module no longer depends on the `sandbox` module.

# v0.1.0

Initial release. See README.md for examples.

