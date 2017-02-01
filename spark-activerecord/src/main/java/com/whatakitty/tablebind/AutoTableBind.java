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
package com.whatakitty.tablebind;

import java.util.List;

import javax.sql.DataSource;

import com.google.common.collect.Lists;
import com.whatakitty.ActiveRecord;
import com.whatakitty.IDataSourceProvider;
import com.whatakitty.Model;
import com.whatakitty.log.Logger;
import com.whatakitty.utils.ClassSearcher;
import com.whatakitty.utils.StrKit;

public class AutoTableBind extends ActiveRecord {

    protected final Logger log = Logger.getLogger(getClass());

    @SuppressWarnings({"unchecked"})
    private List<Class<? extends Model>> excludeClasses = Lists.newArrayList();
    private List<String> includeJars = Lists.newArrayList();
    private boolean autoScan = false;
    private boolean includeAllJarsInLib;
    private String configName = "";
    private INameStyle nameStyle;

    public AutoTableBind(DataSource dataSource) {
        this(dataSource, SimpleNameStyles.DEFAULT);
    }

    public AutoTableBind(DataSource dataSource, INameStyle nameStyle) {
        super(dataSource);
        this.nameStyle = nameStyle;
    }
    
    public AutoTableBind(String configName, DataSource dataSource, INameStyle nameStyle) {
        super(configName, dataSource);
        this.nameStyle = nameStyle;
        this.configName = configName;
    }

    public AutoTableBind(IDataSourceProvider dataSourceProvider) {
        this(dataSourceProvider, SimpleNameStyles.DEFAULT);
    }

    public AutoTableBind(IDataSourceProvider dataSourceProvider, INameStyle nameStyle) {
        super(dataSourceProvider);
        this.nameStyle = nameStyle;
    }

    @SuppressWarnings({ "unchecked" })
    public AutoTableBind addExcludeClasses(Class<? extends Model>... clazzes) {
        for (Class<? extends Model> clazz : clazzes) {
            excludeClasses.add(clazz);
        }
        return this;
    }

    @SuppressWarnings({ "unchecked"})
    public AutoTableBind addExcludeClasses(List<Class<? extends Model>> clazzes) {
        if (clazzes != null) {
            excludeClasses.addAll(clazzes);
        }
        return this;
    }

    public AutoTableBind addJars(List<String> jars) {
        if (jars != null) {
            includeJars.addAll(jars);
        }
        return this;
    }

    public AutoTableBind addJars(String... jars) {
        if (jars != null) {
            for (String jar : jars) {
                includeJars.add(jar);
            }
        }
        return this;
    }
    @SuppressWarnings({ "unchecked" })
    @Override
    public boolean start() {
        List<Class<? extends Model>> modelClasses = ClassSearcher.of(Model.class).injars(includeJars).includeAllJarsInLib(includeAllJarsInLib).search();
        TableBind tb = null;
        for (Class modelClass : modelClasses) {
            if (excludeClasses.contains(modelClass)) {
                continue;
            }
            tb = (TableBind) modelClass.getAnnotation(TableBind.class);
            String tableName;
            String configNameTb;
            if (tb == null) {
                if (!autoScan) {
                    continue;
                }
                tableName = nameStyle.name(modelClass.getSimpleName());
                this.addMapping(tableName, modelClass);
                log.debug("addMapping(" + tableName + ", " + modelClass.getName() + ")");
            } else {
                tableName = tb.tableName();
                configNameTb = StrKit.isBlank(tb.configName()) ? "mysql" : tb.configName();
                if(!StrKit.isBlank(this.configName) && !configNameTb.equals(this.configName)) {
                    // 数据源不同，则忽略
                    continue;
                }
                if (StrKit.notBlank(tb.pkName())) {
                    this.addMapping(tableName, tb.pkName(), modelClass);
                    log.debug("addMapping(" + tableName + ", " + tb.pkName() + "," + modelClass.getName() + ")");
                } else {
                    this.addMapping(tableName, modelClass);
                    log.debug("addMapping(" + tableName + ", " + modelClass.getName() + ")");
                }
            }
        }
        return super.start();
    }

    @Override
    public boolean stop() {
        return super.stop();
    }
    
//    @SuppressWarnings("unchecked")
//    private void scanModel(TableBind tb, Class clazz) {
//        if (!excludeClasses.contains(clazz)) {
//            tb = (TableBind) clazz.getAnnotation(TableBind.class);
//            String tableName;
//            if (tb == null) {
//                if (autoScan) {
//                    tableName = nameStyle.name(clazz.getSimpleName());
//                    this.addMapping(tableName, clazz);
//                    log.debug("addMapping(" + tableName + ", " + clazz.getName() + ")");
//                }
//            } else {
//                tableName = tb.tableName();
//                if (StrKit.notBlank(tb.pkName())) {
//                    this.addMapping(tableName, tb.pkName(), clazz);
//                    log.debug("addMapping(" + tableName + ", " + tb.pkName() + "," + clazz.getName() + ")");
//                } else {
//                    this.addMapping(tableName, clazz);
//                    log.debug("addMapping(" + tableName + ", " + clazz.getName() + ")");
//                }
//            }
//        }
//    }
        

    public AutoTableBind autoScan(boolean autoScan) {
        this.autoScan = autoScan;
        return this;
    }

    public AutoTableBind includeAllJarsInLib(boolean includeAllJarsInLib) {
        this.includeAllJarsInLib = includeAllJarsInLib;
        return this;
    }
}
