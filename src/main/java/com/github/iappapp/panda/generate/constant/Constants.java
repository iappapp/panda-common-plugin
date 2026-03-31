package com.github.iappapp.panda.generate.constant;

import java.io.File;

/**
 * @author iappapp
 * @date 2026-02-09 10:27
 * @description
 */
public interface Constants {
    String RESOURCE_PATH = String.format("%s" + "src"+ "%s"+ "main"+ "%s"+ "resources"+ "%s",
            File.separator, File.separator, File.separator, File.separator);

    String JAVA_SOURCE_PATH = String.format("%s" + "src"+ "%s"+ "main"+ "%s"+ "java"+ "%s",
            File.separator, File.separator, File.separator, File.separator);;

    String SEPARATOR = File.separator;

    String DOT = ".";

    String JAVA_EXTENSION = ".java";
}
