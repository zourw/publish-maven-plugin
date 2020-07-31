package com.mysoft.publish_maven_plugin

class PublishConfig {
    String groupId = ""
    String artifactId = ""
    String version = ""

    String pomName = ""
    String pomDescription = ""
    String pomUrl = ""

    Object repoSnapshot = ""
    Object repoRelease = ""
    String repoName = ""
    String repoPassword = ""

    @Override
    String toString() {
        return "groupId='" + groupId + '\'\n' +
                "artifactId='" + artifactId + '\'\n' +
                "version='" + version + '\'\n' +
                "pomName='" + pomName + '\'\n' +
                "pomDescription='" + pomDescription + '\'\n' +
                "pomUrl='" + pomUrl + '\'\n' +
                "repoSnapshot='" + repoSnapshot + '\'\n' +
                "repoRelease='" + repoRelease + '\'\n' +
                "repoName='" + repoName + '\'\n' +
                "repoPassword='" + repoPassword + '\'\n'
    }
}