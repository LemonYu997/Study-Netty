# 单机百万连接调优思路

默认情况下当前案例的可支持连接数不足1万，实际看系统性能

但是想要提高到百万连接，需要解除服务器限制。

首先在Linux服务器查看单个进程支持的最大句柄数（最大文件数）：
```shell
ulimit -n
```
默认是1024，通过以下命令进行修改：
```shell
sudo vi /etc/security/limits.conf

# 在打开的文件末尾加上下面两行
* hard nofile 1000000
* soft nofile 1000000
```

接下来还需要突破全局文件句柄限制：
```shell
# 查看全局文件数限制，默认为10000
cat /proc/sys/fs/file-max

# 修改为1000000
sudo vi /etc/sysctl.conf
fs.file-max=1000000
```