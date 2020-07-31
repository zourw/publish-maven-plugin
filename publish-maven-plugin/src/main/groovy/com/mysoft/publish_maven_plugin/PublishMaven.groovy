package com.mysoft.publish_maven_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

import java.util.regex.Pattern

class PublishMaven implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // 获取maven的配置
        def publishConfig = project.extensions.create("publishConfig", PublishConfig)

        // apply plugin 'maven-publish'
        project.plugins.apply(MavenPublishPlugin)

        // 获取 publishing
        def publishing = project.extensions.getByType(PublishingExtension)

        // 在 project afterEvaluate 时，写入maven配置相关信息
        project.afterEvaluate {
            // 预处理 publishConfig
            preprocessPublishConfig(project, publishConfig)

            // 仓库地址 & 账号
            publishing.repositories.maven { MavenArtifactRepository repository ->
                repository.url = getMavenRepositoryUrl(publishConfig)
                repository.credentials { credentials ->
                    credentials.username = publishConfig.repoName
                    credentials.password = publishConfig.repoPassword
                }
            }

            project.components.each { component ->
                publishing.publications.create(component.name, MavenPublication.class, { MavenPublication publication ->
                    publication.groupId = publishConfig.groupId
                    publication.artifactId = publishConfig.artifactId
                    publication.version = publishConfig.version
                    publication.from(component)
                    publication.pom {
                        mavenPom -> configPom(mavenPom, publishConfig)
                    }
                })
            }
        }
    }

    private static boolean isEmpty(CharSequence s) {
        if (s == null) {
            return true
        } else {
            return s.toString().length() == 0
        }
    }

    private static def preprocessPublishConfig(Project project, PublishConfig publishConfig) {
        if (isEmpty(publishConfig.groupId)) {
            publishConfig.groupId = DefaultConfig.GROUP_ID
        }

        if (isEmpty(publishConfig.artifactId)) {
            publishConfig.artifactId = project.name
        }

        if (isEmpty(publishConfig.version)) {
            publishConfig.version = getVersionName(project)
        }

        if (isEmpty(publishConfig.repoSnapshot)) {
            publishConfig.repoSnapshot = DefaultConfig.REPO_SNAPSHOT
        }

        if (isEmpty(publishConfig.repoRelease)) {
            publishConfig.repoRelease = DefaultConfig.REPO_RELEASE
        }

        if (isEmpty(publishConfig.repoName)) {
            publishConfig.repoName = DefaultConfig.REPO_NAME
        }

        if (isEmpty(publishConfig.repoPassword)) {
            publishConfig.repoPassword = DefaultConfig.REPO_PASSWORD
        }

        println "publishConfig: \n" + publishConfig
    }

    private static def getVersionName(Project project) {
        def lines = project.getBuildFile().readLines()
        for (def line : lines) {
            if (line.contains("versionName")) {
                def matcher = Pattern.compile('versionName \"(.*?)\"').matcher(line)
                if (matcher.find()) {
                    def versionName = matcher.group(1)
                    return versionName
                }
            }
        }
    }

    private static def getMavenRepositoryUrl(PublishConfig publishConfig) {
        if (publishConfig.version.toLowerCase().contains("snapshot")) {
            return publishConfig.repoSnapshot
        } else {
            return publishConfig.repoRelease
        }
    }

    private static void configPom(MavenPom mavenPom, PublishConfig config) {
        if (!isEmpty(config.pomName)) {
            mavenPom.name = config.pomName
        }
        if (!isEmpty(config.pomDescription)) {
            mavenPom.description = config.pomDescription
        }
        if (!isEmpty(config.pomUrl)) {
            mavenPom.url = config.pomUrl
        }
    }
}