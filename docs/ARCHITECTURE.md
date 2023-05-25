# SonarJS architecture

## Introduction

SonarJs consists on a Java side, which interfaces with the Sonar Scanner API, and a Node.js process, which will handle the analysis of the source files.

### Java plugin

- Initializes Node.js process
- Initializes the linters on the Node process (via `init-linter` endpoint)
- Loops though the files
  - Decides the type of analysis based on cache status
  - Requests analysis to Node.js
  - Retrieves results and saves them

### Node.js

- Handles the analysis of the files

## Sequence diagram

```mermaid
sequenceDiagram
  box Java
  participant J as JsTsSensor
  end
  box Node.js
  participant N as HTTP Endpoint
  participant B as Builder
  participant P as Program Creator
  participant TE as Typescript-eslint
  participant BL as Babel
  participant A as Analyzer
  end
  J->>+N: /init-linter
  N->>-J: OK!
  loop Every JS/TS file
  J->>+N: /analyze-[ts|js]
  N->>+B: build
  alt !SonarLint && !Vue
    B->>+P: find program for file¹
    P->>-B: Program
  else
    B->>B: decide tsconfigs²
  end
  B->>+TE: buildSourceCode
  TE->>-B: SourceCode
  alt Typescript failed to build
    B->>+BL: buildSourceCode
    BL->>-B: SourceCode
  end
  B->>-N: SourceCode
  N->>+A: lint sourceCode
  A->>-N: issues
  N->>-J: results
  end
```

### Notes

1. Find program for file

```mermaid
flowchart TB
  A[Start] --> B{Have<br>we searched<br>for tsconfigs in<br>project<br>baseDir?}
  B -- Yes --> C(Prepend request tsconfigs to list of found tsconfigs)
  B -- No, or `forceUpdateTSConfigs` has been set to true --> D(Look for tsconfig files in project baseDir)
  D --> B
  C -->  H{Are there tsconfigs<br>left to be checked?}
  H -- Yes, pick next tsconfig --> E(Create program with tsconfig)
  H -- No --> G(Create program with fallback tsconfig)
  E -->  F{Does program include the<br>file we want to analyze?}
  G --> F
  F -- No --> H


```

2. Decide tsconfigs

```mermaid
flowchart TB
    Start --> Stop

```
