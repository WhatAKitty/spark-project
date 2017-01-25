# Spark Web Auth

## Usage

```
// before get '/', user should have permission 'admin:main:view'.
PermissionInfo.permissionBased("admin:main:view").permitted().complete(() -> get("/", (req, res) -> "hello world!"));

// before get '/admin/**', user should have role 'admin'.
PermissionInfo.roleBased("admin").permitted().complete(() -> get("/admin/**", (req, res) -> "hello world!"));
```