# spark-project
a solution about web, orm and authorization based on spark.

TODO List:  
- [x] Spark Parent(Package management)
- [x] Spark Common
- [x] Spark ActiveRecord(Based on [JFinal](https://github.com/jfinal/jfinal) ActiveRecord， Support Using Spring DataSource Management And Transaction Management)
- [x] Spark Web Auth
- [x] Spark Web
- [ ] Spark Spring(Combined with Spring Framework)[Attention: Not Support Spring Boot]

## Why Spark Project
Spark is a pretty beautiful project which is a micro framework for creating web application.  
So, I want to build a project that can support db query, user authorization and so on.

## Features
* Support Auto Configuration(Inspired by Spring Boot AutoConfiguration ).

## Building From source
The Spark Project is built on Maven.

### Prerequisites
[Git][] and [JDK 8 or later][JDK8 build]

Be sure that your `JAVA_HOME` environment variable points to the `jdk1.8.0` folder
extracted from the JDK download.

### Check out sources
`git clone git@github.com:WhatAKitty/spark-project.git`

### Import sources into your IDE
This project is developed on Intellij IDEA. So, If you are using other IDE, you could search google about how to import a intellij project.

## Contributing
[Pull requests][] are welcome; see the [contributor guidelines][] for details.

## License
The Spark Project is released under the [MIT][].

[Git]: http://help.github.com/set-up-git-redirect
[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[Pull requests]: https://help.github.com/categories/collaborating-on-projects-using-issues-and-pull-requests/
[contributor guidelines]: https://github.com/WhatAKitty/spark-project
[MIT]: https://github.com/WhatAKitty/spark-project/blob/master/LICENSE
