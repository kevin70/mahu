FROM m.daocloud.io/docker.io/squidfunk/mkdocs-material

RUN pip config --user set global.index https://mirrors.huaweicloud.com/repository/pypi
RUN pip config --user set global.index-url https://mirrors.huaweicloud.com/repository/pypi/simple
RUN pip config --user set global.trusted-host mirrors.huaweicloud.com

RUN pip install mkdocs-minify-plugin \
    mkdocs-mermaid2-plugin \
    mkdocs-table-reader-plugin \
    pymdown-extensions
