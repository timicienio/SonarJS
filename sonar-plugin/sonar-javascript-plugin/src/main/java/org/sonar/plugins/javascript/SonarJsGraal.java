package org.sonar.plugins.javascript;

import java.nio.file.Path;
import java.util.HashMap;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;

public class SonarJsGraal {

  public static void main(String[] args) throws Exception {
    var options = new HashMap<String, String>();
    // Enable CommonJS experimental support.
    options.put("js.commonjs-require", "true");
    // (optional) folder where the NPM modules to be loaded are located.
    options.put("js.commonjs-require-cwd", ".");
    // (optional) initialization script to pre-define globals.
    //    options.put("js.commonjs-global-properties", "./globals.js");
    // (optional) Node.js built-in replacements as a comma separated list.
    options.put(
      "js.commonjs-core-modules-replacements",
      "module:node-module-polyfill,path:path-browserify,assert:assert-browserify"
    );
    // Create context with IO support and experimental options.
    var context = Context
      .newBuilder("js")
      .allowExperimentalOptions(true)
      .allowIO(true)
      .options(options)
      .build();

    try (context) {
      context.eval(Source.newBuilder("js", Path.of("bin/server").toFile()).build());
      var main = context.getBindings("js").execute();
    }
  }
}
