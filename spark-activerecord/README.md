# ActiveRecord

Based on JFinal ActiveRecord.

```
// create the user who has two attributes and save into database
new User().set("name", "James").set("age", 25).save();

// delete the user who's id is 25
User.dao.deleteById(25);

// rename the user's name to 'James' who's id equals to 25
User.dao.findById(25).set("name", "James").update();

// search the user who's id is 25
User user = User.dao.findById(25);

// get an attribute named 'name' from user
String userName = user.getStr("name");

// get an attribute named 'age' from user
Integer userAge = user.getInt("age");

// search all users older than 18 years old.
List<User> users = User.dao.find("select * from user where age > 18");

// search all girls(gril's sex is 1) older than 18 years old by pagination
Page<User> userPage = User.dao.paginate(1, 10, "select *", "from user where sex=? and age>?", 1, 18);
```

TODO List
- [x] Support Configurations.
- [x] Support Auto Table Bind.
- [x] More customize configurations.