import de.undercouch.gradle.tasks.download.Download

plugins {
    kotlin("multiplatform") version "1.7.10"
    id("de.undercouch.download").version("3.4.3")
    id("maven-publish")
}

val webview2_version: String by project
val windows_version: String by project

group = "de.saschat.natives"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    mingwX64("windows") {
        compilations.getByName("main") {
            cinterops  {
                val webview2 by creating {
                    defFile(project.file("src/nativeInterop/cinterop/webview2.def"))
                    includeDirs(
                        project.file("src/nativeInterop/cinterop/data/build/native/include"),
                        "C:\\Program Files (x86)\\Windows Kits\\10\\Include\\" + windows_version + "\\winrt"
                    )
                    packageName("de.saschat.cinterop.webview2")
                    /*extraOpts(
                        "-libraryPath", "$projectDir/src/nativeInterop/cinterop/webview2/data/build/native/x64",
                        "-libraryPath", "C:\\Program Files (x86)\\Windows Kits\\10\\Lib\\" + System.getenv("WINDOWS_SDK_VERSION") + "\\um\\x64")
                    compilerOpts("-L", "$projectDir/src/nativeInterop/cinterop/webview2/data/build/native/x64")*/

                }
            }
        }
    }
    sourceSets {
        val windowsMain by getting
        val windowsTest by getting
    }
}
tasks.named("cinteropWebview2Windows") {
    dependsOn(":headers")
}
task("headers") {
    task<Download>("download") {
        src("https://www.nuget.org/api/v2/package/Microsoft.Web.WebView2/" + webview2_version)
        dest("src/nativeInterop/cinterop/.nupkg")
    }
    task<Copy>("unzip") {
        from(zipTree("src/nativeInterop/cinterop/.nupkg"))
        into("src/nativeInterop/cinterop/data")
        dependsOn(":download")
    }
    task("definition") {
        doLast {
            File(projectDir, "src/nativeInterop/cinterop/webview2.def").writeText("""
headers = WebView2.h
headerFilter = WebView2.h
compilerOpts = -v
linkerOpts = -v -L""" + File(projectDir, "/src/nativeinterop/cinterop/data/build/native/x64").absolutePath.replace("\\", "\\\\") + """ -lWebView2Loader.dll -lole32
   """)
        }
        dependsOn(":unzip")
    }
    dependsOn(":definition")
}

publishing {
    repositories {
        maven {
            url = uri("https://maven.sascha-t.de")
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("MVN_PUBLISH_SASCHA_USER")
                password = System.getenv("MVN_PUBLISH_SASCHA_TOKEN")
            }
        }
    }
}
