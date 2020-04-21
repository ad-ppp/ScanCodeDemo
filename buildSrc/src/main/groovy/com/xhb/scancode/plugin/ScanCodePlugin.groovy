package com.xhb.scancode.plugin

import com.android.build.gradle.BaseExtension
import com.google.common.base.Strings
import com.xhb.scancode.plugin.extension.ScannerExtension
import com.xhb.scancode.plugin.util.Log
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class ScanCodePlugin implements Plugin<Project> {
    final String DEFAULT_PLUGIN_NAME = "ScannerPlugin"
    final String TAG = "ScanCodePlugin"

    @Override
    void apply(Project project) {

        project.extensions.create('scannerConfig', ScannerExtension)
        if (!project.plugins.hasPlugin('com.android.application')) {
            throw new GradleException('xhb-scanner-plugin, Android Application plugin required')
        }

        registerTransform(project)

        project.afterEvaluate {
            def android = project.extensions.android
            def configuration = project.scannerConfig


            android.applicationVariants.all { variant ->
                if (Strings.isNullOrEmpty(configuration.pluginName)) {
                    configuration.pluginName = DEFAULT_PLUGIN_NAME
                }

                final String scanCodeConfigPath = configuration.scanCodeConfigPath
                if (!Strings.isNullOrEmpty(scanCodeConfigPath)) {
                    if (!(new File(scanCodeConfigPath).exists())) {
                        throw new IllegalArgumentException(String
                                .format("scanCodeConfigPath %s is not exist", scanCodeConfigPath))
                    }

                    Log.i(TAG, "%s, enable %s", variant.name, configuration.pluginName)
                    Log.i(TAG, "extension: %s", project.scannerConfig)
                }
            }
        }
    }

    private static void registerTransform(Project project) {
        BaseExtension android = project.extensions.getByType(BaseExtension)
        ScannerTransform transform = new ScannerTransform(project)
        android.registerTransform(transform)
    }
}