Apimap.io Command-line interface (CLI) 
===

🎉 **Welcome** 🎉

This is the home of the Apimap.io project, a freestanding solution to keep track of all functionality a company
provides through an API. It is a push based system, connected with your build pipeline or manually updated using our CLI.

> **Application programming interface (API)**: Point of functional integration between two or more systems connected
> through commonly known standards

**Why is this project useful?** Lost track of all the API functionality provided inside your organization? Don't want
to be tied to an API proxy or management solution? The Apimap.io project uploads, indexes and enables discoverability of all
your organizations APIs. We care about the source code, removing the limitation of where the API is hosted and how your
network is constructed.

## Table of Contents

* [Project Components](#project-components)
* [Commands](#commands)
* [Build and Run](#build-and-run)
* [Contributing](#contributing)

I want to know more of the technical details and implementation guides: [DEVELOPER.md](DEVELOPER.md)

## Project Components
___
This is a complete software solution consisting of a collection of freestanding components. Use only the components you
find useful, create the rest to custom fit your organization.

- A **Developer Portal** with wizards and implementation information
- A **Discovery Portal** to display APIs and filter search results
- An **API** to accommodate all the information
- A **Jenkins plugin** to automate information parsing and upload
- A **CLI** to enable manual information uploads

## Commands

| Command                   | Description                                                                   |
| ------------------------- | ----------------------------------------------------------------------------- |
| apimap --help             | View all available top level commands                                         |
| apimap --version          | View CLI version                                                              |

| Command                   | Description                                                                   |
| ------------------------- | ----------------------------------------------------------------------------- |
| apimap validate           | Validate the content of the metadata and taxonomy file. **Arguments:** **--metadata** 'filename' **--taxonomy** 'filename' |

| Command                   | Description                                                                   |
| ------------------------- | ----------------------------------------------------------------------------- |
| apimap rename             | Rename an existing API. **Arguments:** **--from** 'old name' **--to** 'new name' **--endpoint-url** 'url to apimap instance' **--token** 'token if not handled by CLI'|

| Command                   | Description                                                                   |
| ------------------------- | ----------------------------------------------------------------------------- |
| apimap publish            | Upload metadata and taxonomy files. This will also create the API if it is not already present in the catalog. **Arguments:** **--metadata** 'filename' **--taxonomy** 'filename' **--code-repository-url** 'code repository url' **--endpoint-url** 'url to apimap instance' **--token** 'token if not handled by CLI'|

| Command                   | Description                                                                   |
| ------------------------- | ----------------------------------------------------------------------------- |
| apimap delete             | Delete an API from the catalog. Default this will only delete the version listed in the metadata file. **Arguments:** **--metadata** 'filename' **--recursive** true/false **--endpoint-url** 'url to apimap instance' **--token** 'token if not handled by CLI'|

## Build and Run
___

This library contains the CLI used to communicate with the service API.

It runs on the following operating systems:
* MacOS
* Linux
* Windows 

#### Build

Based on Java with GraalVM the easiest way to build the artifact is using **nativeImage**

```shell
./gradlew nativeImage
```

on windows the "Run using the x64 Native Tools Command Prompt for VS 2019" action must be used before running the gradlew command. If not the cl.exe will be missing.

#### Requirements

Java version 16 or newer with the latest GraalVM setup.

##### Special Windows Requirements
* Visual Studio 2019 or newer

## Contributing
___

Read [howto contribute](CONTRIBUTING.md) to this project.