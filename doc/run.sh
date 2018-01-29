#!/bin/sh
#功能简介：启动上层目录下的jar文件 ./run.sh start xx.jar
#参数简介：
#    $1:jar文件名（包含后缀名）
#  .
#  ├── springboot9081.jar
#  └── run.sh
# 自动构建如下目录
#  .
#  ├── logs
#  │   ├── catalina.out
#  │   ├── error.out
#  │   └── info.out
#  ├── springboot9081.jar
#  ├── run.sh
#  └── tmp
#    └── tadalin.log

# jar包名称
opt=$1
jar_name=$2

read -r -p "Confirm ${opt} the [${jar_name}] now ? [y/N] " response
if ! [[ $response =~ ^([y])$ ]]
then
  exit 0
fi

#参数个数<2或者参数空值时，中断执行
if [ $# -lt 2 ] || [ -z $2 ]; then
    echo -e "请输入操作和部署的jar包名称!"
    exit 1
fi

start(){
    #启动参数
    JAVA_OPTS="-server -Xms400m -Xmx400m -Xmn300m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m -Xverify:none -XX:+DisableExplicitGC -Djava.awt.headless=true"
    echo "Auto deploy ${jar_name} start ..."
    #脚本所在目录,脚本于Jar同目录
    this_dir="$(pwd )"
    log_dir="${this_dir}/logs"
    log_info_file="${log_dir}/info.out"
    log_error_file="${log_dir}/error.out"
    jar_file="${this_dir}/${jar_name}"

    #日志文件夹不存在，则创建
    if [ ! -d "${log_dir}" ]; then
        mkdir "${log_dir}"
    fi

    #父目录下jar文件存在
    if [ -f "${jar_file}" ]; then
        #启动jar包；重定向标准错误输出到文件可以用>/dev/null
        java $JAVA_OPTS -jar ${jar_file} 1>${log_info_file} 2>${log_error_file} &
        echo -e "start success!"
    else
        echo -e "${jar_file}文件不存在！"
    fi
}

case "$1" in
  start)
    start
    ;;
  stop)
    PROCESS_ID=`ps -ef | grep java | grep ${jar_name} | awk {'print $2'}`
    for id in $PROCESS_ID
    do
      kill -9 $id
      echo "killed $id"
    done
    ;;

  restart)
    $0 stop
    sleep 1
    $0 start
    ;;

  *)
    echo "Usage: run.sh {start|stop|restart} xx.jar"
    ;;

esac

exit 0