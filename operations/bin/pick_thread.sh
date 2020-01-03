#!/bin/bash
 parseLog(){
    ##初始值
    upLine="blank"
    last_time_pre="blank"
    last_time_post="blank"
    cat "$1" | while read line
        do
          #此处如果事件格式不一样可以调整，这里的时间格式是 2019-11-05 00:02:00.370
          time_pre=`echo "$line" | awk '{print$1}'`
          time_post=`echo "$line" | awk '{print$2}'`
          if [[ `echo "$time_post" | grep ":"` != "" ]]
            then
            if [[ "$upLine" == "blank" ]]
            ##刚开始给初始时间赋值
            then
              upLine=${line}
              last_time_pre=`echo "$line" | awk '{print$1}'`
              last_time_post=`echo "$line" | awk '{print$2}'`
              total2="$last_time_pre"" $last_time_post"
              last_time_pre=`date +%s -d "$total2"`
              last_time_pre=`expr "$last_time_pre" \* 1000`
              last_time_post=`echo ${last_time_post:9:3}`
            else
              #获得当前毫秒数和上一行的毫秒数，相减，如果时间差大于参数值，则打印到result.log
              total="$time_pre"" $time_post"
              time_pre=`date +%s -d "$total"`
              time_pre=`expr "$time_pre" \* 1000`
              time_post=`echo ${time_post:9:3}`
              time_end=`expr "$time_pre" + "$time_post"`
              time_begin=`expr "$last_time_pre" + "$last_time_post" + "$2"`
              last_time_pre=${time_pre}
              last_time_post=${time_post}
              if [[ "$time_end" -gt "$time_begin" ]]
                then
                    echo ${upLine} >> result.log
                    echo ${line} >> result.log
              fi
              upLine=${line}
            fi
          fi
        done
}

##共需要传入三个参数
#1.日志文件路径
#2.定义的时间差（比如传入15，代表只要同线程的日志上下文超过15ms就会被打印记录下来）
#3.线程关键字，因为并不是每条线程都需要观察，所以输入一个线程关键字过滤掉不想看的系统线程或者其他辅助线程
if [[ ! -n "$1" ]] || [[ ! -n "$2" ]] || [[ ! -n "$3" ]]
  then
    echo [log file] [interval millis] [key words]
    exit
fi

##获取输出最多的前15个线程 根据情况可修改
#这一行共执行三项功能
#1.获得线程名num[$4]++，如果线程名在第五个位置，则改为{num[$5]++}
#2.统计后进行降序排序
#3.过滤出关键线程后，输出到thread.log（只要线程名）
awk '{num[$4]++} 
      END{for(k in num)print k,"----",num[k]|"sort -k 3  -rn"}' "$1" |
      head -n 15 | awk '{str[$1]++} END{for (s in str)print s}'| grep "$3" > thread.log


##对上述输出的每一个线程做分析处理，调用parseLog
cat thread.log | while read line
    do
      grep "\""${line}"\""  "$1" > temp.log
      parseLog temp.log $2
    done
exit

