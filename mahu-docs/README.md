## 构建镜像

```
podman build -t houge/mkdocs .
```

## 构建文档

```
podman run --rm -it -v ./:/docs localhost/houge/mkdocs build
```
