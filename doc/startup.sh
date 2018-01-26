#!/bin/sh
#功能简介：启动上层目录下的jar文件
#参数简介：
#    $1:jar文件名（包含后缀名）
#    注意：jar文件必须位于startup.sh目录的上一层目录。

#启动参数
JAVA_OPTS="-server -Xms400m -Xmx400m -Xmn300m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xverify:none -XX:+DisableExplicitGC -Djava.awt.headless=true"

# jar包名称
jar_name=$1
#脚本所在目录
this_dir="$( cd "$( dirname "$0"  )" && pwd )"
parent_dir=`dirname "${this_dir}"`
log_dir="${parent_dir}/logs"
log_file="${log_dir}/catalina.out"
jar_file="${parent_dir}/userapps/${jar_name}"

#参数个数<1或者参数空值时，中断执行
if [ $# -lt 1 ] || [ -z $1 ]; then
    echo -e "\033[31m请输入要部署的jar包名称!\033[0m"
    exit 1
fi

#日志文件夹不存在，则创建
if [ ! -d "${log_dir}" ]; then
    mkdir "${log_dir}"
fi

#父目录下jar文件存在
if [ -f "${jar_file}" ]; then
    #启动jar包；重定向标准错误输出到文件，丢掉标准输出
    java $JAVA_OPTS -jar ${jar_file} 1>/dev/null 2>"${log_file}" &
    exit 0
else
    echo -e "\033[31m${jar_file}文件不存在！\033[0m"
    exit 1
fi