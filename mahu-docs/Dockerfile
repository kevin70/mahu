FROM docker.1ms.run/squidfunk/mkdocs-material:9.6.11

RUN pip config --user set global.index https://mirrors.huaweicloud.com/repository/pypi
RUN pip config --user set global.index-url https://mirrors.huaweicloud.com/repository/pypi/simple
RUN pip config --user set global.trusted-host mirrors.huaweicloud.com

RUN pip install jieba
