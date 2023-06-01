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
  A[Start] --> RB{Request body<br>contains list<br>of tsconfigs?}
  RB -- Yes --> PFT(Pick first tsconfig)
  RB -- No --> HWS{Have<br>we searched<br>for tsconfigs in<br>project<br>baseDir?}
  HWS -- Yes --> FT(check forceUpdateTSConfigs value)
  HWS -- No --> LFT(Look for tsconfig files in project baseDir)
  FT -- true --> LFT
  FT -- false --> PFT
  PFT --> CR(Create program with tsconfig)
  LFT --> PFT
  PN --> CR
  ATTL -- No --> CRF(Create program with fallback tsconfig)
  CR -->  PROK{Does program include the<br>file we want to analyze?}
  PROK -- No --> ATTL{Are there tsconfigs<br>left to be checked?}
  ATTL -- Yes --> PN(Pick next tsconfig)
  PROK -- Yes --> RT(Return program)
  CRF --> RT


```

2. Decide tsconfigs

```mermaid

```
