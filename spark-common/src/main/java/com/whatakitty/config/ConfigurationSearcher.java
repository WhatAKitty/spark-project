/**
 * Copyright (c) 2011-2013, kidzhou 周磊 (zhouleib1412@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.whatakitty.config;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.whatakitty.utils.PathUtils;
import com.whatakitty.log.Logger;
import org.joor.Reflect;

import com.google.common.collect.Lists;

public class ConfigurationSearcher {

    protected static final Logger LOG = Logger.getLogger(ConfigurationSearcher.class);

    @SuppressWarnings("unchecked")
    private static List<Class<Configuration>> extraction(List<String> classFileList) {
        List<Class<Configuration>> classList = Lists.newArrayList();
        for (String classFile : classFileList) {
            Class<Configuration> classInFile = Reflect.on(classFile).get();
            if (classInFile.isAnnotationPresent(Configuration.class)) {
                classList.add(classInFile);
            }
        }

        return classList;
    }

    /**
     * 递归查找文件
     * 
     * @param baseDirName
     *            查找的文件夹路径
     * @param targetFileName
     *            需要查找的文件名
     */
    private static List<String> findFiles(String baseDirName, String targetFileName) {
        /**
         * 算法简述： 从某个给定的需查找的文件夹出发，搜索该文件夹的所有子文件夹及文件， 若为文件，则进行匹配，匹配成功则加入结果集，若为子文件夹，则进队列。 队列不空，重复上述操作，队列为空，程序结束，返回结果。
         */
        List<String> classFiles = Lists.newArrayList();
        String tempName = null;
        // 判断目录是否存在
        File baseDir = new File(baseDirName);
        if (!baseDir.exists() || !baseDir.isDirectory()) {
            LOG.error("search error：" + baseDirName + "is not a dir！");
        } else {
            String[] filelist = baseDir.list();
            for (int i = 0; i < filelist.length; i++) {
                File readfile = new File(baseDirName + File.separator + filelist[i]);
                if (readfile.isDirectory()) {
                    classFiles.addAll(findFiles(baseDirName + File.separator + filelist[i], targetFileName));
                } else {
                    tempName = readfile.getName();
                    if (ConfigurationSearcher.wildcardMatch(targetFileName, tempName)) {
                        String classname = null;
                        String tem = readfile.getAbsoluteFile().toString().replaceAll("\\\\", "/");
                        if (tem.indexOf("/classes") > -1) {
                            classname = tem.substring(tem.indexOf("/classes") + "/classes".length() + 1,
                                    tem.indexOf(".class"));
                        } else if (tem.indexOf("/test-classes") > -1) {
                            classname = tem.substring(tem.indexOf("/test-classes") + "/test-classes".length() + 1,
                                    tem.indexOf(".class"));
                        } else {
                            continue;
                        }
                        classFiles.add(classname.replaceAll("/", "."));
                    }
                }
            }
        }
        return classFiles;
    }

    /**
     * 通配符匹配
     * 
     * @param pattern
     *            通配符模式
     * @param str
     *            待匹配的字符串 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     *            匹配成功则返回true，否则返回false
     */
    private static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                // 通配符星号*表示可以匹配任意多个字符
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                // 通配符问号?表示匹配任意一个字符
                strIndex++;
                if (strIndex > strLength) {
                    // 表示str中已经没有字符匹配?了。
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return strIndex == strLength;
    }

    private String classpath = PathUtils.getRootClassPath();

    private boolean includeAllJarsInLib = false;

    private List<String> includeJars = Lists.newArrayList();

    private String libDir = PathUtils.getWebRootPath() + File.separator + "WEB-INF" + File.separator + "lib";

    public ConfigurationSearcher injars(List<String> jars) {
        if (jars != null) {
            includeJars.addAll(jars);
        }
        return this;
    }

    public ConfigurationSearcher inJars(String... jars) {
        if (jars != null) {
            for (String jar : jars) {
                includeJars.add(jar);
            }
        }
        return this;
    }

    public ConfigurationSearcher classpath(String classpath) {
        this.classpath = classpath;
        return this;
    }

	public List<Class<Configuration>> search() {
        List<String> classFileList = findFiles(classpath, "*.class");
        classFileList.addAll(findjarFiles(libDir, includeJars));
        return extraction(classFileList);
    }

    /**
     * 查找jar包中的class
     * 
     * @param baseDirName
     *            jar路径
     * @param includeJars
     * @return jarFileURL
     *            jar文件地址 <a href="http://my.oschina.net/u/556800" target="_blank" rel="nofollow">@return</a>
     */
    private List<String> findjarFiles(String baseDirName, final List<String> includeJars) {
        List<String> classFiles = Lists.newArrayList();
        try {
            // 判断目录是否存在
            File baseDir = new File(baseDirName);
            if (!baseDir.exists() || !baseDir.isDirectory()) {
                LOG.error("file serach error：" + baseDirName + " is not a dir！");
            } else {
                String[] filelist = baseDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return includeAllJarsInLib || includeJars.contains(name);
                    }
                });
                for (int i = 0; i < filelist.length; i++) {
                    JarFile localJarFile = new JarFile(new File(baseDirName + File.separator + filelist[i]));
                    Enumeration<JarEntry> entries = localJarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry jarEntry = entries.nextElement();
                        String entryName = jarEntry.getName();
                        if (!jarEntry.isDirectory() && entryName.endsWith(".class")) {
                            String className = entryName.replaceAll("/", ".").substring(0, entryName.length() - 6);
                            classFiles.add(className);
                        }
                    }
                    localJarFile.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return classFiles;

    }

    public ConfigurationSearcher includeAllJarsInLib(boolean includeAllJarsInLib) {
        this.includeAllJarsInLib = includeAllJarsInLib;
        return this;
    }

    public ConfigurationSearcher libDir(String libDir) {
        this.libDir = libDir;
        return this;
    }

}
