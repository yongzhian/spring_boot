#!/bin/sh
#功能简介：启动上层目录下的jar文件
#参数简介：
#    $1:jar文件名（包含后缀名）
#    注意：jar文件必须位于startup.sh目录的上一层目录。


#启动参数
JAVA_OPTS="-server -Xms400m -Xmx400m -Xmn300m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xverify:none -XX:+DisableExplicitGC -Djava.awt.headless=true"

# jar包名称
jar_name=$1

read -r -p "Deploy the [${jar_name}] now ? [y/N] " response
if ! [[ $response =~ ^([y])$ ]]
then
  exit 0
fi

echo "Auto deploy ${jar_name} start ..."

#脚本所在目录,脚本于Jar同目录
this_dir="$( cd "$( dirname "$0"  )" && pwd )"
log_dir="${this_dir}/logs"
log_file="${log_dir}/catalina.out"
jar_file="${this_dir}/${jar_name}"

#参数个数<1或者参数空值时，中断执行
if [ $# -lt 1 ] || [ -z $1 ]; then
    echo -e "请输入要部署的jar包名称!"
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
    echo -e "start success!"
    exit 0
else
    echo -e "${jar_file}文件不存在！"
    exit 1
fi